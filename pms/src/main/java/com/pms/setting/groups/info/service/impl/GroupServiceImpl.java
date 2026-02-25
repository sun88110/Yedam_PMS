package com.pms.setting.groups.info.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.setting.groups.info.dto.GroupDetailDto;
import com.pms.setting.groups.info.mapper.GroupMapper;
import com.pms.setting.groups.info.service.GroupService;
import com.pms.setting.groups.info.vo.GroupVo;
import com.pms.setting.groups.info.vo.RoleVo;
import com.pms.setting.groups.info.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;

    @Override
    @Transactional(readOnly = true)
    public GroupDetailDto getGroupDetail(Long groupNo) {
        return groupMapper.selectGroupDetail(groupNo);
    }

    @Override
    @Transactional
    public boolean updateGroupInfo(GroupVo groupVo) {
        return groupMapper.updateGroup(groupVo) > 0;
    }

    @Override
    @Transactional
    public boolean addMemberToGroup(Long groupNo, String userId) {
        try {
            // 매퍼 인터페이스의 파라미터 순서 확인 필요 (현재 groupNo, userId 순)
            return groupMapper.insertMember(groupNo, userId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean removeMemberFromGroup(Long groupNo, String userId) {
        return groupMapper.deleteMember(groupNo, userId) > 0;
    }

//    @Override
//    @Transactional
//    public boolean grantRoleToGroup(Long groupNo, Long roleNo) {
//        try {
//            return groupMapper.insertGroupRole(roleNo, groupNo) > 0;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    @Override
    @Transactional
    public boolean revokeRoleFromGroup(Long groupNo, Long roleNo) {
        // 단일 역할 삭제 시 roleNo를 파라미터로 전달
        try {
            groupMapper.deleteGroupRole(groupNo, roleNo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVo> searchAvailableUsers(Long groupNo, String keyword) {
        return groupMapper.selectAvailableUsers(groupNo, keyword);
    }

    // 📍 1. 모든 역할 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<RoleVo> getAllRoles() {
        return groupMapper.selectAllRoles(); 
    }

    // 📍 2. 그룹 역할 업데이트 (기존 권한 삭제 후 새 권한 부여)
    @Override
    @Transactional
    public void updateGroupRole(Long groupNo, Long roleNo) {
        // 1. 기존 그룹에 부여된 모든 역할을 삭제 (roleNo를 null로 전달)
        groupMapper.deleteGroupRole(groupNo, null); 
        
        // 2. 새로운 역할 부여
        if (roleNo != null) {
            groupMapper.insertGroupRole(roleNo, groupNo);
        }
    }
}