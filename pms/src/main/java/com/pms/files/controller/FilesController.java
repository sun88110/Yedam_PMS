package com.pms.files.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;

import com.pms.files.entity.FilesDetailsEntity;
import com.pms.files.service.FilesDownloadService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/download")
public class FilesController {

	private final FilesDownloadService filesDownloadService;

	@Value("${file.upload.path}")
	private String uploadPath;

	@GetMapping("/{detailsNo}")
	public void fileDownload(@PathVariable Integer detailsNo, HttpServletResponse res) throws Exception {
		// DB에서 파일 조회
		FilesDetailsEntity details = filesDownloadService.getFileDetails(detailsNo);

		// 헤더 설정 -> 한글 깨짐 방지
		String encodedFileName = UriUtils.encode(details.getFilesName(), StandardCharsets.UTF_8);
		res.setContentType("application/octet-stream");
		res.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
		res.setContentLengthLong(details.getFilesSize());

		// 복호화 -> 스트림 전송
		filesDownloadService.downloadFile(detailsNo, res.getOutputStream());
	}

	@GetMapping("/zip")
	public void fileDownloadZip(@RequestParam List<Integer> detailsNos, HttpServletResponse res) throws Exception {
		String zipName = "pms_files_" + System.currentTimeMillis() + ".zip";
		res.setContentType("application/zip");
		res.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipName + "\"");
		filesDownloadService.fileZipDownload(detailsNos, res.getOutputStream());
	}

}
