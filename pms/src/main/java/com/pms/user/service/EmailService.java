package com.pms.user.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;
	private final StringRedisTemplate redisTemplate;

	@Async
	public void sendPwResetMail(String userMail, String tokenValue) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(userMail);
		message.setSubject("[PMS Project] PW RESET");
		message.setText("PW RESET LINK\n" + "http://localhost:8080/user/pwResetLink?token=" + tokenValue);
		mailSender.send(message);
	}

	@Async
	public void sendEmailChangeMail(String userId, String newUsername, String newEmail, String token) {
		String redisKey = "PROFILE_UPDATE:" + token;
		String redisValue = userId + ":" + newUsername + ":" + newEmail;
		redisTemplate.opsForValue().set(redisKey, redisValue, Duration.ofMinutes(5));

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(newEmail);
		message.setSubject("[PMS Project] EMAIL CHANGE");
		message.setText("CHANGE LINK\n" + "http://localhost:8080/user/updateEmail?token=" + token);
		mailSender.send(message);
	}

}
