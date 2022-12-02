//package org.dows.framework.websocket;
//
//import org.dows.framework.websocket.resolver.*;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.BeanFactoryAware;
//import org.springframework.beans.factory.support.AbstractBeanFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//public class WebSocketResolverConfigure implements BeanFactoryAware {
//
//    private AbstractBeanFactory beanFactory;
//
//    /**
//     * Add netty parser
//     *
//     * @return List<MethodArgumentResolver>
//     */
//    @Bean
//    public List<MethodArgumentResolver> getDefaultResolvers() {
//        List<MethodArgumentResolver> resolvers = new ArrayList<>();
//        resolvers.add(new SessionMethodArgumentResolver());
//        resolvers.add(new HttpHeadersMethodArgumentResolver());
//        resolvers.add(new TextMethodArgumentResolver());
//        resolvers.add(new ThrowableMethodArgumentResolver());
//        resolvers.add(new ByteMethodArgumentResolver());
//        resolvers.add(new RequestParamMapMethodArgumentResolver());
//        resolvers.add(new RequestParamMethodArgumentResolver(beanFactory));
//        resolvers.add(new PathVariableMapMethodArgumentResolver());
//        resolvers.add(new PathVariableMethodArgumentResolver(beanFactory));
//        resolvers.add(new EventMethodArgumentResolver(beanFactory));
//        return resolvers;
//    }
//
//    @Override
//    public void setBeanFactory(BeanFactory beanFactory) {
//        if (!(beanFactory instanceof AbstractBeanFactory)) {
//            throw new IllegalArgumentException(
//                    "AutowiredAnnotationBeanPostProcessor requires a AbstractBeanFactory: " + beanFactory);
//        }
//        this.beanFactory = (AbstractBeanFactory) beanFactory;
//    }
//}
