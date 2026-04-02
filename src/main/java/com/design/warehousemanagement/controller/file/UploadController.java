/*
package com.design.warehousemanagement.controller.file;

import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.util.AliOSSUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

*/
/**
 * @author wwp
 *//*

@Tag(name = "文件流")
@Log4j2
@RestController
@RequiredArgsConstructor
public class UploadController {

    private final AliOSSUtils aliOSSUtils;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("上传图片文件名:{}", file.getOriginalFilename());
        String url = aliOSSUtils.upload(file);
        log.info("上传完毕,url为:{}", url);
        return Result.success(url);
    }
}
*/
