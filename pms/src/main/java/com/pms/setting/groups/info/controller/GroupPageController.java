package com.pms.setting.groups.info.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/settings") // 공통 경로
public class GroupPageController {

    @GetMapping("/groups-info") // URL: /settings/groups-info
    public String groupDetailPage(@RequestParam("group_no") Long groupNo, Model model) {
        model.addAttribute("groupNo", groupNo);
        return "settings/groups-info"; //
    }
}