package com.school.sba.responsedto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolResponse {

	private long schoolId;
	private String schoolName;
	private long contactNum;
	private String email;
	private String address;

	Map<String, String> options;

}
