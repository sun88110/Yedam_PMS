package com.pms.home.memo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.home.memo.dto.HomeMemoDto;
import com.pms.home.memo.mapper.HomeMemoMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeMemoService {

    private final HomeMemoMapper memoMapper;

    @Transactional
    public void saveMemo(HomeMemoDto dto, String userId) {
        // Record의 특성상 새로운 객체를 생성하여 전달
        HomeMemoDto newMemo = new HomeMemoDto(null, dto.content(), null, userId);
        memoMapper.insertMemo(newMemo);
    }

    @Transactional
    public void deleteMemo(Integer memoNo, String userId) {
        // 본인 확인을 위해 userId와 함께 전달
        memoMapper.deleteMemo(memoNo, userId);
    }
}