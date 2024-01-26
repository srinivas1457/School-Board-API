package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

public interface UserService {
	public ResponseEntity<ResponseStructure<UserResponse>> userRegistration(UserRequest userRequest);
	public ResponseEntity<ResponseStructure<UserResponse>> findUserById(int userId);
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(int userId);
	public ResponseEntity<ResponseStructure<UserResponse>> setUserToAcademicProgram(int academicProgramId, int userId);
	public ResponseEntity<ResponseStructure<UserResponse>> setSubjectToTeacher(int subjectId, int userId);
	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUser(@Valid UserRequest userRequest);

}
