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

/*
* @Author: 12574
* @Date:   2018-09-18 17:36:36
* @Last Modified by:   12574
* @Last Modified time: 2018-09-28 16:53:11
*/
import * as React from 'react';
import { Modal, message } from 'antd';
import Api from '../../api/console';

class KillTask extends React.Component<any, any> {
    // 请求杀任务接口
    killTask () {
        const { killResource, jobResource, stage, node } = this.props;

        Api.killTasks({
            jobIdList: [killResource.jobId],
            stage,
            jobResource,
            nodeAddress: node
        }).then((res: any) => {
            if (res.code == 1) {
                this.props.killSuccess(killResource.jobId);
                message.success('操作成功');
                this.props.autoRefresh();
                // 异步,成功之后才能关闭
                this.props.onCancel();
            }
        })
    }
    confirmKilltask () {
        this.killTask();
    }
    render () {
        return (
            <Modal
                title="杀任务"
                visible={this.props.visible}
                onCancel={this.props.onCancel}
                onOk={this.confirmKilltask.bind(this)}
            >
                <p>是否要杀死此任务?</p>
            </Modal>
        )
    }
}
export default KillTask;
