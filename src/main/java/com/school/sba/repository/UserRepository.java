package com.school.sba.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Integer>{

	User findByUserRole(UserRole userRole);

	boolean existsByUserRole(UserRole userRole);

	Optional<User> findByUserName(String userName);
	
	List<User> findByUserRoleAndAcademicPrograms_AcademicProgramId(UserRole userRole,int academicProgramId);

	List<User> findByIsDeletedTrue();
	
	List<User> findByUserRoleNot(UserRole userRole);
}
