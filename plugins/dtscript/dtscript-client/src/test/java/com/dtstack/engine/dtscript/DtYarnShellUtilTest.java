package com.dtstack.engine.dtscript;

import com.dtstack.engine.common.JobClient;

import java.util.Arrays;


public class DtYarnShellUtilTest {
    public static void main(String[] args) throws Exception {


        JobClient jobClient = new JobClient();
        jobClient.setPriority(1111111111111L);
        jobClient.setClassArgs("  --app-type jlogstash --app-name muyuntest_kafka_hdfs_orc_shell_930464c3 --cmd-opts " +
                "H4sIAAAAAAAAAHVUy5KrNhD9JRBmEhZZDDOAoXiUsY1AOwSeICxhJzZg+Pp0y+N7b1KVhQuX1I9z+pzWaYmufHDNdpuITN" +
                "5/C8/3jql7lxzeRTbcRKWcqd2mRqP8+UiKpVXF0izhW9h7ViI2j1RsNnHvzel6sZPP8xKKWVTEGRuruLMyOrAyN2pqD5h" +
                "zWqKuLfMpVunEqXltP2zonctmYB3/sO/fdefk813EH69YR7EhlRC7sBK/EKMeXRsUK/y/hcN14uQuWenKRtg9J/bIaGqE/UUkh2YBbCT" +
                "u/xyBj5X14SP79Kaa+Komnvjaz4JZ0dSWbsR03Vwy5Zt8u8P+Brfca4WYEKOVf3Hqj6fCmasyMuoyv3KyeYSD6ZRQh9MCZiR75ECtmzhuownP6qAw" +
                "v7n3PPBXzRH57+0zzrWi6d+xut44adcW7muYA8xsqWm6xoO7cGLKNuhMVibjv2qartmQbluVuWyJbx9VMXMrMlgZIvYLt/47A2+O+52J+oAeEBeRis4iE" +
                "6EZ+qm/M5ws9xPM/YuTKKuoKTGfBQXZ0fTRUrmwPc7bnZphh3mo90/+FvDfwu+b/z7I1Yt3HfhLQwrjoPw71IB5FgYTxgaxQP1bjV4IigX7xaipYlceFD8wPPXyv" +
                "2qsaTpTozTOS0XzCTT6BM1VTdu0Kp+eZQFbwWNQtxjBN6qi8sYthv6YG+VsTnv9JTXN8WxsVg/4RFC3GavS7SrSyVh1HQug14d90fXAg+BTHY+4uHLObG9fqr396m82qjjnygfsDoH57nSvAjS02JUhP80XsXVdrOwOtBw5AQzLDwwj770bV" +
                "5tfYn/GxUM0V4sJuHLcBbxbk72NOc9ciIHdeWT9+bVDZzYkI8e5U3tiwXFsBrePqX9m2wg8DXdD8oA6YyLQlzoGcsO37PA+J1oL1PHJudH7L3vwou6HmKEH3p8ZZV1LHwYgwDs9s0z8Pm" +
                "F9zQVwaP0W2PnSvaGu4NchhncFduCiZ6n1fH/FeXWJ744U8A5I5mlPHE5wxqjx6vvURsl7E8wjC+Ra0egGtfpnzfCt3UawOwbwkCN6rFU+9n7AfIDfLJrAN2rcfeHcW8pA12b6H7+t6AtOYd" +
                "byhvu+NsQf2OFK4D3QHg8l9BlSeDci+c2vR+6tFaG/U9QHz3eFu8uPtqd3pzT++AcORkXrhAUAAA==");
        jobClient.setTaskParams("worker.memory=512m\n" +
                "worker.cores=1\n" +
                "exclusive=false\n" +
                "worker.num=1\n" +
                "jvmOpts=\' -server -XX:+UseConcMarkSweepGC -XX:MaxDirectMemorySize=128m -XX:MaxMetaspaceSize=128m -XX:-UseCompressedClassPointers -XX:+DisableExplicitGC -XX:-OmitStackTraceInFastThrow -Xms512m -Xmx512m  \'\n" +
                "sasasd=\"asdasd\""
        );

        System.out.println(Arrays.asList(DtYarnShellUtil.buildPythonArgs(jobClient)));
    }
}