package com.csv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.csv.model.FileInfo;

public interface FileRepository extends JpaRepository<FileInfo, Long> {
	FileInfo findByFileName(String fileName);
}
