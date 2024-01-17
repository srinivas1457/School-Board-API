package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.util.ResponseStructure;

public interface SchoolService {
	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(SchoolRequest schoolRequest,int userId);
//	public ResponseEntity<ResponseStructure<SchoolResponse>> findBySchoolId(int schoolId);
//	public ResponseEntity<ResponseStructure<List<SchoolResponse>>> findBySchoolName(String schoolName);
//	public ResponseEntity<ResponseStructure<SchoolResponse>> updateById(SchoolRequest schoolRequest,int schoolId);
//	public ResponseEntity<ResponseStructure<SchoolResponse>> deleteById(int schoolId);
//	public ResponseEntity<ResponseStructure<List<SchoolResponse>>> findAll();
}
