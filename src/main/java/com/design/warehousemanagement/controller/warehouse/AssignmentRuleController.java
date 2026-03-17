package com.design.warehousemanagement.controller.warehouse;

import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.pojo.AssignmentRule;
import com.design.warehousemanagement.service.AssignmentRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse/assignment-rule")
public class AssignmentRuleController {

    @Autowired
    private AssignmentRuleService assignmentRuleService;

    @GetMapping("/list")
    public Result<List<AssignmentRule>> list() {
        return Result.success(assignmentRuleService.list());
    }

    @PostMapping
    public Result<?> add(@RequestBody AssignmentRule assignmentRule) {
        assignmentRuleService.save(assignmentRule);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody AssignmentRule assignmentRule) {
        assignmentRuleService.updateById(assignmentRule);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        assignmentRuleService.removeById(id);
        return Result.success();
    }
}
