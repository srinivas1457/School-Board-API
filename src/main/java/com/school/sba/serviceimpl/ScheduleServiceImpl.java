package com.school.sba.serviceimpl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.exceptionhandler.DataAlreadyExistException;
import com.school.sba.exceptionhandler.ScheduleNotFoundByIdException;
import com.school.sba.exceptionhandler.SchoolNotFoundByIdException;
import com.school.sba.repository.ScheduleRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.ScheduleService;
import com.school.sba.util.ResponseStructure;

@Service
public class ScheduleServiceImpl implements ScheduleService {

	@Autowired
	private ScheduleRepository scheduleRepo;
	@Autowired
	private SchoolRepository schoolRepo;

	@Autowired
	private ResponseStructure<ScheduleResponse> responseStructure;

	private Schedule mapToSchedule(ScheduleRequest scheduleRequest) {
		return Schedule.builder().opensAt(scheduleRequest.getOpensAt()).closesAt(scheduleRequest.getClosesAt())
				.classHoursPerday(scheduleRequest.getClassHoursPerday())
				.classHoursLengthInMinutes(Duration.ofMinutes(scheduleRequest.getClassHoursLengthInMinutes()))
				.breaktime(scheduleRequest.getBreaktime())
				.breakeLengthInMinutes(Duration.ofMinutes(scheduleRequest.getBreakeLengthInMinutes()))
				.lunchTime(scheduleRequest.getLunchTime())
				.lunchBreakLengthInMinutes(Duration.ofMinutes(scheduleRequest.getLunchBreakLengthInMinutes())).build();
	}

	private ScheduleResponse mapToScheduleResponse(Schedule schedule) {
		return ScheduleResponse.builder().scheduleId(schedule.getScheduleId()).opensAt(schedule.getOpensAt())
				.classHoursLengthInMinutes((int) schedule.getClassHoursLengthInMinutes().toMinutes())
				.closesAt(schedule.getClosesAt()).classHoursPerday(schedule.getClassHoursPerday())
				.breaktime(schedule.getBreaktime())
				.breakeLengthInMinutes((int) schedule.getBreakeLengthInMinutes().toMinutes())
				.lunchTime(schedule.getLunchTime())
				.lunchBreakLengthInMinutes((int) schedule.getLunchBreakLengthInMinutes().toMinutes()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> addSchoolSchedule(ScheduleRequest scheduleRequest,
			int schoolId) {
		return schoolRepo.findById(schoolId).map(school -> {
			if (school.getSchedule() == null) {
				Schedule schedule = mapToSchedule(scheduleRequest);
				schedule = scheduleRepo.save(schedule);
				school.setSchedule(schedule);
				schoolRepo.save(school);

				responseStructure.setStatusCode(HttpStatus.CREATED.value());
				responseStructure.setMessage("Schedule created Successfully");
				responseStructure.setData(mapToScheduleResponse(schedule));
				return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure, HttpStatus.CREATED);

			} else {
				throw new DataAlreadyExistException("Schedule data Alredy Exist");
			}
		}).orElseThrow(() -> new SchoolNotFoundByIdException("School Data not Found to given Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findScheduleByschool(int schoolId) {

		return schoolRepo.findById(schoolId).map(school -> {
			if (school.getSchedule() != null) {
				responseStructure.setStatusCode(HttpStatus.FOUND.value());
				responseStructure.setMessage("schedule data found");
				responseStructure.setData(mapToScheduleResponse(school.getSchedule()));
				return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure, HttpStatus.FOUND);
			} else
				throw new ScheduleNotFoundByIdException("Schedule data Not Found By Id");

		}).orElseThrow(() -> new SchoolNotFoundByIdException("School Data not Found to given Id"));

	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateById(int scheduleId,
			ScheduleRequest scheduleRequest) {
		return scheduleRepo.findById(scheduleId).map(schedule -> {
			Schedule schedule2 = mapToSchedule(scheduleRequest);
			schedule2.setScheduleId(schedule.getScheduleId());
			schedule2 = scheduleRepo.save(schedule2);
			ScheduleResponse scheduleResponse = mapToScheduleResponse(schedule2);
			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setMessage("schedule data Updated Successfully");
			responseStructure.setData(scheduleResponse);
			return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure, HttpStatus.OK);

		}).orElseThrow(() -> new ScheduleNotFoundByIdException("Schedule data Not Found By Id"));

	}

}
