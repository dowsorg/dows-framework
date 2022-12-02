package org.dows.framework.websocket.annotation;

import org.dows.framework.websocket.NettyWebSocketSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 开启WebSocket
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(NettyWebSocketSelector.class)
public @interface EnableWebSocket {

    String[] scanBasePackages() default {};

}
