package com.csv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.csv.model.FileInfo;
import com.csv.repository.FileRepository;

@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {

	@Autowired
	private FileRepository fileRepo;
	
	@Override
	public FileInfo findByFileName(String fileName) {
		return fileRepo.findByFileName(fileName);
	}

	@Override
	public FileInfo saveFileInfo(FileInfo fileInfo) {
		return fileRepo.save(fileInfo);

	}

}
