package com.dtstack.rdos.engine.execution.flink140.util;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.flink140.constrant.ConfigConstrant;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkUtil {

    private static final Logger logger = LoggerFactory.getLogger(FlinkUtil.class);

    private static final String URL_SPLITE = "/";

    private static String fileSP = File.separator;

    private static String localSyncFileDir;

    public static void setLocalSyncFileDir(String fileDir){
        if(localSyncFileDir == null){
            synchronized (FlinkUtil.class){
                if(localSyncFileDir == null){
                    localSyncFileDir = fileDir;
                }
            }
        }
    }

    /**
     * 开启checkpoint
     * @param env
     * @throws IOException
     */
    public static void openCheckpoint(StreamExecutionEnvironment env, Properties properties) throws IOException {

        if(properties == null){
            return;
        }

        //设置了时间间隔才表明开启了checkpoint
        if(properties.getProperty(ConfigConstrant.FLINK_CHECKPOINT_INTERVAL_KEY) == null){
            return;
        }else{
            Long interval = Long.valueOf(properties.getProperty(ConfigConstrant.FLINK_CHECKPOINT_INTERVAL_KEY));
            //start checkpoint every ${interval}
            env.enableCheckpointing(interval);
        }

        String checkMode = properties.getProperty(ConfigConstrant.FLINK_CHECKPOINT_MODE_KEY);
        if(checkMode != null){
            if(checkMode.equalsIgnoreCase("EXACTLY_ONCE")){
                env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
            }else if(checkMode.equalsIgnoreCase("AT_LEAST_ONCE")){
                env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
            }else{
                throw new RdosException("not support of FLINK_CHECKPOINT_MODE_KEY :" + checkMode);
            }
        }

        String checkpointTimeoutStr = properties.getProperty(ConfigConstrant.FLINK_CHECKPOINT_TIMEOUT_KEY);
        if(checkpointTimeoutStr != null){
            Long checkpointTimeout = Long.valueOf(checkpointTimeoutStr);
            //checkpoints have to complete within one min,or are discard
            env.getCheckpointConfig().setCheckpointTimeout(checkpointTimeout);
        }

        String maxConcurrCheckpointsStr = properties.getProperty(ConfigConstrant.FLINK_MAXCONCURRENTCHECKPOINTS_KEY);
        if(maxConcurrCheckpointsStr != null){
            Integer maxConcurrCheckpoints = Integer.valueOf(maxConcurrCheckpointsStr);
            //allow only one checkpoint to be int porgress at the same time
            env.getCheckpointConfig().setMaxConcurrentCheckpoints(maxConcurrCheckpoints);
        }

        String cleanupModeStr = properties.getProperty(ConfigConstrant.FLINK_CHECKPOINT_CLEANUPMODE_KEY);
        if(cleanupModeStr != null){//设置在cancle job情况下checkpoint是否被保存
            if("true".equalsIgnoreCase(cleanupModeStr)){
                env.getCheckpointConfig().enableExternalizedCheckpoints(
                        CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);
            }else if("false".equalsIgnoreCase(cleanupModeStr)){
                env.getCheckpointConfig().enableExternalizedCheckpoints(
                        CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
            }else{
                throw new RdosException("not support value of cleanup mode :" + cleanupModeStr);
            }
        }

        String backendPath = properties.getProperty(ConfigConstrant.FLINK_CHECKPOINT_DATAURI_KEY);
        if(backendPath != null){
            //set checkpoint save path on file system, 根据实际的需求设定文件路径,hdfs://, file://
            env.setStateBackend(new FsStateBackend(backendPath));
        }

    }

    /**
     * #ProcessingTime(默认),IngestionTime,EventTime
     * @param env
     * @param properties
     */
    public static void setStreamTimeCharacteristic(StreamExecutionEnvironment env, Properties properties){
        if(!properties.containsKey(ConfigConstrant.FLINK_TIME_CHARACTERISTIC_KEY)){
            //走默认值
            return;
        }

        String characteristicStr = properties.getProperty(ConfigConstrant.FLINK_TIME_CHARACTERISTIC_KEY);
        Boolean flag = false;
        for(TimeCharacteristic tmp : TimeCharacteristic.values()){
            if(characteristicStr.equalsIgnoreCase(tmp.toString())){
                env.setStreamTimeCharacteristic(tmp);
                flag = true;
            }
        }

        if(!flag){
            throw new RdosException("illegal property :" + ConfigConstrant.FLINK_TIME_CHARACTERISTIC_KEY);
        }
    }


    public static PackagedProgram buildProgram(String fromPath, String toPath, List<URL> classpaths,
                                                  String entryPointClass, String[] programArgs, SavepointRestoreSettings spSetting)
            throws FileNotFoundException, ProgramInvocationException {
        if (fromPath == null) {
            throw new IllegalArgumentException("The program JAR file was not specified.");
        }

        File jarFile = downloadJar(fromPath, toPath);

        // Get assembler class
        PackagedProgram program = entryPointClass == null ?
                new PackagedProgram(jarFile, classpaths, programArgs) :
                new PackagedProgram(jarFile, classpaths, entryPointClass, programArgs);

        program.setSavepointRestoreSettings(spSetting);

        return program;
    }

    public static String getTmpFileName(String fileUrl, String toPath){
        String name = fileUrl.substring(fileUrl.lastIndexOf(URL_SPLITE) + 1);
        String tmpFileName = toPath  + fileSP + name;
        return tmpFileName;
    }

    public static File downloadJar(String fromPath, String toPath) throws FileNotFoundException {
        String localJarPath = FlinkUtil.getTmpFileName(fromPath, toPath);
        if(!FlinkFileUtil.downLoadFile(fromPath, localJarPath)){
            //如果不是http 或者 hdfs协议的从本地读取
            String localPath = localSyncFileDir + fileSP + fromPath;
            File localFile = new File(localPath);
            if(localFile.exists()){
                return localFile;
            }
            return null;
        }

        File jarFile = new File(localJarPath);

        // Check if JAR file exists
        if (!jarFile.exists()) {
            throw new FileNotFoundException("JAR file does not exist: " + jarFile);
        } else if (!jarFile.isFile()) {
            throw new FileNotFoundException("JAR file is not a file: " + jarFile);
        }

        return jarFile;
    }

    /**
     * FIXME 暂时不支持 UDF 实现类--有参构造方法
     * TABLE|SCALA
     * 注册UDF到table env
     */
    public static void registerUDF(String type, String classPath, String funcName, TableEnvironment tableEnv,
                                   ClassLoader classLoader){
        if("SCALA".equalsIgnoreCase(type)){
            registerScalaUDF(classPath, funcName, tableEnv, classLoader);
        }else if("TABLE".equalsIgnoreCase(type)){
            registerTableUDF(classPath, funcName, tableEnv, classLoader);
        }else{
            throw new RdosException("not support of UDF which is not in (TABLE, SCALA)");
        }

    }

    /**
     * 注册自定义方法到env上
     * @param classPath
     * @param funcName
     * @param tableEnv
     */
    public static void registerScalaUDF(String classPath, String funcName, TableEnvironment tableEnv,
                                        ClassLoader classLoader){
        try{
            ScalarFunction udfFunc = Class.forName(classPath, false, classLoader)
                    .asSubclass(ScalarFunction.class).newInstance();
            tableEnv.registerFunction(funcName, udfFunc);
            logger.info("register scala function:{} success.", funcName);
        }catch (Exception e){
            logger.error("", e);
            throw new RdosException("register UDF exception:" + e.getMessage());
        }
    }

    /**
     * 注册自定义TABLEFFUNC方法到env上
     * @param classPath
     * @param funcName
     * @param tableEnv
     */
    public static void registerTableUDF(String classPath, String funcName, TableEnvironment tableEnv,
                                        ClassLoader classLoader){
        try {
            TableFunction udfFunc = Class.forName(classPath, false, classLoader)
                    .asSubclass(TableFunction.class).newInstance();

            if(tableEnv instanceof StreamTableEnvironment){
                ((StreamTableEnvironment)tableEnv).registerFunction(funcName, udfFunc);
            }else if(tableEnv instanceof BatchTableEnvironment){
                ((BatchTableEnvironment)tableEnv).registerFunction(funcName, udfFunc);
            }else{
                throw new RdosException("no support tableEnvironment class for " + tableEnv.getClass().getName());
            }

            logger.info("register table function:{} success.", funcName);
        }catch (Exception e){
            logger.error("", e);
            throw new RdosException("register Table UDF exception:" + e.getMessage());
        }
    }


    /**
     *
     * FIXME 仅针对sql执行方式,暂时未找到区分设置source,transform,sink 并行度的方式
     * 设置job运行的并行度
     * @param properties
     */
    public static int getEnvParallelism(Properties properties){
        String parallelismStr = properties.getProperty(ConfigConstrant.SQL_ENV_PARALLELISM);
        return StringUtils.isNotBlank(parallelismStr)?Integer.parseInt(parallelismStr):1;

    }
    
    
    /**
     * 最大并发度
     * @param properties
     * @return
     */
    public static int getMaxEnvParallelism(Properties properties){
        String parallelismStr = properties.getProperty(ConfigConstrant.SQL_MAX_ENV_PARALLELISM);
        return StringUtils.isNotBlank(parallelismStr)?Integer.parseInt(parallelismStr):0;
    }
    
    /**
     * 针对MR类型整个job的并发度设置
     * @param properties
     * @return
     */
    public static int getJobParallelism(Properties properties){
        String parallelismStr = properties.getProperty(ConfigConstrant.MR_JOB_PARALLELISM);
        return StringUtils.isNotBlank(parallelismStr)?Integer.parseInt(parallelismStr):1;
    }
    
    /**
     * 
     * @param properties
     * @return
     */
    public static long getBufferTimeoutMillis(Properties properties){
        String mills = properties.getProperty(ConfigConstrant.SQL_BUFFER_TIMEOUT_MILLIS);
        return StringUtils.isNotBlank(mills)?Long.parseLong(mills):0L;
    }

    public static URLClassLoader createNewClassLoader(List<URL> jarURLList, ClassLoader superClassLoader){

        int size = 0;
        for(URL url : jarURLList){
            if(url.toString().endsWith(".jar")){
                size++;
            }
        }

        URL[] urlArray = new URL[size];
        int i=0;
        for(URL url : jarURLList){
            if(url.toString().endsWith(".jar")){
                urlArray[i] = url;
                i++;
            }
        }

        URLClassLoader classLoader = new URLClassLoader(urlArray, superClassLoader);
        return classLoader;
    }

    // 数据同步专用: 获取flink端插件classpath, 在programArgsList中添加engine端plugin根目录
    public static List<URL> getUserClassPath(List<String> programArgList, String flinkSyncPluginRoot) {
        List<URL> urlList = new ArrayList<>();
        if(programArgList == null || flinkSyncPluginRoot == null)
            return urlList;

        int i = 0;
        for(; i < programArgList.size() - 1; ++i)
            if(programArgList.get(i).equals("-job") || programArgList.get(i).equals("--job"))
                break;

        if(i == programArgList.size() - 1)
            return urlList;

        programArgList.add("-pluginRoot");
        programArgList.add(localSyncFileDir);

        String job = programArgList.get(i + 1);

        try {
            job = java.net.URLDecoder.decode(job, "UTF-8");
            programArgList.set(i + 1, job);
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(job, Map.class);
            LinkedTreeMap jobMap = (LinkedTreeMap) map.get("job");

            List<LinkedTreeMap> contentList = (List<LinkedTreeMap>) jobMap.get("content");
            LinkedTreeMap content = contentList.get(0);
            LinkedTreeMap reader = (LinkedTreeMap) content.get("reader");
            String readerName = (String) reader.get("name");
            LinkedTreeMap writer = (LinkedTreeMap) content.get("writer");
            String writerName = (String) writer.get("name");

            Preconditions.checkArgument(StringUtils.isNotEmpty(readerName), "reader name should not be empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(writerName), "writer ame should not be empty");

            String readerClasspath = "file://" + flinkSyncPluginRoot + fileSP + readerName + fileSP + readerName + ".jar";
            String writerClasspath = "file://" + flinkSyncPluginRoot + fileSP + writerName + fileSP + writerName + ".jar";
            urlList.add(new URL(readerClasspath));
            urlList.add(new URL(writerClasspath));

            File commonDir = new File(flinkSyncPluginRoot + fileSP + "common" + fileSP);
            if(commonDir.exists() && commonDir.isDirectory()) {
                File[] commonJarFiles = commonDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jar");
                    }
                });
                for(File commonJarFile : commonJarFiles) {
                    urlList.add(commonJarFile.toURI().toURL());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return urlList;
        }

    }

    public static TypeInformation[] transformTypes(Class[] fieldTypes){
        TypeInformation[] types = new TypeInformation[fieldTypes.length];
        for(int i=0; i<fieldTypes.length; i++){
            types[i] = TypeInformation.of(fieldTypes[i]);
        }

        return types;
    }

}
