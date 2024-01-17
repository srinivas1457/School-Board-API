package com.school.sba.responsedto;

import java.time.LocalDate;
import java.util.Map;

import com.school.sba.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchoolResponse {

	private int schoolId;
	private String schoolName;
	private long contactNum;
	private String email;
	private String address;

	Map<String, String> options;

}
