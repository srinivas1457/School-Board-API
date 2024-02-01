package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ErrorResponse;
import com.school.sba.util.ResponseStructure;
import com.school.sba.util.ResponseStructure2;

@RestController
public class ClassHourController {
	@Autowired
	private ClassHourService classHourService;
	
	@PostMapping("/academic-program/{academicProgramId}/class-hours")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<String>> addClassHoursToAcademicProgram(@PathVariable int academicProgramId,@RequestBody ClassHourRequest classHourRequest){
		return classHourService.addClassHoursToAcademicProgram(academicProgramId,classHourRequest);
	}
	
	@PutMapping("/class-hours")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure2<List<ClassHourResponse>, List<ErrorResponse>>> updateClassHour(@RequestBody List<ClassHourRequest> classHourRequestList){
		return classHourService.updateClassHour(classHourRequestList);
	}
	
	@PutMapping("/academic-program/{academicProgramId}/class-hours")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<String>> generateNextWeekClassHours(@PathVariable int academicProgramId){
		return classHourService.generateNextWeekClassHours(academicProgramId);
		
	}
	
}
