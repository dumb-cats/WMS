package com.design.warehousemanagement.mapper.warehouse;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.design.warehousemanagement.pojo.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {
}
