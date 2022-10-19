package org.dows.sequence.api.allocator;

/**
 * 序列中的bit对应的值的分配器
 * <p>
 * 1bit符号 + 41bit时间 + 9bit序列 + 13bit的workId
 */
public interface BitAllocator {

    /**
     * 获取序列中的时间值
     *
     * @return 对应的时间
     */
    long getTimeValue();

    /**
     * 获取序列中的自增序列对应的值
     *
     * @return seq
     */
    long getSequenceValue();

    /**
     * 获取序列中的workId对应的值
     *
     * @return workId
     */
    int getWorkIdValue();
}
