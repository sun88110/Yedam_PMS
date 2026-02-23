package com.pms.setting.groups.info.dto;

import java.util.List;
import com.pms.setting.groups.info.vo.RoleVo;
import com.pms.setting.groups.info.vo.GroupMemberVo; // 추가
import lombok.Data;

@Data
public class GroupDetailDto {
    private Long groupNo;
    private String groupName;
    private String roleName;
    private Integer status;

    // 멤버 전용 VO 리스트로 변경
    private List<GroupMemberVo> members;
    
    // 역할 리스트
    private List<RoleVo> roles;
}