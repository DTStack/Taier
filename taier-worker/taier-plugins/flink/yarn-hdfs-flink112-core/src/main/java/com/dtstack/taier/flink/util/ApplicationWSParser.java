package com.dtstack.taier.flink.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.base.util.HttpClientUtil;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.flink.config.FlinkConfig;
import com.dtstack.taier.flink.constant.ConfigConstant;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
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
    public static final String AM_ROOT_TAG = "app";
    public static final String AM_STATUE = "state";
    public static final String AM_CONTAINER_LOGS_TAG = "amContainerLogs";
    public static final String AM_USER_TAG = "user";
    public static final String TRACKING_URL = "trackingUrl";

    private static final Pattern ERR_INFO_BYTE_PATTERN = Pattern.compile("(?<name>[^:]+):+\\s+[a-zA-Z\\s]+(\\d+)\\s*bytes");
    private static final String LOG_FILE_REGEX = "[a-zA-Z]+\\.(log|err|out)";

    public final Map<String, String> amParams;

    public ApplicationWSParser(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        JSONObject rootEle = jsonObject.getJSONObject(AM_ROOT_TAG);
        this.amParams = rootEle.toJavaObject(Map.class);
    }

    public String getParamContent(String key) {
        return amParams.get(key);
    }


    public RollingBaseInfo parseContainerLogBaseInfo(FlinkConfig base, String containerLogsURL, String preURL, String componen, Configuration yarnConfig) throws Exception {
        String amContainerPreViewHttp = ApplicationWSParser.getDataFromYarnRest(base, yarnConfig, containerLogsURL);
        org.jsoup.nodes.Document document = Jsoup.parse(amContainerPreViewHttp);
        Elements el = document.getElementsByClass("content");
        if (el.size() < 1) {
            throw new RuntimeException("httpText don't have any ele in http class : content");
        }

        Elements afs = el.get(0).select("a[href]");
        if (afs.size() < 1) {
            throw new RuntimeException("httpText data format not correct, please check task status or url response content!!");
        }

        RollingBaseInfo rollingBaseInfo = new RollingBaseInfo();
        rollingBaseInfo.setTypeName(componen);

        for (org.jsoup.nodes.Element af : afs) {
            String logURL = af.attr("href");
            //截取url参数部分
            logURL = logURL.substring(0, logURL.indexOf("?"));
            logURL = preURL + logURL;

            String jobErrByteStr = af.text();

            String infoTotalBytes = "0";
            String logName = "";

            Matcher matcher = ERR_INFO_BYTE_PATTERN.matcher(jobErrByteStr);
            if (matcher.find()) {
                logName = StringUtils.trimToEmpty(matcher.group(1));

                infoTotalBytes = matcher.group(2);
            }

            if (logName.matches(LOG_FILE_REGEX)) {
                rollingBaseInfo.addLogBaseInfo(new LogBaseInfo(logName, logURL, infoTotalBytes));
            }
        }
        return rollingBaseInfo;
    }

    public class LogBaseInfo {
        String name;
        String url;
        String totalBytes;

        public LogBaseInfo() {

        }

        public LogBaseInfo(String name, String url, String totalBytes) {
            this.name = name;
            this.url = url;
            this.totalBytes = totalBytes;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTotalBytes() {
            return totalBytes;
        }

        public void setTotalBytes(String totalBytes) {
            this.totalBytes = totalBytes;
        }

        @Override
        public String toString() {
            return "LogBaseInfo{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", totalBytes=" + totalBytes +
                    '}';
        }
    }

    public class RollingBaseInfo {
        String typeName;
        List<LogBaseInfo> logs = Lists.newArrayList();
        String otherInfo;

        public RollingBaseInfo() {

        }

        public RollingBaseInfo(String typeName, List<LogBaseInfo> logs) {
            this.typeName = typeName;
            this.logs = logs;
        }

        public void addLogBaseInfo(LogBaseInfo logBaseInfo) {
            this.logs.add(logBaseInfo);
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public List<LogBaseInfo> getLogs() {
            return logs;
        }

        public void setLogs(List<LogBaseInfo> logs) {
            this.logs = logs;
        }

        public String getOtherInfo() {
            return otherInfo;
        }

        public void setOtherInfo(String otherInfo) {
            this.otherInfo = otherInfo;
        }
    }

    public static String getDataFromYarnRest(FlinkConfig flinkConfig, Configuration yarnConfig, String url) throws Exception {
        return KerberosUtils.login(flinkConfig, () -> {
            String token = yarnConfig.get(ConfigConstant.HTTP_AUTHENTICATION_TOKEN_KEY);
            Header[] headers = {};
            if (StringUtils.isNotEmpty(token)) {
                String authKey = "Authorization";
                String authValue = String.format("Bearer %s", token);
                headers = new Header[]{new BasicHeader(authKey, authValue)};
            }

            String response = "";
            try {
                response = HttpClientUtil.builder().setBaseConfig(flinkConfig)
                        .build()
                        .get(url, headers);
            } catch (Exception e) {
                throw new PluginDefineException(e);
            }
            return response;
        }, yarnConfig);
    }
}
