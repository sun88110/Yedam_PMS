package com.pms.setting.groups.info.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVo {
    private String userId;           // USER_ID (VARCHAR2)
    private String passwd;           // PASSWD (VARCHAR2)
    private String userName;         // USERNAME (VARCHAR2)
    private Integer admin;           // ADMIN (NUMBER)
    private Integer status;          // STATUS (NUMBER)
    private LocalDateTime lastlogin;  // LASTLOGIN (TIMESTAMP)
    private LocalDateTime createtime; // CREATETIME (TIMESTAMP)
    private String email;            // EMAIL (VARCHAR2)
}