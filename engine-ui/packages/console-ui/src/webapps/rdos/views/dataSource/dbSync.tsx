import * as React from 'react';
import {
    Table, Card, Form, Radio,
    Checkbox, Button, Input, Select,
    Icon, InputNumber, DatePicker, Tooltip,
    Alert, Progress, message
} from 'antd';
import moment from 'moment';

import TransformModal from './transformModal';
import GoBack from 'main/components/go-back';
import Api from '../../api';

const Option = Select.Option;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;
const RadioGroup = Radio.Group;

const formItemLayout: any = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 3 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 }
    }
}

class DBSync extends React.Component<any, any> {
    state: any = {
        percent: 0,
        loading: false,
        visible: false,
        tableList: [],
        selectedTable: [],
        transformFields: [],
        hourTime: 1,
        successNum: 0,
        failNum: 0
    }

    componentDidMount () {
        this.getTableList(this.props.routeParams.sourceId);
    }

    // 获取所有表
    getTableList = (sourceId: any) => {
        Api.getOfflineTableList({
            sourceId: sourceId,
            isSys: false
        }).then((res: any) => {
            if (res.code === 1) {
                let tableList = res.data.map((item: any) => {
                    return { tableName: item }
                });

                this.setState({ tableList });
            }
        });
    }

    changeTransformFields = (value: any) => {
        this.setState({ transformFields: value });
    }

    exchangeIdeTableName (text: any) {
        const { transformFields } = this.state;
        const exchangeArr = transformFields.filter((field: any) => {
            return field.convertObject == 1;
        });
        for (let i = 0; i < exchangeArr.length; i++) {
            text = text.replace(new RegExp(exchangeArr[i].convertSrc, 'g'), exchangeArr[i].convertDest);
        }
        return text;
    }

    // table设置
    initColumns = () => {
        return [{
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
            width: '32%'
        }, {
            title: 'DTinsight.IDE',
            dataIndex: 'tableName',
            key: 'ideTableName',
            width: '32%',
            render: (text: any, record: any) => {
                return this.exchangeIdeTableName(text);
            }
        }, {
            title: '任务状态',
            width: '28%',
            render: (text: any, record: any) => {
                if (record.status) {
                    return record.status === 1
                        ? <div>
                            <Icon type="check-circle" style={{ color: 'green', marginRight: 10 }} />
                            成功
                        </div>
                        : <div>
                            <Icon type="close-circle" style={{ color: 'red', marginRight: 10 }} />
                            {record.report}
                        </div>
                }
            }
        }]
    }

    onHourTimeChange = (value: any) => {
        this.setState({ hourTime: value });
    }

    // 点数选择框
    generateDayHours = () => {
        let options: any = [];

        for (let i = 0; i <= 23; i++) {
            options.push(
                <Option
                    key={i}
                    value={`${i}`}>
                    {i < 10 ? `0${i} : 00` : `${i} : 00`}
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
        let options: any = [];

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

    // 发布所有任务
    publishTask = () => {
        const { selectedTable, hourTime, transformFields } = this.state;
        const { form, routeParams } = this.props;

        if (selectedTable.length < 1) {
            message.error('至少选择一张要同步的表');
            return;
        }

        form.validateFields((err: any, values: any) => {
            this.setState((prevState: any, props: any) => {
                return {
                    percent: 0,
                    successNum: 0,
                    failNum: 0
                };
            });

            if (!err) {
                let params: any = {
                    dataSourceId: routeParams.sourceId,
                    scheduleConf: JSON.stringify({
                        beginDate: values.beginDate[0].format('YYYY-MM-DD'),
                        endDate: values.beginDate[1].format('YYYY-MM-DD'),
                        periodType: '2',
                        hour: parseInt(values.hour),
                        min: 0
                    }),
                    syncType: values.syncType,
                    timeFieldIdentifier: values.syncType == 1 ? values.timeFieldIdentifier : undefined,
                    parallelType: values.parallelType,
                    parallelConfig: values.parallelType == 1 ? {
                        hourTime: hourTime,
                        tableNum: values.tableNum
                    } : undefined,
                    transformFields: transformFields
                }

                if (params.parallelType === 1 && !this.checkParallelConfig(params.parallelConfig, values.hour)) {
                    message.error('您所选的某些同步任务可能会在该日24点后才能执行，请检查您的执行计划再提交');
                    return;
                }

                this.setState({ loading: true });

                if (values.saveConfig) {
                    Api.saveSyncConfig(params).then((res: any) => {
                        if (res.code === 1) {
                            message.success('保存成功');
                            this.publishSyncTask(params, res.data);
                        }
                    })
                } else {
                    this.publishSyncTask(params);
                }
            }
        });
    }

    // 逐表发布
    publishSyncTask = async (params?: any, mid?: any) => {
        const { selectedTable, tableList } = this.state;

        let times = 1;

        let scheduleConf = JSON.parse(params.scheduleConf);

        let parallelConfig = params.parallelConfig;

        for (let tableName of selectedTable) {
            let index = selectedTable.indexOf(tableName);

            params.table = tableName;
            params.oldTable = tableName;
            params.migrationId = mid || undefined;

            // 任务调度时间的变化
            if (parallelConfig && index >= parallelConfig.tableNum * times) {
                scheduleConf.hour += parallelConfig.hourTime;
                ++times;
            }

            params.scheduleConf = JSON.stringify(scheduleConf);

            let res = await Api.publishSyncTask(params);

            let isFail = res.code != 1 || res.data.status != 1;

            let percent = parseInt(String(((index + 1) / selectedTable.length) * 100));

            if (res.code === 1) {
                let newTableList: any = [...tableList];

                let curIndex = newTableList.indexOf(newTableList.filter((item: any) => item.tableName === tableName)[0]);

                newTableList[curIndex].status = res.data.status;
                newTableList[curIndex].report = res.data.report;

                this.setState({ tableList: newTableList });
            }

            this.setState((prevState: any) => {
                return {
                    percent: percent,
                    successNum: isFail ? prevState.successNum : prevState.successNum + 1,
                    failNum: isFail ? prevState.failNum + 1 : prevState.failNum
                }
            });
        }

        this.setState({ loading: false });
    }

    // 所选表是否能一天同步完
    checkParallelConfig = (config: any, startTime: any) => {
        const { selectedTable } = this.state;

        let time = 24 - parseInt(startTime);

        let canSyncNum = Math.floor(time / config.hourTime) * config.tableNum;

        return !(selectedTable.length > canSyncNum);
    }

    openConfigModal = () => {
        this.setState({ visible: true });
    }

    closeConfigModal = () => {
        this.setState({ visible: false });
    }

    render () {
        const { form, routeParams } = this.props;
        const { getFieldDecorator } = form;
        const { percent, loading, visible, tableList, selectedTable, successNum, failNum, transformFields } = this.state;

        const rowSelection: any = {
            selectedRowKeys: selectedTable,
            onChange: (selectedIds: any) => {
                this.setState({ selectedTable: selectedIds });
            }
        };

        return (
            <div className="m-card shadow">
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
                            className="dt-ant-table sync-table select-all-table dirt-table-header_rmscroll"
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
                                        initialValue: [
                                            moment('2001-01-01'),
                                            moment('2001-01-01').add(120, 'years')
                                        ]
                                    })(
                                        <RangePicker
                                            format="YYYY-MM-DD"
                                            style={{ width: 300 }}
                                            placeholder={['开始时间', '结束时间']}
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
                                            message: '选择同步方式'
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
                                form.getFieldValue('syncType') === 1 &&
                                <FormItem {...formItemLayout} label="根据日期字段">
                                    {
                                        getFieldDecorator('timeFieldIdentifier', {
                                            rules: [{
                                                required: true,
                                                message: '日期字段不能为空'
                                            }]
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
                                            message: '选择并发配置'
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
                                form.getFieldValue('parallelType') === 1 &&
                                <FormItem {...formItemLayout} label="从启动时间开始，每隔" colon={false}>
                                    {
                                        this.generateHours()
                                    }
                                    <span>同步</span>
                                    {
                                        getFieldDecorator('tableNum', {
                                            rules: [{
                                                required: true,
                                                message: '不可为空'
                                            }]
                                        })(
                                            <InputNumber
                                                min={1}
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
                                        rules: []
                                    })(
                                        <Checkbox>保存</Checkbox>
                                    )
                                }
                            </FormItem>
                        </Form>

                        <div className="sync-action">
                            <Button
                                type="primary"
                                loading={loading}
                                onClick={this.publishTask}>
                                发布任务
                            </Button>
                            <span className="m-h-10">
                                进度
                            </span>
                            <Progress
                                style={{ flexBasis: '30%' }}
                                percent={percent}
                            />
                            <span className="m-h-10">
                                共：{selectedTable.length} 个
                            </span>
                            <span className="m-h-10">
                                成功：{successNum} 个
                            </span>
                            <span className="m-h-10">
                                失败：{failNum} 个
                            </span>
                        </div>
                    </div>

                    <TransformModal
                        visible={visible}
                        transformFields={transformFields}
                        changeTransformFields={this.changeTransformFields}
                        closeModal={this.closeConfigModal}
                    />

                </Card>
            </div>
        )
    }
}
const DBSyncWrapper = Form.create<any>()(DBSync);

export default DBSyncWrapper;
