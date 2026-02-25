package com.pms.setting.roles.info.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 📍 RestController가 아닌 일반 Controller입니다!
public class RolesViewController {

    // 브라우저에서 http://localhost:8080/settings/roles-info 접속 시 호출
    @GetMapping("/settings/roles-info")
    public String rolesInfoPage() {
        // 📍 templates/settings/roles-info.html 파일을 찾아가라는 뜻입니다.
        return "settings/roles-info"; 
    }
}