package com.ethan.subject.common.entity;

// 引入 ResultCodeEnum 枚举，该枚举可能定义了各种操作结果的状态码和描述信息。
import com.ethan.subject.common.enums.ResultCodeEnum;

// 引入 Lombok 的 Data 注解，该注解可以自动为类生成 getter、setter、equals、hashCode、toString 等方法。
import lombok.Data;

/**
 * 统一API响应结果封装
 * 这个类用于定义 API 响应的统一格式，包括操作成功与否的标志、状态码、消息和返回的数据。
 */
@Data
public class Result<T> {
    // 操作是否成功的标志
    private Boolean success;

    // 操作结果的状态码
    private Integer code;

    // 操作结果的描述信息
    private String message;

    // 泛型数据，可以是任何类型的返回数据
    private T data;

    /**
     * 构造一个成功的响应结果
     * @return 一个设置了成功标志、成功状态码和描述的 Result 对象
     */
    public static Result ok(){
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(ResultCodeEnum.SUCCESS.getCode()); // 设置成功的状态码
        result.setMessage(ResultCodeEnum.SUCCESS.getDesc()); // 设置成功的描述信息
        return result;
    }

    /**
     * 构造一个带有数据的成功响应结果
     * @param data 返回的数据
     * @return 一个带有数据的成功的 Result 对象
     */
    public static <T> Result ok(T data){
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(ResultCodeEnum.SUCCESS.getDesc());
        result.setData(data); // 设置返回的数据
        return result;
    }

    /**
     * 构造一个失败的响应结果
     * @return 一个设置了失败标志、失败状态码和描述的 Result 对象
     */
    public static Result fail(){
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCodeEnum.FAIL.getCode()); // 设置失败的状态码
        result.setMessage(ResultCodeEnum.FAIL.getDesc()); // 设置失败的描述信息
        return result;
    }

    /**
     * 构造一个带有数据的失败响应结果
     * @param data 返回的数据
     * @return 一个带有数据的失败的 Result 对象
     */
    public static <T> Result fail(T data){
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCodeEnum.FAIL.getCode());
        result.setMessage(ResultCodeEnum.FAIL.getDesc());
        result.setData(data); // 设置返回的数据
        return result;
    }
}