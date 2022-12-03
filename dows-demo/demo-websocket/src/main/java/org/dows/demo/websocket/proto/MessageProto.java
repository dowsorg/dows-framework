package org.dows.demo.websocket.proto;

import cn.hutool.json.JSONUtil;
import org.dows.demo.websocket.util.DateTimeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息协议
 * | head | body
 * 4
 */
public class MessageProto {
    public static final int PING_PROTO = 1 << 8 | 220; //ping消息
    public static final int PONG_PROTO = 2 << 8 | 220; //pong消息
    public static final int SYST_PROTO = 3 << 8 | 220; //系统消息
    public static final int EROR_PROTO = 4 << 8 | 220; //错误消息
    public static final int AUTH_PROTO = 5 << 8 | 220; //认证消息
    public static final int MESS_PROTO = 6 << 8 | 220; //普通消息

    private int version = 1;
    private int uri;
    private String body;
    private Map<String, Object> extend = new HashMap<>();

    public MessageProto(int head, String body) {
        this.uri = head;
        this.body = body;
    }

    public static String buildPingProto() {
        return buildProto(PING_PROTO, null);
    }

    public static String buildPongProto() {
        MessageProto chatProto = new MessageProto(PONG_PROTO, null);
        String date = DateTimeUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss");
        chatProto.extend.put("code", "30004");
        chatProto.extend.put("date", date);
        return JSONUtil.toJsonStr(chatProto);
    }

    public static String buildSystProto(int code, Object mess) {
        MessageProto chatProto = new MessageProto(SYST_PROTO, null);
        chatProto.extend.put("code", code);
        chatProto.extend.put("mess", mess);
        return JSONUtil.toJsonStr(chatProto);
    }

    public static String buildAuthProto(boolean isSuccess) {
        MessageProto chatProto = new MessageProto(AUTH_PROTO, null);
        chatProto.extend.put("isSuccess", isSuccess);
        return JSONUtil.toJsonStr(chatProto);
    }

    public static String buildErorProto(int code, String mess) {
        MessageProto chatProto = new MessageProto(EROR_PROTO, null);
        chatProto.extend.put("code", code);
        chatProto.extend.put("mess", mess);
        return JSONUtil.toJsonStr(chatProto);
    }

    public static String buildMessProto(int uid, String nick, String mess) {
        MessageProto chatProto = new MessageProto(MESS_PROTO, mess);
        chatProto.extend.put("uid", uid);
        chatProto.extend.put("nick", nick);
        chatProto.extend.put("time", DateTimeUtil.getCurrentTime());
        return JSONUtil.toJsonStr(chatProto);
    }

    public static String buildProto(int head, String body) {
        MessageProto chatProto = new MessageProto(head, body);
        return JSONUtil.toJsonStr(chatProto);
    }

    public int getUri() {
        return uri;
    }

    public void setUri(int uri) {
        this.uri = uri;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<String, Object> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }
}
