package com.dtstack.taier.datasource.plugin.common.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JDBC-URL参数提取工具类
 *
 * @author zhiyi
 */
public class JdbcUrlUtil {

    public static final String PROP_HOST = "host";
    public static final String PROP_PORT = "port";
    public static final String PROP_DATABASE = "database";
    public static final String PROP_SERVER = "server";
    public static final String PROP_PARAMS = "params";
    public static final String PROP_FOLDER = "folder";
    public static final String PROP_FILE = "file";
    public static final String PROP_USER = "user";
    public static final String PROP_PASSWORD = "password";

    private static String getPropertyRegex(String property) {
        switch (property) {
            case PROP_FOLDER:
            case PROP_FILE:
            case PROP_PARAMS:
                return ".+?";
            default:
                return "[\\\\w\\\\-_.~]+";
        }
    }

    private static String replaceAll(String input, String regex, Function<Matcher, String> replacer) {
        final Matcher matcher = Pattern.compile(regex).matcher(input);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, replacer.apply(matcher));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static Pattern getPattern(String sampleUrl) {
        String pattern = sampleUrl;
        pattern = replaceAll(pattern, "\\[(.*?)]", m -> "\\\\E(?:\\\\Q" + m.group(1) + "\\\\E)?\\\\Q");
        pattern = replaceAll(pattern, "\\{(.*?)}", m -> "\\\\E(\\?<\\\\Q" + m.group(1) + "\\\\E>" + getPropertyRegex(m.group(1)) + ")\\\\Q");
        pattern = "^\\Q" + pattern + "\\E$";
        return Pattern.compile(pattern);
    }

    /**
     * 根据主机地址与端口号检查可达性
     *
     * @param host 主机地址
     * @param port 端口号
     * @return 成功返回true，否则为false
     */
    public static boolean reachable(String host, String port) {
        try {
            InetAddress address = InetAddress.getByName(host);
            if (!address.isReachable(1500)) {
                return false;
            }
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, Integer.parseInt(port)), 1500);
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}