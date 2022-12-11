package org.dows.framework.oss.minio;

import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.dows.framework.oss.api.S3OssClient;
import org.dows.framework.oss.api.constant.OssConstant;
import org.dows.framework.oss.minio.model.MinioOssClientConfig;
import org.dows.framework.oss.minio.model.MinioOssConfig;
import io.minio.MinioClient;
import io.minio.http.HttpUtils;
import okhttp3.OkHttpClient;
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
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties({MinioOssProperties.class})
@ConditionalOnProperty(prefix = OssConstant.OSS, name = OssConstant.OssType.MINIO + CharPool.DOT + OssConstant.ENABLE,
        havingValue = OssConstant.DEFAULT_ENABLE_VALUE)
public class MinioOssConfiguration {

    public static final String DEFAULT_BEAN_NAME = "minioOssClient";

    @Autowired
    private MinioOssProperties minioOssProperties;

    @Bean
    public void minioOssClient() {
        Map<String, MinioOssConfig> minioOssConfigMap = minioOssProperties.getOssConfig();
        if (minioOssConfigMap.isEmpty()) {
            SpringUtil.registerBean(DEFAULT_BEAN_NAME, minioOssClient(minioClient(minioOssProperties), minioOssProperties));
        } else {
            String endpoint = minioOssProperties.getEndpoint();
            String accessKey = minioOssProperties.getAccessKey();
            String secretKey = minioOssProperties.getSecretKey();
            MinioOssClientConfig clientConfig = minioOssProperties.getClientConfig();
            minioOssConfigMap.forEach((name, minioOssConfig) -> {
                if (ObjectUtil.isEmpty(minioOssConfig.getEndpoint())) {
                    minioOssConfig.setEndpoint(endpoint);
                }
                if (ObjectUtil.isEmpty(minioOssConfig.getAccessKey())) {
                    minioOssConfig.setAccessKey(accessKey);
                }
                if (ObjectUtil.isEmpty(minioOssConfig.getSecretKey())) {
                    minioOssConfig.setSecretKey(secretKey);
                }
                if (ObjectUtil.isEmpty(minioOssConfig.getClientConfig())) {
                    minioOssConfig.setClientConfig(clientConfig);
                }
                SpringUtil.registerBean(name, minioOssClient(minioClient(minioOssConfig), minioOssConfig));
            });
        }
    }

    public S3OssClient minioOssClient(MinioClient minioClient, MinioOssConfig minioOssConfig) {
        return new MinioOssClient(minioClient, minioOssConfig);
    }

    public MinioClient minioClient(MinioOssConfig minioOssConfig) {
        MinioOssClientConfig clientConfig = minioOssConfig.getClientConfig();
        OkHttpClient okHttpClient = HttpUtils.newDefaultHttpClient(
                clientConfig.getConnectTimeout(), clientConfig.getWriteTimeout(), clientConfig.getReadTimeout());
        return MinioClient.builder()
                .endpoint(minioOssConfig.getEndpoint())
                .credentials(minioOssConfig.getAccessKey(), minioOssConfig.getSecretKey())
                .httpClient(okHttpClient)
                .build();
    }
}
