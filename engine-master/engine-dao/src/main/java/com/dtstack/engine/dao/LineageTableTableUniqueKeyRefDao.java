package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.LineageTableTableUniqueKeyRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chener
 * @Classname LineageTableTableAppRefDao
 * @Description
 * @Date 2020/10/28 18:01
 * @Created chener@dtstack.com
 */
public interface LineageTableTableUniqueKeyRefDao {
    Integer deleteByUniqueKey(@Param("appType") Integer appType, @Param("uniqueKey")String uniqueKey);

    Integer deleteByUniqueKeyAndVersionId(@Param("appType") Integer appType, @Param("uniqueKey")String uniqueKey,@Param("versionId")Integer versionId);

    Integer deleteByLineageTableIdAndUniqueKey(@Param("appType")Integer appType, @Param("uniqueKey")String uniqueKey, @Param("lineageTableId")Long lineageTableId);

    Integer batchInsert(List<LineageTableTableUniqueKeyRef> resList);

    /**
     * 根据表血缘关系id集合逻辑删除表血缘关联关系
     * @param idList
     * @param appType
     */
    void deleteByLineageTableIdList(@Param("idList")List<Long> idList, @Param("appType") Integer appType);
}
