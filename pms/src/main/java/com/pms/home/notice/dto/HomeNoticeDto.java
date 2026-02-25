package com.pms.home.notice.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Alias("HomeNoticeDto")
public record HomeNoticeDto(
	    Integer noticeNo,
	    String title,
	    String content,
	    Integer privacySettings,
	    String userId,
	    Integer filesNo,
	    LocalDateTime addDate,
	    List<com.pms.home.notice.dto.FileDto> files // 파일 리스트
	) {
	    // MyBatis가 목록 조회(7개 컬럼)를 할 때 사용할 생성자 추가
	    public HomeNoticeDto(Integer noticeNo, String title, String content, 
	                         Integer privacySettings, String userId, 
	                         Integer filesNo, LocalDateTime addDate) {
	        this(noticeNo, title, content, privacySettings, userId, filesNo, addDate, java.util.Collections.emptyList());
	    }
	}