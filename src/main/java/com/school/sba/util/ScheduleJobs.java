package com.school.sba.util;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.School;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.service.ClassHourService;
import com.school.sba.service.SchoolService;
import com.school.sba.service.UserService;

@Component
public class ScheduleJobs {
	@Autowired
	private UserService userService;

	@Autowired
	private AcademicProgramService programService;

	@Autowired
	private SchoolService schoolService;
	
	@Autowired
	private ClassHourService classhourService;

//	@Scheduled(fixedDelay = 1000)
//	public void test(){
//		System.out.println("Schedule Jobs Is Activatyed");
//	}

	@Scheduled(fixedDelay = 1000 * 60 * 5)
	void deleteUsersPerminently() {
		userService.deleteUsersPerminently();
	}

	@Scheduled(fixedDelay = 1000 * 60 * 5)
	void deleteAcademicProgramPerminently() {
		programService.deleteAcademicProgramPerminently();
	}
	
	@Scheduled(fixedDelay = 1000*60*10)
	void deleteSchoolPerminently() {
		schoolService.deleteSchoolPerminently();
	}
	
//	@Scheduled(fixedDelay = 4000)
//	void nextWeek() {
//		classhourService.generateNextWeekClassHours(1);
//	}
	
	@Scheduled(cron = "0 0 0 ? * MON")
	public void autoRepeatSchedule() {
	    classhourService.autoGenerateWeeklyClassHours();
	}

}
