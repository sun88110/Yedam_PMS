package com.pms.home.notice.dto;

import java.time.LocalDateTime;
import org.apache.ibatis.type.Alias;

@Alias("HomeNoticeDto")
public record HomeNoticeDto(
    Integer noticeNo,        // NOTICE_NO (기본키)
    String title,           // TITLE (제목)
    String content,         // CONTENT (내용 - 모달용)
    Integer privacySettings, // PRIVACY_SETTINGS (공개여부 - 모달용)
    String userId,          // USER_ID (작성자 확인용)
    LocalDateTime addDate   // ADD_DATE (등록일)
) {}