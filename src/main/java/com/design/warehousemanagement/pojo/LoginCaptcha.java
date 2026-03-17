package com.design.warehousemanagement.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 登录用加减法验证码表
 * @TableName login_captcha
 */
@TableName(value ="login_captcha")
@Data
public class LoginCaptcha {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 验证码唯一标识（UUID）
     */
    private String uuid;

    /**
     * 验证码正确答案（如：5 + 3 = 8，则 answer=8）
     */
    private Integer answer;

    /**
     * 验证码过期时间（例如：当前时间 + 5分钟）
     */
    private LocalDateTime expiredTime;

    /**
     * 是否已被使用（0:未使用，1:已使用）
     */
    private Integer used;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 验证码图片（PNG格式二进制数据）
     */
    private byte[] captchaImage;
}