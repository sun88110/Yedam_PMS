package com.pms.setting.groups.service;

import java.util.List;
import com.pms.setting.groups.vo.GroupsVO;

public interface GroupsService {

    List<GroupsVO> getGroupAll();

    GroupsVO getGroup(Long groupNo);

    List<GroupsVO> search(String keyword);

    void register(GroupsVO vo);
    
    void toggleGroupStatus(Long groupNo);
    
    boolean modifyGroupDetail(GroupsVO groupsVO);
}