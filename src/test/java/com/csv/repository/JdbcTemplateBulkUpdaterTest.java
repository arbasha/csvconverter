package com.csv.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.csv.model.Deal;
import com.csv.model.FileInfo;
import com.csv.model.ValidDeal;

public class JdbcTemplateBulkUpdaterTest {

	@InjectMocks
	private JdbcTemplateBulkUpdater jdbcTemplateBulkUpdater;

	@Mock
	private JdbcTemplate template;

	@Mock
	private FileInfo fileInfo;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() {
		List<Deal> entities = new ArrayList<>();
		entities.add(new ValidDeal("1", 100l, "USD", "AUD", 100d, new Date(System.currentTimeMillis()), fileInfo));
		jdbcTemplateBulkUpdater.bulkPersist(entities);
		Mockito.verify(template).batchUpdate(Mockito.anyString(), Mockito.any(BatchPreparedStatementSetter.class));
	}

}
