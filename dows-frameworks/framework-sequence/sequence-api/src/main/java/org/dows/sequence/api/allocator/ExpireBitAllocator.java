package org.dows.sequence.api.allocator;

public interface ExpireBitAllocator extends BitAllocator {

    /**
     * 获取该节点上一次的过期时间
     *
     * @param namespace 命名空间
     * @return 上次过期时间
     */
    default Long getLastExpireTime(String namespace) {
        return null;
    }
}
