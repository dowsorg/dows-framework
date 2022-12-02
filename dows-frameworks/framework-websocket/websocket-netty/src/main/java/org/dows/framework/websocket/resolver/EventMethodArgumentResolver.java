package org.dows.framework.websocket.resolver;

import io.netty.channel.Channel;
import org.dows.framework.websocket.MethodArgumentResolver;
import org.dows.framework.websocket.OnEvent;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.core.MethodParameter;

public class EventMethodArgumentResolver implements MethodArgumentResolver {

    private final AbstractBeanFactory beanFactory;

    public EventMethodArgumentResolver(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(OnEvent.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        if (object==null) {
            return null;
        }
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        return typeConverter.convertIfNecessary(object, parameter.getParameterType());
    }
}
