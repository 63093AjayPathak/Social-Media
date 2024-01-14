package com.sm.profile_service.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.sm.profile_service.exceptions.PostNotSavedException;
import com.sm.profile_service.service.S3Service;

@Service
public class S3ServiceImpl implements S3Service {

	@Value("${bucketName}")
	private String bucket;
	
	@Value("${region}")
	private String region;
	
	private final AmazonS3 s3;
	
	public S3ServiceImpl(AmazonS3 s3) {
		this.s3=s3;
	}
	
	private File convertToFile(MultipartFile file) throws IOException {
		
		File convertedFile=new File(file.getOriginalFilename());
		FileOutputStream fos= new FileOutputStream(convertedFile);
		fos.write(file.getBytes());
		fos.close();
		
		return convertedFile;
	}
	
	@Override
	public String saveFile(MultipartFile file, int id) {
		String fileName= file.getOriginalFilename()+Timestamp.valueOf(LocalDateTime.now()).toString();
		try {
			File convFile=convertToFile(file);
			PutObjectResult putObjectResult=s3.putObject(bucket, "Post"+id+"/"+fileName.hashCode(), convFile);
			
			return fileName;
		}
		catch(IOException ex) {
			throw new PostNotSavedException("Multimedia couldn't be saved to S3 bucket");
		}
	}

	@Override
	public String deleteFile(String fileName) {
		s3.deleteObject(bucket, fileName);
		return "Object deleted";
	}

	@Override
	public List<String> listAllFiles() {
		ListObjectsV2Result listObjectsV2Result=s3.listObjectsV2(bucket);
		return listObjectsV2Result.getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
	}
	
	public String getUrl() {
		return "https://"+bucket+".s3."+region+".amazonaws.com/Post/";
	}

}
