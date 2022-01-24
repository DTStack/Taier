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

import React, { useState } from 'react';
import { message, Modal, Form, Input, Row, Col } from 'antd';
import { formItemLayout } from '@/constant';
import ajax from '../../api';

const { confirm } = Modal;
const FormItem = Form.Item;

export const CONTAINER_ID = 'container_wrapper';

export default ({ data }: any) => {
  const [publishDesc, changeDesc] = useState('');
  const [visible, changeVisible] = useState(true);
  const [loading, changeLoading] = useState(false);
  const submitTab = () => {
    const params = {
      ...data,
      sqlText: data.value,
      publishDesc,
    };
    // 添加发布描述信息
    if (publishDesc.length > 200) {
      message.error('备注信息不可超过200个字符！');
      return false;
    }
    checkPublishTask(params);
  };
  const checkPublishTask = (result: any, ignoreCheck?: boolean) => {
    result.ignoreCheck = ignoreCheck;
    changeLoading(true);
    delete result.dtuicTenantId;
    delete result.language;
    delete result.appType;
    delete result.componentVersion;
    delete result.increColumn;
    delete result.input;
    delete result.isPublishToProduce;
    ajax
      .publishOfflineTask(result)
      .then((res: any) => {
        changeLoading(false);
        const { data, code } = res;
        if (code === 1) {
          switch (data?.errorSign) {
            case 0: {
              message.success('提交成功！');
              changeVisible(false);
              break;
            }
            case 1: {
              changeVisible(false);
              return Modal.warning({
                title: '无法提交任务',
                content: <p>{data?.errorMessage || '未知错误'}</p>,
              });
            }
            default: {
              confirm({
                title: '无法提交任务',
                content: <p>{data?.errorMessage || '未知错误'}</p>,
                okText: '仍要提交',
                cancelText: '确定',
                onOk() {
                  checkPublishTask(result, false);
                },
                onCancel() {
                  changeVisible(false);
                },
              });
            }
          }
        }
      })
      .finally(() => {
        changeLoading(false);
        changeVisible(false);
      });
  };
  return (
    <Modal
      wrapClassName="vertical-center-modal"
      title="提交任务"
      getContainer={() => document.getElementById(CONTAINER_ID)!}
      prefixCls="ant-modal"
      style={{ height: '600px', width: '600px' }}
      visible={visible}
      onCancel={() => changeVisible(false)}
      onOk={() => submitTab()}
      confirmLoading={loading}
      cancelText="关闭"
    >
      <Form>
        <FormItem {...formItemLayout} label={<span>备注</span>} hasFeedback>
          <Input.TextArea
            value={publishDesc}
            name="publishDesc"
            rows={4}
            onChange={(e) => {
              changeDesc(e.target.value);
            }}
          />
        </FormItem>
      </Form>
      <Row>
        <Col offset={6} span={15}>
          注意：提交过的任务才能被调度执行及发布到其他项目
        </Col>
      </Row>
    </Modal>
  );
};
