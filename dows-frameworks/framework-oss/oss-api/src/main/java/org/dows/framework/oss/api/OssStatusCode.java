package org.dows.framework.oss.api;

import org.dows.framework.api.StatusCode;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/16/2022
 */
public enum OssStatusCode implements StatusCode {

    DOWNLOAD_EXCEPTION,
    LIMIT_EXCEPTION,
    ;

    @Override
    public Integer getCode() {
        return null;
    }

    @Override
    public String getDescr() {
        return null;
    }
}
