package org.dows.framework.oss.api.download;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import org.dows.framework.oss.api.S3OssClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;

/**
 * @author lait.zhang@gmail.com
 * @description: 分片下载Task
 * @weixin SH330786
 * @date 3/16/2022
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadPartTask implements Callable<DownloadPartResult> {

    /**
     * Oss客户端
     */
    private S3OssClient ossClient;
    /**
     * 断点续传对象
     */
    private DownloadCheckPoint downloadCheckPoint;
    /**
     * 分片索引
     */
    private int partNum;

    @Override
    public DownloadPartResult call() {
        DownloadPartResult partResult = null;
        RandomAccessFile output = null;
        InputStream content = null;
        try {
            DownloadPart downloadPart = downloadCheckPoint.getDownloadParts().get(partNum);

            partResult = new DownloadPartResult(partNum + 1, downloadPart.getStart(), downloadPart.getEnd());

            output = new RandomAccessFile(downloadCheckPoint.getTempDownloadFile(), "rw");
            output.seek(downloadPart.getFileStart());

            content = ossClient.downloadPart(downloadCheckPoint.getKey(), downloadPart.getStart(), downloadPart.getEnd());

            long partSize = downloadPart.getEnd() - downloadPart.getStart();
            byte[] buffer = new byte[Convert.toInt(partSize)];
            int bytesRead = 0;
            while ((bytesRead = content.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            partResult.setLength(downloadPart.getLength());
            downloadCheckPoint.update(partNum, true);
            downloadCheckPoint.dump();
        } catch (Exception e) {
            partResult.setException(e);
            partResult.setFailed(true);
        } finally {
            IoUtil.close(output);
            IoUtil.close(content);
        }
        return partResult;
    }
}
