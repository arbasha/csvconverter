package com.csv.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Invalid_Deals")
public class InvalidDeal extends Deal {

	public InvalidDeal() {
	}

	public InvalidDeal(String id, Long dealId, String orderingCurrencyCode, String receivingCurrencyCode,
			Double dealAmount, Date dealTime, FileInfo file) {
		super(id, dealId, orderingCurrencyCode, receivingCurrencyCode, dealAmount, dealTime, file);
	}

}
