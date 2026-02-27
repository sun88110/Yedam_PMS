package com.pms.issue.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.pms.issue.web.IssueDto;
import com.pms.issue.web.IssueSelectDto;

@Mapper
public interface IssueMapper {

	List<IssueSelectDto> selectIssueList(IssueSelectDto issueSelectDto);
	// 일감 단건 조회
	IssueSelectDto selectIssue(Integer jobNo);
	
	// 일감 상태 목록 조회
	List<IssueDto> selectIssueStatus(IssueDto issueDto);
	// 일감 유형 목록 조회
	List<IssueDto> selectIssueType(IssueDto issueDto);
	// 일감 우선순위 목록 조회
	List<IssueDto> selectIssuePriority(IssueDto issueDto);
	// 일감 담당자 선택
	List<IssueDto> selectIssueManager(IssueDto issueDto);
	// 프로젝트의 일감 조회
	List<IssueDto> selectParentIssue(IssueDto issueDto);
	
	// 일감 수정
	Integer updateIssue(IssueDto issueDto);
	
	// 히스토리 저장
	Integer insertIssueHistory(IssueDto issueDto);
	// 히스토리 조회
	List<IssueDto> selectHistoryList(IssueDto issueDto);
	
	// 일감 등록 기능
	Integer insertIssue(IssueDto issueDto);
	// 
	List<IssueSelectDto> selectDashboardIssueList(IssueSelectDto issueSelectDto);

}