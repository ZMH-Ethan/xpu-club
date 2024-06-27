package com.ethan.subject.domain.convert;

import com.ethan.subject.domain.entity.SubjectAnswerBO;
import com.ethan.subject.domain.entity.SubjectInfoBO;
import com.ethan.subject.infra.basic.entity.SubjectInfo;
import com.ethan.subject.infra.basic.entity.SubjectMultiple;
import com.ethan.subject.infra.basic.entity.SubjectRadio;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RadioSubjectConverter {

    RadioSubjectConverter INSTANCE = Mappers.getMapper(RadioSubjectConverter.class);

    SubjectRadio convertBoToEntity(SubjectAnswerBO subjectAnswerBO);

    List<SubjectAnswerBO> convertEntityToBoList(List<SubjectRadio> subjectRadioList);

}
