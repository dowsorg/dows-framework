package org.dows.sequence.api;

public interface WorkerLoader {

    /**
     * 是否接收当前配置
     *
     * @param butterflyConfig 发号器对应的配置
     * @return true：接受，false：不接受
     */
    boolean acceptConfig(SequenceConfig butterflyConfig);

    /**
     * 获取workerId的实例
     *
     * @param namespace       命名空间
     * @param butterflyConfig 具体的配置
     * @return workerId处理器
     */
    WorkerIdHandler loadIdHandler(String namespace, SequenceConfig butterflyConfig);
}
