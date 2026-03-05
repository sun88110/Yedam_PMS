package com.pms.work.service;

import java.util.List;

import com.pms.work.dto.WorkInsertDto;
import com.pms.work.dto.WorkReportDto;
import com.pms.work.dto.WorkSelectDto;
import com.pms.work.dto.WorkUpdateDto;

public interface WorkService {
	// 소요시간 전체 조회 + 조건 검색기능
	List<WorkSelectDto> findAllWorkEntries(WorkSelectDto workSelectDto);
	
	//소요시간 등록 페이지 프로젝트의 일감 조회
	List<WorkInsertDto> findMyIssue(WorkInsertDto workInsertDto);
	// 소요시간 등록 페이지 작업분류 조회
	List<WorkInsertDto> findWorkType(String workEntriesNo);
	// 소요시간 등록 기능
	void addWorkEntries(WorkInsertDto workInsertDto);
	
	// 수정페이지 화면 단건조회
	WorkUpdateDto findWorkEntriesByNo(WorkUpdateDto workUpdateDto);
	// 소요시간 수정 기능 
	void modifyWorkEntries(WorkUpdateDto workUpdateDto);
	
    // 통합 보고서 조회 Method 
	// type 은 job, users, workType, week, month
	List<WorkReportDto> findWorkReport(String type, WorkReportDto workReportDto);

}
