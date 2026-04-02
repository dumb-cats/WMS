package com.design.warehousemanagement.service.wms;

import com.design.warehousemanagement.pojo.dto.inbound.InboundBatchImportRequest;
import com.design.warehousemanagement.pojo.dto.inbound.InboundOrderCreateRequest;
import com.design.warehousemanagement.pojo.vo.inbound.InboundBatchImportResultVO;
import com.design.warehousemanagement.pojo.vo.inbound.InboundCreateResultVO;
import org.springframework.web.multipart.MultipartFile;

public interface InboundOrderService {

    InboundCreateResultVO createOrder(InboundOrderCreateRequest request);

    InboundBatchImportResultVO batchImport(InboundBatchImportRequest request);

    InboundBatchImportResultVO batchImportFromFile(MultipartFile file);
}
