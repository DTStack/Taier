package com.dtstack.engine.dtscript.execution.learning;


import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import org.junit.Test;

import java.util.Properties;

public class LearningClientTest {

    @Test
    public void test1() throws Exception {
        Properties prop = new Properties();
        prop.setProperty("hadoop.conf.dir", "/Users/softfly/Desktop/hadoop");
        prop.setProperty("yarn.resourcemanager.recovery.enabled", "true");
        prop.setProperty("learning.python3.path", "/root/anaconda3/bin/python3");
        LearningClient learningClient = new LearningClient();
        learningClient.init(prop);
        RdosTaskStatus status = learningClient.getJobStatus(JobIdentifier.createInstance("application_1533106130429_0078", null, null));
        System.out.println(status);
    }
}
