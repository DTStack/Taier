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

package com.dtstack.taier.develop.utils.develop.sync.job;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.common.template.CheckFormat;
import com.dtstack.taier.develop.enums.develop.TaskCreateModelType;
import com.dtstack.taier.develop.utils.develop.sync.template.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class SyncJobCheck {

    private static Map<String,String> jobFormat;

    static {
        jobFormat = new LinkedHashMap<>();
        jobFormat.put(JobElementPath.JOB,"模板必须包含 [job] 属性");
        jobFormat.put(JobElementPath.SETTING,"job 对象中必须包含 [setting] 属性");
        jobFormat.put(JobElementPath.CONTENT_ARRAY,"job 对象中必须包含 [content] 属性");
        jobFormat.put(JobElementPath.CONTENT_FIRST,"content 数组不能为空");
        jobFormat.put(JobElementPath.READER,"content 对象必须包含 [reader] 属性");
        jobFormat.put(JobElementPath.READER_NAME,"reader 对象必须包含插件名称 [name]");
        jobFormat.put(JobElementPath.READER_PARAMETER,"reader 对象必须包含参数 [parameter]");
        jobFormat.put(JobElementPath.WRITER,"content 对象必须包含 [writer] 属性");
        jobFormat.put(JobElementPath.WRITER_NAME,"writer 对象必须包含插件名称 [name]");
        jobFormat.put(JobElementPath.WRITER_PARAMETER,"writer 对象必须包含参数 [parameter]");
    }

    /**
     * 校验脚本模式下的 job 格式是否正确
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

            checkJobData(job);

            // 检查各个reader和writer的正确性
            if (TaskCreateModelType.GUIDE.getType().equals(createModelType)) {
                checkReader((JSONObject) JSONPath.eval(job, JobElementPath.READER));
                checkRwiter((JSONObject) JSONPath.eval(job, JobElementPath.WRITER));
            }
        } catch (JSONException e) {
            throw new RdosDefineException(String.format("json格式解析失败，原因是: %s", e.getMessage()));
        }
    }

    private static void checkReader(JSONObject reader){
        CheckFormat checkFormat;
        String readerName = reader.getString("name");
        switch (readerName){
            case PluginName.MySQL_R :
            case PluginName.MySQLD_R :
            case PluginName.Oracle_R :
            case PluginName.SQLServer_R :
            case PluginName.DB2_R :
            case PluginName.GBase_R :
            case PluginName.Clickhouse_R:
            case PluginName.Polardb_for_MySQL_R:
            case PluginName.PostgreSQL_R :
            case PluginName.DM_R:
            case PluginName.GREENPLUM_R:
            case PluginName.KINGBASE_R :
            case PluginName.Phoenix_R :
            case PluginName.ADB_FOR_PG_R:
            case PluginName.Phoenix5_R : checkFormat = new RDBReader(); break;
            case PluginName.ES_R : checkFormat = new EsReader(); break;
            case PluginName.HDFS_R : checkFormat = new HDFSReader(); break;
            case PluginName.HBase_R : checkFormat = new HBaseReader(); break;
            case PluginName.FTP_R : checkFormat = new FtpReader(); break;
            case PluginName.MongoDB_R : checkFormat = new MongoDbReader(); break;
            case PluginName.ODPS_R : checkFormat = new OdpsReader(); break;
            case PluginName.Stream_R : checkFormat = new StreamReader(); break;
            case PluginName.CarbonData_R : checkFormat = new CarbonDataReader(); break;
            case PluginName.Kudu_R : checkFormat = new KuduReader(); break;
            case PluginName.AWS_S3_R : checkFormat = new AwsS3Reader(); break;
            case PluginName.InfluxDB_R : checkFormat = new InfluxDBReader(); break;

            default: throw new RdosDefineException("未知的reader插件类型:" + readerName);
        }
        checkFormat.checkFormat(reader);
    }

    private static void checkRwiter(JSONObject writer){
        CheckFormat checkFormat;
        String writerName = writer.getString("name");
        switch (writerName){
            case PluginName.MySQL_W:
            case PluginName.Oracle_W :
            case PluginName.SQLServer_W :
            case PluginName.DB2_W :
            case PluginName.GBase_W :
            case PluginName.Clichhouse_W:
            case PluginName.Polardb_for_MySQL_W:
            case PluginName.PostgreSQL_W :
            case PluginName.DM_W:
            case PluginName.GREENPLUM_W:
            case PluginName.KINGBASE_W:
            case PluginName.Phoenix_W :
            case PluginName.ADB_FOR_PG_W:
            case PluginName.Phoenix5_W : checkFormat = new RDBWriter(); break;
            case PluginName.ES_W : checkFormat = new EsWriter(); break;
            case PluginName.HDFS_W : checkFormat = new HDFSWriter(); break;
            case PluginName.HBase_W : checkFormat = new HBaseWriter(); break;
            case PluginName.FTP_W : checkFormat = new FtpWriter(); break;
            case PluginName.MongoDB_W : checkFormat = new MongoDbWriter(); break;
            case PluginName.ODPS_W : checkFormat = new OdpsWriter(); break;
            case PluginName.Redis_W : checkFormat = new RedisWriter(); break;
            case PluginName.Stream_W : checkFormat = new StreamWriter(); break;
            case PluginName.CarbonData_W : checkFormat = new CarbonDataWriter(); break;
            case PluginName.Kudu_W : checkFormat = new KuduWriter(); break;
            case PluginName.AWS_S3_W : checkFormat = new AwsS3Writer(); break;
            case PluginName.INCEPTOR_W: checkFormat = new InceptorWriter(); break;

            default: throw new RdosDefineException("未知的writer插件类型:" + writerName);
        }
        checkFormat.checkFormat(writer);
    }

    /**
     * 检测 job 参数的合理性
     * @param job
     */
    private static void checkJobData(JSONObject job){
        if (JSONPath.eval(job, JobElementPath.SPEED) != null){
            long speed = Long.parseLong(String.valueOf(JSONPath.eval(job, JobElementPath.SPEED)));
            //-1是不限制速度
            if (speed < -1 ){
                throw new RdosDefineException("速率 bytes 必须大于 0");
            }
        }

        if(JSONPath.eval(job, JobElementPath.CHANNEL) != null){
            long channel = Long.parseLong(String.valueOf(JSONPath.eval(job, JobElementPath.CHANNEL)));
            if (channel <= 0 ){
                throw new RdosDefineException("并发度 channel 必须大于 0");
            }
            String name = (String) JSONPath.eval(job, "$.job.content[0].writer.name");
            if (PluginName.Clichhouse_W.equals(name)) {
                // 2、clickhouse不支持并发写入，当写入clickhouse时，作业并发数只能设置为1
                if (1 != channel) {
                    throw new DtCenterDefException("clickhouse 作业并发数只能设置为1");
                }
            }

        }

        if(JSONPath.eval(job, JobElementPath.RECORD) != null){
            long record = Long.parseLong(String.valueOf(JSONPath.eval(job, JobElementPath.RECORD)));
            if (record < 0){
                throw new RdosDefineException("错误记录数 record 必须大于等于 0");
            }
        }

        Object percentageObj = JSONPath.eval(job, JobElementPath.PERCENTAGE);
        if (percentageObj!=null){
            Double percentage = NumberUtils.toDouble(percentageObj.toString(),-1);
            if(percentage < 0 || percentage > 100){
                throw new RdosDefineException("错误比率 percentage 必须介于 0-100");
            }
        }

    }
}
