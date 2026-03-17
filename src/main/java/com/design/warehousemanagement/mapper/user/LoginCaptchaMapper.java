package com.design.warehousemanagement.mapper.user;

import com.design.warehousemanagement.pojo.dto.CaptchaDTO;
import com.design.warehousemanagement.pojo.LoginCaptcha;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Administrator
* @description 针对表【login_captcha(登录用加减法验证码表)】的数据库操作Mapper
* @createDate 2025-05-15 15:30:18
* @Entity com.wwp.drivingschool.pojo.LoginCaptcha
*/
@Mapper
public interface LoginCaptchaMapper extends BaseMapper<LoginCaptcha> {

    void saveCaptcha(@Param("dto") CaptchaDTO dto);

    void markAsUsed(String uuid);

    CaptchaDTO getCaptchaByUuid(String uuid);
}




