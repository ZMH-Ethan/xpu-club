package com.ethan.subject.domain.convert;

import com.ethan.subject.domain.entity.SubjectAnswerBO;
import com.ethan.subject.domain.entity.SubjectInfoBO;
import com.ethan.subject.domain.entity.SubjectOptionBO;
import com.ethan.subject.infra.basic.entity.SubjectBrief;
import com.ethan.subject.infra.basic.entity.SubjectJudge;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface JudgeSubjectConverter {

    JudgeSubjectConverter INSTANCE = Mappers.getMapper(JudgeSubjectConverter.class);

    List<SubjectAnswerBO> convertEntityToBoList(List<SubjectJudge> subjectJudgeList);

}
