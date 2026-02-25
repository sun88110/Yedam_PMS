package com.pms.setting.groups.info.service;

import java.util.List;

import com.pms.setting.groups.info.dto.GroupDetailDto;
import com.pms.setting.groups.info.vo.GroupVo;
import com.pms.setting.groups.info.vo.RoleVo;
import com.pms.setting.groups.info.vo.UserVo;

public interface GroupService {

    /**
     * 1. 그룹 상세 정보 가져오기
     * (그룹 기본정보 + 소속 멤버 + 부여된 권한)
     */
    GroupDetailDto getGroupDetail(Long groupNo);

    /**
     * 2. 그룹 기본 정보 수정
     * (그룹명, 상태값 등 변경)
     */
    boolean updateGroupInfo(GroupVo groupVo);

    /**
     * 3. 그룹에 멤버 추가
     * @return 성공 여부 (이미 존재하는 멤버일 경우 처리 로직 포함)
     */
    boolean addMemberToGroup(Long groupNo, String userId);

    /**
     * 4. 그룹에서 멤버 제외
     */
    boolean removeMemberFromGroup(Long groupNo, String userId);

    /**
     * 5. 그룹 권한(역할) 부여
     */
//    boolean grantRoleToGroup(Long groupNo, Long roleNo);

    /**
     * 6. 그룹 권한(역할) 회수
     */
    boolean revokeRoleFromGroup(Long groupNo, Long roleNo);

    /**
     * 7. 그룹에 추가 가능한 유저 검색 (라이브 검색용)
     * (이미 그룹에 속한 유저는 제외하고 검색하는 로직 필요)
     */
    List<UserVo> searchAvailableUsers(Long groupNo, String keyword);
    
    // 📍 신규 추가
    List<RoleVo> getAllRoles(); // 전체 역할 조회
    void updateGroupRole(Long groupNo, Long roleNo); // 역할 수정 (Delete & Insert)
}