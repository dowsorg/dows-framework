package org.dows.sequence.redis.config;

import org.dows.sequence.api.IdGenerator;
import org.dows.sequence.redis.RedisIdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 分布式 IdService 的自动配置
 */
@Configuration
public class IdAutoConfig {

    @Bean
    @ConditionalOnMissingBean(type = "org.dows.id.IdService")
    public IdGenerator idService(RedisConnectionFactory redisConnectionFactory) {
        return new RedisIdGenerator(redisConnectionFactory);
    }
}
