import * as React from 'react';
import { connect } from 'react-redux';
import assign from 'object-assign';

import {
    Row,
    Col,
    Collapse,
    Radio,
    message
} from 'antd';
import FormWrap from './scheduleForm';
import TaskDependence from './taskDependence';

import ajax from '../../../../api';
import { workbenchAction } from '../../../../store/modules/offlineTask/actionType';
import { TASK_TYPE } from '../../../../comm/const';
import HelpDoc from '../../../helpDoc';

const Panel = Collapse.Panel;
const RadioGroup = Radio.Group;

class SchedulingConfig extends React.Component<any, any> {
    constructor (props: any) {
        super(props);

        this.state = {
            wFScheduleConf: undefined,
            selfReliance: undefined
        }
    }

    componentDidMount () {
        this.loadWorkflowConfig();
        const { tabData, isIncrementMode } = this.props;
        let scheduleConf = JSON.parse(tabData.scheduleConf);
        let selfReliance = 0;
        // 此处为兼容代码
        // scheduleConf.selfReliance兼容老代码true or false 值
        if (scheduleConf.selfReliance !== 'undefined') {
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
            selfReliance: selfReliance
        });

        this.loadWorkflowConfig();
    }

    loadWorkflowConfig = () => {
        const { tabData, isWorkflowNode, tabs } = this.props;
        if (!isWorkflowNode) return;
        const workflowId = tabData.flowId;
        const workflow = tabs && tabs.find((item: any) => item.id === workflowId);

        const setWfConf = (task: any) => {
            const wFScheduleConf = JSON.parse(task.scheduleConf);
            this.setState({
                wFScheduleConf
            })
        }
        if (workflow) {
            setWfConf(workflow);
        } else {
            ajax.getOfflineTaskDetail({
                id: workflowId
            }).then((res: any) => {
                if (res.code === 1) {
                    setWfConf(res.data);
                }
            });
        }
    }

    handleScheduleStatus (evt: any) {
        const { checked } = evt.target;
        const status = checked ? 2 : 1;
        const { tabData } = this.props;
        const succInfo = checked ? '冻结成功' : '解冻成功';
        const errInfo = checked ? '冻结失败' : '解冻失败';

        ajax.forzenTask({
            taskIdList: [tabData.id],
            scheduleStatus: status //  1正常调度, 2暂停 NORMAL(1), PAUSE(2),
        }).then((res: any) => {
            if (res.code === 1) {
                // mutate
                this.props.changeScheduleStatus(status);
                message.info(succInfo)
            } else {
                message.err(errInfo)
            }
        })
    }

    handleScheduleConf = () => {
        const { tabData } = this.props;
        let defaultScheduleConf = JSON.parse(tabData.scheduleConf);
        if (!defaultScheduleConf.periodType) {
            defaultScheduleConf = this.getDefaultScheduleConf(2);
        }
        setTimeout(() => {
            this.form.props.form.validateFields({ force: true }, (err: any, values: any) => {
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
                    this.props.changeScheduleConf(formData);
                }
            });
        }, 0);
    }

    handleScheduleType (type: any) {
        const dft = this.getDefaultScheduleConf(type);
        const isFailRetry = this.form.props.form.getFieldValue('isFailRetry');
        const values = assign({}, dft, {
            scheduleStatus: this.form.props.form.getFieldValue('scheduleStatus'),
            periodType: type,
            isFailRetry: this.form.props.form.getFieldValue('isFailRetry'),
            beginDate: this.form.props.form.getFieldValue('beginDate'),
            endDate: this.form.props.form.getFieldValue('endDate'),
            selfReliance: this.form.props.form.getFieldValue('selfReliance')
        });
        if (isFailRetry) {
            values.maxRetryNum = this.form.props.form.getFieldValue('maxRetryNum');
        }
        this.props.changeScheduleConf(values);
    }

    getDefaultScheduleConf (value: any) {
        const scheduleConf: any = {
            0: {
                beginMin: 0,
                endMin: 59,
                beginHour: 0,
                endHour: 23,
                gapMin: 5,
                periodType: 0,
                beginDate: '2001-01-01',
                endDate: '2021-01-01'
            },
            1: {
                beginHour: 0,
                endHour: 23,
                beginMin: 0,
                gapHour: 5,
                periodType: 1
            },
            2: {
                min: 0,
                hour: 0,
                periodType: 2,
                beginDate: '2001-01-01',
                endDate: '2021-01-01'
            },
            3: {
                weekDay: 3,
                min: 0,
                hour: 23,
                periodType: 3
            },
            4: {
                day: 5,
                hour: 0,
                min: 23,
                periodType: 4
            }
        };

        return scheduleConf[value];
    }

    handleDelVOS (o: any) {
        this.props.delVOS(o.id);
    }

    handleAddVOS (task: any) {
        this.props.addVOS(task);
    }

    setSelfReliance (evt: any) {
        const value = evt.target.value;
        this.setState({
            selfReliance: value
        })
        this.handleScheduleConf();
    }

    render () {
        const {
            wFScheduleConf, selfReliance
        } = this.state;

        const { tabData, isWorkflowNode, couldEdit, isIncrementMode, isScienceTask, updateKey } = this.props;

        const isLocked = tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock
        const isWorkflowRoot = tabData.taskType == TASK_TYPE.WORKFLOW;

        let initConf = tabData.scheduleConf;

        let scheduleConf = Object.assign(this.getDefaultScheduleConf(0), {
            beginDate: '2001-01-01',
            endDate: '2021-01-01'
        });

        if (initConf !== '') {
            scheduleConf = Object.assign(scheduleConf, JSON.parse(initConf));
        }
        // 工作流更改默认调度时间配置
        if (isWorkflowNode) {
            scheduleConf = Object.assign(this.getDefaultScheduleConf(2), {
                beginDate: '2001-01-01',
                endDate: '2021-01-01'
            }, scheduleConf);
            scheduleConf.periodType = 2;
        }

        const radioStyle: any = {
            display: 'block',
            height: '30px',
            lineHeight: '30px'
        };

        return <div className="m-scheduling" style={{ position: 'relative' }}>
            {isLocked || (!couldEdit && !isScienceTask) ? <div className="cover-mask"></div> : null}
            <Collapse bordered={false} defaultActiveKey={['1', '2', '3']}>
                <Panel key="1" header="调度属性">
                    <FormWrap
                        scheduleConf={scheduleConf}
                        isScienceTask={isScienceTask}
                        wFScheduleConf={wFScheduleConf}
                        status={tabData.scheduleStatus}
                        isWorkflowNode={isWorkflowNode}
                        isWorkflowRoot={isWorkflowRoot}
                        handleScheduleStatus={this.handleScheduleStatus.bind(this)}
                        handleScheduleConf={this.handleScheduleConf.bind(this)}
                        handleScheduleType={this.handleScheduleType.bind(this)}
                        wrappedComponentRef={(el: any) => this.form = el}
                        key={`${tabData.id}-${scheduleConf.periodType}_${updateKey}`}
                    />
                </Panel>
                {
                    !isWorkflowNode &&
                    tabData.taskType !== TASK_TYPE.VIRTUAL_NODE &&
                    <Panel key="2" header="任务间依赖">
                        <TaskDependence
                            handleAddVOS={this.handleAddVOS.bind(this)}
                            handleDelVOS={this.handleDelVOS.bind(this)}
                            tabData={tabData}
                            getTaskDetail={this.props.getTaskDetail}
                        />
                    </Panel>
                }
                {
                    !isWorkflowNode &&
                    <Panel key="3" header="跨周期依赖">
                        <Row style={{ marginBottom: '16px' }}>
                            <Col span="1" />
                            <Col>
                                <RadioGroup disabled={isScienceTask} onChange={this.setSelfReliance.bind(this)}
                                    value={selfReliance}
                                >
                                    {!isIncrementMode && <Radio style={radioStyle} value={0}>不依赖上一调度周期</Radio>}
                                    <Radio style={radioStyle} value={1}>自依赖，等待上一调度周期成功，才能继续运行</Radio>
                                    <Radio style={radioStyle} value={3}>
                                        自依赖，等待上一调度周期结束，才能继续运行&nbsp;
                                        <HelpDoc style={{ position: 'inherit' }} doc={!isIncrementMode ? 'taskDependentTypeDesc' : 'incrementModeScheduleTypeHelp'} />
                                    </Radio>
                                    {!isIncrementMode && <Radio style={radioStyle} value={2}>等待下游任务的上一周期成功，才能继续运行</Radio>}
                                    {!isIncrementMode && <Radio style={radioStyle} value={4}>
                                        等待下游任务的上一周期结束，才能继续运行&nbsp;
                                        <HelpDoc style={{ position: 'inherit' }} doc="taskDependentTypeDesc" />
                                    </Radio>}
                                </RadioGroup>
                            </Col>
                        </Row>
                    </Panel>
                }
            </Collapse>
        </div>
    }
}

const mapState = (state: any, ownProps: any) => {
    return { ...ownProps };
};

const mapDispatch = (dispatch: any) => {
    return {
        changeScheduleConf: (newConf: any) => {
            dispatch({
                type: workbenchAction.CHANGE_SCHEDULE_CONF,
                payload: newConf
            });
        },
        changeScheduleStatus: (status: any) => {
            dispatch({
                type: workbenchAction.CHANGE_SCHEDULE_STATUS,
                payload: status
            });
        },
        addVOS: (vos: any) => {
            dispatch({
                type: workbenchAction.ADD_VOS,
                payload: vos
            });
        },
        delVOS: (id: any) => {
            dispatch({
                type: workbenchAction.DEL_VOS,
                payload: id
            });
        },
        getTaskDetail: (id: any) => {
            ajax.getOfflineTaskDetail({
                id: id
            }).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: workbenchAction.LOAD_TASK_DETAIL,
                        payload: res.data
                    });
                    dispatch({
                        type: workbenchAction.OPEN_TASK_TAB,
                        payload: id
                    });
                }
            });
        }
    };
};

export default connect(mapState, mapDispatch)(SchedulingConfig);
