package org.dows.sequence.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dows.sequence.api.SequenceConfig;
import org.dows.sequence.api.allocator.BitAllocator;

import static org.dows.sequence.api.SequenceConstant.*;


@Slf4j
public final class UuidSplicer {

    @Getter
    private BitAllocator bitAllocator;

    public UuidSplicer(String bizNamespace, SequenceConfig butterflyConfig) {
        synchronized (this) {
            this.bitAllocator = BeanBitAllocatorFactory.getBitAllocator(bizNamespace, butterflyConfig);

            // 延迟启动固定时间10ms
            try {
                this.wait(DELAY_START_TIME);
            } catch (InterruptedException e) {
                log.warn(LOG_PRE + "delay start fail");
                Thread.currentThread().interrupt();
            }
        }
    }

    synchronized public Long splice() {
        if (null == bitAllocator) {
            //throw new ButterflyException("bitAllocator not init");
        }
        int workerId = bitAllocator.getWorkIdValue();
        long seq = bitAllocator.getSequenceValue();
        long time = bitAllocator.getTimeValue();

        return (time << ((SEQ_HIGH_BITS + WORKER_BITS + SEQ_LOW_BITS)) |
                (((seq << WORKER_BITS) & SEQ_HIGH_MARK)) |
                ((workerId << SEQ_LOW_BITS) & WORKER_MARK) |
                (seq & SEQ_LOW_MARK));
    }
}
