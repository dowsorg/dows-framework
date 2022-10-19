package org.dows.sequence.redis;

import lombok.extern.slf4j.Slf4j;
import org.dows.sequence.api.IdGenerator;
import org.dows.sequence.api.IdKey;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

/**
 * 描述: id生产
 *
 * @author liat.zhang@gmail.com
 * @tel 15801818092
 * @create 2017-11-24 16:06
 */
@Service
@Slf4j
public class RedisIdGenerator /*extends  SnowflakeIdGeneratorForJedis*/ implements IdGenerator {
    protected RedisConnectionFactory redisConnectionFactory;
    private RedisTemplate redisTemplate;

    public RedisIdGenerator(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public RedisIdGenerator(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisIdGenerator() {
    }

    /**
     * 获取一年中的第多少天的第几秒 19年310天86400秒
     *
     * @param timeUnit 生成该 时间单位 的秒数
     * @return 一年中的第多少天的第几秒 19年310天86400秒
     */
    private static String getYDSOfNowPrefix(TimeUnit timeUnit) {
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
     * 获取一年中的第多少天的第多少个小时的第多少分 19年310天20时30分
     *
     * @param date
     * @return
     * @Description
     */
    private static String getDHMSMPrefix(Date date) {
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

    @Override
    public String generate(String module, String prefix) {
        return null;
    }


    @Override
    public String generateId(IdKey idBizCode) {
        String prefix = getTimePrefix(idBizCode);
        String key = idBizCode.getKey().concat(prefix);
        try {
            Long index = redisTemplate.opsForValue().increment(key);
            if (index <= 1) {
                // 设置一秒后超时，消除key
                redisTemplate.expire(key, idBizCode.getTimeout(), idBizCode.getTimeUnit());
            }
            // 补位操作 保证满足6位  id = BizPrefix + 时间 + 000000
            return idBizCode.getBizPrefix().concat(prefix.concat(String.format(idBizCode.getFmtSuffix(), index)));
        } catch (Exception ex) {
            log.error("分布式用户ID生成失败异常: " + ex.getMessage());
        }
        return prefix;
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
     * // prefix    年   日     日内的秒数  redis原子自增
     * //   1       20   234   86399     0000001
     * </pre>
     *
     * @param prefix bizPrefix
     * @return 返回 id.
     */
    @Override
    public String generateId(IdKey idBizCode, String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        //timePrefix 20 123 86400
        String timePrefix = getYDSOfNowPrefix(idBizCode.getTimeUnit());
        // idKey 20 123 86400
        String key = idBizCode.getKey().concat(prefix).concat(timePrefix);
        try {
            RedisConnection connection = this.redisConnectionFactory.getConnection();
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            final Long index = connection.incr(keyBytes);
            // 设置一秒后超时，清除key
            if (nonNull(index) && 2L > index) {
                long seconds = TimeoutUtils.toSeconds(idBizCode.getTimeout(), idBizCode.getTimeUnit());
                connection.expire(keyBytes, seconds);
            }
            // 补位操作 保证满足6位  id = BizPrefix + 时间 + 000000
            return prefix.concat(timePrefix.concat(String.format(idBizCode.getFmtSuffix(), index)));
        } catch (Exception ex) {
            log.error("分布式用户ID生成失败异常: " + ex.getMessage());
            //throw new UnichainException(ExceptionType.BusinessException, "分布式用户ID生成异常");
        }
        return timePrefix;
    }

    @Override
    public String generateId(String prefix, IdKey idBizCode) {
        if (prefix == null) {
            prefix = "";
        }
        //timePrefix 20 123 86400
        String timePrefix = getYDSOfNowPrefix(idBizCode.getTimeUnit());
        // idKey 20 123 86400
        String key = idBizCode.getKey().concat(prefix).concat(timePrefix);
        try {
            RedisConnection connection = this.redisConnectionFactory.getConnection();
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            final Long index = connection.incr(keyBytes);
            // 设置一秒后超时，清除key
            if (nonNull(index) && 2L > index) {
                long seconds = TimeoutUtils.toSeconds(idBizCode.getTimeout(), idBizCode.getTimeUnit());
                connection.expire(keyBytes, seconds);
            }
            // 补位操作 保证满足6位  id = BizPrefix + 时间 + 000000
            return prefix.concat(idBizCode.getBizPrefix().concat(timePrefix.concat(String.format(idBizCode.getFmtSuffix(), index))));
        } catch (Exception ex) {
            log.error("分布式用户ID生成失败异常: " + ex.getMessage());
            //throw new UnichainException(ExceptionType.BusinessException, "分布式用户ID生成异常");
        }
        return timePrefix;
    }


    @Override
    public Long randomLongId(int length) {
        return ThreadLocalRandom.current().nextLong();
    }

    @Override
    public long nextId(IdKey idType) {
        /* super.nextId(idType);*/
        return Long.valueOf(generateId(idType));
    }

    /**
     * @param digit 位数
     * @return 随机生成digit位数的数字
     */
    public Long randomId(int digit) {

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
    }

    private String getTimePrefix(IdKey idBizCode) {
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


    //    @Override
//    public Long generateId(Long appId, IdBizCode bizIdCode) {
//        if (appId == null || appId == 0) {
//            logger.error("分布式用户ID生成失败异常: 应用ID不存在");
//            throw new RuntimeException("分布式用户ID生成失败异常: 应用ID不存在");
//        }
//        String appid = String.format("%1$08d", appId);
//        String prefix = getYDHMPrefix(new Date());
//        String key = bizIdCode.getKey().concat(appid).concat(prefix);
//        try {
//            long index = redisRemplate.opsForValue().increment(key);
//            // 设置一个分钟后超时，消除key
//            redisRemplate.expire(key, 60, TimeUnit.SECONDS);
//            // 补位操作 保证满足6位  id = 时间 + 000000
//            String id = prefix.concat(String.format("%1$06d", index));
//            id = bizIdCode.getBizPrefix().toString().concat(id);
//            return Long.valueOf(id);
//        } catch (Exception ex) {
//            logger.error("分布式用户ID生成失败异常: " + ex.getMessage());
//            throw new RuntimeException(ex);
//        }
//    }


    /*    @Override
    public Long randomId(int length) {
        String prefix = getYDHMPrefix(new Date());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(9);
        }
        Long val = Long.valueOf(stringBuilder.toString());
        Random random = new Random(val);
        Long v = random.nextLong();
        return v;
    }*/

}
