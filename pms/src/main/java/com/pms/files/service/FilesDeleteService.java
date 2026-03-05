package com.pms.files.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.pms.files.entity.FilesDetailsEntity;
import com.pms.files.repository.FilesDetailsRepository;
import com.pms.files.repository.FilesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class FilesDeleteService {
	
	private final AmazonS3 amazonS3; // S3 주입
	private final FilesDetailsRepository filesDetailsRepository;
	private final FilesRepository fileRepository;

	@Value("${cloud.aws.s3.bucket}")
    private String bucket;

	// 실행 메서드
	public void deleteFiles(List<Integer> detailsNos) {
		System.out.println("[detailsNos]: " + detailsNos);
		if (detailsNos == null) {
			return;
		}

		for (Integer no : detailsNos) {
			deleteFileDetail(no);
		}
	}

	// 삭제 메서드
	public void deleteFileDetail(Integer detailsNo) {
		// 상세 정보 조회
		FilesDetailsEntity fileDetail = filesDetailsRepository.findById(detailsNo)
				.orElseThrow(() -> new RuntimeException("저장된 파일이 없습니다."));
		Integer filesNo = fileDetail.getFilesEntity().getFilesNo();
		
		// S3 삭제
        if (amazonS3.doesObjectExist(bucket, fileDetail.getFilesUuid())) {
            amazonS3.deleteObject(bucket, fileDetail.getFilesUuid());
        }

		// DB 삭제
        System.out.println("[detailsNo]: " + detailsNo);
		filesDetailsRepository.deleteById(detailsNo);
		filesDetailsRepository.flush();

		// 모든 디테일 삭제 됐으면 마스터 삭제
		List<FilesDetailsEntity> detailsList =filesDetailsRepository.findByFilesEntity_FilesNo(filesNo); 
		if (detailsList.isEmpty()) {
			fileRepository.deleteById(filesNo);
		}
	}

}
