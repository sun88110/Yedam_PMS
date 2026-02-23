package com.pms.project.dto;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Alias("IssueTrackerDTO")
public class IssueTrackerDTO {
	// 테이블 <thead>에 뿌려줄 동적 상태 목록 (예: ["준비", "진행", "완료", ...])
    private List<String> headers; 
    
    // 테이블 <tbody>에 뿌려줄 피벗 데이터 
    // Key: "JOB_TYPE" 또는 상태이름("준비", "완료"), Value: 카운트 숫자
    private List<Map<String, Object>> rows;
}
