package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.flink120.util.FlinkUtil;
import com.google.common.collect.Queues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.*;

/**
 * 任务提交执行容器
 * 单独起线程执行
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class JobSubmitExecutor{

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitExecutor.class);

    private BlockingQueue<JobClient> submitQueue = Queues.newLinkedBlockingQueue();

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private JobSubmitProcessor processor = new JobSubmitProcessor();

    private IClient clusterClient;

    private boolean hasInit = false;

    private static JobSubmitExecutor singleton = new JobSubmitExecutor();

    private JobSubmitExecutor(){

    }

    public void init(ClientType type, Properties clusterProp){
        clusterClient = ClientFactory.getClient(type);
        clusterClient.init(clusterProp);

        String jarTmpPath = clusterProp.getProperty("jarFileTmpPath", "/tmp/flinkjar");
        FlinkUtil.tmp_file_path = jarTmpPath;
        hasInit = true;
    }

    public static JobSubmitExecutor getInstance(){
        return singleton;
    }

    public void submitJob(JobClient jobClient){
        submitQueue.add(jobClient);
    }

    public void start(){

        if(!hasInit){
            logger.error("need to init JobSubmitExecutor first. please check your program first!");
            System.exit(-1);
        }

        if(processor.isRunnable()){
            logger.error("processor is already started");
            return;
        }

        processor.setRunnable(true);
        executor.submit(processor);
    }

    public void shutdown(){
        processor.setRunnable(false);
        //FIXME 是否需要做同步等processor真正完成
        executor.shutdownNow();
    }

    public int getCurrJobQueue(){
        return submitQueue.size();
    }

    class JobSubmitProcessor implements Callable{

        private boolean runnable;

        @Override
        public Object call() throws Exception {
            while(runnable){
                JobClient jobClient = submitQueue.poll(2000, TimeUnit.MILLISECONDS);
                if(jobClient != null){
                    try{
                        JobResult jobResult = clusterClient.submitJob(jobClient);
                        logger.info("submit job result is:{}.", jobResult);
                    }catch (Exception e){//捕获未处理异常,防止跳出执行线程
                        logger.error("get unexpect exception", e);
                        e.printStackTrace();
                    }
                }

                //TODO maybe have other deal fuc
            }

            return null;
        }

        public boolean isRunnable() {
            return runnable;
        }

        public void setRunnable(boolean runnable) {
            this.runnable = runnable;
        }
    }

}
