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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.school.sba.util.ErrorStructure;

public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<ObjectError> allErrors = ex.getAllErrors(); // UPCASTING : ObjectError is the SuperClass of
															// FieldError
		// which extends Error Class.

		Map<String, String> mapErrors = new HashMap<String, String>();

		for (ObjectError error : allErrors) {
			FieldError fieldError = (FieldError) error; // DOWNCASTING : ObjectError -> FieldError
			String message = fieldError.getDefaultMessage(); /** VALUE **/
			String field = fieldError.getField(); /** KEY **/

			mapErrors.put(field, message);
		}
		return new ResponseEntity<Object>(mapErrors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(SchoolNotFoundByIdException.class)
	public ResponseEntity<ErrorStructure<String>> SchoolNotFoundById(SchoolNotFoundByIdException exp) {
		ErrorStructure<String> es = new ErrorStructure<String>();
		es.setStatusCode(HttpStatus.NOT_FOUND.value());
		es.setMessage(exp.getMessage()); // message what we throw in service
		es.setErrordata(" School NOT PRESENT With Given Id");

		return new ResponseEntity<ErrorStructure<String>>(es, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(SchoolNotFoundByNameException.class)
	public ResponseEntity<ErrorStructure<String>> SchoolNotFoundByName(SchoolNotFoundByNameException exp) {
		ErrorStructure<String> es = new ErrorStructure<String>();
		es.setStatusCode(HttpStatus.NOT_FOUND.value());
		es.setMessage(exp.getMessage()); // message what we throw in service
		es.setErrordata(" School NOT PRESENT With Given Name");

		return new ResponseEntity<ErrorStructure<String>>(es, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(DataNotPresentException.class)
	public ResponseEntity<ErrorStructure<String>> DataNotPresent(DataNotPresentException exp) {
		ErrorStructure<String> es = new ErrorStructure<String>();
		es.setStatusCode(HttpStatus.NOT_FOUND.value());
		es.setMessage(exp.getMessage()); // message what we throw in service
		es.setErrordata(" Data NOT PRESENT");

		return new ResponseEntity<ErrorStructure<String>>(es, HttpStatus.NOT_FOUND);
	}

}
