package com.school.sba.util;

import org.springframework.stereotype.Component;

import com.school.sba.requestdto.ClassHourRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Component
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	private ClassHourRequest classHourRequest;
	private String errorMessage;

}
