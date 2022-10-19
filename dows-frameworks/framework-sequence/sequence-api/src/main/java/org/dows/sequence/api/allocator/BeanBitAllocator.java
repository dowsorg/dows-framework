package org.dows.sequence.api.allocator;


import org.dows.sequence.api.SequenceConfig;

public interface BeanBitAllocator extends BitAllocator {

    /**
     * 是否接受对应的配置
     *
     * @param butterflyConfig 具体的配置
     * @return true：接受，false：不接受
     */
    default boolean acceptConfig(SequenceConfig butterflyConfig) {
        return true;
    }

    /**
     * 初始化的配置的处理
     *
     * @param namespace       命名空间
     * @param butterflyConfig 配置
     */
    default void postConstruct(String namespace, SequenceConfig butterflyConfig) {
    }
}
