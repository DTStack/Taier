package com.dtstack.rdos.engine.execution.base.util;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
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

	    private static final Logger logger = LoggerFactory.getLogger(SparkRestParseUtil.class);
		
		public final static String ROOT = "/";

		public static final String DRIVER_LOGURL_FORMAT = "%s/logPage/?driverId=%s&logType=stderr";

		public static final String APP_LOGURL_FORMAT = "/app/?appId=%s";

		public final static String ADDRESS_KEY = "address";

		public final static String CORE_TOTAL_KEY = "cores.total";

		public final static String CORE_USED_KEY = "cores.used";

		public final static String CORE_FREE_KEY = "cores.free";

		public final static String MEMORY_TOTAL_KEY = "memory.total";

		public final static String MEMORY_USED_KEY = "memory.used";

		public final static String MEMORY_FREE_KEY = "memory.free";

		public final static String SPARK_ENGINE_DOWN = "Current state is not alive: STANDBY";

        private static Pattern memPattern = Pattern.compile("\\s*(\\d+\\.?\\d*)\\s*([G|K|M]?B)\\s*\\(([\\-]?\\d+\\.?\\d*)\\s*([G|K|M]?B)\\s+Used\\)");

        private static Pattern corePattern = Pattern.compile("\\s*(\\d+)\\s*\\(([\\-]?\\d+)\\s+Used\\)");

        private static int MB2B = 1024 * 1024;

		/**
		 * message 为 html字符串
		 * @param message
		 * @return
		 */
		public static Map<String, Map<String, Object>> getAvailSlots(String message){

			Map<String, Map<String, Object>> availSlotMap = new HashMap<>();
			Document doc = Jsoup.parse(message);
			Elements rootEles = doc.getElementsMatchingOwnText("Worker Id");
			Elements workChildEles = rootEles.first().parent().parent().parent().child(1).children();
			for (int i = 0; i < workChildEles.size(); i++) {
				Element element = workChildEles.get(i);
				String workId = element.child(0).text();
				String address = element.child(1).text();
				String state = element.child(2).text();
				String cores = element.child(3).text();
                String memory = element.child(4).text();

                //不统计dead节点信息
                if(!state.equalsIgnoreCase("alive")){
                    continue;
                }

                Pair<Integer, Integer> coreInfo = parserCore(cores);
                Pair<Integer, Integer> memInfo = parserMemory(memory);

                if(coreInfo == null){
                    logger.error("parser worker's core info error {}.", cores);
                    continue;
                }

                if(memInfo == null){
                    logger.error("parser worker's core info error {}.", memInfo);
                    continue;
                }

                //spark页面上的统计数据本身有问题
                if(coreInfo.getRight() < 0 || memInfo.getRight() < 0){
                    continue;
                }

                Map<String, Object> workerInfo = Maps.newHashMap();
                workerInfo.put(ADDRESS_KEY, address);
                workerInfo.put(CORE_TOTAL_KEY, coreInfo.getLeft());
                workerInfo.put(CORE_USED_KEY, coreInfo.getRight());
                workerInfo.put(CORE_FREE_KEY, coreInfo.getLeft() - coreInfo.getRight());

                workerInfo.put(MEMORY_TOTAL_KEY, memInfo.getLeft());
                workerInfo.put(MEMORY_USED_KEY, memInfo.getRight());
                workerInfo.put(MEMORY_FREE_KEY, memInfo.getLeft() - memInfo.getRight());

                availSlotMap.put(workId, workerInfo);
			}

			return availSlotMap;
		}

        /**
         * eg:4 (0 Used)
		 * 可能的情况为:8 (-1 Used)
         * @return
         */
		public static Pair<Integer, Integer> parserCore(String coresStr){
            Matcher matcher = corePattern.matcher(coresStr);
            if(matcher.find() && matcher.groupCount() == 2){
                String totalStr = matcher.group(1);
                String usedStr = matcher.group(2);
                Integer total = MathUtil.getIntegerVal(totalStr);
                Integer used = MathUtil.getIntegerVal(usedStr);
                return Pair.of(total, used);
            }

            return null;
        }

		/**
		 * eg: 6.8 GB (0.0 B Used)
         * 可能的情况为: 14.0 GB (-1073741824.0 B Used)
		 * @param memStr
		 * @return
		 */
		public static Pair<Integer, Integer> parserMemory(String memStr){
			Matcher matcher = memPattern.matcher(memStr);
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

		public static int convert2MB(String capacityStr, String unit){
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

		public static int GB2MB(String gbStr){
			Double gbVal = Double.valueOf(gbStr);
			Double mbVal = gbVal * 1024;
			return mbVal.intValue();
		}

		public static int KB2MB(String kbStr){
			Double kbVal = Double.valueOf(kbStr);
			Double mbVal = kbVal/1024;
			return mbVal.intValue();
		}

		public static int B2MB(String bStr){
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

		public static String getDriverLog(String message, String engineJobId){

            Document rootDoc = Jsoup.parse(message);
            Element workerEle = rootDoc.getElementsContainingOwnText(engineJobId)
                    .first().parent().child(2).select("a").first();
            String workerUrl = workerEle.attr("href");
            return getLog(String.format(DRIVER_LOGURL_FORMAT, workerUrl, engineJobId));
        }

		public static String getAppId(String driverLog){

            String appId = null;

			try{
				BufferedReader reader = new BufferedReader(new StringReader(driverLog));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (line.contains("Connected to Spark cluster with app ID ")) {
						appId = line.split("Connected to Spark cluster with app ID ")[1];
						break;
					}
				}
                return appId;
			} catch (Exception e) {
				logger.info("", e);
			}

			return null;
		}

		public static Map<String, List<Map<String, String>>> getAppLog(String appMessage) {
			Map<String, List<Map<String, String>>> appLogMap = new HashMap<>();
			List<Map<String, String>> list = new ArrayList<>();
            Document doc = Jsoup.parse(appMessage);
            Elements appLogEles = doc.getElementsContainingOwnText("stderr");
            for (int i = 0; i < appLogEles.size(); i++) {
                String appLogUrl = appLogEles.get(i).attr("href");
                String appLog = getLog(appLogUrl);
                String workerId = appLogEles.get(i).parent().parent().child(1).text();
                Map<String, String> map = new HashMap<>();
                map.put("id", workerId);
                map.put("value", appLog);
                list.add(map);
            }
            appLogMap.put("appLog", list);

			return appLogMap;
		}

		private static String getLog(String url) {
			String log = null;
			try {
				Document doc = Jsoup.connect(url).get();
				log = doc.select("pre").text();
			} catch (IOException e) {
				logger.info("" ,e);
			}
			return log;
		}

		public static boolean checkFailureForEngineDown(String msg){
            if(msg.contains(SPARK_ENGINE_DOWN)){
                return true;
            }

			return false;
		}
	}
	
	public static class FlinkRestParseUtil{

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
		
		
		public final static String NORESOURCE_AVAIABLE_EXCEPYION = "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job";

		public final static String FLINK_ENGINE_DOWN = "Failed to retrieve the JobManager gateway";

		private final static ObjectMapper objMapper = new ObjectMapper();

		private static Logger logger = LoggerFactory.getLogger(FlinkRestParseUtil.class);


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
			
			return availSlots;
		}



		public static String parseEngineLog(Map<String,String> jsonMap) throws IOException {

			String except = jsonMap.get("except");
			String accuInfo = jsonMap.get("accuInfo");
			String jobInfo = jsonMap.get("jobInfo");

			Map<String,Object> logMap = new HashMap<>();
			Map<String,Object> perfMap = new HashMap<>();

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
								perfMap.put("numRead", Integer.valueOf(value));
							} else if(name.equals("numWrite")) {
								perfMap.put("numWrite", Integer.valueOf(value));
							} else if(name.equals("nErrors")) {
								perfMap.put("numError", Integer.valueOf(value));
							}
						}
					}
				}
			}

			Map<String,Object> jobInfoMap = PublicUtil.jsonStrToObject(jobInfo, Map.class);
			List<Map<String,Object>> vertices = (List)jobInfoMap.get("vertices");
			if(vertices != null && vertices.size() == 2) {
				for(Map<String,Object> vertice : vertices) {
					String name = (String) vertice.get("name");

					if(name == null) {
						continue;
					}

					if(name.endsWith("reader")) {
						Integer readDuration = (Integer) vertice.get("duration");
						perfMap.put("durationRead", readDuration);
						Map<String,Object> readerMetrics = (Map<String, Object>) vertice.get("metrics");
						if(readerMetrics != null) {
							Integer byteRead = (Integer) readerMetrics.get("write-bytes");
							perfMap.put("byteRead", byteRead);
							try {
								BigDecimal rd = new BigDecimal(readDuration);
								BigDecimal br = new BigDecimal(byteRead);
								if(rd.equals(BigDecimal.ZERO)) {
									perfMap.put("speedRead", 0);
								} else {
									perfMap.put("speedRead", br.multiply(BigDecimal.valueOf(1000)).divideToIntegralValue(rd).intValue());
								}
							} catch(NumberFormatException ex) {
								perfMap.put("speedRead", 0);
							}


						}

					} else if(name.endsWith("writer")) {
						Integer writeDuration = (Integer) vertice.get("duration");
						perfMap.put("durationWrite", writeDuration);
						Map<String,Object> writerMetrics = (Map<String, Object>) vertice.get("metrics");
						if(writerMetrics != null) {
							Integer byteWrite = (Integer) writerMetrics.get("read-bytes");
							perfMap.put("byteWrite", byteWrite);
							try {
								BigDecimal rd = new BigDecimal(writeDuration);
								BigDecimal br = new BigDecimal(byteWrite);
								if(rd.equals(BigDecimal.ZERO)) {
									perfMap.put("speedWrite", 0);
								} else {
									perfMap.put("speedWrite", br.multiply(BigDecimal.valueOf(1000)).divideToIntegralValue(rd).intValue());
								}
							} catch(NumberFormatException ex) {
								perfMap.put("speedWrite", 0);
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
			return PublicUtil.objToString(logMap);
		}

		public static boolean checkFailureForEngineDown(String msg){
			if(msg.contains(FLINK_ENGINE_DOWN)){
				return true;
			}

			return false;
		}
		
	}
}
