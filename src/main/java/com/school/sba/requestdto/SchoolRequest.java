package com.school.sba.requestdto;

import java.time.LocalDate;

import com.school.sba.enums.UserRole;
import com.school.sba.responsedto.UserResponse;

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
public class SchoolRequest {

	private String schoolName;
	private long contactNum;
	private String email;
	private String address;

}
