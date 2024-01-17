package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(@RequestBody SchoolRequest schoolRequest,@PathVariable int userId){
		return schoolService.addSchool(schoolRequest,userId);
	}
//	@GetMapping("/schools/{schoolId}")
//	public ResponseEntity<ResponseStructure<SchoolResponse>> findBySchoolId(@PathVariable int schoolId){
//		return schoolService.findBySchoolId(schoolId);
//	}
//	@GetMapping("/school-names/{schoolName}/schools")
//	public ResponseEntity<ResponseStructure<List<SchoolResponse>>> findBySchoolName(String schoolName){
//		return schoolService.findBySchoolName(schoolName);
//	}
//	@PutMapping("/{schoolId}/schools")
//	public ResponseEntity<ResponseStructure<SchoolResponse>> updateById(@RequestBody SchoolRequest schoolRequest,@PathVariable int schoolId){
//		return schoolService.updateById(schoolRequest, schoolId);
//	}
//	@DeleteMapping("/schools/{schoolId}")
//	public ResponseEntity<ResponseStructure<SchoolResponse>> deleteById(@PathVariable int schoolId){
//		return schoolService.deleteById(schoolId);
//	}
//	
//	@GetMapping("/schools")
//	public ResponseEntity<ResponseStructure<List<SchoolResponse>>> findAll(){
//		return schoolService.findAll();
//	}
}
