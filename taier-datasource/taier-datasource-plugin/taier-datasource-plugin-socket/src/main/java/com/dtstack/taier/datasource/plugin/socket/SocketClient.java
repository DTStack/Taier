package com.dtstack.taier.datasource.plugin.socket;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SocketSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * socket数据源客户端
 *
 * @author ：wangchuan
 * date：Created in 4:16 下午 2020/12/28
 * company: www.dtstack.com
 */
@Slf4j
public class SocketClient extends AbsNoSqlClient {

    // ip:port正则
    private static final Pattern HOST_PORT_PATTERN = Pattern.compile("(?<host>(.*)):((?<port>\\d+))*");

    @Override
    public Boolean testCon(ISourceDTO source) {
        SocketSourceDTO socketSourceDTO = (SocketSourceDTO) source;
        String hostPort = socketSourceDTO.getHostPort();
        if (StringUtils.isBlank(hostPort)) {
            throw new SourceException("socket datasource ip and port not empty");
        }
        Matcher matcher = HOST_PORT_PATTERN.matcher(hostPort);
        if (matcher.find()) {
            String host = matcher.group("host");
            String portStr = matcher.group("port");
            if (StringUtils.isBlank(portStr)) {
                throw new SourceException("socket datasource port is not empty");
            }
            // 转化为int格式的端口
            int port = Integer.parseInt(portStr);
            InetAddress address = null;
            try {
                // 方法内支持ipv6
                address = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                throw new SourceException(String.format("socket connection exception：UnknownHostException：%s", e.getMessage()), e);
            }
            try (Socket socket = new Socket(address, port)) {
                // 往输出流发送一个字节的数据，Socket的SO_OOBINLINE属性没有打开，就会自动舍弃这个字节，该属性默认关闭
                socket.sendUrgentData(0xFF);
            } catch (IOException e) {
                throw new SourceException(String.format("socket connection exception：%s", e.getMessage()), e);
            }
        }
        return true;
    }
}
