import * as React from 'react';
import { Card, Table, Dropdown, Checkbox, Icon, Button } from 'antd';
import styled from 'styled-components';
import { get } from 'lodash';

import { updateComponentState } from 'funcs';

import GroupAPI from '../../../../api/group';
import { IQueryParams } from '../../../../model/comm';

interface IState {
    dataColumns: any[];
    dataColumnsCopy: any[];
    dataSource: any[];
    loading: boolean;
    visibleDropdown: boolean;
    queryParams: { groupId: string; columns?: any[] } & IQueryParams;
    defaultChecked: boolean;
    indeterminate: boolean;
    checkAll: boolean;
    plainOptions: any[];
    defaultList: any[];
    defaultListCopy: any[];
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
    padding: 6px;
`

export default class GroupSpecimenList extends React.Component<any, IState> {
    state: IState = {
        dataSource: [],
        loading: false,
        visibleDropdown: false,
        dataColumns: [],
        dataColumnsCopy: [],
        queryParams: {
            columns: [],
            groupId: null,
            current: 1,
            size: 20,
            orders: [{
                asc: false,
                field: 'updateAt'
            }]
        },
        defaultChecked: true,
        indeterminate: true,
        checkAll: true,
        plainOptions: [],
        defaultList: [],
        defaultListCopy: []
    }

    componentDidMount () {
        this.loadData();
    }
    loadData = async () => {
        const { router } = this.props;
        const { queryParams } = this.state;
        this.setState({
            loading: true
        });
        queryParams.groupId = get(router, 'location.query.groupId');
        const res = await GroupAPI.getGroupSpecimens(queryParams);
        if (res.code === 1) {
            const data = res.data;
            updateComponentState(this, {
                dataColumns: data.thead,
                dataColumnsCopy: data.thead,
                dataSource: data.tbody,
                defaultList: data.thead.map((item) => item.entityAttr),
                defaultListCopy: data.thead.map((item) => item.entityAttr),
                queryParams: {
                    current: Number(data.current),
                    size: Number(data.size),
                    total: Number(data.total)
                }
            })
        }
        this.setState({
            loading: false
        });
    }
    onFilterChange = async (checkedValue: any) => {
        console.log('checkedValue:', checkedValue);
        const { defaultListCopy, dataColumnsCopy } = this.state;
        const arr = dataColumnsCopy.filter(({ entityAttr }) => checkedValue.includes(entityAttr));
        this.setState({
            defaultList: checkedValue,
            indeterminate: !!checkedValue.length && checkedValue.length < defaultListCopy.length,
            checkAll: checkedValue.length === defaultListCopy.length,
            dataColumns: arr
        });
    }

    initColumns = (): any => {
        const { dataColumns = [] } = this.state;
        return dataColumns && dataColumns.map((col, index) => {
            if (index === 0) {
                return {
                    title: col.entityAttrCn,
                    width: 130,
                    dataIndex: col.entityAttr,
                    key: col.entityAttr,
                    fixed: 'left'
                }
            } else if (index === dataColumns.length - 1) {
                return {
                    title: col.entityAttrCn,
                    width: 130,
                    dataIndex: col.entityAttr,
                    key: col.entityAttr,
                    fixed: 'right'
                }
            } else {
                return {
                    title: col.entityAttrCn,
                    dataIndex: col.entityAttr,
                    key: col.entityAttr
                }
            }
        });
    }
    onDropDownChange = () => {
        this.setState({
            visibleDropdown: !this.state.visibleDropdown
        })
    }
    Cancel = () => {
        this.setState({
            visibleDropdown: false
        })
    }
    onCheckAllChange = e => {
        const { defaultListCopy } = this.state
        this.setState({
            defaultList: e.target.checked ? defaultListCopy : [],
            indeterminate: false,
            checkAll: e.target.checked
        });
        e.target.checked ? this.onFilterChange(this.state.defaultListCopy) : this.onFilterChange([]);
    };
    onPaginationChange = e => {
        const {queryParams} = this.state;
        queryParams.current = e.current
        this.setState({ queryParams})
        this.loadData()
    }
    render () {
        const { dataSource, loading, queryParams, defaultList, defaultListCopy } = this.state;
        const pagination: any = {
            total: queryParams.total,
            pageSize: queryParams.size,
            current: queryParams.current
        };
        const overlay = (
            <Overlay >
                <Checkbox.Group onChange={this.onFilterChange} value={defaultList}>
                    <div className='overlay_menu'>
                        {defaultListCopy && defaultListCopy.map(item => <OverlayRow key={item}>
                            <Checkbox value={item} >{item}</Checkbox>
                        </OverlayRow>)}
                    </div>
                    <div style={{ height: '1px', width: '95%', backgroundColor: '#DDDDDD' }} className="ant-divider" />
                </Checkbox.Group>
                <OverlayRow>
                    <Checkbox
                        indeterminate={this.state.indeterminate}
                        onChange={this.onCheckAllChange}
                        checked={this.state.checkAll}
                    >全选</Checkbox>
                    <a style={{ marginLeft: '80px' }} className="ant-dropdown-link" onClick={this.Cancel}>关闭</a>
                </OverlayRow>
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
                        // maxHeight: '500px',
                        border: '1px solid #e9e9e9',
                        borderTop: 0
                    }}
                    rowKey="id"
                    className="dt-ant-table dt-ant-table--border full-screen-table-47 bd"
                    pagination={pagination}
                    onChange={this.onPaginationChange}
                    loading={loading}
                    columns={this.initColumns()}
                    dataSource={dataSource}
                />
            </Card>
        )
    }
}
