package com.merry.meal.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.merry.meal.services.FileService;

@Service
public class FileServiceImpl implements FileService {

	public final String UPLOAD_DIR = new ClassPathResource("static/images/user-contents").getFile().getAbsolutePath();

	public FileServiceImpl() throws IOException {

	}

	// file upload method
	@Override
	public String uploadImage(MultipartFile file) throws IOException {

		// file name
		String name = file.getOriginalFilename();

		// generating random string
		String randomID = UUID.randomUUID().toString();
		String fileNameNew = randomID.concat(name.substring(name.lastIndexOf(".")));

		// full path
		String filePath = UPLOAD_DIR + File.separator + fileNameNew;

		// file copy
		Files.copy(file.getInputStream(), Paths.get(filePath));

		return fileNameNew;
	}

	// file serving method
	@Override
	public InputStream getResource(String fileName) throws FileNotFoundException {
		String fullPath = UPLOAD_DIR + File.separator + fileName;
		InputStream is = new FileInputStream(fullPath);
		return is;
	}

	// file delete method
	@Override
	public void deleteFile(String filename) throws IOException {

		File deleteFile = new ClassPathResource("static/images/user-contents").getFile();

		File deletingFile = new File(deleteFile, filename);
		deletingFile.delete();

	}

}
