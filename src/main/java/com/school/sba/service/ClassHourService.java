package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.util.ErrorResponse;
import com.school.sba.util.ResponseStructure;
import com.school.sba.util.ResponseStructure2;

public interface ClassHourService {

	public ResponseEntity<ResponseStructure<String>> addClassHoursToAcademicProgram(int academicProgramId);

	public ResponseEntity<ResponseStructure2<List<ClassHourResponse>, List<ErrorResponse>>> updateClassHour(List<ClassHourRequest> classHourRequestList);


	void autoGenerateWeeklyClassHours();
	
}
