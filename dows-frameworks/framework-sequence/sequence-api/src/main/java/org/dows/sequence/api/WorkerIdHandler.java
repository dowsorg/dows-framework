package org.dows.sequence.api;

/**
 * 节点 worker_xx 读取和更新管理器
 */
public interface WorkerIdHandler {

    /**
     * 当前工作节点的下次失效时间
     *
     * @return 时间的long类型
     */
    Long getLastExpireTime();

    /**
     * 获取节点的下表索引
     *
     * @return workId
     */
    Integer getWorkerId();
}
