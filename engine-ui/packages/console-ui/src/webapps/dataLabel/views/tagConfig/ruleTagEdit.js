import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty, isNull } from 'lodash';
import { Table, Card, Modal, Form, Button, Input, Select, Popconfirm, DatePicker, Checkbox, message } from 'antd';
import moment from 'moment';

import GoBack from 'main/components/go-back';
import { dataSourceActions } from '../../actions/dataSource';
import { formItemLayout, halfFormItemLayout, rowFormItemLayout } from '../../consts';
import TCApi from '../../api/tagConfig';

const Option = Select.Option;
const FormItem = Form.Item;
const TextArea = Input.TextArea;

const initialSchedule = {
    beginDate: moment().format('YYYY-MM-DD'),
    endDate: moment().add(100, 'years').format('YYYY-MM-DD'),
    periodType: '2',
    day: undefined,
    weekDay: undefined,
    hour: 0,
    min: 0,
    beginHour: 0,
    beginMin: 0,
    gapHour: undefined,
    endHour: 0,
    endMin: 0
}

const mapStateToProps = state => {
    const { common, dataSource } = state;
    return { common, dataSource }
}

const mapDispatchToProps = dispatch => ({
    getDataSourcesList(params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
    getDataSourcesTable(params) {
        dispatch(dataSourceActions.getDataSourcesTable(params));
    },
    resetDataSourcesTable() {
        dispatch(dataSourceActions.resetDataSourcesTable());
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class RuleTagEdit extends Component {

    state = {
        tagId: this.props.routeParams.id,
        currentData: {},
        conditionList: [],
        curCondition: {},
        computeList: [],
        scheduleConfObj: initialSchedule,
        notifyVO: {
            sendTypes: [],
            receivers: []
        }
    }

    componentDidMount() {
        this.getData({ tagId: this.state.tagId });
        this.props.getDataSourcesList();
    }

    getData = (params) => {
        TCApi.getRuleTagDetail(params).then((res) => {
            if (res.code === 1) {
                this.setState({
                    currentData: res.data,
                    conditionList: res.data.conditions
                });

                if (res.data.scheduleConf) {
                    this.setState({
                        scheduleConfObj: JSON.parse(res.data.scheduleConf)
                    });
                }

                if (res.data.notifyVO) {
                    this.setState({
                        notifyVO: res.data.notifyVO
                    });
                }
            }
        });

        TCApi.getComputeSource().then((res) => {
            if (res.code === 1) {
                this.setState({
                    computeList: res.data
                });
            }
        });
    }

    // table设置
    initColumns = () => {
        return [{
            title: '数据源',
            dataIndex: 'dataSourceId',
            key: 'dataSourceId',
            width: '25%',
            render: (text, record) => this.renderColumns(text, record, 'dataSourceId'),
        }, {
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
            width: '25%',
            render: (text, record) => this.renderColumns(text, record, 'tableName'),
        }, {
            title: '过滤条件',
            dataIndex: 'sqlCondition',
            key: 'sqlCondition',
            width: '40%',
            render: (text, record) => this.renderColumns(text, record, 'sqlCondition'),
        }, {
            title: '操作',
            width: '10%',
            render: (text, record) => {
                const { editable } = record;
                return (
                    <div>
                        {
                            editable ?
                            <span>
                                <a className="m-r-8" onClick={() => this.save(record.id)}>保存</a>
                                <a onClick={() => this.cancel(record.id)}>取消</a>
                            </span>
                            : 
                            <span>
                                <a className="m-r-8" onClick={() => this.edit(record.id)}>编辑</a>
                                <Popconfirm title="确定要删除吗？" onConfirm={() => this.delete(record.id)}>
                                    <a>删除</a>
                                </Popconfirm>
                            </span>
                        }
                    </div>
                );
            },
        }]
    }

    renderColumns(text, record, type) {
        let obj = {
            children: <Form layout="inline">
                {
                    record.editable ?
                    this.renderEditTD(text, record, type)
                    :
                    this.renderTD(text, record, type)
                }
            </Form>,
            props: {},
        };

        return obj;
    }

    // 参数改变回调
    changeConditionParams = (type, value) => {
        let obj = {};
        obj[type] = value.target ? value.target.value : value;

        this.setState({ curCondition: {...this.state.curCondition, ...obj} });
    }

    // 数据源下拉框
    renderDataSource = (data) => {
        return data.map((source) => {
            let title = `${source.dataName}（${source.sourceTypeValue}）`;
            return (
                <Option 
                    key={source.id} 
                    value={source.id.toString()}
                    title={title}>
                    {title}
                </Option>
            )
        });
    }

    // 数据表下拉框
    renderSourceTable = (data) => {
        return data.map((tableName) => {
            return <Option 
                key={tableName} 
                value={tableName}>
                {tableName}
            </Option>
        });
    }

    // 计算资源下拉框
    renderComputeSource = (data) => {
        return data.map((source) => {
            let title = `${source.dataName}（${source.sourceTypeValue}）`;
            return (
                <Option 
                    key={source.id} 
                    value={source.id.toString()}
                    title={title}>
                    {title}
                </Option>
            )
        });
    }

    // 数据源变化回调
    onDataSourceChange = (id) => {
        const { form, dataSource } = this.props;
        const { sourceList } = dataSource;

        let item = sourceList.filter(item => item.id == id)[0];

        this.props.resetDataSourcesTable();
        this.props.getDataSourcesTable({ sourceId: id });

        form.setFieldsValue({ tableName: undefined });
        this.setState({ 
            curCondition: {
                ...this.state.curCondition, 
                dataSourceId: id,
                dataSourceName: `${item.dataName}（${item.sourceTypeValue}）`,
                tableName: undefined
            }
        });
    }

    // 编辑状态的TD
    renderEditTD = (text, record, type) => {
        const { form, dataSource } = this.props;
        const { getFieldDecorator } = form;
        const { sourceList, sourceTable } = dataSource;

        switch(type) {
            case 'dataSourceId': {
                return <FormItem {...rowFormItemLayout} className="edit-td">
                    {
                        getFieldDecorator('dataSourceId', {
                            rules: [{
                                required: true, 
                                message: '数据源不可为空'
                            }],
                            initialValue: record.dataSourceId.toString()
                        })(
                            <Select 
                                showSearch 
                                optionFilterProp="title"
                                onChange={this.onDataSourceChange}>
                                {
                                    this.renderDataSource(sourceList)
                                }
                            </Select>
                        )
                    }
                </FormItem>
            }

            case 'tableName': {
                return <FormItem {...rowFormItemLayout} className="edit-td">
                    {
                        getFieldDecorator('tableName', {
                            rules: [{
                                required: true, 
                                message: '数据表不可为空'
                            }],
                            initialValue: record.tableName
                        })(
                            <Select
                                showSearch 
                                onChange={this.changeConditionParams.bind(this, 'tableName')}>
                                {
                                    this.renderSourceTable(sourceTable)
                                }
                            </Select>
                        )
                    }
                </FormItem>
            }

            case 'sqlCondition': {
                return <FormItem {...rowFormItemLayout} className="edit-td">
                    {
                        getFieldDecorator('sqlCondition', {
                            rules: [],
                            initialValue: record.sqlCondition
                        })(
                            <Input 
                                placeholder={`sql过滤条件`}
                                onChange={this.changeConditionParams.bind(this, 'sqlCondition')} 
                            />
                        )
                    }
                </FormItem>
            }
        }
    }

    // 已有数据
    renderTD = (text, record, type) => {
        // return text;
        switch (type) {
            case 'dataSourceId': {
                return record.dataSourceName
            }
            default:
                return text;
        }
    }

    // 编辑规则
    edit(id) {
        const { curCondition, conditionList } = this.state;

        let newData = [...conditionList],
            target  = newData.filter(item => id === item.id)[0];

        if (!isEmpty(curCondition)) {
            if (curCondition.editStatus === 'edit') {
                delete curCondition.editable
                delete curCondition.editStatus
            } else {
                newData.shift();
            }
        }

        if (target) {
            target.editable = true;
            target.editStatus = "edit";

            this.setState({ 
                curCondition: target,
                conditionList: newData
            });
        }
    }

    // 取消编辑
    cancel(id) {
        let newData = [...this.state.conditionList],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target);

        if (target.editStatus === 'edit') {
            delete target.editable;
            delete target.editStatus;
        } else {
            newData.splice(index, 1);
        }

        this.setState({ 
            curCondition: {},
            conditionList: newData
        });
    }

    // 删除规则
    delete(id) {
        const { tagId, conditionList } = this.state;

        let newData = [...conditionList],
            target  = newData.filter(item => id === item.id)[0];
        
        if (target) {
            TCApi.deleteTagCondition({
                conditionId: id
            }).then((res) => {
                if (res.code === 1) {
                    message.success('删除成功');
                    TCApi.getTagCondition({ tagId }).then((res) => {
                        if (res.code === 1) {
                            this.setState({
                                conditionList: res.data
                            });
                        }
                    });
                }
            });
        }
    }

    // 保存规则
    save(id) {
        const { tagId, curCondition, conditionList } = this.state;

        let newData = [...conditionList],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target);

        this.props.form.validateFields(['dataSourceId', 'tableName', 'sqlCondition'], (err, values) => {
            console.log(err,values)
            if(!err) {
                delete curCondition.editable;
                delete curCondition.editStatus;
                newData[index] = curCondition;

                TCApi.editTagCondition({...curCondition}).then((res) => {
                    if (res.code === 1) {
                        message.success('保存成功');
                        TCApi.getTagCondition({ tagId }).then((res) => {
                            if (res.code === 1) {
                                this.setState({
                                    conditionList: res.data,
                                    currentRule: {}
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    // 新增规则
    addNewCondition = () => {
        const { tagId, conditionList, curCondition } = this.state;

        let newData = [...conditionList];

        if (!isEmpty(curCondition)) {
            if (curCondition.editStatus === "edit") {
                delete curCondition.editable
                delete curCondition.editStatus
            } else {
                newData.shift();
                this.props.form.resetFields();
                this.setState({ 
                    curCondition: {}, 
                    functionList: [] 
                });
            }
        }

        let target = {
            id: 0,
            editStatus: 'new',
            editable: true,
            tagId: tagId,
            dataSourceId: '',
            tableName: '',
            sqlCondition: ''
        };

        newData.unshift(target);
        this.setState({ 
            curCondition: target,
            conditionList: newData
        });
    }

    onComputeSourceChange = (id) => {
        const { form } = this.props;
        const { currentData } = this.state;

        this.setState({ 
            currentData: {
                ...currentData, 
                computeSourceId: id
            }
        });
    }

    // 重置执行信息
    resetScheduleConf = (type) => {
        let scheduleConfObj = { ...initialSchedule, periodType: type };

        this.setState({
            scheduleConfObj,
            currentData: {
                ...this.state.currentData, 
                // periodType: type,
                scheduleConf: JSON.stringify(scheduleConfObj)
            }
        });
    }

    // 调度周期下拉框
    renderPeriodType = (data) => {
        return data.map((item) => {
            return <Option 
                key={item.value} 
                value={item.value.toString()}>
                {item.name}
            </Option>
        });
    }

    // 调度周期回调
    onPeriodTypeChange = (type) => {
        this.resetScheduleConf(type);
    }

    // 通知方式列表
    renderSendTypeList = (data) => {
        return data && data.map((item) => {
            return <Checkbox 
                key={item.value} 
                value={item.value.toString()}>
                {item.name}
            </Checkbox>
        });
    }


    onBeginDateChange = (date, dateString) => {
        this.changeScheduleConfTime('beginDate', dateString);
    }

    onEndDateChange = (date, dateString) => {
        this.changeScheduleConfTime('endDate', dateString);
    }

    // 通知人下拉框
    renderUserList = (data) => {
        return data.map((item) => {
            return <Option 
                key={item.id} 
                value={item.id.toString()}>
                {item.userName}
            </Option>
        });
    }

    // 通知方式回调
    onSendTypeChange = (value) => {
        const { form } = this.props;
        const { currentData } = this.state;

        let receivers = form.getFieldValue('receivers');

        if (value.length === 0 && receivers.length === 0) {
            form.setFieldsValue({ receivers: [] });
        }

        let notifyVO = {...currentData.notifyVO, sendTypes: value};

        this.setState({
            currentData: {...currentData, notifyVO}
        });
    }

    // 通知人回调
    onNotifyUserChange = (value) => {
        const { form } = this.props;
        const { currentData } = this.state;

        let sendTypes = form.getFieldValue('sendTypes');

        if (value.length === 0 && sendTypes.length === 0) {
            form.setFieldsValue({ sendTypes: [] });
        }

        let notifyVO = {...currentData.notifyVO, receivers: value};

        this.setState({
            currentData: {...currentData, notifyVO}
        });
    }

    // 调度日期回调
    changeScheduleConfTime = (type, value) => {
        const { scheduleConfObj, currentData } = this.state;

        let newParams = {};
        newParams[type] = value;
        let newConfObj = { ...scheduleConfObj, ...newParams };

        this.setState({
            scheduleConfObj: newConfObj,
            currentData: {
                ...currentData, 
                scheduleConf: JSON.stringify(newConfObj)
            }
        });
    }

    // 根据调度类型的不同返回不同的调度配置
    renderDynamic() {
        const { form, common } = this.props;
        const { getFieldDecorator } = form;
        const { periodType, notifyType } = common;
        const { scheduleConfObj } = this.state;

        // 小时选择框
        const generateHours = (type) => {
            let options = [];

            for (let i = 0; i <= 23; i++) {
                options.push(
                    <Option 
                        key={i} 
                        value={`${i}`}>
                        {i < 10 ? `0${i}`: i}
                    </Option>
                );
            }

            return <Select 
                style={{ width: 150 }} 
                onChange={this.changeScheduleConfTime.bind(this, type)}>
                {options}
            </Select>;
        };

        // 分钟选择框
        const generateMins = (type) => {
            let options = [];

            for (let i = 0; i <= 59; i++) {
                options.push(
                    <Option 
                        key={i} 
                        value={`${i}`}>
                        {i < 10 ? `0${i}`: i}
                    </Option>
                );
            }

            return <Select 
                style={{ width: 150 }} 
                onChange={this.changeScheduleConfTime.bind(this, type)}>
                {options}
            </Select>;
        };

        // 间隔时间选择框
        const generateGapHour = () => {
            let options = [];

            for (let i = 1; i <= 23; i++) {
                options.push(
                    <Option 
                        key={i} 
                        value={`${i}`}>
                        {i}小时
                    </Option>
                );
            }
            
            return <Select
                style={{ width: 150 }}
                onChange={this.changeScheduleConfTime.bind(this, 'gapHour')}>
                {options}
            </Select>
        };

        // 月份内天数选择框
        const generateDate = () => {
            let options = [];

            for (let i = 1; i <= 31; i++) {
                options.push(
                    <Option 
                        key={i} 
                        value={`${i}`}>
                        {`每月${i}号`}
                    </Option>
                );
            }

            return <Select
                mode="multiple"
                style={{ width: 328 }}
                onChange={this.changeScheduleConfTime.bind(this, 'day')}>
                {options}
            </Select>;
        };

        // 周内天数选择框
        const generateDays = () => {
            return <Select
                mode="multiple"
                style={{ width: 328 }}
                onChange={this.changeScheduleConfTime.bind(this, 'weekDay')}>
                <Option key={1} value="1">星期一</Option>
                <Option key={2} value="2">星期二</Option>
                <Option key={3} value="3">星期三</Option>
                <Option key={4} value="4">星期四</Option>
                <Option key={5} value="5">星期五</Option>
                <Option key={6} value="6">星期六</Option>
                <Option key={7} value="7">星期天</Option>
            </Select>
        }

        switch (scheduleConfObj.periodType) {
            case '1': {
                return <div>
                    <FormItem {...formItemLayout} label="开始时间">
                        {
                            getFieldDecorator('beginHour', {
                                rules: [{
                                    required: true, 
                                    message: '开始时间不能为空'
                                }, {
                                    validator: this.checkTime.bind(this)
                                }],
                                initialValue: `${scheduleConfObj.beginHour}`
                            })(
                                generateHours('beginHour')
                            )
                        }
                        <span className="m-8">
                            时
                        </span>
                        {
                            getFieldDecorator('beginMin', {
                                rules: [{
                                    required: true, 
                                    message: '开始时间不能为空'
                                }, {
                                    validator: this.checkTime.bind(this)
                                }],
                                initialValue: `${scheduleConfObj.beginMin}`
                            })(
                                generateMins('beginMin')
                            )
                        }
                        <span className="m-8">
                            分
                        </span>
                    </FormItem>

                    <FormItem {...formItemLayout} label="间隔时间">
                        {
                            getFieldDecorator('gapHour', {
                                rules: [{
                                    required: true, 
                                    message: '间隔时间不能为空'
                                }],
                                initialValue: scheduleConfObj.gapHour ? scheduleConfObj.gapHour : ''
                            })(
                                generateGapHour()
                            )
                        }
                    </FormItem>

                    <FormItem {...formItemLayout} label="结束时间">
                        {
                            getFieldDecorator('endHour', {
                                rules: [{
                                    required: true, 
                                    message: '结束时间不能为空'
                                }, {
                                    validator: this.checkTime.bind(this)
                                }],
                                initialValue: `${scheduleConfObj.endHour}`
                            })(
                                generateHours('endHour')
                            )
                        }
                        <span className="m-8">
                            时
                        </span>
                        {
                            getFieldDecorator('endMin', {
                                rules: [{
                                    required: true, 
                                    message: '结束时间不能为空'
                                }, {
                                    validator: this.checkTime.bind(this)
                                }],
                                initialValue: `${scheduleConfObj.endMin}`
                            })(
                                generateMins('endMin')
                            )
                        }
                        <span className="m-8">
                            分
                        </span>
                    </FormItem>
                </div>
            }

            case '2': {
                return <FormItem {...formItemLayout} label="起调周期">
                    {
                        getFieldDecorator('hour', {
                            rules: [{
                                required: true
                            }],
                            initialValue: `${scheduleConfObj.hour}`
                        })(
                            generateHours('hour')
                        )
                    }
                    <span className="m-8">
                        时
                    </span>
                    {
                        getFieldDecorator('min', {
                            rules: [{
                                required: true
                            }],
                            initialValue: `${scheduleConfObj.min}`
                        })(
                            generateMins('min')
                        )
                    }
                    <span className="m-8">
                        分
                    </span>
                </FormItem>
            }

            case '3': {
                return <div>
                    <FormItem {...formItemLayout} label="选择时间">
                        {
                            getFieldDecorator('weekDay', {
                                rules: [{
                                    required: true, 
                                    message: '周内天数不能为空'
                                }],
                                initialValue: scheduleConfObj.weekDay ? scheduleConfObj.weekDay : []
                            })(
                                generateDays()
                            )
                        }
                    </FormItem>

                    <FormItem {...formItemLayout} label="起调周期">
                        {
                            getFieldDecorator('hour1', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: `${scheduleConfObj.hour}`
                            })(
                                generateHours('hour')
                            )
                        }
                        <span className="m-8">
                            时
                        </span>
                        {
                            getFieldDecorator('min1', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: `${scheduleConfObj.min}`

                            })(
                                generateMins('min')
                            )
                        }
                        <span className="m-8">
                            分
                        </span>
                    </FormItem>
                </div>
            }

            case '4': {
                return <div>
                    <FormItem {...formItemLayout} label="选择时间">
                        {
                            getFieldDecorator('day', {
                                rules: [{
                                    required: true, 
                                    message: '月内天数不能为空'
                                }],
                                initialValue: scheduleConfObj.day ? scheduleConfObj.day : []
                            })(
                                generateDate()
                            )
                        }
                    </FormItem>

                    <FormItem {...formItemLayout} label="起调周期">
                        {
                            getFieldDecorator('hour2', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: `${scheduleConfObj.hour}`
                            })(
                                generateHours('hour')
                            )
                        }
                        <span className="m-8">
                            时
                        </span>
                        {
                            getFieldDecorator('min2', {
                                rules: [{
                                    required: true
                                }],
                                initialValue: `${scheduleConfObj.min}`

                            })(
                                generateMins('min')
                            )
                        }
                        <span className="m-8">
                            分
                        </span>
                    </FormItem>
                </div>
            }

            case '5': 
            default: {
                break;
            }
        }
    }

    // 检查调度开始时间
    checkTime = (rule, value, callback) => {
        const { form } = this.props;
        let beginHour = form.getFieldValue('beginHour'),
            beginMin  = form.getFieldValue('beginMin'),
            endHour   = form.getFieldValue('endHour'),
            endMin    = form.getFieldValue('endMin'),
            beginTime = parseInt(beginHour) * 60 + parseInt(beginMin),
            endTime   = parseInt(endHour) * 60 + parseInt(endMin);

        if (beginTime >= endTime) {
            callback('开始时间不能晚于结束时间');
        } else {
            form.setFieldsValue({
                beginHour,
                beginMin,
                endHour,
                endMin
            });
        }

        callback();
    }

    // 检查生效日期
    checkDate = (rule, value, callback) => {
        const { form } = this.props;
        let beginDate = form.getFieldValue('beginDate'),
            endDate = form.getFieldValue('endDate');

        if (!beginDate || !endDate) {
            callback();
        } else {
            if (beginDate.valueOf() > endDate.valueOf()) {
                callback('生效日期的开始时间不能晚于结束时间');
            } else {
                form.setFieldsValue({
                    beginDate,
                    endDate
                });
            }
        }

        callback();
    }

    saveRuleTag = () => {
        const { form } = this.props;
        const { tagId, curCondition, conditionList, currentData, scheduleConfObj } = this.state;

        if (!isEmpty(curCondition)) {
            message.error('还有未保存的数据源');
            return
        }

        if (conditionList.length < 1) {
            message.error('请添加数据源');
            return
        }

        form.validateFields((err, values) => {
            console.log(err,values)

            if (!err) {
                TCApi.updateTagSqlInfo({
                    id: tagId,
                    conditions: conditionList,
                    notifyVO: currentData.notifyVO,
                    scheduleConf: JSON.stringify(scheduleConfObj),
                    sqlText: form.getFieldValue('sqlText'),
                    computeSourceId: currentData.computeSourceId
                }).then((res) => {

                })
            }
        })
    }

    render() {
        const { form, common } = this.props;
        const { getFieldDecorator } = form;
        const { periodType, notifyType, userList } = common;
        const { loading, currentData, conditionList, computeList, scheduleConfObj } = this.state;

        let sendTypes = currentData.notifyVO ? currentData.notifyVO.sendTypes : [],
            receivers = currentData.notifyVO ? currentData.notifyVO.receivers : [];

        const cardTitle = (
            <div><GoBack /> 配置计算逻辑</div>
        )

        return (
            <div className="box-1 m-card shadow">
                <Card 
                    title={cardTitle}
                    extra={false}
                    noHovering 
                    bordered={false}
                >
                    <div className="tag-edit-step">
                        <h2>
                            <div className="rank-number m-r-8">1</div>
                            基本信息
                            <Button 
                                type="primary" 
                                className="right"
                                onClick={this.addNewCondition}>
                                添加资源
                            </Button>
                        </h2>

                        <Table 
                            rowKey="id"
                            className="m-table"
                            columns={this.initColumns()} 
                            loading={loading}
                            pagination={false}
                            dataSource={conditionList}
                        />
                    </div>

                    <div className="tag-edit-step">
                        <h2>
                            <div className="rank-number m-r-8">2</div>
                            同步目标计算引擎
                        </h2>
                        <FormItem>
                            {
                                getFieldDecorator('computeSourceId', {
                                    rules: [{
                                        required: true, 
                                        message: '不可为空'
                                    }], 
                                    initialValue: currentData.computeSourceId
                                })(
                                    <Select
                                        style={{ width: 300 }}
                                        placeholder="选择要同步的计算引擎"
                                        onChange={this.onComputeSourceChange}>
                                        {
                                            this.renderComputeSource(computeList)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                    </div>

                    <div className="tag-edit-step">
                        <h2>
                            <div className="rank-number m-r-8">3</div>
                            根据以上数据源输入指标计算sql
                        </h2>
                        <FormItem>
                            {
                                getFieldDecorator('sqlText', {
                                    rules: [{
                                        required: true, 
                                        message: 'SQL不可为空'
                                    }], 
                                    initialValue: currentData.sqlText
                                })(
                                    <TextArea 
                                        placeholder="在此输入指标计算sql" 
                                        autosize={{ minRows: 6 }} 
                                        onChange={this.onSqlTextChange}
                                    />
                                )
                            }
                        </FormItem>
                    </div>

                    <div className="tag-edit-step">
                        <h2>
                            <div className="rank-number m-r-8">4</div>
                            调度时间设置
                        </h2>
                        <FormItem {...formItemLayout} label="调度周期">
                            {
                                getFieldDecorator('periodType', {
                                    rules: [{ 
                                        required: true, 
                                        message: '执行周期不能为空' 
                                    }], 
                                    initialValue: `${scheduleConfObj.periodType}`
                                })(
                                    <Select 
                                        style={{ width: 328 }}
                                        onChange={this.onPeriodTypeChange}>
                                        {
                                            this.renderPeriodType(periodType)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>

                        {
                            scheduleConfObj.periodType != 5
                            &&
                            <FormItem {...formItemLayout} label="生效日期">
                                {
                                    getFieldDecorator('beginDate', {
                                        rules: [{
                                            required: true, 
                                            message: '生效日期不能为空'
                                        }, {
                                            validator: this.checkDate.bind(this)
                                        }],
                                        initialValue: moment(scheduleConfObj.beginDate)
                                    })(
                                        <DatePicker
                                            style={{ width: 150 }}
                                            format="YYYY-MM-DD"
                                            placeholder="开始日期"
                                            onChange={this.onBeginDateChange}
                                        />
                                    )
                                }
                                <span className="m-8">
                                    到
                                </span>
                                {
                                    getFieldDecorator('endDate', {
                                        rules: [{
                                            required: true, 
                                            message: '生效日期不能为空'
                                        }, {
                                            validator: this.checkDate.bind(this)
                                        }],
                                        initialValue: moment(scheduleConfObj.endDate)
                                    })(
                                        <DatePicker
                                            style={{ width: 150 }}
                                            format="YYYY-MM-DD"
                                            placeholder="结束日期"
                                            onChange={this.onEndDateChange}
                                        />
                                    )
                                }
                            </FormItem>
                        }

                        {
                            this.renderDynamic()
                        }
                        
                        <FormItem {...formItemLayout} label="通知方式">
                            {
                                getFieldDecorator('sendTypes', {
                                    rules: [{
                                        required: receivers.length,
                                        message: '选择通知方式',
                                    }], 
                                    initialValue: sendTypes.map(item => item.toString())
                                })(
                                    <Checkbox.Group onChange={this.onSendTypeChange}>
                                        {
                                            this.renderSendTypeList(notifyType)
                                        }
                                    </Checkbox.Group>
                                )
                            }
                        </FormItem>
                        
                        <FormItem {...formItemLayout} label="通知接收人">
                            {
                                getFieldDecorator('receivers', {
                                    rules: [{
                                        required: sendTypes.length,
                                        message: '选择通知接收人',
                                    }],
                                    initialValue: receivers.map(item => item.toString())
                                })(
                                    <Select 
                                        allowClear 
                                        mode="multiple" 
                                        style={{ width: 328 }}
                                        onChange={this.onNotifyUserChange}>
                                        {
                                            this.renderUserList(userList)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                    </div>

                    <div className="tag-edit-step">
                        <Button 
                            type="primary" 
                            className="right m-r-8">
                            <Link to="/dl/tagConfig">取消</Link>
                        </Button>
                        <Button 
                            type="primary" 
                            className="right"
                            onClick={this.saveRuleTag}>
                            保存
                        </Button>
                    </div>
                </Card>
            </div>
        )
    }
}
RuleTagEdit = Form.create()(RuleTagEdit);