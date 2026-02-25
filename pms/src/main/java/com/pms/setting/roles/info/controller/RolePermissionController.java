package com.pms.setting.roles.info.controller;

import java.util.List;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pms.setting.roles.info.dto.RolePermissionDTO;
import com.pms.setting.roles.info.service.RolePermissionService;
import com.pms.setting.roles.info.vo.CrudVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/settings/api/roles")
@RequiredArgsConstructor
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @GetMapping("/{roleNo}/permissions")
    public ResponseEntity<List<RolePermissionDTO>> getPermissions(@PathVariable Long roleNo) {
        return ResponseEntity.ok(rolePermissionService.getRolePermissions(roleNo));
    }

    @PostMapping("/{roleNo}/permissions")
    public ResponseEntity<String> savePermissions(@PathVariable Long roleNo, @RequestBody List<CrudVO> permissions) {
        rolePermissionService.updateRolePermissions(roleNo, permissions);
        return ResponseEntity.ok("success");
    }

    @PatchMapping("/{roleNo}")
    public ResponseEntity<String> updateRoleName(
            @PathVariable Long roleNo, 
            @RequestBody Map<String, String> payload) { // 📍 VO 대신 Map으로 받기
        
        // JS에서 { "roleName": "새이름" } 이라고 보냈으므로 키값으로 꺼냅니다.
        String newName = payload.get("roleName"); 
        
        if (newName == null || newName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("이름이 비어있습니다.");
        }

        rolePermissionService.updateRoleName(roleNo, newName);
        return ResponseEntity.ok("success");
    }
}