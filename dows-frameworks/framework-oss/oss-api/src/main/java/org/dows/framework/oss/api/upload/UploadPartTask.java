package org.dows.framework.oss.api.upload;

import cn.hutool.core.io.FileUtil;
import org.dows.framework.oss.api.S3OssClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * @author lait.zhang@gmail.com
 * @description: 分片上传Task
 * @weixin SH330786
 * @date 3/16/2022
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadPartTask implements Callable<UpLoadPartResult> {
    /**
     * OSS客户端
     */
    private S3OssClient ossClient;
    /**
     * 断点续传对象
     */
    private UpLoadCheckPoint upLoadCheckPoint;
    /**
     * 分片索引
     */
    private int partNum;

    @Override
    public UpLoadPartResult call() {
        InputStream inputStream = FileUtil.getInputStream(upLoadCheckPoint.getUploadFile());
        UpLoadPartResult upLoadPartResult = ossClient.uploadPart(upLoadCheckPoint, partNum, inputStream);
        if (!upLoadPartResult.isFailed()) {
            upLoadCheckPoint.update(partNum, upLoadPartResult.getEntityTag(), true);
            upLoadCheckPoint.dump();
        }
        return upLoadPartResult;
    }
}
