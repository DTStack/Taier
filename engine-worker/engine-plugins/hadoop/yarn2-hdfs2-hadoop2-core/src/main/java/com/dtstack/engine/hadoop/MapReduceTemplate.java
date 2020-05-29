package com.dtstack.engine.hadoop;

import com.dtstack.engine.common.JobParam;
import com.dtstack.engine.common.callback.ClassLoaderCallBackMethod;
import com.dtstack.engine.hadoop.program.PackagedProgram;
import com.dtstack.engine.worker.enums.ClassLoaderType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapReduceTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(MapReduceTemplate.class);

    private String jobId;

    private PackagedProgram program;
    private Configuration conf;

    public MapReduceTemplate(Configuration conf, JobParam jobParam) throws Exception {
        this.conf = conf;
        initNew(jobParam);
    }

    private void initNew(JobParam jobParam) throws Exception {
        List<String> args = new ArrayList<>();
        if (StringUtils.isNotBlank(jobParam.getClassArgs())) {
            String[] argstmp = StringUtils.split(jobParam.getClassArgs(), " ");
            args.addAll(Arrays.asList(argstmp));
        }

        File jarFile = new File(jobParam.getJarPath());

        String[] array = (String[]) args.toArray(new String[args.size()]);
        this.program = new PackagedProgram(jarFile, new ArrayList<>(), ClassLoaderType.CHILD_FIRST_CACHE, jobParam.getMainClass(), array);
    }

    public String getJobId() {
        return jobId;
    }

    public void run() throws Exception {
        jobId = ClassLoaderCallBackMethod.callbackAndReset(() -> {
            return program.invokeInteractiveModeForExecution(conf);
        }, program.getUserCodeClassLoader(), true);
    }

}
