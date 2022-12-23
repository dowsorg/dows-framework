package org.dows.sequence.snowflake.config;

import org.dows.sequence.api.IdGenerator;
import org.dows.sequence.snowflake.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = SnowFlakeProperties.class)
@ConditionalOnClass(value = SnowFlakeProperties.class)
public class SnowFlakeAutoConfiguration {

    @Autowired
    private SnowFlakeProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public IdGenerator getGenerator() {
        return new SnowflakeIdGenerator(SnowFlakeConfiguration.parse(properties));
    }

}
