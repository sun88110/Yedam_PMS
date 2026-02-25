package com.pms.project.dto;


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
@Alias("HistoryDTO")
public class HistoryDTO {
	private Integer historyNo;
	private String desc;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date historyDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date deleteDate;
	private Integer jobNo;
	private String userId;
	private String username;
	private String title;
	private String jobType;
	private Integer jobTypeNo;
	private String jobStatus;
	private Integer jobStatusNo;
}