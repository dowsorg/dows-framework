package org.dows.framework.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * @Description: WebSocket path matcher
 */
public interface WebSocketPathMatcher {

    String getPattern();

    boolean matchAndExtract(QueryStringDecoder decoder, Channel channel);
}
