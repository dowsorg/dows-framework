package org.dows.sequence.snowflake;

import lombok.extern.slf4j.Slf4j;
import org.dows.sequence.api.IdGenerator;
import org.dows.sequence.api.IdKey;
import org.dows.sequence.snowflake.config.SnowFlakeConfiguration;

import java.util.concurrent.locks.StampedLock;

/**
 * @author lait.zhang@gmail.com
 * @description: 唯一编码生成服务, 基于Twitter_Snowflake
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 * @weixin SH330786
 * @date 1/17/2022
 */
@Slf4j
public class SnowflakeIdGenerator implements IdGenerator {
    private final StampedLock lock = new StampedLock();

    private final SnowFlakeConfiguration configuration;

    private long sequence = 0L;

    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(SnowFlakeConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public long nextId(IdKey idType) {
        return 0;
    }

    /**
     * Gets the next id.
     *
     * @return a next distinct id
     */
    public long nextId() {
        long stamp = lock.writeLock(), ans;
        try {
            long timestamp = System.currentTimeMillis();
            if (timestamp < lastTimestamp) {
                throw new RuntimeException("Current time is smaller than last timestamp");
            }
            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & configuration.getSEQUENCE_MASK();
                if (sequence == 0) {
                    timestamp = tillNextMills(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            lastTimestamp = timestamp;
            ans = ((timestamp - configuration.getINITIAL_TIME_STAMP()) << configuration.getTIME_STAMP_OFFSET()) |
                    (configuration.getDataCenterId() << configuration.getDATA_CENTER_ID_OFFSET()) |
                    (configuration.getWorkerId() << configuration.getWORKER_ID_OFFSET()) | sequence;
        } finally {
            lock.unlockWrite(stamp);
        }
        return ans;
    }


    private long tillNextMills(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}
