package com.pms.setting.projects.info.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pms.setting.projects.info.dto.ProjectResponseDTO.GroupAddRequest;
import com.pms.setting.projects.info.dto.ProjectResponseDTO.ProjectUpdateRequest;
import com.pms.setting.projects.info.service.ProjectInfoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/settings/api/projects")
@RequiredArgsConstructor
public class ProjectInfoApiController {

    private final ProjectInfoService projectInfoService;

    /**
     * 📍 1. 프로젝트 기본 정보 수정 (PATCH)
     * URL: /settings/api/projects/{projectNo}
     */
    @PatchMapping("/{projectNo}")
    public ResponseEntity<String> updateProject(@PathVariable Long projectNo, 
                                               @RequestBody ProjectUpdateRequest request) {
        projectInfoService.updateProjectBasicInfo(projectNo, request);
        return ResponseEntity.ok("SUCCESS");
    }

    /**
     * 📍 2. 프로젝트에 그룹 추가 (POST)
     * URL: /settings/api/projects/{projectNo}/groups
     */
    @PostMapping("/{projectNo}/groups")
    public ResponseEntity<String> addGroupToProject(@PathVariable Long projectNo, 
                                                   @RequestBody GroupAddRequest request) {
        projectInfoService.addGroupToProject(projectNo, request);
        return ResponseEntity.ok("SUCCESS");
    }

    /**
     * 📍 3. 프로젝트에서 그룹 제외 (DELETE)
     * URL: /settings/api/projects/{projectNo}/groups/{groupNo}
     */
    @DeleteMapping("/{projectNo}/groups/{groupNo}")
    public ResponseEntity<String> removeGroupFromProject(@PathVariable Long projectNo, 
                                                        @PathVariable Long groupNo) {
        projectInfoService.removeGroupFromProject(projectNo, groupNo);
        return ResponseEntity.ok("SUCCESS");
    }
}