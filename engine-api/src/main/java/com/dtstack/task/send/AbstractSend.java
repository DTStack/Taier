package com.dtstack.task.send;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.exception.ExceptionEnums;
import com.dtstack.dtcenter.common.http.PoolHttpClient;
import com.dtstack.dtcenter.common.util.AddressUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yuebai
 * @date 2019-10-28
 */
public abstract class AbstractSend {

    private static String protocol = "http://%s%s";

    public static int retryCount = 3;

    private volatile Set<String> availableNodes = new CopyOnWriteArraySet();

    private volatile Set<String> disableNodes = new CopyOnWriteArraySet();

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    private static Random random = new Random();

    public static Logger logger = LoggerFactory.getLogger(AbstractSend.class);

    private int APP_TYPE = 0;

    public AbstractSend() {
    }

    public AbstractSend(String nodes) {
        if (StringUtils.isBlank(nodes)) {
            throw new DtCenterDefException("node is null...");
        }
        logger.info("nodes--->{}", nodes);
        availableNodes.addAll(Arrays.asList(nodes.split(",")));
        executor.submit(new AbstractSend.MonitorNode());
    }

    public AbstractSend(String nodes, int appType) {
        if (StringUtils.isBlank(nodes)) {
            throw new DtCenterDefException("node is null...");
        }
        logger.info("nodes--->{}", nodes);
        availableNodes.addAll(Arrays.asList(nodes.split(",")));
        executor.submit(new AbstractSend.MonitorNode());
        this.APP_TYPE = appType;
    }

    public String post(String url, String body, Map<String, Object> cookies, Integer retry) {

        if (retry != null && retry == 0) {
            throw new DtCenterDefException("调用次数 必须 > 0 ,请检查!!!");
        }

        int size = availableNodes.size();
        if (availableNodes.size() == 0) {
            throw new DtCenterDefException("can not find any available node...");
        }
        Object bodyObj = JSON.parse(body);
        if (bodyObj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) bodyObj;
            jsonObject.put("appType", this.APP_TYPE);
            body = jsonObject.toJSONString();
        }
        try {
            int index = random.nextInt(size);
            String uri = Lists.newArrayList(availableNodes).get(index);
            String real = String.format(protocol, uri, url);
            String response = null;
            int count = retry == null ? retryCount : retry;
            for (int i = 0; i < count; i++) {
                response = PoolHttpClient.post(real, body, cookies);
                if (StringUtils.isNotBlank(response)) {
                    break;
                }

                //如果可用的地址只剩下最后一个不移除：防止其他请求来的时候获取不到任务地址
                if (i == count - 1 && availableNodes.size() > 1) {
                    availableNodes.remove(uri);
                    disableNodes.add(uri);
                    return post(url, body, cookies, retry);
                }
                Thread.sleep(2000);
            }

            if (StringUtils.isBlank(response)) {
                throw new DtCenterDefException("network error...");
            }

            JSONObject data = JSONObject.parseObject(response);

            if (!"1".equals(data.getString("code"))) {
                final int code = data.getIntValue("code");
                logger.error(data.getString("message"));
                ExceptionEnums exEnum = new ExceptionEnums() {
                    @Override
                    public int getCode() {
                        return code;
                    }

                    @Override
                    public String getDescription() {
                        return null;
                    }
                };
                throw new DtCenterDefException(data.getString("message"), exEnum);
            }

            return data.getString("data");
        } catch (Exception e) {
            logger.error(String.format("%s---->%s:", url, body), e);
            throw new DtCenterDefException(e.getMessage(), e instanceof DtCenterDefException ? ((DtCenterDefException) e).getErrorCode() : null);
        }
    }

    private class MonitorNode implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("availableNodes--->{},disableNodes---->{}", availableNodes, disableNodes);
                    }
                    if (disableNodes.size() > 0) {
                        Iterator<String> iterators = disableNodes.iterator();
                        while (iterators.hasNext()) {
                            String uri = iterators.next();
                            String[] up = uri.split(":");
                            if (AddressUtil.telnet(up[0], Integer.parseInt(up[1]))) {
                                availableNodes.add(uri);
                                disableNodes.remove(uri);
                            }
                        }
                    }
                    Thread.sleep(5000);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    public Set<String> getAvailableNodes() {
        return availableNodes;
    }

    public void setAvailableNodes(Set<String> availableNodes) {
        this.availableNodes = availableNodes;
    }
}