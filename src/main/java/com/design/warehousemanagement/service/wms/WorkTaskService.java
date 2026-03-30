package com.design.warehousemanagement.service.wms;

import com.design.warehousemanagement.pojo.dto.task.InboundTaskGenerateRequest;
import com.design.warehousemanagement.pojo.vo.task.InboundTaskGenerateResultVO;

public interface WorkTaskService {

    InboundTaskGenerateResultVO generateInboundTasks(Long inboundOrderId, InboundTaskGenerateRequest request);
}
