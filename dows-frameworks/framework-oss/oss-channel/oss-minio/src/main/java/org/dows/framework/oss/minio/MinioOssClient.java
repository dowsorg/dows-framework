package org.dows.framework.oss.minio;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.io.ByteStreams;
import org.dows.framework.api.exceptions.OssException;
import org.dows.framework.oss.api.DirectoryOssInfo;
import org.dows.framework.oss.api.FileOssInfo;
import org.dows.framework.oss.api.OssInfo;
import org.dows.framework.oss.api.S3OssClient;
import org.dows.framework.oss.api.constant.OssConstant;
import org.dows.framework.oss.api.download.DownloadCheckPoint;
import org.dows.framework.oss.api.download.DownloadObjectStat;
import org.dows.framework.oss.minio.model.MinioOssConfig;
import io.minio.*;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.apache.http.HttpHeaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * http://docs.minio.org.cn/docs/master/minio-monitoring-guide
 * https://docs.min.io/
 *
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/26/2022
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinioOssClient implements S3OssClient {

    public static final String MINIO_OBJECT_NAME = "minioClient";

    private MinioClient minioClient;
    private MinioOssConfig minioOssConfig;

    @Override
    public OssInfo upLoad(File file, String targetName, Boolean isOverride) {
        String bucketName = getBucket();
        String key = getKey(targetName, false);
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                .bucket(bucketName)
                .build();
        try {
            if (isOverride || !minioClient.bucketExists(bucketExistsArgs)) {
                InputStream is = new FileInputStream(file);
                if (file.exists()) {
                    // ??????????????????s3??????
                    ObjectWriteResponse putObjectResult = minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .stream(is, is.available(), -1)
                            .build());
                    OssInfo ossInfo = getInfo(targetName);
                    ossInfo.setMd5(putObjectResult.etag());
                    return ossInfo;
                } else {
                    throw new OssException("???????????????");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getInfo(targetName);
    }

    @Override
    public OssInfo upLoad(InputStream is, String targetName, Boolean isOverride) {
        try {
            String bucket = getBucket();
            String key = getKey(targetName, true);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(is, is.available(), -1)
                    .build());
        } catch (Exception e) {
            throw new OssException(e);
        }
        return getInfo(targetName);
    }

    @Override
    public OssInfo upLoadCheckPoint(File file, String targetName) {
        try (InputStream inputStream = FileUtil.getInputStream(file)) {
            upLoad(inputStream, targetName, true);
        } catch (Exception e) {
            throw new OssException(e);
        }
        return getInfo(targetName);
    }

    @Override
    public void downLoad(OutputStream os, String targetName) {
        GetObjectResponse is = null;
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(getBucket())
                    .object(getKey(targetName, true))
                    .build();
            is = minioClient.getObject(getObjectArgs);
            ByteStreams.copy(is, os);
        } catch (Exception e) {
            throw new OssException(e);
        } finally {
            IoUtil.close(is);
        }
    }

    @Override
    public void downLoadCheckPoint(File localFile, String targetName) {
        downLoadFile(localFile, targetName, minioOssConfig.getSliceConfig(), OssConstant.OssType.MINIO);
    }

    @Override
    public DownloadObjectStat getDownloadObjectStat(String targetName) {
        try {
            StatObjectArgs statObjectArgs = StatObjectArgs.builder().bucket(getBucket()).object(getKey(targetName, true)).build();
            StatObjectResponse statObjectResponse = minioClient.statObject(statObjectArgs);
            long contentLength = statObjectResponse.size();
            String eTag = statObjectResponse.etag();
            return new DownloadObjectStat().setSize(contentLength)
                    .setLastModified(Date.from(statObjectResponse.lastModified().toInstant())).setDigest(eTag);
        } catch (Exception e) {
            throw new OssException(e);
        }
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
            Long partSize = minioOssConfig.getSliceConfig().getPartSize();
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
    public InputStream downloadPart(String key, long start, long end) throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(getBucket())
                .object(key)
                .offset(start) // ?????????????????????
                .length(end)  // ?????????????????? (????????????????????????????????????????????????)???
                .build();
        return minioClient.getObject(getObjectArgs);
    }

    @Override
    public void delete(String targetName) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(getBucket())
                    .object(getKey(targetName, true))
                    .build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            throw new OssException(e);
        }
    }

    @Override
    public void copy(String sourceName, String targetName, Boolean isOverride) {
        try {
            CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder()
                    .bucket(getBucket())
                    .object(getKey(targetName, true))
                    .source(CopySource.builder()
                            .bucket(getBucket())
                            .object(getKey(sourceName, true))
                            .build())
                    .build();
            minioClient.copyObject(copyObjectArgs);
        } catch (Exception e) {
            throw new OssException(e);
        }
    }

    @Override
    public OssInfo getInfo(String targetName, Boolean isRecursion) {
        try {
            String key = getKey(targetName, false);

            OssInfo ossInfo = getBaseInfo(targetName);
            if (isRecursion && isDirectory(key)) {

                String prefix = convertPath(key, true);
                ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                        .bucket(getBucket())
                        .delimiter("/")
                        .prefix(prefix.endsWith("/") ? prefix : prefix + CharPool.SLASH)
                        .build();
                Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);

                List<OssInfo> fileOssInfos = new ArrayList<>();
                List<OssInfo> directoryInfos = new ArrayList<>();

                for (Result<Item> result : results) {
                    Item item = result.get();
                    String childKey = replaceKey(item.objectName(), getBasePath(), true);
                    if (item.isDir()) {
                        directoryInfos.add(getInfo(childKey, true));
                    } else {
                        fileOssInfos.add(getInfo(childKey, false));
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
        } catch (Exception e) {
            throw new OssException(e);
        }
    }

    @Override
    public String getBasePath() {
        return minioOssConfig.getBasePath();
    }

    @Override
    public Map<String, Object> getClientObject() {
        return new HashMap<String, Object>() {
            {
                put(MINIO_OBJECT_NAME, getMinioClient());
            }
        };
    }

    private String getBucket() {
        return minioOssConfig.getBucketName();
    }

    public OssInfo getBaseInfo(String targetName) {
        String key = getKey(targetName, true);
        OssInfo ossInfo;
        String bucketName = getBucket();
        if (isFile(key)) {
            ossInfo = new FileOssInfo();
            try {
                GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucketName).object(key).build();
                GetObjectResponse objectResponse = minioClient.getObject(getObjectArgs);
                Headers headers = objectResponse.headers();

                ossInfo.setCreateTime(DateUtil.date(headers.getDate(HttpHeaders.DATE)).toString(DatePattern.NORM_DATETIME_PATTERN));
                ossInfo.setLastUpdateTime(DateUtil.date(headers.getDate(HttpHeaders.LAST_MODIFIED)).toString(DatePattern.NORM_DATETIME_PATTERN));
                ossInfo.setSize(Convert.toStr(headers.get(HttpHeaders.CONTENT_LENGTH)));
            } catch (Exception e) {
                log.error("??????{}??????????????????", key, e);
            }
        } else {
            ossInfo = new DirectoryOssInfo();
        }
        ossInfo.setName(StrUtil.equals(targetName, StrUtil.SLASH) ? targetName : FileNameUtil.getName(targetName));
        ossInfo.setPath(replaceKey(targetName, ossInfo.getName(), true));
        return ossInfo;
    }

}
