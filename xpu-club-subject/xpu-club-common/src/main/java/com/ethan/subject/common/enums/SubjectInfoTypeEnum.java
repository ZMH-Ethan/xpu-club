package com.ethan.subject.common.enums;

import lombok.Getter;

/**
 * 题目类型枚举
 * 1单选 2多选 3判断 4简答
 * @author: ChickenWing
 * @date: 2023/10/3
 */
@Getter
public enum SubjectInfoTypeEnum {

    RADIO(1,"单选"),
    MULTIPLE(2,"多选"),
    JUDGE(3,"判断"),
    BRIEF(4,"简答"),
    ;

    // 枚举值
    public int code;
    // 枚举描述
    public String desc;
    // 构造方法
    SubjectInfoTypeEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
    // 根据枚举值获取枚举对象
    public static SubjectInfoTypeEnum getByCode(int codeVal){
        for(SubjectInfoTypeEnum resultCodeEnum : SubjectInfoTypeEnum.values()){
            if(resultCodeEnum.code == codeVal){
                return resultCodeEnum;
            }
        }
        return null;
    }

}
