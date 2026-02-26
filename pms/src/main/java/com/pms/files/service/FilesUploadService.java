package com.pms.files.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pms.files.entity.FilesDetailsEntity;
import com.pms.files.entity.FilesEntity;
import com.pms.files.repository.FilesDetailsRepository;
import com.pms.files.repository.FilesRepository;
import com.pms.files.util.FileCryptoUtil;
import com.pms.files.util.FilesHasUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FilesUploadService {

    private final FilesDetailsRepository filesDetailsRepository;
	private final FilesRepository filesRepository;
	private final FileCryptoUtil fileCryptoUtil;
	private final FilesHasUtil filesHasUtil;

	@Value("${file.upload.path}")
	private String uploadPath;

	public Integer uploadFiles(List<MultipartFile> files, String userId, Integer filesNo) throws Exception {
		// 업로드할 파일이 없으면 반환
		if (!filesHasUtil.hasUploadFiles(files)) {
            return filesNo;
        }
		
		// 파일 부모 생성
		FilesEntity filesEntity = null;
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(filesNo);
		System.out.println();
		System.out.println();
		System.out.println();
		if (filesNo != null) {
			filesEntity = filesRepository.findById(filesNo).orElse(null);
			filesNo = null;
		}
		
		// 없으면 새로 생성
		if (filesNo == null) {
            filesEntity = FilesEntity.builder().userId(userId).build();
            filesRepository.save(filesEntity);
        }
		
		// 폴더 경로 생성
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		
		// 하드에 파일 저장
		filesUploadProcess(filesEntity, files, uploadDir);
		
		return filesEntity.getFilesNo();
	}
	
	// 하드 저장
	private void filesUploadProcess(FilesEntity filesEntity, List<MultipartFile> files, File uploadDir) throws Exception {
		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			String fileUuid = UUID.randomUUID().toString() + "_" + fileName;
			File target = new File(uploadDir, fileUuid);
			
			try (InputStream is = file.getInputStream();
				OutputStream os = new FileOutputStream(target)) {
				fileCryptoUtil.encrypt(is, os);
			}
			
			FilesDetailsEntity filesDetails = FilesDetailsEntity
												.builder()
												.filesEntity(filesEntity)
												.filesName(fileName)
												.filesUuid(fileUuid)
												.filesSize(file.getSize())
												.filesType(file.getContentType())
												.filesPath(uploadPath)
												.build();
			filesDetailsRepository.save(filesDetails);			
		}
	}

}
