package com.pms.user.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pms.user.service.PwrTokenService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class PwrTokenController {

	private final PwrTokenService pwrTokenService;

	@GetMapping("/pwResetPage")
	public String resetForm(Model model) {
		return "user/pw-reset-form";
	}

	// 이메일 발송
	@PostMapping("/pwResetSend")
	public String sendResetMail(@RequestParam("userId") String userId, RedirectAttributes rttr) {
		try {
			String testToken = pwrTokenService.sendResetMail(userId);
			rttr.addFlashAttribute("msg", "재설정 메일이 발송되었습니다. 유효 시간은 5분입니다.");
			rttr.addFlashAttribute("testToken", testToken);
			return "redirect:/user/login";
		} catch (Exception e) {
			rttr.addFlashAttribute("error", "메일 발송 중 오류가 발생하였습니다.");
			return "redirect:/user/pwResetPage";
		}
	}

	// 이메일 링크
	@GetMapping("/pwResetLink")
	public String checkToken(@RequestParam("token") String token, Model model, RedirectAttributes rttr) {
		boolean checkedToken = pwrTokenService.checkToken(token);
		
		if (!checkedToken) {
			rttr.addFlashAttribute("error", "유효하지 않거나 만료된 토큰입니다.");
			return "redirect:/user/pwResetPage";
		}

		model.addAttribute("token", token);
		return "user/new-pw-form";
	}

	// PW 변경
	@PutMapping("/pw")
	public String updatePwProcess(@RequestParam("token") String token, @RequestParam("newPw") String newPw, Model model,
			RedirectAttributes rttr) {

		try {
			pwrTokenService.modifyPwService(token, newPw);
			rttr.addFlashAttribute("msg", "PW가 성공적으로 변경되었습니다.");
			return "redirect:/user/login";
		} catch (Exception e) {
			rttr.addFlashAttribute("error", "PW 변경 중 오류가 발생하였습니다.");
			return "redirect:/user/pwResetPage";
		}
	}

}
