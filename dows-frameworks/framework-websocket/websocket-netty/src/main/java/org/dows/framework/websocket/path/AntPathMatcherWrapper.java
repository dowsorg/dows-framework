package org.dows.framework.websocket.path;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.dows.framework.websocket.WebSocketPathMatcher;
import org.springframework.util.AntPathMatcher;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.dows.framework.websocket.AttributeKeyConstant.URI_TEMPLATE;

/**
 * @Description: Ant path matcher
 */
public class AntPathMatcherWrapper extends AntPathMatcher implements WebSocketPathMatcher {

    private final String pattern;

    public AntPathMatcherWrapper(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matchAndExtract(QueryStringDecoder decoder, Channel channel) {
        Map<String, String> variables = new LinkedHashMap<>();
        boolean result = doMatch(pattern, decoder.path(), true, variables);
        if (result) {
            channel.attr(URI_TEMPLATE).set(variables);
            return true;
        }
        return false;
    }
}
