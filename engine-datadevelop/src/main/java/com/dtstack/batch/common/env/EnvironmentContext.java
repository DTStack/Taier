package com.dtstack.batch.common.env;

import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


/**
 * @author sishu.yss
 */
@Component
@Data
public class EnvironmentContext {

    private static Properties prop;

    static {
        prop = new Properties();
        try {
            prop.load(new InputStreamReader(new FileInputStream(System.getProperty("user.dir") + "/conf/application.properties"), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new DtCenterDefException(String.format("读取配置文件异常,Caused by: %s", e.getMessage()), e);
        }
    }

    @Value("${notify.sendtype.phone:false}")
    private Boolean notifyPhone;

    @Value("${create.table.type:parquet}")
    private String createTableType;

    @Value("${http.port:9020}")
    private Integer httpPort;

    @Value("${http.address:0.0.0.0}")
    private String httpAddress;

    @Value("${hadoop.user.name:admin}")
    private String hadoopUserName;

    @Value("${hdfs.batch.path:/dtInsight/batch/}")
    private String hdfsBatchPath;

    @Value("${dtuic.url}")
    private String dtUicUrl;

    @Value("${public.service.node}")
    private String publicServiceNode;

    @Value("${sync.log.promethues:true}")
    private Boolean syncLogPromethues;

    @Value("${kerberos.local.path:}")
    private String kerberosLocalPath;

    public String getKerberosLocalPath() {
        return StringUtils.isNotBlank(kerberosLocalPath) ? kerberosLocalPath : String.format("%s%s%s",
                System.getProperty("user.dir"), File.separator, "kerberosConf");
    }

    @Value("${kerberos.template.path:}")
    private String kerberosTemplatePath;

    public String getKerberosTemplatePath() {
        return  StringUtils.isNotBlank(kerberosLocalPath) ? kerberosTemplatePath : String.format("%s%s%s%s%s",
                System.getProperty("user.dir"), File.separator, "conf", File.separator, "kerberos");
    }

    /**
     * 获取告警通知 title
     *
     * @return 告警通知 title
     */
    public String getAlarmTitle() {
        return prop.getProperty("notify.title","袋鼠云数栈");
    }

    @Value("${temp.table.lifecycle:1440}")
    private Integer tempTableLifecycle;

    @Value("${delete.life.time:7}")
    private Integer deleteLifeTime;

    @Value("${explain.enable:true}")
    private Boolean explainEnable;

    @Value("${table.limit:200}")
    private Integer tableLimit;

    /**
     * 小文件合并的备份文件删除时间，单位：天
     */
    @Value("${delete.merge.file.time:7}")
    private Long deleteMergeFileTime;

    @Value("${sdk.token}")
    private String sdkToken;

    /**
     * 数据保留天数
     */
    @Value("${data.keepDay:180}")
    private Long dataKeepDay;
}