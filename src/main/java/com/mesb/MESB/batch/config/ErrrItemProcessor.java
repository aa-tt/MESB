package com.mesb.MESB.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.mesb.MESB.bussiness.model.Errr;

public class ErrrItemProcessor implements ItemProcessor<Errr, Errr> {
	
	private static final Logger log = LoggerFactory.getLogger(ErrrItemProcessor.class);

    @Override
    public Errr process(final Errr errr) throws Exception {
    	final Integer errId = errr.getErrId();
        final String errMsg = errr.getErrMsg().toUpperCase();
        final String errUrl = errr.getErrUrl().toUpperCase();

        final Errr transformedErrr = new Errr(errId, errMsg, errUrl);

        log.info("Converting (" + errr + ") into (" + transformedErrr + ")");

        return transformedErrr;
    }

}
