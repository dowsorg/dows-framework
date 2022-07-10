package org.dows.framework.crud.mybatis.utils;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;

import java.util.Date;

@Data
public class CrudQuery<E> {
    private Long id;
    private int pageNo;
    private int pageSize;
    private boolean deleted;
    private Date dt;

    public Wrapper<E> queryWrapper() {
        return new QueryWrapper(this);
    }


}
