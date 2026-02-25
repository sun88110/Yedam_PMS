package com.pms.issue.web;

import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("IssueSelectDto")
public class IssueSelectDto {
	private String projectCode;
	private String projectNo;
	private String projectName;
	private String userId;
	private String managerId;
	private String title;
	private String content;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;
	private String publicRole;
	private String priority;
	private String priorityName;
	private String progress;
	private String parentJobNo;
	private String jobTypeNo;
	private String jobType;
	private String jobNo;
	private String filesNo;
	private String jobStatusNo;
	private String jobStatus;
	private Integer filesNo;

}
