package com.pms.issue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pms.files.entity.FilesDetailsEntity;
import com.pms.files.repository.FilesDetailsRepository;
import com.pms.issue.service.IssueService;
import com.pms.issue.web.IssueDto;
import com.pms.issue.web.IssueSelectDto;

@SpringBootTest
@Transactional
public class IssueMockTest {

	@Autowired
	private IssueService issueService;
	
	@Autowired
	private FilesDetailsRepository filesDetailsRepository;

	/*
	@Test
	@DisplayName("접속한 사용자의 일감을 가져오는지 확인")
	public void findIssueMockTest() {
		System.out.println("[H2 DB] FIND ISSUE TEST START");

		// given
		String userId = "testUser";
		String projectCode = "PMS100";
		Integer projectNo = 1;
		
		IssueDto issue = new IssueDto();
		issue.setUserId(userId);
		issue.setProjectCode(projectCode);
		issue.setProjectNo(projectNo);
		issue.setTitle("테스트 이슈");
		List<MultipartFile> files = Collections.emptyList();
		try {
			issueService.addIssue(issue, files);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// when
		IssueSelectDto selectDto = new IssueSelectDto();
		selectDto.setUserId(userId);
		selectDto.setProjectCode(projectCode);
		List<IssueSelectDto> issueList = issueService.findIssueList(selectDto);

		// then
		assertThat(issueList).isNotEmpty();
		assertThat(issueList.get(0).getTitle()).contains("테스트 이슈");

		System.out.println("[H2 DB] FIND ISSUE TEST END");
	}
	*/

	@Test
	@DisplayName("등록된 일감 번호를 제대로 반환하는지 확인")
	public void insertIssueMockTest() throws Exception {
		System.out.println("[H2 DB] INSERT ISSUE TEST START");
		// given
		String userId = "testUser";
		IssueDto issueDto = new IssueDto();
		issueDto.setTitle("MyBatis 이슈 번호 반환 테스트");
		issueDto.setUserId(userId);

		// when
		List<MultipartFile> files = Collections.emptyList();
		Integer jobNo;

		jobNo = issueService.addIssue(issueDto, files);

		// then
		assertThat(jobNo).isNotNull();
		assertThat(jobNo).isGreaterThan(0);

		System.out.println("[H2 DB] INSERT ISSUE TEST END");
	}

	@Test
	@DisplayName("일감 제목이 없을 시 예외 발생 확인")
	public void issueNullTitleMockTest() throws Exception {
		System.out.println("[H2 DB] NULL TITLE TEST START");

		// given
		IssueDto issueDto = new IssueDto();

		// when -> then
		List<MultipartFile> files = Collections.emptyList();
		assertThatThrownBy(() -> issueService.addIssue(issueDto, files)).isInstanceOf(RuntimeException.class)
				.hasMessageContaining("제목은 필수 입력 사항입니다.");

		System.out.println("[H2 DB] NULL TITLE TEST END");
	}
	
	@Test
	@DisplayName("첨부파일 등록 시 상세정보까지 저장되는지 확인")
	public void insertIssueAndFileMockTest() throws Exception {
		System.out.println("[H2 DB] FILE UPLOAD TEST START");
		// given
		String userId = "testUser";
		IssueDto issueDto = new IssueDto();
		issueDto.setTitle("첨부파일 등록 테스트");
		issueDto.setUserId(userId);

		List<MultipartFile> files = new ArrayList<>();
		files.add(new MockMultipartFile("testFiles01", "test_txt.txt", "text/plain", "H2 DB txt".getBytes()));
		files.add(new MockMultipartFile("testFiles02", "test_img.png", "image/png", "H2 DB png".getBytes()));

		// when

		Integer jobNo = issueService.addIssue(issueDto, files);

		// then
		assertThat(jobNo).isNotNull();
		assertThat(issueDto.getFilesNo()).isNotNull();

		System.out.println("[H2 DB] FILE UPLOAD TEST END");
	}
	
	@Test
	@DisplayName("일감 등록 후 파일 추가 업로드 시 부모 번호가 유지되는지 확인")
	public void modifyIssueTest() throws Exception {
		System.out.println("[H2 DB] MODIFY ISSUE TEST START");

		// given
		// 더미 데이터
		IssueDto issueDto = createBaseIssueDto("song", "원본 제목");
		List<MultipartFile> initialFiles = createMockFiles(3);

		// 일감 등록
		Integer jobNo = issueService.addIssue(issueDto, initialFiles);

		// 결과 조회
		IssueSelectDto savedIssue = issueService.findIssue(jobNo);
		Integer filesNo = savedIssue.getFilesNo();

		assertThat(filesNo).isNotNull();
		List<FilesDetailsEntity> firstDetails = filesDetailsRepository.findByFilesEntity_FilesNo(filesNo);
		assertThat(firstDetails).hasSize(3);

		// when
		// 파일 추가 등록
		IssueDto modifyDto = new IssueDto();
		modifyDto.setJobNo(jobNo);
		modifyDto.setTitle("수정된 제목");
		modifyDto.setUserId(issueDto.getUserId());
		modifyDto.setFilesNo(filesNo);
		modifyDto.setHistoryUserId(issueDto.getUserId());
		modifyDto.setJobTypeNo("1");
		modifyDto.setJobStatusNo("1");
		modifyDto.setProjectNo(1);
		modifyDto.setPriority(210);
		modifyDto.setPublicRole(1);

		List<MultipartFile> newFiles = List
				.of(new MockMultipartFile("files", "added_04.txt", "text/plain", "Added Content 4".getBytes()));

		// 수정
		issueService.modifyIssue(modifyDto, null, newFiles);

		// then
		// 검증
		// 파일 번호 확인
		IssueSelectDto updatedIssue = issueService.findIssue(jobNo);
		assertThat(updatedIssue.getFilesNo()).isEqualTo(filesNo);

		// 파일 개수 확인
		List<FilesDetailsEntity> finalDetails = filesDetailsRepository.findByFilesEntity_FilesNo(filesNo);
		assertThat(finalDetails).hasSize(4);

		System.out.println("[H2 DB] MODIFY ISSUE TEST END");
	}
	
	// 테스트 픽스처 메서드
	/** 테스트용 IssueDto (필수값) */
	private IssueDto createBaseIssueDto(String userId, String title) {
		IssueDto dto = new IssueDto();
		dto.setUserId(userId);
		dto.setTitle(title);
		dto.setProjectNo(1);
		dto.setProjectCode("PMS100");
		dto.setJobTypeNo("1");
	    dto.setJobStatusNo("1");
	    dto.setPriority(210);
	    dto.setPublicRole(1);
		return dto;
	}

	/** Mock 파일 리스트 */
	private List<MultipartFile> createMockFiles(int count) {
		List<MultipartFile> files = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			files.add(new MockMultipartFile("file" + i, "test" + i + ".txt", "text/plain", ("content" + i).getBytes()));
		}
		return files;
	}

}
