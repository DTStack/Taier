package com.dtstack.engine.master.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.enums.DictType;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yuebai
 * @date 2021-07-01
 */
@Configuration
@Component
public class DataClearSchedule {


    private static final Logger LOGGER = LoggerFactory.getLogger(DataClearSchedule.class);
    private static final String clearFlag = "clearFlag";
    private static final String clearDateConfig = "clearDateConfig";
    private static final String deleteDateConfig = "deleteDateConfig";
    private static final String appendWhere = "appendWhere";
    private static final String increment = "increment";
    private static final String directDelete = "directDelete";
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    @Autowired
    private EnvironmentContext environment;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ScheduleDictDao scheduleDictDao;

    public void setIsMaster(boolean isMaster) {
        RUNNING.set(isMaster);
    }

    @Scheduled(cron = "${data.clear.cron:0 0 23 * * ? }")
    public void process() {
        if (!environment.openDataClear()) {
            return;
        }
        if (!RUNNING.get()) {
            LOGGER.info("DataClearSchedule node is not master");
            return;
        }
        List<ScheduleDict> scheduleDicts = scheduleDictDao.listDictByType(DictType.DATA_CLEAR_NAME.type);
        if (CollectionUtils.isEmpty(scheduleDicts)) {
            LOGGER.info("data clear table is empty");
            return;
        }
        CompletableFuture.runAsync(()-> dataClear(scheduleDicts));
    }

    private void dataClear(List<ScheduleDict> scheduleDicts) {
        try (Connection connection = dataSource.getConnection()) {
            for (ScheduleDict scheduleDict : scheduleDicts) {
                String tableName = scheduleDict.getDictName();
                try {
                    JSONObject clearConfig = JSONObject.parseObject(scheduleDict.getDictValue());
                    if (null == clearConfig || clearConfig.isEmpty()) {
                        clearConfig = new JSONObject();
                    }
                    long lastClearId = clearConfig.getLongValue(clearFlag);
                    if (lastClearId < 0) {
                        lastClearId = 0L;
                    }
                    String sqlAppendWhere = (String) clearConfig.getOrDefault(appendWhere, "");
                    // 单位 day
                    Integer clearDate = (Integer) clearConfig.getOrDefault(clearDateConfig, 180);
                    Integer deleteDate = (Integer) clearConfig.getOrDefault(deleteDateConfig, 30);
                    // 每条sql执行范围
                    Integer idIncrement = (Integer) clearConfig.getOrDefault(increment, 10000);
                    boolean directDeleteData = (boolean) clearConfig.getOrDefault(directDelete, false);
                    if (!directDeleteData) {
                        DateTime clearTime = DateTime.now().plusDays(-clearDate);
                        int updateSize = -1;
                        //1. 标记
                        while (updateSize == -1 || updateSize > 0) {
                            long endClearId = lastClearId + idIncrement;
                            String updateSql = String.format("update %s set is_deleted = 2 where id >= %s and id <= %s and gmt_create < %s %s",
                                    tableName, lastClearId, endClearId, clearTime.toString("yyyyMMddHHmmss"), sqlAppendWhere);
                            LOGGER.info("DataClearSchedule update sql [{}]", updateSql);
                            Statement statement = connection.createStatement();
                            updateSize = statement.executeUpdate(updateSql);
                            LOGGER.info("DataClearSchedule update size [{}]", updateSize);
                            if (updateSize > 0) {
                                lastClearId = endClearId;
                            }
                        }
                    }

                    //2. 清理
                    DateTime deleteTime = DateTime.now().plusDays(-clearDate).plusDays(-deleteDate);
                    for (int i = 0; i < lastClearId; i = i + idIncrement) {
                        long endClearId = i + idIncrement;
                        String deleteSql = String.format("delete from %s where is_deleted = 2 and id >= %s and id <= %s and gmt_create < %s %s",
                                tableName, i, endClearId, deleteTime.toString("yyyyMMddHHmmss"), sqlAppendWhere);
                        LOGGER.info("DataClearSchedule delete sql [{}]", deleteSql);
                        Statement statement = connection.createStatement();
                        int clearSize = statement.executeUpdate(deleteSql);
                        LOGGER.info("DataClearSchedule clear size [{}]", clearSize);
                        if (clearSize <= 0) {
                            break;
                        }
                    }
                    //3. 更新标识
                    clearConfig.put(clearFlag, lastClearId);
                    scheduleDict.setDictValue(clearConfig.toJSONString());
                    scheduleDictDao.update(scheduleDict);
                } catch (Exception exception) {
                    LOGGER.error("data clear table :{} process error ", tableName, exception);
                }
            }
        } catch (Exception exception) {
            LOGGER.error("data clear process error ", exception);
        }
    }

}
