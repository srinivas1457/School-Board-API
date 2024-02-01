package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.enums.UserRole;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

public interface UserService {
	public ResponseEntity<ResponseStructure<UserResponse>> userRegistration(UserRequest userRequest);
	public ResponseEntity<ResponseStructure<UserResponse>> findUserById(int userId);
	public ResponseEntity<ResponseStructure<String>> deleteUserById(int userId);
	public ResponseEntity<ResponseStructure<UserResponse>> setUserToAcademicProgram(int academicProgramId, int userId);
	public ResponseEntity<ResponseStructure<UserResponse>> setSubjectToTeacher(int subjectId, int userId);
	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUser(@Valid UserRequest userRequest);
	public ResponseEntity<ResponseStructure<List<UserResponse>>> findUsersByRoleByAcademicProgram(int academicProgramId,
			UserRole userRole);
	void deleteUsersPerminently();
}
