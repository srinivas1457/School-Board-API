package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Subject;
import com.school.sba.exceptionhandler.AcademicProgramNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.SubjectRepo;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.util.ResponseStructure;

@Service
public class SubjectServiceImpl implements SubjectService {

	@Autowired
	private SubjectRepo subjectRepo;

	@Autowired
	private AcademicProgramRepo academicProgramRepo;

	@Autowired
	private AcademicProgramServiceImpl academicProgramServiceImpl;

	private List<Subject> convertToSubjects(SubjectRequest subjectRequest) {
		// Create an empty list to store the Subject objects
		List<Subject> subjects = new ArrayList<>();

		// Check if the list of subject names is not null
		if (subjectRequest.getSubjectNames() != null) {
			// Iterate over the subject names in the SubjectRequest
			subjectRequest.getSubjectNames().forEach(name -> {
				// Your existing logic for each subject goes here
				Subject subject = subjectRepo.findBySubjectName(name.toLowerCase()).map(s -> s).orElseGet(() -> {
					Subject newSubject = new Subject();
					newSubject.setSubjectName(name.toLowerCase());
					subjectRepo.save(newSubject);
					return newSubject;
				});

				subjects.add(subject);
			});
		}

		// Return the list of Subject objects
		return subjects;
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> insertSubjectsIntoAcademicProgram(
			SubjectRequest subjectRequest, int academicProgramId) {
		return academicProgramRepo.findById(academicProgramId).map(academicProgram -> {
			// Log to check if the convertToSubjects method is being called
			System.out.println("convertToSubjects method is called");

			List<Subject> subjectList = convertToSubjects(subjectRequest);

			// Log to check the content of subjectList
			System.out.println("Subject List: " + subjectList);

			academicProgram.setSubjects(subjectList);
			academicProgramRepo.save(academicProgram);

			ResponseStructure<AcademicProgramResponse> responseStructure = new ResponseStructure<>();
			responseStructure.setStatusCode(HttpStatus.CREATED.value());
			responseStructure.setMessage("Updated the Subject list to Academic Program");
			responseStructure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(academicProgram));

			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,
					HttpStatus.CREATED);
		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("AcademicProgram Data Not Found By Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjectsToAcademicProgram(
			SubjectRequest subjectRequest, int academicProgramId) {
		return academicProgramRepo.findById(academicProgramId).map(academicProgram -> {
			List<Subject> subjectList = convertToSubjects(subjectRequest);
			List<Subject> existingSubjects = academicProgram.getSubjects();
			Map<String, Subject> subjectMap = new HashMap<>();
			for (Subject existingSubject : existingSubjects) {
				subjectMap.put(existingSubject.getSubjectName(), existingSubject);
			}
			for (Subject subject : subjectList) {
				subjectMap.put(subject.getSubjectName(), subject);
			}

			existingSubjects = new ArrayList<>(subjectMap.values());
			academicProgram.setSubjects(existingSubjects);

			academicProgramRepo.save(academicProgram);

			ResponseStructure<AcademicProgramResponse> responseStructure = new ResponseStructure<>();
			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setMessage("Updated the Subject list to Academic Program");
			responseStructure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(academicProgram));

			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure, HttpStatus.OK);
		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("AcademicProgram Data Not Found By Id"));

	}

}
