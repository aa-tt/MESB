package com.mesb.MESB.batch.config;
/**
 * reader and writers documentation -->
 * https://docs.spring.io/spring-batch/trunk/reference/html/readersAndWriters.html
 */
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;

import com.mesb.MESB.bussiness.model.Errr;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Errr> reader1() {
        FlatFileItemReader<Errr> reader = new FlatFileItemReader<Errr>();
        reader.setResource(new ClassPathResource("err-data.csv"));
        reader.setLineMapper(new DefaultLineMapper<Errr>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "errId", "errMsg", "errUrl" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Errr>() {{
                setTargetType(Errr.class);
            }});
        }});
        return reader;
    }
    
    @Bean
    public JdbcCursorItemReader<Errr> reader2() throws UnexpectedInputException, ParseException, Exception {
    	JdbcCursorItemReader<Errr> reader = new JdbcCursorItemReader<Errr>();
    	reader.setDataSource(dataSource);
    	reader.setSql("SELECT err_id, err_msg, err_url FROM errr");
    	reader.setRowMapper(new RowMapper<Errr>() {
			
			@Override
			public Errr mapRow(ResultSet arg0, int arg1) throws SQLException {
				// TODO Auto-generated method stub
				Errr errr = new Errr();
				errr.setErrId(arg0.getInt(1));
				errr.setErrMsg(arg0.getString(2));
				errr.setErrUrl(arg0.getString(3));
				return errr;
			}
		});
    	/*int counter = 0;
    	ExecutionContext executionContext = new ExecutionContext();
    	reader.open(executionContext);
    	Object errRecord = new Object();
    	while(errRecord != null){
    		errRecord = reader.read();
    	    counter++;
    	}
    	reader.close();*/
    	return reader;
    }

    @Bean
    public ErrrItemProcessor processor() {
        return new ErrrItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Errr> writer1() {
        JdbcBatchItemWriter<Errr> writer = new JdbcBatchItemWriter<Errr>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Errr>());
        writer.setSql("INSERT INTO errr (err_id, err_msg, err_url) VALUES (:errId, :errMsg, :errUrl)");
        writer.setDataSource(dataSource);
        return writer;
    }
    
    @Bean
    public FlatFileItemWriter<Errr> writer2() {
    	FlatFileItemWriter<Errr> writer = new FlatFileItemWriter<Errr>();
    	writer.setResource(new ClassPathResource("err-output.txt"));
    	writer.setLineAggregator(new DelimitedLineAggregator<Errr>() {{
    		setDelimiter(",");
    		setFieldExtractor(new BeanWrapperFieldExtractor<Errr>() {{
    			setNames(new String[] {"errId", "errMsg", "errUrl"});
    		}});
    	}});
    	return writer;
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener) throws UnexpectedInputException, ParseException, Exception {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .next(step2())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Errr, Errr> chunk(10)
                .reader(reader1())
                .processor(processor())
                .writer(writer1())
                .build();
    }
    
    @Bean
    public Step step2() throws UnexpectedInputException, ParseException, Exception {
    	return stepBuilderFactory.get("step2")
    			.<Errr, Errr> chunk(10)
    			.reader(reader2())
    			.processor(processor())
    			.writer(writer2())
    			.build();
    }
    // end::jobstep[]
}