package com.school.sba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.School;

public interface AcademicProgramRepo extends JpaRepository<AcademicProgram, Integer>{

	List<AcademicProgram>  findByIsDeletedTrue();

	List<AcademicProgram> findBySchool(School school);

}
