package com.school.sba.exceptionhandler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SchoolNotFoundByNameException extends RuntimeException {
	private String message;

	@Override
	public String getMessage() {
		return message;
	}
	

}
