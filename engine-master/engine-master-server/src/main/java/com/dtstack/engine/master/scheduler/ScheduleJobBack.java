package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.api.vo.Pair;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yuebai
 * @date 2020-06-18
 */
@Component
public class ScheduleJobBack {

    private static final Logger log = LoggerFactory.getLogger(ScheduleJobBack.class);

    @Autowired
    private EnvironmentContext environment;

    @Autowired
    private DataSource dataSource;

    private ReentrantLock lock = new ReentrantLock();

    private static List<String> expireTableName = Lists.newArrayList("schedule_job", "schedule_job_job", "schedule_fill_data_job");
    private final static String BACK_TABLE_SUFFIX = "_back";
    private final static String JOB_WHERE_SQL = "where period_type in #{periodType} AND cyc_time < #{limitDate} and id < #{limitId}";
    private final static String FILL_DATA_WHERE_SQL = "where id not in (select fill_id from schedule_job WHERE fill_id > 0 )";
    private final static String JOB_JOB_WHERE_SQL = "where job_key in (select job_key from schedule_job_back where id > #{lastId})";
    private List<Pair<Integer, String>> timePeriodTypeMapping = null;

    @PostConstruct
    public void init() {
        timePeriodTypeMapping = Lists.newArrayList(
                new Pair<>(environment.getHourMax(), "0,1"),
                new Pair<>(environment.getDayMax(), "2"),
                new Pair<>(environment.getMonthMax(), "3,4"));
    }

    public void setIsMaster(boolean isMaster) {
        if (isMaster && environment.openScheduleJobCron()) {
            String cron = environment.getScheduleJobCron();
            long mill = getTimeMillis(cron);
            if (System.currentTimeMillis() - mill < environment.getScheduleJobScope()) {
                lock.lock();
                process();
                lock.unlock();
            }
        }
    }

    private long getTimeMillis(String time) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            SimpleDateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void process() {
        try (Connection connection = dataSource.getConnection()) {
            if (Objects.isNull(connection)) {
                log.error("back up get connect error");
            }
            log.info("back up schedule job start");
            if (CollectionUtils.isNotEmpty(expireTableName)) {
                //创建备份表
                for (String tableName : expireTableName) {
                    boolean tableExist = this.isTableExist(tableName, connection);
                    if (tableExist) {
                        if (!this.isTableExist(tableName + BACK_TABLE_SUFFIX, connection)) {
                            this.createBackUpTable(tableName, tableName + BACK_TABLE_SUFFIX, connection);
                        }
                    }
                }
            }
            //保存schedule_job_back的上一次id
            Long lastJobBackId = this.getLastId(connection, "SELECT id from schedule_job_back ORDER BY id desc limit 1;");


            log.info("back up schedule job lastJobBackId {}",lastJobBackId);
            //schedule_job表
            for (Pair<Integer, String> pair : timePeriodTypeMapping) {
                String limitDate = Objects.isNull(pair.getKey()) ? "" : String.format("'%s'",
                        new DateTime().minusDays(pair.getKey()).withTime(0,0,0,0).toString("yyyyMMddHHmmss"));
                //走ID索引
                Long lastJobId = this.getLastId(connection, String.format("SELECT id from schedule_job where cyc_time >%s limit 1;",limitDate));
                this.backUpTables("schedule_job", pair.getKey(), pair.getValue(), connection, JOB_WHERE_SQL.replace("#{limitId}",String.valueOf(lastJobId)));
            }

            //schedule_fill_data_job 直接删除fill_id没有的数据
            this.backUpTables("schedule_fill_data_job", null, null, connection, FILL_DATA_WHERE_SQL);

            if (lastJobBackId >= 0L) {
                this.backUpTables("schedule_job_job", null, null, connection, JOB_JOB_WHERE_SQL.replace("#{lastId}", String.valueOf(lastJobBackId)));
            }
            log.info("back up schedule job end");
        } catch (Exception e) {
            log.error("process back up job error ", e);
        }
    }

    private boolean isTableExist(String tableName, Connection connection) throws SQLException {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("SHOW TABLES LIKE '" + tableName + "'");
            rs.last();
            int rows = rs.getRow();
            return rows > 0;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void createBackUpTable(String tableName, String backUpTableName, Connection connection) throws SQLException {
        String sql = String.format("SHOW CREATE TABLE %s", tableName);
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            String createSql = "";
            while (resultSet.next()) {
                createSql = resultSet.getString(2).replace("`" + tableName + "`", "`" + backUpTableName + "`");
            }
            log.info("create table {} sql\n {}", backUpTableName, createSql);
            statement.execute(createSql);
            statement.execute("truncate table " + "`" + backUpTableName + "`");
        } catch (SQLException e) {
            log.error("create table error... tableName:{}", tableName);
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }


    private Long getLastId(Connection connection, String sql) {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception e) {
            log.error("getLastId error", e);
        }
        return 0L;
    }

    private void backUpTables(String tableName, Integer maxDays, String periodType, Connection connection, String where_sql) throws SQLException {
        if (connection.getAutoCommit()) {
            connection.setAutoCommit(false);
        }

        try (Statement statement = connection.createStatement()) {
            String backUpTableName = tableName + BACK_TABLE_SUFFIX;
            String limitDate = Objects.isNull(maxDays) ? "" : String.format("'%s'", new DateTime().minusDays(maxDays).toString("yyyyMMddHHmmss"));
            String where = where_sql.replace("#{limitDate}", limitDate)
                    .replace("#{periodType}", String.format("(%s)", periodType));
            log.info("start to backUpTables :{}  {}", tableName, where);
            //导入备份表
            String backUpSql = String.format("INSERT INTO `%s` SELECT * FROM `%s` %s", backUpTableName, tableName, where);
            log.info("backUpSql : {}", backUpSql);
            statement.execute(backUpSql);
            //删除原表
            String deleteSql = String.format("DELETE FROM %s %s", tableName, where);
            log.info("deleteSql : {}", deleteSql);
            statement.execute(deleteSql);
            connection.commit();
        } catch (Exception e) {
            log.error("backUpTables error rollBack...");
            connection.rollback();
            throw e;
        }
    }

}
