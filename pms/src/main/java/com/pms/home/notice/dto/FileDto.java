package com.pms.home.notice.dto;

public record FileDto(
	    Integer detailsNo,   // DETAILS_NO (PK)
	    String filesName,    // FILES_NAME (원본 파일명)
	    String filesSize,    // FILES_SIZE (파일 크기)
	    String filesType,    // FILES_TYPE (확장자/타입)
	    String filesPath,    // FILES_PATH (저장 경로)
	    String filesUuid,    // FILES_UUID (고유 ID)
	    Integer filesNo      // FILES_NO (FK - 그룹 번호)
	) {}