package org.dows.framework.websocket.resolver;

import io.netty.channel.Channel;
import org.dows.framework.websocket.MethodArgumentResolver;
import org.dows.framework.websocket.NettySession;
import org.springframework.core.MethodParameter;

import static org.dows.framework.websocket.AttributeKeyConstant.SESSION_KEY;

public class SessionMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return NettySession.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        return channel.attr(SESSION_KEY).get();
    }
}
