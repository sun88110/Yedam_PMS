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

		// type이 PROJECT고, PM이면 true
		Integer menuId = menu.getMenuId();
		String value;
		if (!"PROJECT".equals(menu.getType())) {
		    value = null; 
		} else {
			value = findValue(menu.getUrlData(), url);
		}
		
		boolean isPm = "PROJECT".equals(menu.getType()) && projectSecurityMapper.checkPm(userId, value);
		if (isPm) {
			return true;
		}

		return projectSecurityMapper.checkAuth(userId, value, menuId, method);
	}

	// DB에서 url 검색
	private ProjectSecurityMenuDto findMenu(String url) {
		return projectSecurityMapper.selectAllMenus().stream().filter(m -> pathMatcher.match(m.getUrlData(), url))
				.max((m1, m2) -> pathMatcher.getPatternComparator(url).compare(m1.getUrlData(), m2.getUrlData()))
				.orElse(null);
	}

	// url에서 변수 추출
	private String findValue(String menu, String url) {
		Map<String, String> value = pathMatcher.extractUriTemplateVariables(menu, url);
		String result = value.values().stream().findFirst().orElse(null);
		return result;
	}
}
