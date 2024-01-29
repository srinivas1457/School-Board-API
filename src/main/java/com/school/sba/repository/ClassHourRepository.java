package com.school.sba.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.ClassHour;

public interface ClassHourRepository extends JpaRepository<ClassHour, Integer> {

//	List<ClassHour> findByBeginsAt(LocalDateTime beginsAt);
	
//	boolean existsByBeginsAtIsLessThanEqualAndEndsAtIsGreaterThanEqualAndRoomNo(LocalDateTime beginsAt, LocalDateTime endsAt,int roomNo);
	
	boolean existsByBeginsAtBetweenAndRoomNo(LocalDateTime beginsAt,LocalDateTime endsAt,int roomNo);
}
