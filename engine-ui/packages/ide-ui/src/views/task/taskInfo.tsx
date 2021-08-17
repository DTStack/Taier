import React from 'react';
import { Col, Row } from 'antd';
import { IEditor } from 'molecule/esm/model';

export default function TaskInfo({ current }: IEditor) {
    if (!current?.activeTab)
        return (
            <div style={{ marginTop: 10, textAlign: 'center', color: '#fff' }}>
                无法提供活动属性
            </div>
        );
    const tab = current.tab;
    //   const taskInfo = props.taskInfo;
    //   const couldEdit = props.couldEdit;
    const labelPrefix = '任务';
    return (
        <Row className="task-info">
            <Row>
                <Col span={10} className="txt-right">
                    {labelPrefix}名称：
                </Col>
                <Col span={14}>{tab?.name}</Col>
            </Row>
            <Row>
                <Col span={10} className="txt-right">
                    {labelPrefix}类型：
                </Col>
                <Col span={14}>
                    {/* <TaskType value={taskInfo.taskType} /> */}
                </Col>
            </Row>
            {/* {taskInfo.resourceList.length && (
        <Row>
          <Col span={10} className="txt-right">
            资源：
          </Col>
          <Col span={14}>
            {taskInfo.resourceList.map((o: any) => (
              <Tag key={o.id} color="blue" style={{ marginTop: 10 }}>
                {o.resourceName}
              </Tag>
            ))}
          </Col>
        </Row>
      )} */}

            <Row>
                <Col span={10} className="txt-right">
                    创建时间：
                </Col>
                {/* <Col span={14}>{utils.formatDateTime(taskInfo.gmtCreate)}</Col> */}
            </Row>
            <Row>
                <Col span={10} className="txt-right">
                    最近修改时间：
                </Col>
                {/* <Col span={14}>{utils.formatDateTime(taskInfo.gmtModified)}</Col> */}
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
                    {/* {taskInfo.taskDesc} */}
                </Col>
            </Row>
        </Row>
    );
}
