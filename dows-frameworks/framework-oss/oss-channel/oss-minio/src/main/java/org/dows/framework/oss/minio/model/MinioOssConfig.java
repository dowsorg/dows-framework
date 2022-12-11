package org.dows.framework.oss.minio.model;

import org.dows.framework.oss.api.SliceConfig;
import org.dows.framework.oss.api.utils.OssPathUtil;
import lombok.Data;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/26/2022
 */
@Data
public class MinioOssConfig {

    private String basePath;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    /**
     * 断点续传参数
     */
    private SliceConfig sliceConfig = new SliceConfig();
    private MinioOssClientConfig clientConfig = new MinioOssClientConfig();
    public void init() {
        this.sliceConfig.init();
        basePath = OssPathUtil.valid(basePath);
    }
}
