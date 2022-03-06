package com.dtstack.taier.develop.service.template;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.common.template.CheckFormat;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taier.develop.utils.develop.sync.template.MongoDbReader;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.dtstack.taier.develop.utils.develop.common.enums.Constant.CREATE_MODEL_GUIDE;
import static com.dtstack.taier.develop.utils.develop.sync.template.JobElementPath.CONTENT_ARRAY;
import static com.dtstack.taier.develop.utils.develop.sync.template.JobElementPath.CONTENT_FIRST;
import static com.dtstack.taier.develop.utils.develop.sync.template.JobElementPath.JOB;
import static com.dtstack.taier.develop.utils.develop.sync.template.JobElementPath.READER;
import static com.dtstack.taier.develop.utils.develop.sync.template.JobElementPath.READER_NAME;
import static com.dtstack.taier.develop.utils.develop.sync.template.JobElementPath.READER_PARAMETER;
import static com.dtstack.taier.develop.utils.develop.sync.template.JobElementPath.WRITER;
import static com.dtstack.taier.develop.utils.develop.sync.template.JobElementPath.WRITER_NAME;
import static com.dtstack.taier.develop.utils.develop.sync.template.JobElementPath.WRITER_PARAMETER;

/**
 * @author sanyue
 * @date 2018/9/13
 */
public class DaJobCheck {
    private static Map<String, String> jobFormat;


    static {
        jobFormat = new LinkedHashMap<>();
        jobFormat.put(JOB, "模板必须包含 [job] 属性");
        jobFormat.put(CONTENT_ARRAY, "job 对象中必须包含 [content] 属性");
        jobFormat.put(CONTENT_FIRST, "content 数组不能为空");
        jobFormat.put(READER, "content 对象必须包含 [reader] 属性");
        jobFormat.put(READER_NAME, "reader 对象必须包含插件名称 [name]");
        jobFormat.put(READER_PARAMETER, "reader 对象必须包含参数 [parameter]");
        jobFormat.put(WRITER, "content 对象必须包含 [writer] 属性");
        jobFormat.put(WRITER_NAME, "writer 对象必须包含插件名称 [name]");
        jobFormat.put(WRITER_PARAMETER, "writer 对象必须包含参数 [parameter]");
    }

    /**
     * 校验脚本模式下的 job 格式是否正确
     *
     * @param jobJsonStr
     */
    public static void checkJobFormat(String jobJsonStr, Integer createModelType){
        try {
            if (StringUtils.isEmpty(jobJsonStr)) {
                throw new RdosDefineException("job内容不能为空");
            }

            // 检测 job 的完整性
            JSONObject job = JSONObject.parseObject(jobJsonStr);
            jobFormat.forEach((path, error) -> {
                if (!JSONPath.contains(job, path)) {
                    throw new RdosDefineException(error);
                }
            });
            if (CREATE_MODEL_GUIDE ==createModelType) {
                // 检查各个reader和writer的正确性
                checkReader((JSONObject) JSONPath.eval(job, READER));
                checkWriter((JSONObject) JSONPath.eval(job, WRITER));
            }
        } catch (JSONException e) {
            throw new RdosDefineException("json格式解析失败，e:" + e.getMessage(), e);
        }
    }
//todo
    private static void checkReader(JSONObject reader) {
        CheckFormat checkFormat;
        String readerName = reader.getString("name");
        switch (readerName) {
            case PluginName.BINLOG_R:
            case PluginName.MYSQL_CDC_R:
                checkFormat = new MysqlBinLogReader();
                break;
            case PluginName.KAFKA_09_R:
                checkFormat = new Kafka09Reader();
                break;
            case PluginName.KAFKA_R:
            case PluginName.KAFKA_10_R:
            case PluginName.KAFKA_11_R:
                checkFormat = new KafkaReader();
                break;
            case PluginName.ORACLE_BINLOG_R:
                checkFormat = new OracleBinLogReader();
                break;
            case PluginName.ORACLE_POLL_R:
                checkFormat = new OraclePollReader();
                break;
            case PluginName.MYSQL_POLL_R:
                checkFormat = new MysqlPollReader();
                break;
            case PluginName.SQLSERVER_CDC_R:
                checkFormat = new SqlServerCdcReader();
                break;
            case PluginName.SQLSERVER_POLL_R:
                checkFormat = new SqlServerPollReader();
                break;
            case PluginName.FTP_R:
                checkFormat = new FTPReader();
                break;
            case PluginName.ES7_R:
                checkFormat = new Es7Reader();
                break;
            case PluginName.SOLR_R:
                checkFormat = new SolrReader();
                break;
            case PluginName.MySQLD_R:
                checkFormat = new MysqlPollReader();
                break;
            case PluginName.PGWAL_R:
                checkFormat = new PostGreSqlCdcReader();
                break;
            case PluginName.PostgreSQL_R:
                checkFormat = new PostGreSqlPollReader();
            case PluginName.MONGODB_R:
                checkFormat = new MongoDbReader();
                break;
            default:
                throw new RdosDefineException("未知的reader插件类型:" + readerName);
        }
//        checkFormat.checkFormat(reader);
    }

//todo
    private static void checkWriter(JSONObject writer) {
        CheckFormat checkFormat;
        String readerName = writer.getString("name");
        switch (readerName) {
            case PluginName.KAFKA_W:
            case PluginName.KAFKA_09_W:
            case PluginName.KAFKA_10_W:
            case PluginName.KAFKA_11_W:
                checkFormat = new KafkaWriter();
                break;
            case PluginName.HDFS_W:
                checkFormat = new HdfsWriter();
                break;
            case PluginName.HIVE_W:
                checkFormat = new Hive2XWriter();
                break;
            case PluginName.FTP_W:
                checkFormat = new FTPWriter();
                break;
            case PluginName.ES7_W:
                checkFormat = new Es7Writer();
                break;
            case PluginName.SOLR_W:
                checkFormat = new SolrWriter();
                break;
            case PluginName.MySQL_W:
                checkFormat = new MySQLWriter();
                break;
            case PluginName.Oracle_W:
                checkFormat = new OracleWriter();
                break;
            case PluginName.SQLSERVER_W:
                checkFormat = new SqlServerWriter();
                break;
            case PluginName.POSTGRESQL_W:
                checkFormat = new PostgreSQLWriter();
                break;
            case PluginName.DORIS_RESTFUL_W:
                checkFormat = new DorisRestfulWriter();
            case PluginName.MONGODB_W:
                checkFormat = new MongoDbWriter();
                break;
            default:
                throw new UnsupportedOperationException();
        }
//        checkFormat.checkFormat(writer);
    }
}
