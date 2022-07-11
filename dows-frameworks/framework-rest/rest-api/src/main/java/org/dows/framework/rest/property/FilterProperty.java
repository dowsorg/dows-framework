package org.dows.framework.rest.property;

import lombok.Data;

import java.util.List;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/3/2022
 */
@Data
public class FilterProperty extends RestProperty {
    /**
     * 拦截器匹配路径pattern
     *
     * @return 拦截器匹配路径
     */
    private List<String> includes;

    /**
     * 拦截器排除匹配，排除指定路径拦截
     *
     * @return 排除指定路径拦截
     */
    private List<String> excludes;

}
