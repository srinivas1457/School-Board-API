package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.util.ResponseStructure;

public interface SubjectService {

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> insertSubjectsIntoAcademicProgram(
			SubjectRequest subjectRequest, int academicProgramId);

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjectsToAcademicProgram(
			SubjectRequest subjectRequest, int academicProgramId);

}
