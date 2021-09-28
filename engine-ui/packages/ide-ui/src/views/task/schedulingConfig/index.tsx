import React from 'react';
import { Row, Col, Collapse, Radio, message } from 'antd';
import FormWrap from './scheduleForm';
import TaskDependence from './taskDependence';
import molecule from 'molecule/esm';
import { TASK_TYPE } from '../../../comm/const';
import HelpDoc from '../../../components/helpDoc';
import Ajax from '../../../api';
import {
    EDIT_TASK_PREFIX,
    EDIT_FOLDER_PREFIX,
    CREATE_TASK_PREFIX,
} from '../../common/utils/const';

const Panel = Collapse.Panel;
const RadioGroup = Radio.Group;

const radioStyle: any = {
    display: 'block',
    height: '30px',
    lineHeight: '30px',
};
const getDefaultScheduleConf = (value: any) => {
    const scheduleConf: any = {
        0: {
            beginMin: 0,
            endMin: 59,
            beginHour: 0,
            endHour: 23,
            gapMin: 5,
            periodType: 0,
            beginDate: '2001-01-01',
            endDate: '2121-01-01',
        },
        1: {
            beginHour: 0,
            endHour: 23,
            beginMin: 0,
            gapHour: 5,
            periodType: 1,
        },
        2: {
            min: 0,
            hour: 0,
            periodType: 2,
            beginDate: '2001-01-01',
            endDate: '2121-01-01',
        },
        3: {
            weekDay: 3,
            min: 0,
            hour: 23,
            periodType: 3,
        },
        4: {
            day: 5,
            hour: 0,
            min: 23,
            periodType: 4,
        },
    };

    return scheduleConf[value];
};
export class SchedulingConfig extends React.Component<any, any> {
    state = {
        selfReliance: undefined,
    };

    form: any;

    componentDidMount() {
        const { current, isIncrementMode } = this.props;
        if (!current) return;
        const tabData = current.tab.data!;
        let scheduleConf;
        try {
            scheduleConf = JSON.parse(tabData.scheduleConf);
        } catch (error) {
            scheduleConf = {};
        }
        let selfReliance = 0;
        // 此处为兼容代码
        // scheduleConf.selfReliance兼容老代码true or false 值
        if (typeof scheduleConf.selfReliance !== 'undefined') {
            if (scheduleConf.selfReliance === true) {
                selfReliance = 1;
            } else if (scheduleConf.selfReliance === false) {
                selfReliance = 0;
            } else if (scheduleConf.selfReliance) {
                selfReliance = scheduleConf.selfReliance;
            }
        }
        // 增量同步做默认处理
        if (isIncrementMode && selfReliance !== 1) {
            selfReliance = 3;
        }

        this.setState({
            selfReliance: selfReliance,
        });
    }

    // 调度状态change处理函数
    handleScheduleStatus(evt: any) {
        const { checked } = evt.target;
        const status = checked ? 2 : 1;
        const { current, changeScheduleConf } = this.props;
        const { data: tabData } = current.tab;
        const sucInfo = checked ? '冻结成功' : '解冻成功';
        const errInfo = checked ? '冻结失败' : '解冻失败';
        Ajax.forzenTask({
            taskIdList: [tabData.id],
            scheduleStatus: status, //  1正常调度, 2暂停 NORMAL(1), PAUSE(2),
        }).then((res: any) => {
            if (res.code === 1) {
                // mutate
                const newData = {
                    scheduleStatus: status,
                };
                changeScheduleConf(current.tab, newData);
                this.form.props.form.setFieldsValue({
                    scheduleStatus: checked,
                });
                message.info(sucInfo);
            } else {
                this.form.props.form.setFieldsValue({
                    scheduleStatus: checked,
                });
                message.error(errInfo);
            }
        });
    }

    // 调度依赖change处理方法
    handleScheduleConf = () => {
        const { current, changeScheduleConf } = this.props;
        const tabData = current.tab.data;
        let defaultScheduleConf: any;
        try {
            defaultScheduleConf = JSON.parse(tabData.scheduleConf);
        } catch (error) {
            defaultScheduleConf = {};
        }
        if (!defaultScheduleConf.periodType) {
            defaultScheduleConf = getDefaultScheduleConf(2);
        }
        this.form.props.form.validateFields(
            { force: true },
            (err: any, values: any) => {
                if (!err) {
                    let formData = this.form.props.form.getFieldsValue();
                    formData.selfReliance = this.state.selfReliance;
                    /**
                     * 默认重试次数 3次
                     */
                    if (formData.isFailRetry) {
                        if (!formData.maxRetryNum) {
                            formData.maxRetryNum = 3;
                        }
                    } else {
                        formData.maxRetryNum = undefined;
                    }
                    formData = Object.assign(defaultScheduleConf, formData);
                    delete formData.scheduleStatus;
                    const newData = {
                        scheduleConf: JSON.stringify(formData),
                    };
                    changeScheduleConf(current.tab, newData);
                }
            }
        );
    };

    // 调度周期change处理函数
    handleScheduleType(type: any) {
        const { current, changeScheduleConf } = this.props;
        const dft = getDefaultScheduleConf(type);
        const isFailRetry = this.form.props.form.getFieldValue('isFailRetry');
        const values = Object.assign({}, dft, {
            scheduleStatus:
                this.form.props.form.getFieldValue('scheduleStatus'),
            periodType: type,
            isFailRetry: this.form.props.form.getFieldValue('isFailRetry'),
            beginDate: this.form.props.form.getFieldValue('beginDate'),
            endDate: this.form.props.form.getFieldValue('endDate'),
            selfReliance: this.form.props.form.getFieldValue('selfReliance'),
        });
        if (isFailRetry) {
            values.maxRetryNum =
                this.form.props.form.getFieldValue('maxRetryNum');
        }
        const newData = {
            scheduleConf: JSON.stringify(values),
        };
        changeScheduleConf(current.tab, newData);
    }

    // 任务间依赖change处理方法
    handleTaskVOSChange = (newTaskVOS: any) => {
        const { current, changeScheduleConf } = this.props;
        changeScheduleConf(current.tab, { taskVOS: newTaskVOS });
    };

    setSelfReliance(evt: any) {
        const value = evt.target.value;
        this.setState(
            {
                selfReliance: value,
            },
            () => {
                this.handleScheduleConf();
            }
        );
    }

    getInitScheduleConf = () => {
        const { isWorkflowNode, current } = this.props;
        const tabData = current.tab.data;
        let initConf: any;
        try {
            initConf = JSON.parse(tabData.scheduleConf);
        } catch (error) {
            initConf = {};
        }

        let scheduleConf = Object.assign(getDefaultScheduleConf(0), {
            beginDate: '2001-01-01',
            endDate: '2121-01-01',
        });

        scheduleConf = Object.assign(scheduleConf, initConf);
        // 工作流更改默认调度时间配置
        if (isWorkflowNode) {
            scheduleConf = Object.assign(
                getDefaultScheduleConf(2),
                {
                    beginDate: '2001-01-01',
                    endDate: '2121-01-01',
                },
                scheduleConf
            );
            scheduleConf.periodType = 2;
        }

        return scheduleConf;
    };

    render() {
        const { selfReliance } = this.state;
        const { current, isWorkflowNode, isIncrementMode, isScienceTask } =
            this.props;
        if (
            !current ||
            !current.activeTab ||
            current.activeTab.includes(EDIT_TASK_PREFIX) ||
            current.activeTab.includes(EDIT_FOLDER_PREFIX) ||
            current.activeTab.includes(CREATE_TASK_PREFIX)
        ) {
            return (
                <div
                    style={{
                        marginTop: 10,
                        textAlign: 'center',
                    }}
                >
                    无法获取调度依赖
                </div>
            );
        }
        const tabData = current.tab.data;
        const scheduleConf = this.getInitScheduleConf();

        return (
            <molecule.component.Scrollable>
                <div className="m-scheduling" style={{ position: 'relative' }}>
                    <Collapse
                        bordered={false}
                        defaultActiveKey={['1', '2', '3']}
                    >
                        <Panel key="1" header="调度属性">
                            <FormWrap
                                scheduleConf={scheduleConf}
                                status={tabData.scheduleStatus}
                                handleScheduleStatus={this.handleScheduleStatus.bind(
                                    this
                                )}
                                handleScheduleConf={this.handleScheduleConf.bind(
                                    this
                                )}
                                handleScheduleType={this.handleScheduleType.bind(
                                    this
                                )}
                                wrappedComponentRef={(el: any) =>
                                    (this.form = el)
                                }
                                key={`${tabData.id}-${scheduleConf?.periodType}`}
                            />
                        </Panel>
                        {!isWorkflowNode &&
                            tabData.taskType !== TASK_TYPE.VIRTUAL_NODE && (
                                <Panel key="2" header="任务间依赖">
                                    <TaskDependence
                                        current={current}
                                        handleTaskVOSChange={
                                            this.handleTaskVOSChange
                                        }
                                        tabData={tabData}
                                    />
                                </Panel>
                            )}
                        {!isWorkflowNode && (
                            <Panel key="3" header="跨周期依赖">
                                <Row style={{ marginBottom: '16px' }}>
                                    <Col span={1} />
                                    <Col>
                                        <RadioGroup
                                            disabled={isScienceTask}
                                            onChange={this.setSelfReliance.bind(
                                                this
                                            )}
                                            value={selfReliance}
                                        >
                                            {!isIncrementMode && (
                                                <Radio
                                                    style={radioStyle}
                                                    value={0}
                                                >
                                                    不依赖上一调度周期
                                                </Radio>
                                            )}
                                            <Radio style={radioStyle} value={1}>
                                                自依赖，等待上一调度周期成功，才能继续运行
                                            </Radio>
                                            <Radio style={radioStyle} value={3}>
                                                自依赖，等待上一调度周期结束，才能继续运行&nbsp;
                                                <HelpDoc
                                                    style={{
                                                        position: 'inherit',
                                                    }}
                                                    doc={
                                                        !isIncrementMode
                                                            ? 'taskDependentTypeDesc'
                                                            : 'incrementModeScheduleTypeHelp'
                                                    }
                                                />
                                            </Radio>
                                            {!isIncrementMode && (
                                                <Radio
                                                    style={radioStyle}
                                                    value={2}
                                                >
                                                    等待下游任务的上一周期成功，才能继续运行
                                                </Radio>
                                            )}
                                            {!isIncrementMode && (
                                                <Radio
                                                    style={radioStyle}
                                                    value={4}
                                                >
                                                    等待下游任务的上一周期结束，才能继续运行&nbsp;
                                                    <HelpDoc
                                                        style={{
                                                            position: 'inherit',
                                                        }}
                                                        doc="taskDependentTypeDesc"
                                                    />
                                                </Radio>
                                            )}
                                        </RadioGroup>
                                    </Col>
                                </Row>
                            </Panel>
                        )}
                    </Collapse>
                </div>
            </molecule.component.Scrollable>
        );
    }
}
