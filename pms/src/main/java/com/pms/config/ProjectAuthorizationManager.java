package com.pms.config;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;

import com.pms.project.dto.ProjectSecurityMenuDto;
import com.pms.project.service.ProjectSecurityService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

	private final ProjectSecurityService projectSecurityService;
	private final PathMatcher pathMatcher;

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
		
		System.out.println();
		System.out.println();
		System.out.println();

		String userId = auth.getName();
		HttpServletRequest request = context.getRequest();
	    String reqUri = request.getRequestURI();
	    
	    // 프로젝트 관리 url 확인
	    ProjectSecurityMenuDto menu = projectSecurityService.findMenu(reqUri);
	    String projectNo = request.getParameter("projectNo");
	    
	    System.out.println("[reqUri]: " + reqUri);
		System.out.println("[menu]: " + menu);
	    if (projectNo == null && menu != null && menu.getUrlData() != null) {
	        String pattern = menu.getUrlData();
	        if (pathMatcher.match(pattern, reqUri)) {
	            var vars = pathMatcher.extractUriTemplateVariables(pattern, reqUri);
	            projectNo = vars.getOrDefault("projectNo", vars.get("projectCode"));
	        }
	    }
		
		// PM 체크
	    if (projectNo != null && projectSecurityService.isPm(userId, projectNo)) {
	        System.out.println("[PM 승인]: " + userId + ", Key: " + projectNo);
	        return new AuthorizationDecision(true);
	    }
	    
		System.out.println();
		System.out.println();
		System.out.println();
		
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
