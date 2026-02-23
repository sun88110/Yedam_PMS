package com.pms.project.dto;

import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// News 제거해서 이제 필요없음
@Getter
@Setter
@ToString
@NoArgsConstructor
@Alias("NoticeDTO")
public class NoticeDTO {
	private Integer noticeNo;
	private String title;
	private String content;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date addDate;
	private Integer privacySettings;
	
	// nocite.userId 를 노출할 수 없으니 userName 조회 결과출력
	private String username;
}
