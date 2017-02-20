package com.dtstack.rdos.engine.entrance.http;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class EngineHttpServer {
	
	private static final Logger logger = LoggerFactory.getLogger(EngineHttpServer.class);

	private String host="0.0.0.0";
	
	private int port;
	
	
	private HttpServer server;
	
	private String localAddress;
	
	public EngineHttpServer(String localAddress) throws Exception{
		this.localAddress = localAddress;
		this.port = (int) HttpCommon.getUrlPort(this.localAddress)[1];
		init();
	}
	
	public void release(){
		this.server.stop(1);
	}
	
	private void init() throws Exception{
		this.server = HttpServer.create(new InetSocketAddress(InetAddress.getByName(host),port), 0);
		this.server.setExecutor(null);
		setHandler();
		this.server.start();
		logger.warn("EngineHttpServer start at:{}",String.valueOf(port));
	}
	
	private void setHandler(){

	}

}
