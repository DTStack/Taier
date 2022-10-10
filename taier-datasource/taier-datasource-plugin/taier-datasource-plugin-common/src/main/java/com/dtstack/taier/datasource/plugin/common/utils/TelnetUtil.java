package com.dtstack.taier.datasource.plugin.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiangbo
 */
public class TelnetUtil {

    protected static final Logger LOG = LoggerFactory.getLogger(TelnetUtil.class);

    private static Pattern JDBC_PATTERN = Pattern.compile("(?<host>[^:@/]+):(?<port>\\d+).*");
    public static final String PHOENIX_PREFIX = "jdbc:phoenix";
    private static Pattern PHOENIX_PATTERN = Pattern.compile("jdbc:phoenix:(?<host>\\S+):(?<port>\\d+).*");
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String SPLIT_KEY = ",";
    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 1000 * 60;

    public static void telnet(String ip,int port, int connectTimeoutMs) {
        try {
            RetryUtil.executeWithRetry(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    TelnetClient client = null;
                    try{
                        client = new TelnetClient();
                        int innerConnectTimeoutMs = connectTimeoutMs;
                        if(innerConnectTimeoutMs <= 0) {
                            innerConnectTimeoutMs = DEFAULT_CONNECTION_TIMEOUT_MS;
                        }
                        client.setConnectTimeout(innerConnectTimeoutMs);
                        client.connect(ip,port);
                        return true;
                    } catch (Exception e){
                        throw new RuntimeException("Unable connect to : " + ip + ":" + port);
                    } finally {
                        try {
                            if (client != null){
                                client.disconnect();
                            }
                        } catch (Exception ignore){
                        }
                    }
                }
            }, 3,1000,false);
        } catch (Exception e) {
            LOG.warn("", e);
        }
    }

    /**
     * telnet 并返回结果
     * @param ip
     * @param port
     * @param connectTimeoutMs
     * @return
     */
    public static Boolean telnetWithResult(String ip,int port, int connectTimeoutMs) {
        try {
            RetryUtil.executeWithRetry(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    TelnetClient client = null;
                    try{
                        client = new TelnetClient();
                        int innerConnectTimeoutMs = connectTimeoutMs;
                        if(innerConnectTimeoutMs <= 0) {
                            innerConnectTimeoutMs = DEFAULT_CONNECTION_TIMEOUT_MS;
                        }
                        client.setConnectTimeout(innerConnectTimeoutMs);
                        client.connect(ip,port);
                        return true;
                    } catch (Exception e){
                        throw new RuntimeException("Unable connect to : " + ip + ":" + port);
                    } finally {
                        try {
                            if (client != null){
                                client.disconnect();
                            }
                        } catch (Exception ignore){
                        }
                    }
                }
            }, 3,10000,false);
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    public static void telnet(int connectionTimeoutMs, String url) {
        telnet(url, connectionTimeoutMs);
    }

    public static void telnet(String url) {
        telnet(url, DEFAULT_CONNECTION_TIMEOUT_MS);
    }

    public static void telnet(String url, int connectionTimeoutMs) {
        if (url == null || url.trim().length() == 0){
            throw new IllegalArgumentException("url can not be null");
        }

        String host = null;
        int port = 0;
        Matcher matcher;
        if(StringUtils.startsWith(url, PHOENIX_PREFIX)){
            matcher = PHOENIX_PATTERN.matcher(url);
        }else{
            matcher = JDBC_PATTERN.matcher(url);
        }
        if (matcher.find()){
            host = matcher.group(HOST_KEY);
            port = Integer.parseInt(matcher.group(PORT_KEY));
        }

        if (host == null || port == 0){
            //oracle高可用jdbc url此处获取不到IP端口，直接return。
            return;
        }

        if(host.contains(SPLIT_KEY)){
            String[] hosts = host.split(SPLIT_KEY);
            for (String s : hosts) {
                if(StringUtils.isNotBlank(s)){
                    telnet(s,port, connectionTimeoutMs);
                }
            }
        }else{
            telnet(host,port, connectionTimeoutMs);
        }
    }
}
