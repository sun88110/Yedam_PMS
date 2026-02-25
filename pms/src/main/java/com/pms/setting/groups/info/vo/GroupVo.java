package com.pms.setting.groups.info.vo;

import lombok.Data;

@Data
public class GroupVo {
    private Long groupNo;      // GROUP_NO (NUMBER)
    private String groupName;  // GROUPNAME (VARCHAR2)
    private Integer status;    // STATUS (NUMBER)
}