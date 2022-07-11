package org.dows.framework.rest.parser;

import org.dows.framework.rest.RestMethodPath;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/29/2022
 */
public interface ResourceNameParser {

    String parseResourceName(Method method, Environment environment);

    RestMethodPath parseHttpMethodPath(Method method);
}
