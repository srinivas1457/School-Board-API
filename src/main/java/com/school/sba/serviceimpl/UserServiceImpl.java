package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exceptionhandler.UserAlreadyDeletedException;
import com.school.sba.exceptionhandler.UserNotFoundByIdException;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ResponseStructure<UserResponse> responseStructure;

	public User mapToUser(UserRequest userRequest) {
		return User.builder().userName(userRequest.getUserName()).password(userRequest.getPassword())
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
//				throw new Exception("Admin User already Exists");
		}

		UserResponse userResponse = mapToUserResponse(userRepo.save(user));

		responseStructure.setStatusCode(HttpStatus.CREATED.value());
		responseStructure.setMessage("User Data Successfully Created");
		responseStructure.setData(userResponse);

		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.CREATED);
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
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(int userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundByIdException("User With Given Id Not Found"));
		if (!user.isDeleted()) {
			if (user.isDeleted() == false)
				user.setDeleted(true);
			User user2 = userRepo.save(user);
			UserResponse userResponse = mapToUserResponse(user2);

			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setMessage("User Deleted Successfully");
			responseStructure.setData(userResponse);

			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.OK);
		} else {
			throw new UserAlreadyDeletedException("User Data Already Deleted ");
		}
	}

}
