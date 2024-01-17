package com.school.sba.requestdto;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.school.sba.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class UserRequest {
	@NotEmpty(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers")
	private String userName;

	@NotEmpty(message = "Password is required")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must"
			+ " contain at least one letter, one number, one special character")
	private String password;
	
	@NotEmpty(message = "User Name Can not be empty")
	@Pattern(regexp = "^[A-Z][a-z]*(?: [A-Z][a-z]*)?$", message = "Username should follow initcap")
	private String firstName;
	
	@NotEmpty(message = "User Name Can not be empty")
	@Pattern(regexp = "^[A-Z][a-z]*(?: [A-Z][a-z]*)?$", message = "Username should follow initcap")
	private String lastName;

	@Min(value = 6000000000l, message = "Phone Number should not start below '6' !!")
	@Max(value = 9999999999l, message = "Phone Number cannot be above 10 Digit !!")
	private long contactNo;

	@NotEmpty(message = "Invalid email")
	@Email(regexp = "[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}", message = "invalid email ")
	private String email;

	@NotEmpty(message = "User role cannot be empty")
	@Pattern(regexp = "^(ADMIN|TEACHER|STUDENT)$",message = "Plese mention ADMIN or STUDENT or TEACHER")
	private String userRole;
	
//	@NotBlank(message = "Date of birth is required")
//    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date of birth format. Use yyyy-MM-dd")
    @Past(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;
}
