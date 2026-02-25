package com.pms.setting.roles.info.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pms.setting.roles.info.dto.RolePermissionDTO;
import com.pms.setting.roles.info.vo.CrudVO;

@Mapper
public interface RolePermissionMapper {

    // 1. 특정 역할의 메뉴별 CRUD 권한 전체 조회
    List<RolePermissionDTO> selectRolePermissions(@Param("roleNo") Long roleNo);

    // 2. 특정 역할의 기존 권한 전체 삭제
    int deletePermissionsByRoleNo(@Param("roleNo") Long roleNo);

    // 3. 새로운 권한 리스트 일괄 등록
    int insertPermissionsBatch(@Param("permissions") List<CrudVO> permissions);
    
    int updateRoleName(@Param("roleNo") Long roleNo, @Param("roleName") String roleName);
}