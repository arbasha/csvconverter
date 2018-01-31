package com.csv.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.csv.model.DealMetrics;
import com.csv.repository.DealMetricsRepository;

@Service("dealMetricsService")
public class DealMetricsServiceImpl implements DealMetricsService {

	@Autowired
	private DealMetricsRepository metricRepo;

	@Override
	public DealMetrics saveDealMetrics(DealMetrics dealMetrics) {
		return metricRepo.save(dealMetrics);

	}

	@Override
	public DealMetrics findByOrderingCurrencyCode(String orderingCurrencyCode) {
		return metricRepo.findByOrderingCurrencyCode(orderingCurrencyCode);

	}

	@Override
	public List<DealMetrics> findAll(List<String> orderingCurrencyCodes) {
		return metricRepo.findAll(orderingCurrencyCodes);
	}

	@Override
	public List<DealMetrics> saveDealMetrics(List<DealMetrics> dealMetrics) {
		return metricRepo.save(dealMetrics);
	}

}
