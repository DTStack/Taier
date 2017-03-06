package com.dtstack.rdos.engine.execution.flink120.util;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.enumeration.ESourceType;
import com.dtstack.rdos.engine.execution.base.operator.CreateResultOperator;
import com.dtstack.rdos.engine.execution.base.pojo.PropertyConstant;
import com.dtstack.rdos.engine.execution.base.util.FileUtil;
import com.dtstack.rdos.engine.execution.flink120.sink.DBSink;
import com.dtstack.rdos.engine.execution.flink120.sink.MysqlSink;
import com.dtstack.rdos.engine.execution.flink120.source.IStreamSourceGener;
import com.dtstack.rdos.engine.execution.flink120.source.FlinkKafka09SourceGenr;

import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class FlinkUtil {

    private static final Logger logger = LoggerFactory.getLogger(FlinkUtil.class);

    public static String tmp_file_path = "/tmp/flinkjar";

    private static final String URL_SPLITE = "/";

    private static String fileSP = File.separator;

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
        if(properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_INTERVAL) == null){
            return;
        }else{
            Long interval = Long.valueOf(properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_INTERVAL));
            //start checkpoint every ${interval}
            env.enableCheckpointing(interval);
        }

        String checkMode = properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_MODE);
        if(checkMode != null){
            if(checkMode.equalsIgnoreCase("EXACTLY_ONCE")){
                env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
            }else if(checkMode.equalsIgnoreCase("AT_LEAST_ONCE")){
                env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
            }else{
                throw new RdosException("not support of FLINK_CHECKPOINT_MODE :" + checkMode);
            }
        }

        String checkpointTimeoutStr = properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_TIMEOUT);
        if(checkpointTimeoutStr != null){
            Long checkpointTimeout = Long.valueOf(checkpointTimeoutStr);
            //checkpoints have to complete within one min,or are discard
            env.getCheckpointConfig().setCheckpointTimeout(checkpointTimeout);
        }

        String maxConcurrCheckpointsStr = properties.getProperty(PropertyConstant.FLINK_MAXCONCURRENTCHECKPOINTS);
        if(maxConcurrCheckpointsStr != null){
            Integer maxConcurrCheckpoints = Integer.valueOf(maxConcurrCheckpointsStr);
            //allow only one checkpoint to be int porgress at the same time
            env.getCheckpointConfig().setMaxConcurrentCheckpoints(maxConcurrCheckpoints);
        }

        String cleanupModeStr = properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_CLEANUPMODE);
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

        String backendPath = properties.getProperty(PropertyConstant.FLINK_CHECKPOINT_DATAURI);
        if(backendPath != null){
            //set checkpoint save path on file system, 根据实际的需求设定文件路径,hdfs://, file://
            env.setStateBackend(new FsStateBackend(backendPath));
        }

    }


    public static PackagedProgram buildProgram(String jarFilePath, List<URL> classpaths,
                                                  String entryPointClass, String[] programArgs, SavepointRestoreSettings spSetting)
            throws FileNotFoundException, ProgramInvocationException {
        if (jarFilePath == null) {
            throw new IllegalArgumentException("The program JAR file was not specified.");
        }

        File jarFile = downloadJar(jarFilePath);

        // Get assembler class
        PackagedProgram program = entryPointClass == null ?
                new PackagedProgram(jarFile, classpaths, programArgs) :
                new PackagedProgram(jarFile, classpaths, entryPointClass, programArgs);

        program.setSavepointRestoreSettings(spSetting);

        return program;
    }

    public static String getTmpFileName(String fileUrl){
        String name = fileUrl.substring(fileUrl.lastIndexOf(URL_SPLITE));
        String tmpFileName = tmp_file_path  + fileSP + name;
        return tmpFileName;
    }

    public static File downloadJar(String remoteFilePath) throws FileNotFoundException {
        String localJarPath = FlinkUtil.getTmpFileName(remoteFilePath);
        if(!FileUtil.downLoadFile(remoteFilePath, localJarPath)){
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
     * TABLE|SCALA
     * 注册UDF到table env
     */
    public static void registerUDF(String type, String classPath, String funcName, StreamTableEnvironment tableEnv){
        if("TABLE".equalsIgnoreCase(type)){
            registerScalaUDF(classPath, funcName, tableEnv);
        }else if("SCALA".equalsIgnoreCase(type)){
            registerTableUDF(classPath, funcName, tableEnv);
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
    public static void registerScalaUDF(String classPath, String funcName, StreamTableEnvironment tableEnv){
        try{
            ScalarFunction udfFunc = Class.forName(classPath)
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
    public static void registerTableUDF(String classPath, String funcName, StreamTableEnvironment tableEnv){

        try {
            TableFunction udfFunc = Class.forName(classPath)
                    .asSubclass(TableFunction.class).newInstance();
            tableEnv.registerFunction(funcName, udfFunc);
            logger.info("register table function:{} success.", funcName);
        }catch (Exception e){
            logger.error("", e);
            throw new RdosException("register Table UDF exception:" + e.getMessage());
        }
    }

    /**
     * 根据指定的类型构造数据源
     * 当前只支持kafka09
     * @param sourceType
     * @return
     */
    public static IStreamSourceGener getStreamSourceGener(ESourceType sourceType){
        switch (sourceType){
            case KAFKA09:
                return new FlinkKafka09SourceGenr();
        }

        throw new RdosException("not support for flink stream source type " + sourceType);
    }


    public static void writeToSink(CreateResultOperator resultOperator, Table table){

        String resultType = resultOperator.getType();
        if("mysql".equalsIgnoreCase(resultType)){
            DBSink jdbcInfo = new MysqlSink(resultOperator);
            table.writeToSink(jdbcInfo);
        }else{
            throw new RdosException("not support type:" + resultType + " for sink!!!");
        }
    }

    /**
     *
     * FIXME 仅针对sql执行方式,暂时未找到区分设置source,transform,sink 并行度的方式
     * 设置job运行的并行度
     * @param env
     * @param properties
     */
    public static void setEnvParallelism(StreamExecutionEnvironment env, Properties properties){

        if(env == null || properties == null){
            return;
        }

        String parallelismStr = properties.getProperty("parallelism");
        if(parallelismStr != null){
            Integer parallelism = Integer.valueOf(parallelismStr);
            env.setParallelism(parallelism);
        }

    }
}
