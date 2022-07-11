package org.dows.framewrok.retrofit.core;

import org.dows.framework.rest.RestMethodPath;
import org.dows.framework.rest.parser.ResourceNameParser;
import org.dows.framework.rest.parser.RestResourceNameParser;
import retrofit2.http.*;

import java.lang.reflect.Method;

public class RetrofitResourceNameParser extends RestResourceNameParser implements ResourceNameParser {

    public RestMethodPath parseHttpMethodPath(Method method) {

        if (method.isAnnotationPresent(HTTP.class)) {
            HTTP http = method.getAnnotation(HTTP.class);
            return new RestMethodPath(http.method(), http.path());
        }

        if (method.isAnnotationPresent(GET.class)) {
            GET get = method.getAnnotation(GET.class);
            return new RestMethodPath("GET", get.value());
        }

        if (method.isAnnotationPresent(POST.class)) {
            POST post = method.getAnnotation(POST.class);
            return new RestMethodPath("POST", post.value());
        }

        if (method.isAnnotationPresent(PUT.class)) {
            PUT put = method.getAnnotation(PUT.class);
            return new RestMethodPath("PUT", put.value());
        }

        if (method.isAnnotationPresent(DELETE.class)) {
            DELETE delete = method.getAnnotation(DELETE.class);
            return new RestMethodPath("DELETE", delete.value());
        }

        if (method.isAnnotationPresent(HEAD.class)) {
            HEAD head = method.getAnnotation(HEAD.class);
            return new RestMethodPath("HEAD", head.value());
        }

        if (method.isAnnotationPresent(PATCH.class)) {
            PATCH patch = method.getAnnotation(PATCH.class);
            return new RestMethodPath("PATCH", patch.value());
        }

        return null;
    }

}
