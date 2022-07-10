package org.dows.framework.crud.mybatis;

import java.io.Serializable;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin PN15855012581
 * @date 12/11/2020 4:19 PM
 */
public interface CrudEntity extends Serializable {
    Long getId();

    CrudEntity setId(Long id);

    Boolean getDeleted();

    CrudEntity setDeleted(Boolean deleted);

}
