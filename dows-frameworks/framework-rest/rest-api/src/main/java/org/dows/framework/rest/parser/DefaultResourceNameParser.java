package org.dows.framework.rest.parser;

import org.dows.framework.rest.RestMethodPath;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/1/2022
 */
public class DefaultResourceNameParser extends RestResourceNameParser {

    @Override
    public RestMethodPath parseHttpMethodPath(Method method) {

        if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping get = method.getAnnotation(GetMapping.class);
            return new RestMethodPath("GET", get.value()[0]);
        }

        if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            return new RestMethodPath("POST", post.value()[0]);
        }

        if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping put = method.getAnnotation(PutMapping.class);
            return new RestMethodPath("PUT", put.value()[0]);
        }

        if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping delete = method.getAnnotation(DeleteMapping.class);
            return new RestMethodPath("DELETE", delete.value()[0]);
        }

        if (method.isAnnotationPresent(PatchMapping.class)) {
            PatchMapping patch = method.getAnnotation(PatchMapping.class);
            return new RestMethodPath("PATCH", patch.value()[0]);
        }
        return null;
    }
}
