package com.pms.config;

import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import com.pms.project.service.ProjectSecurityService;

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
		String reqUri = context.getRequest().getRequestURI();
		String method = getMethod(context.getRequest().getMethod());

		boolean isAuth = projectSecurityService.isAuth(userId, reqUri, method);

		return new AuthorizationDecision(isAuth);
	}

	private String getMethod(String method) {
		return switch (method.toUpperCase()) {
			case "POST" -> "CREATE";
			case "PUT", "PATCH" -> "UPDATE";
			case "DELETE" -> "DELETE";
			default -> "READ";
		};
	}

}
