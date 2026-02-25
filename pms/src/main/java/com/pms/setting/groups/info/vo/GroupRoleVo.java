package com.pms.setting.groups.info.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupRoleVo {
    private Long roleNo;       // ROLE_NO (NUMBER, PK/FK)
    private Long groupNo;      // GROUP_NO (NUMBER, PK/FK)
    private LocalDateTime createAt; // CREATE_AT (TIMESTAMP) - 부여일
    private LocalDateTime updateAt; // UPDATE_AT (TIMESTAMP) - 수정일
}