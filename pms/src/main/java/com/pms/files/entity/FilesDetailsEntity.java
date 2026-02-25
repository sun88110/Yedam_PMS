package com.pms.files.entity;

import com.pms.files.dto.FileListDto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files_details")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FilesDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileDetails_seq")
	@SequenceGenerator(name = "fileDetails_seq", sequenceName = "FILES_DETAILS_SEQ", allocationSize = 1)
	private Integer detailsNo;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "files_no")
	private FilesEntity filesEntity;
	private String filesName;
	private String filesUuid;
	private Long filesSize;
	private String filesType;
	private String filesPath;
	
	public static FileListDto toDto(FilesDetailsEntity entity) {
		return FileListDto.builder()
						.filesName(entity.getFilesName())
						.detailsNo(entity.getDetailsNo())
						.filesPath(entity.getFilesPath())
						.filesSize(entity.getFilesSize())
						.filesType(entity.getFilesType())
						.filesUuid(entity.getFilesUuid())
						.build();
	}
}
