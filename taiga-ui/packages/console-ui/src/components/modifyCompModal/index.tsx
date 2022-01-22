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

import * as React from 'react';
import { Modal } from 'antd';
import { COMPONEMT_CONFIG_NAME_ENUM, COMPONENT_TYPE_VALUE } from '../../consts';

export default class ModifyComponentModal extends React.Component<any, any> {
    render () {
        const { handleDeleteComps, modify, handleCancleModify, deleteComps, selectValue } = this.props;
        let modifyCompsNames: any = [];
        deleteComps.forEach((comps: any) => {
            modifyCompsNames.push(comps.componentName)
        })
        const isSource = selectValue[0] === COMPONENT_TYPE_VALUE.YARN || selectValue[0] === COMPONENT_TYPE_VALUE.KUBERNETES;
        return (
            <Modal
                title="修改组件配置"
                onOk={handleDeleteComps}
                onCancel={handleCancleModify}
                visible={modify}
                className="c-clusterManage__modal"
            >
                {
                    isSource
                        ? <span>切换到 {COMPONEMT_CONFIG_NAME_ENUM[selectValue[0]]} 后 {modifyCompsNames[0]} 的配置信息将丢失，确认切换到 {COMPONEMT_CONFIG_NAME_ENUM[selectValue[0]]}？</span>
                        : <span>删除 {modifyCompsNames.join('、')} 组件后相应配置信息将丢失，确定删除 { modifyCompsNames.join('、') } 组件？</span>
                }
            </Modal>
        )
    }
}