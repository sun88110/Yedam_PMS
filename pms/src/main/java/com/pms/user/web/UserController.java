package com.pms.user.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pms.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	// 회원가입 페이지 이동
	@GetMapping("/new")
	public String newUserForm(Model model) {
		model.addAttribute("userDto", new UserDto());
		return "user/register-form";
	}

	// 회원가입 실행
	@PostMapping("/")
	public String register(@Valid @ModelAttribute("userDto") UserDto userDto, BindingResult bindingResult,
			RedirectAttributes rttr) {
		if (bindingResult.hasErrors()) {
			return "user/register-form";
		}

		try {
			userService.addUser(userDto);
		} catch (Exception e) {
			bindingResult.rejectValue("userId", "newUserErr", e.getMessage());
			return "user/register-form";
		}

		rttr.addFlashAttribute("success", "회원가입이 완료되었습니다.");
		return "redirect:/user/login";
	}

	// 로그인
	@GetMapping("/login")
	public String login(Model model, Authentication authentication) {
		// 로그인상태면 메인으로
		if (authentication != null && authentication.isAuthenticated()) {
			return "redirect:/";
		}
		model.addAttribute("loginDto", new LoginDto());
		return "user/login-form";
	}

	// 이메일 발송
	@PostMapping("/updateEmailSend")
	public String sendResetMail(
			@RequestParam("userId") String userId,
			@RequestParam("newUsername") String newUsername,
			@RequestParam("newEmail") String newEmail,
			RedirectAttributes rttr) {
		try {
			userService.sendCheckEmail(userId, newUsername, newEmail);
			return "redirect:/user/logout";
		} catch (Exception e) {
			rttr.addFlashAttribute("error", "메일 발송 중 오류가 발생하였습니다.");
			return "redirect:/";
		}
	}

	// 이메일 변경
	@GetMapping("/updateEmail")
	public String verifyEmail(@RequestParam String token, RedirectAttributes rttr) {
		try {
			userService.modifyEmail(token);
			rttr.addFlashAttribute("msg", "정보 변경이 성공적으로 완료되었습니다.");
			return "redirect:/user/login";
		} catch (Exception e) {
			rttr.addFlashAttribute("error", "정보 변경 중 오류가 발생하였습니다.");
			return "redirect:/user/login";
		}
	}
}
