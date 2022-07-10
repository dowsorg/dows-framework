package org.dows.framework.api.assertion;

import org.dows.framework.api.StatusCode;
import org.dows.framework.api.exceptions.BaseException;

import java.util.Collection;
import java.util.Map;

/**
 * 枚举类异常断言，提供简便的方式判断条件，并在条件满足时抛出异常
 * 错误码和错误信息定义在枚举类中，在本断言方法中，传递错误信息需要的参数
 */
public interface ExceptionAssert {

    BaseException newException();

    BaseException newException(Object[] args);

    BaseException newException(Throwable t);

    BaseException newException(Throwable t, Object[] args);

    BaseException newException(StatusCode statusCode, Object... args);

    BaseException newException(StatusCode statusCode, Throwable t, Object... args);


    /**
     * 断言对象非空。如果对象为空，则抛出异常
     *
     * @param obj 待判断对象
     */
    default void assertNotNull(Object obj) {
        if (obj == null) {
            throw newException();
        }
    }


    /**
     * 断言对象非空。如果对象为空，则抛出异常
     * 异常信息message支持传递参数方式，避免在判断之前进行字符串拼接操作
     *
     * @param obj  待判断对象
     * @param args message占位符对应的参数列表
     */
    default void assertNotNull(Object obj, Object... args) {
        if (obj == null) {
            throw newException(args);
        }
    }


    /**
     * 断言字符串str不为空串（长度为0）。如果字符串str为空串，则抛出异常
     *
     * @param str 待判断字符串
     */
    default void assertNotEmpty(String str) {
        if (null == str || "".equals(str.trim())) {
            throw newException();
        }
    }

    /**
     * 断言字符串str不为空串（长度为0）。如果字符串str为空串，则抛出异常
     * 异常信息message支持传递参数方式，避免在判断之前进行字符串拼接操作
     *
     * @param str  待判断字符串
     * @param args message占位符对应的参数列表
     */
    default void assertNotEmpty(String str, Object... args) {
        if (str == null || "".equals(str.trim())) {
            throw newException(args);
        }
    }

    /**
     * 断言数组arrays大小不为0。如果数组arrays大小不为0，则抛出异常
     *
     * @param arrays 待判断数组
     */
    default void assertNotEmpty(Object[] arrays) {
        if (arrays == null || arrays.length == 0) {
            throw newException();
        }
    }

    /**
     * 断言数组arrays大小不为0。如果数组arrays大小不为0，则抛出异常
     * 异常信息message支持传递参数方式，避免在判断之前进行字符串拼接操作
     *
     * @param arrays 待判断数组
     * @param args   message占位符对应的参数列表
     */
    default void assertNotEmpty(Object[] arrays, Object... args) {
        if (arrays == null || arrays.length == 0) {
            throw newException(args);
        }
    }

    /**
     * 断言集合c大小不为0。如果集合c大小不为0，则抛出异常
     *
     * @param c 待判断数组
     */
    default void assertNotEmpty(Collection<?> c) {
        if (c == null || c.isEmpty()) {
            throw newException();
        }
    }

    /**
     * 断言集合c大小不为0。如果集合c大小不为0，则抛出异常
     *
     * @param c    待判断数组
     * @param args message占位符对应的参数列表
     */
    default void assertNotEmpty(Collection<?> c, Object... args) {
        if (c == null || c.isEmpty()) {
            throw newException(args);
        }
    }

    /**
     * 断言Mapmap大小不为0。如果Mapmap大小不为0，则抛出异常
     *
     * @param map 待判断Map
     */
    default void assertNotEmpty(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            throw newException();
        }
    }

    /**
     * 断言Mapmap大小不为0。如果Mapmap大小不为0，则抛出异常
     *
     * @param map  待判断Map
     * @param args message占位符对应的参数列表
     */
    default void assertNotEmpty(Map<?, ?> map, Object... args) {
        if (map == null || map.isEmpty()) {
            throw newException(args);
        }
    }

    /**
     * 断言布尔值为false。如果布尔值为true，则抛出异常
     *
     * @param expression 待判断布尔变量
     */
    default void assertIsFalse(boolean expression) {
        if (expression) {
            throw newException();
        }
    }

    /**
     * 断言布尔值为false。如果布尔值为true，则抛出异常
     *
     * @param expression 待判断布尔变量
     * @param args       message占位符对应的参数列表
     */
    default void assertIsFalse(boolean expression, Object... args) {
        if (expression) {
            throw newException(args);
        }
    }

    /**
     * 断言布尔值为true。如果布尔值为false，则抛出异常
     *
     * @param expression 待判断布尔变量
     */
    default void assertIsTrue(boolean expression) {
        if (!expression) {
            throw newException();
        }
    }

    /**
     * 断言布尔值为true。如果布尔值为false，则抛出异常
     *
     * @param expression 待判断布尔变量
     * @param args       message占位符对应的参数列表
     */
    default void assertIsTrue(boolean expression, Object... args) {
        if (!expression) {
            throw newException(args);
        }
    }

    /**
     * 断言对象为null。如果对象不为null，则抛出异常
     *
     * @param obj 待判断对象
     */
    default void assertIsNull(Object obj) {
        if (obj != null) {
            throw newException();
        }
    }

    /**
     * 断言对象为null。如果对象不为null，则抛出异常
     *
     * @param obj  待判断布尔变量
     * @param args message占位符对应的参数列表
     */
    default void assertIsNull(Object obj, Object... args) {
        if (obj != null) {
            throw newException(args);
        }
    }

    /**
     * 直接抛出异常
     */
    default void assertFail() {
        throw newException();
    }

    /**
     * 直接抛出异常
     *
     * @param args message占位符对应的参数列表
     */
    default void assertFail(Object... args) {
        throw newException(args);
    }

    /**
     * 直接抛出异常，并包含原异常信息
     * 当捕获非运行时异常（非继承{@link RuntimeException}）时，并该异常进行业务描述时， 必须传递原始异常，作为新异常的cause
     *
     * @param t 原始异常
     */
    default void assertFail(Throwable t) {
        throw newException(t);
    }


    /**
     * 直接抛出异常，并包含原异常信息
     * 当捕获非运行时异常（非继承{@link RuntimeException}）时，并该异常进行业务描述时， 必须传递原始异常，作为新异常的cause
     *
     * @param t    原始异常
     * @param args message占位符对应的参数列表
     */
    default void assertFail(Throwable t, Object... args) {
        throw newException(t, args);
    }


    /**
     * 断言对象o1与对象o1o2o1相等，此处的相等指（o1.equals(o2)为true）。
     * 如果两对象不相等，则抛出异常
     *
     * @param o1   待判断对象，若o1为null，也当作不相等处理
     * @param o2   待判断对象
     * @param args message占位符对应的参数列表
     */
    default void assertEquals(Object o1, Object o2, Object... args) {
        if (o1 == o2) {
            return;
        }
        if (o1 == null) {
            throw newException(args);
        }
        if (!o1.equals(o2)) {
            throw newException(args);
        }
    }

//
//    /**
//     * 大于O
//     */
//    default void gtZero(Integer num, StatusCode statusCode) {
//        if (num == null || num <= 0) {
//            fail(statusCode);
//        }
//    }
//
//    /**
//     * 大于等于O
//     */
//    default void geZero(Integer num, StatusCode statusCode) {
//        if (num == null || num < 0) {
//            fail(statusCode);
//        }
//    }
//
//    /**
//     * num1大于num2
//     */
//    default void gt(Integer num1, Integer num2, StatusCode statusCode) {
//        if (num1 <= num2) {
//            fail(statusCode);
//        }
//    }
//
//    /**
//     * num1大于等于num2
//     */
//    default void ge(Integer num1, Integer num2, StatusCode statusCode) {
//        if (num1 < num2) {
//            fail(statusCode);
//        }
//    }
//
//
//    /**
//     * 断言对象相等，此处的相等指（o1.equals(o2)为true）。如果两对象不相等，则抛出异常
//     * @param obj1 待判断对象，若o1为null，也当作不相等处理
//     * @param obj2 待判断对象
//     * @param statusCode
//     */
//    default void eq(Object obj1, Object obj2, StatusCode statusCode) {
//        if (obj1 == obj2) {
//            return;
//        }
//        if (obj1 == null) {
//            throw newException();
//        }
//        if (!obj1.equals(obj2)) {
//            throw newException();
//        }
//        if (!obj1.equals(obj2)) {
//            fail(statusCode);
//        }
//    }
//
//    default void isTrue(boolean condition, StatusCode statusCode) {
//        if (!condition) {
//            fail(statusCode);
//        }
//    }
//
//    default void isFalse(boolean condition, StatusCode statusCode) {
//        if (condition) {
//            fail(statusCode);
//        }
//    }
//
//    default void isNull(StatusCode statusCode, Object... conditions) {
//        if (Objects.isNull(conditions)) {
//            fail(statusCode);
//        }
//    }
//
//    default void notNull(StatusCode statusCode, Object... conditions) {
//        if (Objects.nonNull(conditions)) {
//            fail(statusCode);
//        }
//    }
//
//
//    default void isEmpty(StatusCode statusCode, Object... conditions) {
//        if (Objects.hash(conditions) < 1) {
//            fail(statusCode);
//        }
//    }
//
//    default void isNotEmpty(StatusCode statusCode, Object... conditions) {
//        if (Objects.hash(conditions) > 1) {
//            fail(statusCode);
//        }
//    }
//    default void notEmpty(Object[] array, StatusCode statusCode) {
//        if (Objects.hash(array) < 1) {
//            fail(statusCode);
//        }
//    }
//
//    default void noNullElements(Object[] array, StatusCode statusCode) {
//        if (array != null) {
//            for (Object element : array) {
//                if (element == null) {
//                    fail(statusCode);
//                }
//            }
//        }
//    }
//
//    default void notEmpty(Collection<?> collection, StatusCode statusCode) {
//        if (!CollectionUtils.isEmpty(collection)) {
//            fail(statusCode);
//        }
//    }
//
//    default void notEmpty(Map<?, ?> map, StatusCode statusCode) {
//        if (!CollectionUtils.isEmpty(map)) {
//            fail(statusCode);
//        }
//    }
//
//    default void isInstanceOf(Class<?> type, Object obj, StatusCode statusCode) {
//        notNull(statusCode, type);
//        if (!type.isInstance(obj)) {
//            fail(statusCode);
//        }
//    }
//
//    default void isAssignable(Class<?> superType, Class<?> subType, StatusCode statusCode) {
//        notNull(statusCode, superType);
//        if (subType == null || !superType.isAssignableFrom(subType)) {
//            fail(statusCode);
//        }
//    }
}
