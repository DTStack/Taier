package com.dtstack.engine.common.http;

import com.dtstack.engine.common.exception.ExceptionUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;


/**
 * 
 *
 * Date: 2016年12月30日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class HttpClient {
	
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    
    private static int SocketTimeout = 10000;//10秒  
    
    private static int ConnectTimeout = 10000;//10秒 
    
    private static Boolean SetTimeOut = true;

    private static ObjectMapper objectMapper = new ObjectMapper();
    
	private static Charset charset = Charset.forName("UTF-8");

    private static CloseableHttpClient getHttpClient(){
        return HttpClientBuilder.create().build();  
    }
    
    public static String post(String url,Map<String,Object> bodyData){
        String responseBody = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient  = getHttpClient();
            HttpPost httPost = new HttpPost(url);
            if (SetTimeOut) {
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(SocketTimeout)
                        .setConnectTimeout(ConnectTimeout).build();//设置请求和传输超时时间
                httPost.setConfig(requestConfig);
            }
            if(bodyData!=null&&bodyData.size()>0){
                httPost.setEntity(new StringEntity(objectMapper.writeValueAsString(bodyData)));
            }
            //请求数据
            CloseableHttpResponse response = httpClient.execute(httPost);  
            int status = response.getStatusLine().getStatusCode();  
            if (status == HttpStatus.SC_OK) {  
                HttpEntity entity = response.getEntity(); 
                //FIXME 暂时不从header读取
                responseBody = EntityUtils.toString(entity,charset); 
            } else {
            	logger.error("url:"+url+"--->http return status error:" + status);
            }  
        } catch (Exception e) {
        	logger.error("url:"+url+"--->http request error",e);
        } finally {  
            try {
            	if(httpClient!=null){httpClient.close();}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(ExceptionUtil.getErrorMessage(e));
			}  
        }  
        return responseBody;  
    }

    public static String get(String url){

        String respBody = null;
        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                respBody = EntityUtils.toString(entity,charset);
            }
        } catch (IOException e) {
            logger.error("url:"+url+"--->http request error",e);
        } finally {
            try {
                if(httpClient!=null){httpClient.close();}
            } catch (Exception e) {
                logger.error(ExceptionUtil.getErrorMessage(e));
            }
        }

        return respBody;
    }

}
