package com.csv.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.csv.converter.util.Utils;
import com.csv.model.Deal;

@Repository("jdbcTemplateBulkUpdater")
public class JdbcTemplateBulkUpdater {

	private final static Logger logger = LoggerFactory.getLogger(JdbcTemplateBulkUpdater.class);

	@Autowired
	private JdbcTemplate template;

	@Transactional
	public void bulkPersist(final List<? extends Deal> entities) {

		String tableName = entities.get(0).getClass().getAnnotationsByType(Table.class)[0].name();

		logger.info("Starting Bulk Persist on table:" + tableName);

		template.batchUpdate(
				"insert into " + tableName
						+ " (id, deal_id, deal_amt, deal_time, ordering_currency_code, receiving_currency_code, file_id) values (?, ?, ?, ?, ?, ?, ?)",
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setString(1, entities.get(i).getId());
						ps.setLong(2, ObjectUtils.defaultIfNull(entities.get(i).getDealId(), 0l));
						ps.setDouble(3, ObjectUtils.defaultIfNull(entities.get(i).getDealAmount(), 0.0d));
						ps.setTimestamp(4, Utils.convertDateToSqlTime(entities.get(i).getDealTime()));
						ps.setString(5, entities.get(i).getOrderingCurrencyCode());
						ps.setString(6, entities.get(i).getReceivingCurrencyCode());
						ps.setLong(7, entities.get(i).getFile().getFileId());

					}

					@Override
					public int getBatchSize() {
						return entities.size();
					}
				});
		logger.info("Completed Bulk Persist on table:" + tableName);
	}
}
