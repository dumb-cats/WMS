package com.design.warehousemanagement.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class ResetPasswordDTO {
    @Schema(description = "昵称")
    @NotBlank(message = "昵称不能为空")
    private String username;

    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号")
    private String phoneNumber;

    @NotBlank(message = "邮箱不能为空")
    @Schema(description = "邮箱")
    private String email;
}