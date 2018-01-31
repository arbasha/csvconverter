package com.csv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.csv.model.DealMetrics;

public interface DealMetricsRepository extends JpaRepository<DealMetrics, String> {
	DealMetrics findByOrderingCurrencyCode(String orderingCurrencyCode);

	List<DealMetrics> findByOrderingCurrencyCode(List<String> orderingCurrencyCode);
}
