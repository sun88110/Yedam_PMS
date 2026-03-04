package com.pms.user.web;

import java.time.LocalDateTime;

import com.pms.user.entity.UserEntity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

	@NotBlank
	private String userId;

	@NotBlank
	@Pattern(
			regexp = "^(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,}$",
			message = "비밀번호는 8자 이상이며, 최소 하나의 특수문자를 포함해야 합니다.")
	private String password;

	@NotBlank
	private String username;

	@NotNull
	private boolean admin = false;

	private Integer status = 120;

	@NotBlank
	@Email
	private String email;

	public UserEntity toEntity(String encodedPw) {
		return UserEntity.builder()
				.userId(this.userId == null ? null : this.userId.trim())
				.passwd(encodedPw)
				.username(this.username == null ? null : this.username.trim())
				.admin(this.admin)
				.status(this.status)
				.createtime(LocalDateTime.now())
				.email(this.email)
				.build();
	}
}
