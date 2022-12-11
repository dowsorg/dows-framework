package org.dows.framework.oss.minio.model;

import org.dows.framework.oss.api.constant.OssConstant;
import lombok.Data;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 5/8/2022
 */
@Data
public class MinioOssClientConfig {
    private Long connectTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
    private Long writeTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
    private Long readTimeout = OssConstant.DEFAULT_CONNECTION_TIMEOUT;
}
