package com.design.warehousemanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.warehousemanagement.pojo.LoginCaptcha;
import com.design.warehousemanagement.service.LoginCaptchaService;
import com.design.warehousemanagement.mapper.user.LoginCaptchaMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【login_captcha(登录用加减法验证码表)】的数据库操作Service实现
* @createDate 2025-05-15 15:30:18
*/
@Service
public class LoginCaptchaServiceImpl extends ServiceImpl<LoginCaptchaMapper, LoginCaptcha>
    implements LoginCaptchaService{

}




