package com.pms.setting.projects.info.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pms.setting.projects.info.entity.Project;

//ProjectRepository.java
public interface ProjectRepository extends JpaRepository<Project, Long> {
 // 기본 CRUD(findById 등)는 이미 내장되어 있습니다!
}