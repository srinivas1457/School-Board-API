package com.school.sba.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.enums.ClassStatus;
import com.school.sba.exceptionhandler.AcademicProgramNotFoundByIdException;
import com.school.sba.exceptionhandler.IllegalRequestException;
import com.school.sba.exceptionhandler.ScheduleNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService {

	@Autowired
	private ClassHourRepository classHourRepo;

	@Autowired
	private AcademicProgramRepo academicProgramRepo;

	@Autowired
	private ResponseStructure<String> structure;

	private ClassHour mapToClassHour(ClassHourRequest classHourRequest) {
		return ClassHour.builder().roomNo(classHourRequest.getRoomNo()).classStatus(classHourRequest.getClassStatus())
				.build();
	}

	private LocalDateTime dateToDateTime(LocalDate date, LocalTime time) {
		return LocalDateTime.of(date, time);
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> addClassHoursToAcademicProgram(int programId,
			ClassHourRequest request) {
		return academicProgramRepo.findById(programId).map(program -> {
			Schedule schedule = program.getSchool().getSchedule();

			if (schedule == null) {
				throw new ScheduleNotFoundByIdException("Failed to GENERATE Class Hour");
			}

			if (program.getClassHours() == null || program.getClassHours().isEmpty()) {
				List<ClassHour> perDayClasshour = new ArrayList<ClassHour>();
				LocalDate date = program.getBeginsAt();

				// for generating day
				for (int day = 1; day <= 6; day++) {
					LocalTime currentTime = schedule.getOpensAt();
					LocalDateTime lasthour = null;

					// for generating class hours per day
					for (int entry = 1; entry <= schedule.getClassHoursPerday(); entry++) {
						ClassHour classhour = new ClassHour();

						if (currentTime.equals(schedule.getOpensAt())) { // first class hour of the day
							classhour.setBeginsAt(dateToDateTime(date, currentTime));
						} else if (currentTime.equals(schedule.getBreaktime())) { // after break time
							lasthour = lasthour.plus(schedule.getBreakeLengthInMinutes());
							classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
						} else if (currentTime.equals(schedule.getLunchTime())) { // after lunch time
							lasthour = lasthour.plus(schedule.getBreakeLengthInMinutes());
							classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
						} else { // rest class hours of that day
							classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
						}
						classhour.setEndsAt(classhour.getBeginsAt().plus(schedule.getClassHoursLengthInMinutes()));
						classhour.setClassStatus(ClassStatus.NOTSCHEDULED);
						classhour.setAcademicProgram(program);

						perDayClasshour.add(classHourRepo.save(classhour));

						lasthour = perDayClasshour.get(entry - 1).getEndsAt();

						currentTime = lasthour.toLocalTime();

						if (currentTime.equals(schedule.getClosesAt())) // school closing time
							break;

					}
					date = date.plusDays(1);
				}
				program.setClassHours(perDayClasshour);
				academicProgramRepo.save(program);

				structure.setStatusCode(HttpStatus.CREATED.value());
				structure.setMessage("Classhour GENERATED for Program: " + program.getProgramName());
				structure.setData("Completed Successfully");

				return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.CREATED);
			} else
				throw new IllegalRequestException("Classhours Already Generated for :: " + program.getProgramName()
						+ " of ID: " + program.getAcademicProgramId());

		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Failed to GENERATE Class Hour"));
	}

}
