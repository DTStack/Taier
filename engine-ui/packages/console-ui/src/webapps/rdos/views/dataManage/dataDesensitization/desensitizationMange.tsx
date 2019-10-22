import * as React from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import ajax from '../../../api/dataManage';
import { Input, Spin, Table, Button, Card, Popconfirm, message, Tabs, Select } from 'antd';
import '../../../styles/pages/dataManage.scss';
import SlidePane from 'widgets/slidePane'
import AddDesensitization from './addDesensitization';
import TableRelation from './tableRelation';
import BloodRelation from './bloodRelation';
const Search = Input.Search;
const TabPane = Tabs.TabPane;
const Option: any = Select.Option;
@(connect((state: any) => {
    return {
        projects: state.projects,
        user: state.user
    }
}, null) as any)

class DesensitizationMange extends React.Component<any, any> {
    state: any = {
        cardLoading: false,
        addVisible: false,
        visibleSlidePane: false, // 查看脱敏明细
        selectedId: '', // 选中脱敏
        nowView: 'tableRelation', // 默认选中表关系
        table: [], // 表数据
        editModalKey: null,
        queryParams: {
            currentPage: 1,
            pageSize: 20,
            name: undefined,
            pjId: undefined
        },
        total: 0,
        tableInfo: {} // 表点击查看血缘信息
    }
    componentDidMount () {
        this.search();
    }
    search = () => {
        this.setState({
            cardLoading: true
        })
        const { queryParams } = this.state;
        ajax.searchDesensitization(queryParams).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    table: res.data.data,
                    total: res.data.totalCount,
                    cardLoading: false
                })
            } else {
                this.setState({
                    cardLoading: false
                })
            }
        })
    }
    changeName = (e: any) => {
        const { queryParams } = this.state;
        this.setState({
            queryParams: Object.assign(queryParams, {
                name: e.target.value,
                currentPage: 1
            })
        })
    }
    changeProject = (value: any) => {
        const { queryParams } = this.state;
        this.setState({
            queryParams: Object.assign(queryParams, {
                pjId: value,
                currentPage: 1
            })
        }, this.search)
    }
    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        const queryParams = Object.assign(this.state.queryParams, { currentPage: pagination.current })
        this.setState({
            queryParams
        }, this.search)
    }
    showaddModal = () => {
        const { pjId } = this.state.queryParams;
        ajax.voidCheckPermission({
            projectId: pjId
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    addVisible: true,
                    editModalKey: Math.random()
                })
            }
        })
    }
    /**
     * 添加脱敏
     */
    addDesensitization = (desensitization: any) => {
        ajax.addDesensitization(desensitization).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    addVisible: false
                })
                message.success('添加成功!');
                this.search();
            }
        })
    }
    delete = (record: any) => {
        ajax.delDesensitization({
            id: record.id,
            projectId: record.projectId
        }).then((res: any) => {
            if (res.code === 1) {
                message.success('删除成功!');
                this.search()
            }
        })
    }
    // 打开面板
    showDesensitization = (record: any) => {
        this.setState({
            visibleSlidePane: true,
            selectedId: record
        })
    }
    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false
            // selectedId: null
        })
    }
    /**
     * Tab栏切换
     */
    onTabChange = (tabKey: any) => {
        this.setState({
            nowView: tabKey
        })
    }
    /**
     * 获取点击具体查看血缘
     */
    handleClickTable = (tableInfo: any) => {
        this.setState({
            tableInfo
        })
    }
    initialColumns = () => {
        return [
            {
                title: '脱敏名称',
                width: 140,
                dataIndex: 'name',
                render: (text: any, record: any) => {
                    return (
                        <a onClick={() => { this.showDesensitization(record) }}>{text}</a>
                    )
                }
            },
            {
                title: '项目名称',
                width: 140,
                dataIndex: 'projectName'
            },
            {
                title: '项目显示名称',
                width: 140,
                dataIndex: 'projectAlia'
            },
            {
                title: '关联表数量',
                width: 140,
                dataIndex: 'relatedNum'
            },
            {
                title: '脱敏规则',
                width: 140,
                dataIndex: 'ruleName'
            },
            {
                title: '最近修改人',
                width: 200,
                dataIndex: 'modifyUserName'
            },
            {
                title: '最近修改时间',
                width: 200,
                dataIndex: 'gmtModified',
                render (text: any, record: any) {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '操作',
                width: 140,
                dataIndex: 'opera',
                render: (text: any, record: any) => {
                    return (
                        <Popconfirm
                            title="确定删除此条脱敏吗?"
                            okText="是"
                            cancelText="否"
                            onConfirm={() => { this.delete(record) }}
                        >
                            <a>删除</a>
                        </Popconfirm>
                    )
                }
            }
        ]
    }
    render () {
        const columns = this.initialColumns();
        const { cardLoading, table, editModalKey, addVisible, visibleSlidePane, selectedId, nowView, tableInfo, queryParams, total } = this.state;
        const { projects } = this.props;
        const pagination: any = {
            current: queryParams.currentPage,
            pageSize: queryParams.pageSize,
            total
        }
        const projectsOptions = projects.map((item: any) => {
            return <Option
                title={item.projectAlias}
                key={item.id}
                name={item.projectAlias}
                value={`${item.id}`}
            >
                {item.projectAlias}
            </Option>
        })
        return (
            <div className='m-card d-desen__list'>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <div>
                            所属项目：
                            <Select
                                allowClear
                                showSearch
                                style={{ width: '150px', marginRight: '10px' }}
                                placeholder='请选择所属项目'
                                optionFilterProp='children'
                                filterOption={(inputVal: any, option: any) => {
                                    return option.props.children.toLowerCase().indexOf(inputVal.toLowerCase()) >= 0
                                }}
                                onChange={this.changeProject}
                            >
                                {projectsOptions}
                            </Select>
                            <Search
                                placeholder='按脱敏名称搜索'
                                style={{ width: '200px' }}
                                onChange={this.changeName}
                                onSearch={this.search}
                            />
                        </div>
                    }
                    extra={
                        <Button
                            type='primary'
                            style={{ marginTop: '10px' }}
                            onClick={this.showaddModal}
                        >
                            添加脱敏
                        </Button>
                    }
                >
                    <Spin tip="正在加载中..." spinning={cardLoading}>
                        <Table
                            className="dt-ant-table dt-ant-table--border rdos-ant-table-placeholder"
                            rowClassName={
                                (record: any, index: any) => {
                                    if (this.state.selectedId && this.state.selectedId.id == record.id) {
                                        return 'row-select'
                                    } else {
                                        return '';
                                    }
                                }
                            }
                            columns={columns}
                            dataSource={table}
                            pagination={pagination}
                            onChange={this.handleTableChange.bind(this)}
                        />
                    </Spin>
                </Card>
                <SlidePane
                    className="m-tabs bd-top bd-right m-slide-pane"
                    onClose={this.closeSlidePane}
                    visible={visibleSlidePane}
                    style={{ right: '0px', width: '90%', height: '100%', minHeight: '600px' }}
                >
                    <div className='pane-height100-tab'>
                        <Tabs animated={false} onChange={this.onTabChange} activeKey={nowView} className='pane-tabs'>
                            <TabPane tab="表关系" key="tableRelation" className='tab_pane'>
                                <TableRelation
                                    tabKey={nowView}
                                    visibleSlidePane={visibleSlidePane}
                                    onTabChange={this.onTabChange}
                                    handleClickTable={this.handleClickTable}
                                    tableData={selectedId}
                                />
                            </TabPane>
                            <TabPane tab="血缘关系" key="bloodRelation" className='tab_pane'>
                                <BloodRelation
                                    tabKey={nowView}
                                    // onTabChange={this.onTabChange}
                                    visibleSlidePane={visibleSlidePane}
                                    tableDetail={tableInfo}
                                />
                            </TabPane>
                        </Tabs>
                    </div>
                </SlidePane>
                <AddDesensitization
                    key={editModalKey}
                    visible={addVisible}
                    onTabChange={this.onTabChange}
                    onCancel={() => { this.setState({ addVisible: false }) }}
                    onOk={this.addDesensitization}
                />
            </div>
        )
    }
}
export default DesensitizationMange;
