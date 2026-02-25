package com.pms.home.memo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.pms.config.CustomUserDetails;
import com.pms.home.memo.dto.HomeMemoDto;
import com.pms.home.memo.service.HomeMemoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memos")
public class HomeMemoRestController {

    private final HomeMemoService memoService;

    // 메모 저장
    @PostMapping
    public ResponseEntity<String> addMemo(@RequestBody HomeMemoDto dto, 
                                        @AuthenticationPrincipal CustomUserDetails customUser) {
        String userId = customUser.getUsername();
        memoService.saveMemo(dto, userId);
        return ResponseEntity.ok("success");
    }

    // 메모 삭제
    @DeleteMapping("/{memoNo}")
    public ResponseEntity<String> removeMemo(@PathVariable Integer memoNo, 
                                           @AuthenticationPrincipal CustomUserDetails customUser) {
        String userId = customUser.getUsername();
        memoService.deleteMemo(memoNo, userId);
        return ResponseEntity.ok("success");
    }
}