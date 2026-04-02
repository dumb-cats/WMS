# 认证与用户管理模块详细设计

## 1. 模块概述
本模块提供系统用户认证与用户管理能力，包括登录验证码、JWT 登录、用户 CRUD、分页检索、密码重置等功能。

## 2. 功能详细描述
1. 获取登录验证码（算术题图像）。
2. 用户登录（验证码校验 + 账号密码校验 + JWT签发）。
3. 用户新增、删除、更新、查询、分页查询。
4. 按用户名+手机号+邮箱重置密码。

## 3. 接口设计
### 3.1 验证码接口
- `GET /login_captcha/captcha`
- 返回：`uuid` + `image(base64)`

### 3.2 登录接口
- `POST /users/login/{roleId}`
- 请求：`username/password/captchaUuid/captchaAnswer`
- 返回：JWT Token

### 3.3 用户管理接口
- `POST /users`
- `DELETE /users/{userId}`
- `PUT /users/{userId}`
- `GET /users/{userId}`
- `GET /users`
- `GET /users/search`
- `POST /users/user/reset-password`

## 4. 数据结构设计
- 实体：`Users`
- DTO：`LoginRequestDTO`、`UserDTO`、`ResetPasswordDTO`、`CaptchaDTO`
- VO：`UserVO`
- 返回：统一 `Result`

## 5. 业务流程设计
1. 前端先拉取验证码。
2. 登录时先校验验证码，验证成功后再执行账号密码认证。
3. 认证成功后签发 JWT。
4. 用户管理接口执行对应数据库读写。

## 6. 异常处理设计
- 验证码不存在/过期/错误：返回业务错误信息。
- 登录失败：返回“用户名或密码错误”。
- 用户新增冲突：返回字段冲突提示（用户名/身份证/手机号）。

## 7. 安全设计
1. 登录接口强制验证码校验。
2. JWT 用于会话认证。
3. 重置密码需三要素匹配。
4. 建议后续增加密码加密存储（当前为明文逻辑，需尽快升级）。
