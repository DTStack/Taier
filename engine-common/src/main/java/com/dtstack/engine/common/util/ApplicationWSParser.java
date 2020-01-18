package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.commons.math3.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * yarn api application 返回的xml解析
 * Date: 2019/7/24
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ApplicationWSParser {

    private static final String AM_ROOT_TAG = "app";
    private static final String AM_CONTAINER_LOGS_TAG = "amContainerLogs";

    private static final Pattern ERR_INFO_BYTE_PATTERN = Pattern.compile("(\\d+)\\s*bytes");

    public static String getAMContainerLogsURL(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        JSONObject roogEle = jsonObject.getJSONObject(AM_ROOT_TAG);
        String amContainerLogsEle = roogEle.getString(AM_CONTAINER_LOGS_TAG);
        return amContainerLogsEle;
    }

    public static Pair<String, String> parserAMContainerPreViewHttp(String httpText, String preURL){
        org.jsoup.nodes.Document document = Jsoup.parse(httpText);
        Elements el = document.getElementsByClass("content");
        if(el.size() < 1){
            throw new RdosDefineException("httpText don't have any ele in http class : content");
        }

        Elements afs = el.get(0).select("a[href]");
        if (afs.size() < 1) {
            throw new RdosDefineException("httpText data format not correct, please check task status");
        }
        String amErrURL = afs.get(1).attr("href");

        //截取url参数部分
        amErrURL = amErrURL.substring(0, amErrURL.indexOf("?"));
        amErrURL = preURL + amErrURL;

        String jobErrByteStr = afs.get(1).text();

        String infoTotalBytes = "0";
        Matcher matcher = ERR_INFO_BYTE_PATTERN.matcher(jobErrByteStr);
        if(matcher.find()){
            infoTotalBytes = matcher.group(1);
        }

        return new Pair<>(amErrURL, infoTotalBytes);
    }
}
