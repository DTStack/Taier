import React from 'react';
import { Col, Row } from 'antd';
import { IEditor } from 'molecule/esm/model';
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
