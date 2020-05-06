package com.dtstack.engine.hadoop;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MapReduceTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(MapReduceTemplate.class);

    private Job job;

    public static final String JAR = "jar";
    public static final String MAPPER = "mapper";
    public static final String REDUCER = "reducer";
    public static final String INPUT_PATH = "inputPath";
    public static final String OUTPUT_PATH = "outputPath";
    public static final String QUEUE = "queue";

    public MapReduceTemplate(String jobName, Configuration conf, Map<String,String> params) throws Exception {
        job = Job.getInstance(conf, jobName);
        String jar = params.get(JAR);
        if(StringUtils.isNotBlank(jar)) {
            job.setJar(jar);
        }

        String mapper = params.get(MAPPER);
        job.getConfiguration().set(MRJobConfig.MAP_CLASS_ATTR, mapper);

        String reducer = params.get(REDUCER);
        job.getConfiguration().set(MRJobConfig.REDUCE_CLASS_ATTR, reducer);

        // set queue name
        if (params.containsKey(QUEUE)){
            job.getConfiguration().set(MRJobConfig.QUEUE_NAME,params.get(QUEUE));
        }

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        String inputPath = params.get(INPUT_PATH);
        Preconditions.checkNotNull(inputPath, "inputpath can't be null");
        FileInputFormat.addInputPath(job, new Path(inputPath));
        String outputPath = params.get(OUTPUT_PATH);
        Preconditions.checkNotNull(outputPath, "outputpath can't be null");
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        for (Map.Entry<String,String> paramEntry : params.entrySet()) {
            if (!paramEntry.getKey().contains(".")) {
                continue;
            }
            job.getConfiguration().set(paramEntry.getKey(), paramEntry.getValue());
            LOG.info("params {}:{}", paramEntry.getKey(), paramEntry.getValue());
        }
    }



    public String getJobId() {
        return job.getJobID().toString();
    }

    public void run() throws Exception {
        job.submit();
    }

}
