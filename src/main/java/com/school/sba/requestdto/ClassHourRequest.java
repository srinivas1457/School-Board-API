package com.school.sba.requestdto;

import java.time.LocalDateTime;

import com.school.sba.enums.ClassStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClassHourRequest {
	private int roomNo;
	private ClassStatus classStatus;

}
