package com.csv.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.csv.model.DealMetrics;
import com.csv.repository.DealMetricsRepository;

public class DealMetricsServiceImplTest {

	@InjectMocks
	private DealMetricsServiceImpl dealMetrics;

	@Mock
	private DealMetricsRepository metricRepo;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSaveDealMetric() {
		dealMetrics.saveDealMetrics(Mockito.mock(DealMetrics.class));
		Mockito.verify(metricRepo).save(Mockito.any(DealMetrics.class));
	}

	@Test
	public void testFindByOrderingCurrencyCode() {
		dealMetrics.findByOrderingCurrencyCode("USD");
		Mockito.verify(metricRepo).findByOrderingCurrencyCode("USD");
	}

	@Test
	public void findAll() {
		List<String> codes = Arrays.asList("USD", "INR");
		dealMetrics.findAll(codes);
		Mockito.verify(metricRepo).findAll(codes);
	}

	@Test
	public void testSaveDealMetrics() {
		List<DealMetrics> metrics = Arrays.asList(Mockito.mock(DealMetrics.class), Mockito.mock(DealMetrics.class));
		dealMetrics.saveDealMetrics(metrics);
		Mockito.verify(metricRepo).save(metrics);
	}

}
