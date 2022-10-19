package org.dows.sequence.api;

import lombok.Data;

@Data
public class SequenceConfig {
    /**
     * 前缀
     */
    public static final String PREFIX = "sequence";
    /**
     * 是否可用,默认值
     */
    public static final String ENABLED = "enabled";
    /**
     * 默认实现:,默认值
     */
    public static final String DEFAULT_IMPL = "default";
    /**
     * 是否可用
     */
    private String enabled = ENABLED;
    /**
     * 实现
     */
    private String impl = DEFAULT_IMPL;
}
