package com.pms.project.mapper;

import java.util.Set;

import org.apache.ibatis.annotations.Mapper;

import com.pms.project.dto.HolidayDTO;

@Mapper
public interface HolidayMapper {
    // 불필요한 로그 너무많이찍혀서 여기로 옮김
	Set<HolidayDTO> selectHolidays(); // 일감, 간트차트에서 활용할 주말테이블 조회
}