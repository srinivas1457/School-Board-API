package com.school.sba.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AcademicProgramNotFoundByIdException extends RuntimeException{
	private String message;
}
