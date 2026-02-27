package com.pms.project.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // 추가
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pms.config.CustomUserDetails;
import com.pms.issue.mapper.IssueMapper;
import com.pms.issue.service.IssueService;
import com.pms.issue.web.IssueDto;
import com.pms.project.common.mapper.ProjectCommonStatusMapper;
import com.pms.project.dto.HistoryDTO;
import com.pms.project.dto.ProjectInsertDTO;
import com.pms.project.dto.ProjectSearchDTO; // 추가
import com.pms.project.service.ProjectService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    
    // 인터페이스 타입으로 주입받아 결합도를 낮춤
    private final ProjectService projectService;
    private final ProjectCommonStatusMapper projectCommonStatusMapper;
    private final IssueService issueService;
    
    @GetMapping("/read")
    public String listProjects(Model model 
    		, @ModelAttribute ProjectSearchDTO searchDTO
    		, @AuthenticationPrincipal CustomUserDetails customUser 
    		) {
        // 실제 운영 시에는 세션 또는 SecurityContext에서 userId를 가져옴
        String currentUserId = customUser.getUserEntity().getUserId();
        boolean isAdmin = customUser.getUserEntity().isAdmin();
        
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
            model.addAttribute("projects", projectService.findUserProjects(currentUserId, isAdmin));
        }
        
        model.addAttribute("commons" , projectCommonStatusMapper.selectProjectCommonStatusAll());
        model.addAttribute("searchDTO", searchDTO); // 검색 폼의 값 유지를 위해 모델에 추가
        model.addAttribute("admin", isAdmin);
        model.addAttribute("pm", projectService.findIsPM(currentUserId).size() > 0);
        
        return "project/list";
    }
    
	// 새 프로젝트 등록화면 불러오기
    @GetMapping("/create")
    public String getAddProject(Model model) {
    	model.addAttribute("project", new ProjectInsertDTO()); // 전달받을 값을 입력하기위한 빈 객체
    	model.addAttribute("parentProjects", projectService.findParentProjects()); // 빈객체에 입력할 상속 가능한 프로젝트 목록
        model.addAttribute("mode", "new"); // 모드 구분
        
    	return "project/insert-form";
    }
    
    // 프로젝트 입력 처리
    @PostMapping("/create")
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
			return "redirect:/project/create";
    	}
    	
    	boolean isSuccess = projectService.addProject(dto);
    	if (isSuccess) {
    		// 임시 flash 메모리에 Toast 표시값 저장 
    		redirectAttributes.addFlashAttribute("successMessage", "프로젝트가 정상적으로 등록 되었습니다.");
    		// 만들기 : 만들고 계속하기
    		return continueParam != null ? "redirect:/project/create" : "redirect:/project/read";
		}else {
			redirectAttributes.addFlashAttribute("errorMessage", "중복되는 식별자는 등록할 수 없습니다.");
			redirectAttributes.addFlashAttribute("project", dto);
			return "redirect:/project/create";
		}
    }
    
    
    // @PathVariable: 단일값 처리 + 매개변수에 어노테이션선언으로 필수값 선언, 반드시 받을거라 default 사용하지않기로
    @GetMapping("/user/{projectCode}/read")
    public String getProjectInfo(@PathVariable String projectCode, Model model, HttpSession session
    		, @AuthenticationPrincipal CustomUserDetails customUser) {
    	// 세션을 활용하여 pathVal 사용하지않는 페이지에서 프로젝트 코드값 조회
    	session.setAttribute("projectCode", projectCode);
    	
    	model.addAttribute("info", projectService.findInfoByCode(projectCode));
    	model.addAttribute("trackerData", projectService.findJobTrackerPivot(projectCode));
    	model.addAttribute("groupMembers", projectService.findGroupMemberByCode(projectCode));
    	model.addAttribute("childProjects", projectService.findFirstChildsByCode(projectCode));
		model.addAttribute("news", projectService.findNoties());
    	
		// 권한을 바탕으로 프로젝트 수정버튼이 보임
		model.addAttribute("admin", customUser.getUserEntity().isAdmin() );
        model.addAttribute("pm", projectService.findIsPM(customUser.getUserEntity().getUserId()).size() > 0);
		
		return "project/info";
    }
    
    // 최초 - 화면과 데이터를 한 번에 던져줌
    @GetMapping("/user/{projectCode}/issue/gantt/read")
    public String getGantProject(@PathVariable String projectCode, Model model, @AuthenticationPrincipal CustomUserDetails customUser) {
        String userId = customUser.getUserEntity().getUserId();
        // 서비스에서 병렬 처리된 전체 데이터를 받아옴
        Map<String, Object> initData = projectService.findGanttDataByCode(projectCode, userId);
        model.addAttribute("initData", initData);
        return "project/gantt";
    }

    // 데이터 갱신 - fetch 요청(API 방식)
    @GetMapping("/user/{projectCode}/issue/gantt/data/read")
    @ResponseBody 
    public Map<String, Object> getGanttDataApi(@PathVariable String projectCode, @AuthenticationPrincipal CustomUserDetails customUser) {
        String userId = customUser.getUserEntity().getUserId();
        // 동일한 병렬 처리 서비스를 호출하여 순수 JSON 데이터만 반환
        return projectService.findGanttDataByCode(projectCode, userId);
    }
	
    @PostMapping("/user/{projectCode}/issue/gantt/create")
    @ResponseBody
    public IssueDto addGanttData(
            @PathVariable String projectCode
            , @ModelAttribute IssueDto issueDto
            , @RequestParam("files") List<MultipartFile> files 
            , @AuthenticationPrincipal CustomUserDetails customUser) {
        
        // 1. 경로 변수로 들어온 프로젝트 코드 및 필수 데이터 세팅
        issueDto.setProjectCode(projectCode);

        // 2. 파일 처리 로직 (List<MultipartFile> files 등 DTO에 파일 필드가 있다면 여기서 처리)
		try {
			Integer jobNo = issueService.addIssue(issueDto, files);
			log.debug("jobNo check{}", jobNo);
		} catch (Exception e) {
			e.printStackTrace();
		}

        // 3. ★ 새 ID(jobNo)가 담긴 DTO 객체를 그대로 반환! (이게 프론트엔드의 savedTask로 들어갑니다)
        return issueDto;
    }
    
    @PutMapping("/user/{projectCode}/issue/gantt/update")
    @ResponseBody
    public ResponseEntity<?> updateGanttData(
    		@PathVariable String projectCode
            , @AuthenticationPrincipal CustomUserDetails customUser
            , @ModelAttribute IssueDto issueDto
            , @RequestParam(value = "deleteFiles", required = false) List<Integer> deleteFiles
            , @RequestParam(value = "files", required = false) List<MultipartFile> newFiles ){
    	
    	try {
            issueDto.setHistoryUserId(customUser.getUsername()); 

            // 2. 통합 브랜치의 서비스 로직 호출 (파일 삭제/추가, 데이터 업데이트, 히스토리 저장)
            issueService.modifyIssue(issueDto, deleteFiles, newFiles);
            
            // 3. 간트 차트 리렌더링을 위해 업데이트된 객체 정보 반환 (HTTP 200 OK)
            // 프론트엔드의 fetch .then(savedTask => { ... }) 로 전달됩니다.
            return ResponseEntity.ok(issueDto);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            // 4. 에러 발생 시 HTTP 500 응답 전송
            // 프론트엔드의 fetch .catch() 블록이 이를 낚아채서 "처리 중 오류가 발생했습니다" 메시지를 띄웁니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("간트 일감 수정 중 서버 오류가 발생했습니다.");
        }
    }
    
    
    @GetMapping("/user/{projectCode}/issue/history/read")
    public String getHistory(@PathVariable String projectCode, Model model) {
    	
    	List<HistoryDTO> historyList = projectService.findHistoryByCode(projectCode);
    	// 오늘 날짜 구하기
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 날짜(yyyy/MM/dd)를 Key로, 해당 날짜의 작업내역 List를 Value로 묶기 - 순서를 보장하기 위해 반드시 LinkedHashMap을 사용
        Map<String, List<HistoryDTO>> groupedHistory = historyList.stream()
            .collect(Collectors.groupingBy(
                dto -> {
                    String dateStr = new SimpleDateFormat("yyyy/MM/dd").format(dto.getHistoryDate());
                    return dateStr.equals(todayStr) ? "오늘" : dateStr; // 오늘이면 "오늘"로 변환
                },
                LinkedHashMap::new, // 내림차순 정렬 순서 유지
                Collectors.toList()
            ));

        model.addAttribute("groupedHistory", groupedHistory);
        return "project/history";
    }
}