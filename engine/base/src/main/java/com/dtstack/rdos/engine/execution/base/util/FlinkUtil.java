package com.dtstack.rdos.engine.execution.base.util;

import com.dtstack.rdos.engine.execution.exception.RdosException;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

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

    public static PackagedProgram buildProgram(String jarFilePath, List<URL> classpaths,
                                                  String entryPointClass, String[] programArgs, SavepointRestoreSettings spSetting)
            throws FileNotFoundException, ProgramInvocationException {
        if (jarFilePath == null) {
            throw new IllegalArgumentException("The program JAR file was not specified.");
        }

        String localJarPath = getTmpFileName(jarFilePath);
        if(!FileUtil.downLoadFile(jarFilePath, localJarPath)){
            return null;
        }

        File jarFile = new File(localJarPath);

        // Check if JAR file exists
        if (!jarFile.exists()) {
            throw new FileNotFoundException("JAR file does not exist: " + jarFile);
        } else if (!jarFile.isFile()) {
            throw new FileNotFoundException("JAR file is not a file: " + jarFile);
        }

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

    /**
     * 注册自定义方法到env上
     * @param classPath
     * @param funcName
     * @param tableEnv
     */
    public static void registerUDF(String classPath, String funcName, StreamTableEnvironment tableEnv){
        try{
            ScalarFunction udfFunc = Class.forName(classPath)
                    .asSubclass(ScalarFunction.class).newInstance();
            tableEnv.registerFunction(funcName, udfFunc);
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
    public static void registerUDTF(String classPath, String funcName, StreamTableEnvironment tableEnv){

        try {
            TableFunction udfFunc = Class.forName(classPath)
                    .asSubclass(TableFunction.class).newInstance();
            tableEnv.registerFunction(funcName, udfFunc);
        }catch (Exception e){
            logger.error("", e);
            throw new RdosException("register Table UDF exception:" + e.getMessage());
        }
    }

}
