package com.design.warehousemanagement.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import java.util.List;

/**
 * 弃用
 * @param <T>
 */
@Data
public class PageResult<T> {
    private long total;
    private int pageNum;
    private int pageSize;
    private List<T> list;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setPageNum((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setList(page.getRecords());
        return result;
    }

}