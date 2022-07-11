package org.dows.framework.rest.log;


/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
public enum LogStrategy {

    /**
     * No config
     */
    NULL,

    /**
     * No logs.
     */
    NONE,

    /**
     * Logs request and response lines.
     */
    BASIC,

    /**
     * Logs request and response lines and their respective headers.
     */
    HEADERS,

    /**
     * Logs request and response lines and their respective headers and bodies (if present).
     */
    BODY
}
