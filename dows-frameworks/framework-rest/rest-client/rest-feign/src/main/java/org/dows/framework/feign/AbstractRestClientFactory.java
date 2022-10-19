package org.dows.framework.feign;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.RemotingException;
import org.dows.framework.rest.RestClientFactory;
import org.dows.framework.rest.annotation.RestClient;
import org.dows.framework.rest.config.RestSetting;
import org.dows.framework.rest.config.TargetConfig;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程服务客户端工厂接口抽象实现
 */
@Slf4j
@Data
public abstract class AbstractRestClientFactory implements RestClientFactory {
    /**
     * 对象缓存：url->接口代理对象
     */
    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();
    /**
     * 远程配置
     */
    protected RestSetting restSetting;

    public AbstractRestClientFactory(RestSetting restSetting) {
        this.restSetting = restSetting;
    }

    /**
     * 清空缓存
     */
    @Override
    public void clear() {
        CACHE.clear();
    }

    /**
     * 根据接口类对象获取接口代理对象
     *
     * @param clazz:接口类对象
     * @param <T>
     * @return
     */
    @Override
    public <T> T getClient(Class<T> clazz) {
        return getClient(clazz, null);
    }


    /**
     * 根据接口类对象，请求URL 获取接口代理对象
     *
     * @param clazz
     * @param targetConfig
     * @param <T>
     * @return
     */
    @Override
    public <T> T getClient(Class<T> clazz, TargetConfig targetConfig) {
        return buildClient(clazz, targetConfig);
    }

    /**
     * 执行构建
     *
     * @param clazz
     * @param targetConfig
     * @param <T>
     * @return
     */
    protected abstract <T> T doBuildClient(Class<T> clazz, TargetConfig targetConfig);

    /**
     * 初始化
     */
    protected void init() {
        Reflections reflections = new Reflections(restSetting.getScanPackage());
        Set<Class<?>> set = reflections.getTypesAnnotatedWith(RestClient.class);
        for (Class<?> c : set) {
            RestClient annotation = c.getAnnotation(RestClient.class);
            if (restSetting.getServiceUrlMap().containsKey(annotation.baseUrl())) {
                log.info("构建远程服务:{},KEY->{},URL->{}", c, annotation.baseUrl(), restSetting.getServiceUrlMap().get(annotation.baseUrl()));
                buildClient(c, TargetConfig
                        .builder()
                        .connectionTimeout(annotation.connectionTimeout())
                        .socketTimeout(annotation.socketTimeout())
//                        .retryCount(annotation.retryCount())
//                        .period(annotation.period())
                        .url(restSetting.getServiceUrlMap().get(annotation.baseUrl()))
                        .build());
            } else {
                log.warn("未配置远程服务地址参数:{},KEY->{}", c, annotation.baseUrl());
            }
        }
    }


    /**
     * 构建接口代理对象
     *
     * @param clazz
     * @param targetConfig
     * @param <T>
     * @return
     */
    private <T> T buildClient(Class<T> clazz, TargetConfig targetConfig) {
        String key = clazz.getName();
        Object o = CACHE.get(key);
        if (o == null) {
            if (targetConfig == null) {
                throw new RemotingException("目标对象配置不能为空");
            }
            T t = doBuildClient(clazz, targetConfig);
            if (t != null) {
                CACHE.put(key, t);
                return t;
            }
        } else if (clazz.isInstance(o)) {
            return (T) o;
        }
        return doBuildClient(clazz, targetConfig);
    }
}
