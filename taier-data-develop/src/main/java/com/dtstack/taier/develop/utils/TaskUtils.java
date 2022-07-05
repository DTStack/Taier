package com.dtstack.taier.develop.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dtstack.taier.develop.utils.develop.common.enums.Constant.CREATE_MODEL_TEMPLATE;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午1:03 2020/7/22
 * @Description：流计算任务工具类
 */
public class TaskUtils {

    public static final String SASL_JAAS_CONSTANT = "kafka.sasl.jaas.config";
    public static final String SECRET_CONSTANT = "secretKey";
    public static final String PASSWORD_CONSTANT = "password";
    public static final String SASL_JAAS_FLINK112_CONSTANT = "'kafka.sasl.jaas.config'";
    public static final String PASSWORD_FLINK112_CONSTANT = "'password'";
    public static final String SECRET_FLINK112_CONSTANT = "'secretKey'";

    private static final Logger logger = LoggerFactory.getLogger(TaskUtils.class);
    public static List<String> PASSWORD_KEYS = Arrays.asList("password", "pass", "secretkey", "hadoop.security.group.mapping.ldap.bind.password","sasl.jaas.config");

    private static final String TABLE_SPLIT_REGEX = "\\);";

    private static final String TABLE_WITH_SPLIT_REGEX = ",";

    //实时采集有密码的数据源的url可能的key集合
    private static List<String> URL_KEYS = Arrays.asList("jdbcUrl", "url", "connection", "broker");

    //匹配flinkSql中的密码(下面三个组合使用)
    private static final Pattern TABLE_USERNAME = Pattern.compile("(.*)(userName|username|'userName'|'username'|accessKey|'accessKey')\\s*=\\s*'(?<key>(.*))'\\s*");
    private static final Pattern TABLE_URL = Pattern.compile("(.*)(url|address|'url'|'address'|hostname|'hostname'|bootstrapServers|'bootstrapServers'|broker|'broker')\\s*=\\s*'(?<key>(.*))'\\s*");

    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("(?i)create\\s*(table|view)+", Pattern.CASE_INSENSITIVE);

    private static final Pattern MATCH_CREATE_TABLE_PATTERN = Pattern.compile("(?i)create\\s*(table|view)+\\s*(?<tablename>[0-9a-zA-Z_%\\.]+)\\s*", Pattern.CASE_INSENSITIVE);

    //数据源获取去除数据库的url
    private static final Pattern REMOVE_DB_URL = Pattern.compile("(?<jdbcUrl>jdbc:[a-zA-Z0-9.]+:(.*):(\\d+)).*");
    public static final List<String> DESENSITIZATION_KEYS = Arrays.asList("password","kafka.sasl.jaas.config","secretKey");

    /**
     * flinkSql任务脚本模式中密码脱敏处理
     * @param sqlText
     */
    public static String sqlPasswordFilter(String sqlText) {
        if (StringUtils.isBlank(sqlText)) {
            return sqlText;
        }
        //只有在创建表的语句中才会屏蔽密码
        for (String table : sqlText.split(TABLE_SPLIT_REGEX)) {
            Matcher matcher = CREATE_TABLE_PATTERN.matcher(table);
            if (matcher.find()) {
                String[] tableArray = table.split("(?i)\\)\\s*WITH\\s*\\(");
                if (tableArray.length > 1) {
                    String tableReplace ="";
                    String paramTable = tableArray[tableArray.length - 1];
                    String tempTable = tableArray[tableArray.length - 1];
                    for (int i = 0; i < DESENSITIZATION_KEYS.size(); i++) {
                        String key =DESENSITIZATION_KEYS.get(i);
                        if (paramTable.contains(key)) {
                            //flink1.12之前的版本
                            tableReplace = tempTable.replaceAll("(?i)" + key + "\\s*=\\s*'.*?'", key + " = '******'")
                                    //flink1.12的版本
                                    .replaceAll("(?i)'" + key + "'\\s*=\\s*'.*?'", "'" + key + "' = '******'")
                            ;
                            tempTable = tableReplace;
                        }
                    }
                    if (StringUtils.isNotBlank(tableReplace)) {
                        sqlText = sqlText.replace(paramTable, tableReplace);
                    }
                }
            }
        }
        return sqlText;
    }

    /**
     * 脚本模式脱敏后保存的密码变成"******"，进行按照原储存信息进行还原，依据是url+username
     * @param sqlText 更新后的脚本信息
     * @param specialTask 更新前的task信息
     */
    public static String resumeTemplatePwd(String sqlText, Task specialTask) {
        //更新前的脚本
        String sqlTextBefore = specialTask.getSqlText();
        if (StringUtils.isBlank(sqlText) || StringUtils.isBlank(sqlTextBefore)) {
            return sqlText;
        }
        //实时采集任务 - 实时采集带有createModel，job是String，且为base64加密，需要处理
        JSONObject sqlTextJsonObject = JSONObject.parseObject(sqlText);
        JSONObject sqlTextBeforeJsonObject = JSONObject.parseObject(sqlTextBefore);
        if (sqlTextJsonObject.get("job") == null || sqlTextBeforeJsonObject.get("job") == null) {
            return sqlText;
        }
        sqlText = sqlTextJsonObject.getString("job");
        sqlTextBefore = sqlTextBeforeJsonObject.getString("job");
        try {
            Object jsonData = JSONObject.parse(sqlText);
            //map储存原脚本中的url+username为key，password为value
            HashMap<String, String> passMap = new HashMap<>(7);
            //构建密码map - 以url+username作为key，password作为value
            dealDATemplatePwd(JSONObject.parse(sqlTextBefore), passMap, true);
            //对脱敏后保存的 ****** 密码进行复原
            dealDATemplatePwd(jsonData, passMap, false);
            JSONObject sql = new JSONObject(2);
            sql.put("job", jsonData.toString());
            sql.put("createModel", CREATE_MODEL_TEMPLATE);
            sqlText = sql.toJSONString();
        } catch (RdosDefineException e) {
            logger.error("连接密码未变更,{}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            //不做处理
            logger.error("实时采集脚本模式任务密码更新处理异常,{}", e.getMessage(), e);
        }
        return sqlText;
    }

    /**
     * 处理数据库连接的url，去除数据库信息
     * @param url
     * @return
     */
    public static String getDataSourceUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        Matcher mysqlUrl = REMOVE_DB_URL.matcher(url);
        if (mysqlUrl.find()) {
            url = mysqlUrl.group("jdbcUrl");
        }
        return url;
    }

    /**
     * 实时采集脚本模式对数据库中的sqlText进行解析出密码，以url+username作为key，password作为value
     * @param data
     * @param map 解析出的密码map集合
     * @param build 是否是构建map  false：进行密码复原
     * @return
     */
    public static void dealDATemplatePwd(Object data, Map<String, String> map, boolean build){
        if (data == null){
            return;
        }
        if (data instanceof JSONObject){
            for (String key : ((JSONObject)data).keySet()) {
                Object item = ((JSONObject)data).get(key);
                if (item instanceof JSONObject || item instanceof JSONArray){
                    dealDATemplatePwd(item, map, build);
                } else if(PASSWORD_KEYS.contains(key.toLowerCase())){
                    String pwd = ((JSONObject) data).get(key) == null ? null:(String) ((JSONObject) data).get(key);
                    String username = (String) ((JSONObject) data).get("username");
                    String url = "";
                    for (String urlKey : URL_KEYS) {
                        Object tmpU = ((JSONObject) data).get(urlKey);
                        if (tmpU != null){
                            if (tmpU instanceof JSONArray && ((JSONArray) tmpU).size() >0){
                                JSONObject conn = (JSONObject) ((JSONArray) tmpU).get(0);
                                Object jdbcUrl = conn.get("jdbcUrl");
                                if (jdbcUrl != null){
                                    if (jdbcUrl instanceof JSONArray) {
                                        url = (String) ((JSONArray) jdbcUrl).get(0);
                                    }
                                    break;
                                }
                            }else {
                                url = (String) tmpU;
                                break;
                            }
                        }
                    }
                    String removeDbUrl = getDataSourceUrl(url);
                    String passKey = String.format("url=%s&username=%s", removeDbUrl, username);
                    if (!build && StringUtils.isNotBlank(pwd) && "******".equals(pwd)) {
                        if (!map.containsKey(passKey)) {
                            throw new RdosDefineException(String.format("url为%s的数据源连接信息改变，请重新输入密码", url));
                        }
                        ((JSONObject) data).put(key, map.get(passKey));
                    } else {
                        map.put(passKey, pwd);
                    }
                }
            }
            return;
        }
        if(data instanceof JSONArray){
            for (Object datum : ((JSONArray) data)) {
                dealDATemplatePwd(datum, map, build);
            }
        }
    }

    /**
     * 检查字符串是否是 json 格式
     *
     * @param json json 字符串
     */
    public static void checkIsJSON(String json) {
        if (StringUtils.isBlank(json)) {
            return;
        }
        try {
            JSON.parseObject(json);
        } catch (Exception e) {
            throw new RdosDefineException("JSON解析失败，请检查文本是否是JSON格式", e);
        }
    }

    /**
     *  处理实时&离线任务参数
     */
    public static void dealWithTaskParam(TaskResourceParam task){
        if(task.getSettingMap() != null){
            if (Objects.equals(task.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())) {
                //实时任务参数处理
                task.getSettingMap().put("isStream",true);
                task.getSettingMap().put("isRestore",true);
            } else if (Objects.equals(task.getTaskType(), EScheduleJobType.SYNC.getVal())) {
                //离线任务参数处理
                task.getSettingMap().put("isStream",false);
                //离线任务FTP源处理，开启断点续传时必须指定恢复字段
                Integer type = Integer.parseInt(String.valueOf(task.getSourceMap().get("type")));
                if( Objects.equals(task.getSettingMap().get("isRestore") ,true) && Objects.equals(DataSourceType.FTP.getVal(),type)){
                    task.getSettingMap().put("restoreColumnName","");
                }
            }
        }

    }
}
