package com.school.sba.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleNotFoundByIdException extends RuntimeException {
	private String message;
}
