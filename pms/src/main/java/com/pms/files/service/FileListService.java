package com.pms.files.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pms.files.dto.FileListDto;
import com.pms.files.entity.FilesDetailsEntity;
import com.pms.files.repository.FilesDetailsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FileListService {
	private final FilesDetailsRepository filesDetailsRepository;
	
	public List<FileListDto> findFileList(Integer filesNo) {
		
		List<FilesDetailsEntity> fileList = filesDetailsRepository.findByFilesEntity_FilesNo(filesNo);
		List<FileListDto> fileDto = fileList.stream()
                .map(FilesDetailsEntity::toDto)
                .collect(Collectors.toList());

		return fileDto;
	}

}
