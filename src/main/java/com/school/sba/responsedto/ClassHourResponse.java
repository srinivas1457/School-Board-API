package com.school.sba.responsedto;

import java.time.LocalDateTime;

import com.school.sba.enums.ClassStatus;
import com.school.sba.requestdto.ClassHourRequest;

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
public class ClassHourResponse {
	private int classHourId;
	private LocalDateTime beginsAt;
	private LocalDateTime endsAt;
	private int roomNo;
	private ClassStatus classStatus;
	private String subjectName;
	private String teacherName;
}
