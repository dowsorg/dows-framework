package org.dows.sequence.snowflake.config;

import org.dows.sequence.api.SequenceConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lait.zhang@gmail.com
 * @description: 雪花算法配置
 * @weixin SH330786
 * @date 1/17/2022
 */
@ConfigurationProperties(prefix = SequenceConfig.PREFIX + SnowflakeConfig.PREFIX)
public class SnowflakeConfig {
    /**
     * 前缀
     */
    public static final String PREFIX = ".snowflake";
    /**
     * 工作机器ID,默认值
     */
    public static final long DEFAULT_WORKER_ID = 0L;
    /**
     * 数据中心ID:,默认值
     */
    public static final long DEFAULT_DATACENTER_ID = 0L;
    /**
     * 工作机器ID(0~31)
     */
    private long workerId = DEFAULT_WORKER_ID;

    /**
     * 数据中心ID(0~31)
     */
    private long datacenterId = DEFAULT_DATACENTER_ID;

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(long datacenterId) {
        this.datacenterId = datacenterId;
    }
}
