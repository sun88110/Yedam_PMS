package com.pms.files.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pms.files.entity.FilesDetailsEntity;
import com.pms.files.repository.FilesDetailsRepository;
import com.pms.files.util.FileCryptoUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FilesDownloadService {

	private final FilesDetailsRepository filesDetailsRepository;
	private final FileCryptoUtil fileCryptoUtil;
	@Value("${file.upload.path}")
	private String uploadPath;
	
	// DB에서 파일 조회
	public FilesDetailsEntity getFileDetails(Integer detailsNo) {
		return filesDetailsRepository.findById(detailsNo).orElseThrow(() -> new RuntimeException("파일 정보가 없습니다."));
	}
	
	// 다운로드
	public void downloadFile(Integer detailsNo, OutputStream outputStream) throws Exception {
		FilesDetailsEntity details = filesDetailsRepository.findById(detailsNo)
				.orElseThrow(() -> new RuntimeException("일치하는 파일이 존재하지 않습니다."));

		File encryptedFile = new File(uploadPath, details.getFilesUuid());
		if (!encryptedFile.exists()) {
			throw new RuntimeException("저장된 경로가 맞지 않습니다.");
		}

		try (InputStream is = new FileInputStream(encryptedFile)) {
			fileCryptoUtil.decrypt(is, outputStream);
		}
	}

	// 다중 다운로드
	public void fileZipDownload(List<Integer> detailsNos, OutputStream outputStream) throws Exception {
		// zip 스트림
		try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
			int i = 1;

			for (Integer no : detailsNos) {
				// 파일 조회
				FilesDetailsEntity details = filesDetailsRepository.findById(no).orElse(null);
				if (details == null) {
					continue;
				}

				File enFile = new File(uploadPath, details.getFilesUuid());
				if (!enFile.exists()) {
					continue;
				}

				String orgName = details.getFilesName();
				String finalName = (i++) + "_" + orgName;

				ZipEntry zipEntry = new ZipEntry(finalName);
				zos.putNextEntry(zipEntry);

				// 암호화 파일 -> 복호화 -> zip 스트림
				// decrypt 시 스트림을 닫지 않도록 조심해야 됨
				// 자세한건 util decryptZip 주석 참고
				try (InputStream is = new FileInputStream(enFile)) {
					fileCryptoUtil.decryptZip(is, zos);
				}
				zos.closeEntry();
			}
			zos.finish();
		}
	}
}