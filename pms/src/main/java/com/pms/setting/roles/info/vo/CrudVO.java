package com.pms.setting.roles.info.vo;

import lombok.Data;

@Data
public class CrudVO {
    private Long roleNo;
    private Long menuId;
    private int crudCreate; // 1: 허용, 0: 차단
    private int crudRead;
    private int crudUpdate;
    private int crudDelete;
}