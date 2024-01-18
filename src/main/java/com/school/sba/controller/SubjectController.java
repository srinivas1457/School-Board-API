package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.util.ResponseStructure;

@RestController
public class SubjectController {
	
	@Autowired
	private SubjectService subjectService;
	
	@PostMapping("/academic-programs/{academicProgramId}/subjects")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> insertSubjectsIntoAcademicProgram(@RequestBody SubjectRequest subjectRequest,@PathVariable int academicProgramId){
		return subjectService.insertSubjectsIntoAcademicProgram(subjectRequest,academicProgramId);
	}
	
	@PutMapping("/academic-programs/{academicProgramId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjectsToAcademicProgram(@RequestBody SubjectRequest subjectRequest,@PathVariable int academicProgramId){
		return subjectService.updateSubjectsToAcademicProgram(subjectRequest,academicProgramId);
	}

}
