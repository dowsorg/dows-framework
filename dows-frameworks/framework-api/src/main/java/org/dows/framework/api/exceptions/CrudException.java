package org.dows.framework.api.exceptions;

import org.dows.framework.api.StatusCode;
import org.springframework.util.StringUtils;

/**
 *
 */
public class CrudException extends BaseException {

    public CrudException(StatusCode responseCode) {
        super(responseCode);
    }

    public CrudException(Class clazz, String field, String val) {
        super(CrudException.generateMessage(clazz.getSimpleName(), field, val));
    }

    private static String generateMessage(String entity, String field, String val) {
        return StringUtils.capitalize(entity)
                + " with " + field + " " + val + " existed";
    }
}
