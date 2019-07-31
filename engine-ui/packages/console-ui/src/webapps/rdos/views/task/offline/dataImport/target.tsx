import * as React from 'react'
import { connect } from 'react-redux'
import { debounce } from 'lodash'
import {
    Modal, Button, Form, Radio, message,
    Input, Select, Row, Table
} from 'antd';
import utils from 'utils'

import Editor from 'widgets/editor';
import TableEngineSelect from '../../../../components/engineSelect';

import CopyIcon from 'main/components/copy-icon';
import API from '../../../../api/dataManage';
import { formItemLayout } from '../../../../comm/const';
import { DDL_IDE_PLACEHOLDER, LIBRA_DDL_IDE_PLACEHOLDER } from '../../../../comm/DDLCommon'
import { isLibraTable } from '../../../../comm';
import { getTableList } from '../../../../store/modules/offlineTask/comm';
import { getProjectTableTypes } from '../../../../store/modules/tableType';
import HelpDoc, { relativeStyle } from '../../../helpDoc';

const RadioGroup = Radio.Group
const FormItem = Form.Item
const Option = Select.Option

// Table Data
@(connect((state: any) as any) => {
    return {
        project: state.project,
        tables: state.offlineTask.comm.tables,
        projectTableTypes: state.tableTypes.projectTableTypes
    }
}, (dispatch: any) => {
    return {
        getTableList: (projectId: any) => {
            dispatch(getTableList(projectId));
        },
        getProjectTableTypes: (projectId: any) => {
            dispatch(getProjectTableTypes(projectId));
        }
    }
})
class ImportTarget extends React.Component<any, any> {
    state: any = {
        visible: false,
        pagination: {
            current: 1,
            pageSize: 10
        }
    }
    componentDidMount () {
        const { project, getProjectTableTypes } = this.props;
        const projectId = project.id;
        if (projectId) {
            getProjectTableTypes(projectId)
        }
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { visible } = this.props;
        const { visible: visibleNext } = nextProps.visible;
        if (visible != visibleNext && !visibleNext) {
            this.setState({
                pagination: {
                    current: 1,
                    pageSize: 10
                }
            })
        }
    }

    tableInput = (tableName: any) => {
        const { changeStatus, formState } = this.props
        const { tableType } = formState;
        let params: any = {
            tableName
        };
        if (tableType) {
            params.tableType = tableType
        }
        if (tableName.length > 0) {
            API.getTablesByName(params).then((res: any) => {
                if (res.code === 1) {
                    changeStatus({
                        tableList: res.data || []
                    });
                }
            })
        }
    }

    tbNameOnChange = (tableName: any) => {
        const params: any = {
            queryTable: tableName
        }
        if (tableName === '') {
            params.tableData = {}
        }
        this.props.changeStatus(params);
    }

    onTableEngineChange = (value: any) => {
        this.props.changeStatus({
            tableType: value,
            sqlText: null,
            sync: true,
            queryTable: '',
            tableList: [],
            tableData: {}
        });
    }

    debounceSearch = debounce(this.tableInput, 500, { 'maxWait': 2000 })

    tableChange = (value: any, option: any) => {
        const table = option.props.data
        const { changeStatus, data } = this.props;
        const fileColumns = data[0] || [];

        // 加载分区
        API.getTable({ tableId: table.id }).then((res: any) => {
            if (res.code === 1) {
                const tableData = res.data
                const columnMap = tableData.column && tableData.column.map((item: any) => {
                    // 假如发现和文件资源column有相等的columnName，则直接默认设置为此columnName。
                    const columnName = item.columnName;
                    const index = fileColumns.indexOf(columnName);

                    if (index > -1) {
                        return { key: columnName };
                    }
                    return '';
                })
                const partitions = tableData.partition && tableData.partition.map((item: any) => {
                    return {
                        [item.columnName]: ''
                    }
                })
                changeStatus({
                    targetTable: table,
                    queryTable: table.tableName,
                    tableData: tableData,
                    hasPartition: tableData.partition && tableData.partition.length > 0,
                    columnMap: columnMap,
                    originPartitions: tableData.partition,
                    partitions: partitions,
                    targetExchangeWarning: false
                })
            }
        })
    }

    // 检测分区十分存在
    checkPartition = () => {
        const { targetTable, partitions, originPartitions } = this.props.formState
        for (let i = 0; i < partitions.length; i++) {
            const item = partitions[i]
            const key = originPartitions[i].columnName
            if (utils.trim(item[key]) === '') {
                message.error('分区值不可为空！')
                return
            }
        }

        API.checkTablePartition({
            tableId: targetTable.id,
            partitionInfo: partitions
        }).then((res: any) => {
            if (res.data) {
                message.success('分区存在！')
            } else {
                message.error('分区不存在！')
            }
        })
    }
    getTableList () {
        const { project, getTableList } = this.props;
        const projectId = project.id;
        if (projectId) {
            getTableList(projectId);
        }
    }
    createTable = () => {
        const { sqlText, tableType } = this.props.formState;
        if (!tableType) {
            message.error('请先选择表类型！');
            return;
        }
        API.createDdlTable({ sql: sqlText, tableType }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    visible: false,
                    tableList: [res.data]
                })
                this.tbNameOnChange(res.data.id)
                this.tableChange(res.data.id, { props: { data: res.data } })
                this.getTableList();
                message.success('表创建成功!')
            }
        })
    }

    handleCancel = () => {
        this.setState({
            visible: false
        })
        this.props.changeStatus({
            sqlText: null,
            sync: true
        })
    }

    mapChange = (value: any, index: any) => {
        const { formState, changeStatus } = this.props
        const arr: any = [...formState.columnMap];
        arr[index] = Object.assign({}, arr[index], {
            key: value
        });
        changeStatus({
            columnMap: arr,
            targetExchangeWarning: false
        })
    }

    onFormatChange = (value: any, index: any) => {
        const { formState, changeStatus } = this.props
        const arr: any = [...formState.columnMap];
        arr[index] = Object.assign({}, arr[index], {
            format: value
        });
        changeStatus({
            columnMap: arr,
            targetExchangeWarning: false
        })
    }

    debounceFormatChange = debounce(this.onFormatChange, 500, { 'maxWait': 2000 })

    onTableChange(pagination: any) {
        this.setState({
            pagination: pagination
        })
    }

    changeMatchWay = (e: any) => {
        const { formState } = this.props
        if (formState.asTitle) {
            this.props.changeStatus({
                matchType: e.target.value,
                targetExchangeWarning: false
            })
        }
    }

    changeImportMode = (e: any) => {
        this.props.changeStatus({
            overwriteFlag: e.target.value
        })
    }

    tablePartitionChange = (e: any, partition: any, index: any) => {
        const originPartitions = this.props.formState.partitions
        const newPartitions: any = [...originPartitions]
        newPartitions[index] = {
            [partition.columnName]: e.target.value
        }
        this.props.changeStatus({
            partitions: newPartitions
        })
    }

    ddlChange = (newVal: any) => {
        const { changeStatus } = this.props
        changeStatus({
            sqlText: newVal,
            sync: false
        })
    }

    generateCols = (data: any) => {
        const { formState, warning } = this.props;
        const { pagination } = this.state;
        const { columnMap } = formState;
        const options = data && data.length ? data[0].map((item: any, index: any) => {
            return (
                <Option key={`item`} value={item}>
                    {item}
                </Option>
            )
        }) : [];

        const sourceTitle = (
            <span>源字段 {warning && <span style={{ color: '#ce3b3b', float: 'right' }}>请至少选择一个源字段</span>}</span>
        )

        const arr: any = [{
            title: '目标字段',
            key: 'target_part',
            render: (text: any, record: any) => {
                return (
                    <span>{record.columnName}</span>
                )
            }
        }, {
            title: sourceTitle,
            key: 'source_part',
            render: (text: any, record: any, index: any) => {
                let columnIndex = index + (pagination.current - 1) * pagination.pageSize;
                return (<span>
                    <Select
                        value={formState.matchType === 0 ? '' : (columnMap[columnIndex] && columnMap[columnIndex].key) || ''}
                        disabled={formState.matchType === 0}
                        onSelect={(value: any) => { this.mapChange(value, columnIndex) }}
                        style={{ width: '200px' }}
                    >
                        <Option key={`col-null`} value={''}>
                            空字段
                        </Option>
                        {options}
                    </Select>
                </span>)
            }
        }, {
            title: '操作',
            key: 'operation',
            render: (text: any, record: any, index: any) => {
                let columnIndex = index + (pagination.current - 1) * pagination.pageSize;
                const colmunType = record.columnType && record.columnType.toLowerCase();
                const showFormat = colmunType === 'date' || colmunType === 'datetime' ||
                    colmunType === 'time' || colmunType === 'timestamp';
                return showFormat ? (
                    <span>
                        <Input
                            value={columnMap[columnIndex] && columnMap[columnIndex].format}
                            placeholder="字段格式化"
                            style={{ width: 100, marginRight: 5 }}
                            onChange={(e: any) => this.onFormatChange(e.target.value, columnIndex)}
                        />
                        <HelpDoc style={relativeStyle} doc="dateTimeFormat" />
                    </span>
                ) : ''
            }
        }]
        return arr
    }

    generatePartitions = (data: any) => {
        return data.map((item: any, index: any) => {
            return (
                <Row key={`partition-${index}`}>
                    <div
                        className="ellipsis"
                        title={item.columnName}
                        style={{ maxWidth: '30px', display: 'inline-block', float: 'left' }}
                    >
                        {item.columnName}
                    </div>
                    {
                        index === 0
                            ? <Button
                                type="primary"
                                style={{ float: 'right', width: '68px' }}
                                onClick={this.checkPartition}>检测
                            </Button>
                            : ''
                    }
                    <Input
                        style={{ width: '163px', marginRight: '17px', float: 'right' }}
                        onChange={(e: any) => { this.tablePartitionChange(e, item, index) }}
                        placeholder="请输入分区名称" />
                    &nbsp;&nbsp;
                    {
                        index === data.length - 1
                            ? <span style={{ color: '#f60' }}>
                                <br />
                                点击<b>检测</b>按钮，测试分区是否存在
                            </span> : ''
                    }
                </Row>
            )
        })
    }

    render () {
        const { data, display, formState, projectTableTypes = [] } = this.props
        const { tableList, tableData, queryTable, asTitle, sync, sqlText, tableType } = formState
        const { pagination } = this.state;
        const { getFieldDecorator } = this.props.form;
        const columns = this.generateCols(data, tableData)

        const paritions = this.generatePartitions(tableData.partition || [])
        const dataSource = tableData && tableData.column

        const tableOptions = tableList.map((item: any, index: any) =>
            <Option key={`table-${index}`} data={item} value={item.tableName}>
                {item.tableName}
            </Option>
        )

        const DDL_TEMPLATE = isLibraTable(tableType) ? LIBRA_DDL_IDE_PLACEHOLDER : DDL_IDE_PLACEHOLDER;

        return (
            <div style={{ display: display === 'target' ? 'block' : 'none' }}>
                <Row>
                    <Form>
                        <FormItem
                            required
                            label="表类型"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('tableType', {

                            })(
                                <TableEngineSelect
                                    tableTypes={projectTableTypes}
                                    placeholder="请选择表类型"
                                    onChange={this.onTableEngineChange}
                                />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="导入至表"
                        >
                            <div>
                                <Select
                                    mode="combobox"
                                    style={{ width: '200px' }}
                                    onChange={this.tbNameOnChange}
                                    onSearch={this.debounceSearch}
                                    onSelect={this.tableChange}
                                    notFoundContent="没有发现相关表"
                                    value={queryTable}
                                    placeholder="请输入表名"
                                    showArrow={false}
                                    filterOption={false}
                                    defaultActiveFirstOption={false}
                                >
                                    {tableOptions}
                                </Select>
                                &nbsp;&nbsp;
                                <Button
                                    type="primary"
                                    style={{ float: 'right' }}
                                    onClick={() => {
                                        this.setState({ visible: true })
                                    }}>新建表</Button>
                            </div>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            style={{
                                display: tableData.partition &&
                                    tableData.partition.length > 0 ? 'block' : 'none'
                            }}
                            label="分区"
                        >
                            {paritions}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="字段匹配"
                        >
                            <RadioGroup
                                value={formState.matchType}
                                onChange={this.changeMatchWay}
                            >
                                <Radio value={0}>按位置匹配</Radio>
                                <Radio disabled={!asTitle} value={1}>按名称匹配</Radio>
                            </RadioGroup>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="导入模式"
                        >
                            <RadioGroup
                                value={formState.overwriteFlag}
                                onChange={this.changeImportMode}
                            >
                                <Radio value={1}>覆盖</Radio>
                                <Radio value={0}>追加</Radio>
                            </RadioGroup>
                        </FormItem>
                    </Form>
                </Row>
                <Row>
                    <Table
                        className="dt-ant-table dt-ant-table--border"
                        rowKey="id"
                        bordered
                        columns={columns}
                        dataSource={dataSource}
                        onChange={this.onTableChange.bind(this)}
                        pagination={pagination}
                    />
                </Row>
                <Modal className="m-codemodal"
                    title={(
                        <span>建表语句<CopyIcon title="复制模版" style={{ marginLeft: '8px' }} copyText={DDL_TEMPLATE} /></span>
                    )}
                    maskClosable={false}
                    style={{ height: 424 }}
                    visible={this.state.visible}
                    onCancel={this.handleCancel}
                    onOk={this.createTable}
                >
                    <Editor
                        language="dtsql"
                        placeholder={DDL_TEMPLATE}
                        onChange={this.ddlChange}
                        sync={sync}
                        value={sqlText}
                    />
                </Modal>
            </div>
        )
    }
}

export default Form.create<any>()(ImportTarget);
