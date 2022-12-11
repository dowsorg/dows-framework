package org.dows.framework.oss.aliyun;

import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import org.dows.framework.oss.aliyun.model.AliOssConfig;
import org.dows.framework.oss.api.S3OssClient;
import org.dows.framework.oss.api.constant.OssConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/26/2022
 */
@Configuration
@ConditionalOnClass(OSSClient.class)
@EnableConfigurationProperties({AliOssProperties.class})
@ConditionalOnProperty(prefix = OssConstant.OSS, name = OssConstant.OssType.ALI + CharPool.DOT + OssConstant.ENABLE,
        havingValue = OssConstant.DEFAULT_ENABLE_VALUE)
public class AliOssConfiguration {

    public static final String DEFAULT_BEAN_NAME = "aliOssClient";

    @Autowired
    private AliOssProperties aliOssProperties;

    @Bean
    public S3OssClient aliOssClient() {
        Map<String, AliOssConfig> aliOssConfigMap = aliOssProperties.getOssConfig();
        if (aliOssConfigMap.isEmpty()) {
            SpringUtil.registerBean(DEFAULT_BEAN_NAME, aliOssClient(aliOssProperties));
        } else {
            String endpoint = aliOssProperties.getEndpoint();
            String accessKeyId = aliOssProperties.getAccessKeyId();
            String accessKeySecret = aliOssProperties.getAccessKeySecret();
            ClientBuilderConfiguration clientConfig = aliOssProperties.getClientConfig();
            aliOssConfigMap.forEach((name, aliOssConfig) -> {
                if (ObjectUtil.isEmpty(aliOssConfig.getEndpoint())) {
                    aliOssConfig.setEndpoint(endpoint);
                }
                if (ObjectUtil.isEmpty(aliOssConfig.getAccessKeyId())) {
                    aliOssConfig.setAccessKeyId(accessKeyId);
                }
                if (ObjectUtil.isEmpty(aliOssConfig.getAccessKeySecret())) {
                    aliOssConfig.setAccessKeySecret(accessKeySecret);
                }
                if (ObjectUtil.isEmpty(aliOssConfig.getClientConfig())) {
                    aliOssConfig.setClientConfig(clientConfig);
                }
                SpringUtil.registerBean(name, aliOssClient(aliOssConfig));
            });
        }
        return null;
    }

    public S3OssClient aliOssClient(AliOssConfig aliOssConfig) {
        return new AliOssClient(ossClient(aliOssConfig), aliOssConfig);
    }

    public OSS ossClient(AliOssConfig aliOssConfig) {
        String securityToken = aliOssConfig.getSecurityToken();
        ClientBuilderConfiguration clientConfiguration = aliOssConfig.getClientConfig();
        if (ObjectUtil.isEmpty(securityToken) && ObjectUtil.isNotEmpty(clientConfiguration)) {
            return new OSSClientBuilder().build(aliOssConfig.getEndpoint(),
                    aliOssConfig.getAccessKeyId(),
                    aliOssConfig.getAccessKeySecret(), clientConfiguration);
        }
        if (ObjectUtil.isNotEmpty(securityToken) && ObjectUtil.isEmpty(clientConfiguration)) {
            return new OSSClientBuilder().build(aliOssConfig.getEndpoint(),
                    aliOssConfig.getAccessKeyId(),
                    aliOssConfig.getAccessKeySecret(), securityToken);
        }
        return new OSSClientBuilder().build(aliOssConfig.getEndpoint(),
                aliOssConfig.getAccessKeyId(),
                aliOssConfig.getAccessKeySecret(), securityToken, clientConfiguration);
    }

}
