package org.dows.sequence.api;

import java.util.concurrent.TimeUnit;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/1/2022
 */
interface KeyDefine {
    String getKey();

    String getBizPrefix();

    int getTimeout();

    String getFmtSuffix();

    TimeUnit getTimeUnit();
}
