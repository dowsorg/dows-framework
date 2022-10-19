package org.dows.sequence.core;

import lombok.experimental.UtilityClass;
import org.dows.sequence.api.SequenceConfig;
import org.dows.sequence.api.allocator.BeanBitAllocator;
import org.dows.sequence.api.allocator.BitAllocator;

import java.util.Collection;

@UtilityClass
public class BeanBitAllocatorFactory {

    static {
        //ServiceLoaderFactory.init(BeanBitAllocator.class);
    }

    public BitAllocator getBitAllocator(String namespace, SequenceConfig butterflyConfig) {
        Collection<BeanBitAllocator> bitAllocatorCollection = ServiceLoaderFactory.getChildObject(BeanBitAllocator.class);
        if (null != bitAllocatorCollection) {
            for (BeanBitAllocator allocator : bitAllocatorCollection) {
                if (allocator.acceptConfig(butterflyConfig)) {
                    allocator.postConstruct(namespace, butterflyConfig);
                    return allocator;
                }
            }
        }
        return new DefaultBitAllocator(namespace, butterflyConfig);
    }
}
