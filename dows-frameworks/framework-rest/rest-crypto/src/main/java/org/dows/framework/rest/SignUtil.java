package org.dows.framework.rest;

import org.dows.framework.utils.crypto.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.GlobalKeys;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/10/2022
 */
@Slf4j
public class SignUtil {


    public static String signParams(String postBodyString, String requestMethod, String route) {
        TreeMap<String, String> treeMap = splitPostString(postBodyString);
        dynamicParams(treeMap);
        String sign;
        // post请求参数在RequestBody中，被encode了，get请求参数在FormBody中，没有被encode，这里要区分处理。
        if ("GET".equals(requestMethod)) {
            sign = getSignNoDecode(treeMap, route);
        } else {
            sign = getSign(treeMap, route);
        }
        treeMap.put(GlobalKeys.SIGN, sign);
        return getPostParamsStr(treeMap);
    }


    /**
     * @param map
     * @return
     */
    public static TreeMap<String, String> dynamicParams(TreeMap<String, String> map) {
//        String token = AccountUtil.getUserInfo().getToken();
//        String userNo = AccountUtil.getUserInfo().getUserNo();
//        String accountNo = AccountUtil.getUserInfo().getAccountNo();
//        String deviceId = DeviceUtil.getUUID();
//        String ip = IpUtil.getIp();
//
//        map.put(ConstantKeys.timestamp, String.valueOf(System.currentTimeMillis() / 1000));
//        map.put(ConstantKeys.token,token);
//        map.put(ConstantKeys.userNo,userNo);
//        map.put(ConstantKeys.accountNo,accountNo);
//        map.put(ConstantKeys.deviceId,deviceId);
//        map.put(ConstantKeys.ip,ip);
        return map;
    }

    /**
     * 一般接口调用-signa签名生成规则
     *
     * @param map 有序请求参数map
     */
    public static String getSign(TreeMap<String, String> map, String route) {
        StringBuilder sb = new StringBuilder();
        map.forEach((key, val) -> {
            try {
                sb.append(key).append("=").append(URLDecoder.decode(val.toString(), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        // 所有请求参数排序后的字符串后进行MD5（32）参数排序+app_secret
        log.info("sign_string: {}", sb);
        String sign = MD5Util.sign(sb.toString(), "", "utf-8").toLowerCase();
        log.info("sign: {}", sign);
        return sign;
    }

    /**
     * 一般接口调用-signa签名生成规则
     *
     * @param map   有序请求参数map
     * @param route
     */
    public static String getSignNoDecode(TreeMap<String, String> map, String route) {
        StringBuilder sb = new StringBuilder();
        map.forEach((key, val) -> {
            // 之前没有encode过，这里如果decode可能会出问题，例如+号在没有encode过的情况下去decode会变成空格
            sb.append(key).append("=").append(val).append("&");
        });
        // 所有请求参数排序后的字符串后进行MD5（32）
        String str = sb.deleteCharAt(sb.length() - 1).toString();
        String str1 = str.toLowerCase();
        log.info("before sign: {}", str1);
        String sign = MD5Util.sign(sb.toString(), "", "utf-8").toLowerCase();
        log.info("after sign: {}", sign);
        return sign;
    }

    /**
     * 分割请求参数，放入treeMap中,拼接动态参数
     *
     * @param postBodyString 请求参数
     */
    private static TreeMap<String, String> splitPostString(String postBodyString) {
        TreeMap<String, String> map = new TreeMap<>();
        for (String s : postBodyString.split("&")) {
            String[] keyValue = s.split("=");
            map.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
        }
        return map;
    }

    /**
     * 将map拼装成请求字符串
     *
     * @return 返回请求参数
     */
    private static String getPostParamsStr(TreeMap<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        map.forEach((key, val) -> {
            sb.append(key).append("=").append(val).append("&");
        });
        String sb1;
        if (sb.toString().length() > 1) {
            sb1 = sb.substring(0, sb.length() - 1);
        } else {
            sb1 = sb.toString();
        }
        log.info("after_sign: {}", sb1);
        return sb1;
    }
}
