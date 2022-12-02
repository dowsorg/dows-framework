package org.dows.framework.websocket;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

/**
 * @Description: 开启WebSocket
 */
@ConditionalOnMissingBean({WebSocketEndpointExporter.class})
@AutoConfiguration
public class NettyWebSocketSelector {

    @Bean
    public WebSocketEndpointExporter webSocketEndpointExporter(ResourceLoader resourceLoader, List<MethodArgumentResolver> resolvers) {
        return new WebSocketEndpointExporter(resourceLoader,resolvers);
    }
}
