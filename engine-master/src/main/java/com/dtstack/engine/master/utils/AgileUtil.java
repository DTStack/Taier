package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/30
 */
public class AgileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgileUtil.class);

    private static final String HADOOP_IPS_KEY = "hadoop_ips";
    private static final String ZK_IPS_KEY = "zk_ips";
    private static final String HIVE_META_STORE_KEY = "hive_metastore";

    private static String CONFIG = System.getProperty("user.dir") + "/conf/agile.yml";
    private static String HADOOP_PKG = System.getProperty("user.dir") + "/hadoop_pkg";

    private final static long MEM_WEIGHT = 1000000000000L;
    private final static long CPU_WEIGHT = 1000000L;
    private final static int HA_NODE_LIMIT = 2;
    private final static String DOT = ",";
    private final static String COLON = ":";

    private static boolean isAgile = false;
    private static String defaultCluster;

    public static boolean triggerAgile() throws Exception {
        File agileFile = new File(CONFIG);
        if (!agileFile.exists()) {
            return false;
        }
        Map<String, Object> configMap = getConfigMap();
        String hadoopIps = MapUtils.getString(configMap, HADOOP_IPS_KEY);
        String zkIps = MapUtils.getString(configMap, ZK_IPS_KEY);
        String hiveMetaStore = MapUtils.getString(configMap, HIVE_META_STORE_KEY);

        isAgile = triggerAgile(hadoopIps, zkIps, hiveMetaStore);
        return isAgile;
    }

    public static boolean triggerAgile(String hadoopIps, String zkIps, String hiveMetaStore) throws Exception {

        if (StringUtils.isBlank(hadoopIps) || hadoopIps.contains(HADOOP_IPS_KEY) ||
                StringUtils.isBlank(zkIps) || zkIps.contains(ZK_IPS_KEY) ||
                StringUtils.isBlank(hiveMetaStore) || hiveMetaStore.contains(HIVE_META_STORE_KEY)) {
            return false;
        }
        return setAgileEnvInfo(hadoopIps, zkIps, hiveMetaStore);
    }

    private static boolean setAgileEnvInfo(String hadoopIps, String zkIps, String hiveMetaStore) throws Exception {

        String[] arrHadoopIps = StringUtils.split(hadoopIps, DOT);
        String[] arrZkIps = StringUtils.split(zkIps, DOT);
        String[] hiveMetaStoreInfo = StringUtils.split(hiveMetaStore, DOT);
        if (arrHadoopIps.length < HA_NODE_LIMIT || arrZkIps.length < HA_NODE_LIMIT || hiveMetaStoreInfo.length < 3) {
            return false;
        }

        List<Node> nodes = new ArrayList<>(arrHadoopIps.length);
        int len = 0;
        for (String hadoopIp : arrHadoopIps) {
            String[] hadoopIpInfo = StringUtils.split(hadoopIp, COLON);
            if (len == 0) {
                len = hadoopIpInfo.length;
            } else if (hadoopIpInfo.length != len) {
                LOGGER.warn("敏捷版本启动时，存在 HADOOP_IP 信息格式不一致，{}", hadoopIp);
                throw new RuntimeException("敏捷版本启动时，存在 HADOOP_IP 信息格式不一致，" + hadoopIp);
            }

            List<String> hadoopIpInfoList = new ArrayList<String>(Arrays.asList(hadoopIpInfo));
            for (int i = hadoopIpInfo.length; i < 4; i++) {
                hadoopIpInfoList.add("");
            }

            Node node = new Node(hadoopIpInfoList.get(0),
                    NumberUtils.toInt(hadoopIpInfoList.get(1), 0),
                    NumberUtils.toInt(hadoopIpInfoList.get(2), 0),
                    NumberUtils.toInt(hadoopIpInfoList.get(3), 0));
            nodes.add(node);
        }

        Collections.sort(nodes, (n1, n2) -> {
            if (n1.weight == n2.weight) {
                return 0;
            } else if (n1.weight > n2.weight) {
                return 1;
            } else {
                return -1;
            }
        });

        Node nn1 = nodes.get(0);
        Node nn2 = nodes.get(1);
        LOGGER.warn("nn1:{}, nn2:{}", nn1, nn2);

        File defaultClusterFile = new File(HADOOP_PKG + "/default_cluster.json");
        if (defaultClusterFile.exists()) {
            StringBuilder jsonStr = new StringBuilder();
            Files.lines(defaultClusterFile.toPath(), StandardCharsets.UTF_8).forEach(line -> jsonStr.append(line).append("\n"));

            String json = paramReplace(jsonStr.toString(), nn1, nn2, zkIps, hiveMetaStoreInfo);
            LOGGER.warn("default_cluster:{}", json);
            defaultCluster = json;
        }

        GenerateHadoopConf generateHadoopConf = new GenerateHadoopConf(nn1, nn2, zkIps, hiveMetaStoreInfo);
        generateHadoopConf.execute();

        return true;
    }

    private static String paramReplace(String str, Node nn1, Node nn2, String zkIps, String[] hiveMetaStoreInfo) {
        return str
                .replace("${zkIps}", zkIps)
                .replace("${nn1Ip}", nn1.ip)
                .replace("${nn2Ip}", nn2.ip)
                .replace("${rm1Ip}", nn1.ip)
                .replace("${rm2Ip}", nn2.ip)
                .replace("${hiveMetaStoreURL}", hiveMetaStoreInfo[0])
                .replace("${hiveMetaStoreUserName}", hiveMetaStoreInfo[1])
                .replace("${hiveMetaStoreUserPassword}", hiveMetaStoreInfo[2])
                .replace("${hiveThriftServer}", nn1.ip)
                .replace("${prometheusGatewayHost}", nn2.ip)
                .replace("${yarnHistoryServer}", nn1.ip)
                .replace("${flinkHistoryServer}", nn2.ip);
    }

    public static boolean isAgile() {
        return isAgile;
    }

    public static String getDefaultCluster() {
        return defaultCluster;
    }

    private static Map<String, Object> getConfigMap() throws Exception {
        Yaml yaml = new Yaml();
        FileInputStream input = new FileInputStream(new File(CONFIG));
        return yaml.loadAs(input, Map.class);
    }

    public static class Node {
        String ip;
        int mem;
        int cpu;
        int disk;
        long weight;

        public Node(String ip, int mem, int cpu, int disk) {
            this.ip = ip;
            this.mem = mem;
            this.cpu = cpu;
            this.disk = disk;
            this.weight = mem * MEM_WEIGHT + cpu * CPU_WEIGHT + disk;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "ip='" + ip + '\'' +
                    ", mem=" + mem +
                    ", cpu=" + cpu +
                    ", disk=" + disk +
                    ", weight=" + weight +
                    '}';
        }
    }


    private static class GenerateHadoopConf {
        Node nn1, nn2;
        String zkIps;
        String[] hiveMetaStoreInfo;

        public GenerateHadoopConf(Node nn1, Node nn2, String zkIps, String[] hiveMetaStoreInfo) {
            this.nn1 = nn1;
            this.nn2 = nn2;
            this.zkIps = zkIps;
            this.hiveMetaStoreInfo = hiveMetaStoreInfo;
        }

        public void execute() throws Exception {
            postHadoopConfDir();
            generateHadoopConfFile(Lists.newArrayList("core-site.xml", "hdfs-site.xml", "yarn-site.xml", "hive-site.xml"));
            afterHadoopConfDir();
        }

        public void postHadoopConfDir() {
            AgileUtil.postHadoopConfDir();
        }

        public void generateHadoopConfFile(List<String> files) throws Exception {
            for (String file : files) {
                AgileUtil.generateHadoopConfFile(file, nn1, nn2, zkIps, hiveMetaStoreInfo);
            }
        }

        public void afterHadoopConfDir() {
            AgileUtil.afterHadoopConfDir();
        }
    }


    private static void postHadoopConfDir() {
        File hadoopConfDir = new File(ConfigConstant.LOCAL_HADOOP_CONF_DIR);
        if (hadoopConfDir.exists()) {
            File[] hadoopConfFiles = hadoopConfDir.listFiles();
            if (hadoopConfFiles != null && hadoopConfFiles.length > 0) {
                for (int i = 0; i < hadoopConfFiles.length; i++) {
                    if (hadoopConfFiles[i].isFile()) {
                        hadoopConfFiles[i].delete();
                    }
                }
            }
        }
        hadoopConfDir.mkdirs();
    }

    private static void generateHadoopConfFile(String fileName, Node nn1, Node nn2, String zkIps, String[] hiveMetaStoreInfo) throws Exception {
        File defaultClusterFile = new File(HADOOP_PKG + "/" + fileName);
        if (defaultClusterFile.exists()) {
            StringBuilder yarnSite = new StringBuilder();
            Files.lines(defaultClusterFile.toPath(), StandardCharsets.UTF_8).forEach(line -> yarnSite.append(line).append("\n"));
            File newFile = new File(ConfigConstant.LOCAL_HADOOP_CONF_DIR + "/" + fileName);
            LOGGER.info("create ：" + newFile.getPath());
            if (newFile.createNewFile()) {
                Files.write(newFile.toPath(), paramReplace(yarnSite.toString(), nn1, nn2, zkIps, hiveMetaStoreInfo).getBytes());
            } else {
                throw new RuntimeException(newFile.getName() + " create file fail");
            }
        }
    }

    private static void afterHadoopConfDir() {
        System.setProperty("HADOOP_CONF_DIR", ConfigConstant.LOCAL_HADOOP_CONF_DIR);
    }
}
