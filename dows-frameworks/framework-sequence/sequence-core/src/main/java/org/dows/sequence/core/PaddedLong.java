package org.dows.sequence.core;

import java.util.concurrent.atomic.AtomicLong;

public final class PaddedLong extends AtomicLong {

    /**
     * 添加防止伪共享
     */
    private volatile long p1, p2, p3, p4, p5, p6 = 7L;

    public PaddedLong(long value) {
        super(value);
    }
}
