package com.pms.files.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pms.files.entity.FilesDetailsEntity;

public interface FilesDetailsRepository extends JpaRepository<FilesDetailsEntity, Integer> {
	List<FilesDetailsEntity> findByFilesEntity_FilesNo(Integer filesNo);

	boolean existsByFilesEntity_FilesNo(Integer filesNo);
}
