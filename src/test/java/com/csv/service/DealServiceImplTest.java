package com.csv.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.csv.model.InvalidDeal;
import com.csv.model.ValidDeal;
import com.csv.repository.DealRepository;
import com.csv.repository.InvalidDealRepository;
import com.csv.repository.JdbcTemplateBulkUpdater;

public class DealServiceImplTest {

	@InjectMocks
	private DealServiceImpl dealService;

	@Mock
	private DealRepository dealRepository;

	@Mock
	private InvalidDealRepository inValidDealRepository;

	@Mock
	private JdbcTemplateBulkUpdater bulkOperation;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSaveDeal() {
		dealService.saveDeal(Mockito.mock(ValidDeal.class));
		Mockito.verify(dealRepository).save(Mockito.any(ValidDeal.class));
	}

	@Test
	public void testSaveDeals() {
		List<ValidDeal> deals = Arrays.asList(Mockito.mock(ValidDeal.class), Mockito.mock(ValidDeal.class));
		dealService.saveDeals(deals);
		Mockito.verify(bulkOperation).bulkPersist(deals);
	}

	@Test
	public void testSaveInValidDeals() {
		List<InvalidDeal> deals = Arrays.asList(Mockito.mock(InvalidDeal.class), Mockito.mock(InvalidDeal.class));
		dealService.saveInvalidDeals(deals);
		Mockito.verify(inValidDealRepository).save(deals);
	}

}
