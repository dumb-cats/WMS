package com.design.warehousemanagement.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * @author wwp
 */
@Data
public class UserDTO {
    @Schema(description = "用户ID")
    private String userId;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "姓名")
    private String name;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "身份证号")
    private String idCardNumber;
    @Schema(description = "手机号")
    private String phoneNumber;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "地址")
    private String address;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "出生日期")
    private LocalDate dateOfBirth;
    @Schema(description = "权限 0--学员 1--教练 2--管理员")
    private Integer roleId;
}