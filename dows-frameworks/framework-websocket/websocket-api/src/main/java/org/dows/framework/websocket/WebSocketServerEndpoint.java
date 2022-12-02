package org.dows.framework.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.Set;

public interface WebSocketServerEndpoint {

    boolean hasBeforeHandshake(Channel channel, String path);

    void doBeforeHandshake(Channel channel, FullHttpRequest req, String path);

    void doOnOpen(Channel channel, FullHttpRequest req, String path);

    void doOnClose(Channel channel);

    void doOnError(Channel channel, Throwable throwable);

    void doOnMessage(Channel channel, WebSocketFrame frame);

    void doOnBinary(Channel channel, WebSocketFrame frame);

    void doOnEvent(Channel channel, Object evt);

    String getHost();

    int getPort();
    Set<WebSocketPathMatcher> getPathMatcherSet();

    void addPathMethodMapping(String path, WebSocketMethodMapping webSocketMethodMapping);
}
