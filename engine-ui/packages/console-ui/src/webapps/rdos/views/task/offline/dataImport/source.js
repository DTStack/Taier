import React, { Component } from 'react'

import { 
    Modal, Button, Form, Table, Checkbox,
    Icon, Input, Select, Row, Radio,
} from 'antd';

import TableCell from 'widgets/tableCell'
import { formItemLayout } from '../../../../comm/const'


const FormItem = Form.Item
const Option = Select.Option
const RadioGroup = Radio.Group

export default class ImportSource extends Component {

    state = {
        radio: 1,
    }

    splitChange = (value) => {
        this.props.changeStatus({splitSymbol: value})
    }

    charsetChange = (value) => {
        this.props.changeStatus({charset: value})
    }

    asTitle = (e) => {
        const value = e.target.checked
        this.props.changeStatus({ 
            asTitle: value,
            matchType: value ? 1 : 0,
         })
    }

    changeStartLine = (e) => {
        const { formState } = this.props
        let value = e.target.value 
        value = value > formState.originLineCount ? formState.originLineCount : value
        this.props.changeStatus({ startLine: value })
    }

    radioChange = (e) => {
        this.setState({
            radio: e.target.value,
        });
    }

    generateCols = (asTitle, data) => {
        if (data && data.length > 0) {
            const arr = []
            if (asTitle) {
                data.forEach((item, index) => {
                    arr.push({
                        title: item,
                        width: 100,
                        key: index + item,
                        render: (text, item) => {
                            return <TableCell style={{minWidth: 100}} value={item[index]} />
                        },
                    })
                })
                return arr
            } else {
                data.forEach((item, index) => {
                    const title = `col-${index + 1}`
                    arr.push({
                        key: title,
                        width: 100,
                        title: title,
                        render: (text, item) => {
                            return <TableCell style={{minWidth: 100}} value={item[index]} />
                        }
                    })
                }) 
                return arr
            }
        }
        return []
    }

    getRowKey(record, index) {
        return `${record.length > 0 ? record[0] : ''}-${index}`;
    }

    render() {
        const { data, file, display, formState } = this.props
        const columns = this.generateCols(formState.asTitle, data[0]);
        const dataSource = formState.asTitle ? data.slice(1) : data;

        console.log('data:', data)
        console.log('dataSource:', dataSource)

        const { radio } = this.state

        return (
            <div style={{display: display === 'source' ? 'block' : 'none'}}>
                <Row>
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="已选文件"
                        >
                            <span style={{fontSize: '14px'}}>{file.name || ''}</span>
                            &nbsp;&nbsp;
                            <span style={{color: '#f60'}}>
                                只支持.txt、.csv和.log文件类型
                            </span>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="分隔符"
                        >
                            <RadioGroup onChange={this.radioChange} defaultValue={1}>
                                <Radio value={1}/>
                                <Select 
                                    disabled={radio === 2}
                                    style={{width: '80px'}} 
                                    value={formState.splitSymbol}
                                    onChange={this.splitChange}>
                                    <Option value=",">逗号</Option>
                                    <Option value="tab">Tab</Option>
                                    <Option value=";">分号</Option>
                                    <Option value="blank">空格</Option>
                                    <Option value="|">|</Option>
                                    <Option value="#">#</Option>
                                    <Option value="&">&</Option>
                                </Select>
                                &nbsp;&nbsp;<Radio value={2}>自定义</Radio>
                                <Input 
                                    disabled={radio === 1}
                                    onChange={(e) => {
                                        this.splitChange(e.target.value)
                                    }}
                                    style={{width: '80px'}}
                                />
                            </RadioGroup>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="原始字符集"
                        >
                            <Select 
                                value={formState.charset}
                                onChange={this.charsetChange}
                            >
                                <Option value="UTF-8">UTF-8</Option>
                                <Option value="GBK">GBK</Option>
                                <Option value="CP936">CP936</Option>
                                <Option value="ISO-8859">ISO-8859</Option>
                            </Select>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="导入起始行"
                        >
                            <Input 
                                onChange={this.changeStartLine}
                                type="number"
                                min={1}
                                max={data.length}
                                value={formState.startLine} 
                            />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="首行为标题"
                        >
                            <Checkbox 
                                checked={formState.asTitle}
                                onChange={this.asTitle}
                            >
                                是
                            </Checkbox>
                        </FormItem>
                    </Form>
                </Row>
                <Row className="no-table-padding">
                    <Table 
                        rowKey={this.getRowKey}
                        scroll={{ y: 240, x: 800, }} 
                        bordered
                        dataSource={dataSource} 
                        columns={columns}
                    />
                </Row>
            </div>
        )
    }

}