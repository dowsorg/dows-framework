package org.dows.framework.websocket;

import org.dows.framework.websocket.resolver.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 开启WebSocket
 */
@ConditionalOnMissingBean({WebSocketEndpointExporter.class})
@AutoConfiguration
public class NettyWebSocketSelector {

    public List<MethodArgumentResolver> getDefaultResolvers(AbstractBeanFactory beanFactory) {
        List<MethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new SessionMethodArgumentResolver());
        resolvers.add(new HttpHeadersMethodArgumentResolver());
        resolvers.add(new TextMethodArgumentResolver());
        resolvers.add(new ThrowableMethodArgumentResolver());
        resolvers.add(new ByteMethodArgumentResolver());
        resolvers.add(new RequestParamMapMethodArgumentResolver());
        resolvers.add(new RequestParamMethodArgumentResolver(beanFactory));
        resolvers.add(new PathVariableMapMethodArgumentResolver());
        resolvers.add(new PathVariableMethodArgumentResolver(beanFactory));
        resolvers.add(new EventMethodArgumentResolver(beanFactory));
        return resolvers;
    }


    @Bean
    public WebSocketEndpointExporter webSocketEndpointExporter(ResourceLoader resourceLoader, BeanFactory beanFactory) {
        if (!(beanFactory instanceof AbstractBeanFactory)) {
            throw new IllegalArgumentException(
                    "AutowiredAnnotationBeanPostProcessor requires a AbstractBeanFactory: " + beanFactory);
        }
        return new WebSocketEndpointExporter(resourceLoader,getDefaultResolvers((AbstractBeanFactory) beanFactory));
    }
}
