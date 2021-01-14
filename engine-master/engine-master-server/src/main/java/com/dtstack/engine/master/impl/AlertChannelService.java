package com.dtstack.engine.master.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtstack.engine.alert.domian.PageResult;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import com.dtstack.engine.api.param.ClusterAlertPageParam;
import com.dtstack.engine.alert.enums.AlertGateTypeEnum;
import com.dtstack.engine.common.enums.IsDefaultEnum;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.domain.AlertChannel;
import com.dtstack.engine.api.param.ClusterAlertParam;
import com.dtstack.engine.api.vo.alert.AlertGateVO;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.dao.AlertChannelDao;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/1/12 9:44 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class AlertChannelService {

    @Autowired
    private AlertChannelDao alertChannelDao;

    @Transactional
    public Boolean addChannelOrEditChannel(AlertGateVO alertGateVO) {
        AlertChannel alertChannel = null;

        if (alertGateVO.getId() != null) {
            alertChannel = alertChannelDao.selectById(alertGateVO.getId());
        }

        int changed = 0;
        if (alertChannel == null) {
            // 插入通道
            alertChannel = new AlertChannel();
            buildBean(alertGateVO, alertChannel);
            changed = alertChannelDao.insert(alertChannel);
        } else {
            // 编辑通道
            alertChannel.setId(alertGateVO.getId());
            buildBean(alertGateVO, alertChannel);
            changed =  alertChannelDao.updateById(alertChannel);
        }

        return changed > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean checkAlertGateSourceExist(String alertGateSource) {
        AlertChannel alertChannel = alertChannelDao.selectOne(new QueryWrapper<AlertChannel>()
                .eq("alert_gate_source",alertGateSource)
                .eq("is_deleted",IsDeletedEnum.NOT_DELETE.getType())
        );
        return alertChannel == null ? Boolean.FALSE : Boolean.TRUE;
    }

    private void buildBean(AlertGateVO alertGateVO, AlertChannel alertChannel) {
        if(alertGateVO.getClusterId()!=null) {
            alertChannel.setClusterId(alertGateVO.getClusterId().longValue());
        }

        if(StringUtils.isNotBlank(alertGateVO.getAlertGateName())) {
            alertChannel.setAlertGateName(alertGateVO.getAlertGateName());
        }

        if(alertGateVO.getAlertGateType()!=null) {
            alertChannel.setAlertGateType(alertGateVO.getAlertGateType());
        }

        if(StringUtils.isNotBlank(alertGateVO.getAlertGateCode())) {
            alertChannel.setAlertGateCode(alertGateVO.getAlertGateCode());
        }

        if(StringUtils.isNotBlank(alertGateVO.getAlertGateJson())) {
            alertChannel.setAlertGateJson(alertGateVO.getAlertGateJson());
        }

        if(StringUtils.isNotBlank(alertGateVO.getAlertGateSource())) {
            alertChannel.setAlertGateSource(alertGateVO.getAlertGateSource());
        }

        if(StringUtils.isNotBlank(alertGateVO.getAlertTemplate())) {
            alertChannel.setAlertTemplate(alertGateVO.getAlertTemplate());
        }

        if(StringUtils.isNotBlank(alertGateVO.getFilePath())) {
            alertChannel.setFilePath(alertGateVO.getFilePath());
        }

        if(alertGateVO.getIsDefault()!=null) {
            alertChannel.setIsDefault(alertGateVO.getIsDefault());
        }
        alertChannel.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
    }


    @Transactional
    public Boolean setDefaultAlert(ClusterAlertParam param) {
        checkDefaultParam(param);
        AlertChannel channel = alertChannelDao.selectById(param.getAlertId());

        if (channel == null) {
            throw new RdosDefineException("通道不存在!");
        }

        // 重设默认通道
        AlertChannel alertChannel = new AlertChannel();
        alertChannel.setIsDefault(IsDefaultEnum.NOT_DEFAULT.getType());
        alertChannelDao.update(alertChannel, new UpdateWrapper<AlertChannel>()
                .eq("is_deleted", IsDeletedEnum.NOT_DELETE.getType())
                .eq("alert_gate_type", param.getAlertGateType())
        );

        // 更新默认通道
        AlertChannel defaultAlertChannel = new AlertChannel();
        defaultAlertChannel.setIsDefault(IsDefaultEnum.DEFAULT.getType());
        defaultAlertChannel.setId(param.getAlertId());
        int index = alertChannelDao.updateById(defaultAlertChannel);
        return index > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    private void checkDefaultParam(ClusterAlertParam param) {
        if (param.getClusterId()==null) {
            param.setClusterId(0L);
        }

        if (param.getAlertId() == null) {
            throw new RdosDefineException("通道ID（AlterId）是必传参数");
        }

        if (param.getAlertGateType() == null) {
            throw new RdosDefineException("通道类型（AlertGateType）是必传参数");
        }

        if (AlertGateTypeEnum.CUSTOMIZE.getType().equals(param.getAlertGateType())) {
            throw new RdosDefineException("自定义通道类型（AlertGateType）不能设置默认值");
        }
    }

    public PageResult<ClusterAlertPO> page(ClusterAlertPageParam pageParam) {
        Page<AlertChannel> iPage = new Page<>(pageParam.getCurrentPage(),pageParam.getPageSize());
        IPage<AlertChannel> page = alertChannelDao.selectPage(iPage,new QueryWrapper<AlertChannel>()
            .eq("cluster_id",pageParam.getClusterId())
            .eq("is_deleted",IsDeletedEnum.NOT_DELETE.getType())
            .in("alert_gate_type",pageParam.getAlertGateType())
        );

        PageResult<ClusterAlertPO> result = new PageResult<>();
        result.setTotalPage((int) page.getPages());
        result.setCurrentPage((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotalCount((int) page.getTotal());
        List<ClusterAlertPO> records = build(page.getRecords());
        result.setData(records);
        return result;
    }

    private List<ClusterAlertPO> build(List<AlertChannel> records) {
        List<ClusterAlertPO> clusterAlertPOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(records)) {
            for (AlertChannel record : records) {
                ClusterAlertPO po = build(record);
                clusterAlertPOS.add(po);
            }
        }
        return clusterAlertPOS;
    }

    private ClusterAlertPO build(AlertChannel record) {
        ClusterAlertPO po = new ClusterAlertPO();
        po.setAlertId(record.getId());
        po.setClusterId(record.getClusterId());
        po.setAlertGateName(record.getAlertGateName());
        po.setAlertGateType(record.getAlertGateType());
        po.setAlertGateSource(record.getAlertGateSource());
        po.setIsDefault(record.getIsDefault());
        po.setIsDeleted(record.getIsDeleted());
        po.setGmtCreated(record.getGmtCreated());
        po.setGmtModified(record.getGmtModified());
        return po;
    }


    public AlertGateVO getGateById(Long id) {
        AlertGateVO gateVO = null;
        if (id != null) {
            AlertChannel alertChannel = alertChannelDao.selectById(id);
            gateVO = new AlertGateVO();

            build(gateVO, alertChannel);
        }
        return gateVO;
    }

    private void build(AlertGateVO gateVO, AlertChannel alertChannel) {
        gateVO.setId(alertChannel.getId());
        gateVO.setAlertGateSource(alertChannel.getAlertGateSource());
        gateVO.setAlertGateName(alertChannel.getAlertGateName());
        gateVO.setClusterId(alertChannel.getClusterId());
        gateVO.setIsDefault(alertChannel.getIsDefault());
        gateVO.setAlertGateType(alertChannel.getAlertGateType());
        gateVO.setAlertGateJson(alertChannel.getAlertGateJson());
        gateVO.setAlertGateCode(alertChannel.getAlertGateCode());
        gateVO.setAlertTemplate(alertChannel.getAlertTemplate());
        gateVO.setFilePath(alertChannel.getFilePath());
    }

    public Boolean deleteGate(Long id) {
        AlertChannel alertChannel = new AlertChannel();
        alertChannel.setIsDeleted(IsDeletedEnum.DELETE.getType());
        alertChannel.setId(id);
        int index = alertChannelDao.updateById(alertChannel);
        return index > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    public List<ClusterAlertPO> listShow() {
        List<AlertChannel> alertChannels = alertChannelDao.selectList(null);

        List<ClusterAlertPO> aimPos = Lists.newArrayList();

        for (AlertChannel channel : alertChannels) {
            if (channel.getIsDefault().equals(1) && !AlertGateTypeEnum.CUSTOMIZE.getType().equals(channel.getAlertGateType())) {
                AlertGateTypeEnum enumByCode = AlertGateTypeEnum.getEnumByCode(channel.getAlertGateType());

                if (enumByCode != null) {
                    channel.setAlertGateSource(AlertGateTypeEnum.getDefaultFiled(enumByCode));
                    channel.setAlertGateName(enumByCode.getMsg());
                    aimPos.add(build(channel));
                }
            } else if (AlertGateTypeEnum.CUSTOMIZE.getType().equals(channel.getAlertGateType())) {
                aimPos.add(build(channel));
            }
        }

        return aimPos;
    }

    public List<AlertChannel> selectAlertByIds(List<String> alertGateSources) {
        if (org.springframework.util.CollectionUtils.isEmpty(alertGateSources)) {
            return Lists.newArrayList();
        }
        List<AlertChannel> pos = Lists.newArrayList();
        List<Integer> defaultAlert = Lists.newArrayList();
        List<String> customizeAlert = Lists.newArrayList();

        for (String alertGateSource : alertGateSources) {
            Integer defaultFile = AlertGateTypeEnum.isDefaultFile(alertGateSource);
            if (defaultFile != null) {
                defaultAlert.add(defaultFile);
            } else {
                customizeAlert.add(alertGateSource);
            }
        }

        if (CollectionUtils.isNotEmpty(defaultAlert)) {
            pos.addAll(alertChannelDao.selectList(new QueryWrapper<AlertChannel>()
                    .eq("is_deleted",IsDeletedEnum.NOT_DELETE.getType())
                    .eq("is_default",IsDefaultEnum.DEFAULT.getType())));
        }

        if (CollectionUtils.isNotEmpty(customizeAlert)) {
            pos.addAll(alertChannelDao.selectList(new QueryWrapper<AlertChannel>()
                    .eq("is_deleted",IsDeletedEnum.NOT_DELETE.getType())
                    .in("alert_gate_source",alertGateSources)));
        }

        return pos;
    }
}
