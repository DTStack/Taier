package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * flink restful 返回数据解析
 * Date: 2017/11/23
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkRestParseUtil {

    /**
     * 数据样例
     {
     "taskmanagers": [
     {
     "id": "ac1e2d5668eb1e908e15e3d40f8b67d6",
     "path": "akka.tcp://flink@node01:52079/user/taskmanager",
     "dataPort": 37512,
     "timeSinceLastHeartbeat": 1508393749742,
     "slotsNumber": 4,
     "freeSlots": 4,
     "cpuCores": 4,
     "physicalMemory": 8254550016,
     "freeMemory": 1073741824,
     "managedMemory": 670946944
     }]}
     */
    public final static String SLOTS_INFO = "/taskmanagers";

    public final static String OVERVIEW_INFO = "/overview";

    /**
     * 数据样例
     {
     "root-exception": "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job. You can decrease the operator parallelism or increase the number of slots per TaskManager in the configuration. Task to schedule: < Attempt #0 (Source: mysqlreader (3/4)) @ (unassigned) - [SCHEDULED] > with groupID < bc764cd8ddf7a0cff126f51c16239658 > in sharing group < SlotSharingGroup [bc764cd8ddf7a0cff126f51c16239658, 20ba6b65f97481d5570070de90e4e791] >. Resources available to scheduler: Number of instances=1, total number of slots=10, available slots=0\n\tat org.apache.flink.runtime.jobmanager.scheduler.Scheduler.scheduleTask(Scheduler.java:262)\n\tat org.apache.flink.runtime.jobmanager.scheduler.Scheduler.allocateSlot(Scheduler.java:139)\n\tat org.apache.flink.runtime.executiongraph.Execution.allocateSlotForExecution(Execution.java:368)\n\tat org.apache.flink.runtime.executiongraph.ExecutionJobVertex.allocateResourcesForAll(ExecutionJobVertex.java:478)\n\tat org.apache.flink.runtime.executiongraph.ExecutionGraph.scheduleEager(ExecutionGraph.java:865)\n\tat org.apache.flink.runtime.executiongraph.ExecutionGraph.scheduleForExecution(ExecutionGraph.java:816)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply$mcV$sp(JobManager.scala:1425)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply(JobManager.scala:1372)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply(JobManager.scala:1372)\n\tat scala.concurrent.impl.Future$PromiseCompletingRunnable.liftedTree1$1(Future.scala:24)\n\tat scala.concurrent.impl.Future$PromiseCompletingRunnable.run(Future.scala:24)\n\tat akka.dispatch.TaskInvocation.run(AbstractDispatcher.scala:40)\n\tat akka.dispatch.ForkJoinExecutorConfigurator$AkkaForkJoinTask.exec(AbstractDispatcher.scala:397)\n\tat scala.concurrent.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)\n\tat scala.concurrent.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)\n\tat scala.concurrent.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)\n\tat scala.concurrent.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)\n",
     "all-exceptions": [],
     "truncated": false
     }
     */
    public final static String EXCEPTION_INFO = "/jobs/%s/exceptions";

    public final static String JOB_INFO = "/jobs/%s";

    public final static String JOB_ACCUMULATOR_INFO = "/jobs/%s/accumulators";

    public static String parseEngineLog(Map<String,String> jsonMap) throws IOException {

        String except = jsonMap.get("except");
        String accuInfo = jsonMap.get("accuInfo");
        String jobInfo = jsonMap.get("jobInfo");

        Map<String,Object> logMap = new HashMap<>();
        Map<String,Object> perfMap = new HashMap<>();
        Map<String,Object> increConfMap = new HashMap<>();

        if(StringUtils.isNotEmpty(except)) {
            Map<String,Object> exceptMap = PublicUtil.jsonStrToObject(except, Map.class);
            logMap.putAll(exceptMap);
        }

        if(StringUtils.isNotEmpty(accuInfo)) {
            Map<String,Object> accuInfoMap = PublicUtil.jsonStrToObject(accuInfo, Map.class);
            if(accuInfoMap != null) {
                List<Map<String,Object>> accuList = (List)accuInfoMap.get("user-task-accumulators");
                if(accuList != null) {
                    for(Map<String,Object> accu : accuList) {
                        String name = (String) accu.get("name");
                        String value = (String) accu.get("value");
                        if (name == null) {
                            continue;
                        }
                        if(name.equals("numRead")) {
                            perfMap.put("numRead", Long.valueOf(value));
                        } else if(name.equals("numWrite")) {
                            perfMap.put("numWrite", Long.valueOf(value));
                        } else if(name.equals("nErrors")) {
                            perfMap.put("numError", Long.valueOf(value));
                        } else if(name.equals("nullErrors")){
                            perfMap.put("nullErrors", Long.valueOf(value));
                        } else if(name.equals("duplicateErrors")){
                            perfMap.put("duplicateErrors", Long.valueOf(value));
                        } else if(name.equals("conversionErrors")){
                            perfMap.put("conversionErrors", Long.valueOf(value));
                        } else if(name.equals("otherErrors")){
                            perfMap.put("otherErrors", Long.valueOf(value));
                        } else if(name.equals("tableCol")){
                            String[] tableCol = value.split("-");
                            increConfMap.put("table",tableCol[0]);
                            increConfMap.put("increColumn",tableCol[1]);
                        } else if(name.equals("endLocation")){
                            increConfMap.put("endLocation",value);
                        } else if(name.equals("startLocation")){
                            increConfMap.put("startLocation",value);
                        }
                    }
                }
            }
        }

        if(StringUtils.isNotEmpty(accuInfo)) {
            Map<String, Object> jobInfoMap = PublicUtil.jsonStrToObject(jobInfo, Map.class);
            List<Map<String, Object>> vertices = (List) jobInfoMap.get("vertices");
            if (vertices != null && vertices.size() == 2) {
                for (Map<String, Object> vertice : vertices) {
                    String name = (String) vertice.get("name");

                    if (name == null) {
                        continue;
                    }

                    if (name.endsWith("reader")) {
                        Integer readDuration = (Integer) vertice.get("duration");
                        perfMap.put("durationRead", readDuration);
                        Map<String, Object> readerMetrics = (Map<String, Object>) vertice.get("metrics");
                        if (readerMetrics != null) {
                            Long byteRead = MathUtil.getLongVal(readerMetrics.get("write-bytes"));
                            perfMap.put("byteRead", byteRead);
                            try {
                                BigDecimal rd = new BigDecimal(readDuration);
                                BigDecimal br = new BigDecimal(byteRead);
                                if (rd.equals(BigDecimal.ZERO)) {
                                    perfMap.put("speedRead", 0);
                                } else {
                                    perfMap.put("speedRead", br.multiply(BigDecimal.valueOf(1000)).divideToIntegralValue(rd).intValue());
                                }
                            } catch (NumberFormatException ex) {
                                perfMap.put("speedRead", 0);
                            }
                        }

                    } else if (name.endsWith("writer")) {
                        Integer writeDuration = (Integer) vertice.get("duration");
                        perfMap.put("durationWrite", writeDuration);
                        Map<String, Object> writerMetrics = (Map<String, Object>) vertice.get("metrics");
                        if (writerMetrics != null) {
                            Long byteWrite = MathUtil.getLongVal(writerMetrics.get("read-bytes"));
                            perfMap.put("byteWrite", byteWrite);
                            try {
                                BigDecimal rd = new BigDecimal(writeDuration);
                                BigDecimal br = new BigDecimal(byteWrite);
                                if (rd.equals(BigDecimal.ZERO)) {
                                    perfMap.put("speedWrite", 0);
                                } else {
                                    BigDecimal numError = new BigDecimal(perfMap.containsKey("numError") ? perfMap.get("numError").toString() : "0");
                                    BigDecimal numRead = new BigDecimal(perfMap.containsKey("numRead") ? perfMap.get("numRead").toString() : "0");
                                    if(numError.equals(numRead)){
                                        perfMap.put("speedWrite", 0);
                                        perfMap.put("byteWrite", 0);
                                    } else {
                                        perfMap.put("speedWrite", br.multiply(BigDecimal.valueOf(1000)).divideToIntegralValue(rd).intValue());
                                    }
                                }
                            } catch (NumberFormatException ex) {
                                perfMap.put("speedWrite", 0);
                            }
                        }

                    }
                }
            }
        }

        StringBuffer sb = new StringBuffer();
        if(perfMap.containsKey("numRead")) {
            sb.append("读取记录数:\t" + perfMap.get("numRead") + "\n");
        }

        if(perfMap.containsKey("byteRead")) {
            sb.append("读取字节数:\t" + perfMap.get("byteRead") + "\n");
        }

        if(perfMap.containsKey("speedRead")) {
            sb.append("读取速率(B/s):\t" + perfMap.get("speedRead") + "\n");
        }

        if(perfMap.containsKey("numWrite")) {
            sb.append("写入记录数:\t" + perfMap.get("numWrite") + "\n");
        }

        if(perfMap.containsKey("byteWrite")) {
            sb.append("写入字节数:\t" + perfMap.get("byteWrite") + "\n");
        }

        if(perfMap.containsKey("speedWrite")) {
            sb.append("写入速率(B/s):\t" + perfMap.get("speedWrite") + "\n");
        }

        if(perfMap.containsKey("numError")) {
            sb.append("错误记录数:\t" + perfMap.get("numError") + "\n");
        }

        logMap.put("perf", sb.toString());
        logMap.put("countInfo",perfMap);
        logMap.put("increConf",increConfMap);
        return PublicUtil.objToString(logMap);
    }

}
