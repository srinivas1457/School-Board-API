package com.school.sba.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.school.sba.enums.ProgramType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcademicProgram {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int academicProgramId;
	@Enumerated(EnumType.STRING)
	private ProgramType programType;
	private String programName;
	private LocalDate beginsAt;
	private LocalDate endsAt;
	private boolean isDeleted;
	
	@ManyToOne
	private  School school;
	
	@ManyToMany
	private List<Subject> subjects;
	
	@ManyToMany
	private Set<User>users;
	
	@OneToMany(mappedBy = "academicProgram",fetch = FetchType.EAGER)
	private List<ClassHour> classHours;
}
