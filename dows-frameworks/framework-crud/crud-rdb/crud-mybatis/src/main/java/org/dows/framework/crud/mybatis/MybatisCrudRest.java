package org.dows.framework.crud.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.dows.framework.api.Response;
import org.dows.framework.api.status.CrudStatusCode;
import org.dows.framework.crud.mybatis.utils.QueryWrapperUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public interface MybatisCrudRest<Form, Entity extends CrudEntity, Service extends MybatisCrudService<Entity>> {

    default Service getService() {
        Type[] types = getClass().getGenericInterfaces();
        return CrudContext.getBean((Class<Service>) ((ParameterizedType) types[0]).getActualTypeArguments()[2]);
    }


    default Class<Entity> entityClass() {
        Type[] types = getClass().getGenericInterfaces();
        Class<Entity> entityClass = (Class<Entity>) ((ParameterizedType) types[0]).getActualTypeArguments()[1];
        /*for (Field filed : entityClass.getDeclaredFields()) {
            TenantNoInsert tenantNo = filed.getAnnotation(TenantNoInsert.class);
            if (tenantNo != null) {
                Long tenantId = UserUtil.getTenantId();
                BeanUtil.setFieldValue(entityClass, filed.getName(), tenantId);
            }
            TenantNameInsert tenantName = filed.getAnnotation(TenantNameInsert.class);
            if (tenantName != null) {
                String name = UserUtil.getAccount().getTenantName();
                BeanUtil.setFieldValue(entityClass, filed.getName(), name);
            }
        }*/
        return entityClass;
    }

    /**
     * 保存
     *
     * @param form
     * @return
     */
    @ApiOperation("保存当前请求对象")
    @PostMapping
    default Response<Entity> save(@Validated @RequestBody Form form) {
        Class<Entity> entityClass = entityClass();
        Entity entity = BeanConvert.convert(form, entityClass);
        if (!getService().save(entity)) {
            Response.crudFailed(CrudStatusCode.CREATE_FAILSED);
        }
        return Response.ok(entity);
    }

    /**
     * 批量保存
     *
     * @param forms
     * @return
     */
    @ApiOperation("批量保存当前请求对象")
    @PostMapping("/batch")
    default Response<List<Entity>> save(@Validated @RequestBody List<Form> forms) {
        List<Entity> entitys = BeanConvert.converts(forms, entityClass());
        if (!getService().saveBatch(entitys)) {
            Response.crudFailed(CrudStatusCode.CREATE_FAILSED);
        }
        return Response.ok(entitys);
    }


    /**
     * 根据 id 更新实体, 对实体未进行校验, 直接更新 不为 null 的值.
     *
     * @param form
     * @return true 表示更新成功
     */
    @ApiOperation("根据ID更新记录")
    @PutMapping("/{id}")
    default Response<Boolean> updById(@RequestBody @Validated Form form, @PathVariable("id") Long id) {
        Entity entity = BeanConvert.convert(form, entityClass());
        entity.setId(id);
        if (!getService().updateById(entity)) {
            return Response.crudFailed(CrudStatusCode.UPDATE_FAILED);
        }
        return Response.ok();
    }

    /**
     * 根据 id 更新实体, 对实体未进行校验, 直接更新 不为 null 的值.
     *
     * @param form
     * @return true 表示更新成功
     */
    @ApiOperation("根据ID更新记录")
    @PutMapping
    default Response<Boolean> updByIdV1(@RequestBody @Validated Form form) {
        Entity entity = BeanConvert.convert(form, entityClass());
        if (!getService().updateById(entity)) {
            return Response.crudFailed(CrudStatusCode.UPDATE_FAILED);
        }
        return Response.ok();
    }


    ///////////////////////////////////////query///////////////////////////////////////////

    /**
     * 根据 id 查询对象
     *
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询记录")
    @GetMapping(path = "/{id}")
    default Response<Entity> getById(@PathVariable("id") Long id) {
        return Response.ok(getService().getById(id));
    }

    /**
     * 根据 entity 条件查询对象.
     *
     * @param entity 入参
     * @return
     */
    @ApiOperation("根据条件查询记录(一条)")
    @GetMapping("/findOne")
    default Response<Entity> findOne(@Validated Entity entity) {
        try {
            Entity e = getService().getOne(new QueryWrapper<>(entity));
            return Response.ok(e);
        } catch (Exception e) {
            return Response.crudFailed(CrudStatusCode.QUERY_MANY_RESULT);
        }
    }

    /**
     * 根据 entity 条件查询对象列表.
     *
     * @param entity 入参
     * @return
     */
    @ApiOperation("根据条件查询记录(多条)")
    @GetMapping("/find")
    default Response<List<Entity>> find(@Validated Entity entity) {
        List<Entity> entitys = getService().list(QueryWrapperUtils.getPredicate(entity));
        return Response.ok(entitys);
    }

    /**
     * 查询所有列表
     *
     * @return DTO
     */
    @ApiOperation("列出所有记录")
    @GetMapping("/list")
    default Response<List<Entity>> list() {
        QueryWrapper qw = new QueryWrapper();
        qw.orderByDesc("dt");
        List<Entity> entitys = getService().list(qw);
        return Response.ok(entitys);
    }

    /**
     * 分页查询.
     *
     * @param entity 入参
     * @return {@link Page}
     */
    @ApiOperation("根据查询条件获取分页数据")
    @GetMapping("/page")
    default Response<IPage<Entity>> pageV1(@Validated Entity entity,
                                           @RequestParam(value = "page", defaultValue = "1") Integer pageNo,
                                           @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
        Page<Entity> page = new Page<>(pageNo, pageSize);
        QueryWrapper<Entity> queryWrapper = QueryWrapperUtils.getPredicate(entity);

        IPage result = getService().page(page, queryWrapper);
        return Response.ok(result);
    }

    /**
     * 分页查询.
     *
     * @param entity   入参
     * @param pageNo   第几页
     * @param pageSize 页大小
     * @return {@link Page}
     */
    @ApiOperation("根据查询条件获取分页数据")
    @GetMapping("/page/{page}/{size}")
    default Response<IPage<Entity>> page(@Validated Entity entity,
                                         @PathVariable(value = "page") Integer pageNo,
                                         @PathVariable(value = "size") Integer pageSize) {
        Page<Entity> page = new Page<>(pageNo, pageSize);
        QueryWrapper<Entity> queryWrapper = QueryWrapperUtils.getPredicate(entity);

        IPage result = getService().page(page, queryWrapper);
        return Response.ok(result);
    }


    ///////////////////////////////////////delete///////////////////////////////////////////

    /**
     * 根据 id 删除
     *
     * @param id id
     * @return DTO
     */
    @ApiOperation("根据ID逻辑删除")
    @DeleteMapping(path = "/{id}")
    default Response<Boolean> delById(@PathVariable("id") Long id) {
        if (!getService().update(new UpdateWrapper<Entity>().eq("id", id)
                .set("deleted", Boolean.TRUE))) {
            return Response.crudFailed(CrudStatusCode.DELETE_FAILED);
        }
        return Response.ok();
    }


    /**
     * 根据id批量逻辑删除
     *
     * @param ids
     * @return
     */
    @ApiOperation("根据ID批量逻辑删除")
    @DeleteMapping("/delByIds")
    default Response<Boolean> delById(@RequestBody List<Long> ids) {
        if (!getService().update(new UpdateWrapper<Entity>().in("id", ids)
                .set("deleted", Boolean.TRUE))) {
            return Response.crudFailed(CrudStatusCode.DELETE_FAILED);
        }
        return Response.ok();
    }
}
