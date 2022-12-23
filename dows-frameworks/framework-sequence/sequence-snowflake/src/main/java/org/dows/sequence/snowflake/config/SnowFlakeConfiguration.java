package org.dows.sequence.snowflake.config;

public class SnowFlakeConfiguration {

    private final long SEQUENCE_BITS = 12L;

    private final long WORKER_ID_OFFSET = SEQUENCE_BITS;

    private final long SEQUENCE_MASK = ~(-1 << SEQUENCE_BITS);

    private long INITIAL_TIME_STAMP;

    private long WORK_ID_BITS;

    private long DATA_CENTER_ID_BITS;

    private long MAX_WORKER_ID;

    private long MAX_DATA_CENTER_ID;

    private long DATA_CENTER_ID_OFFSET;

    private long TIME_STAMP_OFFSET;

    private long workerId;

    private long dataCenterId;

    public static SnowFlakeConfiguration parse(SnowFlakeProperties properties) {
        SnowFlakeConfiguration configuration = new SnowFlakeConfiguration();
        configuration.INITIAL_TIME_STAMP = properties.getInitialTimestamp();
        configuration.WORK_ID_BITS = properties.getWorkerIdBits();
        configuration.DATA_CENTER_ID_BITS = properties.getDataCenterIdBits();
        configuration.MAX_WORKER_ID = ~(-1 << configuration.WORK_ID_BITS);
        configuration.MAX_DATA_CENTER_ID = ~(-1 << configuration.DATA_CENTER_ID_BITS);
        if (properties.getWorkerId() > configuration.MAX_WORKER_ID || properties.getWorkerId() < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can not be greater than %d or smaller than 0",
                    configuration.MAX_WORKER_ID));
        }
        if (properties.getDataCenterId() > configuration.MAX_DATA_CENTER_ID || properties.getDataCenterId() < 0) {
            throw new IllegalArgumentException(String.format("DataCenterId can not be greater than %d or smaller than 0",
                    configuration.MAX_DATA_CENTER_ID));
        }
        configuration.workerId = properties.getWorkerId();
        configuration.dataCenterId = properties.getDataCenterId();
        configuration.DATA_CENTER_ID_OFFSET = configuration.SEQUENCE_BITS + configuration.WORK_ID_BITS;
        configuration.TIME_STAMP_OFFSET = configuration.DATA_CENTER_ID_OFFSET + configuration.DATA_CENTER_ID_BITS;
        return configuration;
    }

    public long getSEQUENCE_BITS() {
        return SEQUENCE_BITS;
    }

    public long getWORKER_ID_OFFSET() {
        return WORKER_ID_OFFSET;
    }

    public long getSEQUENCE_MASK() {
        return SEQUENCE_MASK;
    }

    public long getINITIAL_TIME_STAMP() {
        return INITIAL_TIME_STAMP;
    }

    public void setINITIAL_TIME_STAMP(long INITIAL_TIME_STAMP) {
        this.INITIAL_TIME_STAMP = INITIAL_TIME_STAMP;
    }

    public long getWORK_ID_BITS() {
        return WORK_ID_BITS;
    }

    public void setWORK_ID_BITS(long WORK_ID_BITS) {
        this.WORK_ID_BITS = WORK_ID_BITS;
    }

    public long getDATA_CENTER_ID_BITS() {
        return DATA_CENTER_ID_BITS;
    }

    public void setDATA_CENTER_ID_BITS(long DATA_CENTER_ID_BITS) {
        this.DATA_CENTER_ID_BITS = DATA_CENTER_ID_BITS;
    }

    public long getMAX_WORKER_ID() {
        return MAX_WORKER_ID;
    }

    public void setMAX_WORKER_ID(long MAX_WORKER_ID) {
        this.MAX_WORKER_ID = MAX_WORKER_ID;
    }

    public long getMAX_DATA_CENTER_ID() {
        return MAX_DATA_CENTER_ID;
    }

    public void setMAX_DATA_CENTER_ID(long MAX_DATA_CENTER_ID) {
        this.MAX_DATA_CENTER_ID = MAX_DATA_CENTER_ID;
    }

    public long getDATA_CENTER_ID_OFFSET() {
        return DATA_CENTER_ID_OFFSET;
    }

    public void setDATA_CENTER_ID_OFFSET(long DATA_CENTER_ID_OFFSET) {
        this.DATA_CENTER_ID_OFFSET = DATA_CENTER_ID_OFFSET;
    }

    public long getTIME_STAMP_OFFSET() {
        return TIME_STAMP_OFFSET;
    }

    public void setTIME_STAMP_OFFSET(long TIME_STAMP_OFFSET) {
        this.TIME_STAMP_OFFSET = TIME_STAMP_OFFSET;
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
