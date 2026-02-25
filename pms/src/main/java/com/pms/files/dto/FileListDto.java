package com.pms.files.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class FileListDto {
	private Integer detailsNo;
	private String filesName;
	private String filesUuid;
	private Long filesSize;
	private String filesType;
	private String filesPath;


}
