package com.school.sba.serviceimpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exceptionhandler.DataNotPresentException;
import com.school.sba.exceptionhandler.IllegalRequestException;
import com.school.sba.exceptionhandler.SchoolNotFoundByIdException;
import com.school.sba.exceptionhandler.SchoolNotFoundByNameException;
import com.school.sba.exceptionhandler.UnauthorizedAccessException;
import com.school.sba.exceptionhandler.UserNotFoundByIdException;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@Service
public class SchoolServiceImpl implements SchoolService {

	@Autowired
	private SchoolRepository schoolRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ResponseStructure<SchoolResponse> responseStructure;

	private School mapToSchool(SchoolRequest schoolRequest) {
		return School.builder().schoolName(schoolRequest.getSchoolName()).email(schoolRequest.getEmail())
				.contactNum(schoolRequest.getContactNum()).address(schoolRequest.getAddress()).build();
	}

	private SchoolResponse mapToSchoolResponse(School school) {
		return SchoolResponse.builder().schoolId(school.getSchoolId()).schoolName(school.getSchoolName())
				.email(school.getEmail()).contactNum(school.getContactNum()).address(school.getAddress()).build();
	}


	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(SchoolRequest schoolRequest, int userId) {
		/* 1st Approach */
//		User user = userRepo.findById(userId)
//				.orElseThrow(() -> new UserNotFoundByIdException("User With Given Id Not Present"));
//		if (user.getUserRole() == UserRole.ADMIN) {
//			School school = mapToSchool(schoolRequest);
//			 school = schoolRepo.save(school);
//
//			SchoolResponse schoolResponse = mapToSchoolResponse(school);
//
//			responseStructure.setStatusCode(HttpStatus.CREATED.value());
//			responseStructure.setMessage("School Data Inserted Successfully");
//			responseStructure.setData(schoolResponse);
//
//			return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure, HttpStatus.CREATED);
//		} else {
//			throw new UnauthorizedAccessException("Failed To Create School");
//		}

		/* 2nd Approach */

		return userRepo.findById(userId).map(user -> {
			if (user.getUserRole().equals(UserRole.ADMIN)) {
				if (user.getSchool() == null) {
					School school = mapToSchool(schoolRequest);
					school = schoolRepo.save(school);
					user.setSchool(school);
					userRepo.save(user);

					responseStructure.setStatusCode(HttpStatus.CREATED.value());
					responseStructure.setMessage("School Data saved Successfully");
					responseStructure.setData(mapToSchoolResponse(school));

					return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure, HttpStatus.CREATED);

				} else
					throw new IllegalRequestException("School Data Already Exist");
			} else
				throw new UnauthorizedAccessException("Failed To Create School");

		}).orElseThrow(() -> new UserNotFoundByIdException("User With Given Id Not Present"));
	}

}
