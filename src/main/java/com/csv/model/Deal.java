package com.csv.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.csv.validator.Convert;
import com.csv.validator.DateConverter;
import com.csv.validator.DoubleConverter;
import com.csv.validator.ISOConverter;
import com.csv.validator.LongConverter;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Deal {

	public Deal() {
		this.id = UUID.randomUUID().toString();
	}

	public Deal(String id, Long dealId, String orderingCurrencyCode, String receivingCurrencyCode, Double dealAmount,
			Date dealTime, FileInfo file) {
		super();
		this.id = id;
		this.dealId = dealId;
		this.orderingCurrencyCode = orderingCurrencyCode;
		this.receivingCurrencyCode = receivingCurrencyCode;
		this.dealAmount = dealAmount;
		this.dealTime = dealTime;
		this.file = file;
	}

	@Id
	private String id;

	@Column(name = "deal_id")
	@Convert(csvColumnName = "Deal Unique Id", validateAndConvertClass = LongConverter.class)
	private Long dealId;

	@Column(name = "ordering_currency_code")
	@Convert(csvColumnName = "From Currency ISO Code", validateAndConvertClass = ISOConverter.class)
	private String orderingCurrencyCode;

	@Column(name = "receiving_currency_code")
	@Convert(csvColumnName = "To Currency ISO Code", validateAndConvertClass = ISOConverter.class)
	private String receivingCurrencyCode;

	@Column(name = "deal_amt")
	@Convert(csvColumnName = "Deal Amount in ordering currency", validateAndConvertClass = DoubleConverter.class)
	private Double dealAmount;

	@Temporal(TemporalType.TIMESTAMP)
	@Convert(csvColumnName = "Deal timestamp", validateAndConvertClass = DateConverter.class)
	private Date dealTime;

	@ManyToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name = "fileId")
	private FileInfo file;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getDealId() {
		return dealId;
	}

	public void setDealId(Long dealId) {
		this.dealId = dealId;
	}

	public String getOrderingCurrencyCode() {
		return orderingCurrencyCode;
	}

	public void setOrderingCurrencyCode(String orderingCurrencyCode) {
		this.orderingCurrencyCode = orderingCurrencyCode;
	}

	public String getReceivingCurrencyCode() {
		return receivingCurrencyCode;
	}

	public void setReceivingCurrencyCode(String receivingCurrencyCode) {
		this.receivingCurrencyCode = receivingCurrencyCode;
	}

	public Double getDealAmount() {
		return dealAmount;
	}

	public void setDealAmount(Double dealAmount) {
		this.dealAmount = dealAmount;
	}

	public FileInfo getFile() {
		return file;
	}

	public void setFile(FileInfo file) {
		this.file = file;
	}

	public Date getDealTime() {
		return dealTime;
	}

	public void setDealTime(Date dealTime) {
		this.dealTime = dealTime;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Deal)) {
			return false;
		}
		Deal other = (Deal) obj;
		return getId().equals(other.getId());
	}

}
