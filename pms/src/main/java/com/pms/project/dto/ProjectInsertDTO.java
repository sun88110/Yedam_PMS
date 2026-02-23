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
@NoArgsConstructor // 클라이언트에게서 값을전달받을 빈 객체를 생성 가능하게 하기 위함, 식별자는 시퀀스를 사용
@Alias("ProjectInsertDTO")
public class ProjectInsertDTO {
	private Integer projectNo;  // 프로젝트 식별자(PK)
	private Integer parentProjectNo; // 상위프로젝트 식별자(FK)
	private String projectName;
	private String userId;      // 프로젝트 제작자(owner)
	private String projectDesc;
	private String projectHome;
	private String projectCode; // URI 식별자
	
	@DateTimeFormat(pattern = "yyyy-MM-dd") // 생성일
	private Date createAt;
	@DateTimeFormat(pattern = "yyyy-MM-dd") // 수정일
	private Date updateAt;
	@DateTimeFormat(pattern = "yyyy-MM-dd") // 프로젝트 시작일: 예상진행일 계산을위해 필요함
	private Date startDate; 
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;   
	
	private Integer parentMemberYn = 0; // 부모프로젝트맴버상속여부(0: False, 1: True): 부모프로젝트의 멤버를 상속할때 사용, 만들때만 사용
	private Integer publicYn; // 공개여부(0: False, 1: True): 1이면 프로젝트에 소속되어있지 않아도 조회 가능 
	private Integer status;   // 프로젝트 상태 코드 (330: 진행, 360: 종료, 390: 삭제)
}
