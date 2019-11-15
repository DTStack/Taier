import * as React from 'react';
import { Link } from 'react-router';
import { Card, Table, Dropdown, Checkbox, Icon, Col, Button } from 'antd';
import styled from 'styled-components'

interface IState {
    pageNo: number;
    pageSize: number;
    total: number;
    dataSource: any[];
    searchVal: string;
    loading: boolean;
    desc: boolean;
    sorterField: string;
}

const basePath = '/groupAnalyse';

const Title = styled.span`
    font-size: 12px;
    color: #999999;
    letter-spacing: 0
`;

export default class GroupSpecimenList extends React.Component<any, IState> {
    state: IState = {
        pageNo: 1,
        pageSize: 20,
        total: 2,
        dataSource: [{
            id: 1, name: '关系名称1', desc: '描述', entiry: '关联实体', updateTime: '2019-12-10 12:33', creator: '创建者一号', useNum: '4000'
        }, {
            id: 2, name: '关系名称2', desc: '描述', entiry: '关联实体', updateTime: '2019-12-10 12:33', creator: '创建者一号', useNum: '4000'
        }],
        searchVal: undefined,
        loading: false,
        desc: true,
        sorterField: ''
    }

    componentDidMount () {

    }

    loadData = () => {

    }

    handleSearch = (query: any) => {
        this.setState({
            searchVal: query,
            pageNo: 1
        }, this.loadData)
    }

    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        this.setState({
            pageNo: pagination.current,
            sorterField: sorter.field || '',
            desc: sorter.order == 'descend' || false
        }, this.loadData);
    }

    onFilterChange = () => {
        // TODO delete a relation entity.
    }

    initColumns = () => {
        return [{
            title: '群组名称',
            dataIndex: 'name',
            key: 'name',
            render: (text: any, record: any) => {
                return <Link to={{ pathname: `${basePath}/detail`, state: record }}>{text}</Link>
            }
        }, {
            title: '群组数据量',
            dataIndex: 'entiry',
            key: 'entiry'
        }, {
            title: '群组类型',
            dataIndex: 'desc',
            key: 'desc'
        }, {
            title: '创建者',
            dataIndex: 'creator',
            key: 'creator'
        }, {
            title: '创建时间',
            dataIndex: 'useNum',
            key: 'useNum',
            sorter: true
        }, {
            title: '操作',
            dataIndex: 'operation',
            key: 'operation',
            width: '150px'
        }]
    }

    render () {
        const { total, pageSize, pageNo, dataSource, loading } = this.state;
        const pagination: any = {
            total: total,
            pageSize: pageSize,
            current: pageNo
        };

        const overlay = (
            <div>
                <Checkbox.Group onChange={this.onFilterChange}>
                    <Col span={8}><Checkbox value="A">A</Checkbox></Col>
                    <span className="ant-divider" />
                    <Col span={8}><Checkbox value="ALL">全选</Checkbox></Col>
                </Checkbox.Group>
            </div>
        );

        const extra = (
            <Dropdown
                overlay={overlay}
            >
                <Button className="right" type="primary">新增群组<Icon type="down" /></Button>
            </Dropdown>
        )
        return (
            <Card
                title={<Title>样本列表抽样展示部分样本用于进一步细查单样本信息，最多1000条</Title>}
                extra={extra}
                noHovering
                bordered={false}
                bodyStyle={{ padding: '20px 0px' }}
                className="noBorderBottom"
            >
                <Table
                    rowKey="id"
                    className="dt-ant-table dt-ant-table--border full-screen-table-47"
                    pagination={pagination}
                    onChange={this.handleTableChange}
                    loading={loading}
                    columns={this.initColumns()}
                    dataSource={dataSource}
                />
            </Card>
        )
    }
}
