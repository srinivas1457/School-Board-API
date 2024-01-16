package com.school.sba.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.School;

public interface SchoolRepository extends JpaRepository<School, Integer>{

	List<School> findBYSchoolName(String schoolName);

}
