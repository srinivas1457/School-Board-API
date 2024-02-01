package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@RestController
public class SchoolController {
	@Autowired
	private SchoolService schoolService;
	
	@PostMapping("/users/{userId}/schools")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(@RequestBody SchoolRequest schoolRequest){
		return schoolService.addSchool(schoolRequest);
	}
	
	@DeleteMapping("schools/{schoolId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<String>> deleteSchoolById(@PathVariable int schoolId){
		return schoolService.deleteSchoolById(schoolId);
	}

}
