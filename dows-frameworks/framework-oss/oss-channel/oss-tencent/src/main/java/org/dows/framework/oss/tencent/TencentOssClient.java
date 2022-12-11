package org.dows.framework.oss.tencent;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import org.dows.framework.api.exceptions.OssException;
import org.dows.framework.oss.api.*;
import org.dows.framework.oss.api.constant.OssConstant;
import org.dows.framework.oss.api.download.DownloadCheckPoint;
import org.dows.framework.oss.api.download.DownloadObjectStat;
import org.dows.framework.oss.tencent.model.TencentOssConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.oss.api.upload.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * https://cloud.tencent.com/document/product/436
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TencentOssClient implements S3OssClient {

    public static final String COS_OBJECT_NAME = "cosClient";

    private COSClient cosClient;
    private TencentOssConfig tencentOssConfig;

    @Override
    public OssInfo upLoad(File file, String targetName, Boolean isOverride) {
        String bucketName = getBucket();
        String key = getKey(targetName, false);
        if (isOverride || !cosClient.doesObjectExist(bucketName, key)) {
            if (file.exists()) {
                PutObjectResult putObjectResult = cosClient.putObject(bucketName, key, file);
                OssInfo ossInfo = getInfo(targetName);
                ossInfo.setMd5(putObjectResult.getContentMd5());
                return ossInfo;
            } else {
                throw new OssException("文件不存在");
            }
        }
        return getInfo(targetName);
    }


    @Override
    public OssInfo upLoad(InputStream is, String targetName, Boolean isOverride) {
        String bucketName = getBucket();
        String key = getKey(targetName, false);

        if (isOverride || !cosClient.doesObjectExist(bucketName, key)) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            try {
                objectMetadata.setContentLength(is.available());
            } catch (IOException e) {
                e.printStackTrace();
                throw new OssException("文件流异常");
            }
            PutObjectResult putObjectResult = cosClient.putObject(bucketName, key, is, objectMetadata);
            OssInfo ossInfo = getInfo(targetName);
            ossInfo.setMd5(putObjectResult.getContentMd5());
            return ossInfo;
        }
        return getInfo(targetName);
    }

    @Override
    public OssInfo upLoadCheckPoint(File file, String targetName) {
        uploadFile(file, targetName, tencentOssConfig.getSliceConfig(), OssConstant.OssType.TENCENT);
        return getInfo(targetName);
    }

    @Override
    public void completeUpload(UpLoadCheckPoint upLoadCheckPoint, List<UpLoadPartEntityTag> partEntityTags) {
        List<PartETag> eTags = partEntityTags.stream().sorted(Comparator.comparingInt(UpLoadPartEntityTag::getPartNumber))
                .map(partEntityTag -> new PartETag(partEntityTag.getPartNumber(), partEntityTag.getETag())).collect(Collectors.toList());

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(upLoadCheckPoint.getBucket(), upLoadCheckPoint.getKey(), upLoadCheckPoint.getUploadId(), eTags);
        cosClient.completeMultipartUpload(completeMultipartUploadRequest);

        FileUtil.del(upLoadCheckPoint.getCheckpointFile());
    }

    @Override
    public void prepareUpload(UpLoadCheckPoint uploadCheckPoint, File upLoadFile, String targetName, String checkpointFile, SliceConfig slice) {
        String bucket = getBucket();
        String key = getKey(targetName, false);

        uploadCheckPoint.setMagic(UpLoadCheckPoint.UPLOAD_MAGIC);
        uploadCheckPoint.setUploadFile(upLoadFile.getPath());
        uploadCheckPoint.setKey(key);
        uploadCheckPoint.setBucket(bucket);
        uploadCheckPoint.setCheckpointFile(checkpointFile);
        uploadCheckPoint.setUploadFileStat(UpLoadFileStat.getFileStat(uploadCheckPoint.getUploadFile()));

        long partSize = slice.getPartSize();
        long fileLength = upLoadFile.length();
        int parts = (int) (fileLength / partSize);
        if (fileLength % partSize > 0) {
            parts++;
        }

        uploadCheckPoint.setUploadParts(splitUploadFile(uploadCheckPoint.getUploadFileStat().getSize(), partSize));
        uploadCheckPoint.setPartEntityTags(new ArrayList<>());
        uploadCheckPoint.setOriginPartSize(parts);

        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucket, key);
        log.info("uploadrequest:{}", request);
        InitiateMultipartUploadResult result = cosClient.initiateMultipartUpload(request);

        uploadCheckPoint.setUploadId(result.getUploadId());
    }

    @Override
    public UpLoadPartResult uploadPart(UpLoadCheckPoint upLoadCheckPoint, int partNum, InputStream inputStream) {
        UpLoadPartResult partResult = null;
        UploadPart uploadPart = upLoadCheckPoint.getUploadParts().get(partNum);
        long partSize = uploadPart.getSize();
        partResult = new UpLoadPartResult(partNum + 1, uploadPart.getOffset(), partSize);
        try {
            inputStream.skip(uploadPart.getOffset());

            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(upLoadCheckPoint.getBucket());
            uploadPartRequest.setKey(upLoadCheckPoint.getKey());
            uploadPartRequest.setUploadId(upLoadCheckPoint.getUploadId());
            uploadPartRequest.setInputStream(inputStream);
            uploadPartRequest.setPartSize(partSize);
            uploadPartRequest.setPartNumber(uploadPart.getNumber());

            UploadPartResult uploadPartResponse = cosClient.uploadPart(uploadPartRequest);

            partResult.setNumber(uploadPartResponse.getPartNumber());
            partResult.setEntityTag(new UpLoadPartEntityTag().setETag(uploadPartResponse.getETag())
                    .setPartNumber(uploadPartResponse.getPartNumber()));
        } catch (Exception e) {
            partResult.setFailed(true);
            partResult.setException(e);
        } finally {
            IoUtil.close(inputStream);
        }

        return partResult;
    }

    @Override
    public void downLoad(OutputStream os, String targetName) {
        COSObject cosObject = cosClient.getObject(getBucket(), getKey(targetName, false));
        IoUtil.copy(cosObject.getObjectContent(), os);
    }

    @Override
    public void downLoadCheckPoint(File localFile, String targetName) {
        downLoadFile(localFile, targetName, tencentOssConfig.getSliceConfig(), OssConstant.OssType.TENCENT);
    }

    @Override
    public DownloadObjectStat getDownloadObjectStat(String targetName) {
        ObjectMetadata objectMetadata = cosClient.getObjectMetadata(getBucket(), getKey(targetName, false));
        DateTime date = DateUtil.date(objectMetadata.getLastModified());
        long contentLength = objectMetadata.getContentLength();
        String eTag = objectMetadata.getETag();
        return new DownloadObjectStat().setSize(contentLength).setLastModified(date).setDigest(eTag);
    }

    @Override
    public void prepareDownload(DownloadCheckPoint downloadCheckPoint, File localFile, String targetName, String checkpointFile) {
        downloadCheckPoint.setMagic(DownloadCheckPoint.DOWNLOAD_MAGIC);
        downloadCheckPoint.setDownloadFile(localFile.getPath());
        downloadCheckPoint.setBucketName(getBucket());
        downloadCheckPoint.setKey(getKey(targetName, false));
        downloadCheckPoint.setCheckPointFile(checkpointFile);

        downloadCheckPoint.setObjectStat(getDownloadObjectStat(targetName));

        long downloadSize;
        if (downloadCheckPoint.getObjectStat().getSize() > 0) {
            Long partSize = tencentOssConfig.getSliceConfig().getPartSize();
            long[] slice = getDownloadSlice(new long[0], downloadCheckPoint.getObjectStat().getSize());
            downloadCheckPoint.setDownloadParts(splitDownloadFile(slice[0], slice[1], partSize));
            downloadSize = slice[1];
        } else {
            //download whole file
            downloadSize = 0;
            downloadCheckPoint.setDownloadParts(splitDownloadOneFile());
        }
        downloadCheckPoint.setOriginPartSize(downloadCheckPoint.getDownloadParts().size());
        createDownloadTemp(downloadCheckPoint.getTempDownloadFile(), downloadSize);
    }

    @Override
    public InputStream downloadPart(String key, long start, long end) {
        GetObjectRequest request = new GetObjectRequest(getBucket(), key);
        request.setRange(start, end);
        COSObject object = cosClient.getObject(request);
        return object.getObjectContent();
    }

    @Override
    public void delete(String targetName) {
        cosClient.deleteObject(getBucket(), getKey(targetName, false));
    }

    @Override
    public void copy(String sourceName, String targetName, Boolean isOverride) {
        String bucketName = getBucket();
        String targetKey = getKey(targetName, false);
        if (isOverride || !cosClient.doesObjectExist(bucketName, targetKey)) {
            cosClient.copyObject(getBucket(), getKey(sourceName, false), getBucket(), targetKey);
        }
    }

    @Override
    public OssInfo getInfo(String targetName, Boolean isRecursion) {
        String key = getKey(targetName, false);
        OssInfo ossInfo = getBaseInfo(key);
        ossInfo.setName(StrUtil.equals(targetName, StrUtil.SLASH) ? targetName : FileNameUtil.getName(targetName));
        ossInfo.setPath(replaceKey(targetName, ossInfo.getName(), true));
        ossInfo.setFileLink(cosClient.getObjectUrl(getBucket(), key).toString());

        if (isRecursion && isDirectory(key)) {
            String prefix = convertPath(key, false);
            ObjectListing listObjects = cosClient.listObjects(getBucket(), prefix.endsWith("/") ? prefix : prefix + CharPool.SLASH);

            List<OssInfo> fileOssInfos = new ArrayList<>();
            List<OssInfo> directoryInfos = new ArrayList<>();
            for (COSObjectSummary cosObjectSummary : listObjects.getObjectSummaries()) {
                if (FileNameUtil.getName(cosObjectSummary.getKey()).equals(FileNameUtil.getName(key))) {
                    ossInfo.setLastUpdateTime(DateUtil.date(cosObjectSummary.getLastModified()).toString(DatePattern.NORM_DATETIME_PATTERN));
                    ossInfo.setCreateTime(DateUtil.date(cosObjectSummary.getLastModified()).toString(DatePattern.NORM_DATETIME_PATTERN));
                    ossInfo.setSize(Convert.toStr(cosObjectSummary.getSize()));
                } else {
                    fileOssInfos.add(getInfo(replaceKey(cosObjectSummary.getKey(), getBasePath(), false), false));
                }
            }

            for (String commonPrefix : listObjects.getCommonPrefixes()) {
                String target = replaceKey(commonPrefix, getBasePath(), false);
                if (isDirectory(commonPrefix)) {
                    directoryInfos.add(getInfo(target, true));
                } else {
                    fileOssInfos.add(getInfo(target, false));
                }
            }
            if (ObjectUtil.isNotEmpty(fileOssInfos) && fileOssInfos.get(0) instanceof FileOssInfo) {
                ReflectUtil.setFieldValue(ossInfo, "fileInfos", fileOssInfos);
            }
            if (ObjectUtil.isNotEmpty(directoryInfos) && directoryInfos.get(0) instanceof DirectoryOssInfo) {
                ReflectUtil.setFieldValue(ossInfo, "directoryInfos", directoryInfos);
            }
        }

        return ossInfo;
    }

    @Override
    public Boolean isExist(String targetName) {
        return cosClient.doesObjectExist(getBucket(), getKey(targetName, false));
    }

    @Override
    public String getBasePath() {
        return tencentOssConfig.getBasePath();
    }

    @Override
    public Map<String, Object> getClientObject() {
        return new HashMap<String, Object>() {
            {
                put(COS_OBJECT_NAME, getCosClient());
            }
        };
    }

    private String getBucket() {
        return tencentOssConfig.getBucketName();
    }

    public OssInfo getBaseInfo(String key) {
        OssInfo ossInfo;

        if (isFile(key)) {
            ossInfo = new FileOssInfo();
            try {
                ObjectMetadata objectMetadata = cosClient.getObjectMetadata(getBucket(), replaceKey(key, "", false));
                ossInfo.setLastUpdateTime(DateUtil.date(objectMetadata.getLastModified()).toString(DatePattern.NORM_DATETIME_PATTERN));
                ossInfo.setCreateTime(DateUtil.date(objectMetadata.getLastModified()).toString(DatePattern.NORM_DATETIME_PATTERN));
                ossInfo.setSize(Convert.toStr(objectMetadata.getContentLength()));
            } catch (Exception e) {
                log.error("获取{}文件属性失败", key, e);
            }
        } else {
            ossInfo = new DirectoryOssInfo();
        }
        return ossInfo;
    }

}
