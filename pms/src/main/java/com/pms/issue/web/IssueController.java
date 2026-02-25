package com.pms.issue.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pms.config.CustomUserDetails;
import com.pms.files.dto.FileListDto;
import com.pms.files.service.FileListService;
import com.pms.issue.service.IssueService;
import com.pms.project.service.ProjectService;
import com.pms.user.entity.UserEntity;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project/user/{projectCode}/issue")
public class IssueController {

	private final IssueService issueService;
	private final FileListService fileListService;
	private final ProjectService projectService;

	// 일감 전체 리스트
	@GetMapping("/list")
	public String findIssueList(@AuthenticationPrincipal CustomUserDetails customUser, 
											@PathVariable String projectCode,
											Model model, 
											IssueSelectDto issueSelectDto, 
											@RequestParam(value = "showOnlyMe", required = false) String showOnlyMe) {
		UserEntity user = customUser.getUserEntity();

		issueSelectDto.setProjectCode(projectCode);
		// 내 일감만 보기
		if ("Y".equals(showOnlyMe)) {
			//  체크를 하면 id를 가져와서 내것만 보여줌
			issueSelectDto.setUserId(user.getUserId());
		} else {
			// projectCode가 고정으로 필터링 되니까 id 값 null줘도 
			// 전체 소요시간이 아닌 프로젝트의 전체 소요시간으로 나올 수 있다
			issueSelectDto.setUserId(null);
		}

		model.addAttribute("userId", user.getUserId());
		model.addAttribute("projectCode", projectCode);
		model.addAttribute("issueList",  issueService.findIssueList(issueSelectDto));
		
		return "issue/issue-list";
	}

	// 일감 등록 form
	@GetMapping("/new")
	public String newIssueForm(@PathVariable String projectCode, 
			Model model, 
			@ModelAttribute("issue") IssueDto issueDto) {
		
		issueDto.setProjectCode(projectCode);
		// 일감 상태 목록
		List<IssueDto> statusList = issueService.getStatusList(issueDto);
		// 일감 유형 목록
		List<IssueDto> typeList = issueService.getTypeList(issueDto);
		// 우선순위 목록
		List<IssueDto> priorityList = issueService.getPriorityList(issueDto);
		// 프로젝트 참여중인 멤버 목록
		List<IssueDto> managerList = issueService.getManagerList(issueDto);
		// 상위 일감 목록
		List<IssueDto> parentIssueList = issueService.getParentIssueList(issueDto);
		
		// model 에 담아서 보냄
		model.addAttribute("statusList", statusList);
		model.addAttribute("typeList", typeList);
		model.addAttribute("priorityList", priorityList);
		model.addAttribute("managerList", managerList);
		model.addAttribute("parentIssueList", parentIssueList);
		model.addAttribute("projectCode", projectCode);
		model.addAttribute("project", projectService.findInfoByCode(projectCode));
		
		return "issue/issue-insert";
	}

	// 일감 등록 기능
	@PostMapping("/new")
	public String addIssue(@AuthenticationPrincipal CustomUserDetails customUser, 
			@Valid @ModelAttribute("issue") IssueDto issueDto, 
			BindingResult bindingResult,
			@RequestParam("files") List<MultipartFile> files, 
			RedirectAttributes redirectAttributes, 
			@PathVariable String projectCode,
			Model model) {
		
		 if (bindingResult.hasErrors()) { 
			 System.out.println("에러 발생 필드: " + bindingResult.getFieldErrors());
				issueDto.setProjectCode(projectCode);
				// 일감 상태 목록
				List<IssueDto> statusList = issueService.getStatusList(issueDto);
				// 일감 유형 목록
				List<IssueDto> typeList = issueService.getTypeList(issueDto);
				// 우선순위 목록
				List<IssueDto> priorityList = issueService.getPriorityList(issueDto);
				// 프로젝트 참여중인 멤버 목록
				List<IssueDto> managerList = issueService.getManagerList(issueDto);
				// 상위 일감 목록
				List<IssueDto> parentIssueList = issueService.getParentIssueList(issueDto);
				// model 에 담아서 보냄
				model.addAttribute("statusList", statusList);
				model.addAttribute("typeList", typeList);
				model.addAttribute("priorityList", priorityList);
				model.addAttribute("managerList", managerList);
				model.addAttribute("parentIssueList", parentIssueList);
				model.addAttribute("projectCode", projectCode);
				return "issue/issue-insert";
		 }

		try {
			issueDto.setUserId(customUser.getUsername());
			Integer jobNo = issueService.addIssue(issueDto, files);
			return "redirect:/project/user/" + projectCode + "/issue/info?jobNo=" + jobNo;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/project/user/" + projectCode + "/issue/new";
		}
	}
	
	// 일감 수정
	@PutMapping("/update")
	public String modifyIssue(
					@AuthenticationPrincipal CustomUserDetails customUser,
					@PathVariable String projectCode,
					IssueDto issueDto,
					@RequestParam(value = "deleteFiles", required = false) List<Integer> deleteFiles,
					@RequestParam(value = "files", required = false) List<MultipartFile> newFiles,
					RedirectAttributes redirectAttributes) {
		try {
			issueDto.setUserId(customUser.getUsername());
			issueService.modifyIssue(issueDto, deleteFiles, newFiles);
			redirectAttributes.addFlashAttribute("message", "일감이 수정되었습니다.");
			return "redirect:/project/user/" + projectCode + "/issue/info?jobNo=" + issueDto.getJobNo();
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "일감이 수정 중 오류가 발생하였습니다.");
			return "redirect:/project/user/" + projectCode + "/issue/info?jobNo=" + issueDto.getJobNo();
		}		
	}
		
	// 일감 수정 form
	@GetMapping("/update")
	public String issueModifyPage(@AuthenticationPrincipal CustomUserDetails customUser,
			                                    @PathVariable String projectCode,
			                                    @RequestParam("jobNo") Integer jobNo, 
			                                    Model model) {
		
		UserEntity user = customUser.getUserEntity();
		IssueSelectDto issue =  issueService.findIssue(jobNo);
		// 첨부파일 목록 가져오는건 추후에 model.addAttribute("fileList", );
		model.addAttribute("statusList", issueService.getStatusList(null));
		model.addAttribute("typeList", issueService.getTypeList(null));
		model.addAttribute("priorityList", issueService.getPriorityList(null));
		model.addAttribute("issue", issue);
		model.addAttribute("userId", user.getUserId());
		model.addAttribute("projectCode", projectCode);
		model.addAttribute("project", projectService.findInfoByCode(projectCode));
		if (issue.getFilesNo() != null)
			model.addAttribute("fileList", fileListService.findFileList(Integer.parseInt(issue.getFilesNo())));
		else 
			model.addAttribute("fileList", new ArrayList<FileListDto>());
		return "issue/issue-modify";
	}
	// 일감 수정 기능
	
	// 일감 상세조회 form
	@GetMapping("/info")
	public String issueInfoPage(@AuthenticationPrincipal CustomUserDetails customUser,
			                                    @PathVariable String projectCode,
			                                    @RequestParam("jobNo") Integer jobNo, 
			                                    Model model) {
		
		UserEntity user = customUser.getUserEntity();
		IssueSelectDto issue =  issueService.findIssue(jobNo);
		
		model.addAttribute("statusList", issueService.getStatusList(null));
		model.addAttribute("typeList", issueService.getTypeList(null));
		model.addAttribute("priorityList", issueService.getPriorityList(null));
		model.addAttribute("issue", issueService.findIssue(jobNo));
		model.addAttribute("userId", user.getUserId());
		model.addAttribute("projectCode", projectCode);
		model.addAttribute("project", projectService.findInfoByCode(projectCode));
		if (issue.getFilesNo() != null)
			model.addAttribute("fileList", fileListService.findFileList(Integer.parseInt(issue.getFilesNo())));
		else 
			model.addAttribute("fileList", new ArrayList<FileListDto>() );
		
		return "issue/issue-info";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
