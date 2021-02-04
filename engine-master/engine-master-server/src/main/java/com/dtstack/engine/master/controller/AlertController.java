package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.client.AlertGateFacade;
import com.dtstack.engine.alert.client.AlertServiceProvider;
import com.dtstack.engine.alert.domian.PageResult;
import com.dtstack.engine.alert.enums.AGgateType;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.param.*;
import com.dtstack.engine.alert.serivce.AlertGateService;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.po.AlertGatePO;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import com.dtstack.engine.api.param.ClusterAlertPageParam;
import com.dtstack.engine.api.param.ClusterAlertParam;
import com.dtstack.engine.api.vo.alert.AlertGateTestVO;
import com.dtstack.engine.api.vo.alert.AlertGateVO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.AlertGateTypeEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.master.config.MvcConfig;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.utils.CheckUtils;
import com.dtstack.lang.data.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2020/8/7
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Api(tags = "告警通道")
@RestController
@RequestMapping("/node/alert")
public class AlertController {

    private final Logger log = LoggerFactory.getLogger(AlertController.class);

    @Autowired
    private AlertGateFacade alertGateFacade;
    @Autowired
    private AlertServiceProvider alertServiceProvider;

    @Autowired
    private MvcConfig mvcConfig;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private AlertGateService alertGateService;

    @ApiOperation("新增编辑告警通道 用于替换console接口: /api/console/service/alert/edit")
    @PostMapping("/edit")
    public Boolean edit(@RequestParam(value = "file", required = false) MultipartFile file,
                                    AlertGateVO alertGateVO) throws Exception {
        CheckUtils.checkAlertGateVOFormat(alertGateVO);

        if (AlertGateTypeEnum.CUSTOMIZE.getType().equals(alertGateVO.getAlertGateType()) && StringUtils.isBlank(alertGateVO.getAlertGateCode())) {
            alertGateVO.setAlertGateCode(AlertGateCode.AG_GATE_CUSTOM_JAR.code());
        }

        if (alertGateVO.getId() == null) {
            alertGateFacade.checkAlertGateSourceExist(alertGateVO.getAlertGateSource());
        }

        //暂时默认为0
        alertGateVO.setClusterId(0);
        if (file != null) {
            String filePath = mvcConfig.getPluginPath(false,alertGateVO.getAlertGateSource());
            String destPath = filePath + "/" + file.getOriginalFilename();
            File destFile = new File(destPath);
            if (!destFile.exists()) {
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                destFile.createNewFile();
            }
            file.transferTo(destFile);

            String dbPath = destPath;
            // 上传sftp
            if (environmentContext.getOpenConsoleSftp()) {
                // 查询默认集群的sftp
                Component sftpComponent = componentService.getComponentByClusterId(-1L, EComponentType.SFTP.getTypeCode());
                if (sftpComponent != null) {
                    SftpConfig sftpConfig = JSONObject.parseObject(sftpComponent.getComponentConfig(), SftpConfig.class);
                    if (sftpConfig != null) {
                        try {
                            String remoteDir = sftpConfig.getPath() + File.separator + filePath;
                            SftpFileManage sftpManager = SftpFileManage.getSftpManager(sftpConfig);
                            sftpManager.uploadFile(remoteDir ,destPath);

                            dbPath = dbPath + GlobalConst.PATH_CUT + remoteDir + File.separator + file.getOriginalFilename();
                        } catch (Exception e) {
                            log.error("上传sftp失败:",e);
                        }
                    }
                }
            }

            alertGateVO.setFilePath(dbPath);
        } else {
            alertGateVO.setFilePath(null);
        }

        return alertGateFacade.editGate(alertGateVO);
    }

    @ApiOperation("设为默认告警通道 用于取代console接口: /api/console/service/alert/setDefaultAlert")
    @PostMapping("/setDefaultAlert")
    public Boolean setDefaultAlert(@RequestBody ClusterAlertParam param) {
        param.setClusterId(0);
        return alertGateFacade.setDefaultAlert(param);
    }

    @ApiOperation("获取告警通道分页 用于取代console接口: /api/console/service/alert/page")
    @PostMapping("/page")
    public PageResult<ClusterAlertPO> page(@RequestBody ClusterAlertPageParam pageParam) {
        //暂时默认为0
        pageParam.setClusterId(0);
        return alertGateFacade.page(pageParam);
    }


    @ApiOperation("告警通道详情 用于取代console接口: /api/console/service/alert/getByAlertId")
    @PostMapping("/getByAlertId")
    public AlertGateVO getByAlertId(@RequestBody AlertGateVO alertGateVO) {
        return alertGateFacade.getGateById(alertGateVO.getId());
    }


    @ApiOperation("删除告警通道 用于取代console接口: /api/console/service/alert/delete")
    @PostMapping("/delete")
    public Boolean delete(@RequestBody AlertGateVO alertGateVO) {
        Assert.notNull(alertGateVO.getId(), "id不能为空");
        return alertGateFacade.deleteGate(alertGateVO.getId());
    }

    @ApiOperation("获取告警通道分页")
    @PostMapping("/list/show")
    public List<ClusterAlertPO> listShow() {
        return alertGateFacade.listShow();
    }

    @ApiOperation("jar上传接口")
    @PostMapping("/jarUpload")
    public String jarUpload(@RequestParam("file") MultipartFile file, String gateSource) throws Exception {
        String filePath = mvcConfig.getPluginPath(false, gateSource);
        String destPath = filePath + "/" + file.getOriginalFilename();
        File destFile = new File(destPath);
        if (!destFile.exists()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
        }
        file.transferTo(destFile);
        return destFile.getAbsolutePath();
    }


    @ApiOperation("测试告警通道")
    @PostMapping("/testAlert")
    public void testAlert(@RequestParam(value = "file", required = false) MultipartFile file,
                                AlertGateTestVO alertGateTestVO) throws Exception {
        CheckUtils.checkFormat(alertGateTestVO);
        String filePath = mvcConfig.getPluginPath(true,alertGateTestVO.getAlertGateSource());
        if (file != null) {
            String destPath = filePath + "/" + file.getOriginalFilename();
            File destFile = new File(destPath);
            if (!destFile.exists()) {
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                destFile.createNewFile();
            }
            file.transferTo(destFile);
            alertGateTestVO.setFilePath(destFile.getAbsolutePath());
        } else {
            if (alertGateTestVO.getId() != null) {
                AlertGatePO alertGatePO = alertGateService.getSuitGateById(alertGateTestVO.getId());
                if (alertGatePO != null) {
                    alertGateTestVO.setFilePath(alertGatePO.getFilePath());
                }
            }
        }
        log.info("testAlert jar path :{}", alertGateTestVO.getFilePath());


        //build test alertParam
        AlertParam alertParam = buildTestAlertParam(alertGateTestVO);
        R send = alertServiceProvider.send(alertParam);
        if (send.isSuccess()) {
            return;
        }
        throw new RdosDefineException(send.getMessage());
    }

    private AlertParam buildTestAlertParam(AlertGateTestVO alertGateTestVO) {
        AlertGateCode parse = AlertGateCode.parse(alertGateTestVO.getAlertGateCode());

        AlertParam result = null;
        if (parse == AlertGateCode.AG_GATE_SMS_JAR) {
            SmsAlertParam smsAlertParam = new SmsAlertParam();
            smsAlertParam.setPhones(alertGateTestVO.getPhones());
            smsAlertParam.setAGgateType(AGgateType.AG_GATE_TYPE_SMS);
            Map<String, String> dynamicParams = new HashMap();
            dynamicParams.put("user", "测试用户");
            dynamicParams.put("content", "测试内容");
            smsAlertParam.setDynamicParams(dynamicParams);
            smsAlertParam.setPhones(alertGateTestVO.getPhones());
            smsAlertParam.setMessage("测试内容");
            result = smsAlertParam;
        }

        if (parse == AlertGateCode.AG_GATE_MAIL_DT
                || parse == AlertGateCode.AG_GATE_MAIL_JAR) {
            MailAlertParam mailAlertParam = new MailAlertParam();
            mailAlertParam.setAGgateType(AGgateType.AG_GATE_TYPE_MAIL);
            mailAlertParam.setEmails(alertGateTestVO.getEmails());
            mailAlertParam.setSubject("测试Subject");
            Map<String, String> dynamicParams = new HashMap<>();
            dynamicParams.put("message", "测试内容");
            mailAlertParam.setDynamicParams(dynamicParams);
            result = mailAlertParam;
        }

        if (parse == AlertGateCode.AG_GATE_DING_JAR
                || parse == AlertGateCode.AG_GATE_DING_DT) {
            DingAlertParam dingAlertParam = new DingAlertParam();
            dingAlertParam.setAGgateType(AGgateType.AG_GATE_TYPE_DING);
            dingAlertParam.setDings(alertGateTestVO.getDings());
            dingAlertParam.setSubject("测试Subject");
            dingAlertParam.setMessage("测试内容");
            Map<String, Object> conf = new HashMap<>();
            conf.put("atMobiles", "");
            conf.put("isAtAll", Boolean.FALSE);
            result = dingAlertParam;
        }

        if (parse == AlertGateCode.AG_GATE_CUSTOM_JAR) {
            CustomizeAlertParam customizeAlertParam = new CustomizeAlertParam();
            customizeAlertParam.setAGgateType(AGgateType.AG_GATE_TYPE_CUSTOMIZE);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content",alertGateTestVO.getAlertTemplate());
            customizeAlertParam.setData(jsonObject.toJSONString());
            result = customizeAlertParam;
        }

        if (result != null) {
            result.setSource(alertGateTestVO.getAlertGateSource());
            result.setAlertTemplate(alertGateTestVO.getAlertTemplate());
            AlertGatePO alertGatePO = new AlertGatePO();
            alertGatePO.setAlertGateCode(parse.code());
            alertGatePO.setFilePath(alertGateTestVO.getFilePath());
            alertGatePO.setAlertGateJson(alertGateTestVO.getAlertGateJson());
            result.setAlertGatePO(alertGatePO);
        }
        
        return result;
    }
}
