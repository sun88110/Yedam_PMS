package com.pms.setting.projects.info.service;

import java.util.List;

import com.pms.setting.common.entity.CommonEntity;
import com.pms.setting.projects.dto.SettingProjectDto;
import com.pms.setting.projects.info.dto.GroupListDTO;
import com.pms.setting.projects.info.dto.ProjectResponseDTO;

public interface ProjectInfoService {
    List<SettingProjectDto> getAllProjects();
    List<SettingProjectDto> searchProjects(Integer status, String keyword);
    List<CommonEntity> getStatusList();
    void toggleLock(Long projectNo, Integer targetStatus);
    void logicalDelete(Long projectNo); // 삭제 코드 390 처리용

    ProjectResponseDTO getProjectDetail(Long projectNo);
    void updateProjectBasicInfo(Long projectNo, ProjectResponseDTO.ProjectUpdateRequest request);
    void addGroupToProject(Long projectNo, ProjectResponseDTO.GroupAddRequest request);
    void removeGroupFromProject(Long projectNo, Long groupNo);
    List<GroupListDTO> getAvailableGroupsForModal();
}