package com.school.sba.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorStructure<T> {
	private int statusCode;
	private String message;
	private T errordata;

}
