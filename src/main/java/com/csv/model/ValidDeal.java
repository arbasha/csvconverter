package com.csv.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Valid_Deals")
public class ValidDeal extends Deal {


	public ValidDeal() {
	}

	public ValidDeal(String id, Long dealId, String orderingCurrencyCode, String receivingCurrencyCode, Double dealAmount,
			Date dealTime, FileInfo file) {
		super(id, dealId, orderingCurrencyCode, receivingCurrencyCode, dealAmount, dealTime, file);
	}
}
