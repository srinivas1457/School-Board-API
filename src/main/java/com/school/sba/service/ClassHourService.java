package com.school.sba.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.util.ErrorResponse;
import com.school.sba.util.ResponseStructure;
import com.school.sba.util.ResponseStructure2;

public interface ClassHourService {

	public ResponseEntity<ResponseStructure<String>> addClassHoursToAcademicProgram(int academicProgramId);

	public ResponseEntity<ResponseStructure2<List<ClassHourResponse>, List<ErrorResponse>>> updateClassHour(List<ClassHourRequest> classHourRequestList);


	void autoGenerateWeeklyClassHours();

	public ResponseEntity<ResponseStructure<String>> createExcelSheet(int academicProgramId,
			ExcelRequestDto excelRequestDto);

	public ResponseEntity<?> writeToExcel(MultipartFile file, int academicProgramId,
			LocalDate fromDate, LocalDate toDate);
	
}
