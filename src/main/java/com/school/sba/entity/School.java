package com.school.sba.entity;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@Entity
public class School {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long schoolId;
	private String schoolName;
	private long contactNum;
	private String email;
	private String address;
	
	@OneToOne
	private Schedule schedule;
	
}
