package com.dtstack.engine.alert.client;

import com.dtstack.engine.alert.converter.AlertGateConverter;
import com.dtstack.engine.alert.converter.AlertTemplateConvert;
import com.dtstack.engine.alert.domian.PageResult;
import com.dtstack.engine.alert.serivce.AlertGateService;
import com.dtstack.engine.alert.serivce.AlertTemplateService;
import com.dtstack.engine.alert.serivce.ClusterAlertService;
import com.dtstack.engine.api.domain.po.AlertGatePO;
import com.dtstack.engine.api.domain.po.AlertTemplatePO;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import com.dtstack.engine.api.param.ClusterAlertPageParam;
import com.dtstack.engine.api.param.ClusterAlertParam;
import com.dtstack.engine.api.vo.alert.AlertGateVO;
import com.dtstack.engine.common.enums.AlertGateTypeEnum;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.AlertGateDao;
import com.dtstack.engine.dao.ClusterAlertDao;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class AlertGateFacade {

    @Autowired
    private AlertGateService alertGateService;

    @Autowired
    private AlertTemplateService alertTemplateService;

    @Autowired
    private ClusterAlertService clusterAlertService;

    @Autowired
    private ClusterAlertDao clusterAlertDao;

    @Autowired
    private AlertGateDao alertGateDao;



    public boolean activeGate(Long gateId) {
        return alertGateService.activeAlertGate(gateId);
    }

    public boolean disableGate(Long gateId) {
        return alertGateService.disableAlertGate(gateId);
    }

    /**
     * 删除告警通道
     *
     * @param gateId
     * @return
     */
    @Transactional
    public boolean deleteGate(Long gateId) {
        AlertGatePO alertGate = alertGateService.getGateById(gateId);
        if (alertGate != null) {
            alertTemplateService.deleteTemplateByTypeAndSource(alertGate.getAlertGateType(), alertGate.getAlertGateSource());
            alertGateService.deleteAlertGateLogic(gateId);
            clusterAlertDao.delete(gateId.intValue());
        }
        return Boolean.TRUE;
    }

    /**
     * <p>
     * 编辑告警通道
     * </p>
     *
     * @param alertGateVO
     * @return
     */
    @Transactional
    public boolean editGate(AlertGateVO alertGateVO) {
        AlertGatePO alertGatePO = alertGateService.editAlertGate(AlertGateConverter.toDTO(alertGateVO));

        //有模板的情况，添加模板
        if (StringUtils.isNotBlank(alertGateVO.getAlertTemplate())) {
            alertTemplateService.editAlertTemplate(AlertTemplateConvert.toDTO(alertGateVO));
        }

        ClusterAlertPO clusterAlertPO = new ClusterAlertPO();
        clusterAlertPO.setClusterId(alertGateVO.getClusterId());
        clusterAlertPO.setAlertId(alertGatePO.getId().intValue());
        clusterAlertPO.setIsDefault(alertGateVO.getIsDefault());
        clusterAlertPO.setAlertGateType(alertGateVO.getAlertGateType());
        clusterAlertService.edit(clusterAlertPO);

        return true;
    }

    @Transactional
    public boolean setDefaultAlert(ClusterAlertParam clusterAlertParam) {
        clusterAlertService.setDefaultAlert(clusterAlertParam.getClusterId(),
                clusterAlertParam.getAlertGateType(), clusterAlertParam.getAlertId());
        return true;
    }


    public AlertGateVO getGateById(Long gateId) {
        AlertGatePO alertGatePO = alertGateService.getSuitGateById(gateId);
        if (alertGatePO == null) {
            return new AlertGateVO();
        }
        AlertTemplatePO alertTemplatePO = alertTemplateService.getAlertTemplateByTypeAndSource(alertGatePO.getAlertGateType(), alertGatePO.getAlertGateSource());
        ClusterAlertPO query = new ClusterAlertPO();
        query.setAlertId(gateId.intValue());
        ClusterAlertPO clusterAlertPO = clusterAlertDao.get(query);

        return AlertGateConverter.toVO(alertGatePO, alertTemplatePO, clusterAlertPO);
    }

    public PageResult<ClusterAlertPO> page(ClusterAlertPageParam pageParam) {
        ClusterAlertPO po = new ClusterAlertPO();
        po.setClusterId(pageParam.getClusterId());
        po.setAlertGateTypes(pageParam.getAlertGateType());

        clusterAlertDao.list(po);
        Page<ClusterAlertPO> pageData = PageHelper.startPage(pageParam.getCurrentPage(), pageParam.getPageSize())
                .doSelectPage(() -> clusterAlertDao.list(po));

        PageResult<ClusterAlertPO> result = new PageResult<>();
        result.setTotalPage(pageData.getPages());
        result.setCurrentPage(pageData.getPageNum());
        result.setPageSize(pageData.getPageSize());
        result.setTotalCount((int) pageData.getTotal());
        result.setData(pageData.getResult());
        return result;
    }

    public void checkAlertGateSourceExist(String alertGateSource) {
        AlertGatePO query = new AlertGatePO();
        query.setAlertGateSource(alertGateSource);
        AlertGatePO exist = alertGateDao.get(query);
        Assert.isNull(exist,"通道标识已存在");
    }

    public List<ClusterAlertPO> selectAlertByIds(List<String> alertGateSources) {
        if (CollectionUtils.isEmpty(alertGateSources)) {
            return Lists.newArrayList();
        }
        List<ClusterAlertPO> pos = Lists.newArrayList();
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

        if (!CollectionUtils.isEmpty(defaultAlert)) {
            pos.addAll(alertGateDao.selectDefaultAlert(defaultAlert,1));
        }

        if (!CollectionUtils.isEmpty(customizeAlert)) {
            pos.addAll(alertGateDao.selectAlertByIds(alertGateSources));
        }

        return pos;
    }

    public List<ClusterAlertPO> listShow() {
        // 查询所有通道
        List<ClusterAlertPO> pos = alertGateDao.allGate();

        List<ClusterAlertPO> aimPos = Lists.newArrayList();

        for (ClusterAlertPO po : pos) {
            if (po.getIsDefault().equals(1) && !AlertGateTypeEnum.CUSTOMIZE.getType().equals(po.getAlertGateType())) {
                AlertGateTypeEnum enumByCode = AlertGateTypeEnum.getEnumByCode(po.getAlertGateType());

                if (enumByCode != null) {
                    po.setAlertGateSource(AlertGateTypeEnum.getDefaultFiled(enumByCode));
                    po.setAlertGateName(enumByCode.getMsg());
                    aimPos.add(po);
                }
            } else if (AlertGateTypeEnum.CUSTOMIZE.getType().equals(po.getAlertGateType())) {
                aimPos.add(po);
            }
        }

        if (CollectionUtils.isEmpty(aimPos)) {
            throw new RdosDefineException("请先配置console控制台告警通道！");
        }

        return aimPos;

    }
}
