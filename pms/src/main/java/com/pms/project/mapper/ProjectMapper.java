package com.pms.project.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pms.project.dto.GanttDTO;
import com.pms.project.dto.HistoryDTO;
import com.pms.project.dto.HolidayDTO;
import com.pms.project.dto.JobDTO;
import com.pms.project.dto.MemberDTO;
import com.pms.project.dto.NoticeDTO;
import com.pms.project.dto.PMGroupDTO;
import com.pms.project.dto.ParentProjectDTO;
import com.pms.project.dto.ProjectGMemberDTO;
import com.pms.project.dto.ProjectInsertDTO;
import com.pms.project.dto.ProjectSearchDTO;
import com.pms.project.dto.ProjectSelectDTO;

@Mapper
public interface ProjectMapper {
    // 사용자의 프로젝트 목록 조회 (통계 포함)
    List<ProjectSelectDTO> selectUserProjects(@Param("userId") String userId, @Param("isAdmin") boolean isAdmin);
    // 사용자가 소속된 pm 권한지닌 그룹 조회
    List<PMGroupDTO> selectIsPM(@Param("userId") String userId);
    
    // 사용자의 검색결과를 바탕으로하는 프로젝트 목록 조회 (통계 포함) 
    List<ProjectSelectDTO> selectProjectsByOptions(
    	    @Param("search") ProjectSearchDTO searchDTO, 
    	    @Param("currentUserId") String currentUserId, 
    	    @Param("isAdmin") boolean isAdmin
    	);
    List<ParentProjectDTO> selectParentProjects();
    
    // 조회 가속을 위한 메서드 추가 (List<Integer>를 파라미터로 받음)
    List<JobDTO> selectJobsByProjectNos(@Param("projectNos") List<Integer> projectNos);
    List<MemberDTO> selectMembersByProjectNos(@Param("projectNos") List<Integer> projectNos);
    
    // 새 프로젝트 페이지
    // 사용자의 입력값을 바탕으로 프로젝트 추가
    int insertProject(ProjectInsertDTO projectInsertDTO);
    // 신규 프로젝트 등록 시 상위프로젝트 멤버 상속
    int insertInheritedGroups(@Param("newProjectNo") int newProjectNo, @Param("parentProjectNo") int parentProjectNo);
    // 생성자 그룹 PM 권한 부여
    void insertCreatorGroupAsPM(@Param("projectNo") Integer projectNo, @Param("pmGroupNos") List<Integer> pmGroupNos);
    // 상위 프로젝트의 시작-종료 기간 조회 
    ProjectInsertDTO selectParentProjectDuration(ProjectInsertDTO projectInsertDTO);
    // 프로젝트 관리자 이름 목록조회
    List<String> selectAssigneeNames();
    
    // 프로젝트 개요 페이지 
    ProjectInsertDTO selectInfoByCode(@Param("projectCode") String projectCode);
    // 테이블 헤더 및 반복문 기준이 될 상태 목록 조회
    List<String> selectJobStatusNames();
    // 테이블 데이터 조회
    List<Map<String, Object>> selectJobTrackerPivot(@Param("projectCode") String projectCode, @Param("pivotInSQL") String pivotInSQL);
    
    // 프로젝트에 소속된 모든 그룹의 모든 멤버 표시
    List<ProjectGMemberDTO> selectGroupMemberByCode(@Param("projectCode") String projectCode);
    // 최신 공지사항 목록 조회
    List<NoticeDTO> selectNotices();
    // 하위 프로젝트 목록 조회(이름, 식별자, 상태)
    List<ProjectSelectDTO> selectFirstChildsByCode(@Param("projectCode") String projectCode, Integer active, Integer locked );
    
    
    // GanttChart
    List<GanttDTO> selectGanttData(@Param("projectCode") String projectCode);
    
    // History
    List<HistoryDTO> selectHistoryByCode(@Param("projectCode") String projectCode);
    // 선택된 기간의 이력 조회
    List<HistoryDTO> selectHistoryByCodeAndDate(Map<String, Object> params);
    // 과거의 일감 존재 여부 확인
    int selectCountOlderHistory(Map<String, Object> params);
}