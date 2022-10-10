package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 22:47 2020/2/26
 * @Description：Tel 工具类
 */
@Slf4j
public class TelUtil {

    // ip:端口 正则解析，适配ipv6
    private static final Pattern HOST_PORT_PATTERN = Pattern.compile("(?<host>(.*)):(?<port>\\d+)*");

    public static boolean checkTelnetAddr(String urls) {
        String[] addrs = urls.split(",");
        for (String addr : addrs) {
            Matcher matcher = HOST_PORT_PATTERN.matcher(addr);
            if (!matcher.find()) {
                throw new SourceException(String.format("address：%s wrong format", addr));
            }
            String host = matcher.group("host");
            String portStr = matcher.group("port");
            if (StringUtils.isBlank(host) || StringUtils.isBlank(portStr)) {
                throw new SourceException(String.format("address：%s missing ip or port", addr));
            }
            //集群内任一地址能telnet通则返回成功
            boolean connected = AddressUtil.telnet(host.trim(), Integer.parseInt(portStr.trim()));
            if (connected) {
                return true;
            }
        }
        throw new SourceException(String.format("all addresses ：%s can't connect", urls));
    }
}
