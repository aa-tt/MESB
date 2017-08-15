package com.mesb.MESB.batch.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.mesb.MESB.bussiness.model.Errr;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			List<Errr> results = jdbcTemplate.query("SELECT err_id, err_msg, err_url FROM errr", new RowMapper<Errr>() {
				@Override
				public Errr mapRow(ResultSet rs, int row) throws SQLException {
					return new Errr(rs.getInt(1), rs.getString(2), rs.getString(3));
				}
			});

			for (Errr err : results) {
				log.info("Found <" + err + "> in the database.");
			}

		}
	}
}
