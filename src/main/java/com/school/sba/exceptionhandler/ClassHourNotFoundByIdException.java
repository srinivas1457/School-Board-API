package com.school.sba.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClassHourNotFoundByIdException extends RuntimeException {
	private String massage;
}
