package com.pms.setting.roles.info. service;

import java.util.List;

import com.pms.setting.roles.info.dto.RolePermissionDTO;
import com.pms.setting.roles.info.vo.CrudVO;

public interface RolePermissionService {
    // 특정 역할의 권한 목록 조회
    List<RolePermissionDTO> getRolePermissions(Long roleNo);

    // 특정 역할의 권한 일괄 수정 (삭제 후 등록)
    void updateRolePermissions(Long roleNo, List<CrudVO> permissions);
    
    // 📍 역할 이름 수정을 위한 메소드 추가
    int updateRoleName(Long roleNo, String roleName);
}