package com.pms.setting.groups.info.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pms.setting.groups.info.dto.GroupDetailDto;
import com.pms.setting.groups.info.vo.GroupVo;
import com.pms.setting.groups.info.vo.RoleVo;
import com.pms.setting.groups.info.vo.UserVo;

@Mapper
public interface GroupMapper {

    /**
     * 1. 그룹 상세 정보 조회 (기본정보 + 멤버리스트 + 역할리스트)
     * XML에서 <resultMap>과 <collection>을 사용하여 한 번에 채울 예정입니다.
     */
    GroupDetailDto selectGroupDetail(Long groupNo);

    /**
     * 2. 그룹에 새로운 멤버 추가 (MEMBERS 테이블)
     */
    int insertMember(@Param("groupNo") Long groupNo, @Param("userId") String userId);

    /**
     * 3. 그룹에서 멤버 제외 (MEMBERS 테이블)
     */
    int deleteMember(@Param("groupNo") Long groupNo, @Param("userId") String userId);

    /**
     * 4. 그룹에 새로운 역할 부여 (GROUPS_ROLES 테이블)
     */
    void insertGroupRole(@Param("roleNo") Long roleNo, @Param("groupNo") Long groupNo);
    /**
     * 5. 그룹에서 역할 해제 (GROUPS_ROLES 테이블)
     */
    int deleteGroupRole(@Param("groupNo") Long groupNo, @Param("roleNo") Long roleNo);
    
    /**
     * 6. 그룹 기본 정보 수정 (이름, 상태 등)
     */
    int updateGroup(GroupVo groupVo);
    
    /**
     * 7. 그룹에 아직 속하지 않은 사용자들 중에서 검색 (라이브 검색용)
     */
    List<UserVo> selectAvailableUsers(@Param("groupNo") Long groupNo, @Param("keyword") String keyword);
    
 // 📍 전체 역할 조회를 위한 메서드 추가
    List<RoleVo> selectAllRoles();
}