package org.dows.framework.rest.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 连接池参数配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoolConfig {
    private int maxIdleConnections;
    private long keepAliveSecond;

}
