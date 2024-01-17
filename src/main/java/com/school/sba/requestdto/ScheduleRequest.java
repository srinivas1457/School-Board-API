package com.school.sba.requestdto;

import java.time.Duration;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {
	private LocalTime opensAt;
	private LocalTime closesAt;
	private int classHoursPerday;
	private Duration classHoursLength;
	private LocalTime breaktime;
	private Duration breakeLength;
	private LocalTime lunchTime;
	private Duration lunchBreakLength;
}
