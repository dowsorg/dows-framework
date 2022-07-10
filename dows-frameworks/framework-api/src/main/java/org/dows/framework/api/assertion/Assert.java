//package org.dows.framework.api.assertion;
//
//import org.dows.framework.api.StatusCode;
//import org.dows.framework.api.exceptions.BizException;
//
///**
// * @author lait.zhang@gmail.com
// * @description: TODO
// * @weixin PN15855012581
// * @date :
// */
//public class Assert /*implements ArgumentExceptionAssert,BusinessExceptionAssert,CommonExceptionAssert*/ {
//
//    protected Assert() {
//        // to do noting
//    }
//
//    /**
//     * 失败结果
//     *
//     * @param statusCode 异常错误码
//     */
//    public static void fail(StatusCode statusCode) {
//
//        //throw ExceptionFactory.buildException(statusCode);
//    }
//
//    public static void fail(boolean condition, StatusCode statusCode) {
//        if (condition) {
//            fail(statusCode);
//        }
//    }
//
//    public static void fail(String message) {
//        throw new BizException(message);
//    }
//
//    public static void fail(boolean condition, String message) {
//        if (condition) {
//            fail(message);
//        }
//    }
//
//
//}
