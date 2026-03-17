package com.design.warehousemanagement.common;

import com.design.warehousemanagement.enums.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwp
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    /**
     * 成功返回结果（无数据）
     */
    public static Result success() {
        return success(null);
    }

    /**
     * 成功返回结果（带数据）
     */
    public static Result success(Object data) {
        StatusCode status = StatusCode.SUCCESS;
        return new Result(status.getCode(), status.getMessage(), data);
    }

    /**
     * 错误返回结果（默认系统错误）
     */
    public static Result error() {
        StatusCode status = StatusCode.INTERNAL_ERROR;
        return new Result(status.getCode(), status.getMessage(), null);
    }

    /**
     * 错误返回结果（自定义错误信息）
     */
    public static Result error(String msg) {
        StatusCode status = StatusCode.BAD_REQUEST;
        return new Result(status.getCode(), msg, null);
    }

    /**
     * 根据枚举生成返回结果
     */
    public static Result of(StatusCode statusCode) {
        return new Result(statusCode.getCode(), statusCode.getMessage(), null);
    }

    /**
     * 根据枚举生成返回结果（带数据）
     */
    public static Result of(StatusCode statusCode, Object data) {
        return new Result(statusCode.getCode(), statusCode.getMessage(), data);
    }

    /**
     * 根据枚举生成返回结果（带自定义错误信息）
     */
    public static Result of(StatusCode statusCode, String customMsg) {
        return new Result(statusCode.getCode(), customMsg, null);
    }

    /**
     * 根据枚举生成返回结果（带自定义错误信息和数据）
     */
    public static Result of(StatusCode statusCode, String customMsg, Object data) {
        return new Result(statusCode.getCode(), customMsg, data);
    }

    /**
     * 判断是否为成功状态
     */
    public boolean isSuccess() {
        return this.code == StatusCode.SUCCESS.getCode();
    }

    /**
     * 判断是否为错误状态
     */
    public boolean isError() {
        return !isSuccess();
    }
}