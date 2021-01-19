package com.dtstack.engine.master.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.AlterSender;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.po.ClusterAlertPO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.ClusterAlertPageParam;
import com.dtstack.engine.api.param.ClusterAlertParam;
import com.dtstack.engine.api.vo.alert.AlertGateTestVO;
import com.dtstack.engine.api.vo.alert.AlertGateVO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.master.config.MvcConfig;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.impl.AlertChannelService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.utils.CheckUtils;
import com.dtstack.lang.data.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

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
    private AlterSender alterSender;

    @Autowired
    private MvcConfig mvcConfig;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private AlertChannelService alertChannelService;


    @ApiOperation("新增编辑告警通道 用于替换console接口: /api/console/service/alert/edit")
    @PostMapping("/edit")
    public Boolean edit(@RequestParam(value = "file", required = false) MultipartFile file,
                                    AlertGateVO alertGateVO) throws Exception {
        CheckUtils.checkAlertGateVOFormat(alertGateVO);
        Assert.isTrue(!alertChannelService.checkAlertGateSourceExist(alertGateVO.getAlertGateSource()),"通道标识以重复，请修改通道标识");

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
                dbPath = UploadFileToSftp(file, filePath, destPath, dbPath);
            }

            alertGateVO.setFilePath(dbPath);
        } else {
            alertGateVO.setFilePath(null);
        }

        return alertChannelService.addChannelOrEditChannel(alertGateVO);
    }

    private String UploadFileToSftp(@RequestParam(value = "file", required = false) MultipartFile file, String filePath, String destPath, String dbPath) {
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
        return dbPath;
    }

    @ApiOperation("设为默认告警通道 用于取代console接口: /api/console/service/alert/setDefaultAlert")
    @PostMapping("/setDefaultAlert")
    public Boolean setDefaultAlert(@RequestBody ClusterAlertParam param) {
        return alertChannelService.setDefaultAlert(param);
    }

    @ApiOperation("获取告警通道分页 用于取代console接口: /api/console/service/alert/page")
    @PostMapping("/page")
    public PageResult<List<ClusterAlertPO>> page(@RequestBody ClusterAlertPageParam pageParam) {
        return alertChannelService.page(pageParam);
    }


    @ApiOperation("告警通道详情 用于取代console接口: /api/console/service/alert/getByAlertId")
    @PostMapping("/getByAlertId")
    public AlertGateVO getByAlertId(@RequestBody AlertGateVO alertGateVO) {
        return alertChannelService.getGateById(alertGateVO.getId());
    }


    @ApiOperation("删除告警通道 用于取代console接口: /api/console/service/alert/delete")
    @PostMapping("/delete")
    public Boolean delete(@RequestBody AlertGateVO alertGateVO) {
        Assert.notNull(alertGateVO.getId(), "id不能为空");
        return alertChannelService.deleteGate(alertGateVO.getId());
    }

    @ApiOperation("获取告警通道分页")
    @PostMapping("/list/show")
    public List<ClusterAlertPO> listShow() {
        return alertChannelService.listShow();
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
                AlertGateVO alertGateVO = alertChannelService.getGateById(alertGateTestVO.getId());
                if (alertGateVO != null) {
                    alertGateTestVO.setFilePath(alertGateVO.getFilePath());
                }
            }
        }
        log.info("testAlert jar path :{}", alertGateTestVO.getFilePath());

        //build test alertParam
        AlterContext alertParam = buildTestAlterContext(alertGateTestVO);
        R send = alterSender.sendSyncAlter(alertParam,null);
        if (send.isSuccess()) {
            return;
        }
        throw new RdosDefineException(send.getMessage());
    }

    private AlterContext buildTestAlterContext(AlertGateTestVO alertGateTestVO) {
        AlertGateCode parse = AlertGateCode.parse(alertGateTestVO.getAlertGateCode());

        AlterContext result = new AlterContext();
        if (parse == AlertGateCode.AG_GATE_SMS_JAR) {
            List<String> phones = alertGateTestVO.getPhones();
            result.setPhone(phones.get(0));
            result.setContent("测试内容");
        }

        if (parse == AlertGateCode.AG_GATE_MAIL_DT
                || parse == AlertGateCode.AG_GATE_MAIL_JAR) {

        }

        if (parse == AlertGateCode.AG_GATE_DING_JAR
                || parse == AlertGateCode.AG_GATE_DING_DT) {

        }

        if (parse == AlertGateCode.AG_GATE_CUSTOM_JAR) {

        }

        result.setAlertGateCode(parse);
        result.setAlertGateJson(alertGateTestVO.getAlertGateJson());
        result.setJarPath(alertGateTestVO.getFilePath());
        return result;
    }
}
