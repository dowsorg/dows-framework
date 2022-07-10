package org.dows.framework.crud.mybatis.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.annotation.TenantNoQuery;
import org.dows.framework.crud.api.annotation.DataPermission;
import org.dows.framework.crud.api.annotation.Query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 */
@Slf4j
public class QueryWrapperUtils {

    public static <R, Q> QueryWrapper<R> getPredicate(Q query) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("dt");
        if (query == null) {
            return queryWrapper;
        }

        // 数据权限验证
        DataPermission permission = query.getClass().getAnnotation(DataPermission.class);
        if (permission != null) {
            // 获取数据权限
//            List<Long> dataScopes = SecurityUtils.getCurrentUserDataScope();
//            if (CollectionUtil.isNotEmpty(dataScopes)) {
//                if (StringUtils.isNotBlank(permission.fieldName()) && StrUtil.isBlank(permission.inSql())) {
//                    queryWrapper.in(permission.fieldName(), dataScopes);
//
//                } else if (StringUtils.isNotBlank(permission.fieldName()) && StrUtil.isNotBlank(permission.inSql())) {
//                    String sql = permission.inSql();
//                    sql = StrUtil.replace(sql, "?", StrUtil.join(",", dataScopes));
//                    queryWrapper.inSql(permission.fieldName(), sql);
//                }
//            } else {
//                // 声明了需要数据权限，但是dataScopes为空，所以使用一个不存在的数模拟为空的情况
//                queryWrapper.in(permission.fieldName(), 999999L);
//            }
        }

        try {
            List<Field> fields = getAllFields(query.getClass(), new ArrayList<>());
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Object val = field.get(query);
                // 统一填充租户号
                String attributeName = StrUtil.toUnderlineCase(field.getName());
                TenantNoQuery tenantNo = field.getAnnotation(TenantNoQuery.class);
                if (tenantNo != null) {
                    // 获取当前租户号/租户ID，这里要确保租户号一定能够取到值，否则数据隔离不起效果
//                    Long tenantId = UserUtil.getTenantId();
//                    queryWrapper.eq(attributeName, tenantId);
                }

                if (ObjectUtil.isNull(val) || "".equals(val)) {
                    continue;
                }

                Query q = field.getAnnotation(Query.class);
                if (q != null) {
                    String propName = q.propName();
                    String blurry = q.blurry();
                    attributeName = StrUtil.isBlank(propName) ? attributeName : StrUtil.toUnderlineCase(propName);
                    // 模糊多字段
                    if (ObjectUtil.isNotEmpty(blurry)) {
                        String[] blurrys = blurry.split(",");
                        queryWrapper.and(wrapper -> {
                            for (String blurry1 : blurrys) {
                                String column = StrUtil.toUnderlineCase(blurry1);
                                wrapper.or();
                                wrapper.like(column, val.toString());
                            }
                        });
                        continue;
                    }
                    String finalAttributeName = attributeName;
                    switch (q.type()) {
                        case EQUAL:
                            queryWrapper.eq(finalAttributeName, val);
                            break;
                        case GREATER_THAN:
                            queryWrapper.ge(finalAttributeName, val);
                            break;
                        case LESS_THAN:
                            queryWrapper.le(finalAttributeName, val);
                            break;
                        case LESS_THAN_NQ:
                            queryWrapper.lt(finalAttributeName, val);
                            break;
                        case INNER_LIKE:
                            queryWrapper.like(finalAttributeName, val);
                            break;
                        case LEFT_LIKE:
                            queryWrapper.likeLeft(finalAttributeName, val);
                            break;
                        case RIGHT_LIKE:
                            queryWrapper.likeRight(finalAttributeName, val);
                            break;
                        case IN:
                            if (CollUtil.isNotEmpty((Collection<Long>) val)) {
                                queryWrapper.in(finalAttributeName, (Collection<Long>) val);
                            } else {
                                queryWrapper.in(finalAttributeName, 999999L);
                            }
                            break;
                        case IN_SQL: {
                            String sql = q.sql();
                            sql = StrUtil.replace(sql, "?", val.toString());
                            queryWrapper.inSql(finalAttributeName, sql);
                        }
                        break;
                        case NOT_EQUAL:
                            queryWrapper.ne(finalAttributeName, val);
                            break;
                        case NOT_NULL:
                            queryWrapper.isNotNull(finalAttributeName);
                            break;
                        case IS_NULL:
                            queryWrapper.isNull(finalAttributeName);
                            break;
                        case BETWEEN:
                            List<Object> between = new ArrayList<>((List<Object>) val);
                            queryWrapper.between(finalAttributeName, between.get(0), between.get(1));
                            break;
                        default:
                            break;
                    }
                } else {
                    queryWrapper.eq(attributeName, val);
                }
                field.setAccessible(accessible);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return queryWrapper;
    }

    public static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            //getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }


//    public static void main(String[] args) {
//        QueryWrapper<Paging> query = new QueryWrapper<Paging>();
//        //query.or();
//        query.or(wrapper -> wrapper.eq("store_id", 1).or().eq("store_id", 2));
//        //query.like("a",1);
//        //query.or();
//        //query.like("b",2);
//        //query.and(wrapper->wrapper.eq("c",1));
//        query.eq("1", 1);
//
//        System.out.println(query.getSqlSegment());
//    }
}
