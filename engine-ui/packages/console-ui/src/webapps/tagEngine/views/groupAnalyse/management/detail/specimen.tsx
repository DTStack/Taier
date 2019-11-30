import * as React from 'react';
import { Card, Table, Dropdown, Checkbox, Icon, Button } from 'antd';
import styled from 'styled-components';

import { updateComponentState } from 'funcs';

import GroupAPI from '../../../../api/group';
import { IQueryParams } from '../../../../model/comm';

interface IState {
    dataSource: any[];
    loading: boolean;
    visibleDropdown: boolean;
    queryParams: { groupId: string; columns?: any[] } & IQueryParams ;
}

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

const Overlay = styled.div`
    background: #FFFFFF;
    box-shadow: 0 2px 6px 0 rgba(0,0,0,0.10);
    border-radius: 2px;
    border-radius: 2px;
    width: 180px;
    height: auto;
`

const OverlayRow = styled.div`
    padding: 5px;
`

export default class GroupSpecimenList extends React.Component<any, IState> {
    state: IState = {
        dataSource: [],
        loading: false,
        visibleDropdown: false,
        queryParams: {
            columns: [],
            groupId: null,
            current: 1,
            size: 20,
            orders: [{
                asc: false,
                field: 'updateAt'
            }]
        }
    }

    componentDidMount () {
        this.loadData();
    }

    loadData = async () => {
        const { params } = this.props.router;
        const { queryParams } = this.state;
        queryParams.groupId = params.groupId;
        const res = await GroupAPI.getGroupSpecimens(queryParams);
        if (res.code === 1) {
            const data = res.data;
            updateComponentState(this, {
                dataSource: data.contentList,
                queryParams: {
                    current: data.current,
                    size: data.size,
                    total: data.total
                }
            })
            this.setState({
                dataSource: data.contentList
            })
        }
    }

    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        const params: IQueryParams = {
            current: pagination.current
        };
        if (sorter) {
            params.orders = [{
                asc: sorter.order !== 'descend',
                field: sorter.field
            }]
        }
        updateComponentState(this, { queryParams: params }, this.loadData)
    }

    onFilterChange = async (checkedValue: any) => {
        // TODO delete a relation entity.
        console.log('checkedValue:', checkedValue);
        updateComponentState(this, { queryParams: {
            columns: checkedValue
        } }, this.loadData)
    }

    initColumns = () => {
        return []
    }

    onDropDownChange = () => {
        this.setState({
            visibleDropdown: !this.state.visibleDropdown
        })
    }

    render () {
        const { dataSource, loading, queryParams } = this.state;
        const pagination: any = {
            total: queryParams.total,
            pageSize: queryParams.size,
            current: queryParams.current
        };

        const overlay = (
            <Overlay>
                <Checkbox.Group onChange={this.onFilterChange}>
                    <OverlayRow><Checkbox value="A">A</Checkbox></OverlayRow>
                    <OverlayRow><Checkbox value="B">B</Checkbox></OverlayRow>
                    <OverlayRow><Checkbox value="C">C</Checkbox></OverlayRow>
                    <div style={{ height: '1px', width: '100%' }} className="ant-divider" />
                    <OverlayRow><Checkbox value="ALL">全选</Checkbox></OverlayRow>
                </Checkbox.Group>
            </Overlay>
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
                            visible={this.state.visibleDropdown}
                            trigger={['click']}
                            overlay={overlay}
                        >
                            <Button onClick={this.onDropDownChange} className="right" type="primary">
                                设置显示维度<Icon type="down" />
                            </Button>
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
