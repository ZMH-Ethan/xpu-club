package com.ethan.subject.common.enums;

// 引入 Lombok 的 Getter 注解，该注解可以自动为枚举的字段生成 getter 方法。
import lombok.Getter;

/**
 * 操作结果的状态码枚举
 * 这个枚举类定义了一组操作结果的状态码和对应的描述信息。
 */
@Getter
public enum ResultCodeEnum {
    // 成功的状态码和描述
    SUCCESS(200, "成功"),
    // 失败的状态码和描述
    FAIL(500, "失败");

    // 枚举实例的字段，存储状态码
    private int code;
    // 枚举实例的字段，存储描述信息
    private String desc;

    // 枚举构造函数，用于初始化状态码和描述信息
    ResultCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取对应的 ResultCodeEnum 枚举实例
     * @param codeVal 要查询的状态码
     * @return 如果找到对应的枚举实例，则返回该实例；否则返回 null。
     */
    public static ResultCodeEnum getByCode(int codeVal) {
        for (ResultCodeEnum resultCodeEnum : ResultCodeEnum.values()) {
            if (resultCodeEnum.code == codeVal) {
                return resultCodeEnum;
            }
        }
        return null;
    }
}