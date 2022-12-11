package org.dows.framework.oss.local;

import cn.hutool.core.text.CharPool;
import cn.hutool.extra.spring.SpringUtil;
import com.qcloud.cos.COSClient;
import org.dows.framework.oss.api.S3OssClient;
import org.dows.framework.oss.api.constant.OssConstant;
import org.dows.framework.oss.local.model.LocalOssConfig;
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
@ConditionalOnClass(COSClient.class)
@EnableConfigurationProperties({LocalOssProperties.class})
@ConditionalOnProperty(prefix = OssConstant.OSS, name = OssConstant.OssType.LOCAL + CharPool.DOT + OssConstant.ENABLE,
        havingValue = OssConstant.DEFAULT_ENABLE_VALUE, matchIfMissing = true)
public class LocalOssConfiguration {

    public static final String DEFAULT_BEAN_NAME = "localOssClient";

    @Autowired
    private LocalOssProperties localProperties;

    @Bean
    public void localOssClient() {
        Map<String, LocalOssConfig> localOssConfigMap = localProperties.getOssConfig();
        if (localOssConfigMap.isEmpty()) {
            SpringUtil.registerBean(DEFAULT_BEAN_NAME, localOssClient(localProperties));
        } else {
            localOssConfigMap.forEach((name, localOssConfig) -> {
                SpringUtil.registerBean(name, localOssClient(localOssConfig));
            });
        }
    }

    public S3OssClient localOssClient(LocalOssConfig localOssConfig) {
        return new LocalOssClient(localOssConfig);
    }
}
