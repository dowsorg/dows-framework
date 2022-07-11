package org.dows.framework.rest;


import org.dows.framework.rest.config.TargetConfig;

/**
 * 远程服务客户端工厂接口
 */
public interface RestClientFactory {
    /**
     * 根据接口类对象获取接口代理对象
     *
     * @param clazz
     * @return
     */
    <T> T getClient(Class<T> clazz);


    /**
     * 根据接口类对象，请求URL 获取接口代理对象
     *
     * @param clazz
     * @param targetConfig
     * @param <T>
     * @return
     */
    <T> T getClient(Class<T> clazz, TargetConfig targetConfig);

    /**
     * 清空缓存
     */
    void clear();
}
