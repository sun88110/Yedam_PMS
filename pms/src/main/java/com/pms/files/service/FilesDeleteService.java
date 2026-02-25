package com.pms.files.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pms.files.entity.FilesDetailsEntity;
import com.pms.files.repository.FilesDetailsRepository;
import com.pms.files.repository.FilesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class FilesDeleteService {
	private final FilesDetailsRepository filesDetailsRepository;
	private final FilesRepository fileRepository;
	@Value("${file.upload.path}")
	private String uploadPath;

	// 실행 메서드
	public void deleteFiles(List<Integer> detailsNos) {
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

		// 하드 삭제
		File file = new File(uploadPath, fileDetail.getFilesUuid());
		if (file.exists()) {
			file.delete();
		}

		// DB 삭제
		filesDetailsRepository.deleteById(detailsNo);

		// 모든 디테일 삭제 됐으면 마스터 삭제
		Integer filesNo = fileDetail.getFilesEntity().getFilesNo();
		List<FilesDetailsEntity> detailsList =filesDetailsRepository.findByFilesEntity_FilesNo(filesNo); 
		if (detailsList.isEmpty()) {
			fileRepository.deleteById(filesNo);
		}
	}

}
