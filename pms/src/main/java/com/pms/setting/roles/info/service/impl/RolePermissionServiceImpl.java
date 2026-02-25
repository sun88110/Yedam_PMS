package com.pms.setting.roles.info.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.setting.roles.info.dto.RolePermissionDTO;
import com.pms.setting.roles.info.mapper.RolePermissionMapper;
import com.pms.setting.roles.info.service.RolePermissionService;
import com.pms.setting.roles.info.vo.CrudVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<RolePermissionDTO> getRolePermissions(Long roleNo) {
        return rolePermissionMapper.selectRolePermissions(roleNo);
    }

    @Override
    @Transactional
    public void updateRolePermissions(Long roleNo, List<CrudVO> permissions) {
        rolePermissionMapper.deletePermissionsByRoleNo(roleNo);
        if (permissions != null && !permissions.isEmpty()) {
            permissions.forEach(p -> p.setRoleNo(roleNo));
            rolePermissionMapper.insertPermissionsBatch(permissions);
        }
    }

    @Override
    @Transactional
    public int updateRoleName(Long roleNo, String roleName) {
        // 이 부분은 별도의 RoleMapper가 있다면 그쪽을 호출해도 좋지만, 
        // 편의상 RolePermissionMapper에 작성하셔도 됩니다.
        return rolePermissionMapper.updateRoleName(roleNo, roleName);
    }
}