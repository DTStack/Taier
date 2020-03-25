package com.dtstack.engine.learning;


import com.dtstack.engine.common.JobIdentifier;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LearningClientTest {

    private static final Logger logger = LoggerFactory.getLogger(LearningClientTest.class);

    @Test
    public void testJobIdentifier() throws Exception {
        JobIdentifier jobIdentifier = JobIdentifier.createInstance("application_1533106130429_0078", null, null);
        logger.info(jobIdentifier.toString());
    }
}
