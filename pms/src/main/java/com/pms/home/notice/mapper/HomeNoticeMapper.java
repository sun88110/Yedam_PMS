package com.pms.home.notice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.pms.home.notice.dto.HomeNoticeDto;

@Mapper
public interface HomeNoticeMapper {
    List<HomeNoticeDto> findNotices();
    
    // 이름을 selectNoticeDetail에서 getNotice로 변경
    HomeNoticeDto getNotice(Integer noticeNo);

    int insertNotice(HomeNoticeDto noticeDto);

    int updateNotice(HomeNoticeDto noticeDto);

    int deleteNotice(@Param("noticeNo") Integer noticeNo, @Param("userId") String userId);
}
