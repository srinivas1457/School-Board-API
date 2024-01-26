package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@RestController
public class ClassHourController {
	@Autowired
	private ClassHourService classHourService;
	
	@PostMapping("/academic-program/{academicProgramId}/class-hours")
	public ResponseEntity<ResponseStructure<String>> addClassHoursToAcademicProgram(@PathVariable int academicProgramId,@RequestBody ClassHourRequest classHourRequest){
		return classHourService.addClassHoursToAcademicProgram(academicProgramId,classHourRequest);
	}
}
