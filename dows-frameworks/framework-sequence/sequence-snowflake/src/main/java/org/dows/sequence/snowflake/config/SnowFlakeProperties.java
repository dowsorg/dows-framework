package org.dows.sequence.snowflake.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = SnowFlakeProperties.SNOWFLAKE_PREFIX)
public class SnowFlakeProperties {

    /**
     * Prefix of SnowFlake properties.
     */
    public static final String SNOWFLAKE_PREFIX = "snowflake";


    /**
     * Initial time stamp, usually the time stamp when the application is created.
     * For example,
     * <pre>
     *     Calendar calendar = Calendar.getInstance();
     *     calendar.set(2020, Calendar.NOVEMBER, 1);
     *     long timestamp = calendar.getTimeInMillis());
     * </pre>
     */
    private long initialTimestamp = 1604383611644L;

    /**
     * # of bits for worker id.
     */
    private long workerIdBits = 5L;

    /**
     * # of bits for data center.
     */
    private long dataCenterIdBits = 5L;

    /**
     * current worker id.
     */
    private long workerId = 1L;

    /**
     * current data center id.
     */
    private long dataCenterId = 1L;

    public long getInitialTimestamp() {
        return initialTimestamp;
    }

    public void setInitialTimestamp(long initialTimestamp) {
        this.initialTimestamp = initialTimestamp;
    }

    public long getWorkerIdBits() {
        return workerIdBits;
    }

    public void setWorkerIdBits(long workerIdBits) {
        this.workerIdBits = workerIdBits;
    }

    public long getDataCenterIdBits() {
        return dataCenterIdBits;
    }

    public void setDataCenterIdBits(long dataCenterIdBits) {
        this.dataCenterIdBits = dataCenterIdBits;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

}
