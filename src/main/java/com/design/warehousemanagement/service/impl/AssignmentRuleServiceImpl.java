package com.design.warehousemanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.warehousemanagement.mapper.warehouse.AssignmentRuleMapper;
import com.design.warehousemanagement.pojo.AssignmentRule;
import com.design.warehousemanagement.service.AssignmentRuleService;
import org.springframework.stereotype.Service;

@Service
public class AssignmentRuleServiceImpl extends ServiceImpl<AssignmentRuleMapper, AssignmentRule> implements AssignmentRuleService {
}
