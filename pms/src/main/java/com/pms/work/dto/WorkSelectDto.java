package com.pms.work.dto;

import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Alias("WorkSelectDto")
public class WorkSelectDto {
	private String projectCode;
	private Integer projectNo;
	private String projectName;
	private String userId; // 현재 로그인 된 유저 id 정보, 일감 작성자
	private String username; // 실제 유저 이름
	private String managerId; // 일감 담당자
	private Integer progress; // 일감 진척도
	private Integer jobNo; // 일감 번호
	private String title;  // 일감 제목
	private Integer workEntriesNo;
	private String workers;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date workDate;
	private Integer workTime;
	private String workContent;
	private Integer workDetailsNo; 
	private String workType; 

}
