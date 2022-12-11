package org.dows.framework.oss.tencent;

import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import org.dows.framework.oss.api.S3OssClient;
import org.dows.framework.oss.api.constant.OssConstant;
import org.dows.framework.oss.tencent.model.SelfDefinedEndpointBuilder;
import org.dows.framework.oss.tencent.model.TencentOssConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
@ConditionalOnClass(COSClient.class)
@EnableConfigurationProperties({TencentOssProperties.class})
@ConditionalOnProperty(prefix = OssConstant.OSS, name = OssConstant.OssType.TENCENT + CharPool.DOT + OssConstant.ENABLE,
        havingValue = OssConstant.DEFAULT_ENABLE_VALUE)
public class TencentOssConfiguration {

    public static final String DEFAULT_BEAN_NAME = "tencentOssClient";

    @Autowired
    private TencentOssProperties tencentOssProperties;

    @Bean
    public void tencentOssClient11() {
        Map<String, TencentOssConfig> tencentOssConfigMap = tencentOssProperties.getOssConfig();
        if (tencentOssConfigMap.isEmpty()) {
            SpringUtil.registerBean(DEFAULT_BEAN_NAME, build(tencentOssProperties));
        } else {
            String region = tencentOssProperties.getRegion();
            String secretId = tencentOssProperties.getSecretId();
            String secretKey = tencentOssProperties.getSecretKey();
            tencentOssConfigMap.forEach((name, tencentOssConfig) -> {
                if (ObjectUtil.isEmpty(tencentOssConfig.getRegion())) {
                    tencentOssConfig.setRegion(region);
                }
                if (ObjectUtil.isEmpty(tencentOssConfig.getSecretId())) {
                    tencentOssConfig.setSecretId(secretId);
                }
                if (ObjectUtil.isEmpty(tencentOssConfig.getSecretKey())) {
                    tencentOssConfig.setSecretKey(secretKey);
                }

                SpringUtil.registerBean(name, build(tencentOssConfig));
            });
        }
    }

    private S3OssClient build(TencentOssConfig tencentOssConfig) {
        Region region = region(tencentOssConfig);
        ClientConfig clientConfig = config(region);
        clientConfig.setEndpointBuilder(new SelfDefinedEndpointBuilder(tencentOssConfig));
        clientConfig.setHttpProtocol(HttpProtocol.http);
        COSCredentials cosCredentials = cosCredentials(tencentOssConfig);
        COSClient cosClient = cosClient(cosCredentials, clientConfig);
        return tencentOssClient(cosClient, tencentOssConfig);
    }

    public S3OssClient tencentOssClient(COSClient cosClient, TencentOssConfig tencentOssConfig) {
        return new TencentOssClient(cosClient, tencentOssConfig);
    }

    public COSCredentials cosCredentials(TencentOssConfig tencentOssConfig) {
        return new BasicCOSCredentials(tencentOssConfig.getSecretId(), tencentOssConfig.getSecretKey());
    }

    public Region region(TencentOssConfig tencentOssConfig) {
        return new Region(tencentOssConfig.getRegion());
    }

    public ClientConfig config(Region region) {
        return new ClientConfig(region);
    }

    public COSClient cosClient(COSCredentials cred, ClientConfig clientConfig) {
        return new COSClient(cred, clientConfig);
    }

}
