package com.pms.project.dto;

import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString 
@Getter 
@Setter
@Alias("ProjectSelectDTO")
public class ProjectSelectDTO {
    // [1] 메인 SQL에서 조회되는 기본 컬럼
    private Integer projectNo;
    private String  projectCode;
    private String  projectName;
    private String  projectDesc;
    private String  projectHome; // 새로 추가됨
    
    private Integer status;
    private String  statusName; // FN_GET_CODE_NAME 결과
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate; // 시작일 추가
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;   // 종료일 추가
    
    private Integer publicYn;
    
    // [2] 계층형 구조를 위한 컬럼 (리스트 X, 레벨값 O)
    private Integer le;              // 레벨 (1, 2, 3...)
    private Integer parentProjectNo; // 부모 프로젝트 번호
    
    // [3] 최신 일감 제목
    private String latestJobTitle;
    
    // [4] 집계 프로젝트 PM여부
    private Boolean isPm;  
    
    // [4] 집계 데이터 (한 번의 서브쿼리로 싹 다 가져옴) 
    private ProjectTotalDTO projectTotalDTO;
    
}
