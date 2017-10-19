package com.dtstack.rdos.engine.execution.base.util;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author sishu.yss
 *
 */
public class EngineRestParseUtil {
	
	public static class SparkRestParseUtil{
		
		public final static String ROOT = "/";
		
		public final static String EXCEPTION_IINFO = "";

        private Pattern pattern = Pattern.compile("\\s*(\\d+\\.\\d+)\\s*([G|K|M]*B)\\s*\\((\\d+\\.\\d+)\\s*([G|K|M]*B)\\s+Used\\)");

        private int MB2B = 1024 * 1024;

		/**
		 * message 为 html字符串
		 * @param message
		 * @return
		 */
		public static Map<String,Map<String, Object>> getAvailSlots(String message){

			Map<String, List<Map<String, String>>> appLogMap = new HashMap<>();
			List<Map<String, String>> list = new ArrayList<>();
			Document doc = Jsoup.parse(message);
			Elements appLogEles = doc.getElementsMatchingOwnText("Worker Id");
			Elements workChildEles = appLogEles.first().parent().parent().parent().child(1).children();
			for (int i = 0; i < workChildEles.size(); i++) {
				Element element = workChildEles.get(i);
				String workId = element.child(1).text();
				String addresss = element.child(2).text();
				String state = element.child(3).text();
				String cores = element.child(4).text();
                String memory = element.child(5).text();

                //TODO 转换数据
			}

			return null;
		}

		/**
		 * eg: 6.8 GB (0.0 B Used)
		 * @param capacityStr
		 * @return
		 */
		public Pair<Integer, Integer> parserMemory(String capacityStr){
			Matcher matcher = pattern.matcher(capacityStr);
			if(matcher.find() && matcher.groupCount() == 4){
				String total = matcher.group(1);
				String totalUnit = matcher.group(2);

				String used = matcher.group(3);
				String usedUnit = matcher.group(4);

				int totalResult = convert2MB(total, totalUnit);
				int usedResult = convert2MB(used, usedUnit);
				return Pair.of(totalResult, usedResult);
			}

			return null;
		}

		public int convert2MB(String capacityStr, String unit){
			if(unit.equalsIgnoreCase("GB")){
				return GB2MB(capacityStr);
			}else if(unit.equalsIgnoreCase("KB")){
				return KB2MB(capacityStr);
			}else if(unit.equalsIgnoreCase("B")){
				return B2MB(capacityStr);
			}else if(unit.equalsIgnoreCase("MB")){
				return Double.valueOf(capacityStr).intValue();
			}else{
				throw new RuntimeException("not support unit:" + unit);
			}
		}

		public int GB2MB(String gbStr){
			Double gbVal = Double.valueOf(gbStr);
			Double mbVal = gbVal * 1024;
			return mbVal.intValue();
		}

		public int KB2MB(String kbStr){
			Double kbVal = Double.valueOf(kbStr);
			Double mbVal = kbVal/1024;
			return mbVal.intValue();
		}

		public int B2MB(String bStr){
			Double bVal = Double.valueOf(bStr);
			Double mbVal = bVal/MB2B;
			return mbVal.intValue();
		}

		/**
		 * TODO
		 * @param message
		 * @return
		 */
		public static String getJobMessage(String message){
			return null;
		}
	}
	
	public static class FlinkRestParseUtil{

		/**
		 * 数据格式
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
		 },
		 {
		 "id": "72e8c19db43ef4117e12e87288ed1337",
		 "path": "akka.tcp://flink@node03:43820/user/taskmanager",
		 "dataPort": 41888,
		 "timeSinceLastHeartbeat": 1508393745446,
		 "slotsNumber": 4,
		 "freeSlots": 4,
		 "cpuCores": 4,
		 "physicalMemory": 8254550016,
		 "freeMemory": 1073741824,
		 "managedMemory": 670863424
		 }
		 ]
		 }
		 */
		public final static String SLOTS_INFO = "/taskmanagers";

		/**
		 * 数据格式
		 {
		 "root-exception": "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job. You can decrease the operator parallelism or increase the number of slots per TaskManager in the configuration. Task to schedule: < Attempt #0 (Source: mysqlreader (3/4)) @ (unassigned) - [SCHEDULED] > with groupID < bc764cd8ddf7a0cff126f51c16239658 > in sharing group < SlotSharingGroup [bc764cd8ddf7a0cff126f51c16239658, 20ba6b65f97481d5570070de90e4e791] >. Resources available to scheduler: Number of instances=1, total number of slots=10, available slots=0\n\tat org.apache.flink.runtime.jobmanager.scheduler.Scheduler.scheduleTask(Scheduler.java:262)\n\tat org.apache.flink.runtime.jobmanager.scheduler.Scheduler.allocateSlot(Scheduler.java:139)\n\tat org.apache.flink.runtime.executiongraph.Execution.allocateSlotForExecution(Execution.java:368)\n\tat org.apache.flink.runtime.executiongraph.ExecutionJobVertex.allocateResourcesForAll(ExecutionJobVertex.java:478)\n\tat org.apache.flink.runtime.executiongraph.ExecutionGraph.scheduleEager(ExecutionGraph.java:865)\n\tat org.apache.flink.runtime.executiongraph.ExecutionGraph.scheduleForExecution(ExecutionGraph.java:816)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply$mcV$sp(JobManager.scala:1425)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply(JobManager.scala:1372)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply(JobManager.scala:1372)\n\tat scala.concurrent.impl.Future$PromiseCompletingRunnable.liftedTree1$1(Future.scala:24)\n\tat scala.concurrent.impl.Future$PromiseCompletingRunnable.run(Future.scala:24)\n\tat akka.dispatch.TaskInvocation.run(AbstractDispatcher.scala:40)\n\tat akka.dispatch.ForkJoinExecutorConfigurator$AkkaForkJoinTask.exec(AbstractDispatcher.scala:397)\n\tat scala.concurrent.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)\n\tat scala.concurrent.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)\n\tat scala.concurrent.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)\n\tat scala.concurrent.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)\n",
		 "all-exceptions": [],
		 "truncated": false
		 }
		 */
		public final static String EXCEPTION_INFO = "/jobs/%s/exceptions";
		
		
		public final static String NORESOURCEAVAIABLEEXCEPYION = "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job";

		private final static ObjectMapper objMapper = new ObjectMapper();

		private static Logger logger = LoggerFactory.getLogger(FlinkRestParseUtil.class);

		/**
		 * TODO
		 * @param message
		 * @return
		 */
		public static Map<String, Map<String,Object>> getAvailSlots(String message){

			if(Strings.isNullOrEmpty(message)){
				return null;
			}

			Map<String, Map<String,Object>> availSlots = Maps.newHashMap();

			try{
				Map<String, Object> taskManagerInfo = objMapper.readValue(message, Map.class);
				if(taskManagerInfo.containsKey("taskmanagers")){
					List<Map<String, Object>> taskManagerList = (List<Map<String, Object>>) taskManagerInfo.get("taskmanagers");
					for(Map<String, Object> tmp : taskManagerList){
						availSlots.put((String)tmp.get("id"), tmp);
					}
				}
			}catch (Exception e){
				logger.error("", e);
				return null;
			}
			
			return null;
		}

		/**
		 * TODO
		 * @param message
		 * @return
		 */
		public static String getJobMessage(String message){
			return null;
		}
		
	}
}
