package org.dows.framework.rest.annotation;

import org.dows.framework.rest.interceptor.RestInterceptor;

import java.lang.annotation.*;


/**
 * 自动将注解上的参数值赋值到handleInterceptor实例上
 *
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@InterceptMark
@Repeatable(Intercepts.class)
public @interface Intercept {
    /**
     * 拦截器匹配路径pattern
     *
     * @return 拦截器匹配路径
     */
    String[] include() default {"/**"};

    /**
     * 拦截器排除匹配，排除指定路径拦截
     *
     * @return 排除指定路径拦截
     */
    String[] exclude() default {};

    /**
     * Interceptor handler
     * 优先从spring容器获取对应的Bean，如果获取不到，则使用反射创建一个！
     * First obtain the corresponding Bean from the spring container, if not, use reflection to create one!
     *
     * @return 拦截器处理器 Interceptor handler
     */
    Class<? extends RestInterceptor> handler();
}
