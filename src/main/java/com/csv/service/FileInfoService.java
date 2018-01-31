package com.csv.service;

import com.csv.model.FileInfo;

public interface FileInfoService {

	public FileInfo findByFileName(String fileName);

	public FileInfo saveFileInfo(FileInfo fileInfo);

}
