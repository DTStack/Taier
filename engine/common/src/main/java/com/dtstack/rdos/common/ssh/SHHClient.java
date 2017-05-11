package com.dtstack.rdos.common.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.StreamGobbler;


/**
 * 
 * @author sishu.yss
 *
 */
public class SHHClient {
	
	private Logger logger = LoggerFactory.getLogger(SHHClient.class);
	
	private String userName;
	
	private String password;
	
	private String serverIp;
	
	private int port = 22;
	
	public SHHClient(String userName,String password,String serverIp){
		this.userName =  userName;
		this.password = password;
		this.serverIp  = serverIp;
	}
	
	public SHHClient(String userName,String password,String serverIp,int port){
		this.userName =  userName;
		this.password = password;
		this.serverIp  = serverIp;
		this.port = port;
	}
	
	public String ssh(String command) throws Exception{
        StringBuffer sb = new StringBuffer();
        try
        {
            Connection conn = new Connection(serverIp,port);
            conn.connect();
            boolean isAuthenticated = false;
            if(StringUtils.isNotBlank(password)){
            	 isAuthenticated = conn.authenticateWithPassword(userName, password);
            }else{
           	     isAuthenticated = conn.authenticateWithNone(userName);
            }
            if (isAuthenticated == false)
                throw new IOException("Authentication failed...");        
            ch.ethz.ssh2.Session sess = conn.openSession();
            sess.execCommand(command); 
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while (true)
            {
                if ((stdout.available() == 0)) {  
                    int conditions = sess.waitForCondition(ChannelCondition.STDOUT_DATA |   
                          ChannelCondition.STDERR_DATA | ChannelCondition.EOF, 1000*5);  
                      if ((conditions & ChannelCondition.TIMEOUT) != 0) {  
                          logger.info("time out break");  
                          break;//超时后退出循环，要保证超时时间内，脚本可以运行完成  
                      }  
                    if ((conditions & ChannelCondition.EOF) != 0) {  
                      if ((conditions & (ChannelCondition.STDOUT_DATA |   
                              ChannelCondition.STDERR_DATA)) == 0) {  
                              logger.info("break");  
                              break;  
                          }  
                      }  
                  }
                String line = br.readLine();
                if (line == null)
                    break;
                sb.append(line+"\n");
            }
            sess.close();
            conn.close();
        }catch (IOException e){
        	logger.error("",e);
        }
        return sb.toString();
    }
	
	
	public static void main(String[] args) throws Exception{
		
		System.out.println(new SHHClient("root","abc123","172.16.1.155").ssh("cd /opt/dtstack/datax/bin;nohup python datax.py ./mysql2odps.json &"));

		
//		System.out.println(new SHHClient("sishu.yss","ysq63712284","127.0.0.1").ssh("cd /Users/sishuyss/datax/bin;nohup python datax.py ./mysql2odps.json &"));
	}
	
}
