package com.pms.setting.groups.info.vo;

import lombok.Data;

@Data
public class GroupMemberVo {
    private String userId;
    private String userName;   // XML의 USERNAME과 매칭
    private String email;
    private String accessDate; // TO_CHAR로 변환된 문자열 매칭
}