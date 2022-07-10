package org.dows.framework.crud.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 主配置
 */
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {

    private String password;
    private String timeout = "6000";
    private Boolean standAlone = true;
    private String host = "127.0.0.1";
    private int port = 6379;
    private int database = 0;
    private RedisClusterConfig cluster = new RedisClusterConfig();
    private JedisConfig jedis = new JedisConfig();

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public Boolean getStandAlone() {
        return standAlone;
    }

    public void setStandAlone(Boolean standAlone) {
        this.standAlone = standAlone;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public RedisClusterConfig getCluster() {
        return cluster;
    }

    public void setCluster(RedisClusterConfig cluster) {
        this.cluster = cluster;
    }

    public JedisConfig getJedis() {
        return jedis;
    }

    public void setJedis(JedisConfig jedis) {
        this.jedis = jedis;
    }
}
