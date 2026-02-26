package com.pms.common.error;


import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

	@Controller
	public class CustomErrorController implements ErrorController {

	    @RequestMapping("/error")
	    public String handleError(HttpServletRequest request, Model model) {
	        // 에러 상태 코드 가져오기 (404, 500, 403 등)
	        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

	        if (status != null) {
	            int statusCode = Integer.parseInt(status.toString());

	            // 404: 페이지 없음
	            if (statusCode == HttpStatus.NOT_FOUND.value()) {
	                return "error/404";
	            }
	            // 403: 접근 권한 없음 
	            if (statusCode == HttpStatus.FORBIDDEN.value()) {
	                return "error/403";
	            }
	            // 500: 서버 내부 오류 ( 파일 업로드 에러 등)
	            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	                return "error/500";
	            }
	        }
	        
	        // 정의되지 않은 에러는 기본 에러 페이지로
	        return "error/error";
	    }
	}