package org.dows.framework.websocket.path;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.dows.framework.websocket.WebSocketPathMatcher;

/**
 * @Description: Default path matcher
 */
public class DefaultPathMatcher implements WebSocketPathMatcher {

    private final String pattern;

    public DefaultPathMatcher(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matchAndExtract(QueryStringDecoder decoder, Channel channel) {
        return pattern.equals(decoder.path());
    }
}
