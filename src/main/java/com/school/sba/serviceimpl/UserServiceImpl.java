package com.school.sba.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exceptionhandler.AcademicProgramNotFoundByIdException;
import com.school.sba.exceptionhandler.DataAlreadyDeletedException;
import com.school.sba.exceptionhandler.DataNotPresentException;
import com.school.sba.exceptionhandler.IllegalRequestException;
import com.school.sba.exceptionhandler.SchoolNotFoundByIdException;
import com.school.sba.exceptionhandler.SubjectNotFoundByIdException;
import com.school.sba.exceptionhandler.UnauthorizedAccessException;
import com.school.sba.exceptionhandler.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.SubjectRepo;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AcademicProgramRepo academicProgramRepo;

	@Autowired
	private SubjectRepo subjectRepo;

	@Autowired
	private ClassHourRepository classHourRepo;

	@Autowired
	private ResponseStructure<UserResponse> responseStructure;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private SchoolRepository schoolRepo;

	public User mapToUser(UserRequest userRequest) {
		return User.builder().userName(userRequest.getUserName()).password(encoder.encode(userRequest.getPassword()))
				.firstName(userRequest.getFirstName()).lastName(userRequest.getLastName()).email(userRequest.getEmail())
				.dateOfBirth(userRequest.getDateOfBirth()).contactNo(userRequest.getContactNo())
				.userRole(UserRole.valueOf(userRequest.getUserRole())).build();
	}

	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().userId(user.getUserId()).userName(user.getUserName())
				.firstName(user.getFirstName()).lastName(user.getLastName()).contactNo(user.getContactNo())
				.email(user.getEmail()).userRole(user.getUserRole()).isDeleted(user.isDeleted())
				.dateOfBirth(user.getDateOfBirth()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> userRegistration(UserRequest userRequest) {
		User user = mapToUser(userRequest);
		if (user.getUserRole() == UserRole.ADMIN && userRepo.existsByUserRole(UserRole.ADMIN)) {
			responseStructure.setStatusCode(HttpStatus.BAD_REQUEST.value());
			responseStructure.setMessage("Admin user already exists");
			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.BAD_REQUEST);
		}

		if (user.getUserRole() == UserRole.ADMIN) {
			UserResponse userResponse = mapToUserResponse(userRepo.save(user));

			responseStructure.setStatusCode(HttpStatus.CREATED.value());
			responseStructure.setMessage("User Data Successfully Created");
			responseStructure.setData(userResponse);

			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.CREATED);
		} else {
			throw new IllegalRequestException("User Role Not Matching With Admin");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> findUserById(int userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundByIdException("User With Given Id Not Found"));

		UserResponse userResponse = mapToUserResponse(user);

		responseStructure.setStatusCode(HttpStatus.FOUND.value());
		responseStructure.setMessage("User Data found for a given id");
		responseStructure.setData(userResponse);

		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.CREATED);

	}

	@Override
	public ResponseEntity<ResponseStructure<String>> deleteUserById(int userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundByIdException("User With Given Id Not Found"));
		if (!user.isDeleted()) {
			if (user.isDeleted() == false)
				user.setDeleted(true);
			userRepo.save(user);
			ResponseStructure<String> responseStructure = new ResponseStructure<>();
			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setMessage("User Deleted Successfully");
			responseStructure.setData("User With given Id : " + userId + " is Successfully Deleted");

			return new ResponseEntity<ResponseStructure<String>>(responseStructure, HttpStatus.OK);
		} else {
			throw new DataAlreadyDeletedException("User Data Already Deleted ");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> setUserToAcademicProgram(int academicProgramId, int userId) {
		return userRepo.findById(userId).map(user -> {
			AcademicProgram pro = null;
			if (user.getUserRole().equals(UserRole.ADMIN))
				throw new IllegalRequestException("Failed to SET user to THIS PROGRAM");
			else {
				pro = academicProgramRepo.findById(academicProgramId).map(program -> {

					if (user.getUserRole().equals(UserRole.TEACHER)) {

						if (user.getSubject() == null) {
							throw new IllegalRequestException("Teacher should assigned to a SUBJECT");
						}

						if (program.getSubjects() == null || program.getSubjects().isEmpty()) {
							throw new IllegalRequestException("Program should assigned with SUBJECTS to Add TEACHER");
						}

						if (!program.getSubjects().contains(user.getSubject())) {
							throw new IllegalRequestException("Irrelevant TEACHER to the Academic Program");
						}
					}

					user.getAcademicPrograms().add(program);
					userRepo.save(user);
					program.getUsers().add(user);
					program = academicProgramRepo.save(program);
					return program;
				}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Failed to SET user to THIS PROGRAM"));
			}
			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setMessage(user.getUserRole() + " assigned with the Program " + pro.getProgramName());
			responseStructure.setData(mapToUserResponse(user));

			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.OK);
		}).orElseThrow(() -> new UserNotFoundByIdException("Failed to SET user to THIS PROGRAM"));

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> setSubjectToTeacher(int subjectId, int userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundByIdException("User With Given Id Not Found"));
		Subject subject = subjectRepo.findById(subjectId)
				.orElseThrow(() -> new SubjectNotFoundByIdException("Subject With Given Id Not Found"));

		if (user.getUserRole() == UserRole.TEACHER) {

			if (user.getSubject() == null) {
				user.setSubject(subject);
				user = userRepo.save(user);
				UserResponse userResponse = mapToUserResponse(user);
				responseStructure.setStatusCode(HttpStatus.OK.value());
				responseStructure.setMessage("Subject Set To Teacher Successfully");
				responseStructure.setData(userResponse);
				return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.OK);
			} else {
				throw new IllegalRequestException(
						"TEACHER already engaged with the " + user.getSubject().getSubjectName() + " Subject");
			}

		} else
			throw new IllegalRequestException("User role is not matching to teacher");
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUser(@Valid UserRequest userRequest) {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepo.findByUserName(userName).map(user -> {
			return schoolRepo.findById(user.getSchool().getSchoolId()).map(school -> {
				User user2 = mapToUser(userRequest);
				if (user.getUserRole() == UserRole.STUDENT || user.getUserRole() == UserRole.TEACHER) {
					user2.setSchool(school);
					UserResponse userResponse = mapToUserResponse(userRepo.save(user2));

					responseStructure.setStatusCode(HttpStatus.CREATED.value());
					responseStructure.setMessage("User Data Successfully Created");
					responseStructure.setData(userResponse);

					return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.CREATED);
				} else {
					throw new IllegalRequestException("Please Enter User Role As STUDENT Or TEACHER");
				}
			}).orElseThrow(() -> new SchoolNotFoundByIdException("Please Check School Exist Or Not"));

		}).orElseThrow(() -> new UnauthorizedAccessException("Please Login As A ADMIN"));

	}

	@Override
	public ResponseEntity<ResponseStructure<List<UserResponse>>> findUsersByRoleByAcademicProgram(int academicProgramId,
			UserRole userRole) {
		if (UserRole.ADMIN.equals(userRole)) {
			throw new IllegalRequestException(" No Access To Fetch Admin Details");
		}
		return academicProgramRepo.findById(academicProgramId).map(academicProgram -> {

			//////// *** 1 st approach***high time complexity *** it will fetch all users
			//////// data present in academic program list/////
//			List<User> users = academicProgram.getUsers().stream()
//					.filter(user -> user.getUserRole().equals(userRole))
//					.toList();

			///////// *** 2nd Approach *** low Time Complexity ** it will fetch only teacher
			///////// list//////////////

			List<User> users = userRepo.findByUserRoleAndAcademicPrograms_AcademicProgramId(userRole,
					academicProgramId);

			if (users.isEmpty()) {
				throw new DataNotPresentException("No Users found with User Role: " + userRole);
			}

			List<UserResponse> userResponses = users.stream().map(this::mapToUserResponse) // Use method reference
					.toList();

			ResponseStructure<List<UserResponse>> responseStructure = new ResponseStructure<>();
			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setMessage("Users found with User Role: " + userRole);
			responseStructure.setData(userResponses);

			return new ResponseEntity<>(responseStructure, HttpStatus.OK);
		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Academic Program Data Not Found With given Id"));
	}

	public void deleteUsersPerminently() {
		List<User> usersToDelete = userRepo.findByIsDeletedTrue();

		for (User user : usersToDelete) {
			// Remove the user from associated academic programs
			if (user.getAcademicPrograms() != null) {
				for (AcademicProgram academicProgram : user.getAcademicPrograms()) {
					academicProgram.getUsers().remove(user);
				}
				academicProgramRepo.saveAll(user.getAcademicPrograms());
			}

			// Break the relationship between user and class hours
			List<ClassHour> classHours = classHourRepo.findByUser(user);
			for (ClassHour classHour : classHours) {
				classHour.setUser(null);
			}
			classHourRepo.saveAll(classHours);
		}

		// Delete users
		userRepo.deleteInBatch(usersToDelete);
	}

}
