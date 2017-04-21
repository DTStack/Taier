package com.dtstack.rdos.engine.entrance.http;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.engine.entrance.http.handler.NodeHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年12月30日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
@SuppressWarnings("restriction")
public class EHttpServer {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

	private String host="0.0.0.0";
	
	private int port;
	
	private HttpServer server;
	
	private String localAddress;
	
	private Executor executors = Executors.newCachedThreadPool();
	
	public EHttpServer(String localAddress) throws Exception{
		this.localAddress = localAddress;
		this.port = (Integer) HttpCommon.getUrlPort(this.localAddress)[1];
		init();
	}
	
	public EHttpServer(Map<String, Object> nodeConfig) throws Exception{
		// TODO Auto-generated constructor stub
		this.localAddress = (String) nodeConfig.get("localAddress");
		this.port = (Integer) HttpCommon.getUrlPort(this.localAddress)[1];
		init();
	}

	public void release(){
		this.server.stop(1);
	}
	
	private void init() throws Exception{
		this.server = HttpServer.create(new InetSocketAddress(InetAddress.getByName(host),port), 0);
		this.server.setExecutor(executors);
		setHandler();
		this.server.start();
		logger.warn("EngineHttpServer start at:{}",String.valueOf(port));
	}
	
	private void setHandler(){
		server.createContext(Urls.ROOT, new NodeHandler());
	}
}
