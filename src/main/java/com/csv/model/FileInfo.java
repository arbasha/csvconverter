package com.csv.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "File_Info")
public class FileInfo {

	public FileInfo() {
	}

	public FileInfo(long fileId, String fileName, long flags) {
		super();
		this.fileId = fileId;
		this.fileName = fileName;
		this.flags = flags;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "file_id")
	private long fileId;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "imported_deals_count")
	private long importedDealCount;

	@Column(name = "invalid_deals_count")
	private long invalidDealCount;

	@Column(name = "flags")
	private long flags;

	@OneToMany(mappedBy = "file", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Deal> deals;

	public List<Deal> getDeals() {
		return deals;
	}

	public void setDeals(List<Deal> deals) {
		this.deals = deals;
	}

	@Transient
	private String status;

	@Transient
	private String processingTime;

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFlags() {
		return flags;
	}

	public void setFlags(long flags) {
		this.flags = flags;
	}

	public long getImportedDealCount() {
		return importedDealCount;
	}

	public void setImportedDealCount(long importedDealCount) {
		this.importedDealCount = importedDealCount;
	}

	public long getInvalidDealCount() {
		return invalidDealCount;
	}

	public void setInvalidDealCount(long invalidDealCount) {
		this.invalidDealCount = invalidDealCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(String processingTime) {
		this.processingTime = processingTime;
	}

}
