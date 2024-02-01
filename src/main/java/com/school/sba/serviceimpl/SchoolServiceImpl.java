package com.school.sba.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.School;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exceptionhandler.DataAlreadyDeletedException;
import com.school.sba.exceptionhandler.IllegalRequestException;
import com.school.sba.exceptionhandler.SchoolNotFoundByIdException;
import com.school.sba.exceptionhandler.UnauthorizedAccessException;
import com.school.sba.exceptionhandler.UserNotFoundByUserNameException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepository;
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
	private ClassHourRepository classHourRepo;

	@Autowired
	private AcademicProgramRepo academicProgramRepo;

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
	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(SchoolRequest schoolRequest) {
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
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepo.findByUserName(userName).map(user -> {
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

		}).orElseThrow(() -> new UserNotFoundByUserNameException("Present User Details Not Matching With ADMIN"));
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> deleteSchoolById(int schoolId) {
		School school = schoolRepo.findById(schoolId)
				.orElseThrow(() -> new SchoolNotFoundByIdException("School With Given Id Not Found"));
		if (!school.isDeleted()) {
			if (school.isDeleted() == false)
				school.setDeleted(true);
			schoolRepo.save(school);
			ResponseStructure<String> responseStructure = new ResponseStructure<>();
			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setMessage("School Deleted Successfully");
			responseStructure.setData("School With given Id : " + schoolId + " is Successfully Deleted");

			return new ResponseEntity<ResponseStructure<String>>(responseStructure, HttpStatus.OK);
		} else {
			throw new DataAlreadyDeletedException("School Data Already Deleted ");
		}
	}

	@Override
	public void deleteSchoolPerminently() {
		List<School> schools = schoolRepo.findByIsDeletedTrue();
		if (!schools.isEmpty()) {
			for (School school : schools) {
				List<AcademicProgram> academicPrograms = academicProgramRepo.findBySchool(school);
				for (AcademicProgram academicProgram : academicPrograms) {
					List<ClassHour> classHours = academicProgram.getClassHours();
					classHourRepo.deleteAllInBatch(classHours);
				}
				academicProgramRepo.deleteAllInBatch(academicPrograms);

				List<User> deleteUsersList = userRepo.findByUserRoleNot(UserRole.ADMIN);
				userRepo.deleteAllInBatch(deleteUsersList);
			}
			User user = userRepo.findByUserRole(UserRole.ADMIN);
			user.setSchool(null);
			userRepo.save(user);
			schoolRepo.deleteAllInBatch(schools);
		}
	}

}
