package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.enums.UserRole;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@RestController
public class AcademicProgramController {

	@Autowired
	private AcademicProgramService academicProgramService;

	@PostMapping("/schools/{schoolId}/academic-programs")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> insertAcademicProgram(@PathVariable int schoolId,
			@RequestBody AcademicProgramRequest academicProgramRequest) {
		return academicProgramService.insertAcademicProgram(schoolId, academicProgramRequest);
	}

	@GetMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAcademicProgramsBySchoolId(
			@PathVariable int schoolId) {
		return academicProgramService.findAcademicProgramsBySchoolId(schoolId);
	}
	
	@DeleteMapping("academic-programs/{academicProgramId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<String>> deleteAcademicProgramById(@PathVariable int academicProgramId){
		return academicProgramService.deleteAcademicProgramById(academicProgramId);
	}

}
