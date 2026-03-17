package com.design.warehousemanagement.controller.warehouse;
import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.pojo.WorkTasks;
import com.design.warehousemanagement.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouse/task")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping("/assign")
    public Result<WorkTasks> assignTask(@RequestParam String sku, @RequestHeader("User-Id") String userId) {
        WorkTasks assignedTask = warehouseService.assignTaskBySku(sku, userId);
        if (assignedTask != null) {
            return Result.success(assignedTask);
        }
        return Result.error("No task available for the given SKU");
    }

    @PostMapping("/complete")
    public Result<?> completeTask(@RequestParam int taskId, @RequestParam String locationCode, @RequestHeader("User-Id") String userId) {
        boolean success = warehouseService.completeTaskByLocationScan(taskId, locationCode, userId);
        if (success) {
            return Result.success("Task completed successfully");
        }
        return Result.error("Incorrect location code");
    }
}
