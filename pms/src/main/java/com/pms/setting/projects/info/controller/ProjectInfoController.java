package com.pms.setting.projects.info.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pms.setting.projects.info.dto.ProjectResponseDTO;
import com.pms.setting.projects.info.service.ProjectInfoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class ProjectInfoController {

    private final ProjectInfoService projectInfoService;

    /**
     * 1. 프로젝트 상세 관리 페이지 이동 (View)
     * URL: /settings/projects-info?projectNo=...
     */
    @GetMapping("/projects-info")
    public String projectInfoPage(@RequestParam("projectNo") Long projectNo, Model model) {
        ProjectResponseDTO project = projectInfoService.getProjectDetail(projectNo);
        model.addAttribute("project", project);
        return "settings/projects-info";
    }
}