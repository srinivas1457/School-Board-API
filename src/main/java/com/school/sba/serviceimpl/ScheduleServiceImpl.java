package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.exceptionhandler.DataAlreadyExistException;
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
				.classHoursLength(scheduleRequest.getClassHoursLength()).breaktime(scheduleRequest.getBreaktime())
				.breakeLength(scheduleRequest.getBreakeLength()).lunchTime(scheduleRequest.getLunchTime())
				.lunchBreakLength(scheduleRequest.getLunchBreakLength()).build();
	}

	private ScheduleResponse mapToScheduleResponse(Schedule schedule) {
		return ScheduleResponse.builder().scheduleId(schedule.getScheduleId()).opensAt(schedule.getOpensAt())
				.classHoursLength(schedule.getClassHoursLength()).closesAt(schedule.getClosesAt())
				.classHoursPerday(schedule.getClassHoursPerday()).breaktime(schedule.getBreaktime())
				.breakeLength(schedule.getBreakeLength()).lunchTime(schedule.getLunchTime())
				.lunchBreakLength(schedule.getLunchBreakLength()).build();
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

}
