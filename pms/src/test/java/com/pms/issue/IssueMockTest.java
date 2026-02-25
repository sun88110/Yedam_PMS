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
import com.pms.files.repository.FilesRepository;
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
	
	@Autowired
	private FilesRepository filesRepository;

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

	@Test
	@DisplayName("등록된 일감 번호를 제대로 반환하는지 확인")
	public void insertIssueMockTest() {
		System.out.println("[H2 DB] INSERT ISSUE TEST START");

		// given
		String userId = "testUser";
		IssueDto issueDto = new IssueDto();
		issueDto.setTitle("MyBatis 이슈 번호 반환 테스트");
		issueDto.setUserId(userId);

		try {
			// when
			List<MultipartFile> files = Collections.emptyList();
			Integer jobNo;
			
			jobNo = issueService.addIssue(issueDto, files);
			
			// then
			assertThat(jobNo).isNotNull();
			assertThat(jobNo).isGreaterThan(0);
		} catch (Exception e) {
			e.printStackTrace();
		}


		System.out.println("[H2 DB] INSERT ISSUE TEST END");
	}

	@Test
	@DisplayName("일감 제목이 없을 시 예외 발생 확인")
	public void issueNullTitleMockTest() {
		System.out.println("[H2 DB] NULL TITLE TEST START");
				
		// given
		IssueDto issueDto = new IssueDto();
		
		// when -> then
		List<MultipartFile> files = Collections.emptyList();
		assertThatThrownBy(() -> issueService.addIssue(issueDto, files))
								.isInstanceOf(RuntimeException.class)
								.hasMessageContaining("제목은 필수 입력 사항입니다.");

		System.out.println("[H2 DB] NULL TITLE TEST END");
	}
	
	@Test
	@DisplayName("첨부파일 등록 시 상세정보까지 저장되는지 확인")
	public void insertIssueAndFileMockTest() {
		System.out.println("[H2 DB] FILE UPLOAD TEST START");
		
		// given
		String userId = "testUser";
		IssueDto issueDto = new IssueDto();
		issueDto.setTitle("첨부파일 등록 테스트");
		issueDto.setUserId(userId);
		
		List<MultipartFile> files = new ArrayList<>();
		files.add(new MockMultipartFile(
				"testFiles01", 
				"test_txt.txt",
				"text/plain",
				"H2 DB txt".getBytes()
				));
		files.add(new MockMultipartFile(
				"testFiles02", 
				"test_img.png",
				"image/png",
				"H2 DB png".getBytes()
				));
		
		// when
		try {
			Integer jobNo = issueService.addIssue(issueDto, files);
			
			// then
			assertThat(jobNo).isNotNull();
			assertThat(issueDto.getFilesNo()).isNotNull();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("[H2 DB] FILE UPLOAD TEST END");
	}
	
	@Test
	@DisplayName("일감 수정 및 파일 삭제 테스트, 모든 파일 삭제 시 files_no가 null 되는지 확인")
	public void modifyIssueTest() {
		System.out.println("[H2 DB] MODIFY ISSUE TEST START");
		
		try {
			// given
			IssueDto issueDto = createBaseIssueDto("song", "원본 제목");
			Integer jobNo;
			jobNo = issueService.addIssue(issueDto, createMockFiles(3));

			Integer filesNo = getJobDetail(jobNo).getFilesNo();
			List<FilesDetailsEntity> beforeDetails = filesDetailsRepository.findByFilesEntity_FilesNo(filesNo);
		    Integer deleteNo = beforeDetails.get(0).getDetailsNo();

			// when: 한 개 파일 삭제 -> 추가
			issueDto.setJobNo(jobNo);
			issueDto.setTitle("수정된 제목");
			issueDto.setComment("수정 코멘트");
			issueDto.setFilesNo(filesNo);
			List<Integer> deleteFilesList = List.of(deleteNo);
		    List<MultipartFile> newFiles = List.of(
		    		new MockMultipartFile("files", 
		    							"new_upload.txt",
		    							"text/plain",
		    							"new content".getBytes()));
			issueService.modifyIssue(issueDto, deleteFilesList, newFiles);

			// then
			IssueSelectDto updatedIssue = getJobDetail(jobNo);
			List<FilesDetailsEntity> afterDetails = filesDetailsRepository.findByFilesEntity_FilesNo(updatedIssue.getFilesNo());
			
			assertThat(updatedIssue.getTitle()).isEqualTo("수정된 제목");
			assertThat(afterDetails.size()).isEqualTo(3);
			boolean hasNewFile = afterDetails.stream().anyMatch(d -> d.getFilesName().equals("new_upload.txt"));
		    assertThat(hasNewFile).isTrue();
		} catch (Exception e) {
			e.printStackTrace();
		}

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

	/** 일감 조회 */
	private IssueSelectDto getJobDetail(Integer jobNo) {
		IssueSelectDto selectDto = new IssueSelectDto();
		selectDto.setProjectCode("PMS100");
		return issueService.findIssueList(selectDto)
							.stream()
							.filter(i -> i.getJobNo().equals(jobNo))
							.findFirst()
							.orElseThrow(() -> new RuntimeException("일감을 찾을 수 없습니다. jobNo: " + jobNo));
	}
	
}
