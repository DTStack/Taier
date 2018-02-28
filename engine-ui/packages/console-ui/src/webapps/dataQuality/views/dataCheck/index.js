import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Row, Table, Button, Icon, Input, Form,  } from 'antd'

const Search = Input.Search;
const InputGroup = Input.Group;

export default class DataCheck extends Component {

    componentDidMount() {
    	console.log(this)
    }

    initColumns = () => {
        return [{
            title: '数据源名称',
            dataIndex: 'dataName',
            key: 'dataName',
            width: 80,
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            width: 80,
            render: (text, record) => {
                return <DatabaseType value={record.type} />
            },
            filters: DataSourceTypeFilter,
            filterMultiple: false,
        }, 
        {
            title: '描述',
            dataIndex: 'dataDesc',
            key: 'dataDesc',
            width: 100,
        }, {
            title: '最近修改人',
            dataIndex: 'modifyUserId',
            key: 'modifyUserId',
            render: (text, record) => {
                return record.modifyUser ? record.modifyUser.userName : ''
            }
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text),
        }, {
            title: '状态',
            dataIndex: 'active',
            key: 'active',
            width: 100,
            render: (text, record) => {
                return record.active === 1 ? '使用中' : '未启用'
            },
        }, {
            title: '操作',
            width: 100,
            key: 'operation',
            render: (text, record) => {
                 // active  '0：未启用，1：使用中'。  只有为0时，可以修改
                return (
                    <span key={record.id}>
                        <a onClick={() => {this.initEdit(record)}}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title="确定删除此数据源？"
                            okText="确定" cancelText="取消"
                            onConfirm={() => { this.remove(record) }}
                        >
                            <a>删除</a>
                        </Popconfirm>
                    </span>
                )
            },
        }]
    }

    render() {
    	const { getFieldDecorator } = this.props.form;

        return (
        	<div className="box-1">
        		<div className="rule-action">
        			<InputGroup compact>
                        {
                            getFieldDecorator('itemValue', {
                                rules: [
                                    { required: true, message: '不能为空' },
                                ],
                                initialValue: ''
                            })(
                                <Input 
                                    placeholder="分析对象，支持车牌号码、手机三码、mac地址" 
                                    style={{ width: 300 }} 
                                    onChange={this.handleInputChange}
                                    onFocus={this.openTrailPop} />
                            )
                        }
                        <Button type="primary" onClick={this.handleClickAnalyse}>分析</Button>
                    </InputGroup>
                    <Button type="primary" style={{ marginRight: 15 }} onClick={this.showModal}><Icon type="plus" />新建规则</Button>
                    
                    <Search
                        className="rule-search"
                        placeholder="请输入关键字"
                        onSearch={value => console.log(value)}
                    />
                </div>
	            <div className="box-1">
	                <h1 className="txt-center">DataCheck111</h1>
	            </div>
        	</div>
        )
    }
}
DataCheck = Form.create()(DataCheck);


