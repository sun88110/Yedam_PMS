package com.pms.work.dto;

import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("WorkReportDto")
public class WorkReportDto {
	private String type; // 보고서 종류 일감, 프로젝트, 사용자, 주월별 
	private String userId;
	private String username; // 담당자
	private String managerId; 
	// 프로젝트 정보
	private Integer projectNo;
	private String projectCode;
	private String projectName;
	// 일감정보 
	private Integer jobNo;
	private String title;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;
	// 작업분류
	private Integer workDetailsNo;
	private String workType;
	private String workers;
	private Integer workTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date workDate;
	// sql의 AS 별칭 붙인 값들
	private String workMonth;
	private String workWeek;
	private Integer realWorkTime; // 실제 근무한 시간
	private Integer workDays; // 근무 일수
	private Integer estimatedTime; // 근무시간 * 8
	private Integer averageTime; // 주,월 별 평균 시간
	
	

}
