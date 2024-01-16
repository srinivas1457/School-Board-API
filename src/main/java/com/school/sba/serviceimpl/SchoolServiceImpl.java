package com.school.sba.serviceimpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.exceptionhandler.DataNotPresentException;
import com.school.sba.exceptionhandler.SchoolNotFoundByIdException;
import com.school.sba.exceptionhandler.SchoolNotFoundByNameException;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@Service
public  class SchoolServiceImpl implements SchoolService {
	
	@Autowired
	private SchoolRepository schoolRepo;
	
	@Autowired
	private ResponseStructure<SchoolResponse> responseStructure;
	
	private School convertToSchool(SchoolRequest schoolRequest, School school) {
		school.setSchoolName(schoolRequest.getSchoolName());
		school.setContactNum(schoolRequest.getContactNum());
		school.setEmail(schoolRequest.getEmail());
		school.setAddress(schoolRequest.getAddress());
		return school;
	}
	
	private SchoolResponse convertToSchoolResponse(School school) {
		SchoolResponse schoolResponse=new SchoolResponse();
		schoolResponse.setSchoolId(school.getSchoolId());
		schoolResponse.setSchoolName(school.getSchoolName());
		schoolResponse.setContactNum(school.getContactNum());
		schoolResponse.setEmail(school.getEmail());
		schoolResponse.setAddress(school.getAddress());
		
		return schoolResponse;
	}


	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(SchoolRequest schoolRequest) {
		School school=convertToSchool(schoolRequest,new School());
		School school2 = schoolRepo.save(school);
		
		SchoolResponse schoolResponse=convertToSchoolResponse(school2);
		
		responseStructure.setStatusCode(HttpStatus.CREATED.value());
		responseStructure.setMessage("School Data Inserted Successfully");
		responseStructure.setData(schoolResponse);
		
		return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure,HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> findBySchoolId(int schoolId) {
		Optional<School> optional = schoolRepo.findById(schoolId);
		if (optional.isPresent()) {
			SchoolResponse schoolResponse = convertToSchoolResponse(optional.get());
		
			responseStructure.setStatusCode(HttpStatus.FOUND.value());
			responseStructure.setMessage("School Data Found Successfully");
			responseStructure.setData(schoolResponse);
			
			return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure,HttpStatus.FOUND);
		} else {
			throw new SchoolNotFoundByIdException("School Not Found By Given Id");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<SchoolResponse>>> findBySchoolName(String schoolName) {
		List<School> schools=schoolRepo.findBySchoolName(schoolName);
		if (!schools.isEmpty()) {
			List<SchoolResponse>schoolResponses=new LinkedList<>();
			for (School school : schools) {
				SchoolResponse schoolResponse = convertToSchoolResponse(school);
				schoolResponses.add(schoolResponse);
			}
			ResponseStructure<List<SchoolResponse>> responseStructure=new ResponseStructure<>();
			responseStructure.setStatusCode(HttpStatus.FOUND.value());
			responseStructure.setMessage("Schools Data Found Successfully");
			responseStructure.setData(schoolResponses);
			
			return new ResponseEntity<ResponseStructure<List<SchoolResponse>>>(responseStructure,HttpStatus.FOUND);
		} else {
			throw new SchoolNotFoundByNameException("School Not Found By Given Name");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> updateById(SchoolRequest schoolRequest, int schoolId) {
		Optional<School> optional = schoolRepo.findById(schoolId);
		if (optional.isPresent()) {
			School school = convertToSchool(schoolRequest, optional.get());
			SchoolResponse schoolResponse = convertToSchoolResponse(school);
			
			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setMessage("School Data Updated Successfully");
			responseStructure.setData(schoolResponse);
			
			return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure,HttpStatus.OK);
		} else {
			throw new SchoolNotFoundByIdException("School Not Found By Given Id");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> deleteById(int schoolId) {
		Optional<School> optional = schoolRepo.findById(schoolId);
		if (optional.isPresent()) {
			SchoolResponse schoolResponse = convertToSchoolResponse(optional.get());
			
			schoolRepo.delete(optional.get());
		
			responseStructure.setStatusCode(HttpStatus.FOUND.value());
			responseStructure.setMessage("School Data Deleted Successfully");
			responseStructure.setData(schoolResponse);
			
			return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure,HttpStatus.FOUND);
		} else {
			throw new SchoolNotFoundByIdException("School Not Found By Given Id");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<SchoolResponse>>> findAll() {
		List<School> schools=schoolRepo.findAll();
		if (!schools.isEmpty()) {
			List<SchoolResponse>schoolResponses=new LinkedList<>();
			for (School school : schools) {
				SchoolResponse schoolResponse = convertToSchoolResponse(school);
				schoolResponses.add(schoolResponse);
			}
			ResponseStructure<List<SchoolResponse>> responseStructure=new ResponseStructure<>();
			responseStructure.setStatusCode(HttpStatus.FOUND.value());
			responseStructure.setMessage("Schools Data Found Successfully");
			responseStructure.setData(schoolResponses);
			
			return new ResponseEntity<ResponseStructure<List<SchoolResponse>>>(responseStructure,HttpStatus.FOUND);
			
		} else {
			throw new DataNotPresentException("School Data Not Present");
		}
	}

	

}
