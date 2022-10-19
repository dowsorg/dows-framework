package org.dows.sequence.core;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class ServiceLoaderFactory {

    private static final Map<Class<?>, Collection<Object>> SERVICE_MAP = new ConcurrentHashMap<>();

    /**
     * 初始化某些类
     *
     * @param interfaceClasses 多个待扩展的spi类型接口
     */
    public void init(Class<?>... interfaceClasses) {
        Arrays.stream(interfaceClasses).forEach(interfaceClass -> {
            for (Object instance : ServiceLoader.load(interfaceClass)) {
                SERVICE_MAP.compute(interfaceClass, (k, v) -> {
                    if (null == v) {
                        Collection<Object> valueCollection = new LinkedList<>();
                        valueCollection.add(instance);
                        return valueCollection;
                    } else {
                        v.add(instance);
                        return v;
                    }
                });
            }
        });
    }

    /**
     * 获取接口类对应的所有spi对象集合
     *
     * @param interfaceClass 目标接口类
     * @param <T>            类型
     * @return spi对象集合
     */
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getChildObject(Class<T> interfaceClass) {
        return (Collection<T>) SERVICE_MAP.get(interfaceClass);
    }

    /**
     * 获取指定类的对象
     *
     * @param tClass 目标类
     * @param <T>    类型
     * @return 目标类的实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getTarget(Class<T> tClass) {
        return (T) SERVICE_MAP.entrySet().stream().flatMap(e -> {
            if (e.getKey().isAssignableFrom(tClass)) {
                return e.getValue().stream();
            }
            return null;
        }).filter(Objects::nonNull).filter(o -> o.getClass().equals(tClass)).findFirst().orElse(null);
    }
}
