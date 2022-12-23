package org.dows.sequence.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.sequence.api.IdGenerator;
import org.dows.sequence.api.IdKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 描述: id生产
 *
 * @author liat.zhang@gmail.com
 * @tel 15801818092
 * @create 2017-11-24 16:06
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RedisIdGenerator implements IdGenerator {

    private final RedisTemplate redisTemplate;


    private String generateId(IdKey idBizCode) {
        /**
         //timePrefix 20 123 86400
         String timePrefix = getYDSOfNowPrefix(idBizCode.getTimeUnit());
         // idKey 20 123 86400
         String key = idBizCode.getKey().concat(timePrefix);
         try {
         final Long index = redisTemplate.opsForValue().increment(key);
         // 设置一秒后超时，清除key
         if (nonNull(index) && 2L > index) {
         redisTemplate.expire(key, idBizCode.getTimeout(), idBizCode.getTimeUnit());
         }
         // 补位操作 保证满足6位  id = BizPrefix + 时间 + 000000
         return idBizCode.getBizPrefix().concat(timePrefix.concat(String.format(idBizCode.getFmtSuffix(), index)));
         } catch (Exception ex) {
         log.error("分布式用户ID生成失败异常: " + ex.getMessage());
         //throw new UnichainException(ExceptionType.BusinessException, "分布式用户ID生成异常");
         }
         return timePrefix;
         */
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


    @Override
    public long nextId(IdKey idKey) {
        return Long.valueOf(generateId(idKey));
    }

    @Override
    public long nextId() {
        return 0;
    }

}
