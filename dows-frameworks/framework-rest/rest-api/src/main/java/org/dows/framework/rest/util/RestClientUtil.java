package org.dows.framework.rest.util;

import org.dows.framework.rest.annotation.RestClient;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/29/2022
 */
public class RestClientUtil {

    private static final String SUFFIX = "/";

    public static String convertBaseUrl(RestClient restClient, String baseUrl, Environment environment) {
        if (StringUtils.hasText(baseUrl)) {
            baseUrl = environment.resolveRequiredPlaceholders(baseUrl);
            // 解析baseUrl占位符
            if (!baseUrl.endsWith(SUFFIX)) {
                baseUrl += SUFFIX;
            }
        } else {
            String serviceId = restClient.serviceId();
            String path = restClient.path();
            if (!path.endsWith(SUFFIX)) {
                path += SUFFIX;
            }
            baseUrl = "http://" + (serviceId + SUFFIX + path).replaceAll("/+", SUFFIX);
            baseUrl = environment.resolveRequiredPlaceholders(baseUrl);
        }
        return baseUrl;
    }
}
