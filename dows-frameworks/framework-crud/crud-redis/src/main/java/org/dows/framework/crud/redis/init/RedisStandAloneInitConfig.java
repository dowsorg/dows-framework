package org.dows.framework.crud.redis.init;

import org.dows.framework.crud.redis.config.PoolConfig;
import org.dows.framework.crud.redis.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 单机环境配置，在spring.redis.stand-alone中为true 时生效，默认为单机环境
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.redis", name = "standalone", havingValue = "true")
public class RedisStandAloneInitConfig {

    @Autowired
    private RedisConfig redisConfig;

    @Bean
    public JedisPoolConfig poolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        PoolConfig pool = redisConfig.getJedis().getPool();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMaxWaitMillis(pool.getMaxWait());
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMinIdle(pool.getMinIdle());

        return poolConfig;
    }

    @Bean
    public RedisStandaloneConfiguration redisConfiguration() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisConfig.getHost());
        configuration.setPort(redisConfig.getPort());
        configuration.setDatabase(redisConfig.getDatabase());
        configuration.setPassword(RedisPassword.of(redisConfig.getPassword()));

        return configuration;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig poolConfig,
                                                         RedisStandaloneConfiguration redisConfiguration) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisConfiguration);
        connectionFactory.setPoolConfig(poolConfig);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }
}
