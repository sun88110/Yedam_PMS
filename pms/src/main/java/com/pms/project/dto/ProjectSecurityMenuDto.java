package com.pms.project.dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("ProjectSecurityMenuDto")
public class ProjectSecurityMenuDto {
	private Integer menuId;
	private String menuName;
	private String urlData;
	private String type;
}
