package org.dows.sequence.core;

import lombok.experimental.UtilityClass;

import static org.dows.sequence.api.SequenceConstant.DELAY_THREAD_HOLD;
import static org.dows.sequence.api.SequenceConstant.TIME_BACK;

/**
 * 时间调整工具
 */
@UtilityClass
public class TimeAdjuster {

    /**
     * 优化调整时间，防止时间过快或者过慢
     *
     * <p>注意下面的{@code @SuppressWarnings("all")}这个不能删除，因为代码{@code Thread.sleep}通过不了sonar检测，添加
     * 这个可以使其通过，sonar检测建议采用{@code wait()}，但是{@code wait}这个会释放锁，但是这里的逻辑是不能释放锁，释放锁会有时间
     * 增速过快问题
     *
     * @param usedTime 序列中使用的时间
     */
    public void adjustTime(PaddedLong usedTime) {
        long currentUsedTime = usedTime.get();
        long now = System.currentTimeMillis();
        if (currentUsedTime < now) {
            // 如果过慢，则向前前进一段时间
            if (now - currentUsedTime > DELAY_THREAD_HOLD) {
                usedTime.addAndGet(DELAY_THREAD_HOLD >>> 2);
            }
        } else if (currentUsedTime > now) {
            // 时间过快，如果超过当前时间，则等待一定时间
            long offset = currentUsedTime - now;
            if (offset <= TIME_BACK) {
                try {
                    //时间偏差大于门限值，则等待两倍
                    Thread.sleep(offset << 1);
                    if (currentUsedTime > System.currentTimeMillis()) {
                        //throw new ButterflyException("回拨补偿尝试失败");
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            } else {
                //throw new ButterflyException("回拨时间过大");
            }
        }
    }

    /**
     * 获取相对一个固定时间起点的时间，用于延长年的使用时间
     *
     * @param currentTime 当前时间
     * @return 调整后的时间
     */
    public long getRelativeTime(long currentTime) {
        if (currentTime <= ButterflyIdGenerator.startTime) {
            //throw new ButterflyException("回拨时间超过：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date(currentTime)));
        }
        return currentTime - ButterflyIdGenerator.startTime;
    }
}
