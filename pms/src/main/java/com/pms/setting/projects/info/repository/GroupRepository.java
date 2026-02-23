package com.pms.setting.projects.info.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pms.setting.projects.info.entity.Group;

//GroupRepository.java (그룹 목록을 불러올 때 사용)
public interface GroupRepository extends JpaRepository<Group, Long> {

    // 📍 Native Query를 사용하면 복잡한 JOIN과 DTO 변환을 한 방에 해결할 수 있습니다.
    @Query(value = "SELECT g.GROUP_NO as groupNo, g.GROUPNAME as groupName, " +
                   "r.ROLE_NAME as roleName, " +
                   "(SELECT COUNT(*) FROM MEMBERS m WHERE m.GROUP_NO = g.GROUP_NO) as memberCount " +
                   "FROM GROUPS g " +
                   "LEFT JOIN GROUP_ROLES gr ON g.GROUP_NO = gr.GROUP_NO " +
                   "LEFT JOIN ROLES r ON gr.ROLE_NO = r.ROLE_NO " +
                   "WHERE g.STATUS = 510", nativeQuery = true)
    
    List<Map<String, Object>> findAllGroupsWithRole();
}