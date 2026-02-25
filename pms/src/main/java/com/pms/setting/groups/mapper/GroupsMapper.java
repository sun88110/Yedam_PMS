package com.pms.setting.groups.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 📍 주의: 스프링용 말고 마이바티스용 Param을 써야 안전합니다.
import com.pms.setting.groups.vo.GroupsVO;

@Mapper
public interface GroupsMapper {

    List<GroupsVO> selectGroupAll();

    List<GroupsVO> searchGroup(@Param("keyword") String keyword);

    void insertGroup(GroupsVO vo);

    // 📍 2. 상태 변경 (둘 중 JS에서 사용하는 하나만 있어도 무방합니다)
    void toggleGroupStatus(@Param("groupNo") Long groupNo);

    // 상세 보기가 필요 없다면 이건 주석 처리해도 됩니다.
    GroupsVO selectGroup(Long groupNo);

	int updateGroupDetail(GroupsVO groupsVO);
}