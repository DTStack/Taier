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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MapReduceTemplate {
    private Configuration conf;
    private Job job;

    public static final String JAR = "jar";
    public static final String MAPPER = "mapper";
    public static final String REDUCER = "reducer";
    public static final String INPUT_PATH = "inputPath";
    public static final String OUTPUT_PATH = "outputPath";
    public static final String QUEUE = "queue";

    public MapReduceTemplate(String jobName, Configuration conf, Map<String,String> params) throws Exception {
        job = Job.getInstance(conf, jobName);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String jar = params.get(JAR);
        if(StringUtils.isNotBlank(jar)) {
            job.setJar(jar);
//            cl = new URLClassLoader(new URL[] {new URL("file://" + jar)});
        }

        String mapper = params.get(MAPPER);
//        cl.loadClass(mapper);
        job.getConfiguration().set(MRJobConfig.MAP_CLASS_ATTR, mapper);

        String reducer = params.get(REDUCER);
//        cl.loadClass(reducer);
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

    }



    public String getJobId() {
        return job.getJobID().toString();
    }

    public void run() throws Exception {
        job.submit();
    }

    public static void main(String[] args) throws Exception {
        String jobName = "my_job_name";
        Configuration conf = new Configuration();
        conf.clear();
        conf.set("mapreduce.framework.name", "yarn");
        File dir = new File("D:\\hadoop\\etc\\hadoop");
        File[] files = dir.listFiles();
        for(File file : files) {
            String path = "path: " + file.getPath();
            System.out.println("add resource: " + path);
            if(path.endsWith("xml")) {
                conf.addResource(file.toURI().toURL());
            }
        }

        conf.set("yarn.scheduler.maximum-allocation-mb", "1024");
        conf.set("yarn.nodemanager.resource.memory-mb", "1024");
        conf.set("mapreduce.map.memory.mb","1024");
        conf.set("mapreduce.reduce.memory.mb","1024");
        conf.setBoolean("mapreduce.app-submission.cross-platform", true);

        Map<String,String> params = new HashMap<>();
        params.put(JAR, "D:\\Users/mymr-1.0-SNAPSHOT.jar");
        params.put(MAPPER, "bigdata.TokenizerMapper");
        params.put(REDUCER, "bigdata.IntSumReducer");
        params.put(INPUT_PATH, "/hyf/xx.txt");
        params.put(OUTPUT_PATH, "/hyf/xxout.txt");

        System.setProperty("HADOOP_USER_NAME", "admin");

        MapReduceTemplate job = new MapReduceTemplate(jobName, conf, params);
        job.run();
        System.out.println(job.getJobId());
    }
}
