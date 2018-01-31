package com.csv.service;

import java.util.List;

import com.csv.model.Deal;
import com.csv.model.InvalidDeal;

public interface DealService {

	public void saveDeal(Deal deal);
	public void saveDeals(List<? extends Deal> deals);
	public void saveInvalidDeals(List<InvalidDeal> deals);

}
