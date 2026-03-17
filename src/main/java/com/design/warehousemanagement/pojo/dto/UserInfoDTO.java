package com.design.warehousemanagement.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 根据instructor_id查询用户信息（教练端）返回参
 */
@Data
public class UserInfoDTO {
    @Schema(description = "用户唯一标识符 (UUID)")
    private String userId;

    @Schema(description = "用户登录名")
    private String username;

    @Schema(description = "用户真实姓名")
    private String name;

    @Schema(description = "性别", example = "男 / 女")
    private String gender;

    @Schema(description = "身份证号码")
    private String idCardNumber;

    @Schema(description = "手机号码")
    private String phoneNumber;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "家庭住址")
    private String address;

    @Schema(description = "出生日期")
    private LocalDate dateOfBirth;

    @Schema(description = "是否已验证身份", example = "true / false")
    private Boolean isVerified;

    @Schema(description = "创建时间")
    private LocalDate createdAt;

    @Schema(description = "最后更新时间")
    private LocalDate updatedAt;

    @Schema(description = "科目名称")
    private String courseName;

    @Schema(description = "已完成学时")
    private String completedHours;

    @Schema(description = "总学时")
    private String totalHours;
}
