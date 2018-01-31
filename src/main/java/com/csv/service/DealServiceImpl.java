package com.csv.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.csv.model.Deal;
import com.csv.model.InvalidDeal;
import com.csv.repository.DealRepository;
import com.csv.repository.InvalidDealRepository;
import com.csv.repository.JdbcTemplateBulkUpdater;

@Service("dealService")
public class DealServiceImpl implements DealService {

	@Autowired
	private DealRepository dealRepository;

	@Autowired
	private InvalidDealRepository inValidDealRepository;

	@Autowired
	private JdbcTemplateBulkUpdater bulkOperation;

	@Override
	public void saveDeal(Deal deal) {
		dealRepository.save(deal);
	}

	// Retry three time if any exception, just to rollback due to db down cases
	@Override
	@Retryable(value = { Exception.class }, maxAttempts = 3)
	public void saveDeals(List<? extends Deal> deals) {
		bulkOperation.bulkPersist(deals);

	}

	@Override
	public void saveInvalidDeals(List<InvalidDeal> deals) {
		inValidDealRepository.save(deals);

	}
}
