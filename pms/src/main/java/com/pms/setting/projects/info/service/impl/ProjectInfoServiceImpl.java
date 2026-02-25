package com.pms.setting.projects.info.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.setting.common.entity.CommonEntity;
import com.pms.setting.common.repository.CommonRepository;
import com.pms.setting.projects.dto.SettingProjectDto;
import com.pms.setting.projects.info.dto.GroupListDTO;
import com.pms.setting.projects.info.dto.ProjectResponseDTO;
import com.pms.setting.projects.info.entity.GProject;
import com.pms.setting.projects.info.entity.Group;
import com.pms.setting.projects.info.entity.Project;
import com.pms.setting.projects.info.repository.GroupRepository;
import com.pms.setting.projects.info.repository.ProjectRepository;
import com.pms.setting.projects.info.service.ProjectInfoService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional	
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @PersistenceContext
    private EntityManager em;

    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository;
    private final CommonRepository commonRepository;

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectDetail(Long projectNo) {
        Project project = projectRepository.findById(projectNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setProjectNo(project.getProjectNo());
        dto.setProjectName(project.getProjectName());
        dto.setProjectDesc(project.getProjectDesc());
        dto.setProjectCode(project.getProjectCode());
        dto.setStatus(project.getStatus());
        dto.setPublicYn(project.getPublicYn()); 
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());

        // 📍 모달용 데이터를 미리 가져와서 매핑에 활용 (엔티티 연관관계 에러 회피)
        List<GroupListDTO> allGroups = getAvailableGroupsForModal();

        dto.setGroups(project.getProjectGroups().stream().map(gp -> {
            ProjectResponseDTO.GroupInfoDTO gDto = new ProjectResponseDTO.GroupInfoDTO();
            gDto.setGroupNo(gp.getGroup().getGroupNo());
            gDto.setGroupName(gp.getGroup().getGroupName());
            gDto.setIsPm(gp.getPm());
            
            // 모달 데이터에서 일치하는 정보를 찾아 역할명과 인원수 주입
            allGroups.stream()
                .filter(ag -> ag.getGroupNo().equals(gp.getGroup().getGroupNo()))
                .findFirst()
                .ifPresent(matched -> {
                    gDto.setRoleName(matched.getRoleName());
                    gDto.setMemberCount(matched.getMemberCount());
                });

            if(gDto.getRoleName() == null) gDto.setRoleName("일반");
            if(gDto.getMemberCount() == null) gDto.setMemberCount(0);

            return gDto;
        }).toList());

        return dto;
    }

    @Override
    public void updateProjectBasicInfo(Long projectNo, ProjectResponseDTO.ProjectUpdateRequest request) {
        Project project = projectRepository.findById(projectNo)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        project.updateBasicInfo(
                request.getProjectName(), 
                request.getProjectDesc(), 
                request.getStatus(),
                request.getPublicYn(),
                request.getStartDate(), 
                request.getEndDate()
        );
    }

    @Override
    public void addGroupToProject(Long projectNo, ProjectResponseDTO.GroupAddRequest request) {
        Project project = projectRepository.findById(projectNo).get();
        Group group = groupRepository.findById(request.getGroupNo()).get();

        boolean exists = project.getProjectGroups().stream()
                .anyMatch(gp -> gp.getGroup().getGroupNo().equals(request.getGroupNo()));
        
        if (!exists) {
            GProject mapping = new GProject(project, group, request.getIsPm());
            project.getProjectGroups().add(mapping);
        }
    }

    @Override
    public void removeGroupFromProject(Long projectNo, Long groupNo) {
        Project project = projectRepository.findById(projectNo).get();
        project.getProjectGroups().removeIf(gp -> gp.getGroup().getGroupNo().equals(groupNo));
    }

    @Override
    public void toggleLock(Long projectNo, Integer targetStatus) {
        Project project = projectRepository.findById(projectNo).get();
        project.updateBasicInfo(project.getProjectName(), project.getProjectDesc(), targetStatus, project.getPublicYn(), project.getStartDate(), project.getEndDate());
        em.flush(); em.clear();
    }

    @Override
    public void logicalDelete(Long projectNo) {
        Project project = projectRepository.findById(projectNo).get();
        project.updateBasicInfo(project.getProjectName(), project.getProjectDesc(), 390, project.getPublicYn(), project.getStartDate(), project.getEndDate());
        em.flush(); em.clear();
    }

    @Override @Transactional(readOnly = true)
    public List<SettingProjectDto> getAllProjects() {
        return projectRepository.findAll().stream().map(this::convertToSettingDto).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<SettingProjectDto> searchProjects(Integer status, String keyword) {
        return getAllProjects();
    }

    @Override @Transactional(readOnly = true)
    public List<CommonEntity> getStatusList() {
        return commonRepository.findByParent_CommonNoAndDisplayYn(300L, "Y");
    }

    private SettingProjectDto convertToSettingDto(Project p) {
        SettingProjectDto dto = new SettingProjectDto();
        dto.setProjectNo(p.getProjectNo());
        dto.setProjectName(p.getProjectName());
        dto.setPublicYn(p.getPublicYn());
        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GroupListDTO> getAvailableGroupsForModal() {
        List<Map<String, Object>> results = groupRepository.findAllGroupsWithRole();

        return results.stream().map(row -> {
            Map<String, Object> upperRow = new java.util.HashMap<>();
            row.forEach((k, v) -> upperRow.put(k.toUpperCase(), v));

            Object gNo = upperRow.get("GROUPNO");
            if(gNo == null) gNo = upperRow.get("GROUP_NO");
            Object gName = upperRow.get("GROUPNAME");
            Object rName = upperRow.get("ROLE_NAME");
            if(rName == null) rName = upperRow.get("ROLENAME");
            Object mCount = upperRow.get("MEMBERCOUNT");

            return GroupListDTO.builder()
                    .groupNo(gNo != null ? ((Number) gNo).longValue() : 0L)
                    .groupName(gName != null ? String.valueOf(gName) : "이름 없음")
                    .roleName(rName != null ? String.valueOf(rName) : "일반")
                    .memberCount(mCount != null ? ((Number) mCount).intValue() : 0)
                    .build();
        }).toList();
    }
}