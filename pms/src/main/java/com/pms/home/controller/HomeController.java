package com.pms.home.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.pms.config.CustomUserDetails;
import com.pms.home.dto.HomeDto;
import com.pms.home.service.HomeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final HomeService homeService;

	@GetMapping("/home")
	public String home(@AuthenticationPrincipal CustomUserDetails customUser, Model model) {
		if (customUser == null) {
			return "redirect:/user/login";
		}
		String userId = customUser.getUsername();
		HomeDto dtoList = homeService.loadMainPage(userId);
		model.addAttribute("list", dtoList);
		return "home/home-index";
	}
}
