package com.school.sba.requestdto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExcelRequestDto {
	private LocalDate fromDate;
	private LocalDate toDate;
	private String filePath;
	

}
