package org.dows.demo.websocket.proto;


public class MessageCode {

    /**
     * ping消息
     */
    public static final int PING_CODE = 10001;
    /**
     * pong消息
     */
    public static final int PONG_CODE = 10002;
    /**
     * 登录授权消息
     */
    public static final int AUTH_CODE = 10003;
    /**
     * 普通消息
     */
    public static final int MESS_CODE = 10004;
    /**
     * 注册设备、订阅kafka消息
     */
    public static final int REGIS_CODE = 10005;

    /**
     * 系统消息类型
     */
    public static final int SYS_USER_COUNT = 20001; // 在线用户数
    public static final int SYS_AUTH_STATE = 20002; // 认证结果
    public static final int SYS_OTHER_INFO = 20003; // 系统消息

}
