package com.pms.setting.projects.info.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pms.setting.projects.info.entity.GProject;

//GProjectRepository.java (매핑 관리)
public interface GProjectRepository extends JpaRepository<GProject, Long> {
 // 특정 프로젝트에 속한 매핑 정보만 찾기
 List<GProject> findByProject_ProjectNo(Long projectNo);
}