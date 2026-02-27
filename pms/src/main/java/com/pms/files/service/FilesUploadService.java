package com.pms.files.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
	
	private final AmazonS3 amazonS3;
    private final FilesDetailsRepository filesDetailsRepository;
	private final FilesRepository filesRepository;
	private final FileCryptoUtil fileCryptoUtil;
	private final FilesHasUtil filesHasUtil;

	@Value("${cloud.aws.s3.bucket}")
    private String bucket;

	public Integer uploadFiles(List<MultipartFile> files, String userId, Integer filesNo) throws Exception {
		// 업로드할 파일이 없으면 반환
		if (!filesHasUtil.hasUploadFiles(files)) {
            return filesNo;
        }
		
		// 파일 부모 생성
		FilesEntity filesEntity = null;
		if (filesNo != null) {
			filesEntity = filesRepository.findById(filesNo).orElse(null);
			filesNo = null;
		}
		
		// 없으면 새로 생성
		if (filesNo == null) {
            filesEntity = FilesEntity.builder().userId(userId).build();
            filesRepository.save(filesEntity);
        }
		
		// AWS 저장
		filesUploadProcess(filesEntity, files);
		
		return filesEntity.getFilesNo();
	}
	
	// AWS 저장
	private void filesUploadProcess(FilesEntity filesEntity, List<MultipartFile> files) throws Exception {
		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			String fileUuid = UUID.randomUUID().toString() + "_" + fileName;
			
			// 임시 파일 생성 (서버 메모리 보호)
            File tempFile = File.createTempFile("enc_", ".tmp");
            tempFile.deleteOnExit();
			
			try (InputStream is = file.getInputStream();
				OutputStream os = new FileOutputStream(tempFile)) {
				fileCryptoUtil.encrypt(is, os);
			}
			
			// 메타데이터 설정
			ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(tempFile.length());
            metadata.setContentType(file.getContentType());
            
            // S3 업로드
            String s3Key = "uploads/" + fileUuid;
            try (InputStream uploadIs = new FileInputStream(tempFile)) {
                amazonS3.putObject(new PutObjectRequest(bucket, s3Key, uploadIs, metadata));
            }
            
            // 임시 파일 삭제
            tempFile.delete();
			
			FilesDetailsEntity filesDetails = FilesDetailsEntity
												.builder()
												.filesEntity(filesEntity)
												.filesName(fileName)
												.filesUuid(s3Key)
												.filesSize(file.getSize())
												.filesType(file.getContentType())
												.filesPath(bucket)
												.build();
			filesDetailsRepository.save(filesDetails);			
		}
	}

}
