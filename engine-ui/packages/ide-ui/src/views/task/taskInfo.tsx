import React from 'react';
import { Col, Row, Tag } from 'antd';
import { IEditor } from 'molecule/esm/model';
import { TaskType } from '../../components/status';
import { formatDateTime } from '../../comm';
import { TASK_TYPE } from '../../comm/const';
import './taskInfo.scss';

export default function TaskInfo({ current }: IEditor) {
    if (!current?.activeTab)
        return (
            <div style={{ marginTop: 8, textAlign: 'center' }}>
                无法提供活动属性
            </div>
        );
    const taskInfo = current.tab!.data;
    const labelPrefix = '任务';
    return (
        <Row className="task-info">
            <Row className="task-info-line">
                <Col span={10} className="txt-left">
                    {labelPrefix}名称
                </Col>
                <Col span={14} className="txt-right">
                    {taskInfo.name}
                </Col>
            </Row>
            <Row className="task-info-line">
                <Col span={10} className="txt-left">
                    {labelPrefix}类型
                </Col>
                <Col span={14} className="txt-right">
                    <TaskType value={taskInfo.taskType} />
                </Col>
            </Row>
            {(taskInfo.taskType === TASK_TYPE.SQL ||
                taskInfo.taskType === TASK_TYPE.MR ||
                taskInfo.taskType === TASK_TYPE.PYTHON) && (
                <Row className="task-info-line">
                    <Col span={10} className="txt-left">
                        Spark引擎版本
                    </Col>
                    <Col span={14} className="txt-right">
                        {taskInfo.componentVersion}
                    </Col>
                </Row>
            )}
            {taskInfo.resourceList &&
                taskInfo.resourceList.length > 0 &&
                taskInfo.resourceList !== [] && (
                    <Row className="task-info-line">
                        <Col span={10} className="txt-left">
                            资源
                        </Col>
                        <Col span={14} className="txt-right">
                            {taskInfo.resourceList.map((o: any) => (
                                <Tag
                                    key={o.id}
                                    color="blue"
                                    style={{ marginTop: 10 }}
                                >
                                    {o.resourceName}
                                </Tag>
                            ))}
                        </Col>
                    </Row>
                )}
            <Row className="task-info-line">
                <Col span={10} className="txt-left">
                    创建时间
                </Col>
                <Col span={14} className="txt-right">
                    {formatDateTime(taskInfo.gmtCreate)}
                </Col>
            </Row>
            <Row className="task-info-line">
                <Col span={10} className="txt-left">
                    最近修改时间
                </Col>
                <Col span={14} className="txt-right">
                    {formatDateTime(taskInfo.gmtModified)}
                </Col>
            </Row>
            <Row>
                <Col span={10} className="txt-left">
                    描述
                </Col>
                <Col
                    span={14}
                    style={{
                        lineHeight: '40px',
                        padding: '10 0',
                    }}
                    className="txt-right"
                >
                    {taskInfo.taskDesc ? taskInfo.taskDesc : '—'}
                </Col>
            </Row>
        </Row>
    );
}
