import React from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import assign from 'object-assign';

import { Row,
    Col,
    Checkbox,
    Form,
    DatePicker,
    TimePicker,
    Select,
    Collapse,
    Table,
    Radio,
    Input,
    Spin,
    message
 } from 'antd';

import ajax from '../../../api';
import { workbenchAction } from '../../../store/modules/offlineTask/actionType';
import { TASK_TYPE } from '../../../comm/const';
import { debounceEventHander } from '../../../comm';
import HelpDoc from '../../helpDoc';

const Panel = Collapse.Panel;
const Option = Select.Option;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;

const formItemLayout = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 4 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
    },
}


class ScheduleForm extends React.Component {
    constructor(props) {
        super(props);
        this.changeScheduleStatus = this.props.handleScheduleStatus;
        this.changeScheduleConf = this.props.handleScheduleConf;
        this.changeScheduleType = this.props.handleScheduleType;
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { status, scheduleConf } = this.props;
        const { periodType } = scheduleConf;



        const generateHours = () => {
            let options = [];
            for(let i = 0; i <= 23; i++) {
                options.push(<Option key={i} value={`${i}`}>{i < 10 ? `0${i}`:i}</Option>)
            }
            return <Select onChange={ this.changeScheduleConf.bind(this) }>{ options }</Select>;
        };
        const generateMins = () => {
            let options = [];
            for(let i = 0, l = 59; i <= l; i++) {
                options.push(<Option key={i} value={`${i}`}>{i < 10 ? `0${i}`: i}</Option>)
            }
            return <Select onChange={ this.changeScheduleConf.bind(this) }>{ options }</Select>;
        };
        const generateDate = () => {
            let options = [];
            for(let i = 1; i <= 31; i++) {
                options.push(<Option key={i} value={`${i}`}>{`每月${i}号`}</Option>)
            }
            return <Select
                mode="multiple"
                style={{width: '100%'}}
                onChange={ this.changeScheduleConf.bind(this) }
            >{ options }</Select>;
        };
        const generateDays = () => {
            return <Select
                mode="multiple"
                style={{width: '100%'}}
                onChange={ this.changeScheduleConf.bind(this) }
            >
                <Option key={1} value="1">星期一</Option>
                <Option key={2} value="2">星期二</Option>
                <Option key={3} value="3">星期三</Option>
                <Option key={4} value="4">星期四</Option>
                <Option key={5} value="5">星期五</Option>
                <Option key={6} value="6">星期六</Option>
                <Option key={7} value="7">星期天</Option>
            </Select>
        }

        return <Form key={ periodType }>
            <FormItem
                {...formItemLayout}
                label="调度状态"
            >
            {getFieldDecorator('scheduleStatus', {
                valuePropName: 'checked',
                initialValue: status === 0 || status === 2,
            })(
                <Checkbox
                    onChange={ this.changeScheduleStatus.bind(this) }
                >冻结</Checkbox>
            )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="生效日期"
            >
            {getFieldDecorator('beginDate', {
                initialValue: moment(scheduleConf.beginDate, 'YYYY-MM-DD')
            })(
                <DatePicker
                    onChange={ this.changeScheduleConf.bind(this) }
                />
            )}
            <span className="split-text" style={{float:"none"}} >-</span>
            {getFieldDecorator('endDate', {
                initialValue: moment(scheduleConf.endDate, 'YYYY-MM-DD')
            })(
                <DatePicker
                    onChange={ this.changeScheduleConf.bind(this) }
                />
            )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="调度周期"
            >
                <Col span="6">
                    {getFieldDecorator('periodType', {
                        initialValue: `${scheduleConf.periodType}`,
                        rules: [{
                            required: true,
                        }]
                    })(
                        <Select onChange={ this.changeScheduleType.bind(this) }>
                            <Option key={0} value="0">分钟</Option>
                            <Option key={1} value="1">小时</Option>
                            <Option key={2} value="2">天</Option>
                            <Option key={3} value="3">周</Option>
                            <Option key={4} value="4">月</Option>
                        </Select>
                    )}
                </Col>
            </FormItem>
            <FormItem style={{display: 'none'}}>
                {getFieldDecorator('selfReliance', {
                    initialValue: scheduleConf.selfReliance
                })(
                    <Input type="hidden"></Input>
                )}
            </FormItem>
            {(function(type, ctx) {
                let dom;
                switch(type) {
                    case 0: //分钟
                        dom = <span key={type}>
                            <FormItem
                                {...formItemLayout}
                                label="开始时间"
                            >
                                <Col span="6">

                                {getFieldDecorator('beginHour', {
                                    rules: [{
                                            required: true
                                        },{
                                            validator: ctx.checkTimeS.bind(ctx)
                                        }
                                    ],
                                    initialValue: `${scheduleConf.beginHour}`
                                })(
                                     generateHours()
                                )}
                                </Col>

                                <span className="split-text">时</span>
                                <Col span="6">

                                {getFieldDecorator('beginMin', {
                                    rules: [{
                                        required: true
                                    }],
                                    initialValue: '0'
                                })(
                                        <Select disabled>
                                            <Option value="0">0</Option>
                                        </Select>
                                )}
                                </Col>

                                <span className="split-text">分</span>
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="间隔时间"
                            >
                                <Col span="6">
                                {getFieldDecorator('gapMin', {
                                    rules: [{
                                        required: true
                                    }],
                                    initialValue: `${scheduleConf.gapMin}`
                                })(
                                    <Select
                                        onChange={ ctx.changeScheduleConf.bind(ctx) }
                                    >
                                    {(function() {
                                        let options = [];
                                        for(let i = 5; i <= 55; i+=5) {
                                            options.push(<Option key={i} value={`${i}`}>{i}分钟</Option>)
                                        }
                                        return options;
                                    })()}
                                    </Select>
                                )}
                                </Col>
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="结束时间"
                            >
                                <Col span="6">

                                {getFieldDecorator('endHour', {
                                    rules: [{
                                        required: true
                                    }, {
                                        validator: ctx.checkTimeE.bind(ctx)
                                    }],
                                    initialValue: `${scheduleConf.endHour}`
                                })(
                                    generateHours()
                                )}

                                </Col>
                                <span className="split-text">时</span>
                                <Col span="6">

                                {getFieldDecorator('endMin', {
                                    initialValue: '59'
                                })(
                                    <Select disabled>
                                        <Option value="59">59</Option>
                                    </Select>
                                )}
                                </Col>
                                <span className="split-text">分</span>
                            </FormItem>
                        </span>;
                    break;

                    case 1: //小时
                        dom = <span key={type}>
                            <FormItem
                                {...formItemLayout}
                                label="开始时间"
                            >
                                <Col span="6">

                                {getFieldDecorator('beginHour', {
                                    rules: [{
                                        required: true
                                    }, {
                                        validator: ctx.checkTimeS1.bind(ctx)
                                    }],
                                    initialValue: `${scheduleConf.beginHour}`
                                })(
                                    generateHours()
                                )}
                                </Col>

                                <span className="split-text">时</span>
                                <Col span="6">

                                {getFieldDecorator('beginMin', {
                                    rules: [{
                                        required: true
                                    }, {
                                        validator: ctx.checkTimeS1.bind(ctx)
                                    }],
                                    initialValue: `${scheduleConf.beginMin}`
                                })(
                                    generateMins()
                                )}
                                </Col>

                                <span className="split-text">分</span>
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="间隔时间"
                            >
                                <Col span="6">

                                {getFieldDecorator('gapHour', {
                                    rules: [{
                                        required: true
                                    }],
                                    initialValue: `${scheduleConf.gapHour}`
                                })(
                                    <Select
                                        onChange={ ctx.changeScheduleConf.bind(ctx) }
                                    >
                                        {(function() {
                                            let options = [];
                                            for(let i = 1, l = 23; i <= l; i++) {
                                                options.push(<Option key={i} value={`${i}`}>{i}小时</Option>)
                                            }
                                            return options;
                                        })()}
                                    </Select>
                                )}
                                </Col>
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="结束时间"
                            >
                            <Col span="6">
                            {getFieldDecorator('endHour', {
                                rules: [{
                                    required: true
                                }, {
                                    validator: ctx.checkTimeE1.bind(ctx)
                                }],
                                initialValue: `${scheduleConf.endHour}`
                            })(
                                    generateHours()
                            )}
                            </Col>
                            <span className="split-text">时</span>
                            <Col span="6">
                            {getFieldDecorator('endMin', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: '59'
                            })(
                                    <Select disabled>
                                        <Option value="59">59</Option>
                                    </Select>
                            )}
                            </Col>
                            <span className="split-text">分</span>
                            </FormItem>
                        </span>;
                    break;

                    case 2: // 天
                        dom = <span  key={type}>
                            <FormItem
                                {...formItemLayout}
                                label="起调周期"
                            >
                            <Col span="6">
                            {getFieldDecorator('hour', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: `${scheduleConf.hour}`
                            })(
                                generateHours()
                            )}
                            </Col>

                            <span className="split-text">时</span>
                            <Col span="6">

                            {getFieldDecorator('min', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: `${scheduleConf.min}`

                            })(
                                    generateMins()
                            )}
                            </Col>
                            <span className="split-text">分</span>
                        </FormItem>
                    </span>;
                    break;

                    case 3: // 周
                        dom = <span key={type}>
                            <FormItem
                                {...formItemLayout}
                                label="选择时间"
                            >
                                <Col span="13">
                                {getFieldDecorator('weekDay', {
                                    rules: [{
                                        required: true
                                    }],
                                    initialValue: `${scheduleConf.weekDay}`.split(',')
                                })(
                                    generateDays()
                                )}
                                </Col>
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="具体时间"
                            >
                                <Col span="6">
                                {getFieldDecorator('hour', {
                                    rules: [{
                                        required: true
                                    }],
                                    initialValue: `${scheduleConf.hour}`
                                })(
                                    generateHours()
                                )}
                                </Col>

                                <span className="split-text">时</span>
                                <Col span="6">

                                {getFieldDecorator('min', {
                                    rules: [{
                                        required: true
                                    }],
                                    initialValue: `${scheduleConf.min}`
                                })(
                                    generateMins()
                                )}
                                </Col>

                                <span className="split-text">分</span>
                            </FormItem>
                        </span>;
                    break;

                    case 4: // 月
                        dom = <span key={type}>
                            <FormItem
                                {...formItemLayout}
                                label="选择时间"
                            >
                                <Col span="13">

                                {getFieldDecorator('day', {
                                    rules: [{
                                        required: true
                                    }],
                                    initialValue: `${scheduleConf.day}`.split(',')
                                })(
                                    generateDate()
                                )}
                                </Col>

                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="具体时间"
                            >
                                <Col span="6">

                                {getFieldDecorator('hour', {
                                    rules: [{
                                        required: true
                                    }],
                                    initialValue: `${scheduleConf.hour}`
                                })(
                                    generateHours()
                                )}
                                </Col>

                                <span className="split-text">时</span>
                                <Col span="6">

                                {getFieldDecorator('min', {
                                    rules: [{
                                        required: true
                                    }],
                                    initialValue: `${scheduleConf.min}`
                                })(
                                    generateMins()
                                )}
                                </Col>
                                <span className="split-text">分</span>
                            </FormItem>
                        </span>;
                    break;

                    default: dom = <span>something wrong</span>;
                }

                return dom;
            })(+periodType, this)}
        </Form>
    }

    checkTimeS(rule, value, callback) {
        const { form } = this.props;
        const endHour = form.getFieldValue('endHour');

        if(+value > +endHour) {
            callback('开始时间不能晚于结束时间');
        }
        callback();
    }
    checkTimeE(rule, value, callback) {
        const { form } = this.props;
        const beginHour = form.getFieldValue('beginHour');

        if(+value < +beginHour) {
            callback('结束时间不能早于开始时间');
        }
        callback();
    }
    checkTimeS1(rule, value, callback) {
        const { form } = this.props;
        const beginHour = +form.getFieldValue('beginHour');
        const beginMin = +form.getFieldValue('beginMin');
        const endHour = +form.getFieldValue('endHour') * 60 + 59;

        if(beginHour*60+beginMin > endHour) {
            callback('开始时间不能晚于结束时间');
        }
        callback();
    }
    checkTimeE1(rule, value, callback) {
        const { form } = this.props;
        const beginHour = +form.getFieldValue('beginHour');
        const beginMin = +form.getFieldValue('beginMin');
        const endHour = +form.getFieldValue('endHour') * 60 + 59;

        if(beginHour*60+beginMin > endHour) {
            callback('结束时间不能早于开始时间');
        }
        callback();
    }
};

const FormWrap = Form.create()(ScheduleForm);

class SchedulingConfig extends React.Component {
    constructor(props) {
        super(props);

        const { tabData } = this.props;
        let initConf = tabData.scheduleConf;
        const scheduleConf = initConf === '' ?
            Object.assign(this.getDefaultScheduleConf(0), {
                beginDate: '2001-01-01',
                endDate: '2021-01-01'
            }) :
            JSON.parse(initConf);

        // this._selfReliance = typeof scheduleConf.selfReliance === 'undefined' ?
        //     false : scheduleConf.selfReliance;
        // scheduleConf.selfReliance兼容老代码true or false 值
        if (scheduleConf.selfReliance !== 'undefined') {
            if (scheduleConf.selfReliance === false) {
                this._selfReliance = 0;
            } else if (scheduleConf.selfReliance === true) {
                this._selfReliance = 1;
            } else {
                this._selfReliance = scheduleConf.selfReliance;
            }
        } else {
            this._selfReliance = 0;
        }
    }

    handleScheduleStatus(evt) {
        const { checked } = evt.target;
        // mutate
        this.props.changeScheduleStatus(checked ? 2 : 1);
    }

    handleScheduleConf() {
        setTimeout(() => {
            this.form.validateFields((err, values) => {
                if(!err) {
                    const formData = this.form.getFieldsValue();

                    formData.selfReliance = this._selfReliance;
                    delete formData.scheduleStatus;
                    this.props.changeScheduleConf(formData);
                }
            });
        }, 0);
    }

    handleScheduleType(type) {
        const dft = this.getDefaultScheduleConf(type);
        const values = assign({}, dft, {
            scheduleStatus: this.form.getFieldValue('scheduleStatus'),
            beginDate: this.form.getFieldValue('beginDate'),
            endDate: this.form.getFieldValue('endDate'),
            periodType: type,
            selfReliance: this.form.getFieldValue('selfReliance')
        })
        this.props.changeScheduleConf(values);
    }

    getDefaultScheduleConf(value) {
        const scheduleConf = {
            0: {
                beginHour: 0,
                gapMin: 5,
                endHour: 23,
                periodType: 0
            },
            1: {
                beginHour: 0,
                beginMin: 0,
                endHour: 23,
                gapHour: 5,
                periodType: 1
            },
            2: {
                min: 0,
                hour: 0,
                periodType: 2
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
                periodType: 4,
            }
        };

        return scheduleConf[value];
    }

    handleDelVOS(o) {
        this.props.delVOS(o.id);
    }

    handleAddVOS(task) {
        this.props.addVOS(task);
    }

    goEdit(task) {
        this.props.getTaskDetail(task.id)
    }

    setSelfReliance(evt) {
        const value = evt.target.value;
        this._selfReliance = value;
        this.handleScheduleConf();
    }

    render() {
        const { tabData } = this.props;
        const isLocked = tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock
        let initConf = tabData.scheduleConf;

        const scheduleConf = initConf === '' ?
            Object.assign(this.getDefaultScheduleConf(0), {
                beginDate: '2001-01-01',
                endDate: '2021-01-01'
            }) :
            JSON.parse(initConf);

        const columns = [
            {
                title: '任务名称',
                dataIndex: 'name',
                key: 'name',
                render: (text, record) => <a 
                    href="javascript:void(0)"
                    onClick={ this.goEdit.bind(this, record) }
                    >{text}</a>,
            },
            {
                title: '责任人',
                dataIndex: 'createUser.userName',
                key: 'createUser.userName',
            },
            {
                title: '操作',
                key: 'action',
                render: (text, record) => (
                    <span>
                        <a href="javascript:void(0)"
                            onClick={ this.handleDelVOS.bind(this, record) }
                        >删除</a>
                    </span>
                ),
            }
        ];

        const radioStyle = {
            display: 'block',
            height: '30px',
            lineHeight: '30px',
        };

        return <div className="m-scheduling" style={{position: 'relative'}}>
            {isLocked?<div className="cover-mask"></div>:null} 
            <Collapse bordered={false} defaultActiveKey={['1', '2', '3']}>
                <Panel key="1" header="调度属性">
                    <FormWrap
                        scheduleConf={ scheduleConf }
                        status={ tabData.scheduleStatus }
                        handleScheduleStatus={ this.handleScheduleStatus.bind(this) }
                        handleScheduleConf={ this.handleScheduleConf.bind(this) }
                        handleScheduleType={ this.handleScheduleType.bind(this) }
                        ref={ el => this.form = el }
                        key={ `${tabData.id} - ${scheduleConf.periodType}` }
                    />
                </Panel>
                {
                    tabData.taskType !== TASK_TYPE.VIRTUAL_NODE &&
                    <Panel key="2" header="任务间依赖">
                        <Form>
                            <FormItem
                            {...formItemLayout}
                            label="上游任务"
                            >
                                <TaskSelector
                                    onSelect={ this.handleAddVOS.bind(this) }
                                    taskId={ tabData.id }
                                />
                            </FormItem>
                        </Form>
                        <Row>
                            <Col>
                                <Table
                                    className="m-table"
                                    columns={columns}
                                    bordered={false}
                                    dataSource={ tabData.taskVOS }
                                    rowKey={record => record.id.lable}
                                />
                            </Col>
                        </Row>
                    </Panel>
                }
                <Panel key="3" header="依赖类型">
                    <Row>
                        <Col span="1" />
                        <Col>
                            <RadioGroup onChange={ this.setSelfReliance.bind(this) }
                                value={ this._selfReliance }
                            >
                                <Radio style={radioStyle} value={0}>不依赖上一调度周期</Radio>
                                <Radio style={radioStyle} value={3}>
                                    自依赖，等待上一调度周期结束，才能继续运行&nbsp;
                                    <HelpDoc style={{position: 'inherit'}} doc="taskDependentTypeDesc" />
                                </Radio>
                                <Radio style={radioStyle} value={1}>自依赖，等待上一调度周期完成，才能继续运行</Radio>
                                <Radio style={radioStyle} value={2}>等待下游任务的上一周期完成，才能继续运行</Radio>
                                <Radio style={radioStyle} value={4}>等待下游任务的上一周期结束，才能继续运行</Radio>
                            </RadioGroup>
                        </Col>
                    </Row>
                </Panel>
            </Collapse>
        </div>
    }
}

class TaskSelector extends React.Component {
    constructor(props) {
        super(props);
        this.searchVOS = this.fetchVOS.bind(this);
        this.handleClick = this.handleClick.bind(this);
        this.selectVOS = this.props.onSelect;
        this.taskId = this.props.taskId;

        this.state = { list: [] };
    }

    fetchVOS(evt) {
        const value = evt.target.value;
        if(value.trim() === '') {
            this.setState({
                list: []
            });
            return;
        }

        ajax.getOfflineTaskByName({
            name: value,
            taskId: this.taskId
        }).then(res => {
            if(res.code === 1) {
                res.data.length === 0 && message.error('没有符合条件的任务');
                this.setState({
                    list: res.data,
                    fetching: false
                });
            }
        })
    }

    handleClick(task) {
        ajax.checkIsLoop({
            taskId: this.taskId,
            dependencyTaskId: task.id
        })
        .then(res => {
            if(res.code === 1) {
                if(res.data) message.error(`添加失败，该任务循环依赖任务${res.data.name || ''}!`)
                else {
                    this.selectVOS(task);
                    this.$input.value = '';
                    this.setState({
                        list: []
                    });
                }
            }
        })
    }

    render() {
        const { list } = this.state;

        return <div className="m-taskselector">
            <input onInput={debounceEventHander(this.searchVOS, 500, { 'maxWait': 2000 }) }
                ref={el => this.$input = el}
                className="ant-input"
            />
            {list.length > 0 && <ul className="tasklist">
                {list.map(o => <li className="taskitem"
                    onClick={ ()=>{ this.handleClick(o) } }
                    key={ o.id }
                >
                    {o.name}
                </li>)}
            </ul>}
        </div>
    }
}

const mapState = (state, ownProps) => {
    return { ...ownProps };
};

const mapDispatch = dispatch => {
    return {
        changeScheduleConf: (newConf) => {
            dispatch({
                type: workbenchAction.CHANGE_SCHEDULE_CONF,
                payload: newConf
            });
        },
        changeScheduleStatus: (status) => {
            dispatch({
                type: workbenchAction.CHANGE_SCHEDULE_STATUS,
                payload: status
            });
        },
        addVOS: (vos) => {
            dispatch({
                type: workbenchAction.ADD_VOS,
                payload: vos
            });
        },
        delVOS: id => {
            dispatch({
                type: workbenchAction.DEL_VOS,
                payload: id
            });
        },
        getTaskDetail: (id) => {
            ajax.getOfflineTaskDetail({
                id: id
            }).then(res => {
                if(res.code === 1) {
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

