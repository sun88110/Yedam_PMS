package com.pms.project.common.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProjectCommonStatusMapper {
    // 프로젝트 상태 목록값 조회
    List<ProjectCommonStatusMapper> selectProjectCommonStatusAll(@Param("isAdmin") boolean isAdmin);
}