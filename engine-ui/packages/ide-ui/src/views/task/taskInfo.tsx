import React from 'react';
import { Col, Row } from 'antd';
import { IEditor } from 'molecule/esm/model';
import { TaskType } from '../../components/status';
import { formatDateTime } from '../../comm';

export default function TaskInfo({ current }: IEditor) {
    if (!current?.activeTab)
        return (
            <div style={{ marginTop: 10, textAlign: 'center', color: '#fff' }}>
                无法提供活动属性
            </div>
        );
    const tab = current.tab!;
    //   const taskInfo = props.taskInfo;
    const labelPrefix = '任务';
    return (
        <Row className="task-info">
            <Row>
                <Col span={10} className="txt-right">
                    {labelPrefix}名称：
                </Col>
                <Col span={14}>{tab.name}</Col>
            </Row>
            <Row>
                <Col span={10} className="txt-right">
                    {labelPrefix}类型：
                </Col>
                <Col span={14}>
                    <TaskType value={tab.data.taskType} />
                </Col>
            </Row>
            <Row>
                <Col span={10} className="txt-right">
                    创建时间：
                </Col>
                <Col span={14}>{formatDateTime(tab.data.gmtCreate)}</Col>
            </Row>
            <Row>
                <Col span={10} className="txt-right">
                    最近修改时间：
                </Col>
                <Col span={14}>{formatDateTime(tab.data.gmtModified)}</Col>
            </Row>
            <Row>
                <Col span={10} className="txt-right">
                    描述：
                </Col>
                <Col
                    span={14}
                    style={{
                        lineHeight: '20px',
                        padding: '10 0',
                    }}
                >
                    {tab.data.taskDesc}
                </Col>
            </Row>
        </Row>
    );
}
