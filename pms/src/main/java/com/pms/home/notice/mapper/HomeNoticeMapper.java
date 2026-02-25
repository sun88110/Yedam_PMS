package com.pms.home.notice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.pms.home.notice.dto.FileDto;
import com.pms.home.notice.dto.HomeNoticeDto;

@Mapper
public interface HomeNoticeMapper {
    List<HomeNoticeDto> findNotices();
    
    // 이름을 selectNoticeDetail에서 getNotice로 변경
    HomeNoticeDto getNotice(Integer noticeNo);

    int insertNotice(HomeNoticeDto noticeDto);

    int updateNotice(HomeNoticeDto noticeDto);

    int deleteNotice(@Param("noticeNo") Integer noticeNo, @Param("userId") String userId);
    
    /**
     * 특정 공지사항에 귀속된 파일 상세 목록 조회
     * (XML의 <collection>에서 사용하거나 별도로 호출 가능)
     */
    List<FileDto> selectFilesDetailsByNo(Integer filesNo);

    /**
     * 첨부파일 개별 삭제 (FILES_DETAILS 테이블의 PK인 detailsNo 기준)
     */
    int deleteFileDetail(Integer detailsNo);

    /**
     * 물리적 파일 삭제를 위해 파일의 UUID와 경로 정보를 가져오는 메서드
     */
    FileDto getFileDetailByNo(Integer detailsNo);
}
