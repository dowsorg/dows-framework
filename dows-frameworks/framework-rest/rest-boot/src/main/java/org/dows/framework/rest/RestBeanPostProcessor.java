package org.dows.framework.rest;

import org.dows.framework.rest.property.RestProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 动态注册配置对应的bean
 *
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/10/2022
 */
public class RestBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    ApplicationContext applicationContext;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        RestProperties restProperties = applicationContext.getBean(RestProperties.class);
        /*for (Class<? extends RestCryptor> apiCrypto : restProperties.getSecurity().getApiCryptos()) {
            Assert.isNull(applicationContext.getBean(apiCrypto));
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(apiCrypto);
            //beanDefinitionBuilder.addConstructorArgReference("");
            //beanDefinitionBuilder.addPropertyValue("", null);
            registry.registerBeanDefinition(StrUtil.lowerFirst(apiCrypto.getSimpleName()),
                    beanDefinitionBuilder.getBeanDefinition());
        }*/
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
