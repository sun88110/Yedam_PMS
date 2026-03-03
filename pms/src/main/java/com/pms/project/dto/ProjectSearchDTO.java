package com.pms.project.dto;

import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Alias("ProjectSearchDTO")
public class ProjectSearchDTO {
    private String projectName;
    private String  projectCode;
    private Integer projectStatus; // 또는 String 타입, 상태 코드에 따라
    private String projectAssignee; // user_id : 프로젝트 생성자
    private String currentUserId; // 현재 로그인한 사용자 ID 추가
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    // 필요하다면 페이징 관련 필드 추가
    // private int page = 1;
    // private int pageSize = 10;
}
