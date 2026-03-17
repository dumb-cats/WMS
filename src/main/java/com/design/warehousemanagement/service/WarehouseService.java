package com.design.warehousemanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.design.warehousemanagement.pojo.WorkTasks;

public interface WarehouseService extends IService<WorkTasks> {
    void generateInboundTasks(WorkTasks inboundTask);
    WorkTasks assignTaskBySku(String sku, String userId);
    boolean completeTaskByLocationScan(int taskId, String locationCode, String userId);
}
