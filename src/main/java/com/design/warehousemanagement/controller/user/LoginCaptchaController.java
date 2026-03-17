package com.design.warehousemanagement.controller.user;

import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.pojo.dto.CaptchaDTO;
import com.design.warehousemanagement.mapper.user.LoginCaptchaMapper;
import com.design.warehousemanagement.util.CaptchaUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ${王遗芃}
 * @date 2025/5/15 15:31
 */
@Tag( name = "登录用加减法验证码" )
@RestController
@RequiredArgsConstructor
@RequestMapping("/login_captcha")
public class LoginCaptchaController {

    private final LoginCaptchaMapper captchaMapper;

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Result getCaptcha() throws Exception {
        Map<String, Object> captchaInfo = CaptchaUtils.generateMathCaptcha();
        String question = (String) captchaInfo.get("question");
        int answer = (int) captchaInfo.get("answer");
        String uuid = UUID.randomUUID().toString();
        BufferedImage image = CaptchaUtils.createCaptchaImage(question);
        byte[] imageBytes = CaptchaUtils.convertImageToBytes(image);
        CaptchaDTO dto = new CaptchaDTO();
        dto.setUuid(uuid);
        dto.setCaptchaImage(imageBytes);
        dto.setAnswer(answer);
        dto.setExpiredTime(LocalDateTime.now().plusMinutes(5));
        dto.setUsed(false);
        captchaMapper.saveCaptcha(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("image",  Base64.getEncoder().encodeToString(imageBytes));
        return Result.success(result);
    }
}
