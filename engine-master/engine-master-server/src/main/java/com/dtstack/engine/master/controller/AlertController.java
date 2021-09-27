package com.dtstack.engine.master.controller;

import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.AlterSender;
import com.dtstack.engine.alert.EventMonitor;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.domain.po.ClusterAlertPO;
import com.dtstack.engine.common.pager.PageResult;
import com.dtstack.engine.master.controller.param.ClusterAlertPageParam;
import com.dtstack.engine.master.controller.param.ClusterAlertParam;
import com.dtstack.engine.pluginapi.pojo.ComponentTestResult;
import com.dtstack.engine.master.vo.AlterSftpVO;
import com.dtstack.engine.master.vo.alert.AlertGateTestVO;
import com.dtstack.engine.master.vo.alert.AlertGateVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.master.event.AlterEnvHandlerEvent;
import com.dtstack.engine.master.event.SftpDownloadEvent;
import com.dtstack.engine.master.impl.AlertChannelService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.utils.CheckUtils;
import dt.insight.plat.lang.web.R;
import com.google.common.collect.Lists;
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

    private final Logger LOGGER = LoggerFactory.getLogger(AlertController.class);

    @Autowired
    private AlterSender alterSender;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private AlertChannelService alertChannelService;

    @Autowired
    private EventMonitor contentReplaceEvent;

    @Autowired(required = false)
    private SftpDownloadEvent sftpDownloadEvent;

    @Autowired
    private AlterEnvHandlerEvent alterEnvHandlerEvent;
    
    private String getPluginPath(boolean isTmp,String gateSource) {
        String tmp = isTmp ? "/tmp" : "/normal";
        return environmentContext.getUploadPath() + tmp + "/" + gateSource;
    }

    @ApiOperation("新增编辑告警通道 用于替换console接口: /api/console/service/alert/edit")
    @PostMapping("/edit")
    public Boolean edit(@RequestParam(value = "file", required = false) MultipartFile file,
                                    AlertGateVO alertGateVO) throws Exception {
        try {
            CheckUtils.checkAlertGateVOFormat(alertGateVO);
            if (alertGateVO.getId() == null) {
                Assert.isTrue(!alertChannelService.checkAlertGateSourceExist(alertGateVO.getAlertGateSource()), "通道标识以重复，请修改通道标识");
            }

            if (file != null) {
                String filePath = this.getPluginPath(false,alertGateVO.getAlertGateSource());
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
                if (environmentContext.getOpenConsoleSftp() && sftpDownloadEvent != null) {
                    // 查询默认集群的sftp
                    dbPath = sftpDownloadEvent.uploadFileToSftp(file, filePath, destPath, dbPath);
                }

                alertGateVO.setFilePath(dbPath);
            } else {
                alertGateVO.setFilePath(null);
            }

            return alertChannelService.addChannelOrEditChannel(alertGateVO);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
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
        AlertGateVO gateById = alertChannelService.getGateById(alertGateVO.getId());
        String filePath = gateById.getFilePath();
        if (StringUtils.isNotBlank(filePath)) {
            String[] split = StringUtils.split(filePath, File.separator);
            gateById.setFilePath(split[split.length - 1]);
        }
        return gateById;
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
        String filePath = this.getPluginPath(false, gateSource);
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
        String filePath = this.getPluginPath(true,alertGateTestVO.getAlertGateSource());
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
        LOGGER.info("testAlert jar path :{}", alertGateTestVO.getFilePath());

        // build test alertParam
        AlterContext alertParam = buildTestAlterContext(alertGateTestVO);
        List<EventMonitor> eventMonitors = Lists.newArrayList();
        eventMonitors.add(contentReplaceEvent);
        eventMonitors.add(alterEnvHandlerEvent);
        R send = null;
        try {
            send = alterSender.sendSyncAlter(alertParam,eventMonitors);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }

        if (send == null) {
            throw new RdosDefineException("未知错误");
        }

        if (!send.isSuccess()) {
            throw new RdosDefineException(send.getMessage());
        }

    }

    @PostMapping("/sftp/get")
    @ApiOperation("获得通道的sftp信息")
    public AlterSftpVO sftpGet() {
        return alertChannelService.sftpGet();
    }

    @PostMapping("/sftp/update")
    @ApiOperation("获得通道的sftp信息")
    public Boolean sftpUpdate(@RequestBody AlterSftpVO vo) {
        return alertChannelService.sftpUpdate(vo);
    }

    @PostMapping("/sftp/testConnect")
    public ComponentTestResult  sftpTestConnect(){
        return alertChannelService.sftpTestConnect(sftpDownloadEvent.getSftpConfig());
    }

    private AlterContext buildTestAlterContext(AlertGateTestVO alertGateTestVO) {
        AlertGateCode parse = AlertGateCode.parse(alertGateTestVO.getAlertGateCode());

        AlterContext result = new AlterContext();
        if (parse == AlertGateCode.AG_GATE_SMS_JAR) {
            List<String> phones = alertGateTestVO.getPhones();
            result.setPhone(phones.get(0));
            result.setContent("测试一下短信拉，别紧张~~(●ﾟωﾟ●) (●ﾟωﾟ●)");
        }

        if (parse == AlertGateCode.AG_GATE_MAIL_DT
                || parse == AlertGateCode.AG_GATE_MAIL_JAR) {
            List<String> emails = alertGateTestVO.getEmails();
            result.setEmails(emails);
            result.setTitle("测试邮件通道");
            result.setContent("测试一下邮件拉，别紧张~~(●ﾟωﾟ●) (●ﾟωﾟ●)");
        }

        if (parse == AlertGateCode.AG_GATE_DING_JAR
                || parse == AlertGateCode.AG_GATE_DING_DT) {
            List<String> dings = alertGateTestVO.getDings();
            result.setDing(dings.get(0));
            result.setTitle("测试钉钉通道");
            result.setContent("测试一下钉钉拉，别紧张~~(●ﾟωﾟ●) (●ﾟωﾟ●)");
        }

        if (parse == AlertGateCode.AG_GATE_CUSTOM_JAR) {
            result.setTitle("测试自定义通道");
            result.setContent("测试一下自定义通道拉，别紧张~~(●ﾟωﾟ●) (●ﾟωﾟ●)");
        }

        result.setAlertGateCode(parse);
        result.setAlertGateJson(alertGateTestVO.getAlertGateJson());
        result.setJarPath(alertGateTestVO.getFilePath());
        result.setAlertTemplate(alertGateTestVO.getAlertTemplate());
        return result;
    }
}
