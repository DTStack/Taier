package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleTaskCommit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/12/14 4:54 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleTaskCommitMapper {


    Boolean insertBatch(@Param("scheduleTaskCommits") List<ScheduleTaskCommit> scheduleTaskCommits);

    String getExtInfoByTaskId(@Param("taskId") Long taskId, @Param("appType") Integer appType,@Param("commitId") String commitId);

    ScheduleTaskCommit getTaskCommitByTaskId(@Param("taskId") Long taskId, @Param("appType") Integer appType,@Param("commitId") String commitId);

    Boolean updateTaskExtInfo(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("info") String info, @Param("commitId") String commitId);

    Long findMinIdOfTaskCommitByCommitId(@Param("commitId") String commitId);

    List<ScheduleTaskCommit> findTaskCommitByCommitId(@Param("minId") Long minId, @Param("commitId") String commitId, @Param("limit") Integer limit);

    Boolean updateTaskCommit(@Param("id") Long id);
}
