package com.dtstack.taier.datasource.plugin.websocket;

import com.dtstack.taier.datasource.plugin.common.exception.IErrorPattern;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.common.service.ErrorAdapterImpl;
import com.dtstack.taier.datasource.plugin.common.service.IErrorAdapter;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.WebSocketSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.stream.Collectors;

@Slf4j
public class SocketClient extends AbsNoSqlClient {
    /**
     * socket 地址
     */
    private static final String SOCKET_URL = "%s?%s";

    private static final IErrorPattern ERROR_PATTERN = new WebsocketErrorPattern();

    // 异常适配器
    private static final IErrorAdapter ERROR_ADAPTER = new ErrorAdapterImpl();

    @Override
    public Boolean testCon(ISourceDTO source) {
        WebSocketSourceDTO socketSourceDTO = (WebSocketSourceDTO) source;
        String authParamStr = null;
        if (MapUtils.isNotEmpty(socketSourceDTO.getAuthParams())) {
            authParamStr = socketSourceDTO.getAuthParams().entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
        }
        WebSocketClient myClient = null;
        try {
            String url = StringUtils.isNotBlank(authParamStr) ? String.format(SOCKET_URL, socketSourceDTO.getUrl(), authParamStr) : socketSourceDTO.getUrl();
            myClient = new WebSocketClient(new URI(url)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("socket connected successfully");
                }

                @Override
                public void onMessage(String message) {
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("socket close was succeeded");
                }

                @Override
                public void onError(Exception ex) {
                    throw new SourceException(ex.getMessage(), ex);
                }
            };
            myClient.connect();
            // 判断是否连接成功，未成功后面发送消息时会报错
            int maxRetry = 0;
            while (!WebSocket.READYSTATE.OPEN.equals(myClient.getReadyState()) && maxRetry < 5) {
                maxRetry++;
                Thread.sleep(1000);
            }
            return WebSocket.READYSTATE.OPEN.equals(myClient.getReadyState());
        } catch (Exception e) {
            throw new SourceException(ERROR_ADAPTER.connAdapter(e.getMessage(), ERROR_PATTERN), e);
        } finally {
            if (myClient != null) {
                myClient.close();
            }
        }
    }

}
