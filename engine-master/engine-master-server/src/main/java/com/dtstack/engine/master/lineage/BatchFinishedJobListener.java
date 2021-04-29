package com.dtstack.engine.master.lineage;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.vo.lineage.param.ParseColumnLineageParam;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.EngineTypeDataSourceType;
import com.dtstack.engine.dao.LineageDataSourceDao;
import com.dtstack.engine.master.event.ScheduleJobEventPublisher;
import com.dtstack.engine.lineage.enums.EngineTaskType2SourceType;
import com.dtstack.engine.lineage.impl.LineageService;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chener
 * @Classname BatchFinishedJobListener
 * @Description TODO
 * @Date 2020/12/11 13:51
 * @Created chener@dtstack.com
 */
@Service
public class BatchFinishedJobListener extends SqlJobFinishedListener {

    private static final Logger logger = LoggerFactory.getLogger(BatchFinishedJobListener.class);

    private static final Pattern USE_DB_PATTERN = Pattern.compile("(?i)\\s*use\\s+(?<db>[a-zA-Z0-9_]+\\s*)");

    @Autowired
    private LineageService lineageService;

    @Autowired
    private LineageDataSourceDao lineageDataSourceDao;

    @Override
    protected void onFocusedJobFinished(Integer type,String engineType,String sqlText,Long  taskId, ScheduleJob scheduleJob, Integer status) {
        //解析sql并存储
        sqlText = sqlText.replaceAll("--.*","");
        List<String> sqls = SqlFormatUtil.splitSqlText(sqlText);
        if (CollectionUtils.isEmpty(sqls)){
            return;
        }
        //离线第一条sql为use db
        String useDbSql = sqls.get(0);
        Matcher matcher = USE_DB_PATTERN.matcher(useDbSql);
        String defaultDb = "";
        if (matcher.matches()){
            defaultDb = matcher.group("db");
            sqls.remove(0);
        }else {
            logger.info("sql不正确{}",useDbSql);
            return;
        }
        DataSourceType dataSourceTypeByTaskTypeInt;
        if(EScheduleType.TEMP_JOB.getType() == type){
            EngineType eType = EngineType.getEngineType(engineType);
            dataSourceTypeByTaskTypeInt = EngineTypeDataSourceType.getDataSourceTypeByTaskType(eType);
        }else{
            dataSourceTypeByTaskTypeInt = EngineTaskType2SourceType.getDataSourceTypeByTaskTypeInt(scheduleJob.getTaskType());
        }
        if(null == dataSourceTypeByTaskTypeInt){
            logger.error("do not support ,engineType:{},taskType:{}",engineType,scheduleJob.getTaskType());
            return;
        }
        List<Integer> hadoopList = Arrays.asList(DataSourceType.HIVE.getVal(), DataSourceType.IMPALA.getVal(), DataSourceType.SPARKTHRIFT2_1.getVal());
        if(hadoopList.contains(dataSourceTypeByTaskTypeInt.getVal())){
            LineageDataSource dataSource = lineageDataSourceDao.getHadoopDefaultDatasource(scheduleJob.getProjectId(),scheduleJob.getAppType(),hadoopList);
            dataSourceTypeByTaskTypeInt = DataSourceType.getSourceType(dataSource.getSourceType());
        }
        for (String sql : sqls){
            ParseColumnLineageParam columnLineageParam = new ParseColumnLineageParam();
            columnLineageParam.setAppType(AppType.RDOS.getType());
            columnLineageParam.setDataSourceType(dataSourceTypeByTaskTypeInt.getVal());
            columnLineageParam.setDefaultDb(defaultDb);
            columnLineageParam.setDtUicTenantId(scheduleJob.getDtuicTenantId());
            columnLineageParam.setUniqueKey(null== taskId? null:String.valueOf(taskId));
            columnLineageParam.setType(type);
            columnLineageParam.setSql(sql);
            columnLineageParam.setVersionId(scheduleJob.getVersionId());
            logger.info("调用字段血缘解析:{}", JSON.toJSON(columnLineageParam));
            lineageService.parseAndSaveColumnLineage(columnLineageParam);
        }
    }

    @Override
    public Set<EScheduleJobType> focusedJobTypes() {
        return Sets.newHashSet(EScheduleJobType.SPARK_SQL,EScheduleJobType.HIVE_SQL,EScheduleJobType.LIBRA_SQL,EScheduleJobType.IMPALA_SQL);
    }

    @Override
    public AppType focusedAppType() {
        return AppType.RDOS;
    }

    @PostConstruct
    public void registerEvent(){
        ScheduleJobEventPublisher.getInstance().register(this);
    }
}
