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

import { useState } from 'react';
import { Alert,message, Modal } from 'antd';

import { getTenantId, getUserId } from '@/utils';
import ajax from '../../api';

export const CONTAINER_ID = 'container_wrapper';

export default ({ taskId }: { taskId: number }) => {
    const [visible, changeVisible] = useState(true);
    const [loading, changeLoading] = useState(false);

    const checkPublishTask = () => {
        changeLoading(true);
        ajax.publishOfflineTask({
            id: taskId,
            tenantId: getTenantId(),
            userId: getUserId(),
            preSave: true,
        })
            .then((res) => {
                const { code } = res;
                if (code === 1) {
                    message.success('提交成功！');
                    changeVisible(false);
                }
            })
            .finally(() => {
                changeLoading(false);
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
            onOk={() => checkPublishTask()}
            confirmLoading={loading}
            cancelText="关闭"
        >
            <Alert message="提交过的任务才能被调度执行及发布到其他项目" type="warning" closable={false} />
            <br />
            <Alert message="新提交的任务需要第二天才能生成周期实例" type="warning" closable={false} />
        </Modal>
    );
};
