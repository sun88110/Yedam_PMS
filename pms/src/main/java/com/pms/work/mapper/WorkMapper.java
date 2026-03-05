package com.pms.work.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.pms.work.dto.WorkInsertDto;
import com.pms.work.dto.WorkReportDto;
import com.pms.work.dto.WorkSelectDto;
import com.pms.work.dto.WorkUpdateDto;


@Mapper
public interface WorkMapper {
	// 소요시간 전체 조회 + 검색조건 추가 후 조회
	List<WorkSelectDto> selectWorkEntries(WorkSelectDto workSelectDto);
	
	// 소요시간 등록 페이지 프로젝트의 일감 조회
	List<WorkInsertDto> selectIssueInProject(WorkInsertDto workInsertDto);
	// 소요시간 등록 페이지 작업분류 조회
	List<WorkInsertDto> selectWorkDetails(String workEntriesNo);
	// 소요시간 등록 기능 매개변수 타입이 WorkInsertDto  insert된 row 행의 개수
	int insertWorkEntries(WorkInsertDto workInsertDto);
	
	// 소요시간 수정 페이지 수정을 위한 상세조회 단건조회 List안쓴다
	WorkUpdateDto selectWorkEntriesByNo(WorkUpdateDto workUpdateDto);
	// 소요시간 수정 기능
	int updateWorkEntries(WorkUpdateDto workUpdateDto);
	
	// 일감별 소요시간 보고서
	List<WorkReportDto> selectJobReport(WorkReportDto workReportDto);
	// 프로젝트별 소요시간 보고서
	List<WorkReportDto> selectProjectReport(WorkReportDto workReportDto);
	// 작업분류 별 소요시간 보고서
	List<WorkReportDto> selectWorkTypeReport(WorkReportDto workReportDto);
	// 사용자별 소요시간 보고서
	List<WorkReportDto> selectUserReport(WorkReportDto workReportDto);
	// 프로젝트 주별 소요시간 보고서
	List<WorkReportDto> selectWeekReport(WorkReportDto workReportDto);
	// 프로젝트 월별 소요시간 보고서
	List<WorkReportDto> selectMonthReport(WorkReportDto workReportDto);
}
