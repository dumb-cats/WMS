package com.design.warehousemanagement.pojo.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wwp
 */
@Data
public class UserVO {
    @Schema(description = "用户ID")
    private String userId;
    @Schema(description = "用户名")
    private String username;
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
    @Schema(description = "出生日期")
    private String dateOfBirth;
    @Schema(description = "是否已认证")
    private Boolean isVerified;
    @Schema(description = "头像")
    private String createdAt;
    @Schema(description = "创建时间")
    private String updatedAt;
    @Schema(description = "权限 0--学员 1--教练 2--管理员")
    private Integer roleId;
}