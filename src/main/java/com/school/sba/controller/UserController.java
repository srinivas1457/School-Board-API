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
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> userRegistration(@RequestBody @Valid UserRequest userRequest){
		return userService.userRegistration(userRequest);
	}
	
	@PostMapping("/users")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUser(@RequestBody @Valid UserRequest userRequest){
		return userService.addOtherUser (userRequest);
	}
	@GetMapping("users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> findUserById(@PathVariable int userId){
		return userService.findUserById(userId);
	}
	@DeleteMapping("users/{userId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<String>> deleteUserById(@PathVariable int userId){
		return userService.deleteUserById(userId);
	}
	
	@PutMapping("/academic-programs/{academicProgramId}/users/{userId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<UserResponse>> setUserToAcademicProgram(@PathVariable int academicProgramId,@PathVariable int userId){
		return userService.setUserToAcademicProgram(academicProgramId,userId);
	}
	
	@PutMapping("/subjects/{subjectId}/users/{userId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<UserResponse>> setSubjectToTeacher(@PathVariable int subjectId,@PathVariable int userId){
		return userService.setSubjectToTeacher(subjectId,userId);
	}
	
	@GetMapping("/academic-programs/{academicProgramId}/user-roles/{userRole}/users")
	public ResponseEntity<ResponseStructure<List<UserResponse>>> findUsersByRoleByAcademicProgram(@PathVariable int academicProgramId,@PathVariable UserRole userRole){
		return userService.findUsersByRoleByAcademicProgram(academicProgramId,userRole);
	}
}
