package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

public interface ScheduleService {

	ResponseEntity<ResponseStructure<ScheduleResponse>> addSchoolSchedule(ScheduleRequest scheduleRequest,int schoolId);

	ResponseEntity<ResponseStructure<ScheduleResponse>> findScheduleByschool(int schoolId);

	ResponseEntity<ResponseStructure<ScheduleResponse>> updateById(int scheduleId,  ScheduleRequest scheduleRequest);

}
