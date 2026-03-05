package com.pms.home.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.config.CustomUserDetails;
import com.pms.home.dto.HomeDto;
import com.pms.home.memo.dto.HomeMemoDto;
import com.pms.home.memo.mapper.HomeMemoMapper;
import com.pms.home.notice.dto.HomeNoticeDto;
import com.pms.home.notice.mapper.HomeNoticeMapper;
import com.pms.home.project.dto.HomeProjectDto;
import com.pms.home.project.mapper.HomeProjectMapper;
import com.pms.project.service.ProjectSecurityService;
import com.pms.user.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeService {
	
	private final ProjectSecurityService projectSecurityService;
    private final HomeProjectMapper projectMapper;
    private final HomeNoticeMapper noticeMapper;
    private final HomeMemoMapper memoMapper;
    
    @Transactional(readOnly = true)
    public HomeDto loadMainPage(CustomUserDetails customUser) {
        // 1. 관리자 여부 판단 로직 (예시: 특정 ID이거나 DB에서 권한을 조회)
        // 실제로는 SecurityContextHolder나 매개변수로 권한 리스트를 받아 처리하는 것이 좋습니다.
    	UserEntity user = customUser.getUserEntity();
    	String userId = user.getUserId();
        boolean isAdmin = user.isAdmin();
        boolean isPm = projectSecurityService.isPm(userId);
        
        // 타입을 명시적으로 선언하여 타입 불일치를 방지
        List<HomeProjectDto> projects = projectMapper.findProjects(userId, isAdmin);
        List<HomeNoticeDto> notices = noticeMapper.findNotices(isAdmin, isPm);
        List<HomeMemoDto> memos = memoMapper.findMemos(userId);
        
        return new HomeDto(projects, notices, memos);
    }
    
}