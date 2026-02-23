package com.pms.project.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // 추가
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pms.config.CustomUserDetails;
import com.pms.project.common.mapper.ProjectCommonStatusMapper;
import com.pms.project.dto.ProjectInsertDTO;
import com.pms.project.dto.ProjectSearchDTO; // 추가
import com.pms.project.service.ProjectService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    
    // 인터페이스 타입으로 주입받아 결합도를 낮춤
    private final ProjectService projectService;
    private final ProjectCommonStatusMapper projectCommonStatusMapper;
    
    @GetMapping("/")
    public String listProjects(Model model 
    		, @ModelAttribute ProjectSearchDTO searchDTO
    		, @AuthenticationPrincipal CustomUserDetails customUser 
    		) {
        // 실제 운영 시에는 세션 또는 SecurityContext에서 userId를 가져옴
        String currentUserId = customUser.getUserEntity().getUserId();
        
        
		/*
		 * if(customUser.getUserEntity().isAdmin() || 아니면 내가 pm) { // 내가 관리자 이거나 pm 이면
		 * 해당프로젝트내부의 모든 정보 열람 가능 };
		 */
        
        // 검색 조건이 있는지 확인 (projectName, projectStatus, projectAssignee 중 하나라도 값이 있으면 검색 조건으로 간주)
        boolean hasSearchCriteria = searchDTO.getProjectName() != null && !searchDTO.getProjectName().isEmpty() ||
                                    searchDTO.getProjectStatus() != null ||
                                    searchDTO.getProjectAssignee() != null && !searchDTO.getProjectAssignee().isEmpty();

        if (hasSearchCriteria) {
            // 검색 조건이 있으면 검색 결과 반환
            // 현재 로그인 사용자 ID를 searchDTO에 추가하여 쿼리에서 활용 (예: has_login_user_joined 필드)
            searchDTO.setCurrentUserId(currentUserId); // ProjectSearchDTO에 currentUserId 필드 추가 필요
            model.addAttribute("projects", projectService.findProjectByOptions(searchDTO));
        } else {
            // 검색 조건이 없으면 사용자 프로젝트 전체 목록 반환
            model.addAttribute("projects", projectService.findUserProjects(currentUserId));
        }
        
        model.addAttribute("commons" , projectCommonStatusMapper.selectProjectCommonStatusAll());
        model.addAttribute("searchDTO", searchDTO); // 검색 폼의 값 유지를 위해 모델에 추가
        
        return "project/list";
    }
    
	// 새 프로젝트 등록화면 불러오기
    @GetMapping("/new")
    public String getAddProject(Model model) {
    	model.addAttribute("project", new ProjectInsertDTO()); // 전달받을 값을 입력하기위한 빈 객체
    	model.addAttribute("parentProjects", projectService.findParentProjects()); // 빈객체에 입력할 상속 가능한 프로젝트 목록
        model.addAttribute("mode", "new"); // 모드 구분
        
    	return "project/insert-form";
    }
    
    // 프로젝트 입력 처리
    @PostMapping("/new")
    public String addProject(
    		@ModelAttribute ProjectInsertDTO dto
    		, @AuthenticationPrincipal CustomUserDetails customUser 
    		, RedirectAttributes redirectAttributes
    		, @RequestParam(name = "continue", required = false) String continueParam) {
    	// 로그인 사용자 정보에서 id 추출
    	dto.setUserId(customUser.getUserEntity().getUserId()); 
    	
    	if(!projectService.findParentProjectDuration(dto)) {
    		redirectAttributes.addFlashAttribute("errorMessage", "하위프로젝트의 작업기간은 상위프로젝트의 작업기간을 벗어날 수 없습니다.");
			redirectAttributes.addFlashAttribute("project", dto);
			return "redirect:/project/new";
    	}
    	
    	boolean isSuccess = projectService.addProject(dto);
    	if (isSuccess) {
    		// 임시 flash 메모리에 Toast 표시값 저장 
    		redirectAttributes.addFlashAttribute("successMessage", "프로젝트가 정상적으로 등록 되었습니다.");
    		// 만들기 : 만들고 계속하기
    		return continueParam != null ? "redirect:/project/new" : "redirect:/project/";
		}else {
			redirectAttributes.addFlashAttribute("errorMessage", "중복되는 식별자는 등록할 수 없습니다.");
			redirectAttributes.addFlashAttribute("project", dto);
			return "redirect:/project/new";
		}
    }
    
    
    // @PathVariable: 단일값 처리 + 매개변수에 어노테이션선언으로 필수값 선언, 반드시 받을거라 default 사용하지않기로
    @GetMapping("/user/{projectCode}/info")
    public String getProjectInfo(@PathVariable String projectCode, Model model, HttpSession session) {
    	// 세션을 활용하여 pathVal 사용하지않는 페이지에서 프로젝트 코드값 조회
    	session.setAttribute("projectCode", projectCode);
    	
    	model.addAttribute("trackerData", projectService.findJobTrackerPivot(projectCode));
    	model.addAttribute("groupMembers", projectService.findGroupMemberByCode(projectCode));
    	model.addAttribute("childProjects", projectService.findFirstChildsByCode(projectCode));
		model.addAttribute("news", projectService.findNoties());
    	
		return "project/info";
    }
    
    
    // 프로젝트 list -> settings.project 로 이동
    @GetMapping("/user/{projectCode}/edit")
    public String getEditProject(@PathVariable String projectCode) {
    	return "null";
    }
    
    @GetMapping("/user/{projectCode}/gantt")
    public String getGantProject(@PathVariable String projectCode, Model model) {
    	model.addAttribute("ganttData", projectService.findGanttDataByCode(projectCode));
    	
    	return "project/gantt";
    }
}