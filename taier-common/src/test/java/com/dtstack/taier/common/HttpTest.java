/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.common;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTest.class);


    private void config(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        httpRequestBase.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");//"en-US,en;q=0.5");
        httpRequestBase.setHeader("Accept-Charset", "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(3000)
                .setSocketTimeout(3000)
                .build();
        httpRequestBase.setConfig(requestConfig);
    }

    @Test
    public void testHttp() {
//        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
//        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
//        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//                .register("http", plainsf)
//                .register("https", sslsf)
//                .build();
//
//        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
//        // 将最大连接数增加到200
//        cm.setMaxTotal(200);
//        // 将每个路由基础的连接增加到20
//        cm.setDefaultMaxPerRoute(20);
//
//        // 将目标主机的最大连接数增加到50
//        HttpHost localhost = new HttpHost("http://blog.csdn.net/gaolu",80);
//        cm.setMaxPerRoute(new HttpRoute(localhost), 50);
//
//        //请求重试处理
//        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
//            public boolean retryRequest(IOException exception,int executionCount, HttpContext context) {
//                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
//                    return false;
//                }
//                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
//                    return true;
//                }
//                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
//                    return false;
//                }
//                if (exception instanceof InterruptedIOException) {// 超时
//                    return false;
//                }
//                if (exception instanceof UnknownHostException) {// 目标服务器不可达
//                    return false;
//                }
//                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
//                    return false;
//                }
//                if (exception instanceof SSLException) {// ssl握手异常
//                    return false;
//                }
//
//                HttpClientContext clientContext = HttpClientContext.adapt(context);
//                HttpRequest request = clientContext.getRequest();
//                // 如果请求是幂等的，就再次尝试
//                if (!(request instanceof HttpEntityEnclosingRequest)) {
//                    return true;
//                }
//                return false;
//            }
//        };
//
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setConnectionManager(cm)
//                .setRetryHandler(httpRequestRetryHandler)
//                .build();
//
//        // URL列表数组
//        String[] urisToGet = {
//                "http://blog.csdn.net/gaolu/article/details/48466059",
//                "http://blog.csdn.net/gaolu/article/details/48243103",
//                "http://blog.csdn.net/gaolu/article/details/39499073",
//                "http://blog.csdn.net/gaolu/article/details/39314327",
//                "http://blog.csdn.net/gaolu/article/details/38820809",
//                "http://blog.csdn.net/gaolu/article/details/38439375",
//        };
//
//        long start = System.currentTimeMillis();
//        try {
//            int pagecount = urisToGet.length;
//            ExecutorService executors = Executors.newFixedThreadPool(pagecount);
//            CountDownLatch countDownLatch = new CountDownLatch(pagecount);
//            for(int i = 0; i< pagecount;i++){
//                HttpGet httpget = new HttpGet(urisToGet[i]);
//                config(httpget);
//
//                //启动线程抓取
//                executors.execute(new GetRunnable(httpClient,httpget,countDownLatch));
//            }
//
//            countDownLatch.await();
//            executors.shutdown();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            logger.info("thread " + Thread.currentThread().getName() + "," + System.currentTimeMillis() + ", 所有线程已完成，开始进入下一步！");
//        }
//
//        long end = System.currentTimeMillis();
//        logger.info("consume -> " + (end - start));
//    }
//
//    static class GetRunnable implements Runnable {
//        private CountDownLatch countDownLatch;
//        private final CloseableHttpClient httpClient;
//        private final HttpGet httpget;
//
//        public GetRunnable(CloseableHttpClient httpClient, HttpGet httpget, CountDownLatch countDownLatch){
//            this.httpClient = httpClient;
//            this.httpget = httpget;
//
//            this.countDownLatch = countDownLatch;
//        }
//
//        @Override
//        public void run() {
//            CloseableHttpResponse response = null;
//            try {
//                response = httpClient.execute(httpget,HttpClientContext.create());
//                HttpEntity entity = response.getEntity();
//                logger.info(EntityUtils.toString(entity, "utf-8")); ;
//                EntityUtils.consume(entity);
//            } catch (IOException e) {
//                logger.error("", e);
//            } finally {
//                countDownLatch.countDown();
//
//                try {
//                    if(response != null)
//                        response.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}