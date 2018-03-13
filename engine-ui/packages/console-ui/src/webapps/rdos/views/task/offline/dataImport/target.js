import React, { Component } from 'react'

import { isObject, debounce } from 'lodash'

import { 
    Modal, Button, Form, Radio, message,
    Icon, Input, Select, Row, Table,
} from 'antd';

import utils from 'utils'
import API from '../../../../api'
import Editor from '../../../../components/code-editor'
import { formItemLayout } from '../../../../comm/const'

const RadioGroup = Radio.Group
const FormItem = Form.Item
const Option = Select.Option

// Table Data
export default class ImportTarget extends Component {

    state = {
        visible: false,
    }

    tableInput = (tableName) => {
        const { changeStatus } = this.props
        if (tableName.length > 0) {
            API.getTablesByName({ tableName }).then((res) => {
                if (res.code === 1) {
                    changeStatus({
                        tableList: res.data || [], 
                    });
                }
            })
        }
    }

    tbNameOnChange = (tableName) => {
        const params = {
            queryTable: tableName
        }
        if (tableName === '') {
            params.tableData = {}
        }
        this.props.changeStatus(params);
    }

    debounceSearch = debounce(this.tableInput, 500, { 'maxWait': 2000 })

    tableChange = (value, option) => {
        const table = option.props.data
        const { changeStatus } = this.props

        // 加载分区
        API.getTable({ tableId: table.tableId }).then((res) => {
            if (res.code === 1) {
                const tableData = res.data
                const columnMap = tableData.column && tableData.column.map(item => {
                    return ""
                })
                const partitions = tableData.partition && tableData.partition.map(item => {
                    return {
                        [item.name]: ''
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
                })
            }
        })
    }

    // 检测分区十分存在
    checkPartition = () => {
        const { targetTable, partitions, originPartitions } = this.props.formState
        for (let i = 0; i < partitions.length; i++) {
            const item = partitions[i]
            const key = originPartitions[i].name
            if (utils.trim(item[key]) === '') {
                message.warning('分区值不可为空！')
                return
            }
        }

        API.checkTablePartition({
            tableId: targetTable.tableId,
            partitionInfo: partitions,
        }).then((res) => {
            if (res.data) {
                message.success('分区存在！')
            } else {
                message.warning('分区不存在！')
            }
        })
    }

    createTable = () => {
        const { sqlText } = this.props.formState
        API.createDdlTable({sql: sqlText}).then((res) => {
            if (res.code === 1) {
                this.setState({
                    visible: false
                })
                message.info('表创建成功!')
            }
        })
    }
    
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }

    mapChange = (value, key) => {
        const { formState, changeStatus } = this.props
        const arr = [...formState.columnMap]
        arr[key] = value
        changeStatus({
            columnMap: arr,
        })
    }

    changeMatchWay = (e) => {
        const { formState } = this.props
        if (formState.asTitle) {
            this.props.changeStatus({
                matchType: e.target.value 
            })
        }
    }

    changeImportMode = (e) => {
        this.props.changeStatus({
            overwriteFlag: e.target.value
        })
    }

    tablePartitionChange = (e, partition, index) => {
        const originPartitions = this.props.formState.partitions
        const newPartitions = [...originPartitions]
        newPartitions[index] = { 
            [partition.name]: e.target.value,
        }
        this.props.changeStatus({
            partitions: newPartitions 
        })
    }

    ddlChange = (origin, newVal) => {
        const { changeStatus } = this.props
        changeStatus({
            sqlText: newVal 
        })
    }

    generateCols = (data) => {
        const { formState } = this.props
        const options = data ? data[0].map((item, index) => {
            return (
                <Option key={`col-${index}`} value={item}>
                    {item}
                </Option>
            )
        }) : []

        const arr = [{
            title: '目标字段',
            key: 'target_part',
            render: (text, record) => {
                return (
                    <span>{record.name}</span>
                )
            }
        }, {
            title: '源字段',
            key: 'source_part',
            render: (text, record, index) => {
                console.log('record:', record)
                return (<span>
                    <Select
                        defaultValue={""}
                        disabled={formState.matchType === 0 }
                        onSelect={(value) => { this.mapChange(value, index) }}
                        style={{width: '200px'}} 
                    >
                        <Option key={`col-null`} value={""}>
                            空字段
                        </Option>
                        {options}
                    </Select>
                </span>)
            },
        }]
        return arr
    }

    generatePartitions = (data) => {
        return data.map((item, index) => {
            return (
                <Row key={`partition-${index}`}>
                    <div style={{width: '60px', display: 'inline-block'}} >
                        {item.name}
                    </div>
                    <Input 
                        style={{width: '140px'}}
                        onChange={(e) => {this.tablePartitionChange(e, item, index)}} 
                        placeholder="请输入分区名称" />
                    &nbsp;&nbsp;
                    {
                        index === 0 ? 
                        <Button 
                            type="primary"
                            onClick={this.checkPartition}>检测
                        </Button>
                         : ''
                    }
                    {
                        index === data.length - 1 ? 
                        <span style={{color: '#f60'}}>
                            <br/>
                            点击"检测"按钮，测试分区是否存在
                        </span> : ''
                    }
                </Row>
            )
        })
    }

    render() {
        const { data, file, display, formState } = this.props
        const { tableList, tableData, queryTable } = formState

        const columns = this.generateCols(data, tableData)
        
        const paritions = this.generatePartitions(tableData.partition || [])
        const dataSource = tableData && tableData.column
        
        const tableOptions = tableList.map((item, index) => 
            <Option key={`table-${index}`} data={item} value={item.tableName}>
                {item.tableName}
            </Option>
        )

        return (
            <div style={{display: display === 'target' ? 'block' : 'none'}}>
                <Row>
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="导入至表"
                        >
                            <div>
                                <Select 
                                    mode="combobox"
                                    style={{width: '200px'}} 
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
                                    onClick={() => {
                                    this.setState({visible: true})
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
                                <Radio value={1}>按名称匹配</Radio>
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
                                <Radio value={0}>写入</Radio>
                                <Radio value={1}>覆盖</Radio>
                            </RadioGroup>
                        </FormItem>
                    </Form>
                </Row>
                <Row>
                    <Table 
                        bordered
                        columns={columns}
                        dataSource={dataSource} 
                    />
                </Row>
                <Modal className="m-codemodal"
                    title="建表语句"
                    style={{height: 350}}
                    visible={this.state.visible}
                    onCancel={this.handleCancel}
                    onOk={this.createTable}
                >
                    <Editor onChange={this.ddlChange} />
                </Modal>
            </div>
        )
    }
}