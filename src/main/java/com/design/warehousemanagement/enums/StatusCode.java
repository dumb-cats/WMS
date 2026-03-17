package com.design.warehousemanagement.enums;

import lombok.Getter;

/**
 * 状态码枚举，用于统一返回给前端的响应状态。
 * 包括通用 HTTP 状态码、用户相关状态码、数据操作相关状态码等。
 * @author wwp
 */
@Getter
public enum StatusCode {
    /**
     * 请求成功，HTTP 状态码 200。
     */
    SUCCESS(200, "请求成功"),

    /**
     * 错误的请求，客户端发送了服务器无法处理的请求，HTTP 状态码 400。
     */
    BAD_REQUEST(400, "错误的请求"),

    /**
     * 未授权访问，缺少有效身份认证信息，HTTP 状态码 401。
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问，服务器理解请求，但拒绝执行，HTTP 状态码 403。
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 未找到资源，请求的资源不存在，HTTP 状态码 404。
     */
    NOT_FOUND(404, "未找到资源"),

    /**
     * 服务器内部错误，HTTP 状态码 500。
     */
    INTERNAL_ERROR(500, "服务器内部错误"),


    // ===== 用户相关状态码 =====

    /**
     * 用户不存在，常用于用户查询失败时返回。
     */
    USER_NOT_FOUND(404, "用户不存在"),

    /**
     * 用户名已存在，注册时用户名重复。
     */
    USERNAME_EXISTS(400, "用户名已存在"),

    /**
     * 手机号已存在，注册或绑定手机号时冲突。
     */
    PHONE_NUMBER_EXISTS(400, "手机号已存在"),

    /**
     * 身份证号已存在，注册或绑定身份证号时冲突。
     */
    ID_CARD_EXISTS(400, "身份证号已存在"),

    /**
     * 用户输入无效，参数校验不通过。
     */
    INVALID_USER_INPUT(400, "用户输入无效"),


    // ===== 数据操作相关状态码 =====

    /**
     * 数据插入失败，数据库操作异常。
     */
    DATA_INSERT_FAILED(500, "数据插入失败"),

    /**
     * 数据更新失败，数据库操作异常。
     */
    DATA_UPDATE_FAILED(500, "数据更新失败"),

    /**
     * 数据删除失败，数据库操作异常。
     */
    DATA_DELETE_FAILED(500, "数据删除失败"),

    /**
     * 数据查询失败，数据库操作异常。
     */
    DATA_QUERY_FAILED(500, "数据查询失败");


    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据状态码获取对应的枚举。
     *
     * @param code 状态码值
     * @return 对应的 StatusCode 枚举
     * @throws IllegalArgumentException 如果找不到匹配的状态码
     */
    public static StatusCode fromCode(int code) {
        for (StatusCode status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的状态码: " + code);
    }
}