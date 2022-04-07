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
import stream from "@/api/stream";
import { FLINK_VERSIONS, INTERVAL_TYPE, STRATEGY_START_TYPE, TASK_TYPE_ENUM } from "@/constant";
import { IEditor } from "@dtinsight/molecule/esm/model";
import { Alert, Col, Collapse, DatePicker, Input, Radio, Row, Select } from "antd";
import { isEmpty } from "lodash";
import moment from "moment";
import React, { useEffect, useMemo, useState } from "react";
import { streamTaskActions } from "../../taskFunc";
import LockPanel from "../lockPanel";
import DateRange from "./dateRange";
import DirtyForm from "./dirtyForm";
import StrategyTimeRange from "./strategyTimeRange";
import WrappedCataForm from "./wrappedForm";
import './index.scss'
import { TAB_WITHOUT_DATA } from "@/pages/rightBar";
import classNames from "classnames";

const Panel = Collapse.Panel;
const Option = Select.Option;
const RangePicker = DatePicker.RangePicker;
const formItemLayout: any = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 }
    }
}

export default function StreamSetting({ current }: Pick<IEditor, 'current'>) {
    const currentPage = current?.tab?.data || {};
    const { streamTaskRetry, streamTaskDirtyDataManageVO, isDirtyDataManage, taskType, componentVersion, strategyId } = currentPage;
    const showDirtyManage = taskType !== TASK_TYPE_ENUM.MR && taskType !== TASK_TYPE_ENUM.DATA_COLLECTION && componentVersion === FLINK_VERSIONS.FLINK_1_12;

    const [strategyList, useStrategyList] = useState([{ id: 0, name: '无' }]);
    const [target, setTarget] = useState<any>()

    const initData = () => {
        stream.getAllStrategy().then((res: any) => {
            if (res.code == 1) {
                const strategyList = [{ id: 0, name: '无' }, ...res.data]
                useState(strategyList);
                strategyId && getTarget(strategyId);
            }
        })
    }
    const getTarget = (id: any) => {
        const target = strategyList.find((tar: any) => tar.id == id);
        setTarget(target);
    }
    const taskChange = (res: any) => {
        const data = {
            streamTaskRetry: { ...res, failRetry: res.failRetry === 1 ? 1 : 0 },
            notSynced: true
        }
        streamTaskActions.updateCurrentPage(data);
    }
    const strategyChange = (newVal: any) => {
        const data = {
            strategyId: newVal,
            notSynced: true
        }
        streamTaskActions.updateCurrentPage(data);
    }
    // 脏数据管理
    const dirtyDataChange = (params: any) => {
        let obj: any = {}
        if ('isDirtyDataManage' in params) {
            obj.isDirtyDataManage = params.isDirtyDataManage;
            delete params.isDirtyDataManage;
        }
        obj.streamTaskDirtyDataManageVO = isEmpty(params) ? null : params;
        obj.notSynced = true // 添加未保存标记
        streamTaskActions.updateCurrentPage(obj);
    }
    const handleStrategyChange = (id: any) => {
        getTarget(id)
        strategyChange(id)
    }

    useEffect(() => {
        initData()
    }, [])

    /**
     * 当前的 tab 是否不合法，如不合法则展示 Empty
     */
    const isInValidTab = useMemo(
        () =>
            !current ||
            !current.activeTab ||
            TAB_WITHOUT_DATA.some((prefix) => current.activeTab?.toString().includes(prefix)),
        [current],
    );
    if (isInValidTab) {
		return <div className={classNames('text-center', 'mt-10px')}>无法获取任务设置</div>;
	}
    const dateFormat = 'YYYY-MM-DD';
    const strategyOptions = strategyList && strategyList.map((item: any) => {
        return (<Option key={item.id} value={`${item.id}`}>{item.name}</Option>)
    })
    const extendConfig = target?.extendConfig ? JSON.parse(target.extendConfig) : {};
    return <Collapse className="strategy-panel" bordered={false} defaultActiveKey={showDirtyManage ? ['1', '2', '3'] : ['1', '2']}>
        <Panel key="1" header="任务设置">
            <Row className="task-info strategy-info">
                <WrappedCataForm formItemLayout={formItemLayout} defaultData={streamTaskRetry} taskChange={taskChange} />
            </Row>
            <LockPanel lockTarget={currentPage} />
        </Panel>
        <Panel key="2" header="启停策略">
            <Row className="task-info strategy-info">
                <Alert
                    closable
                    showIcon
                    type="info"
                    style={{ marginBottom: 20 }}
                    message="任务保存后，启停策略立刻生效" />
                <Row>
                    <Col span={5} className="txt-right">策略名称：</Col>
                    <Col span={19}>
                        <Select
                            className="strategy-select"
                            optionFilterProp="name"
                            placeholder="请选择策略"
                            onChange={handleStrategyChange}
                            value={target?.id ? `${target.id}` : '0'}
                            showSearch
                        >
                            {strategyOptions}
                        </Select>
                    </Col>
                </Row>
                {target?.id ? <React.Fragment>
                    <Row>
                        <Col span={5} className="txt-right">时区：</Col>
                        <Col span={19}>
                            <Input disabled defaultValue={target?.timeZone} />
                        </Col>
                    </Row>
                    <Row>
                        <Col span={5} className="txt-right">生效区间：</Col>
                        <Col span={19}>
                            <Radio.Group value={target?.intervalType} disabled>
                                <Radio value={INTERVAL_TYPE.EVERY}>每天</Radio>
                                <Radio value={INTERVAL_TYPE.WORKDAY}>周一至周五</Radio>
                                <Radio value={INTERVAL_TYPE.CUSTOM}>自定义</Radio>
                            </Radio.Group>
                        </Col>
                    </Row>
                    {
                        target?.intervalType === INTERVAL_TYPE.CUSTOM
                            ? (
                                <React.Fragment>
                                    <Row>
                                        <Col span={5} className="txt-right">自定义文件：</Col>
                                        <Col span={19}>
                                            <span>{extendConfig.csvFile?.name}</span>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span={5} className="txt-right">区间预览：</Col>
                                        <Col span={19}>
                                            <DateRange value={extendConfig.date || []} type="single" />
                                        </Col>
                                    </Row>
                                </React.Fragment>
                            ) : (
                                <Row>
                                    <Col span={5} className="txt-right">日期范围：</Col>
                                    <Col span={19}>
                                        <div className="c-strategyPanel__time">
                                            {target?.strategyDateVOS.map((item: any) => {
                                                return (
                                                    <RangePicker
                                                        key={item.id}
                                                        disabled
                                                        value={[(moment(item.startDate, dateFormat) as any), (moment(item.endDate, dateFormat) as any)]}
                                                        className="strategy-rangePicker"
                                                    />
                                                )
                                            })}
                                        </div>
                                    </Col>
                                </Row>
                            )
                    }
                    <Row>
                        <Col span={5} className="txt-right">时间范围：</Col>
                        <Col span={19}>
                            <StrategyTimeRange value={target?.strategyTimeVOS || []} disabled />
                        </Col>
                    </Row>
                    <Row>
                        <Col span={5} className="txt-right">启动方式：</Col>
                        <Col span={19}>
                            <Radio.Group value={target?.startType} disabled>
                                {STRATEGY_START_TYPE.map((item: any) => <Radio key={item.value} value={item.value}>{item.text}</Radio>)}
                            </Radio.Group>
                        </Col>
                    </Row>
                </React.Fragment> : null}
            </Row>
            <LockPanel lockTarget={currentPage} />
        </Panel>
        {showDirtyManage && (
            <Panel key="3" header="脏数据管理">
                <Row className="task-info strategy-info">
                    <DirtyForm
                        formItemLayout={formItemLayout}
                        data={streamTaskDirtyDataManageVO || {}}
                        isDirtyDataManage={isDirtyDataManage}
                        onChange={dirtyDataChange}
                    />
                </Row>
                <LockPanel lockTarget={currentPage} />
            </Panel>
        )}
    </Collapse>
}