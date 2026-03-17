package com.design.warehousemanagement.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ${王遗芃}
 * @date 2025/5/15 15:46
 */
@Data
public class LoginRequestDTO {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "验证码的UUID")
    private String captchaUuid;
    @Schema(description = "验证码的答案")
    private Integer captchaAnswer;
}
