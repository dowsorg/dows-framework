//package tech.wisdomer.framework.crud.utils;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.core.metadata.OrderItem;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 分页工具
// */
//public class PageUtil extends cn.hutool.core.util.PageUtil {
//
//    /**
//     * List 分页
//     */
//    public static List toPage(int page, int size, List list) {
//        int fromIndex = page * size;
//        int toIndex = page * size + size;
//        if (fromIndex > list.size()) {
//            return new ArrayList();
//        } else if (toIndex >= list.size()) {
//            return list.subList(fromIndex, list.size());
//        } else {
//            return list.subList(fromIndex, toIndex);
//        }
//    }
//
//
//
//
//    public static <T> IPage<T> toMybatisPage(Pageable pageable) {
//        return toMybatisPage(pageable, false);
//    }
//    public static <T> IPage<T> toMybatisPage(Pageable pageable, boolean ignoreOrderBy) {
//        com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page =
//                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
//        if (!ignoreOrderBy) {
//            for (Sort.Order order : pageable.getSort()) {
//                OrderItem orderItem = new OrderItem();
//                orderItem.setAsc(order.isAscending());
//                orderItem.setColumn(com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(order.getProperty()));
//                page.addOrder(orderItem);
//            }
//        }
//        return page;
//    }
//
//}
