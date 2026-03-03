package com.pms.user.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;
	private final StringRedisTemplate redisTemplate;
	private final TemplateEngine templateEngine;

	@Async
	public void sendPwResetMail(String userMail, String tokenValue) {
		String link = "http://localhost:8080/user/pwResetLink?token=" + tokenValue;
		sendCommonMail(userMail,
				"[PMS] 비밀번호 재설정 안내", 
	            "비밀번호 재설정", 
	            "비밀번호를 잊으셨나요?<br/>아래 버튼을 클릭하여 새로운 비밀번호를 설정하실 수 있습니다.<br/>유효 시간은 5분입니다.", 
	            link, 
	            "비밀번호 재설정하기");
	}

	@Async
	public void sendEmailChangeMail(String userId, String newUsername, String newEmail, String token) {
		String redisKey = "PROFILE_UPDATE:" + token;
		String redisValue = userId + ":" + newUsername + ":" + newEmail;
		redisTemplate.opsForValue().set(redisKey, redisValue, Duration.ofMinutes(5));
		String link = "http://localhost:8080/user/updateEmail?token=" + token;
		
		sendCommonMail(newEmail,
				"[PMS] 회원정보 변경 확인 안내", 
	            "회원정보 변경 확인", 
	            "회원정보 변경을 완료하려면 인증이 필요합니다.<br/>아래 버튼을 클릭하시면 변경이 완료됩니다.<br/>유효 시간은 5분입니다.", 
	            link, 
	            "이메일 인증 완료");
	}

	private void sendCommonMail(String to, String subject, String title, String message, String link,
			String buttonText) {
		try {
			Context context = new Context();
			context.setVariable("title", title);
			context.setVariable("message", message);
			context.setVariable("link", link);
			context.setVariable("buttonText", buttonText);

			String htmlContent = templateEngine.process("mail/common-mail", context);

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlContent, true);

			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			log.error("Failed to send email to: {}", to, e);
			throw new RuntimeException("메일 발송 중 오류가 발생했습니다.");
		}
	}

}
