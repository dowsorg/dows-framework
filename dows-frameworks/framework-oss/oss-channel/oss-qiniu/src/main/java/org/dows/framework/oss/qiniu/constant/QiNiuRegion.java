package org.dows.framework.oss.qiniu.constant;

import cn.hutool.core.util.ReflectUtil;
import com.qiniu.storage.Region;

import java.lang.reflect.Method;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 5/8/2022
 */
public enum QiNiuRegion {
    /**
     * 华东区域
     */
    HUADONG("huadong"),
    /**
     * 华北区域
     */
    HUABEI("huabei"),
    /**
     * 华南区域
     */
    HUANAN("huanan"),
    /**
     * 北美区域
     */
    BEIMEI("beimei"),
    /**
     * 新加坡区域
     */
    XINJIAPO("xinjiapo"),
    /**
     *
     */
    AUTOREGION("autoRegion");

    private final String region;

    QiNiuRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return region;
    }

    public Region buildRegion() {
        Method method = ReflectUtil.getMethodByName(Region.class, this.region);
        return ReflectUtil.invokeStatic(method);
    }
}
