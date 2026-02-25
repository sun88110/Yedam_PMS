package com.pms.files.util;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FilesHasUtil {
	
	public boolean hasUploadFiles(List<MultipartFile> files) {
		return files != null && !files.isEmpty() && !files.get(0).isEmpty();
	}
	
	public boolean hasDeleteFiles(List<Integer> deleteFileList) {
		return deleteFileList != null && !deleteFileList.isEmpty();
	}
}
