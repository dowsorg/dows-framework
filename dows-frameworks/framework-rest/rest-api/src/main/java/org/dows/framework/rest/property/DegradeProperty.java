package org.dows.framework.rest.property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dows.framework.rest.degrade.DegradeType;
import org.dows.framework.rest.parser.DefaultResourceNameParser;
import org.dows.framework.rest.parser.ResourceNameParser;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/3/2022
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DegradeProperty extends RestProperty {

    /**
     * 熔断降级类型，暂时只支持SENTINEL
     */
    private DegradeType degradeType = DegradeType.SENTINEL;

    /**
     * 资源名称解析器
     */
    private Class<? extends ResourceNameParser> resourceNameParser = DefaultResourceNameParser.class;


}
