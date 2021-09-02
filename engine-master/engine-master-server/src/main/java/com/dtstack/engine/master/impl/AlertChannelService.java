package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.enums.AlertGateTypeEnum;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.ClusterAlertPageParam;
import com.dtstack.engine.api.param.ClusterAlertParam;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.AlterSftpVO;
import com.dtstack.engine.api.vo.alert.AlertGateVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.IsDefaultEnum;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.dao.AlertChannelDao;
import com.dtstack.engine.api.domain.AlertChannel;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.event.SftpDownloadEvent;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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

    @Autowired
    private ScheduleDictDao scheduleDictDao;

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private WorkerOperator workerOperator;


    @Transactional
    public Boolean addChannelOrEditChannel(AlertGateVO alertGateVO) {
        AlertChannel alertChannel = null;

        if (alertGateVO.getId() != null) {
            alertChannel = alertChannelDao.selectById(alertGateVO.getId());
        }

        Integer isDefault = alertGateVO.getIsDefault();

        if (IsDefaultEnum.DEFAULT.getType().equals(isDefault)) {
            // 设置默认值
            alertChannelDao.updateDefaultAlertByType( alertGateVO.getAlertGateType(),IsDefaultEnum.NOT_DEFAULT.getType(),IsDeletedEnum.NOT_DELETE.getType());
        }

        int changed = 0;
        if (alertChannel == null) {
            // 插入通道
            alertChannel = new AlertChannel();
            buildBean(alertGateVO, alertChannel);
            alertChannel.setAlertGateSource(alertGateVO.getAlertGateSource());
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
        AlertChannel queryAlertChannel = new AlertChannel();
        queryAlertChannel.setAlertGateSource(alertGateSource);
        queryAlertChannel.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
        List<AlertChannel> alertChannels = alertChannelDao.selectByQuery(queryAlertChannel);
        return CollectionUtils.isEmpty(alertChannels)? Boolean.FALSE : Boolean.TRUE;
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


    @Transactional(rollbackFor = Exception.class)
    public Boolean setDefaultAlert(ClusterAlertParam param) {
        checkDefaultParam(param);
        AlertChannel channel = alertChannelDao.selectById(param.getAlertId());

        if (channel == null) {
            throw new RdosDefineException("channel does not exist!");
        }

        // 重设默认通道
        alertChannelDao.updateDefaultAlertByType( param.getAlertGateType(),IsDefaultEnum.NOT_DEFAULT.getType(),IsDeletedEnum.NOT_DELETE.getType());

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
            throw new RdosDefineException("Channel id（AlterId）is a required parameter");
        }

        if (param.getAlertGateType() == null) {
            throw new RdosDefineException("Channel type（AlertGateType）is a required parameter");
        }

        if (AlertGateTypeEnum.CUSTOMIZE.getType().equals(param.getAlertGateType())) {
            throw new RdosDefineException("Custom channel type（AlertGateType）cannot set default value");
        }
    }

    public PageResult<List<ClusterAlertPO>> page(ClusterAlertPageParam pageParam) {
        List<Integer> alertGateType = pageParam.getAlertGateType();
        Integer clusterId = pageParam.getClusterId();
        Page<AlertChannel> pageData = PageHelper.startPage(pageParam.getCurrentPage(), pageParam.getPageSize())
                .doSelectPage(() -> alertChannelDao.selectList(IsDeletedEnum.NOT_DELETE.getType(),alertGateType,clusterId));
        List<ClusterAlertPO> records = build(pageData.getResult());
        return new PageResult<>(
                 pageData.getPageNum(),
                 pageData.getPageSize(),
                (int) pageData.getTotal(),
                 pageData.getPages(),
                 records);
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
        List<AlertChannel> alertChannels = alertChannelDao.selectAll();

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
            AlertChannel queryAlertChannel = new AlertChannel();
            queryAlertChannel.setIsDeleted(IsDeletedEnum.NOT_DELETE.getType());
            queryAlertChannel.setIsDefault(IsDefaultEnum.DEFAULT.getType());

            pos.addAll(alertChannelDao.selectInType(queryAlertChannel,defaultAlert));
        }

        if (CollectionUtils.isNotEmpty(customizeAlert)) {
            pos.addAll(alertChannelDao.selectListByGateSources(IsDeletedEnum.NOT_DELETE.getType(),customizeAlert));
        }

        return pos;
    }

    public AlterSftpVO sftpGet() {
        AlterSftpVO vo = new AlterSftpVO();
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(SftpDownloadEvent.Constant.COMPONENT_TEMPLATE_ID, Boolean.FALSE);
        if (CollectionUtils.isEmpty(componentConfigs)) {
            String pluginName = EComponentType.convertPluginNameByComponent(EComponentType.SFTP);
            ScheduleDict typeNameMapping = scheduleDictDao.getByNameValue(DictType.TYPENAME_MAPPING.type, pluginName.trim(), null,null);
            if (null != typeNameMapping) {
                componentConfigs = componentConfigDao.listByComponentId(Long.parseLong(typeNameMapping.getDictValue()), true);
            }
        }

        vo.setComponentTemplate(JSONObject.toJSONString(ComponentConfigUtils.buildDBDataToClientTemplate(componentConfigs)));
        vo.setComponentConfig(JSONObject.toJSONString(ComponentConfigUtils.convertComponentConfigToMap(componentConfigs)));
        return vo;
    }

    public Boolean sftpUpdate(AlterSftpVO vo) {
        String componentTemplate = vo.getComponentTemplate();

        if (StringUtils.isBlank(componentTemplate)) {
            throw new RdosDefineException("template is null");
        }

        List<ClientTemplate> clientTemplates = JSONArray.parseArray(componentTemplate, ClientTemplate.class);
        componentConfigService.addOrUpdateComponentConfig(clientTemplates, SftpDownloadEvent.Constant.COMPONENT_TEMPLATE_ID
                , SftpDownloadEvent.Constant.COMPONENT_TEMPLATE_ID, EComponentType.SFTP.getTypeCode());

        return Boolean.TRUE;
    }


    public ComponentTestResult sftpTestConnect(SftpConfig sftpConfig) {
        if (sftpConfig == null) {
            throw new RdosDefineException("sftp未配置或者未保存，请先配置或者保存后在进行测试联通性");
        }

        EComponentType sftp = EComponentType.SFTP;
        String pluginName = EComponentType.convertPluginNameByComponent(sftp);

        JSONObject dataInfo = JSONObject.parseObject(JSON.toJSONString(sftpConfig));
        dataInfo.put("componentType", EComponentType.SFTP.getName());
        return workerOperator.testConnect(pluginName, dataInfo.toJSONString());
    }
}
