package com.csv.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.csv.model.FileInfo;
import com.csv.repository.FileRepository;

public class FileInfoServiceImplTest {

	@InjectMocks
	private FileInfoServiceImpl fileInfoService;

	@Mock
	private FileRepository fileRepo;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testFindByFileName() {
		fileInfoService.findByFileName("test");
		Mockito.verify(fileRepo).findByFileName("test");
	}

	@Test
	public void testSave() {
		fileInfoService.saveFileInfo(Mockito.mock(FileInfo.class));
		Mockito.verify(fileRepo).save(Mockito.any(FileInfo.class));
	}

}
