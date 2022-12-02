package org.dows.framework.websocket;

public interface WebSocketServer {

    void init() throws Exception;


    WebSocketServerEndpoint getEndpoint();
}
