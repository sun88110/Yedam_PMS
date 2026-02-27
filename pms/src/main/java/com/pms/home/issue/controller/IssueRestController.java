package com.pms.home.issue.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.pms.config.CustomUserDetails;
import com.pms.issue.service.IssueService;
import com.pms.issue.web.IssueSelectDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class IssueRestController {

    private final IssueService issueService;

    @GetMapping("/{projectCode}/issues")
    public List<IssueSelectDto> getDashboardIssues(
            @PathVariable String projectCode,
            @RequestParam(value = "showOnlyMe", defaultValue = "N") String showOnlyMe,
            @AuthenticationPrincipal CustomUserDetails customUser) {

        IssueSelectDto selectDto = new IssueSelectDto();
        String userId = customUser.getUserEntity().getUserId();
        
        // 🚨 권한 체크: 어드민 여부 확인
        boolean isAdmin = customUser.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 1. [프로젝트 필터링]
        if ("all".equals(projectCode)) {
            if (isAdmin) {
                // 어드민은 전체 프로젝트 조회
                selectDto.setProjectCode(null);
            } else {
                // 일반 유저는 전체를 눌러도 본인이 속한 프로젝트만 조회하도록 
                // Mapper 쿼리에서 userId를 통해 JOIN해야 함
                selectDto.setProjectCode(null); 
            }
        } else {
            // 특정 프로젝트 선택 시
            selectDto.setProjectCode(projectCode);
        }

        // 2. [담당자 필터링]
        if ("Y".equals(showOnlyMe)) {
            // '내 것만' 체크 시 무조건 로그인 유저 ID 세팅
            selectDto.setUserId(userId);
        } else {
            // '내 것만' 체크 해제 시
            if (!isAdmin) {
                // 일반 유저는 '내 것만' 해제해도 본인 프로젝트의 모든 일감만 봐야 함
                // 이것도 쿼리에서 프로젝트 제한이 필요
                selectDto.setUserId(null); 
            } else {
                // 어드민은 '내 것만' 해제 시 모든 유저의 일감 조회
                selectDto.setUserId(null);
            }
        }
        
        // 💡 중요: 쿼리에서 일반 유저의 프로젝트 제한이 안 된다면 
        // 컨트롤러에서 userId를 세팅해서 쿼리해야 할 수도 있습니다.
        
        return issueService.selectDashboardIssueList(selectDto);
    }
}