package org.dows.framework.feign;

import org.dows.framework.rest.RestClientFactory;
import org.dows.framework.rest.config.RestSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = RestSetting.class)
@ConditionalOnClass(RestSetting.class)
@ConditionalOnProperty(prefix = RestSetting.PREFIX, value = RestSetting.ENABLED, matchIfMissing = true)
public class RestFeignAutoConfiguration {
    @Autowired
    private RestSetting restSetting;

    @Bean
    @ConditionalOnMissingBean(RestClientFactory.class)
    public RestClientFactory createRemoteClientFactory() throws Exception {
        return new RestFeignClientFactory(restSetting);
    }
}
