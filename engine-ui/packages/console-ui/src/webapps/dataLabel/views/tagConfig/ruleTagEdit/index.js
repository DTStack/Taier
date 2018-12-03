import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link, hashHistory } from 'react-router';
import { isEmpty } from 'lodash';
import {
    Table, Card, Form,
    Button, Input, Select,
    Popconfirm, DatePicker,
    Checkbox, message
} from 'antd';
import moment from 'moment';

import GoBack from 'main/components/go-back';
import { dataSourceActions } from '../../../actions/dataSource';
import { rowFormItemLayout } from '../../../consts';
import TCApi from '../../../api/tagConfig';

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

const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 9 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 }
    }
}

const mapStateToProps = state => {
    const { common, dataSource } = state;
    return { common, dataSource }
}

const mapDispatchToProps = dispatch => ({
    getDataSourcesList (params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
    getDataSourcesTable (params) {
        dispatch(dataSourceActions.getDataSourcesTable(params));
    },
    resetDataSourcesTable () {
        dispatch(dataSourceActions.resetDataSourcesTable());
    }
})

@connect(mapStateToProps, mapDispatchToProps)
class RuleTagEdit extends Component {
    state = {
        tagId: this.props.routeParams.id,
        currentData: {},
        curCondition: {},
        conditionList: [],
        computeList: [],
        scheduleConfObj: initialSchedule,
        notifyVO: {
            sendTypes: [],
            receivers: []
        }
    }

    componentDidMount () {
        this.getTagDetailInfo();
        this.getComputeSourceData();
        this.props.getDataSourcesList();
    }

    // 获取标签详细数据
    getTagDetailInfo = () => {
        TCApi.getRuleTagDetail({ tagId: this.state.tagId }).then((res) => {
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
    }

    // 获取计算引擎数据
    getComputeSourceData = () => {
        TCApi.getComputeSource().then((res) => {
            if (res.code === 1) {
                this.setState({
                    computeList: res.data
                });
            }
        });
    }
    /* eslint-disable */
    // table设置
    initColumns = () => {
        return [{
            title: '数据源',
            dataIndex: 'dataSourceId',
            key: 'dataSourceId',
            width: '25%',
            render: (text, record) => this.renderColumns(text, record, 'dataSourceId')
        }, {
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
            width: '25%',
            render: (text, record) => this.renderColumns(text, record, 'tableName')
        }, {
            title: '过滤条件',
            dataIndex: 'sqlCondition',
            key: 'sqlCondition',
            width: '40%',
            render: (text, record) => this.renderColumns(text, record, 'sqlCondition')
        }, {
            title: '操作',
            width: '10%',
            render: (text, record) => {
                return (
                    <div>
                        {
                            record.editable ?
                                <span>
                                    <a className="m-r-8" onClick={this.saveCondition.bind(this, record.id)}>
                                        保存
                                    </a>
                                    <a onClick={this.cancelEdit.bind(this, record.id)}>
                                        取消
                                    </a>
                                </span>
                                :
                                <span>
                                    <a className="m-r-8" onClick={this.editCondition.bind(this, record.id)}>
                                        编辑
                                    </a>
                                    <Popconfirm title="确定要删除吗？" onConfirm={this.deleteCondition.bind(this, record.id)}>
                                        <a>删除</a>
                                    </Popconfirm>
                                </span>
                        }
                    </div>
                );
            }
        }]
    }

    renderColumns (text, record, type) {
        let obj = {
            children: <Form layout="inline">
                {
                    record.editable ? this.renderEditTD(text, record, type) : this.renderTD(text, record, type)
                }
            </Form>,
            props: {}
        };

        return obj;
    }
    // 编辑状态的TD
    renderEditTD = (text, record, type) => {
        const { form, dataSource } = this.props;
        const { getFieldDecorator } = form;
        const { sourceList, sourceTable } = dataSource;
        switch (type) {
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
                                            sourceList.map(source => {
                                                let title = `${source.dataName}（${source.sourceTypeValue}）`;
                                                return <Option
                                                    key={source.id}
                                                    value={source.id.toString()}
                                                    title={title}>
                                                    {title}
                                                </Option>
                                            })
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
                            <Select showSearch>
                                {
                                    sourceTable.map(tableName => {
                                        return <Option
                                            key={tableName}
                                            value={tableName}>
                                            {tableName}
                                        </Option>
                                    })
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
                            <Input placeholder={`sql过滤条件`} />
                        )
                    }
                </FormItem>
            }
        }
    }

    // 已有数据
    renderTD = (text, record, type) => {
        switch (type) {
            case 'dataSourceId': {
                return `${record.dataSourceName}（${record.sourceTypeValue}）`
            }

            default:
                return text;
        }
    }

    // 数据源变化回调
    onDataSourceChange = (id) => {
        const { form } = this.props;

        this.props.resetDataSourcesTable();
        this.props.getDataSourcesTable({ sourceId: id });

        form.setFieldsValue({ tableName: undefined });
    }

    // 编辑规则
    editCondition(id) {
        const { curCondition, conditionList } = this.state;

        let newData = [...conditionList],
            target = newData.filter(item => id === item.id)[0];

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
            target.editStatus = 'edit';
            this.props.resetDataSourcesTable();
            this.props.getDataSourcesTable({ sourceId: target.dataSourceId });

            this.setState({
                curCondition: target,
                conditionList: newData
            });
        }
    }

    // 取消编辑
    cancelEdit(id) {
        const { conditionList } = this.state;

        let newData = [...conditionList],
            target = newData.filter(item => id === item.id)[0];

        if (target.editStatus === 'edit') {
            delete target.editable;
            delete target.editStatus;
        } else {
            newData.splice(newData.indexOf(target), 1);
        }

        this.setState({
            curCondition: {},
            conditionList: newData
        });
    }

    // 删除规则
    deleteCondition(id) {
        let newData = [...this.state.conditionList],
            target = newData.filter(item => id === item.id)[0],
            index = newData.indexOf(target);

        if (target) {
            newData.splice(index, 1);
            this.setState({ conditionList: newData });
        }
    }

    // 保存condition
    saveCondition(id) {
        const { form, dataSource } = this.props;
        const { sourceList } = dataSource;
        const { tagId, curCondition, conditionList } = this.state;

        let newData = [...conditionList],
            target = newData.filter(item => id === item.id)[0],
            index = newData.indexOf(target);

        form.validateFields(['dataSourceId', 'tableName', 'sqlCondition'], (err, values) => {
            if (!err) {
                delete curCondition.editable;

                let source = [...sourceList].filter(item => item.id == values.dataSourceId)[0];

                newData[index] = {
                    ...curCondition,
                    ...values,
                    dataSourceName: source.dataName,
                    sourceTypeValue: source.sourceTypeValue
                };

                this.setState({
                    curCondition: {},
                    conditionList: newData
                });
            }
        });
    }

    // 新增condition
    addNewCondition = () => {
        const { form } = this.props;
        const { tagId, conditionList, curCondition } = this.state;

        let newData = [...conditionList];

        if (!isEmpty(curCondition)) {
            if (curCondition.editStatus === "edit") {
                delete curCondition.editable
                delete curCondition.editStatus
            } else {
                newData.shift();
                form.resetFields();
            }
        }

        let target = {
            id: newData[0] ? newData[0].id + 1 : 1,
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

    // 调度周期回调
    onPeriodTypeChange = (type) => {
        let scheduleConfObj = { ...initialSchedule, periodType: type };

        this.setState({ scheduleConfObj });
    }

    // 通知方式回调
    onSendTypesChange = (value) => {
        const { form } = this.props;
        const { notifyVO } = this.state;

        let receivers = form.getFieldValue('receivers');

        if (value.length === 0 && receivers.length === 0) {
            form.setFieldsValue({ receivers: [] });
        }

        this.setState({ notifyVO: { ...notifyVO, sendTypes: value } });
    }

    // 通知人回调
    onReceiversChange = (value) => {
        const { form } = this.props;
        const { notifyVO } = this.state;

        let sendTypes = form.getFieldValue('sendTypes');

        if (value.length === 0 && sendTypes.length === 0) {
            form.setFieldsValue({ sendTypes: [] });
        }

        this.setState({ notifyVO: { ...notifyVO, receivers: value } });
    }

    // 调度日期回调
    changeScheduleConfTime = (type, value) => {
        const { scheduleConfObj } = this.state;

        let newValue = {};
        newValue[type] = value;

        this.setState({ scheduleConfObj: { ...scheduleConfObj, ...newValue } });
    }

    onBeginDateChange = (date, dateString) => {
        this.changeScheduleConfTime('beginDate', dateString);
    }

    onEndDateChange = (date, dateString) => {
        this.changeScheduleConfTime('endDate', dateString);
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
                        {i < 10 ? `0${i}` : i}
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
                        {i < 10 ? `0${i}` : i}
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
                                initialValue: scheduleConfObj.gapHour ? scheduleConfObj.gapHour : undefined
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
            beginMin = form.getFieldValue('beginMin'),
            endHour = form.getFieldValue('endHour'),
            endMin = form.getFieldValue('endMin'),
            beginTime = parseInt(beginHour) * 60 + parseInt(beginMin),
            endTime = parseInt(endHour) * 60 + parseInt(endMin);

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
        const { tagId, curCondition, conditionList, scheduleConfObj, notifyVO } = this.state;

        if (!isEmpty(curCondition)) {
            message.error('基本信息未保存');
            return
        }

        if (conditionList.length < 1) {
            message.error('请添加基本信息');
            return
        }

        form.validateFields((err, values) => {
            console.log(err, values)
            if (err && err.endDate) {
                message.error(err.endDate.errors[0].message)
            }

            if (!err) {
                // 新增的condition不需要id
                conditionList.forEach((element, index) => {
                    if (element.editStatus === 'new') {
                        delete element.id
                    }
                });

                TCApi.updateTagSqlInfo({
                    id: tagId,
                    conditions: conditionList,
                    notifyVO: notifyVO,
                    scheduleConf: JSON.stringify(scheduleConfObj),
                    sqlText: values.sqlText,
                    computeSourceId: values.computeSourceId
                }).then((res) => {
                    if (res.code === 1) {
                        message.success('保存成功');
                        hashHistory.push('/dl/tagConfig');
                    }
                })
            }
        })
    }

    render() {
        const { form, common } = this.props;
        const { getFieldDecorator } = form;
        const { periodType, notifyType, userList } = common;
        const { currentData, conditionList, computeList, scheduleConfObj, notifyVO } = this.state;
        const { sendTypes, receivers } = notifyVO;

        const cardTitle = (
            <div>
                <GoBack /> 配置计算逻辑
            </div>
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
                                添加
                            </Button>
                        </h2>

                        <Table
                            rowKey="id"
                            className="m-table"
                            columns={this.initColumns()}
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
                                    initialValue: currentData.computeSourceId ? currentData.computeSourceId.toString() : undefined
                                })(
                                    <Select
                                        style={{ width: 300 }}
                                        placeholder="选择要同步的计算引擎">
                                        {
                                            computeList.map(source => {
                                                let title = `${source.dataName}（${source.sourceTypeValue}）`;
                                                return <Option
                                                    key={source.id}
                                                    value={source.id.toString()}
                                                    title={title}>
                                                    {title}
                                                </Option>
                                            })
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                    </div>

                    <div className="tag-edit-step">
                        <h2>
                            <div className="rank-number m-r-8">3</div>
                            根据以上数据源输入指标计算SQL
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
                                            periodType.map(item => {
                                                return <Option
                                                    key={item.value}
                                                    value={item.value.toString()}>
                                                    {item.name}
                                                </Option>
                                            })
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
                                    <Checkbox.Group onChange={this.onSendTypesChange}>
                                        {
                                            notifyType.map(item => {
                                                return <Checkbox
                                                    key={item.value}
                                                    value={item.value.toString()}>
                                                    {item.name}
                                                </Checkbox>
                                            })
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
                                        onChange={this.onReceiversChange}>
                                        {
                                            userList.map(item => {
                                                return <Option
                                                    key={item.id}
                                                    value={item.id.toString()}>
                                                    {item.userName}
                                                </Option>
                                            })
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                    </div>

                    <div className="tag-edit-step txt-right">
                        <Link to="/dl/tagConfig">
                            <Button
                                type="primary"
                                className="m-r-8">
                                取消
                            </Button>
                        </Link>
                        <Button
                            type="primary"
                            onClick={this.saveRuleTag}>
                            保存
                        </Button>
                    </div>
                </Card>
            </div>
        )
    }
}
export default (Form.create()(RuleTagEdit));