package com.pms.work.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.work.dto.WorkInsertDto;
import com.pms.work.dto.WorkReportDto;
import com.pms.work.dto.WorkSelectDto;
import com.pms.work.dto.WorkUpdateDto;
import com.pms.work.mapper.WorkMapper;
import com.pms.work.service.WorkService;

import lombok.RequiredArgsConstructor;

@Service
// field에 final 있으면 생성자 주입
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {
	// mapper와 연결
	private final WorkMapper workMapper;

	// 소요시간 전체 조회 + 검색조건 추가 후 조회
	@Override
	public List<WorkSelectDto> findAllWorkEntries(WorkSelectDto workSelectDto) {
		// 소속된 프로젝트의 전체 일감, 소요시간 조회 가능
		// 내 것만 보기 기능은 controller에서 수행
		return workMapper.selectWorkEntries(workSelectDto);
	};

	// 소요시간 등록 페이지 프로젝트의 일감 조회
	@Override
	public List<WorkInsertDto> findMyIssue(WorkInsertDto workInsertDto) {
		return workMapper.selectIssueInProject(workInsertDto);
	}
	@Override
	public List<WorkInsertDto> findWorkType(String workEntriesNo) {
		return workMapper.selectWorkDetails(workEntriesNo);
	};

	// 소요시간 등록 기능
	@Override
	@Transactional
	public void addWorkEntries(WorkInsertDto workInsertDto) {
		// 등록된 일감에 대한 소요시간만 작성할 수 있다
		if (workInsertDto.getJobNo() == null) {
			/*
			 * IllegalArgumentException 적절하지 않은 인자(Argument) 전달 예외 메서드가 호출될 때, 전달된
			 * 매개변수(Parameter)의 값이 허용되는 범위나 조건에 맞지 않을 때 발생
			 */
			throw new IllegalArgumentException("일감 정보가 없습니다");
		}
		// 작업시간이 null이 아니면서 값을 0으로 입력했을 때
		if (workInsertDto.getWorkTime() != null && workInsertDto.getWorkTime() < 0) {
			throw new IllegalArgumentException("작업 시간은 0 이상이어야 합니다");
		}

		workMapper.insertWorkEntries(workInsertDto);
	}

	// 소요시간 수정 페이지
	@Override
	public WorkUpdateDto findWorkEntriesByNo(WorkUpdateDto workUpdateDto) {
		// DTO에서 workEntriesNo를 가져온다
		Integer workNo = workUpdateDto.getWorkEntriesNo();
		// 실제 등록된 소요시간인지 확인
		if (workNo == null) {
			throw new IllegalArgumentException("소요시간 번호가 없습니다");
		}
		// Mapper호출 상세조회할 데이터를 details에 담는다
		WorkUpdateDto details = workMapper.selectWorkEntriesByNo(workUpdateDto);
		// 실제 등록된 소요시간인지 확인
		if (details == null) {
			throw new IllegalArgumentException("해당 소요시간 번호 " + workNo + "는 존재하지 않는 기록입니다");
		}
		// 조회된 상세 화면 반환
		return details;
	}

	// 소요시간 수정 기능
	@Override
	@Transactional
	public void modifyWorkEntries(WorkUpdateDto workUpdateDto) {
		// 실제 등록된 소요시간의 정보를 수정하는지 확인
		if (workUpdateDto.getWorkEntriesNo() == null) {
			throw new IllegalArgumentException("수정하려는 소요시간 대상이 없습니다");
		}
		// 작업 시간에 값을 입력했는데, 0이하의 값을 입력했을 경우
		if (workUpdateDto.getWorkTime() != null && workUpdateDto.getWorkTime() < 0) {
			throw new IllegalArgumentException("작업시간은 0 이상이어야 입니다");
		}

		workMapper.updateWorkEntries(workUpdateDto);
	}

	// 통합 소요시간 보고서 기능
	@Override
	public List<WorkReportDto> findWorkReport(String type, WorkReportDto workReportDto) {
		if (type == null) {
			return null;
		}
		// input hidden에서 전달받은 type에 따라 각기 다른 mapper의 Method호출
		switch (type.toLowerCase()) {
		case "job":
			return workMapper.selectJobReport(workReportDto);
		case "users":
			return workMapper.selectUserReport(workReportDto);
		case "work":
			return workMapper.selectWorkTypeReport(workReportDto);
		case "week":
			return workMapper.selectWeekReport(workReportDto);
		case "month":
			return workMapper.selectMonthReport(workReportDto);
		default:
			// 잘못된 type이 들어왔을 때
			return null;
		}
	};

}
