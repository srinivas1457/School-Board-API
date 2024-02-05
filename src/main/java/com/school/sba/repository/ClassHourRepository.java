package com.school.sba.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.User;

public interface ClassHourRepository extends JpaRepository<ClassHour, Integer> {	
	
	boolean existsByBeginsAtBetweenAndRoomNo(LocalDateTime beginsAt,LocalDateTime endsAt,int roomNo);

	List<ClassHour> findByUser(User user);
	
	@Query("SELECT ch FROM ClassHour ch WHERE ch.academicProgram = :academicProgram " +
		       "ORDER BY ch.classHourId DESC " +
		       "LIMIT :lastNrecords")
	List<ClassHour> findLastNRecordsByAcademicProgram( AcademicProgram academicProgram, int lastNrecords);


	List<ClassHour> findAllByAcademicProgramAndBeginsAtBetween(AcademicProgram academicProgram, LocalDateTime from,
			LocalDateTime to);
	
}
