package com.design.warehousemanagement.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ${王遗芃}
 * @date 2025/5/15 15:32
 */
@Data
public class CaptchaDTO {
    private String uuid;
    private byte[] captchaImage;
    private Integer answer;
    private LocalDateTime expiredTime;
    private Boolean used;
}