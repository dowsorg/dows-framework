package org.dows.framework.oss.api;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 6/3/2022
 */
public interface OssSetting<T> {

    /**
     * 获取路径
     * @return
     */
    void setBasePath(String basePath);
    String getBasePath();

    /**
     * bucket名称
     * @return
     */
    void setBucketName(String bucketName);
    String getBucketName();

    /**
     *
     * @return
     */
    void setSecretId(String secretId);
    String getSecretId();

    void setSecretKey(String secretKey);
    String getSecretKey();

    /**
     * 区域
     * @return
     */
    void setRegion(String region);
    String getRegion();

    /**
     * 暴露端
     * @return
     */
    void setEndpoint(String endpoint);
    String getEndpoint();

    /**
     * 断点续传参数
     */
    void setSliceConfig(SliceConfig sliceConfig);
    SliceConfig getSliceConfig();
}
