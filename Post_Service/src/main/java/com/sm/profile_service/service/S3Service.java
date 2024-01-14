package com.sm.profile_service.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

	String saveFile(MultipartFile file, int id);
	
	String deleteFile(String fileName);
	
	List<String> listAllFiles();
	
	 String getUrl();
}
