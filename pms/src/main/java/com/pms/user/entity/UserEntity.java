package com.pms.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class UserEntity {
	@Id
	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "passwd", nullable = false)
	private String passwd;

	@Column(name = "username", nullable = false)
	private String username;

	// 공통코드 : 1 / 0
	@Column(name = "admin", nullable = false)
	private boolean admin;

	// 공통코드 : 110 / 120 / 130
	@Column(name = "status", nullable = false)
	private Integer status;

	// 등록할 땐 null
	@Column(name = "lastlogin", nullable = true)
	private LocalDateTime lastlogin;

	@Column(name = "createtime", nullable = false)
	private LocalDateTime createtime;

	@Column(name = "email", nullable = false)
	private String email;

	@Builder
	private UserEntity(String userId, String passwd, String username, boolean admin, Integer status,
			LocalDateTime createtime, String email) {
		this.userId = userId;
		this.passwd = passwd;
		this.username = username;
		this.admin = admin;
		this.status = status;
		this.createtime = createtime;
		this.email = email;
	}
	
	// 더티 체킹
	public void updatePwEntity(String encrytedPw) {
		this.passwd = encrytedPw;
	}
	
	// 정보 업데이트
	public void updateProfile(String newUsername, String newEmail) {
		this.username = newUsername;
		this.email = newEmail;
	}

}
