package org.dows.framework.crud.api;

import lombok.Data;

@Data
public class PageQuery {
    //@NotNull(message = "size不可为空")
    //@Min(value = 1, message = "每页数量最小为1")
    private Integer size = 10;
    //@NotNull(message = "page不可为口令")
    //@Min(value = 1, message = "页数最小为1")
    private Integer page;

    public Integer getSize() {
        if (null == this.size || this.size < 1) {
            return 20;
        }
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPage() {
        if (null == this.page || this.page < 1) {
            return 1;
        }
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
