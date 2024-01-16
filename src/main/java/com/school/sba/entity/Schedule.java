package com.school.sba.entity;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@Entity
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int scheduleId;
	private LocalTime opensAt;
	private LocalTime closesAt;
	private LocalTime classHoursPerday;
	private LocalTime classHoursLength;
	private LocalTime breaktime;
	private LocalTime breakeLength;
	private LocalTime lunchTime;
	private LocalTime lunchBreakLength;
	
	
}
