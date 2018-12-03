import React from 'react';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Pagination,
    Form, DatePicker, Select, Icon, Card, Spin
} from 'antd';
import { isEmpty } from 'lodash';
import moment from 'moment';
import utils from 'utils';

import SlidePane from 'widgets/slidePane';

import ajax from '../../api/dataManage';
import * as UserAction from '../../store/modules/user'

const Search = Input.Search;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;
const Option = Select.Option;

class LogSearchForm extends React.Component {
    render () {
        const { getFieldDecorator } = this.props.form;
        const { projectUsers } = this.props;

        return (
            <Form
                className="m-form-inline"
                layout="inline"
                style={{ marginTop: '10px' }}
            >
                <FormItem label="变更时间">
                    {getFieldDecorator('range', {
                        initialValue: [moment().subtract(7, 'days'), moment()]
                    })(
                        <RangePicker size="default" style={{ width: 200 }} format="YYYY-MM-DD" />
                    )}
                </FormItem>
                <FormItem label="操作人">
                    {getFieldDecorator('actionUserId')(
                        <Select allowClear placeholder="请选择操作人" style={{ width: 126 }}>
                            {projectUsers.map(o => <Option value={`${o.userId}`}
                                key={o.userId}
                            >
                                {o.user && o.user.userName}
                            </Option>)}
                        </Select>
                    )}
                </FormItem>
                <FormItem label="变更语句">
                    {getFieldDecorator('sql')(
                        <Input placeholder="变更语句" size="default" style={{ width: 126 }}></Input>
                    )}
                </FormItem>
                <FormItem>
                    <Button type="primary" size="default" onClick={this.props.search} > 搜索 </Button>
                </FormItem>
                <FormItem>
                    {getFieldDecorator('tableId', {
                        initialValue: +this.props.tableId
                    })(
                        <Input type="hidden" />
                    )}
                </FormItem>
            </Form>
        )
    }
}

const FormWrapper = Form.create()(LogSearchForm);
class TableLog extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            logs: {},
            isDeleted: 1
        };
    }

    componentDidMount () {
        this.search();
    }

    render () {
        const { tableId, tableName, projectUsers } = this.props;
        const { logs } = this.state;
        const columns = [{
            title: '变更时间',
            width: 200,
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            render (text, record) {
                return moment(text).format('YYYY-MM-DD HH:mm:ss')
            }
        }, {
            title: '操作人',
            width: 200,
            dataIndex: 'userId',
            key: 'userId',
            render (text, record) {
                let userName;

                projectUsers.forEach(function (o) {
                    if (o.userId == text) userName = o.user && o.user.userName;
                }, this);

                return <span>{userName}</span>
            }
        }, {
            title: '操作语句',
            dataIndex: 'actionSql',
            key: 'actionSql',
            render (text) {
                return <code style={{ maxWidth: '400px', maxHeight: '100px' }}>{text}</code>
            }
        }];

        const title = (
            <FormWrapper tableId={tableId}
                ref={el => this.searchForm = el}
                search={this.search.bind(this)}
                projectUsers={projectUsers}
            />
        )

        return <div className="m-tablelog m-slide-pane">
            <h1 className="box-title">
                {tableName}
            </h1>
            <div className="m-card">
                <Card
                    title={title}
                    noHovering
                    bordered={false}
                    loading={false}
                >
                    <Table columns={columns}
                        className="m-table bd"
                        rowKey="id"
                        style={{ borderBottom: 0 }}
                        dataSource={logs.data}
                        pagination={false}
                    />
                    <Pagination
                        pageSize={10}
                        style={{ float: 'right', margin: '30px' }}
                        current={logs.pageIndex}
                        total={logs.totalCount}
                        onChange={this.showPage.bind(this)}
                    />
                </Card>
            </div>
        </div>
    }

    showPage (pageIndex, pageSize) {
        const form = this.getFormParams();
        const params = Object.assign(form, {
            pageIndex, pageSize
        });

        this.doSearch(params);
    }

    search () {
        const params = this.getFormParams();
        this.doSearch(params);
    }

    getFormParams () {
        const params = this.searchForm.getFieldsValue();
        if (params.range) {
            var [startTime, endTime] = params.range;
            startTime = startTime && startTime.format('X');
            endTime = endTime && endTime.format('X');
        }

        delete params.range;
        return { startTime, endTime, ...params };
    }

    doSearch (params) {
        ajax.searchLog(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    logs: res.data
                });
            }
        })
    }
}

class Log extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            tableList: {},
            tableLog: this.props.routeParams,
            visibleSlidePane: false,
            tableName: '',
            isDeleted: '',
            loading: false
        }
    }

    componentDidMount () {
        const tableName = utils.getParameterByName('tableName');
        this.searchTable({
            tableName: tableName || ''
        });
        this.props.getUsers()
    }

    searchTable (args) {
        let { isDeleted, tableName, tableList } = this.state
        tableList = [];
        this.setState({ loading: true, tableList })
        const params = Object.assign({
            timeSort: 'desc',
            pageSize: 20,
            tableName,
            isDeleted
        }, args);

        ajax.newSearchTable(params).then(res => {
            if (res.code === 1) {
                tableList = res.data;
            }
            this.setState({
                tableList,
                loading: false
            })
        });
    }

    onTableNameChange = (e) => {
        this.setState({
            tableName: e.target.value
        })
    }

    handleTableChange = (pagination, filters) => {
        if (filters.tableName) {
            const isDeleted = filters.tableName[0]
            this.setState({
                isDeleted
            }, this.searchTable)
        }
    }

    showTableListPage (page, pageSize) {
        const { isDeleted } = this.state;
        this.searchTable({
            pageIndex: page,
            isDeleted
        });
    }

    showTableLog (table) {
        const { id, tableName } = table;
        this.setState({
            tableLog: { tableId: id, tableName },
            visibleSlidePane: true
        })
    }

    onFilter = (value, record) => {
        this.searchTable({
            isDeleted: value
        })
    }

    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false,
            tableLog: {}
        })
    }

    render () {
        const the = this;
        const columns = [{
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
            width: 300,
            render (text, record) {
                return <a href="javascript:void(0)"
                    onClick={the.showTableLog.bind(the, record)}
                >
                    {text}{record.isDeleted === 1 ? '(已删除)' : ''}
                </a>
            },
            filters: [
                { text: '已删除', value: 1 },
                { text: '未删除', value: 0 }
            ],
            filterMultiple: false,
            filteredValue: (this.state.isDeleted || this.state.isDeleted === 0) ? [this.state.isDeleted] : undefined
        }, {
            title: '最近变更时间',
            dataIndex: 'lastDmlTime',
            key: 'lastDmlTime',
            render (text, record) {
                return moment(text).format('YYYY-MM-DD HH:mm:ss')
            }
        }];
        const { tableList, tableLog, visibleSlidePane, isDeleted, loading } = this.state;

        const { data, currentPage, totalCount } = tableList;
        const { projectUsers } = this.props;

        const title = (
            <Search style={{ width: 200, marginTop: 10 }}
                placeholder="按表名搜索"
                defaultValue={ utils.getParameterByName('tableName')}
                onChange={this.onTableNameChange}
                onSearch={value => { this.searchTable({ tableName: value, isDeleted }) }}
            />
        )
        return <div className="g-tablelogs">
            <h1 className="box-title">操作记录</h1>
            <div className="box-2 m-card" style={{ padding: '0 20px 20px 0' }}>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={title}
                >
                    <Spin spinning={loading}>
                        <Table columns={columns}
                            rowClassName={
                                (record, index) => {
                                    if (this.state.tableLog && this.state.tableLog.tableId == record.id) {
                                        return 'row-select'
                                    } else {
                                        return '';
                                    }
                                }
                            }
                            dataSource={data || []}
                            className="m-table"
                            rowKey="id"
                            pagination={false}
                            onChange={this.handleTableChange}
                            bordered
                        />
                        <Pagination
                            pageSize={20}
                            style={{ float: 'right', marginTop: '16px' }}
                            current={currentPage || 0}
                            total={totalCount || 0}
                            onChange={this.showTableListPage.bind(this)}
                        />
                    </Spin>
                    <SlidePane
                        onClose={this.closeSlidePane}
                        visible={visibleSlidePane}
                        style={{ right: '-20px', width: '80%', height: '100%', minHeight: '600px' }}
                    >
                        <div className="m-loglist">
                            {isEmpty(tableLog) ? <p style={{
                                fontSize: 36,
                                color: '#ddd',
                                textAlign: 'center',
                                marginTop: 40
                            }}><Icon type="exclamation-circle-o" /> 未选中任何数据表</p>
                                : <TableLog key={tableLog.tableId} {...tableLog}
                                    projectUsers={projectUsers}
                                />}
                        </div>
                    </SlidePane>
                </Card>
            </div>
        </div>
    }
}

const mapState = state => ({
    projectId: state.project.id,
    projectUsers: state.projectUsers
});

const mapDispatch = dispatch => ({
    getUsers () {
        dispatch(UserAction.getProjectUsers())
    }
});

export default connect(mapState, mapDispatch)(Log);
