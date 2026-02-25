package com.pms.config;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.pms.user.entity.UserEntity;

import lombok.Getter;

@Getter
public class CustomUserDetails extends User {

	private final UserEntity userEntity;

	public CustomUserDetails(UserEntity user) {
		super(
			user.getUserId(),
			user.getPasswd(),
			AuthorityUtils.createAuthorityList(
					user.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER")
			);
		this.userEntity = user;
	}
	
}
