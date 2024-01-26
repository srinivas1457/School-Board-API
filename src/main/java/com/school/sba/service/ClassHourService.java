package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService {

	public ResponseEntity<ResponseStructure<String>> addClassHoursToAcademicProgram(int academicProgramId,
			ClassHourRequest classHourRequest);

}
