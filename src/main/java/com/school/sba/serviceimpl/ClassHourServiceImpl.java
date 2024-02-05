package com.school.sba.serviceimpl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.ClassStatus;
import com.school.sba.enums.UserRole;
import com.school.sba.exceptionhandler.AcademicProgramNotFoundByIdException;
import com.school.sba.exceptionhandler.ClassHourNotFoundByIdException;
import com.school.sba.exceptionhandler.DataNotPresentException;
import com.school.sba.exceptionhandler.IllegalRequestException;
import com.school.sba.exceptionhandler.ScheduleNotFoundByIdException;
import com.school.sba.exceptionhandler.SubjectNotFoundByIdException;
import com.school.sba.exceptionhandler.UnauthorizedAccessException;
import com.school.sba.exceptionhandler.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SubjectRepo;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ErrorResponse;
import com.school.sba.util.ResponseStructure;
import com.school.sba.util.ResponseStructure2;

@Service
public class ClassHourServiceImpl implements ClassHourService {

	@Autowired
	private ClassHourRepository classHourRepo;

	@Autowired
	private AcademicProgramRepo academicProgramRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SubjectRepo subjectRepo;

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
	public ResponseEntity<ResponseStructure<String>> addClassHoursToAcademicProgram(int programId) {
		return academicProgramRepo.findById(programId).map(program -> {
			Schedule schedule = program.getSchool().getSchedule();

			if (schedule == null) {
				throw new ScheduleNotFoundByIdException("Failed to GENERATE Class Hour");
			}

			if (program.getClassHours() == null || program.getClassHours().isEmpty()) {
				List<ClassHour> perDayClasshour = new ArrayList<ClassHour>();
				LocalDate date = program.getBeginsAt();
				DayOfWeek dayOfWeek = date.getDayOfWeek();
				int end = 6;

				if (!dayOfWeek.equals(DayOfWeek.MONDAY)) {
					end = end + (7 - dayOfWeek.getValue());
				}

				// for generating day
				for (int day = 1; day <= end; day++) {
					if (date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
						date = date.plusDays(1);

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

	@Override
	public ResponseEntity<ResponseStructure2<List<ClassHourResponse>, List<ErrorResponse>>> updateClassHour(
			List<ClassHourRequest> classHourRequestList) {
		List<ClassHourResponse> successList = new ArrayList<>();
		List<ErrorResponse> errorList = new ArrayList<>();
		ResponseStructure2<List<ClassHourResponse>, List<ErrorResponse>> responseStructure = new ResponseStructure2<>();

		for (ClassHourRequest classHourRequest : classHourRequestList) {
			try {
				ClassHourResponse classHourResponse = processSingleClassHourRequest(classHourRequest);
				successList.add(classHourResponse);
			} catch (Exception e) {
				// Handle or log the exception
				ErrorResponse errorResponse = new ErrorResponse(classHourRequest, e.getMessage());
				errorList.add(errorResponse);
			}
		}

		// Set the response structure after the loop
		responseStructure.setStatusCode(HttpStatus.OK.value());
		responseStructure.setMessage("Class Hours Update Summary");
		responseStructure.setData(successList);
		responseStructure.setErrors(errorList);

		return new ResponseEntity<ResponseStructure2<List<ClassHourResponse>, List<ErrorResponse>>>(responseStructure,
				HttpStatus.OK);
	}

	private ClassHourResponse processSingleClassHourRequest(ClassHourRequest classHourRequest) {
		ClassHour classHour = classHourRepo.findById(classHourRequest.getClassHourId()).orElseThrow(
				() -> new ClassHourNotFoundByIdException("Class Hour Not Exist :" + classHourRequest.getClassHourId()));

		User user = userRepo.findById(classHourRequest.getUserId()).orElseThrow(
				() -> new UserNotFoundByIdException("User Not Found By Given Id :" + classHourRequest.getUserId()));

		if (!user.getUserRole().equals(UserRole.TEACHER)) {
			throw new UnauthorizedAccessException("Mentioned User Role is Not Match With Teacher.");
		}
		if (!user.getAcademicPrograms().contains(classHour.getAcademicProgram().getAcademicProgramId())) {
			throw new IllegalRequestException("Teacher Not Present in Academic Program List ");
		}

		Subject subject = subjectRepo.findById(classHourRequest.getSubjectId())
				.orElseThrow(() -> new SubjectNotFoundByIdException(
						"Subject Not Found By Given Id :" + classHourRequest.getSubjectId()));

		if (!subject.getSubjectName().equals(user.getSubject().getSubjectName())) {
			throw new IllegalRequestException("Subject Id Not Matching With Teacher Dealing Subject");
		}

		boolean isPresent = classHourRepo.existsByBeginsAtBetweenAndRoomNo(classHour.getBeginsAt(),
				classHour.getEndsAt(), classHourRequest.getRoomNo());

		if (isPresent) {
			throw new IllegalRequestException("Room Number Is Already Engaged");
		} else {
			classHour.setRoomNo(classHourRequest.getRoomNo());
			classHour.setSubject(subject);
			classHour.setUser(user);
			ClassHour updatedClassHour = classHourRepo.save(classHour);
			return mapToclassHourResponse(updatedClassHour);
		}
	}

	private ClassHourResponse mapToclassHourResponse(ClassHour classHour) {
		return ClassHourResponse.builder().classHourId(classHour.getClassHourId()).beginsAt(classHour.getBeginsAt())
				.endsAt(classHour.getEndsAt()).roomNo(classHour.getRoomNo()).classStatus(classHour.getClassStatus())
				.subjectName(classHour.getSubject().getSubjectName()).teacherName(classHour.getUser().getUserName())
				.build();
	}

	@Override
	public void autoGenerateWeeklyClassHours() {
		List<AcademicProgram> programsToAutoRepeat = academicProgramRepo.findByAutoRepeatScheduledTrue();

		if (!programsToAutoRepeat.isEmpty()) {

			programsToAutoRepeat.forEach(program -> {
				int recordsNeeded = (program.getSchool().getSchedule().getClassHoursPerday()) * 6;
				List<ClassHour> classhours = classHourRepo.findLastNRecordsByAcademicProgram(program, recordsNeeded);
				if (classhours != null || !classhours.isEmpty()) {
					for (int i = classhours.size() - 1; i >= 0; i--) {
						classHourRepo.save(mapToNewClassHour(classhours.get(i)));
					}
				} else
					throw new DataNotPresentException("For Autorepetation Existing ClassHours Data NotPresent");
			});
		}
	}

	private ClassHour mapToNewClassHour(ClassHour existClassHour) {
		return ClassHour.builder().user(existClassHour.getUser()).academicProgram(existClassHour.getAcademicProgram())
				.roomNo(existClassHour.getRoomNo()).beginsAt(existClassHour.getBeginsAt().plusDays(7))
				.endsAt(existClassHour.getEndsAt().plusDays(7)).classStatus(existClassHour.getClassStatus())
				.subject(existClassHour.getSubject()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> createExcelSheet(int programId, ExcelRequestDto excelRequestDto) {
		return academicProgramRepo.findById(programId).map(program -> {
			if (!program.isDeleted()) {

				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet();

				int rowNumber = 0;
				Row header = sheet.createRow(rowNumber);

				header.createCell(0).setCellValue("Date");
				header.createCell(1).setCellValue("Begin Time");
				header.createCell(2).setCellValue("End Time");
				header.createCell(3).setCellValue("Subject");
				header.createCell(4).setCellValue("Teacher");
				header.createCell(5).setCellValue("Room No.");

				LocalDateTime startingAt = excelRequestDto.getFromDate().atTime(LocalTime.MIDNIGHT);
				LocalDateTime endingAt = excelRequestDto.getToDate().atTime(LocalTime.MIDNIGHT).plusDays(1);

				List<ClassHour> classhours = classHourRepo.findAllByAcademicProgramAndBeginsAtBetween(program,
						startingAt, endingAt);

				DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:MM");
				DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

				if (!classhours.isEmpty()) {
					for (ClassHour classhour : classhours) {

						Row newRow = sheet.createRow(++rowNumber);

						newRow.createCell(0).setCellValue(dateformatter.format(classhour.getBeginsAt()));
						newRow.createCell(1).setCellValue(timeformatter.format(classhour.getBeginsAt()));
						newRow.createCell(2).setCellValue(timeformatter.format(classhour.getEndsAt()));

						if (classhour.getSubject() == null)
							newRow.createCell(3).setCellValue("NOT AVAILABLE");
						else
							newRow.createCell(3).setCellValue(classhour.getSubject().getSubjectName());

						if (classhour.getUser() == null)
							newRow.createCell(3).setCellValue("NOT AVAILABLE");
						else
							newRow.createCell(4).setCellValue(classhour.getUser().getUserName());

						newRow.createCell(5).setCellValue(classhour.getRoomNo());

					}

					try {
						workbook.write(new FileOutputStream(excelRequestDto.getFilePath() + "\\Classhours"
								+ excelRequestDto.getFromDate() + excelRequestDto.getToDate() + ".xlsx"));
					} catch (Exception e) {
						e.printStackTrace();
					}

					structure.setStatusCode(HttpStatus.CREATED.value());
					structure.setMessage("Excel Sheet Created Successfully");
					structure.setData("Excel for the PROGRAM:" + program.getAcademicProgramId());

					return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.CREATED);
				}
				throw new IllegalRequestException("Requested Classhours is EMPTY");
			}
			throw new IllegalRequestException("Program Already DELETED");
		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Failed to WRITE Excel"));
	}

	@Override
	public ResponseEntity<?> writeToExcel(MultipartFile file, int academicProgramId,
			LocalDate fromDate, LocalDate toDate){
		return academicProgramRepo.findById(academicProgramId).map(program -> {
			if (!program.isDeleted()) {
				LocalDateTime startingAt = fromDate.atTime(LocalTime.MIDNIGHT);
				LocalDateTime endingAt = toDate.atTime(LocalTime.MIDNIGHT).plusDays(1);

				XSSFWorkbook workbook=null;
				try {
					workbook = new XSSFWorkbook(file.getInputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				List<ClassHour> classhours = classHourRepo.findAllByAcademicProgramAndBeginsAtBetween(program,
						startingAt, endingAt);

				DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:MM");
				DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

				if (!classhours.isEmpty()) {
					workbook.forEach(sheet->{
						int rowNumber = 0;
						Row header = sheet.createRow(rowNumber);

						header.createCell(0).setCellValue("Date");
						header.createCell(1).setCellValue("Begin Time");
						header.createCell(2).setCellValue("End Time");
						header.createCell(3).setCellValue("Subject");
						header.createCell(4).setCellValue("Teacher");
						header.createCell(5).setCellValue("Room No.");
						
						for (ClassHour classhour : classhours) {

							Row newRow = sheet.createRow(++rowNumber);

							newRow.createCell(0).setCellValue(dateformatter.format(classhour.getBeginsAt()));
							newRow.createCell(1).setCellValue(timeformatter.format(classhour.getBeginsAt()));
							newRow.createCell(2).setCellValue(timeformatter.format(classhour.getEndsAt()));

							if (classhour.getSubject() == null)
								newRow.createCell(3).setCellValue("NOT AVAILABLE");
							else
								newRow.createCell(3).setCellValue(classhour.getSubject().getSubjectName());

							if (classhour.getUser() == null)
								newRow.createCell(3).setCellValue("NOT AVAILABLE");
							else
								newRow.createCell(4).setCellValue(classhour.getUser().getUserName());

							newRow.createCell(5).setCellValue(classhour.getRoomNo());

						}

					});
					
					ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
					try {
						workbook.write(arrayOutputStream);
						workbook.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					byte[] byteData=arrayOutputStream.toByteArray();

					return ResponseEntity.ok().header("Content Disposition", "atttachment; filename="+file.getOriginalFilename())
							.contentType(MediaType.APPLICATION_OCTET_STREAM).body(byteData);

					
				}
				throw new IllegalRequestException("Requested Classhours is EMPTY");
			}
			throw new IllegalRequestException("Program Already DELETED");
		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Failed to WRITE Excel"));
	}

	
}
