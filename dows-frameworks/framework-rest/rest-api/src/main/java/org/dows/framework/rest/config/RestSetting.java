package org.dows.framework.rest.config;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * 远程配置
 */
@Data
@ToString
public class RestSetting {
    /**
     * 前缀
     */
    public static final String PREFIX = "remote";
    /**
     * 是否可用,默认值
     */
    public static final String ENABLED = "enabled";
    /**
     * 重试次数,默认值
     */
    public static final int DEFAULT_RETRY_COUNT = 0;
    /**
     * 重试周期,默认值,毫秒
     */
    public static final int DEFAULT_PERIOD = 5000;
    /**
     * 连接池最大并发连接数,默认值
     */
    public static final int DEFAULT_MAX_TOTAL = 300;
    /**
     * 连接池最大并发连接数,默认值
     */
    public static final int DEFAULT_MAX_ROUTE = 50;
    /**
     * 数据传输处理时间,默认值
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
    /**
     * 建立连接的timeout时间,默认值
     */
    public static final int DEFAULT_CONN_TIMEOUT = 5 * 1000;
    /**
     * 从连接池中获取连接的timeout时间,默认值
     */
    public static final int DEFAULT_CONN_REQ_TIMEOUT = 2 * 1000;
    /**
     * 包扫描路径,默认值
     */
    public static final String DEFAULT_SCAN_PACKAGE = "";
    /**
     * 是否支持SSL
     */
    private boolean ssl = false;
    /**
     * 证书路径
     */
    private String jksPath;
    /**
     * 证书密码
     */
    private String jksPwd;
    /**
     * 重试次数
     */
    private int retryCount = DEFAULT_RETRY_COUNT;
    /**
     * 重试周期:5秒
     */
    private long period = DEFAULT_PERIOD;
    /**
     * 连接池最大并发连接数
     */
    private int maxTotal = DEFAULT_MAX_TOTAL;
    /**
     * 单路由最大并发数
     */
    private int defaultMaxPerRoute = DEFAULT_MAX_ROUTE;
    /**
     * 数据传输处理时间
     */
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    /**
     * 建立连接的timeout时间
     */
    private int connectionTimeout = DEFAULT_CONN_TIMEOUT;
    /**
     * 从连接池中获取连接的timeout时间
     */
    private int connectionRequestTimeout = DEFAULT_CONN_REQ_TIMEOUT;
    /**
     * 包扫描路径
     */
    private String scanPackage = DEFAULT_SCAN_PACKAGE;
    /**
     * 远程地址服务地址映射 远程地址服务地址KEY->远程服务地址
     */
    private Map<String, String> serviceUrlMap = new HashMap<>();

}
