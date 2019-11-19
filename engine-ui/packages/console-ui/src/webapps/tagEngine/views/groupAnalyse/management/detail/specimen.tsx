import * as React from 'react';
import { Link } from 'react-router';
import { Card, Table, Dropdown, Checkbox, Icon, Col, Button } from 'antd';
import styled from 'styled-components'

import GroupAPI from '../../../../api/group';

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

const Title = styled.div`
    font-size: 12px;
    color: #999999;
    letter-spacing: 0;
    text-indent: 5px;
    float: left;
`;

const Header = styled.div`
    margin-top: 14px;
    margin-bottom: 10px;
    height: 17px;
    position: relative;
`

const Extra = styled.div`
    position: absolute;
    right: 0;
    top: -10px;
`

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
        this.loadData();
    }

    loadData = async () => {
        const res = await GroupAPI.getGroupSpecimens();
        this.setState({
            dataSource: res.data
        })
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
            dataIndex: 'count',
            key: 'count'
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

        return (
            <Card
                title={null}
                noHovering
                bordered={false}
                className="noBorderBottom"
            >
                <Header style={{ marginTop: '14px' }}>
                    <Title>样本列表抽样展示部分样本用于进一步细查单样本信息，最多1000条</Title>
                    <Extra>
                        <Dropdown
                            overlay={overlay}
                        >
                            <Button className="right" type="primary">新增群组<Icon type="down" /></Button>
                        </Dropdown>
                    </Extra>
                </Header>
                <Table
                    style={{
                        maxHeight: '500px',
                        border: '1px solid #e9e9e9',
                        borderTop: 0
                    }}
                    rowKey="id"
                    className="dt-ant-table dt-ant-table--border full-screen-table-47 bd"
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
