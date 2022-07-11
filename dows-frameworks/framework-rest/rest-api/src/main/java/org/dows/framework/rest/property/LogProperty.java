package org.dows.framework.rest.property;

import lombok.Data;
import org.dows.framework.rest.log.LogLevel;
import org.dows.framework.rest.log.LogStrategy;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/28/2022
 */
@Data
public class LogProperty extends RestProperty {
    /**
     * 日志打印级别
     */
    protected LogLevel logLevel = LogLevel.INFO;
    /**
     * 日志打印策略
     */
    protected LogStrategy logStrategy = LogStrategy.BASIC;
}
