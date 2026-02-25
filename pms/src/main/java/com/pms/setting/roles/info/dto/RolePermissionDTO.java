package com.pms.setting.roles.info.dto;

import lombok.Data;

@Data
public class RolePermissionDTO {
	
	private Long roleNo;
    private String roleName;
    
    // Menu 정보
    private Long menuId;
    private String menuName;
    private String urlData;

    // 해당 역할(Role)의 CRUD 권한 (Left Join 결과값)
    private int crudCreate;
    private int crudRead;
    private int crudUpdate;
    private int crudDelete;
}