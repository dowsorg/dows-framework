package org.dows.demo.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ClientMonitor {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);


    public void start() {
        // 定时扫描所有的Channel，关闭失效的Channel
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ClientManager.scanNotActiveChannel();
            }
        }, 3, 60, TimeUnit.SECONDS);

        // 定时向所有客户端发送Ping消息
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ClientManager.broadCastPing();
            }
        }, 3, 50, TimeUnit.SECONDS);
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
