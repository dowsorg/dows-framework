package org.dows.framework.rest.parser;

import org.dows.framework.rest.RestMethodPath;
import org.dows.framework.rest.annotation.RestClient;
import org.dows.framework.rest.util.RestClientUtil;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RestResourceNameParser implements ResourceNameParser {
    private static final String PREFIX = "HTTP_OUT";

    private static final Map<Method, String> RESOURCE_NAME_CACHE = new ConcurrentHashMap<>(128);

    @Override
    public String parseResourceName(Method method, Environment environment) {

        String resourceName = RESOURCE_NAME_CACHE.get(method);
        if (resourceName != null) {
            return resourceName;
        }
        Class<?> declaringClass = method.getDeclaringClass();
        RestClient restClient = declaringClass.getAnnotation(RestClient.class);
        String baseUrl = restClient.baseUrl();
        baseUrl = RestClientUtil.convertBaseUrl(restClient, baseUrl, environment);
        RestMethodPath restMethodPath = parseHttpMethodPath(method);
        resourceName = defineResourceName(baseUrl, restMethodPath);
        RESOURCE_NAME_CACHE.put(method, resourceName);
        return resourceName;
    }

    /**
     * define resource name.
     *
     * @param baseUrl        baseUrl
     * @param restMethodPath httpMethodPath
     * @return resource name.
     */
    protected String defineResourceName(String baseUrl, RestMethodPath restMethodPath) {
        return String.format("%s:%s:%s", PREFIX, restMethodPath.getMethod(), baseUrl + restMethodPath.getPath());
    }

}
