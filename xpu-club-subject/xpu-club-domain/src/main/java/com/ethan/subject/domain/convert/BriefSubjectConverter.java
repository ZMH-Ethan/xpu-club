package com.ethan.subject.domain.convert;

import com.ethan.subject.domain.entity.SubjectAnswerBO;
import com.ethan.subject.domain.entity.SubjectInfoBO;
import com.ethan.subject.infra.basic.entity.SubjectBrief;
import com.ethan.subject.infra.basic.entity.SubjectMultiple;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BriefSubjectConverter {

    BriefSubjectConverter INSTANCE = Mappers.getMapper(BriefSubjectConverter.class);

    SubjectBrief convertBoToEntity(SubjectInfoBO subjectInfoBO);

}
