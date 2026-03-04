package com.pms.project.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import com.pms.project.dto.ProjectSecurityMenuDto;
import com.pms.project.mapper.ProjectSecurityMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectSecurityService {

	private final ProjectSecurityMapper projectSecurityMapper;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Transactional(readOnly = true)
	public boolean isAuth(String userId, String url, String method) {
		// 등록된 menu인지 확인
		ProjectSecurityMenuDto menu = findMenu(url);
		if (menu == null) {
			return true;
		}

		Integer menuId = menu.getMenuId();
		String value = null;
		if (menu.getUrlData().contains("{projectCode}")) {
		    value = findValue(menu.getUrlData(), url);
		} else if (menu.getMenuId() == 410) { 
	        return false;
	    }
		
		boolean isPm = "PROJECT".equals(menu.getType()) && projectSecurityMapper.checkPm(userId, value);
		if (isPm) {
			return true;
		}

		return projectSecurityMapper.checkAuth(userId, value, menuId, method);
	}
	
	// 프로젝트 key 추출
	public String extractProjectKey(String url) {
		ProjectSecurityMenuDto menu = findMenu(url);
		if (menu == null || menu.getUrlData() == null) {
			return null;
		}

		String pattern = menu.getUrlData();
		if (!pathMatcher.match(pattern, url)) {
			return null;
		}

		Map<String, String> result = pathMatcher.extractUriTemplateVariables(pattern, url);
		String projectNo = result.get("projectNo");
		String projectCode = result.get("projectCode");

		if (projectNo != null) {
			return projectNo;
		}
		if (projectCode != null) {
			projectCode = projectSecurityMapper.findProjectNoByCode(projectCode);
			return projectCode;
		}
		return null;
	}

	// DB에서 url 검색
	public ProjectSecurityMenuDto findMenu(String url) {
		return projectSecurityMapper.selectAllMenus().stream().filter(m -> pathMatcher.match(m.getUrlData(), url))
				.sorted((m1, m2) -> pathMatcher.getPatternComparator(url).compare(m1.getUrlData(), m2.getUrlData()))
				.findFirst()
				.orElse(null);
	}

	// url에서 변수 추출
	private String findValue(String menu, String url) {
		Map<String, String> value = pathMatcher.extractUriTemplateVariables(menu, url);
		String result = value.values().stream().findFirst().orElse(null);
		return result;
	}
	
	// manager에서 사용하는 메서드
	public boolean isPm(String userId, String projectNo) {
		if (projectNo == null || userId == null) {
			return false;
		}
	    return projectSecurityMapper.checkPm(userId, projectNo);
	}
	
}
