import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Row, Col, Tabs } from 'antd';

const TabPane = Tabs.TabPane;

export default class TagConfig extends Component {

    state = {
        
    }

    componentDidMount() {
    }

    // table设置
    initColumns = () => {
        return [{
            title: '表',
            dataIndex: 'tableName',
            key: 'tableName',
            render: (text, record) => (
                <a onClick={this.openSlidePane.bind(this, record)}>{text}</a>
            ),
            width: '15%'
        }, {
            title: '分区',
            dataIndex: 'partationValue',
            key: 'partationValue',
            render: (text, record) => {
                return text ? text : '--';
            },
            width: '15%'
        }, {
            title: '状态',
            width: '10%',
            dataIndex: 'status',
            key: 'status',
            render: (text, record) => {
                return <div>
                    <TaskStatus style={{ marginRight: 30 }} value={text} />
                    {
                        text === 2 
                        &&
                        <Tooltip 
                            placement="right" 
                            title={record.logInfo}
                            overlayStyle={{ wordBreak: 'break-word' }}
                        >
                            <Icon className="font-14" type="info-circle-o" />
                        </Tooltip>
                    }
                    </div>
            },
            filters: taskStatusFilter,
        }, {
            title: '规则异常数',
            dataIndex: 'alarmSum',
            key: 'alarmSum',
            width: '8%',
            // sorter: true
        }, {
            title: '类型',
            dataIndex: 'sourceTypeValue',
            key: 'sourceTypeValue',
            render: (text, record) => {
                return text ? `${text} / ${record.sourceName}` : '--';
            },
            width: '15%'
        }, {
            title: '配置人',
            dataIndex: 'configureUserName',
            key: 'configureUserName',
            width: '12%'
        }, 
        {
            title: '执行时间',
            dataIndex: 'executeTime',
            key: 'executeTime',
            render: (text) => {
                return text ? moment(text).format("YYYY-MM-DD HH:mm:ss") : '--';
            },
            width: '12%',
            sorter: true
        }]
    }
    
    render() {
        const cardTitle = (
            <div className="flex font-12">
                <Search
                    placeholder="标签名称"
                    onSearch={this.onTagSearch}
                    style={{ width: 200, margin: '10px 0' }}
                />

                <div className="m-l-8">
                    标签分类：
                    <Select 
                        allowClear
                        showSearch
                        style={{ width: 150 }}
                        // placeholder="选择数据源类型"
                        onChange={this.onSourceChange}>
                        <Option key={"1"} value={"1"}>标签1</Option>
                        <Option key={"2"} value={"2"}>标签2</Option>
                    </Select>
                </div>

                <div className="m-l-8">
                    二级分类：
                    <Select
                        allowClear 
                        showSearch
                        style={{ width: 150 }}
                        // optionFilterProp="title"
                        // placeholder="选择数据源"
                        onChange={this.onUserSourceChange}>
                        <Option key={"1"} value={"1"}>标签1</Option>
                        <Option key={"2"} value={"2"}>标签2</Option>
                    </Select>
                </div>
            </div>
        )

        return (
            <div className="tag-config">
                <div className="box-1 m-card shadow m-tabs">
                    <Tabs 
                        animated={false}
                        defaultActiveKey={'1'}
                        onChange={this.onTabChange}
                    >
                        <TabPane tab="规则标签" key="1">
                            <Card 
                                title={cardTitle}
                                noHovering 
                                bordered={false}
                            >
                                <Table 
                                    rowKey="id"
                                    className="m-table"
                                    columns={this.initColumns()} 
                                    // loading={loading}
                                    pagination={false}
                                    dataSource={[]}
                                    onChange={this.onTableChange}
                                />
                            </Card>
                        </TabPane>
                        <TabPane tab="注册标签" key="2">
                            2
                        </TabPane>
                    </Tabs>
                </div>
            </div>
        )
    }
}