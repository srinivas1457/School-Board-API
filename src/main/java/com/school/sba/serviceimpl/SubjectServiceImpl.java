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
import com.school.sba.exceptionhandler.DataNotPresentException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.SubjectRepo;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SubjectResponse;
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

	@Autowired
	private ResponseStructure<AcademicProgramResponse> responseStructure;

	private SubjectResponse mapToSubjectResponse(Subject subject) {
		return SubjectResponse.builder().subjectId(subject.getSubjectId()).subjectName(subject.getSubjectName())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> insertSubjectsIntoAcademicProgram(
			SubjectRequest subjectRequest, int academicProgramId) {

		return academicProgramRepo.findById(academicProgramId).map(academicProgram -> {
			List<Subject> subjects = (academicProgram.getSubjects() != null) ? academicProgram.getSubjects()
					: new ArrayList<>();

			subjectRequest.getSubjectNames().forEach(name -> {
				boolean isPresent = false;
				for (Subject subject : subjects) {
					isPresent = (name.equalsIgnoreCase(subject.getSubjectName())) ? true : false;
					if (isPresent)
						break;
				}
				if (!isPresent) {
					subjects.add(subjectRepo.findBySubjectName(name)
							.orElseGet(() -> subjectRepo.save(Subject.builder().subjectName(name).build())));
				}
			});
			List<Subject> toBeRemoved = new ArrayList<>();
			subjects.forEach(subject -> {
				boolean isPresent = false;
				for (String name : subjectRequest.getSubjectNames()) {
					isPresent = (subject.getSubjectName().equalsIgnoreCase(name)) ? true : false;
					if (isPresent)
						break;
				}
				if (!isPresent)
					toBeRemoved.add(subject);
			});
			subjects.removeAll(toBeRemoved);

			ResponseStructure<AcademicProgramResponse> responseStructure = new ResponseStructure<>();
			responseStructure.setStatusCode(HttpStatus.CREATED.value());
			responseStructure.setMessage("Updated the Subject list to Academic Program");
			responseStructure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(academicProgram));

			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,
					HttpStatus.CREATED);
		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("AcademicProgram Data Not Found By Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<SubjectResponse>>> findAllSubjects() {
		List<Subject> subjects = subjectRepo.findAll();
		if (!subjects.isEmpty()) {
			List<SubjectResponse> list = new ArrayList<>();

			for (Subject subject : subjects) {
				list.add(mapToSubjectResponse(subject));
			}

			ResponseStructure<List<SubjectResponse>> structure = new ResponseStructure<>();

			structure.setStatusCode(HttpStatus.FOUND.value());
			structure.setMessage("Subjects Found");
			structure.setData(list);

			return new ResponseEntity<ResponseStructure<List<SubjectResponse>>>(structure, HttpStatus.FOUND);

		} else
			throw new DataNotPresentException("No Subjects Present");
	}

// ///// @2nd approach for update Subjects To Academic Program
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjectsToAcademicProgram(
			SubjectRequest subjectRequest, int academicProgramId) {
		return academicProgramRepo.findById(academicProgramId).map(academicProgram -> {
			List<String> newsubList = new ArrayList<>();
			List<Subject> mached = new ArrayList<>();
			for (Subject subject : academicProgram.getSubjects()) {
				for (String subjectName : subjectRequest.getSubjectNames()) {

					if (subject.getSubjectName().equals(subjectName.toLowerCase())) {
						mached.add(subject);
						break;
					} else if (!subject.getSubjectName().equals(subjectName.toLowerCase())) {
						newsubList.add(subjectName);
					}
				}
			}
			SubjectRequest newSubReq = convertToSubReq(newsubList);
			List<Subject> subjectList = convertToSubjects(newSubReq);
			mached.addAll(subjectList);
			academicProgram.setSubjects(null);
			academicProgramRepo.save(academicProgram);
			academicProgram.setSubjects(mached);
			academicProgramRepo.save(academicProgram);

			ResponseStructure<AcademicProgramResponse> responseStructure = new ResponseStructure<>();
			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setMessage("Updated the Subject list to Academic Program");
			responseStructure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(academicProgram));

			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure, HttpStatus.OK);
		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("AcademicProgram Data Not Found By Id"));
	}

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

	private SubjectRequest convertToSubReq(List<String> subjectNames) {
		SubjectRequest newSubReq = new SubjectRequest();
		newSubReq.setSubjectNames(subjectNames);
		return newSubReq;
	}

}
