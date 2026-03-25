package com.design.warehousemanagement.controller.user;

import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.enums.StatusCode;
import com.design.warehousemanagement.mapper.user.LoginCaptchaMapper;
import com.design.warehousemanagement.pojo.Users;
import com.design.warehousemanagement.pojo.dto.CaptchaDTO;
import com.design.warehousemanagement.pojo.dto.LoginRequestDTO;
import com.design.warehousemanagement.pojo.dto.ResetPasswordDTO;
import com.design.warehousemanagement.pojo.dto.UserDTO;
import com.design.warehousemanagement.service.UsersService;
import com.design.warehousemanagement.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wwp
 */
@Tag(name = "用户信息")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UsersService userService;
    private final LoginCaptchaMapper captchaMapper;

    @Operation(summary = "增加用户")
    @PostMapping
    public Result addUser(@RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId);
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable String userId, @RequestBody UserDTO userDTO) {
        return userService.updateUser(userId, userDTO);
    }

    @Operation(summary = "查询单个用户")
    @GetMapping("/{userId}")
    public Result getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    @Operation(summary = "查询所有用户")
    @GetMapping
    public Result getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "根据roleId和name分页查询用户")
    @GetMapping("/search")
    public Result searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer roleId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getUsersByName(name, page, size, roleId);
    }

    @Operation(summary = "登录")
    @PostMapping("/login/{roleId}")
    public Result login(
            @RequestBody LoginRequestDTO loginRequest,
            @PathVariable Integer roleId) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String captchaUuid = loginRequest.getCaptchaUuid();
        Integer captchaAnswer = loginRequest.getCaptchaAnswer();
        try {
            validateCaptcha(captchaUuid, captchaAnswer);
            Users user = userService.login(username, password, roleId);
            if (user == null) {
                return Result.error("用户名或密码错误");
            }
            Map<String, Object> claims = buildClaims(user);
            String jwt = JwtUtils.generateJwt(claims);
            return Result.success(jwt);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private void validateCaptcha(String captchaUuid, Integer captchaAnswer) {
        CaptchaDTO captchaDTO = captchaMapper.getCaptchaByUuid(captchaUuid);
        if (captchaDTO == null) {
            captchaMapper.markAsUsed(captchaUuid);
            throw new IllegalArgumentException("验证码不存在或已过期");
        }
        if (!captchaDTO.getAnswer().equals(captchaAnswer)) {
            captchaMapper.markAsUsed(captchaUuid);
            throw new IllegalArgumentException("验证码错误");
        }
        captchaMapper.markAsUsed(captchaUuid);
    }

    private Map<String, Object> buildClaims(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("roleId", user.getRoleId());
        claims.put("username", user.getUsername());
        claims.put("name", user.getName());
        return claims;
    }

    @Operation(summary = "根据用户名、手机号、邮箱重置密码")
    @PostMapping("/user/reset-password")
    public Result resetPassword(@RequestBody ResetPasswordDTO dto) {
        try {
            userService.resetPassword(dto);
            return Result.success("密码已重置为 123456");
        } catch (Exception e) {
            return Result.of(StatusCode.DATA_UPDATE_FAILED, e.getMessage());
        }
    }
}
