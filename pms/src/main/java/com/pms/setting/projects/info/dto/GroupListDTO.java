package com.pms.setting.projects.info.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupListDTO {

    private Long groupNo;       // 그룹 식별 번호
    private String groupName;   // 그룹명
    private String roleName;    // 📍 핵심: 그룹에 부여된 역할 이름 (예: 기획, 개발 등)
    private Integer memberCount; // 그룹 내 소속 인원 수
    private Integer status;      // 그룹 상태 코드 (510: 활성 등)
    private String statusName;  // 상태명 (활성화/비활성화)

}