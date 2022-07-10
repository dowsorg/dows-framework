package org.dows.framework.crud.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 使用BlockingQueue阻塞队列进行请求合并
 */
public class Flusher<T> {

    /**
     * 防止多个线程同时执行。增加一个随机数间隔
     */
    private static final Random RANDOM = new Random();
    /**
     * 随机数
     */
    private static final int DELTA = 50;
    private static ScheduledExecutorService TIMER = new ScheduledThreadPoolExecutor(1);
    private static ExecutorService POOL = Executors.newCachedThreadPool();
    private final FlushThread<T>[] flushThreads;
    private AtomicInteger index;

    /**
     * @param name      线程名称
     * @param queueSize 队列大小
     * @param threads   线程数
     * @param processor 处理器
     */
    public Flusher(String name, int queueSize, int threads, Processor<T> processor) {
        this(name, queueSize, 1000, queueSize, threads, processor);
    }

    /**
     * @param name          线程名称
     * @param flushInterval 刷新间隔(单位:毫秒)
     * @param queueSize     队列大小
     * @param threads       线程数
     * @param processor     处理器
     */
    public Flusher(String name, int flushInterval, int queueSize, int threads, Processor<T> processor) {
        this(name, queueSize, flushInterval, queueSize, threads, processor);
    }

    /**
     * @param name          线程名称
     * @param bufferSize    缓冲区大小
     * @param flushInterval 刷新间隔(单位:毫秒)
     * @param queueSize     队列大小
     * @param threads       线程数
     * @param processor     处理器
     */
    public Flusher(String name, int bufferSize, int flushInterval, int queueSize, int threads, Processor<T> processor) {

        this.flushThreads = new FlushThread[threads];

        if (threads > 1) {
            index = new AtomicInteger();
        }

        for (int i = 0; i < threads; i++) {
            final FlushThread<T> flushThread = new FlushThread<T>(name + "-" + i, bufferSize, flushInterval, queueSize, processor);
            flushThreads[i] = flushThread;
            POOL.submit(flushThread);
            // 定时调用 timeOut()方法
            int initialDelay = RANDOM.nextInt(DELTA);
            TIMER.scheduleAtFixedRate(flushThread::timeOut, initialDelay, flushInterval, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 对 index 取模，保证多线程都能被add
     *
     * @param item 具体数据
     * @return
     */
    public boolean add(T item) {
        int len = flushThreads.length;
        if (len == 1) {
            return flushThreads[0].add(item);
        }
        int mod = index.incrementAndGet() % len;
        return flushThreads[mod].add(item);
    }

    private static class FlushThread<T> implements Runnable {

        private final String name;

        /**
         * 队列大小
         */
        private final int bufferSize;
        /**
         * 持有数据的阻塞队列
         */
        private final BlockingQueue<T> queue;
        /**
         * 达成条件后具体执行的方法
         */
        private final Processor<T> processor;
        /**
         * 操作间隔
         */
        private int flushInterval;
        /**
         * 上一次提交的时间
         */
        private volatile long lastFlushTime;
        private volatile Thread writer;

        public FlushThread(String name, int bufferSize, int flushInterval, int queueSize, Processor<T> processor) {
            this.name = name;
            this.bufferSize = bufferSize;
            this.flushInterval = flushInterval;
            this.lastFlushTime = System.currentTimeMillis();
            this.processor = processor;
            this.queue = new ArrayBlockingQueue<>(queueSize);
        }

        public boolean add(T item) {
            boolean result = queue.offer(item);
            flushOnDemand();
            return result;
        }

        public void timeOut() {
            if (System.currentTimeMillis() - lastFlushTime >= flushInterval) {
                start();
            }
        }

        private void start() {
            LockSupport.unpark(writer);
        }

        private void flushOnDemand() {
            if (queue.size() > bufferSize) {
                start();
            }
        }

        public void flush() {
            lastFlushTime = System.currentTimeMillis();
            List<T> temp = new ArrayList<>(bufferSize);
            int size = queue.drainTo(temp, bufferSize);
            if (size > 0) {
                try {
                    processor.process(temp);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean canFlush() {
            return queue.size() > bufferSize || System.currentTimeMillis() - lastFlushTime > flushInterval;
        }

        @Override
        public void run() {
            writer = Thread.currentThread();
            writer.setName(name);

            while (!writer.isInterrupted()) {
                while (!canFlush()) {
                    LockSupport.park(this);
                }
                flush();
            }
        }

    }


}
