package com.pms.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pms.user.entity.UserEntity;
import com.pms.user.repository.UserRepository;
import com.pms.user.service.UserSecurityService;
import com.pms.user.service.UserService;
import com.pms.user.web.UserDto;

@ExtendWith(MockitoExtension.class)
public class UserMockTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@InjectMocks
	private UserSecurityService userSecurityService;

	@Test
	@DisplayName("사용자 등록 시 비밀번호 암호화 및 저장 호출 확인")
	public void idCheckMockTest() throws Exception {
		System.out.println("[Mock] USER JOIN TEST START");
		// given
		UserDto userDto = new UserDto();
		userDto.setUserId("testUser");
		userDto.setPassword("1234");
		userDto.setUsername("테스터");
		userDto.setEmail("test@test.com");

		// 암호화 동작 모킹
		when(passwordEncoder.encode("1234")).thenReturn("hashed_1234");

		// when
		userService.addUser(userDto);

		// then
		verify(userRepository).save(any(UserEntity.class));
		verify(passwordEncoder).encode("1234");

		System.out.println("[Mock] USER JOIN TEST SUCCESS");
	}

	@Test
	@DisplayName("로그인 시 활성화된(110) 사용자라면 UserDetails를 반환한다")
	public void loginSuccessMockTest() throws Exception {
		System.out.println("[Mock] LOGIN SUCCESS TEST START");

		// given
		String userId = "mockUser";
		UserEntity user = UserEntity.builder()
									.userId(userId)
									.passwd("1234")
									.status(110)
									.build();
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		UserDetails userDetails = userSecurityService.loadUserByUsername(userId);

		// then
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.getUsername()).isEqualTo(userId);

		System.out.println("[Mock] LOGIN SUCCESS TEST SUCCESS");
	}

	@Test
	@DisplayName("회원의 Status가 110이 아닐 경우 로그인이 제한 되어야 한다.")
	public void loginFailMockTest() throws Exception {
		System.out.println("[Mock] LOGIN FAIL TEST START");

		// given
		String userId = "mockUser";
		UserEntity user = UserEntity.builder()
									.userId(userId)
									.status(120)
									.build();
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when -> then
		assertThatThrownBy(() -> userSecurityService
				.loadUserByUsername(userId))
				.isInstanceOf(DisabledException.class);
		
		System.out.println("[Mock] LOGIN FAIL TEST SUCCESS");
	}
}
