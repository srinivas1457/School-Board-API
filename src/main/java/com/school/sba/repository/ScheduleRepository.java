package com.school.sba.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer>{
	
	Optional<Schedule> findScheduleBySchool(int schoolId);

}
