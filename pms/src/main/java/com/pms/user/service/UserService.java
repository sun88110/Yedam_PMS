package com.pms.user.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.user.entity.UserEntity;
import com.pms.user.repository.UserRepository;
import com.pms.user.web.UserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final StringRedisTemplate redisTemplate;

	@Transactional
	public void addUser(UserDto userDto) {
		String userId = userDto.getUserId().trim();
		String encodedPw = passwordEncoder.encode(userDto.getPassword());

		if (userRepository.existsById(userId)) {
			throw new IllegalArgumentException("이미 존재하는 ID입니다.");
		}

		UserEntity userData = userDto.toEntity(encodedPw);
		userRepository.save(userData);
	}

	@Transactional
	public void modifyDateUpdate(String userId) {
		userRepository.updateLastLogin(userId, LocalDateTime.now());
	}

	// 업데이트 메일 발송
	public void sendCheckEmail(String userId, String newUsername, String newEmail) throws Exception {
		if (newUsername.trim().isEmpty() || newEmail.trim().isEmpty()) {
			throw new Exception("이름과 이메일은 필수 입력 항목입니다.");
		}
		String token = UUID.randomUUID().toString();
		emailService.sendEmailChangeMail(userId, newUsername, newEmail, token);
	}

	@Transactional
	public void modifyEmail(String token) throws Exception {
		String redisKey = "PROFILE_UPDATE:" + token;
		String storedData = redisTemplate.opsForValue().get(redisKey);

		if (storedData == null) {
			throw new RuntimeException("유효하지 않거나 만료된 토큰입니다.");
		}

		// 데이터 파싱 (userId : newEmail)
		String[] parts = storedData.split(":");
		String userId = parts[0];
		String newUsername = parts[1];
		String newEmail = parts[2];

		// DB 업데이트
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		user.updateProfile(newUsername, newEmail);

		// 사용한 토큰 삭제
		redisTemplate.delete(redisKey);
	}

}
