package com.school.sba.util;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ResponseStructure2<T, E> {
	
	
	    private int statusCode;
	    private String message;
	    private T data;
	    private E errors;

	    // Constructors, getters, and setters

}
