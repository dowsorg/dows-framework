package org.dows.framework.rest;

import org.dows.framework.rest.interceptor.RestInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/2/2022
 */
public class InterceptorFactoryBean implements FactoryBean<RestInterceptor>, ApplicationContextAware {
    private RestInterceptor restInterceptor;
    private ApplicationContext applicationContext;

    @Override
    public RestInterceptor getObject() throws Exception {
        return null;
    }


    @Override
    public Class<?> getObjectType() {
        return restInterceptor.getClass();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
