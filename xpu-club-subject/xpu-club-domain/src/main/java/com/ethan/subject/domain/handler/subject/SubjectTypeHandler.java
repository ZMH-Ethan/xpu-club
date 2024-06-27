package com.ethan.subject.domain.handler.subject;

import com.ethan.subject.common.enums.SubjectInfoTypeEnum;
import com.ethan.subject.domain.entity.SubjectInfoBO;
import com.ethan.subject.domain.entity.SubjectOptionBO;

// 题目类型处理器接口
public interface SubjectTypeHandler {

    /**
     * 枚举身份的识别
     */
    SubjectInfoTypeEnum getHandlerType();

    /**
     * 实际的题目的插入
     */
    void add(SubjectInfoBO subjectInfoBO);

    /**
     * 实际的题目的插入
     */
    SubjectOptionBO query(int subjectId);

}
