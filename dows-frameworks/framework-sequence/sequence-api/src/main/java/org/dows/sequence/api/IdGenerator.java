package org.dows.sequence.api;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author lait.zhang@gmail.com
 * @description: 唯一序列生成接口
 * @weixin SH330786
 * @date 1/17/2022
 */
//@Spi(ImplTyp.DEFAULT)
public interface IdGenerator {


    /**
     * 获取一年中的第多少天的第几秒 19年310天86400秒
     *
     * @param timeUnit 生成该 时间单位 的秒数
     * @return 一年中的第多少天的第几秒 19年310天86400秒
     */
    default String getYDSOfNowPrefix(TimeUnit timeUnit) {
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        // 今天是第多少天
        int year = dateTime.getYear();
        int day = dateTime.getDayOfYear();
        int second;
        // 0补位操作 必须满足2位
        String yearFmt = String.format("%1$02d", (year - 2000));
        // 0补位操作 必须满足3位
        String dayFmt = String.format("%1$03d", day);
        // 生成该 时间单位 的秒数
        if (timeUnit == TimeUnit.SECONDS) {
            second = (dateTime.getHour() * 3600) +
                    (dateTime.getMinute() * 60) +
                    dateTime.getSecond();
        } else if (timeUnit == TimeUnit.MINUTES) {
            second = (dateTime.getHour() * 3600) +
                    (dateTime.getMinute() * 60);
        } else if (timeUnit == TimeUnit.HOURS) {
            second = dateTime.getHour() * 3600;
        } else if (timeUnit == TimeUnit.DAYS) {
            second = 0;
        } else {
            second = (dateTime.getHour() * 3600) +
                    (dateTime.getMinute() * 60) +
                    dateTime.getSecond();
        }

        // 0补位操作 必须满足5位
        String secondFmt = String.format("%1$05d", second);
        return yearFmt + dayFmt + secondFmt;
    }

    /**
     * 获取一年中的第多少天的第多少个小时的第多少分 19年310天20时30分10秒123
     *
     * @param date
     * @return
     * @Description
     */
    default String getDHMSMPrefix(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        // 今天是第多少天
        int day = c.get(Calendar.DAY_OF_YEAR);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millSecond = c.get(Calendar.MILLISECOND);
        return (year - 2000) + String.format("%1$03d%2$02d%3$02d%4$02d%5$03d", day, hour, minute, second, millSecond);
    }


    default String getTimePrefix(IdKey idBizCode) {
        // 20 123 13 24 34
        String prefix = getDHMSMPrefix(new Date());
        if (idBizCode.getTimeUnit() == TimeUnit.DAYS) {
            prefix = prefix.substring(0, 5);
        } else if (idBizCode.getTimeUnit() == TimeUnit.HOURS) {
            prefix = prefix.substring(0, 7);
        } else if (idBizCode.getTimeUnit() == TimeUnit.MINUTES) {
            prefix = prefix.substring(0, 9);
        } else if (idBizCode.getTimeUnit() == TimeUnit.SECONDS) {
            prefix = prefix.substring(0, 11);
        } else if (idBizCode.getTimeUnit() == TimeUnit.MILLISECONDS) {
            prefix = prefix.substring(0, 14);
        }
        return prefix;
    }


    /**
     * 随机 ID
     *
     * @param digit 位数
     * @return 随机生成digit位数的数字
     */
    default long randomId(int digit) {
        return ThreadLocalRandom.current().nextLong();
        /**
         StringBuilder str = new StringBuilder();
         String prefix = getDHMSMPrefix(new Date());
         for (int i = 0; i < digit; i++) {
         if (i == 0 && digit > 1) {
         str.append(new Random().nextInt(9) + 1);
         } else {
         str.append(new Random().nextInt(10));
         }
         }
         return Long.valueOf(prefix.concat(str.toString()));
         */
    }

    /**
     * {@link IdKey#APP_ID} 返回 ID格式 prefix + 0020123000001 .<br>
     * <pre>
     * // 返回 id: prefix + 0020123000001
     * // prefix  idBizCode  年  日   redis原子自增
     * // prefix    00       20 123   000001
     * </pre>
     * 除 {@link IdKey#APP_ID} 外返回
     * <pre>
     * // 返回 id: prefix + 202348639900000001
     * // prefix      年  日  日内的秒数 redis原子自增
     * //   1        20 234 86399 00000
     * </pre>
     *
     * @return 返回 id.
     */
    long nextId(IdKey idType);

    /**
     * 无业务雪花ID
     *
     * @return
     */
    long nextId();


}
