package com.pms.home.notice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pms.home.notice.dto.HomeNoticeDto;
import com.pms.home.notice.mapper.HomeNoticeMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class HomeNoticeRestController {

    private final HomeNoticeMapper noticeMapper;

    // 1. 상세 조회 (모달에 데이터 채우기용)
    @GetMapping("/{noticeNo}")
    public ResponseEntity<HomeNoticeDto> getNotice(@PathVariable Integer noticeNo) {
        HomeNoticeDto notice = noticeMapper.getNotice(noticeNo);
        return notice != null ? ResponseEntity.ok(notice) : ResponseEntity.notFound().build();
    }

    // 2. 공지사항 등록
    @PostMapping
    public ResponseEntity<?> createNotice(@RequestBody HomeNoticeDto dto, 
                                        @AuthenticationPrincipal UserDetails user) {
        // Record는 불변이므로 필요한 정보(userId)를 포함한 새 객체 생성 또는 매퍼에서 처리
        // 여기서는 매퍼 호출 시 user.getUsername()을 넘기거나 
        // DTO를 새로 생성하여 userId를 채웁니다.
        HomeNoticeDto newDto = new HomeNoticeDto(
            null, 
            dto.title(), 
            dto.content(), 
            dto.privacySettings(), 
            user.getUsername(), // 현재 로그인한 사용자 ID
            null
        );
        
        int result = noticeMapper.insertNotice(newDto);
        return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // 3. 공지사항 수정
    @PutMapping("/{noticeNo}")
    public ResponseEntity<?> updateNotice(@PathVariable Integer noticeNo,
                                        @RequestBody HomeNoticeDto dto,
                                        @AuthenticationPrincipal UserDetails user) {
        HomeNoticeDto updateDto = new HomeNoticeDto(
            noticeNo, 
            dto.title(), 
            dto.content(), 
            dto.privacySettings(), 
            user.getUsername(), 
            null
        );
        
        int result = noticeMapper.updateNotice(updateDto);
        return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.status(403).body("수정 권한이 없습니다.");
    }

    // 4. 공지사항 삭제
    @DeleteMapping("/{noticeNo}")
    public ResponseEntity<?> deleteNotice(@PathVariable Integer noticeNo,
                                        @AuthenticationPrincipal UserDetails user) {
        int result = noticeMapper.deleteNotice(noticeNo, user.getUsername());
        return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.status(403).body("삭제 권한이 없습니다.");
    }
}