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

import React from 'react';
import { Col, Row } from 'antd';
import { IEditor } from '@dtinsight/molecule/esm/model';
import { TaskType } from '../../components/status';
import { formatDateTime } from '../../comm';

export default function TaskInfo({ current }: IEditor) {
    if (!current?.activeTab)
        return (
            <div style={{ marginTop: 8, textAlign: 'center' }}>
                无法提供活动属性
            </div>
        );
    const tab = current.tab!;
    const labelPrefix = '任务';
    return (
        <Row className="task-info" style={{ padding: '5px 20px' }}>
            <Row>
                <Col span={8} >
                    {labelPrefix}名称：
                </Col>
                <Col span={8}>{tab.name}</Col>
            </Row>
            <Row>
                <Col span={8}>
                    {labelPrefix}类型：
                </Col>
                <Col span={14}>
                    <TaskType value={tab.data.taskType} />
                </Col>
            </Row>
            <Row>
                <Col span={8}>
                    创建时间：
                </Col>
                <Col span={14}>{formatDateTime(tab.data.gmtCreate)}</Col>
            </Row>
            <Row>
                <Col span={8}>
                    修改时间：
                </Col>
                <Col span={14}>{formatDateTime(tab.data.gmtModified)}</Col>
            </Row>
            <Row>
                <Col span={8}>
                    描述：
                </Col>
                <Col
                    span={14}
                    style={{
                        lineHeight: '20px',
                        padding: '10 0',
                    }}
                >
                    {tab.data.taskDesc || '-'}
                </Col>
            </Row>
        </Row>
    );
}
