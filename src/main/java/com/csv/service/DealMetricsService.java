package com.csv.service;

import java.util.List;

import com.csv.model.DealMetrics;

public interface DealMetricsService {

	public DealMetrics saveDealMetrics(DealMetrics dealMetrics);

	public List<DealMetrics> saveDealMetrics(List<DealMetrics> dealMetrics);

	public DealMetrics findByOrderingCurrencyCode(String orderingCurrencyCode);

	public List<DealMetrics> findAll(List<String> orderingCurrencyCode);
}
