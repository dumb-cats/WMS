package com.design.warehousemanagement.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.warehousemanagement.mapper.warehouse.AssignmentRuleMapper;
import com.design.warehousemanagement.mapper.warehouse.LocationsMapper;
import com.design.warehousemanagement.mapper.warehouse.ProductsMapper;
import com.design.warehousemanagement.mapper.warehouse.WorkTasksMapper;
import com.design.warehousemanagement.pojo.AssignmentRule;
import com.design.warehousemanagement.pojo.Locations;
import com.design.warehousemanagement.pojo.Products;
import com.design.warehousemanagement.pojo.WorkTasks;
import com.design.warehousemanagement.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarehouseServiceImpl extends ServiceImpl<WorkTasksMapper, WorkTasks> implements WarehouseService {

    @Autowired
    private AssignmentRuleMapper assignmentRuleMapper;

    @Autowired
    private ProductsMapper productsMapper;

    @Autowired
    private LocationsMapper locationsMapper;

    @Autowired
    private WorkTasksMapper workTasksMapper;

    @Autowired
    private TaskCheckpointsMapper taskCheckpointsMapper;

    @Override
    @Transactional
    public void generateInboundTasks(WorkTasks inboundTask) {
        // 1. 获取入库货物的类别
        Products product = productsMapper.selectById(inboundTask.getProductId());
        String category = product.getCategory();

        // 2. 查找匹配的分配规则
        LambdaQueryWrapper<AssignmentRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssignmentRule::getProductCategory, category)
                .eq(AssignmentRule::getIsActive, true)
                .orderByDesc(AssignmentRule::getPriority);
        List<AssignmentRule> rules = assignmentRuleMapper.selectList(queryWrapper);

        if (rules.isEmpty()) {
            // 如果没有找到规则，可以抛出异常或记录日志
            // 这里我们暂时只打印日志
            System.out.println("No assignment rule found for category: " + category);
            return;
        }

        AssignmentRule rule = rules.get(0); // 取优先级最高的规则

        // 3. 在目标区域内寻找空闲库位并生成移库任务
        LambdaQueryWrapper<Locations> locationQueryWrapper = new LambdaQueryWrapper<>();
        locationQueryWrapper.eq(Locations::getParentId, rule.getTargetZoneId())
                .eq(Locations::getIsOccupied, false)
                .last("LIMIT " + inboundTask.getTargetQuantity());

        List<Locations> freeLocations = locationsMapper.selectList(locationQueryWrapper);

        if (freeLocations.size() < inboundTask.getTargetQuantity()) {
            // 如果空闲库位不足，可以抛出异常或记录日志
            System.out.println("Not enough free locations in zone: " + rule.getTargetZoneId());
            return;
        }

        for (int i = 0; i < inboundTask.getTargetQuantity(); i++) {
            WorkTasks transferTask = new WorkTasks();
            transferTask.setTaskType("TRANSFER");
            transferTask.setProductId(inboundTask.getProductId());
            transferTask.setTargetQuantity(1); // 每个任务搬运一个
            transferTask.setFromLocationId(inboundTask.getToLocationId()); // 从入库区搬出
            transferTask.setToLocationId(freeLocations.get(i).getId()); // 搬到分配的库位
            workTasksMapper.insert(transferTask);
        }
    }

    @Override
    public WorkTasks assignTaskBySku(String sku, String userId) {
        // 1. 根据 SKU 查询 product_id
        LambdaQueryWrapper<Products> productQueryWrapper = new LambdaQueryWrapper<>();
        productQueryWrapper.eq(Products::getSku, sku);
        Products product = productsMapper.selectOne(productQueryWrapper);

        if (product == null) {
            return null; // or throw exception
        }

        // 2. 查找待分配的移库任务
        LambdaQueryWrapper<WorkTasks> taskQueryWrapper = new LambdaQueryWrapper<>();
        taskQueryWrapper.eq(WorkTasks::getProductId, product.getId())
                .eq(WorkTasks::getStatus, "PENDING")
                .eq(WorkTasks::getTaskType, "TRANSFER")
                .orderByAsc(WorkTasks::getCreatedAt)
                .last("LIMIT 1");

        WorkTasks taskToAssign = workTasksMapper.selectOne(taskQueryWrapper);

        if (taskToAssign != null) {
            // 3. 分配任务
            taskToAssign.setStatus("ASSIGNED");
            taskToAssign.setAssignedUserId(userId);
            workTasksMapper.updateById(taskToAssign);
        }

        return taskToAssign;
    }

    @Override
    @Transactional
    public boolean completeTaskByLocationScan(int taskId, String locationCode, String userId) {
        // 1. 记录扫码操作
        TaskCheckpoints checkpoint = new TaskCheckpoints();
        checkpoint.setTaskId(taskId);
        checkpoint.setUserId(userId);
        checkpoint.setOperationType("SCAN_LOCATION");
        checkpoint.setScannedCode(locationCode);

        // 2. 获取任务信息和正确的库位码
        WorkTasks task = workTasksMapper.selectById(taskId);
        Locations correctLocation = locationsMapper.selectById(task.getToLocationId());

        // 3. 验证库位码
        if (correctLocation.getCode().equals(locationCode)) {
            checkpoint.setIsCorrect(true);

            // 4. 更新任务状态
            task.setStatus("COMPLETED");
            task.setEndTime(new java.sql.Timestamp(System.currentTimeMillis()));
            workTasksMapper.updateById(task);

            // 5. 更新库存
            // (这里简化处理，实际可能需要更复杂的库存更新逻辑)
            // a. 减少源库位库存 (暂存区)
            // b. 增加目标库位库存

            // 6. 更新目标库位状态
            correctLocation.setIsOccupied(true);
            correctLocation.setCurrentProductId(task.getProductId());
            locationsMapper.updateById(correctLocation);

            taskCheckpointsMapper.insert(checkpoint);
            return true;
        } else {
            checkpoint.setIsCorrect(false);
            taskCheckpointsMapper.insert(checkpoint);
            return false;
        }
    }
}
