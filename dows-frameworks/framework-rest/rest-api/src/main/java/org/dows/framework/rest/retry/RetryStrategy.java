package org.dows.framework.rest.retry;

/**
 * 重试机制
 *
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
public class RetryStrategy {
    private int maxRetries;
    private int intervalMs;

    public RetryStrategy(int maxRetries, int intervalMs) {
        this.maxRetries = maxRetries;
        this.intervalMs = intervalMs;
    }

    public boolean shouldRetry() {
        return maxRetries > 0;
    }

    public void retry() {
        maxRetries--;
        waitUntilNextTry();
    }

    private void waitUntilNextTry() {
        try {
            Thread.sleep(intervalMs);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
