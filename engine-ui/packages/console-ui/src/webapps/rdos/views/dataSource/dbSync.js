import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link, hashHistory } from 'react-router';
import { isEmpty } from 'lodash';
import { 
    Table, Card, Form, Radio, Modal, Checkbox, Row, Col,
    Button, Input, Select, Icon, InputNumber, DatePicker, Tooltip, message, Alert, Progress
} from 'antd';
import moment from 'moment';

import GoBack from 'main/components/go-back';
import Api from '../../api';

const Option = Select.Option;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;
const RadioGroup = Radio.Group;

const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 3 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 },
    },
}

export default class DBSync extends Component {

    state = {
        percent: 0,
        visible: false,
        tableList: [],
        selectedTable: [],
        transformFields: [],
        config: {},
        hourTime: 1,
        successNum: 0,
        failNum: 0
    }

    componentDidMount() {
        this.getTableList(this.props.routeParams.sourceId);
    }

    getTableList = (sourceId) => {
        Api.getOfflineTableList({
            sourceId: sourceId,
            isSys: false
        }).then(res => {
            if (res.code === 1) {
                let tableList = res.data.map(item => {
                    return { tableName: item }
                });

                this.setState({ tableList });
            }
        });
    }

    // table设置
    initColumns = () => {
        return [{
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
            width: '30%',
        }, {
            title: 'DTinsight.IDE',
            dataIndex: 'tableName',
            key: 'ideTableName',
            width: '30%',
        }, {
            title: '任务状态',
            width: '30%',
            render: (text, record) => {
                if (record.status) {
                    return record.status === 1 ? 
                    <div>
                        <Icon type="check-circle" style={{ color: 'green', marginRight: 10 }} /> 
                        成功 
                    </div>
                    : 
                    <div>
                        <Icon type="close-circle" style={{ color: 'red', marginRight: 10 }} />
                        {record.report}
                    </div>
                }
            },
        }]
    }

    onDateChange = (date, dateString) => {
        console.log(date, dateString)
    }

    onHourTimeChange = (value) => {
        this.setState({ hourTime: value });
    }

    // 点数选择框
    generateDayHours = () => {
        let options = [];

        for (let i = 0; i <= 23; i++) {
            options.push(
                <Option 
                    key={i} 
                    value={`${i}`}>
                    {i < 10 ? `0${i} : 00`: `${i} : 00`}
                </Option>
            );
        }

        return <Select 
            style={{ width: 300 }}>
            {options}
        </Select>
    }

    // 小时选择框
    generateHours = () => {
        let options = [];

        for (let i = 1; i <= 23; i++) {
            options.push(
                <Option 
                    key={i} 
                    value={`${i}`}>
                    {`${i} 小时`}
                </Option>
            );
        }

        return <Select 
            className="m-r-8"
            defaultValue={'1'}
            style={{ width: 100 }}
            onChange={this.onHourTimeChange}>
            {options}
        </Select>
    }

    publishTask = () => {
        const { selectedTable, hourTime } = this.state;
        const { form, routeParams } = this.props;

        if (selectedTable.length < 1) {
            message.error('至少选择一张要同步的表');
            return;
        }

        form.validateFields((err, values) => {
            console.log(err,values)
            this.setState((prevState, props) => {
                return {
                    percent: 0,
                    successNum: 0,
                    failNum: 0
                };
            });

            if(!err) {
                let params = {
                    dataSourceId: routeParams.sourceId,
                    scheduleConf: JSON.stringify({
                        beginDate: values.beginDate[0].format('YYYY-MM-DD'),
                        endDate: values.beginDate[1].format('YYYY-MM-DD'),
                        periodType: '2',
                        hour: values.hour
                    }),
                    syncType: values.syncType,
                    timeFieldIdentifier: values.syncType == 1 ? values.timeFieldIdentifier : undefined,
                    parallelType: values.parallelType,
                    parallelConfig: values.parallelType == 1 ? {
                        hourTime: hourTime,
                        tableNum: values.tableNum
                    } : undefined,
                    transformFields: []
                }

                if (params.parallelType === 1 && !this.checkParallelConfig(params.parallelConfig)) {
                    message.error('您所选的某些同步任务可能会在该日24点后才能执行，请检查您的执行计划再提交');
                    return;
                }

                if (values.saveConfig) {
                    Api.saveSyncConfig(params).then(res => {
                        if (res.code === 1) {
                            message.success('保存成功')
                            this.publishSyncTask(params, res.data);
                        }
                    })
                } else {
                    this.publishSyncTask(params);
                }
            }
        });
    }

    publishSyncTask = async (params, mid) => {
        const { selectedTable, tableList } = this.state;

        for (let tableName of selectedTable) {
            params.table = tableName;
            params.oldTable = tableName;
            params.migrationId = mid ? mid : undefined;
    
            let res = await Api.publishSyncTask(params);
            let isFail = res.code != 1 || res.data.status != 1;
            let percent = parseInt(((selectedTable.indexOf(tableName) + 1) / selectedTable.length) * 100);
           
            if (res.code === 1) {
                let newTableList = [...tableList];
                let curIndex = newTableList.indexOf(newTableList.filter(item => item.tableName === tableName)[0]);

                newTableList[curIndex].status = res.data.status;
                newTableList[curIndex].report = res.data.report;

                this.setState({
                    tableList: newTableList
                });
            }

            this.setState((prevState) => {
                return {
                    percent: percent,
                    successNum: isFail ? prevState.successNum : prevState.successNum + 1,
                    failNum: isFail ? prevState.failNum + 1 : prevState.failNum
                }
            });
        }
    }

    // 所选表是否能一天同步完
    checkParallelConfig = (config) => {
        const { selectedTable } = this.state;

        let canSyncNum = Math.floor(24 / config.hourTime);
        console.log(canSyncNum, config)

        return selectedTable.length > canSyncNum ? false : true;
    }

    saveSyncConfig = () => {

    }

    onSaveChange = (e) => {
        console.log(e)
    }

    openConfigModal = () => {
        this.setState({ visible: true });
    }

    closeConfigModal = () => {
        this.setState({ visible: false });
    }

    render() {
        const { form, routeParams } = this.props;
        const { getFieldDecorator } = form;
        const { percent, visible, tableList, selectedTable, successNum, failNum } = this.state;

        // 差异比对选择配置
        const rowSelection = {
            selectedRowKeys: selectedTable,
            onChange: (selectedIds) => {
                this.setState({
                    selectedTable: selectedIds
                });
            }
        };

        return (
            <div className="box-1 m-card shadow">
                <Card 
                    title={<div><GoBack /> {routeParams.sourceName}</div>}
                    extra={false}
                    noHovering 
                    bordered={false}
                >
                    <Alert message={`注意：生成的同步任务，每天周期运行，产出表只有一级分区为pt，
                        用户需注意数据库负载。产出的目录为 clone_database/${routeParams.sourceName}`} type="info" showIcon />
                    
                    <div className="sync-content">
                        <div className="sync-title">
                            选择要同步的数据表
                            <Button
                                type="primary"
                                onClick={this.openConfigModal}
                            >
                                高级设置
                            </Button>
                        </div>

                        <Table 
                            bordered
                            rowKey="tableName"
                            className="m-table sync-table"
                            columns={this.initColumns()} 
                            pagination={false}
                            rowSelection={rowSelection}
                            dataSource={tableList}
                            scroll={{ y: 300 }}
                        />

                        <Form>
                            <FormItem {...formItemLayout} label="生效日期">
                                {
                                    getFieldDecorator('beginDate', {
                                        rules: [{
                                            required: true, 
                                            message: '生效日期不能为空'
                                        }],
                                        initialValue: [moment(), moment().add(100, 'years')]
                                    })(
                                        <RangePicker
                                            format="YYYY-MM-DD"
                                            style={{ width: 300 }}
                                            placeholder={['开始时间', '结束时间']}
                                            onChange={this.onDateChange}
                                        />
                                    )
                                }
                            </FormItem>
                            <FormItem {...formItemLayout} label="具体时间">
                                {
                                    getFieldDecorator('hour', {
                                        rules: [{
                                            required: true,
                                            message: '具体开始时间不能为空'
                                        }],
                                        initialValue: '0'
                                    })(
                                        this.generateDayHours()
                                    )
                                }
                            </FormItem>
                            <FormItem {...formItemLayout} label="同步方式">
                                {
                                    getFieldDecorator('syncType', {
                                        rules: [{
                                            required: true,
                                            message: '选择同步方式',
                                        }], 
                                        initialValue: 1
                                    })(
                                        <RadioGroup>
                                            <Radio value={1}>增量</Radio>
                                            <Radio value={2}>全量</Radio>
                                        </RadioGroup>
                                    )
                                }
                            </FormItem>

                            {
                                form.getFieldValue('syncType') === 1
                                &&
                                <FormItem {...formItemLayout} label="根据日期字段">
                                    {
                                        getFieldDecorator('timeFieldIdentifier', {
                                            rules: [{
                                                required: true,
                                                message: '日期字段不能为空',
                                            }],
                                        })(
                                            <Input
                                                style={{ width: 300, height: 32 }}
                                                placeholder="使用标志数据变更的时间字段，如gmt_modified"
                                                suffix={
                                                    <Tooltip title="使用可唯一标识数据变更时间的字段，会抽取时间为业务日期范围内的数据">
                                                        <Icon type="question-circle-o" />
                                                    </Tooltip>
                                                }
                                            />
                                        )
                                    }
                                </FormItem>
                            }

                            <FormItem {...formItemLayout} label="并发配置">
                                {
                                    getFieldDecorator('parallelType', {
                                        rules: [{
                                            required: true,
                                            message: '选择并发配置',
                                        }], 
                                        initialValue: 1
                                    })(
                                        <RadioGroup>
                                            <Radio value={1}>分批上传</Radio>
                                            <Radio value={2}>整批上传</Radio>
                                            <Tooltip title="如果数据表过多，请尽量分批上传，以免数据库负载过高，影响业务">
                                                <Icon type="question-circle-o" />
                                            </Tooltip>
                                        </RadioGroup>
                                    )
                                }
                            </FormItem>

                            {
                                form.getFieldValue('parallelType') === 1
                                &&
                                <FormItem {...formItemLayout} label="从启动时间开始，每" colon={false}>
                                    {
                                        this.generateHours()
                                    }
                                    <span>同步</span>
                                    {
                                        getFieldDecorator('tableNum', {
                                            rules: [{
                                                required: true,
                                                message: '不可为空',
                                            }],
                                        })(
                                            <InputNumber 
                                                min={1} 
                                                max={10}
                                                step={1}
                                                precision={0}
                                                className="m-l-8"
                                                style={{ width: 100 }}
                                            />
                                        )
                                    }
                                    <span>个表</span>
                                </FormItem>
                            }

                            <FormItem {...formItemLayout} label="是否保存配置">
                                {
                                    getFieldDecorator('saveConfig', {
                                        rules: [], 
                                    })(
                                        <Checkbox onChange={this.onSaveChange}>保存</Checkbox>
                                    )
                                }
                            </FormItem>
                        </Form>

                        <div className="sync-action">
                            <Button 
                                type="primary" 
                                onClick={this.publishTask}>
                                发布任务
                            </Button>
                            <span className="m-v-10">
                                进度
                            </span>
                            <Progress 
                                style={{ flexBasis: '30%' }} 
                                percent={percent} 
                            />
                            <span className="m-v-10">
                                共：{selectedTable.length} 个
                            </span>
                            <span className="m-v-10">
                                成功：{successNum} 个
                            </span>
                            <span className="m-v-10">
                                失败：{failNum} 个
                            </span>
                        </div>
                    </div>

                    <Modal
                        title="高级设置"
                        width={'50%'}
                        visible={visible}
                        maskClosable={false}
                        okText="保存"
                        cancelText="取消"
                        onOk={this.closeConfigModal}
                        onCancel={this.closeConfigModal}
                    >
                        <Form layout="inline">
                            <Row>
                                <FormItem label="表名转换规则">
                                    {
                                        getFieldDecorator('tableNameRule', {
                                            rules: [], 
                                        })(
                                            <Input />
                                        )
                                    }
                                </FormItem>
                                <FormItem>
                                    {
                                        getFieldDecorator('tableNameRule1', {
                                            rules: [], 
                                        })(
                                            <Input />
                                        )
                                    }
                                </FormItem>
                            </Row>
                            <Row>
                                <FormItem label="字段名转换规则">
                                    {
                                        getFieldDecorator('columnNameRule', {
                                            rules: [], 
                                        })(
                                            <Input />
                                        )
                                    }
                                </FormItem>
                                <FormItem>
                                    {
                                        getFieldDecorator('columnNameRule1', {
                                            rules: [], 
                                        })(
                                            <Input />
                                        )
                                    }
                                </FormItem>
                            </Row>
                            <Row>
                                <FormItem label="字段类型转换规则">
                                    {
                                        getFieldDecorator('columnTypeRule', {
                                            rules: [], 
                                        })(
                                            <Input />
                                        )
                                    }
                                </FormItem>
                                <FormItem>
                                    {
                                        getFieldDecorator('columnTypeRule1', {
                                            rules: [], 
                                        })(
                                            <Input />
                                        )
                                    }
                                </FormItem>
                            </Row>
                        </Form>

                    </Modal>
                </Card>
            </div>
        )
    }
}
DBSync = Form.create()(DBSync);