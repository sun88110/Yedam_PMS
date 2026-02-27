package com.pms.config;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import com.pms.project.dto.ProjectSecurityMenuDto;
import com.pms.project.service.ProjectSecurityService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

	private final ProjectSecurityService projectSecurityService;

	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
		Authentication auth = authentication.get();
		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
			return new AuthorizationDecision(false);
		}

		// 관리자 확인
		CustomUserDetails customUser = (CustomUserDetails) auth.getPrincipal();
		if (customUser.getUserEntity().isAdmin()) {
			return new AuthorizationDecision(true);
		}

		String userId = auth.getName();
		HttpServletRequest request = context.getRequest();
	    String reqUri = request.getRequestURI();
	    
	    // 프로젝트 관리 url 확인
	    ProjectSecurityMenuDto menu = projectSecurityService.findMenu(reqUri);
	    
		// 410번(프로젝트 설정)일 경우 처리
		String projectNo = null;
		if (menu != null && menu.getMenuId() == 410) {
			projectNo = request.getParameter("projectNo");
		}
		// PM 체크
		if (projectNo != null) {
			boolean result = projectSecurityService.isPm(userId, projectNo);
			return new AuthorizationDecision(result);
		}
	    
	    // 권한 체크
		String method = getMethod(request);
		boolean isAuth = projectSecurityService.isAuth(userId, reqUri, method);

		return new AuthorizationDecision(isAuth);
	}

	private String getMethod(HttpServletRequest req) {
		String method = req.getMethod().toUpperCase();
		String uri = req.getRequestURI();

		// GET으로 특정 페이지 진입할 경우 권한 매핑
		if ("GET".equals(method)) {
			return List.of("create", "update", "delete").stream()
					.filter(action -> uri.matches(".*(/" + action + "($|/.*))"))
			        .findFirst()
			        .map(String::toUpperCase)
			        .orElse("READ");
		}

		return switch (method.toUpperCase()) {
		case "POST" -> "CREATE";
		case "PUT", "PATCH" -> "UPDATE";
		case "DELETE" -> "DELETE";
		default -> "READ";
		};
	}

}
