package org.dows.framework.crud.redis.config;


public class JedisConfig {

    private PoolConfig pool = new PoolConfig();

    public PoolConfig getPool() {
        return pool;
    }

    public void setPool(PoolConfig pool) {
        this.pool = pool;
    }
}
