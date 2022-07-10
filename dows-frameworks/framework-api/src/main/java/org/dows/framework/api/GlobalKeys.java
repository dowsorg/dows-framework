package org.dows.framework.api;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/10/2022
 */
public interface GlobalKeys {

    String USER_NO = "UserNo";
    String ACCOUNT_NO = "AccountNo";
    String DEVICE_ID = "DeviceId";
    String IP = "Ip";
    String OS = "Os";

    String TIMESTAMP = "Timestamp";
    String SIGN = "Sign";
    String TOKEN = "Token";
    String TOKEN_NAME = "Authorization";
    // token 前缀
    String TOKEN_PREFIX = "Bearer ";

    class App{
        /**
         * 应用ID
         */
        String APP_ID = "App-Id";
        String APPKEY = "Appkey";
        /**
         * app版本号
         */
        String APP_VERSION = "App-Version";

        String SECRT_ID = "SecrtId";
        String SECRET_KEY = "SecretKey";
    }

    class HttpHeader {
        /**
         * 用户的登录token
         */
        String X_TOKEN = "X-Token";

        /**
         * api的版本号
         */
        String API_VERSION = "Api-Version";
        /**
         * 调用来源(IOS、ANDROID、PC、WECHAT、WEB)
         */
        String CALL_SOURCE = "Call-Source";

        /**
         * API的返回格式
         */
        String API_STYLE = "Api-Style";
        /**
         * 租户号
         */
        String TENANT_CODE = "Tenant-Code";
    }
}
