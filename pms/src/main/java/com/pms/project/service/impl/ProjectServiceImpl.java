package com.pms.project.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.project.dto.GanttDTO;
import com.pms.project.dto.IssueTrackerDTO;
import com.pms.project.dto.JobDTO;
import com.pms.project.dto.MemberDTO;
import com.pms.project.dto.NoticeDTO;
import com.pms.project.dto.ParentProjectDTO;
import com.pms.project.dto.ProjectGMemberDTO;
import com.pms.project.dto.ProjectInsertDTO;
import com.pms.project.dto.ProjectSearchDTO;
import com.pms.project.dto.ProjectSelectDTO;
import com.pms.project.dto.ProjectStatus;
import com.pms.project.dto.ProjectTotalDTO;
import com.pms.project.mapper.ProjectMapper;
import com.pms.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<ProjectSelectDTO> findUserProjects(String userId) {
        // [1] DB 왕복 1회: 기본 프로젝트 정보 로드
        List<ProjectSelectDTO> allProjects = projectMapper.selectUserProjects(userId);
        if (allProjects.isEmpty()) return allProjects;

        // [2] 벌크 조회를 위한 ID 추출
        List<Integer> projectNos = allProjects.stream()
                .map(ProjectSelectDTO::getProjectNo).toList();

        // [3] DB 왕복 2, 3회: 일감 및 멤버 벌크 로딩
        List<JobDTO> allJobs = projectMapper.selectJobsByProjectNos(projectNos);
        List<MemberDTO> allMembers = projectMapper.selectMembersByProjectNos(projectNos);

        // [4] 고속 조회를 위한 Map 그룹화 (O(1))
        Map<Integer, List<JobDTO>> jobMap = allJobs.stream()
                .collect(Collectors.groupingBy(JobDTO::getProjectNo));
        Map<Integer, List<MemberDTO>> memberMap = allMembers.stream()
                .collect(Collectors.groupingBy(MemberDTO::getProjectNo));

        // [5] 자바 메모리 연산 (DB 재접속 없음)
        for (ProjectSelectDTO p : allProjects) {
            p.setProjectTotalDTO(calculateSubtreeStats(p, allProjects, jobMap, memberMap));
        }

        return allProjects;
    }

    /**
     * 계층형 통계 합산 로직 (재귀 활용)
     */
    private ProjectTotalDTO calculateSubtreeStats(ProjectSelectDTO current, List<ProjectSelectDTO> all, 
                                                  Map<Integer, List<JobDTO>> jobMap, 
                                                  Map<Integer, List<MemberDTO>> memberMap) {
        // 1. 하위 트리 ID 목록 수집 (나 포함)
        List<Integer> subtreeIds = new ArrayList<>();
        findSubtreeIds(current.getProjectNo(), all, subtreeIds);
        
        // 2. 일감 데이터 합산
        List<JobDTO> subJobs = subtreeIds.stream()
                .flatMap(id -> jobMap.getOrDefault(id, List.of()).stream()).toList();
        
        // 3. 인원 데이터 합산 (중복 제거)
        Set<String> subMemberIds = subtreeIds.stream()
                .flatMap(id -> memberMap.getOrDefault(id, List.of()).stream())
                .map(MemberDTO::getUserId).collect(Collectors.toSet());

        ProjectTotalDTO stats = new ProjectTotalDTO();
        stats.setTotalJobCount(subJobs.size());
        stats.setTotalMemberCount(subMemberIds.size());
        
        // 4. 실제 진척도 계산
        double avgProgress = subJobs.stream()
                .mapToDouble(JobDTO::getProgress).average().orElse(0.0);
        stats.setCurrentProgress(Math.round(avgProgress * 100.0) / 100.0);

        // 5. 예상 진척도 계산
        stats.setTargetProgress(calculateTimeTargetProgress(current, all, subtreeIds));

        return stats;
    }

    /**
     * 재귀적으로 하위 프로젝트 ID를 찾는 헬퍼 메서드
     */
    private void findSubtreeIds(Integer parentId, List<ProjectSelectDTO> all, List<Integer> result) {
        result.add(parentId);
        for (ProjectSelectDTO p : all) {
            if (parentId.equals(p.getParentProjectNo())) {
                findSubtreeIds(p.getProjectNo(), all, result);
            }
        }
    }

    /**
     * 시간 기준 예상 진척도 계산 (Java 날짜 연산)
     */
    private double calculateTimeTargetProgress(ProjectSelectDTO current, List<ProjectSelectDTO> all, List<Integer> subtreeIds) {
        LocalDate today = LocalDate.now();

        // 1. 하위 트리(나 포함)에 해당하는 프로젝트들만 필터링
        List<ProjectSelectDTO> subtreeProjects = all.stream()
                .filter(p -> subtreeIds.contains(p.getProjectNo()))
                .toList();

        // 2. 각 프로젝트별 예상 진척도 계산 후 평균 산출
        double avgTarget = subtreeProjects.stream().mapToDouble(p -> {
            // 시작일이나 종료일이 없으면 계획이 없는 것으로 간주 (0%)
            if (p.getStartDate() == null || p.getEndDate() == null) return 0.0;

            LocalDate start = convertToLocalDate(p.getStartDate());
            LocalDate end = convertToLocalDate(p.getEndDate());

            // 전체 계획 기간 (분모)
            long totalDays = java.time.temporal.ChronoUnit.DAYS.between(start, end);
            
            // 당일 시작-당일 종료인 경우의 예외 처리
            if (totalDays <= 0) return today.isBefore(start) ? 0.0 : 100.0;

            // 시작일로부터 오늘까지 경과된 기간 (분자)
            long passedDays = java.time.temporal.ChronoUnit.DAYS.between(start, today);

            // 진행률 계산 및 범위 보정 [0% ~ 100%]
            // 수식: (오늘 - 시작일) / (종료일 - 시작일) * 100
            double progress = (double) passedDays / totalDays * 100.0;
            return Math.min(100.0, Math.max(0.0, progress));
            
        }).average().orElse(0.0);

        // 소수점 둘째 자리까지 반올림
        return Math.round(avgTarget * 100.0) / 100.0;
    }
    

	@Override
	public List<ProjectSelectDTO> findAdminProjects() {
		List<ProjectSelectDTO> projects = projectMapper.selectAdminProjects();
		return projects;
	}


	@Override
	public List<ProjectSelectDTO> findProjectByOptions(ProjectSearchDTO searchDTO) {
		return projectMapper.selectProjectsByOptions(searchDTO);
	}


	@Override
	public List<ParentProjectDTO> findParentProjects() {
		return projectMapper.selectParentProjects();
	}

	// 새 프로젝트 페이지
	@Override
	public boolean findParentProjectDuration(ProjectInsertDTO projectInsertDTO) {
		// 1. 부모 프로젝트가 지정되지 않은 경우 (최상위 프로젝트 생성) 바로 통과
	    if (projectInsertDTO.getParentProjectNo() == null || projectInsertDTO.getParentProjectNo() == 0) {
	        return true; 
	    }
		ProjectInsertDTO parent = projectMapper.selectParentProjectDuration(projectInsertDTO);
		
		// 2. 조회결과에 오류있을 시
		if (parent == null || parent.getStartDate() == null || parent.getEndDate() == null) {
	        return false;
	    }
		
		// 3. 기간설정 잘못된 경우 (자식 시작일 < 부모시작일 || 자식종료일 > 부모 종료일) 에러반환 
		if ( convertToLocalDate(projectInsertDTO.getStartDate()).isBefore(convertToLocalDate(parent.getStartDate()))
				|| 
			convertToLocalDate(projectInsertDTO.getEndDate()).isAfter( convertToLocalDate(parent.getEndDate())) ) {
			return false;
		}
		return true;
	}
	// INSERT 결과로 생성된 row count 확인하여 성공 여부 판단, 복수테이블에 복수컬럼에 작업이 들어가야해서 트랜잭션
	@Override
	@Transactional
	public boolean addProject(ProjectInsertDTO projectInsertDTO) {
		// 프로젝트 생성일이 오늘이면 활성프로젝트 아니면 보관 프로젝트로 생성
		LocalDate startDate = convertToLocalDate(projectInsertDTO.getStartDate());
	    LocalDate today = LocalDate.now();
		if (startDate.equals(today)) {
			projectInsertDTO.setStatus(ProjectStatus.ACTIVE.getCode());
		} else {
			projectInsertDTO.setStatus(ProjectStatus.PAUSED.getCode());
		}
		
		// 2. 프로젝트 단일 INSERT 실행
	    // 결과가 0이면 WHERE NOT EXISTS에 걸려 중복 처리된 것임
	    int insertCount = projectMapper.insertProject(projectInsertDTO);
	    if (insertCount == 0) {
	        return false; // 중복 식별자 발생으로 생성 실패
	    }
		
		
	    // 이 시점에서 insertProject 매퍼의 <selectKey> 덕분에 
	    // projectInsertDTO.getProjectNo()에는 방금 생성된 PK 값이 들어있습니다.

	    // 3. 부모 프로젝트 멤버 상속 여부에 따라 분기 (Null 방어를 위해 Integer.valueOf 활용)
	    if (Integer.valueOf(1).equals(projectInsertDTO.getParentMemberYn()) 
	        && projectInsertDTO.getParentProjectNo() != null 
	        && projectInsertDTO.getParentProjectNo() > 0) {
	        
	        // 부모의 그룹-프로젝트 매핑 정보를 내 프로젝트로 복사
	        projectMapper.insertInheritedGroups(
	            projectInsertDTO.getProjectNo(),     // 내 프로젝트 번호 (새로 생성됨)
	            projectInsertDTO.getParentProjectNo() // 부모 프로젝트 번호
	        );
	    }
	    
	    return true;
	}
	
	// Date → LocalDate 변환 헬퍼 메서드
	private LocalDate convertToLocalDate(Date date) {
	    return date.toInstant()
	               .atZone(ZoneId.systemDefault())
	               .toLocalDate();
	}


	// 개요페이지
	@Override
	@Transactional(readOnly = true)
	public IssueTrackerDTO findJobTrackerPivot(String projectCode) {
		
		IssueTrackerDTO trackerDTO = new IssueTrackerDTO();
		
		// 피벗 In 절 쿼리를 만들기 위한 상태목록조회 쿼리 - oracle g11 에는 피벗 in 절 동적쿼리 지원안한다고함
		List<String> headers = projectMapper.selectJobStatusNames();
		trackerDTO.setHeaders(headers);

		if (headers != null && !headers.isEmpty()) {
	        // 3. 가져온 리스트로 반복문을 돌려 오라클 PIVOT IN 절 문자열 조립
	        StringBuilder pivotBuilder = new StringBuilder();
	        for (int i = 0; i < headers.size(); i++) {
	            String status = headers.get(i);
	            pivotBuilder.append("'").append(status).append("' AS \"").append(status).append("\"");
	            
	            // 마지막 요소가 아니면 쉼표 추가
	            if (i < headers.size() - 1) {
	                pivotBuilder.append(", ");
	            }
	        }
	        
	        String pivotInSQL = pivotBuilder.toString(); 
	        
	        // 조립된 문자열을 매퍼로 넘겨 피벗 데이터(Map 리스트) 조회
	        List<Map<String, Object>> rows = projectMapper.selectJobTrackerPivot(projectCode, pivotInSQL);
	        
	        // 5. 가로행 합계(Total) 계산 로직
	        for (Map<String, Object> row : rows) {
	            long total = 0;
	            for (String header : headers) {
	                Object val = row.get(header); // AS "상태명" 덕분에 키값은 상태명과 동일
	                if (val instanceof Number) {
	                    total += ((Number) val).longValue();
	                }
	            }
	            row.put("합계", total); // 계산된 합계를 Map에 추가
	        }
	        trackerDTO.setRows(rows);
	    }
		
		return trackerDTO;
	}
	
	@Override
	public Map<String, List<String>> findGroupMemberByCode(String projectCode) {
		return projectMapper.selectGroupMemberByCode(projectCode).stream()
		        .collect(Collectors.groupingBy(
		        		ProjectGMemberDTO::getGroupname,
	                    LinkedHashMap::new,  // 순서 유지
	                    Collectors.mapping(ProjectGMemberDTO::getUsername, Collectors.toList())
		            ));
	}
	@Override
	public List<NoticeDTO> findNoties() {
		return projectMapper.selectNotices();
	}
	@Override
	public List<ProjectSelectDTO> findFirstChildsByCode(String projectCode) {
		return projectMapper.selectFirstChildsByCode(projectCode, ProjectStatus.ACTIVE.getCode(), ProjectStatus.LOCKED.getCode());
	}

	@Override
	public Map<String, Object> findGanttDataByCode(String projectCode) {
		List<GanttDTO> list = projectMapper.selectGanttData(projectCode);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		
		list.forEach(dto -> {
			if (dto.getStartDate() != null && dto.getEndDate() != null) {
	            LocalDate start = LocalDate.parse(dto.getStartDate(), formatter);
	            LocalDate end   = LocalDate.parse(dto.getEndDate(), formatter);
	            dto.setDuration((int) ChronoUnit.DAYS.between(start, end) + 1);
	        }
		});
		
		Map<String, Object> result = new LinkedHashMap<>();
	    result.put("data", list);
	    result.put("links", List.of());
	    return result;
	}

}