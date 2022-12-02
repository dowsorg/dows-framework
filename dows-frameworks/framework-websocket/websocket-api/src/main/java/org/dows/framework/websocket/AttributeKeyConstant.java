package org.dows.framework.websocket;

import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Map;

public interface AttributeKeyConstant {
    AttributeKey<Object> WEB_SOCKET_KEY = AttributeKey.valueOf("WEBSOCKET_IMPLEMENT");

    AttributeKey<NettySession> SESSION_KEY = AttributeKey.valueOf("WEBSOCKET_SESSION");

    AttributeKey<String> PATH_KEY = AttributeKey.valueOf("WEBSOCKET_PATH");

    AttributeKey<Map<String, String>> URI_TEMPLATE = AttributeKey.valueOf("WEBSOCKET_URI_TEMPLATE");

    AttributeKey<Map<String, List<String>>> REQUEST_PARAM = AttributeKey.valueOf("WEBSOCKET_REQUEST_PARAM");
}
