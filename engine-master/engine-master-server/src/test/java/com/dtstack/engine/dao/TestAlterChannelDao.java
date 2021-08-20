package com.dtstack.engine.dao;

import com.dtstack.engine.domain.AlertChannel;
import com.dtstack.engine.domain.AlertContent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther: dazhi
 * @Date: 2021/1/27 5:42 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface TestAlterChannelDao {

    @Insert({"insert into alert_channel (`id`, `cluster_id`, `alert_gate_name`, `alert_gate_type`, `alert_gate_code`, `alert_gate_json`, `alert_gate_source`, `file_path`, `is_default`, `is_deleted`, `gmt_created`, `gmt_modified`, `alert_template`) " +
            "values (#{alertChannel.id}, #{alertChannel.clusterId}, #{alertChannel.alertGateName}, #{alertChannel.alertGateType}, #{alertChannel.alertGateCode}, #{alertChannel.alertGateJson}, #{alertChannel.alertGateSource}, #{alertChannel.filePath}, #{alertChannel.isDefault}, #{alertChannel.isDeleted}, #{alertChannel.gmtCreated}, #{alertChannel.gmtModified}, #{alertChannel.alertTemplate})"})
    @Options()
    Integer insert(@Param("alertChannel") AlertChannel alertChannel);
}
