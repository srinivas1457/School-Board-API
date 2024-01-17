package com.school.sba.exceptionhandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

	// Utility method to create a standardized response structure
	private ResponseEntity<Object> structure(HttpStatus status, String message, Object rootCause) {
		return new ResponseEntity<Object>(Map.of("Status", status.value(), "message", message, "rootCause", rootCause),
				status);
	}

	// Extract field errors and their corresponding error messages
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<ObjectError> allErrors = ex.getAllErrors();
		Map<String, String> errors = new HashMap<>();

		// Extract field errors and their corresponding error messages
		allErrors.forEach(error -> {
			FieldError fieldError = (FieldError) error;
			errors.put(fieldError.getField(), error.getDefaultMessage());
		});
		return structure(HttpStatus.BAD_REQUEST, "Failed To Save The Data", errors);
	}

	// Custom handling for DataNotPresentException
	@ExceptionHandler(DataNotPresentException.class)
	public ResponseEntity<Object> handllerDataNotPresent(DataNotPresentException ex) {
		return structure(HttpStatus.NOT_FOUND, ex.getMessage(), "Data Not Present");
	}

	// Custom handling for UserNotFoundByIdException
	@ExceptionHandler(UserNotFoundByIdException.class)
	public ResponseEntity<Object> handllerUserNotFoundById(UserNotFoundByIdException ex) {
		return structure(HttpStatus.NOT_FOUND, ex.getMessage(), "User With Given Id Not Present");
	}

	@ExceptionHandler(SchoolNotFoundByIdException.class)
	public ResponseEntity<Object> handllerSchoolNotFoundById(SchoolNotFoundByIdException ex) {
		return structure(HttpStatus.NOT_FOUND, ex.getMessage(), " School NOT PRESENT With Given Id");
	}

	@ExceptionHandler(SchoolNotFoundByNameException.class)
	public ResponseEntity<Object> handllerSchoolNotFoundByName(SchoolNotFoundByNameException ex) {
		return structure(HttpStatus.NOT_FOUND, ex.getMessage(), " School NOT PRESENT With Given Name");
	}

	@ExceptionHandler(UserAlreadyDeletedException.class)
	public ResponseEntity<Object> handlerUserAlreadyDeleted(UserAlreadyDeletedException ex) {
		return structure(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), "User With Given Id is already Deleted");
	}

	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<Object> handlerUnauthorizedAccess(UnauthorizedAccessException ex) {
		return structure(HttpStatus.UNAUTHORIZED, ex.getMessage(), "Unauthorized User");
	}

	@ExceptionHandler(ScheduleNotFoundByIdException.class)
	public ResponseEntity<Object> handlerScheduleNotFoundById(ScheduleNotFoundByIdException ex) {
		return structure(HttpStatus.NOT_FOUND, ex.getMessage(), "Schedule Not Present With Given Id");
	}

	@ExceptionHandler(IllegalRequestException.class)
	public ResponseEntity<Object> handlerIllegalRequest(IllegalRequestException ex) {
		return structure(HttpStatus.BAD_REQUEST, ex.getMessage(), "Illegal Request");
	}

	@ExceptionHandler(DataAlreadyExistException.class)
	public ResponseEntity<Object> handlerDataAlreadyExist(DataAlreadyExistException ex) {
		return structure(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), "Data Already Exist");
	}
}
