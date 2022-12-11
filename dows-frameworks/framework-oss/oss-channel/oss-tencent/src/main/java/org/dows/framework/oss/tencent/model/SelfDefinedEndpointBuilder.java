package org.dows.framework.oss.tencent.model;

import com.qcloud.cos.endpoint.EndpointBuilder;
import com.qcloud.cos.region.Region;

/**
 * 自定义endpoint
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/23/2022
 */
public class SelfDefinedEndpointBuilder implements EndpointBuilder {
    private String region;
    private String domain;

    public SelfDefinedEndpointBuilder(String region, String domain) {
        super();
        // 格式化 Region
        this.region = Region.formatRegion(new Region(region));
        this.domain = domain;
    }

    public SelfDefinedEndpointBuilder(TencentOssConfig tencentOssConfig) {
        // 格式化 Region
        this.region = Region.formatRegion(new Region(tencentOssConfig.getRegion()));
        this.domain = tencentOssConfig.getEndpoint();
    }

    @Override
    public String buildGeneralApiEndpoint(String bucketName) {
        // 构造 Endpoint
        String endpoint = String.format("%s.%s", this.region, this.domain.replaceAll("/",""));
        // 构造 Bucket 访问域名
        return String.format("%s.%s", bucketName, endpoint);
    }

    @Override
    public String buildGetServiceApiEndpoint() {
        return String.format("%s.%s", this.region,  this.domain.replaceAll("/",""));
    }
}
