package com.design.warehousemanagement.controller.wms;

import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.pojo.dto.inbound.InboundBatchImportRequest;
import com.design.warehousemanagement.pojo.dto.inbound.InboundOrderCreateRequest;
import com.design.warehousemanagement.service.wms.InboundOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "入库管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/wms/inbound-orders")
public class InboundOrderController {

    private final InboundOrderService inboundOrderService;

    @Operation(summary = "手动创建入库单")
    @PostMapping("/manual")
    public Result manualCreate(@Valid @RequestBody InboundOrderCreateRequest request) {
        return Result.success(inboundOrderService.createOrder(request));
    }

    @Operation(summary = "批量导入入库单（JSON）")
    @PostMapping("/batch-import")
    public Result batchImport(@Valid @RequestBody InboundBatchImportRequest request) {
        return Result.success(inboundOrderService.batchImport(request));
    }

    @Operation(summary = "批量导入入库单（CSV文件）")
    @PostMapping(value = "/batch-import/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result batchImportByFile(@RequestParam("file") MultipartFile file) {
        return Result.success(inboundOrderService.batchImportFromCsv(file));
    }
}
