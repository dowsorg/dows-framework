//package org.dows.crud.redis;
//
//import org.dows.crud.redis.utils.SerializeUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.redis.connection.DataType;
//import org.springframework.data.redis.core.*;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.Resource;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//@Repository
//public class StringReidsMapper {
//    //此处用的hash存储的对象 所以加一个key
//    private static final String objectKey = "objectMapKey";
//
//    private final RedisTemplate<String, Object> redisTemplate;
////	redisTemplate.opsForValue();//操作字符串
////	redisTemplate.opsForHash();//操作hash
////	redisTemplate.opsForList();//操作list
////	redisTemplate.opsForSet();//操作set
////	redisTemplate.opsForZSet();//操作有序set
//
//    //key操作
//
//    /**
//     * 删除key 可传多个key
//     *
//     * @param keys
//     */
//    public void delete(String... keys) {
//        redisTemplate.delete(CollectionUtils.arrayToList(keys));
//    }
//
//    /**
//     * 给指定的key设置过期时间  单位为毫秒
//     *
//     * @param key
//     * @param timeout 过期时间 单位是毫秒
//     */
//    public void expire(String key, Integer timeout) {
//        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
//    }
//
//
//    /**
//     * 移除key的过期时间，将key永久保存
//     *
//     * @param key
//     */
//    public void persist(String key) {
//        redisTemplate.persist(key);
//    }
//
//    /**
//     * 检验该key是否存在 存在返回true
//     *
//     * @param key
//     */
//    public boolean hasKey(String key) {
//        return redisTemplate.hasKey(key);
//    }
//
//    /**
//     * 返回 key 所储存的值的类型
//     *
//     * @param key
//     * @return
//     */
//    public DataType getType(String key) {
//        return redisTemplate.type(key);
//    }
//
//    /**
//     * 从redis中随机返回一个key
//     *
//     * @return
//     */
//    public Object redomKey() {
//        return redisTemplate.randomKey();
//    }
//
//
//    //对象操作
//
//    /**
//     * 字符串：存对象
//     *
//     * @param key
//     * @param value
//     */
//    public void setObject(String key, Object value) {
//        final byte[] values = SerializeUtil.serialize(value);
//        redisTemplate.execute((RedisCallback<Void>) connection -> {
//            connection.set(key.getBytes(), values);
//            return null;
//        });
//    }
//
//    /**
//     * 字符串：取对象
//     *
//     * @param key
//     * @param targetClass：实体对象
//     * @return
//     */
//    public <T> T getObject(final String key, Class<T> targetClass) {
//        byte[] result = redisTemplate.execute((RedisCallback<byte[]>) connection -> connection.get(key.getBytes()));
//        if (result == null) {
//            return null;
//        }
//        return SerializeUtil.deserialize(result, targetClass);
//    }
//
//    /**
//     * 字符串：存对象 并设置失效时间
//     *
//     * @param key
//     * @param value
//     * @param expireTime
//     */
//    public <T> void setObjectTime(String key, T value, final long expireTime) {
//        final byte[] values = SerializeUtil.serialize(value);
//        redisTemplate.execute((RedisCallback<Void>) connection -> {
//            connection.setEx(key.getBytes(), expireTime, values);
//            return null;
//        });
//    }
//
//    /**
//     * 字符串：存放多个对象
//     *
//     * @param key
//     * @param objList
//     */
//    public <T> void setList(String key, List<T> objList) {
//        final byte[] value = SerializeUtil.serializeList(objList);
//        redisTemplate.execute((RedisCallback<Void>) connection -> {
//            connection.set(key.getBytes(), value);
//            return null;
//        });
//    }
//
//    /**
//     * 取多个对象
//     *
//     * @param key
//     * @param targetClass
//     * @return
//     */
//    public <T> List<T> getList(final String key, Class<T> targetClass) {
//        byte[] result = redisTemplate.execute((RedisCallback<byte[]>) connection -> connection.get(key.getBytes()));
//        if (result == null) {
//            return null;
//        }
//        return SerializeUtil.deserializeList(result, targetClass);
//    }
//
//
//    /**
//     * 从hashMap中取出对象
//     *
//     * @param key
//     * @param hashKey
//     * @param targetClass
//     * @param <T>
//     * @return
//     */
//    public <T> T hGetObject(final String key, final String hashKey, Class<T> targetClass) {
//        byte[] result = redisTemplate.execute((RedisCallback<byte[]>) connection -> connection.hGet(key.getBytes(), hashKey.getBytes()));
//        if (result == null) {
//            return null;
//        }
//        return SerializeUtil.deserialize(result, targetClass);
//    }
//
//
//    /**
//     * 在hashMap中放入对象
//     *
//     * @param key
//     * @param hashKey
//     * @param obj
//     * @param <T>
//     * @return
//     */
//    public <T> boolean hSetObject(String key, String hashKey, T obj) {
//        final byte[] value = SerializeUtil.serialize(obj);
//        return redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.hSet(key.getBytes(), hashKey.getBytes(), value));
//    }
//
//    /**
//     * 在hashMap中放入对象并设置失效时间
//     *
//     * @param key
//     * @param hashKey
//     * @param obj
//     * @param <T>
//     */
//    public <T> void hSetObjectTime(String key, String hashKey, T obj, long expireTime) {
//        final byte[] value = SerializeUtil.serialize(obj);
//        redisTemplate.execute((RedisCallback<Void>) connection -> {
//            connection.hSet(key.getBytes(), hashKey.getBytes(), value);
//            connection.expire(key.getBytes(), expireTime);
//            return null;
//        });
//    }
//
//    /**
//     * 从hashmap中取出list对象
//     *
//     * @param key
//     * @param hashKey
//     * @param targetClass
//     * @param <T>
//     * @return
//     */
//    public <T> List<T> hGetListObject(final String key, final String hashKey, Class<T> targetClass) {
//        byte[] result = redisTemplate.execute((RedisCallback<byte[]>) connection -> connection.hGet(key.getBytes(), hashKey.getBytes()));
//        if (result == null) {
//            return null;
//        }
//        return SerializeUtil.deserializeList(result, targetClass);
//    }
//
//    /**
//     * 往hashMap中放入一个list对象
//     *
//     * @param key
//     * @param hashKey
//     * @param objList
//     * @param <T>
//     * @return
//     */
//    public <T> boolean hSetListObject(String key, String hashKey, List<T> objList) {
//        final byte[] value = SerializeUtil.serializeList(objList);
//        return redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.hSet(key.getBytes(), hashKey.getBytes(), value));
//    }
//
//    /**
//     * 根据key和hashKeys取出多个map对象
//     *
//     * @param key
//     * @param hashKeys    一个或多个
//     * @param targetClass
//     * @param <T>
//     * @return
//     */
//    public <T> Map<String, T> hMGetObject(String key, Collection<String> hashKeys, Class<T> targetClass) {
//        List<byte[]> byteFields = hashKeys.stream().map(String::getBytes).collect(Collectors.toList());
//        byte[][] queryFields = new byte[byteFields.size()][];
//        byteFields.toArray(queryFields);
//        List<byte[]> cache = redisTemplate.execute((RedisCallback<List<byte[]>>) connection -> connection.hMGet(key.getBytes(), queryFields));
//
//        Map<String, T> results = new HashMap<>(16);
//        Iterator<String> it = hashKeys.iterator();
//        int index = 0;
//        while (it.hasNext()) {
//            String k = it.next();
//            if (cache.get(index) == null) {
//                index++;
//                continue;
//            }
//            results.put(k, SerializeUtil.deserialize(cache.get(index), targetClass));
//            index++;
//        }
//        return results;
//    }
//
//    /**
//     * 放入一个map对象
//     *
//     * @param key
//     * @param values
//     * @param <T>
//     */
//    public <T> void hMSetObject(String key, Map<String, T> values) {
//        Map<byte[], byte[]> byteValues = new HashMap<>(16);
//        for (Map.Entry<String, T> value : values.entrySet()) {
//            byteValues.put(value.getKey().getBytes(),
//                    SerializeUtil.serialize(value.getValue()));
//        }
//        redisTemplate.execute((RedisCallback<Void>) connection -> {
//            connection.hMSet(key.getBytes(), byteValues);
//            return null;
//        });
//    }
//
//
//    /**
//     * 根据key获取所有的map
//     *
//     * @param key
//     * @param targetClass
//     * @param <T>
//     * @return
//     */
//    public <T> Map<String, T> hGetAll(String key, Class<T> targetClass) {
//        Map<byte[], byte[]> records = redisTemplate
//                .execute((RedisCallback<Map<byte[], byte[]>>) connection -> connection.hGetAll(key.getBytes()));
//        Map<String, T> ret = new HashMap<>(16);
//        for (Map.Entry<byte[], byte[]> record : records.entrySet()) {
//            T obj = SerializeUtil.deserialize(record.getValue(), targetClass);
//            ret.put(new String(record.getKey()), obj);
//        }
//        return ret;
//    }
//
//
//    /**
//     * 根据key取出对应下标为index处的对象
//     *
//     * @param key
//     * @param index
//     * @param targetClass
//     * @param <T>
//     * @return
//     */
//    public <T> T lIndex(String key, int index, Class<T> targetClass) {
//        byte[] value = redisTemplate.execute((RedisCallback<byte[]>) connection -> connection.lIndex(key.getBytes(), index));
//        return SerializeUtil.deserialize(value, targetClass);
//    }
//
//
//    /**
//     * 根据 key取出下标从start至end的数据
//     *
//     * @param key
//     * @param start
//     * @param end
//     * @param targetClass
//     * @param <T>
//     * @return
//     */
//    public <T> List<T> lRange(String key, int start, int end, Class<T> targetClass) {
//        List<byte[]> value = redisTemplate
//                .execute((RedisCallback<List<byte[]>>) connection -> connection.lRange(key.getBytes(), start, end));
//        return value.stream().map(record -> SerializeUtil.deserialize(record, targetClass)).collect(Collectors.toList());
//    }
//
//
//    /**
//     * 在list左边push一个对象
//     *
//     * @param key
//     * @param obj
//     * @param <T>
//     */
//    public <T> void lPushObject(String key, T obj) {
//        final byte[] value = SerializeUtil.serialize(obj);
//        redisTemplate.execute((RedisCallback<Long>) connection -> connection.lPush(key.getBytes(), value));
//    }
//
//
//    /**
//     * 在list左边push多个对象
//     *
//     * @param key
//     * @param objList
//     * @param <T>
//     */
//    public <T> void lPushObject(String key, List<T> objList) {
//        List<byte[]> byteFields = objList.stream().map(SerializeUtil::serialize).collect(Collectors.toList());
//        byte[][] values = new byte[byteFields.size()][];
//        redisTemplate.execute((RedisCallback<Long>) connection -> connection.lPush(key.getBytes(), values));
//    }
//
//
//    //字符串操作
//
//    /**
//     * set字符串
//     *
//     * @param key
//     * @param value
//     */
//    public void set(String key, Object value) {
//        redisTemplate.opsForValue().set(key, value);
//
//
//    }
//
//
//    /**
//     * set字符串 并加上失效时间  以豪秒为单位
//     *
//     * @param key
//     * @param value
//     * @param timeout 失效时间 单位为豪秒
//     */
//    public void set(String key, Object value, Integer timeout) {
//        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
//    }
//
//    /**
//     * 当key不存在时 设置key value
//     *
//     * @param key
//     * @param value
//     */
//    public void setNx(String key, Object value) {
//        redisTemplate.opsForValue().setIfAbsent(key, value);
//    }
//
//
//    /**
//     * 根据key获取value
//     *
//     * @param key
//     * @return
//     */
//    public Object get(String key) {
//        return redisTemplate.opsForValue().get(key);
//    }
//
//    /**
//     * 获取所有(一个或多个)给定 key 的值
//     *
//     * @param keys
//     * @return
//     */
//    public List<Object> mGet(String... keys) {
//        return redisTemplate.opsForValue().multiGet(CollectionUtils.arrayToList(keys));
//    }
//
//    /**
//     * 同时设置多个key，value
//     *
//     * @param map
//     */
//    public void mSet(Map<String, Object> map) {
//        redisTemplate.opsForValue().multiSet(map);
//    }
//
//    /**
//     * 所有给定的key都不存在时，设置多个key，value
//     *
//     * @param map
//     */
//    public void mSetNx(Map<String, Object> map) {
//        redisTemplate.opsForValue().multiSetIfAbsent(map);
//    }
//
//
//    /**
//     * 通过key返回value的长度
//     *
//     * @param key
//     * @return
//     */
//    public int strLength(String key) {
//        return redisTemplate.opsForValue().get(key).toString().length();
//    }
//
//    /**
//     * 当前key存在时  向这个key对应value的默认追加上此字符
//     *
//     * @param key
//     * @param value 要追加的字符
//     */
//    public void appendStr(String key, String value) {
//        redisTemplate.opsForValue().append(key, value);
//    }
//
//
//    //Hash操作
//
//    /**
//     * 删除key中指定的hashkey的值  相当于一个key中存储着map值 这个方法就是删除这个key中的map里面的一个或多个key
//     *
//     * @param key
//     * @param hashKeys
//     */
//    public void hdel(String key, String... hashKeys) {
//        redisTemplate.opsForHash().delete(key, hashKeys);
//
//    }
//
//    //根据redis的key和map中的key获取数据
//    public Object getMapObject(String key, Object hashKey) {
//        return redisTemplate.opsForHash().get(key, hashKey);
//    }
//
//
//    /**
//     * 添加一个hash值
//     *
//     * @param key
//     * @param hashKey 相当于 map中的key
//     * @param value   存储的值 相当于map中的value
//     */
//    public void put(String key, String hashKey, Object value) {
//        redisTemplate.opsForHash().put(key, hashKey, value);
//    }
//
//
//    /**
//     * 添加一个map
//     *
//     * @param key
//     * @param map
//     */
//    public void putAll(String key, Map<Object, Object> map) {
//        redisTemplate.opsForHash().putAll(key, map);
//    }
//
//    /**
//     * 获取redis中的map
//     *
//     * @param key
//     * @return
//     */
//    public Map<Object, Object> getRedisMap(String key) {
//        return redisTemplate.opsForHash().entries(key);
//    }
//
//    /**
//     * 返回这个key里面所有hashKeys的值
//     *
//     * @param key
//     * @param hashKeys
//     * @return
//     */
//    public List<Object> multiGet(String key, String... hashKeys) {
//        return redisTemplate.opsForHash().multiGet(key, CollectionUtils.arrayToList(hashKeys));
//    }
//
//
//    /**
//     * 返回这个key中的所有value
//     *
//     * @param key
//     * @return
//     */
//    public List<Object> getValues(String key) {
//        return redisTemplate.opsForHash().values(key);
//    }
//
//    /**
//     * 判断key中的hashKey是否存在
//     *
//     * @param key
//     * @param hashKey
//     */
//    public Boolean hashMapKey(String key, String hashKey) {
//        return redisTemplate.opsForHash().hasKey(key, hashKey);
//    }
//
//
//    //list操作
//
//    /**
//     * 从左入栈
//     *
//     * @param key
//     * @param value
//     */
//    public void lpush(String key, Object value) {
//        redisTemplate.opsForList().leftPush(key, value);
//    }
//
//    /**
//     * 从右入栈
//     *
//     * @param key
//     * @param value
//     */
//    public void rpush(String key, Object value) {
//        redisTemplate.opsForList().rightPush(key, value);
//    }
//
//
//    /**
//     * 从左出栈
//     *
//     * @param key
//     * @return
//     */
//    public Object lPop(String key) {
//        return redisTemplate.opsForList().leftPop(key);
//    }
//
//    /**
//     * 从右出栈
//     *
//     * @param key
//     * @return
//     */
//    public Object rPop(String key) {
//        return redisTemplate.opsForList().rightPop(key);
//    }
//
//
//    /**
//     * 获取该key index处的元素
//     *
//     * @param key
//     * @param index
//     * @return
//     */
//    public Object getKeyIndex(String key, int index) {
//        return redisTemplate.opsForList().index(key, index);
//    }
//
//    /**
//     * 获取列表的长度
//     *
//     * @param key
//     * @return
//     */
//    public Long getLength(String key) {
//        return redisTemplate.opsForList().size(key);
//    }
//
//    /**
//     * 获取key中下标从start到end处的值
//     *
//     * @param key
//     * @param start 开始下标
//     * @param end   结束下标
//     * @return
//     */
//    public List<Object> range(String key, int start, int end) {
//        return redisTemplate.opsForList().range(key, start, end);
//    }
//
//
//    //set操作  无序集合
//
//    /**
//     * 向集合中添加
//     *
//     * @param key
//     * @param values
//     */
//    public void addSet(String key, Object... values) {
//        redisTemplate.opsForSet().add(key, values);
//    }
//
//    /**
//     * 移除并取出第一个元素
//     *
//     * @param key
//     * @return
//     */
//    public Object getSet(String key) {
//        return redisTemplate.opsForSet().pop(key);
//    }
//
//    /**
//     * 返回集合中所有的元素
//     *
//     * @param key
//     * @return
//     */
//    public Set<Object> getSets(String key) {
//        return redisTemplate.opsForSet().members(key);
//    }
//
//    /**
//     * 返回指定数量的元素(随机)
//     *
//     * @param key
//     * @param count
//     * @return
//     */
//    public List<Object> randomMembers(String key, int count) {
//        return redisTemplate.opsForSet().randomMembers(key, count);
//    }
//
//    /**
//     * 返回集合中的长度
//     *
//     * @param key
//     * @return
//     */
//    public Long getSetsNum(String key) {
//        return redisTemplate.opsForSet().size(key);
//    }
//
//    /**
//     * 返回给定集合的差集（返回 key在otherKeys不存在的元素）
//     *
//     * @param key       主集合
//     * @param otherKeys 其他集合
//     * @return
//     */
//    public Set<Object> difference(String key, String... otherKeys) {
//        return redisTemplate.opsForSet().difference(key, CollectionUtils.arrayToList(otherKeys));
//    }
//
//    /**
//     * 返回给定集合的交集（返回 key与otherKeys中共同存在的元素）
//     *
//     * @param key
//     * @param otherKeys
//     * @return
//     */
//    public Set<Object> intersect(String key, String... otherKeys) {
//        return redisTemplate.opsForSet().intersect(key, CollectionUtils.arrayToList(otherKeys));
//    }
//
//    /**
//     * 返回给定集合的并集（key和otherKeys加起来的所有元素，共同拥有的元素只返回一个）
//     *
//     * @param key
//     * @param otherKeys
//     * @return
//     */
//    public Set<Object> union(String key, String... otherKeys) {
//        return redisTemplate.opsForSet().union(key, CollectionUtils.arrayToList(otherKeys));
//    }
//
//    /**
//     * 返回集合中的所有元素
//     *
//     * @param key
//     * @return
//     */
//    public Set<Object> members(String key) {
//        return redisTemplate.opsForSet().members(key);
//    }
//
//    /**
//     * 迭代集合中的元素
//     *
//     * @param key
//     * @return
//     */
//    public Cursor<Object> scan(String key) {
//        return redisTemplate.opsForSet().scan(key, ScanOptions.NONE);
//    }
//
//
//    //zSet操作 有序集合
//
//    /**
//     * 添加数据
//     * <p>
//     * 添加方式：
//     * 1.创建一个set集合
//     * Set<ZSetOperations.TypedTuple<Object>> sets=new HashSet<>();
//     * 2.创建一个有序集合
//     * ZSetOperations.TypedTuple<Object> objectTypedTuple1 = new DefaultTypedTuple<Object>(value,排序的数值，越小越在前);
//     * 4.放入set集合
//     * sets.add(objectTypedTuple1);
//     * 5.放入缓存
//     * reidsImpl.Zadd("zSet", list);
//     *
//     * @param key
//     * @param tuples
//     */
//    public void Zadd(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
//        redisTemplate.opsForZSet().add(key, tuples);
//    }
//
//    /**
//     * 获取有序集合的成员数
//     *
//     * @param key
//     * @return
//     */
//    public Long Zcard(String key) {
//        return redisTemplate.opsForZSet().zCard(key);
//    }
//
//    /**
//     * 计算在有序集合中指定区间分数的成员数
//     *
//     * @param key
//     * @param min 最小排序分数
//     * @param max 最大排序分数
//     * @return
//     */
//    public Long Zcount(String key, Double min, Double max) {
//        return redisTemplate.opsForZSet().count(key, min, max);
//    }
//
//    /**
//     * 获取有序集合下标区间 start 至 end 的成员  分数值从小到大排列
//     *
//     * @param key
//     * @param start
//     * @param end
//     */
//    public Set<Object> Zrange(String key, int start, int end) {
//        return redisTemplate.opsForZSet().range(key, start, end);
//    }
//
//    /**
//     * 获取有序集合下标区间 start 至 end 的成员  分数值从大到小排列
//     *
//     * @param key
//     * @param start
//     * @param end
//     */
//    public Set<Object> reverseRange(String key, int start, int end) {
//        return redisTemplate.opsForZSet().reverseRange(key, start, end);
//    }
//
//    /**
//     * 返回 分数在min至max之间的数据 按分数值递减(从大到小)的次序排列。
//     *
//     * @param key
//     * @param min
//     * @param max
//     * @return
//     */
//    public Set<Object> reverseRange(String key, Double min, Double max) {
//        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
//    }
//
//    /**
//     * 返回指定成员的下标
//     *
//     * @param key
//     * @param value
//     * @return
//     */
//    public Long Zrank(String key, Object value) {
//        return redisTemplate.opsForZSet().rank(key, value);
//    }
//
//    /**
//     * 删除key的指定元素
//     *
//     * @param key
//     * @param values
//     * @return
//     */
//    public Long ZremoveValue(String key, Object values) {
//        return redisTemplate.opsForZSet().remove(key, values);
//    }
//
//    /**
//     * 移除下标从start至end的元素
//     *
//     * @param key
//     * @param start
//     * @param end
//     * @return
//     */
//    public Long ZremoveRange(String key, int start, int end) {
//        return redisTemplate.opsForZSet().removeRange(key, start, end);
//    }
//
//    /**
//     * 移除分数从min至max的元素
//     *
//     * @param key
//     * @param min
//     * @param max
//     * @return
//     */
//    public Long ZremoveRangeByScore(String key, Double min, Double max) {
//        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
//    }
//}
