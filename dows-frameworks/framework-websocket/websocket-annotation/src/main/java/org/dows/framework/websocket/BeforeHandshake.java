package org.dows.framework.websocket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 当有新的连接进入之前，对该方法进行回调 注入参数的类型:Session、HttpHeaders...
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeHandshake {
}
