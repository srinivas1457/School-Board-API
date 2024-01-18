package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.Subject;
import com.school.sba.exceptionhandler.DataNotPresentException;
import com.school.sba.exceptionhandler.SchoolNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@Service
public class AcademicProgramServiceImpl implements AcademicProgramService {

	@Autowired
	private AcademicProgramRepo academicProgramRepo;

	@Autowired
	private SchoolRepository schoolRepo;
	@Autowired
	private ResponseStructure<AcademicProgramResponse> responseStructure;

	public AcademicProgramResponse mapToAcademicProgramResponse(AcademicProgram academicProgram) {
		List<String> subjects = new ArrayList<>();

		if (academicProgram.getSubjects() != null) {
			academicProgram.getSubjects().forEach(subject -> {
				subjects.add(subject.getSubjectName());
			});
		}
		return AcademicProgramResponse.builder().academicProgramId(academicProgram.getAcademicProgramId())
				.programType(academicProgram.getProgramType()).programName(academicProgram.getProgramName())
				.beginsAt(academicProgram.getBeginsAt()).endsAt(academicProgram.getEndsAt()).subjects(subjects).build();
	}

	private AcademicProgram mapToAcademicProgram(AcademicProgramRequest academicProgramRequest) {
		return AcademicProgram.builder().programType(academicProgramRequest.getProgramType())
				.programName(academicProgramRequest.getProgramName()).beginsAt(academicProgramRequest.getBeginsAt())
				.endsAt(academicProgramRequest.getEndsAt()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> insertAcademicProgram(int schoolId,
			AcademicProgramRequest academicProgramRequest) {
		return schoolRepo.findById(schoolId).map(school -> {
			AcademicProgram academicProgram = mapToAcademicProgram(academicProgramRequest);
			academicProgram.setSchool(school);
			academicProgram = academicProgramRepo.save(academicProgram);
			school.getAcadamicProgramList().add(academicProgram);
			schoolRepo.save(school);

			responseStructure.setStatusCode(HttpStatus.CREATED.value());
			responseStructure.setMessage("AcademicProgram Saved Successfully");
			responseStructure.setData(mapToAcademicProgramResponse(academicProgram));
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,
					HttpStatus.CREATED);
		}).orElseThrow(() -> new SchoolNotFoundByIdException("School not found by given Id"));

	}

	@Override
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAcademicProgramsBySchoolId(
			int schoolId) {
		return schoolRepo.findById(schoolId).map(school -> {
			List<AcademicProgram> acadamicProgramList = school.getAcadamicProgramList();

			if (!acadamicProgramList.isEmpty()) {
				List<AcademicProgramResponse> academicProgramResponseList = new ArrayList<>();
				for (AcademicProgram academicProgram : acadamicProgramList) {
					AcademicProgramResponse academicProgramResponse = mapToAcademicProgramResponse(academicProgram);
					academicProgramResponseList.add(academicProgramResponse);
				}

				ResponseStructure<List<AcademicProgramResponse>> responseStructure = new ResponseStructure<>();
				responseStructure.setStatusCode(HttpStatus.FOUND.value());
				responseStructure.setMessage("AcademicProgram Data Found Successfully");
				responseStructure.setData(academicProgramResponseList);

				return new ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>>(responseStructure,
						HttpStatus.FOUND);

			} else {
				throw new DataNotPresentException("AcadamicProgram Data Not Found By Given SchoolId");
			}
		}).orElseThrow(() -> new SchoolNotFoundByIdException("School not found by given Id"));

	}

}
