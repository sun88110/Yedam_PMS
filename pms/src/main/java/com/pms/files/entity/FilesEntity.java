package com.pms.files.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor
public class FilesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_seq")
	@SequenceGenerator(name = "files_seq", sequenceName = "FILES_SEQ", allocationSize = 1)
	private Integer filesNo;
	private String userId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime uploadDate;
	
	@Builder
	public FilesEntity(String userId) {
		this.userId = userId;
		this.uploadDate = LocalDateTime.now().withNano(0);	
	}
}
