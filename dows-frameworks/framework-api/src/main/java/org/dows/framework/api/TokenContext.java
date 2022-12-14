package org.dows.framework.api;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/13/2022
 */
public class TokenContext {

    private static final String KEY_TOKEN = "token";
    private static final String KEY_PAYLOAD = "payload";

    private static final ThreadLocal<Map<Object, Object>> context = new ThreadLocal<>();

    private TokenContext() {
    }

    public static void set(Object key, Object value) {
        Map<Object, Object> locals = context.get();
        if (locals == null) {
            locals = new HashMap<>();
            context.set(locals);
        }
        locals.put(key, value);
    }

    public static Object get(Object key) {
        Map<Object, Object> locals = context.get();
        if (locals != null) {
            return locals.get(key);
        }
        return null;
    }

    public static void remove(Object key) {
        Map<Object, Object> locals = context.get();
        if (locals != null) {
            locals.remove(key);
            if (locals.isEmpty()) {
                context.remove();
            }
        }
    }

    public static void removeAll() {
        Map<Object, Object> locals = context.get();
        if (locals != null) {
            locals.clear();
        }
        context.remove();
    }

    public static String getToken() {
        return (String) get(KEY_TOKEN);
    }

    public static void setToken(String token) {
        set(KEY_TOKEN, token);
    }

    public static Object getPayload() {
        return get(KEY_PAYLOAD);
    }

    public static void setPayload(Object payload) {
        set(KEY_PAYLOAD, payload);
    }
}
