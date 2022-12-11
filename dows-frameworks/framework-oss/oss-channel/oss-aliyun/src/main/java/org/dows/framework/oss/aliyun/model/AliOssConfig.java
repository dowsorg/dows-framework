package org.dows.framework.oss.aliyun.model;

import com.aliyun.oss.ClientBuilderConfiguration;
import org.dows.framework.oss.api.SliceConfig;
import org.dows.framework.oss.api.utils.OssPathUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * https://help.aliyun.com/product/31815.html
 *
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/26/2022
 */
@Data
@EqualsAndHashCode
public class AliOssConfig {
    /**
     * 数据存储路径
     */
    private String basePath;
    /**
     * Bucket名称
     */
    private String bucketName;
    /**
     * OSS地址
     */
    private String endpoint;
    /**
     * AccessKey ID
     */
    private String accessKeyId;
    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    private String securityToken;

    private ClientBuilderConfiguration clientConfig;

    /**
     * 断点续传参数
     */
    private SliceConfig sliceConfig = new SliceConfig();

    public void init() {
        this.sliceConfig.init();
        basePath = OssPathUtil.valid(basePath);
    }

}
