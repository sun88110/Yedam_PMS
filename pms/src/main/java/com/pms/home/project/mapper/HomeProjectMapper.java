package com.pms.home.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pms.home.project.dto.HomeProjectDto;
import com.pms.project.dto.ProjectSelectDTO;

@Mapper
public interface HomeProjectMapper {
	List<HomeProjectDto> findProjects(@Param("userId") String userId, @Param("isAdmin") boolean isAdmin);
}
