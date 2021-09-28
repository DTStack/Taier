/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.controller;

import com.dtstack.engine.alert.enums.AlertGateTypeEnum;
import com.dtstack.engine.master.controller.param.AlarmSendParam;
import com.dtstack.engine.master.controller.param.ClusterAlertPageParam;
import com.dtstack.engine.master.controller.param.ClusterAlertParam;
import com.dtstack.engine.master.controller.param.NotifyRecordParam;
import com.dtstack.engine.master.vo.alert.AlertGateTestVO;
import com.dtstack.engine.master.vo.alert.AlertGateVO;
import com.dtstack.engine.domain.AlertChannel;
import com.dtstack.engine.domain.AlertContent;
import com.dtstack.engine.domain.AlertRecord;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @Auther: dazhi
 * @Date: 2021/1/25 2:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlertControllerTest extends AbstractTest {

    @Autowired
    private AlertController alertController;

    @Autowired
    private NotifyRecordController notifyRecordController;

    private AlertChannel defaultAlterChannelComJar;
    private AlertChannel defaultAlterChannelDingDt;
    private AlertChannel defaultAlterChannelDingJar;
    private AlertChannel defaultAlterChannelMailDt;
    private AlertChannel defaultAlterChannelMailJar;
    private AlertChannel defaultAlterChannelSmsJar;

    private AlertRecord alertRecord;

    private AlertContent alertContent;

    @Before
    public void before(){
        this.defaultAlterChannelComJar = DataCollection.getData().getDefaultAlterChannelComJar();
        this.defaultAlterChannelDingDt = DataCollection.getData().getDefaultAlterChannelDingDt();
        this.defaultAlterChannelDingJar = DataCollection.getData().getDefaultAlterChannelDingJar();
        this.defaultAlterChannelMailDt = DataCollection.getData().getDefaultAlterChannelMailDt();
        this.defaultAlterChannelMailJar = DataCollection.getData().getDefaultAlterChannelMailJar();
        this.defaultAlterChannelSmsJar = DataCollection.getData().getDefaultAlterChannelSmsJar();

        this.alertRecord = DataCollection.getData().getDefaultRecord();
    }
    @Test
    public void editText() {
        AlertGateVO alertGateVO = new AlertGateVO();
        alertGateVO.setAlertGateCode("sms_jar");
        alertGateVO.setAlertGateJson("{\"className\":\"com.dtstack.sdk.example.ISmsChannelExample\"}");
        alertGateVO.setAlertGateName("测试通道");
        alertGateVO.setAlertGateSource("abc");
        alertGateVO.setIsDefault(0);

        MultipartFile file = new MockMultipartFile("ceshi", "ceshi".getBytes());

        try {
            alertController.edit(file, alertGateVO);
            alertGateVO.setId(1L);
            alertController.edit(file,alertGateVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setDefaultAlert() {
        ClusterAlertParam clusterAlertParam = new ClusterAlertParam();
        clusterAlertParam.setAlertId(defaultAlterChannelSmsJar.getId());
        clusterAlertParam.setAlertGateType(defaultAlterChannelSmsJar.getAlertGateType());
        alertController.setDefaultAlert(clusterAlertParam);

    }

    @Test
    public void setPageTest() {
        ClusterAlertPageParam pageParam = new ClusterAlertPageParam();
        pageParam.setCurrentPage(1);
        pageParam.setPageSize(15);
        alertController.page(pageParam);

    }

    @Test
    public void getByAlertIdTest() {
        AlertGateVO alertGateVO = new AlertGateVO();
        alertGateVO.setId(1L);

        alertController.getByAlertId(alertGateVO);
    }

    @Test
    public void getShowList() {
        alertController.listShow();
    }



    @Test
    public void getTestJar() throws Exception {
        String classPath = this.getClass().getResource("/").getPath();
        byte[] bytes = FileUtils.readFileToByteArray(new File(classPath + "/alter/console-alert-plugin-sdk-example-4.0.0.jar"));
        MultipartFile multipartFile = new MockMultipartFile("console-alert-plugin-sdk-example-4.0.0.jar","console-alert-plugin-sdk-example-4.0.0.jar","jar",bytes);


        // 测试邮箱Dt
        try {
            // 测试短信jar 发送
            AlertGateTestVO alertGateTestSmsJarVO = new AlertGateTestVO();
            alertGateTestSmsJarVO.setPhones(Lists.newArrayList("13982756743"));
            testSmsJar(alertGateTestSmsJarVO,multipartFile,defaultAlterChannelSmsJar);

            // 测试邮箱jar 发送
            AlertGateTestVO alertGateTestMailJarVO = new AlertGateTestVO();
            alertGateTestMailJarVO.setEmails(Lists.newArrayList("13982756743@qq.com"));
            testSmsJar(alertGateTestMailJarVO,multipartFile,defaultAlterChannelMailJar);

            // 测试dingjar 发送
            AlertGateTestVO alertGateTestDingJarVO = new AlertGateTestVO();
            alertGateTestDingJarVO.setDings(Lists.newArrayList("https://oapi.dingtalk.com/robot/send?access_token=16cc0086eeef4f4f905ce4eda70be58bbe8ec9ecb45fb58c55706fac07e50530"));
            testSmsJar(alertGateTestDingJarVO,multipartFile,defaultAlterChannelDingJar);

            // 测试自定义 发送
            AlertGateTestVO alertGateTestComJarVO = new AlertGateTestVO();
            testSmsJar(alertGateTestComJarVO,multipartFile,defaultAlterChannelComJar);

            AlertGateTestVO alertGateTestMailDtVO = new AlertGateTestVO();
            alertGateTestMailDtVO.setEmails(Lists.newArrayList("1306123139@qq.com"));
            testSmsJar(alertGateTestMailJarVO,multipartFile,defaultAlterChannelMailDt);

            // 测试dingjar 发送
            AlertGateTestVO alertGateTestDingDtVO = new AlertGateTestVO();
            alertGateTestDingDtVO.setDings(Lists.newArrayList("https://oapi.dingtalk.com/robot/send?access_token=16cc0086eeef4f4f905ce4eda70be58bbe8ec9ecb45fb58c55706fac07e50530"));
            testSmsJar(alertGateTestDingJarVO,multipartFile,defaultAlterChannelDingDt);
        } catch (Exception e) {
        }
    }

    private void testSmsJar(AlertGateTestVO alertGateTestVO,MultipartFile multipartFile,AlertChannel alertChannel) throws Exception {
        alertGateTestVO.setClusterId(-1L);
        alertGateTestVO.setAlertGateCode(alertChannel.getAlertGateCode());
        alertGateTestVO.setAlertGateJson(alertChannel.getAlertGateJson());
        alertGateTestVO.setAlertGateName("有要");
        alertGateTestVO.setAlertGateSource(alertChannel.getAlertGateSource());
        alertGateTestVO.setFilePath(alertChannel.getFilePath());
        alertGateTestVO.setIsDefault(0);

        alertController.testAlert(multipartFile,alertGateTestVO);
    }

    @Test
    public void getRecordOne() throws Exception {
        NotifyRecordParam notifyRecordParam = new NotifyRecordParam();
        notifyRecordParam.setReadId(alertRecord.getId());
        notifyRecordParam.setAppType(1);
        notifyRecordController.getOne(notifyRecordParam);
    }

    @Test
    public void sendAlarmNewTest(){
        // 生成内容
        NotifyRecordParam notifyRecordParam = new NotifyRecordParam();
        notifyRecordParam.setAppType(7);
        notifyRecordParam.setContent("测试一下");
        notifyRecordParam.setProjectId(1L);
        notifyRecordParam.setTenantId(1L);
        notifyRecordParam.setStatus(1);
        Long contentId = notifyRecordController.generateContent(notifyRecordParam);

        AlarmSendParam param = new AlarmSendParam();
        param.setContentId(contentId);
        param.setTitle("测试");
        param.setWebhook("https://oapi.dingtalk.com/robot/send?access_token=16cc0086eeef4f4f905ce4eda70be58bbe8ec9ecb45fb58c55706fac07e50530");
        param.setAlertGateSources(Lists.newArrayList(AlertGateTypeEnum.getDefaultFiled(AlertGateTypeEnum.DINGDING)));
        param.setTenantId(1L);
        param.setStatus(1);
        param.setProjectId(1L);
        param.setAppType(7);
        notifyRecordController.sendAlarmNew(param);
    }




}
