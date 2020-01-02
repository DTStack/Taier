package com.dtstack.engine.entrance.test;


import java.util.ArrayList;
import java.util.List;

/**
 * Reason:
 * Date: 2017/3/7
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class TestOOM {

    public static void main(String[] args) throws Exception {

        List<String> envList = new ArrayList<>(20);
        String[] env = envList.toArray(new String[envList.size()]);

        String command = "java -Xmx100m -Xms100m -server -classpath /Users/zhaozhangwan/localgit/rdos-execution-engine/service/target/test-classes:/Users/zhaozhangwan/localgit/rdos-execution-engine/service/target/classes com.dtstack.rdos.engine.entrance.test.TestGG 1>./dtstdout.log 2>./dterror.log";

        String[] cmd = {"bash", "-c", command};

        System.out.println("start");
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(cmd, env);

        process.waitFor();
        int exitValue = process.exitValue();
        System.out.println("container_wait_for_end exitValue: " + exitValue);
    }
}
