//package org.dows.framework.rest.annotation;
//
//
//import org.dows.framework.rest.cryptor.Cryptor;
//
//import java.lang.annotation.*;
//
///**
// * @author lait.zhang@gmail.com
// * @description: TODO
// * @weixin SH330786
// * @date 4/5/2022
// */
//@Retention(RetentionPolicy.RUNTIME)
//@Target({ElementType.METHOD, ElementType.TYPE})
//@Documented
//@InterceptMark
//public @interface Crypto {
//    /**
//     * 密钥key
//     * 支持占位符形式配置。默认为空
//     *
//     * @return
//     */
//    String secretId() default "";
//
//    /**
//     * 密钥
//     * 支持占位符形式配置,默认为空
//     *
//     * @return
//     */
//    String secretKey() default "";
//    /**
//     * 是否校验token，默认开启
//     */
//    boolean token() default true;
//
//    /**
//     * 是否校验sign,默认关闭
//     */
//    boolean sign() default false;
//
//    /**
//     * 编码格式
//     *
//     * @return
//     */
//    String charset() default "UTF-8";
//
//
//    String timeout() default "0";
//
//    /**
//     * 接口的加解密器集合
//     *
//     * @return
//     */
//    Cryptor cryptor() default Cryptor.nullCryptor;
//
//}
