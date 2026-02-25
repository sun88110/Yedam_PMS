package com.pms.home.memo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.pms.home.memo.dto.HomeMemoDto;

@Mapper
public interface HomeMemoMapper {
	List<HomeMemoDto> findMemos(String userId);

	void deleteMemo(Integer memoNo, String userId);

	void insertMemo(HomeMemoDto newMemo);
}
