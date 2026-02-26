package com.pms.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
	@DisplayName("중복된 ID가 있을 시 예외가 발생해야 한다.")
	public void idCheckMockTest() {
		System.out.println("[Mock] ID CHECK TEST START");
		// given
		UserDto userDto = new UserDto();
		userDto.setUserId("mockUser01");
		userDto.setPassword("1234");
		when(userRepository.existsById("mockUser01")).thenReturn(true);

		// when -> then
		assertThatThrownBy(() -> userService.addUser(userDto))
								.isInstanceOf(RuntimeException.class)
								.hasMessageContaining("이미 존재하는 ID입니다.");
		System.out.println("[Mock] ID CHECK TEST SUCCESS");
	}

	@Test
	@DisplayName("회원가입 시 PW는 암호화 되어야 한다.")
	public void pwEncodeMockTest() {
		System.out.println("[Mock] PW HASHING TEST START");
		// given
		UserDto userDto = new UserDto();
		userDto.setUserId("mockUser01");
		userDto.setPassword("1234");
		when(passwordEncoder.encode("1234")).thenReturn("encode_1234");

		// when
		userService.addUser(userDto);

		// then
		verify(passwordEncoder).encode("1234");

		// 상태 검증
		ArgumentCaptor<UserEntity> userCap = ArgumentCaptor.forClass(UserEntity.class);
		verify(userRepository).save(userCap.capture());
		UserEntity user = userCap.getValue();

		// PW 확인
		assertThat(user.getPasswd()).as("PW는 Not Null입니다.").isNotEqualTo(null);
		assertThat(user.getPasswd()).as("PW가 암호화되지 않았습니다.").isEqualTo("encode_1234");
		System.out.println("[Mock] PW HASHING TEST SUCCESS");
	}
	
	@Test
	@DisplayName("회원의 Status가 정상일 경우 UserDetails을 반환해야 한다.")
	public void loginSuccessMockTest() {
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
	public void loginFailMockTest() {
		System.out.println("[Mock] LOGIN FAIL TEST START");
		
		// given
		String userId = "mockUser";
		UserEntity user = UserEntity.builder()
									.userId(userId)
									.status(120)
									.build();
		Integer userStatus = user.getStatus();
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		
		// when -> then
		assertThatThrownBy(() -> userSecurityService.loadUserByUsername(userId))
													.isInstanceOf(DisabledException.class)
													.hasMessage("올바른 상태 코드가 아닙니다[" + userStatus + "]. 관리자에게 문의하세요.");
		
		System.out.println("[Mock] LOGIN FAIL TEST SUCCESS");
	}
}
