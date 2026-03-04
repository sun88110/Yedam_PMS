package com.pms.user.service;

import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.user.entity.PwrTokenEntity;
import com.pms.user.entity.UserEntity;
import com.pms.user.repository.PwrTokenRepository;
import com.pms.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PwrTokenService {

	private final UserRepository userRepository;
	private final PwrTokenRepository pwrTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	
	private static final String PW_REGEX = "^(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,}$";
    private static final Pattern PATTERN = Pattern.compile(PW_REGEX);

	@Transactional
	public String sendResetMail(String userId) {
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));

		// 기존에 발행된 토큰이 있는지 확인
		// 있으면 삭제
		pwrTokenRepository.findByUserId(userId).ifPresent(pwrTokenRepository::delete);

		// 랜덤 토큰 저장
		String tokenValue = UUID.randomUUID().toString();
		// Redis 전용 엔티티
        PwrTokenEntity newToken = PwrTokenEntity.builder()
                								.userId(userId)
                								.tokenValue(tokenValue)
                								.expiration(300L)
                								.build();
		
		// 메일 발송
		emailService.sendPwResetMail(user.getEmail(), tokenValue);
		pwrTokenRepository.save(newToken);

		return tokenValue;
	}
	
	public boolean checkToken(String tokenValue) {
        return pwrTokenRepository.findByTokenValue(tokenValue).isPresent();
    }

	// 토큰 확인 후 PW 변경
	@Transactional
	public void modifyPwService(String token, String newPw) {
		validate(newPw);
		
		PwrTokenEntity pwrToken = pwrTokenRepository.findByTokenValue(token)
				.orElseThrow(() -> new IllegalArgumentException("토큰이 존재하지 않습니다."));

		UserEntity user = userRepository.findById(pwrToken.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		String encodedPw = passwordEncoder.encode(newPw);
		user.updatePwEntity(encodedPw);

		// PW 변경 후 Redis에서 토큰 삭제
		pwrTokenRepository.delete(pwrToken);
	}
	
	private void validate(String password) {
        if (password == null || !PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이며, 최소 하나의 특수문자를 포함해야 합니다.");
        }
    }

}
