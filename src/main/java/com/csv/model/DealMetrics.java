package com.csv.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Deal_Metrics")
public class DealMetrics {

	public DealMetrics() {

	}

	public DealMetrics(String orderingCurrencyCode, long dealCount) {
		super();
		this.orderingCurrencyCode = orderingCurrencyCode;
		this.dealCount = dealCount;
	}

	@Id
	@Column(name = "ordering_currency_code")
	private String orderingCurrencyCode;

	@Column(name = "deal_count")
	private long dealCount;

	public String getOrderingCurrencyCode() {
		return orderingCurrencyCode;
	}

	public void setOrderingCurrencyCode(String orderingCurrencyCode) {
		this.orderingCurrencyCode = orderingCurrencyCode;
	}

	public long getDealCount() {
		return dealCount;
	}

	public void setDealCount(long dealCount) {
		this.dealCount = dealCount;
	}

}
