package com.pms.setting.projects.info.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.setting.common.entity.CommonEntity;
import com.pms.setting.common.repository.CommonRepository;
import com.pms.setting.projects.dto.SettingProjectDto;
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

        dto.setGroups(project.getProjectGroups().stream().map(gp -> {
            ProjectResponseDTO.GroupInfoDTO gDto = new ProjectResponseDTO.GroupInfoDTO();
            gDto.setGroupNo(gp.getGroup().getGroupNo());
            gDto.setGroupName(gp.getGroup().getGroupName());
            gDto.setIsPm(gp.getPm());
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
        // 📍 삭제 코드 390 반영
        project.updateBasicInfo(project.getProjectName(), project.getProjectDesc(), 390, project.getPublicYn(), project.getStartDate(), project.getEndDate());
        em.flush(); em.clear();
    }

    @Override @Transactional(readOnly = true)
    public List<SettingProjectDto> getAllProjects() {
        return projectRepository.findAll().stream().map(this::convertToSettingDto).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<SettingProjectDto> searchProjects(Integer status, String keyword) {
        return getAllProjects(); // 실제 검색 Repository 메서드 연결 필요
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
}