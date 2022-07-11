package org.dows.framework.rest.property;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/5/2022
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SecurityProperty extends RestProperty {
    List<Class<?>> apiCryptos = new ArrayList<>();
    /**
     * 密钥key
     * 支持占位符形式配置。
     *
     * @return
     */
    private String secretId;
    /**
     * 密钥
     * 支持占位符形式配置。
     *
     * @return
     */
    private String secretKey;
    /**
     * 编码格式
     */
    private String charset;
    private long timeout;


}
