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

import com.pms.issue.service.IssueService;
import com.pms.issue.web.IssueDto;

@SpringBootTest
@Transactional
public class IssueMockTest {

	@Autowired
	private IssueService issueService;

	/*
	@Test
	@DisplayName("접속한 사용자의 일감을 가져오는지 확인")
	public void findIssueMockTest() {
		System.out.println("[H2 DB] FIND ISSUE TEST START");

		// given
		String userId = "testUser";
		IssueDto issue = new IssueDto();
		issue.setUserId(userId);
		issue.setTitle("테스트 이슈");
		List<MultipartFile> files = Collections.emptyList();
		issueService.addIssue(issue, files);
		
		// when
		List<IssueDto> issueList = issueService.findIssueList(userId);

		// then
		assertThat(issueList).isNotEmpty();
		assertThat(issueList.get(0).getTitle()).contains("테스트 이슈");

		System.out.println("[H2 DB] FIND ISSUE TEST END");
	}
	*/

	@Test
	@DisplayName("등록된 일감 번호를 제대로 반환하는지 확인")
	public void insertIssueMockTest() {
		System.out.println("[H2 DB] INSERT ISSUE TEST START");

		// given
		String userId = "testUser";
		IssueDto issueDto = new IssueDto();
		issueDto.setTitle("MyBatis 이슈 번호 반환 테스트");
		issueDto.setUserId(userId);

		// when
		List<MultipartFile> files = Collections.emptyList();
		Integer jobNo = issueService.addIssue(issueDto, files);

		// then
		assertThat(jobNo).isNotNull();
		assertThat(jobNo).isGreaterThan(0);

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
			System.out.println("일감 번호: " + jobNo);
			System.out.println("파일 번호: " + issueDto.getFilesNo());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println("[H2 DB] FILE UPLOAD TEST END");
	}
	
}
