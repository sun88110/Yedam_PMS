package com.pms.setting.roles.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.setting.roles.info.mapper.RolePermissionMapper;
import com.pms.setting.roles.mapper.RolesMapper;
import com.pms.setting.roles.service.RolesService;
import com.pms.setting.roles.vo.RolesVO;

import lombok.RequiredArgsConstructor;

	@Service
	@RequiredArgsConstructor
	public class RolesServiceImpl implements RolesService {

	    private final RolesMapper rolesMapper;
	    private final RolePermissionMapper permissionMapper;
	    
	    @Override
	    public List<RolesVO> getAllRoles() {
	        return rolesMapper.selectAllRoles();
	    }

	    @Override
	    public List<RolesVO> searchRoles(String keyword) {
	        // 키워드가 비어있으면 전체 조회로 유도 (UX 최적화)
	        if (keyword == null || keyword.isBlank()) {
	            return rolesMapper.selectAllRoles();
	        }
	        return rolesMapper.searchRoles(keyword);
	    }
	    
	    @Override
	    public void register(RolesVO vo) {
	        rolesMapper.insertRole(vo);
	    }
	    
	    @Transactional // 📍 중요: 두 작업 중 하나라도 실패하면 원복합니다.
	    @Override
	    public void removeRole(Long roleNo) {
	        // 1. 자식 레코드(CRUD 권한)부터 삭제
	        permissionMapper.deletePermissionsByRoleNo(roleNo);
	        
	        // 2. 부모 레코드(ROLE) 삭제
	        rolesMapper.deleteRole(roleNo);
	    }
	}