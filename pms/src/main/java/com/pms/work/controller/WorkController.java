package com.pms.work.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pms.config.CustomUserDetails;
import com.pms.project.service.ProjectService;
import com.pms.user.entity.UserEntity;
import com.pms.work.dto.WorkInsertDto;
import com.pms.work.dto.WorkReportDto;
import com.pms.work.dto.WorkSelectDto;
import com.pms.work.dto.WorkUpdateDto;
import com.pms.work.service.WorkService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project/user/{projectCode}/work")
public class WorkController {
	private final WorkService workService;
	private final ProjectService projectService;

	// 소요시간 전체 조회 + 검색기능
	@GetMapping("/list")
	public String workList(@AuthenticationPrincipal CustomUserDetails customUser, 
									@PathVariable String projectCode,
									@RequestParam(value = "showOnlyMe", required = false) String showOnlyMe,
									Model model, 
									WorkSelectDto workSelectDto) {
		// showOnlyMe 가 필수는 아니라 required = false임 없어도 controller는 작동해야 하니까
		
		// 현재 로그인 정보가 담긴 커스텀 객체
		UserEntity user = customUser.getUserEntity();
		// 프로젝트 코드는 고정 
		workSelectDto.setProjectCode(projectCode);
		// 처음 프로젝트 생성하고 일감 생성하면 소요시간 정보가 없으니 화면에 아무것도 안나올 가능성이 있음
		// 프로젝트 이름만 따로 따서 조회
		
		// 내 소요시간만 보기 
		if ("Y".equals(showOnlyMe)) {
			//  체크를 하면 id를 가져와서 내것만 보여줌
			workSelectDto.setUserId(user.getUserId());
		} else {
			// projectCode가 고정으로 필터링 되니까 id 값 null줘도 
			// 전체 소요시간이 아닌 프로젝트의 전체 소요시간으로 나올 수 있다
			workSelectDto.setUserId(null);
		}

		// 전체 조회 + 검색조건 조회
		List<WorkSelectDto> workEntriesList = workService.findAllWorkEntries(workSelectDto);
		
		// 검색한 결과를 담아 보냄
		model.addAttribute("showOnlyMe", showOnlyMe);
		model.addAttribute("projectCode", projectCode);
		model.addAttribute("userId", user.getUserId());
		model.addAttribute("workType", workService.findWorkType(null));
		model.addAttribute("project", projectService.findInfoByCode(projectCode));
		model.addAttribute("workEntriesList", workEntriesList);
		return "work/work-list";
	}
	/*
	 * 예시)
	 * 
	 * @GetMapping("주소") public String example(@AuthenticationPrincipal
	 * CustomUserDetails customUser) { // 유저 엔티티 가져옴 UserEntity user =
	 * customUser.getUserEntity(); // 필드값 바로 사용 System.out.println("아이디: " +
	 * user.getUserId()); System.out.println("이메일: " + user.getEmail());
	 * System.out.println("이름: " + user.getUsername()); return "리턴값"; }
	 */

	// 소요시간 등록 화면 + 작업분류의 이름 가져오기 + 프로젝트의 일감 번호
	@GetMapping("/insert")
	public String workAddPage(@AuthenticationPrincipal CustomUserDetails customUser, 
											 @PathVariable String projectCode, 
											 Model model,
											 @Valid @ModelAttribute("work") WorkInsertDto workInsertDto) {
	    //  현재 로그인한 사용자 정보
		UserEntity user = customUser.getUserEntity();

		workInsertDto.setProjectCode(projectCode);
		workInsertDto.setUserId(user.getUserId());
		
		// session에 저장된 userId라는 데이터를 찾아옴 서버는 그냥 Object로 해서 주기에 강제 타입변환
		model.addAttribute("projectCode", projectCode);
		model.addAttribute("userId", user.getUserId());
		model.addAttribute("project", projectService.findInfoByCode(projectCode));
		model.addAttribute("issueList", workService.findMyIssue(workInsertDto));
		model.addAttribute("workType", workService.findWorkType(null));
	
		System.out.println("조회된 일감" + workService.findMyIssue(workInsertDto));
		return "work/work-add";
	}
	// 쇼요시간 등록 기능
	@PostMapping("/insert")
	public String workAdd(@PathVariable String projectCode, 
									  WorkInsertDto workInsertDto) {
		workService.addWorkEntries(workInsertDto);
		return "redirect:/project/user/" + projectCode + "/work/list";
	}

	// 소요시간 수정화면 이미 등록된 정보를 서버가 제공 + 작업분류 가져오기
	@GetMapping("/update")
	public String workModifyPage(@AuthenticationPrincipal CustomUserDetails customUser, 
												 @PathVariable String projectCode, 
												 @RequestParam("workNo") Integer workEntriesNo,
												 Model model, 
												 WorkUpdateDto workUpdateDto) {
		
		UserEntity user = customUser.getUserEntity();
	
		workUpdateDto.setProjectCode(projectCode);
		workUpdateDto.setWorkEntriesNo(workEntriesNo);

		model.addAttribute("userId", user.getUserId());
		model.addAttribute("projectCode", projectCode);
		model.addAttribute("project", projectService.findInfoByCode(projectCode));
		model.addAttribute("workType", workService.findWorkType(workUpdateDto.getWorkType()));
		model.addAttribute("workDetails", workService.findWorkEntriesByNo(workUpdateDto));
		return "work/work-modify";
	}
	// 소요시간 수정 기능
	@PostMapping("/update")
	public String workModify(@AuthenticationPrincipal CustomUserDetails customUser, 
										  @PathVariable String projectCode,
										  @RequestParam("workEntriesNo") Integer workEntriesNo,
										  WorkUpdateDto workUpdateDto,
										  HttpServletResponse response) throws Exception {
		
		WorkUpdateDto originData = workService.findWorkEntriesByNo(workUpdateDto);
		
		if (originData == null) {
			return "redirect:/project/user/" + projectCode + "/work/list";
		}
		// 현재 로그인 한 사용자 Id 가져와서 
		UserEntity user = customUser.getUserEntity();
		// 권한 확인 admin
		boolean isAdmin = user.isAdmin();
		
		// pm 여부 추가
		
		// 본인
		boolean isMine = user.getUserId().equals(originData.getUserId());
		
		// admin 아니고 본인 것도 아니면 권한없음 403으로
		if (!isAdmin && !isMine) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		// admin 이거나 본인이면 넘어감
		
		// URL에서 받은 workEntries 변수 가져온다 
		// 기능 실행
		workUpdateDto.setWorkEntriesNo(workEntriesNo);
		workService.modifyWorkEntries(workUpdateDto);
		System.out.println(workUpdateDto);
		return "redirect:/project/user/" + projectCode + "/work/list";
	}

	// 소요시간 보고서
	@GetMapping("/report")
	public String workReport(@AuthenticationPrincipal CustomUserDetails customUser, 
										  @PathVariable String projectCode,
		WorkReportDto workReportDto, Model model) {
		
		UserEntity user = customUser.getUserEntity();

		workReportDto.setProjectCode(projectCode);
		workReportDto.setUserId(user.getUserId());

		// type은 job, project, users, week, month
		String type = workReportDto.getType();
		// 보고서 페이지에서 조회를 눌렀을 때 type이 존재하면
		if (type != null && !type.isEmpty()) {
			// type이 "field-job,field-project" 여러개 넘어오면 인덱스 0번째 를 기준으로 처리
			if (type.contains(",")) {
				type = type.split(",")[0];
			}
			
			List<WorkReportDto> reportList = workService.findWorkReport(type, workReportDto);
						
			model.addAttribute("reportList", reportList);
			model.addAttribute("reportType", workReportDto.getType());
		}
		// model에 담아서 보
		model.addAttribute("userId", user.getUserId());
		model.addAttribute("projectCode", projectCode);
		model.addAttribute("project", projectService.findInfoByCode(projectCode));
		return "work/work-report";

	}

}
