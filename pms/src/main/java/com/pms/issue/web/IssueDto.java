package com.pms.issue.web;

import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Alias("IssueDto")
public class IssueDto {
	private Integer jobNo;
   @NotBlank
	private String managerId;
   @NotNull
	private Integer publicRole;
   @NotBlank
	private String title;
	private String content;
	@NotNull
	private Integer priority;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date addDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;
	private Integer progress;
	@NotBlank
	private String userId;
	private String username;
	@NotBlank
	private String jobTypeNo;
	private String jobType;
	@NotBlank
	private String jobStatusNo;
	private String jobStatus;
	private String projectNo;
	private String projectCode;
	private String projectName;
	private Integer parentJobNo;
	private Integer filesNo;
    private Integer commonNo;
    private String commonName;
    
    // 히스토리
    private Integer historyNo;
    private String comment;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date historyDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date deleteDate;
    
}
