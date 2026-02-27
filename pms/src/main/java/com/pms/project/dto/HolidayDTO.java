package com.pms.project.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Alias("HolidayDTO")
public class HolidayDTO implements Serializable {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date holidayDt;
	private String isHoliday;
	private String holiday_name;
}

//@JsonProperty("end_date");