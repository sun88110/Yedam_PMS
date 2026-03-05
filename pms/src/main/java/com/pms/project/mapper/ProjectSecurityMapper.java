package com.pms.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pms.project.dto.ProjectSecurityMenuDto;

@Mapper
public interface ProjectSecurityMapper {
	boolean checkAuth(
			@Param("userId") String userId,
			@Param("value") String value,
			@Param("menuId") Integer menuId,
			@Param("method") String method
			);
	
	boolean checkPm(
			@Param("userId") String userId, 
			@Param("projectCode") String projectCode
			);
	
	boolean checkImPm(@Param("userId") String userId);
	
	List<ProjectSecurityMenuDto> selectAllMenus();
	
	String findProjectNoByCode(@Param("projectCode") String projectCode);
}
