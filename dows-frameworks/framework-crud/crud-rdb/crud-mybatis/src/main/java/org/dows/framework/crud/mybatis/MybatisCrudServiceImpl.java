package org.dows.framework.crud.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

public abstract class MybatisCrudServiceImpl<M extends MybatisCrudMapper<T>, T> extends ServiceImpl<M, T> implements MybatisCrudService<T> {
}
