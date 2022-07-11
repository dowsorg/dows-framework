package org.dows.framework.rest;

import org.dows.framework.rest.degrade.FallbackFactory;
import org.dows.framework.rest.exception.RetrofitBlockException;
import org.dows.framework.rest.property.DegradeProperty;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public final class RestInvocationHandler implements InvocationHandler {

    private final Object source;

    private final DegradeProperty degradeProperty;

    private final Object fallback;

    private final FallbackFactory<?> fallbackFactory;


    public RestInvocationHandler(Object source, Object fallback, FallbackFactory<?> fallbackFactory, DegradeProperty degradeProperty) {
        this.source = source;
        this.degradeProperty = degradeProperty;
        this.fallback = fallback;
        this.fallbackFactory = fallbackFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(source, args);
        } catch (Throwable e) {
            Throwable cause = e.getCause();
            // 熔断逻辑
            if (cause instanceof RetrofitBlockException && degradeProperty.isEnable()) {
                Object fallbackObject = getFallbackObject(cause);
                if (fallbackObject != null) {
                    return method.invoke(fallbackObject, args);
                }
            }
            throw cause;
        }
    }

    private Object getFallbackObject(Throwable cause) {
        if (fallback != null) {
            return fallback;
        }

        if (fallbackFactory != null) {
            return fallbackFactory.create(cause);
        }
        return null;
    }
}
