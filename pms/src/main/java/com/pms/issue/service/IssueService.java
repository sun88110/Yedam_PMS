package com.pms.issue.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.pms.files.repository.FilesDetailsRepository;
import com.pms.files.service.FilesDeleteService;
import com.pms.files.service.FilesUploadService;
import com.pms.files.util.FilesHasUtil;
import com.pms.issue.mapper.IssueMapper;
import com.pms.issue.web.IssueDto;
import com.pms.issue.web.IssueSelectDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueService {


	private final FilesUploadService filesUploadService;
	private final FilesDeleteService filesDeleteService;
	private final FilesDetailsRepository filesDetailsRepository;
	private final FilesHasUtil filesHasUtil;
	private final IssueMapper issueMapper;


	// 일감 리스트 전체 조회 + 조건 검색기능
	public List<IssueSelectDto> findIssueList(IssueSelectDto issueSelectDto) {
		List<IssueSelectDto> issueList = issueMapper.selectIssueList(issueSelectDto);
		return issueList;
	}
	// 일감 단건 조회
	public IssueSelectDto findIssue(Integer jobNo) {
		IssueSelectDto issue = issueMapper.selectIssue(jobNo);
		return issue;
	}

	// 일감 등록 화면에 필요한 데이터들을 각각 조회
	// 일감 상태
	public List<IssueDto> getStatusList(IssueDto issueDto) {
		return issueMapper.selectIssueStatus(issueDto);
	}
	// 일감 유형
	public List<IssueDto> getTypeList(IssueDto issueDto) {
		return issueMapper.selectIssueType(issueDto);
	}
	// 우선순위
	public List<IssueDto> getPriorityList(IssueDto issueDto) {
		return issueMapper.selectIssuePriority(issueDto);
	}
	// 프로젝트에 소속된 멤버 조회
	public List<IssueDto> getManagerList(IssueDto issueDto) {
		return issueMapper.selectIssueManager(issueDto);
	}
	// 프로젝트에 등록된 일감 조회
	public List<IssueDto> getParentIssueList(IssueDto issueDto) {
		return issueMapper.selectParentIssue(issueDto);
	}

	// 일감 등록
	@Transactional
	public Integer addIssue(IssueDto issueDto, List<MultipartFile> files) throws Exception {
		if (!StringUtils.hasText(issueDto.getTitle())) {
			throw new RuntimeException("제목은 필수 입력 사항입니다.");
		}

		String userId = issueDto.getUserId();
		Integer filesNo = filesUploadService.uploadFiles(files, userId, issueDto.getFilesNo());
		issueDto.setFilesNo(filesNo);

		issueMapper.insertIssue(issueDto);
		Integer jobNo = issueDto.getJobNo();

		return jobNo;
	}
	
	// 일감 수정
	@Transactional
	public void modifyIssue(IssueDto issueDto, 
							List<Integer> deleteFileList,
							List<MultipartFile> newFiles) throws Exception {
		// 파일 삭제
		if(filesHasUtil.hasDeleteFiles(deleteFileList)) {
			filesDeleteService.deleteFiles(deleteFileList);
		}
		
		// 파일 업로드
		Integer saveFilesNo = filesUploadService.uploadFiles(newFiles, issueDto.getUserId(), issueDto.getFilesNo());
		issueDto.setFilesNo(saveFilesNo);
		
		// 일감 업데이트 -> 히스토리 저장
		Optional.ofNullable(issueDto.getFilesNo())
	    		.filter(filesNo -> !filesDetailsRepository.existsByFilesEntity_FilesNo(filesNo))
	    		.ifPresent(filesNo -> issueDto.setFilesNo(null));
		issueMapper.updateIssue(issueDto);
		issueMapper.insertIssueHistory(issueDto);
	}

}
