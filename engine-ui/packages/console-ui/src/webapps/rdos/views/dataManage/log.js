import React from 'react';
import { connect } from 'react-redux';
import SplitPane from 'react-split-pane';
import { Input, Button, message, Modal, Table, Pagination,
    Form, DatePicker, Select, Icon } from 'antd';
import { isEmpty } from 'lodash';
import  moment from 'moment';

import ajax from '../../api';
import actions from '../../store/modules/dataManage/actionCreator';


const Search = Input.Search;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;
const Option = Select.Option;

class LogSearchForm extends React.Component {
    render() {
        const { getFieldDecorator } = this.props.form;
        const { projectUsers } = this.props;

        return (
            <Form layout="inline">
                <FormItem style={{ marginBottom: 24 }} label="变更时间">
                    {getFieldDecorator('range', {
                        initialValue: [moment().subtract(7, 'days'), moment()]
                    })(
                        <RangePicker style={{width: 200}} format="YYYY-MM-DD" />
                    )}
                </FormItem>
                <FormItem style={{ marginBottom: 24 }} label="操作人">
                    {getFieldDecorator('actionUserId')(
                        <Select placeholder="请选择操作人" style={{width: 200}}>
                            {projectUsers.map(o => <Option value={ `${o.userId}` }
                                key={ o.userId }
                            >
                                {o.userName}
                            </Option>)}
                        </Select>
                    )}
                </FormItem>
                <FormItem label="变更语句">
                    {getFieldDecorator('sql')(
                        <Input placeholder="变更语句" style={{width: 200}}></Input>
                    )}
                </FormItem>
                <FormItem>
                    <Button type="primary" onClick={ this.props.search } > 搜索 </Button>
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
    constructor(props) {
        super(props);
        this.state = {
            logs: {},
            isDeleted: 1,
        };
    }

    componentDidMount() {
        this.search();
    }

    render() {
        const { tableId, tableName, projectUsers } = this.props;
        const { logs } = this.state;
        const columns = [{
            title: '变更时间',
            dataIndex: 'gmtCreate',
            width: '200px',
            key: 'gmtCreate',
            render(text, record) {
                return moment(text).format('YYYY-MM-DD HH:mm:ss')
            }
        }, {
            title: '操作人',
            dataIndex: 'userId',
            width: '200px',
            key: 'userId',
            render(text, record) {
                let userName;

                projectUsers.forEach(function(o) {
                    if(o.userId == text) userName = o.userName;
                }, this);

                return <span>{ userName }</span>
            }
        }, {
            title: '操作语句',
            dataIndex: 'actionSql',
            key: 'actionSql',
            render(text) {
                return <code style={{ wordBreak: 'normal' }}>{text}</code>
            }
        }];

        return <div className="m-tablelog">
            <h3 className="m-logheader"><b>{ tableName }</b></h3>
            <div className="box" style={{ marginTop: 50, padding: 20 }}>
                <FormWrapper tableId={tableId}
                    ref={ el => this.searchForm = el }
                    search={ this.search.bind(this) }
                    projectUsers={ projectUsers }
                />
                <Table columns={ columns }
                    dataSource={ logs.data }
                    style={{ margin: '20 0' }}
                    pagination={ false }
                />
                <Pagination
                    pageSize={ 10 }
                    style={{ float: 'right' }}
                    current={ logs.currentPage }
                    total={ logs.totalCount }
                    onChange={ this.showPage.bind(this) }
                />
            </div>
        </div>
    }

    showPage(pageIndex, pageSize) {
        const form = this.getFormParams();
        const params = Object.assign(form, {
            pageIndex, pageSize
        });

        this.doSearch(params);
    }

    search() {
        const params = this.getFormParams();
        this.doSearch(params);
    }

    getFormParams() {
        const params = this.searchForm.getFieldsValue();
        if(params.range) {
            var [startTime, endTime] = params.range;
            startTime = startTime.format('X');
            endTime = endTime.format('X');
        }

        delete params.range;
        return {startTime, endTime, ...params};
    }

    doSearch(params) {
        ajax.searchLog(params).then(res => {
            if(res.code === 1) {
                this.setState({
                    logs: res.data
                });
            }
        })
    }
}

class Log extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            tableList: {},
            tableLog: this.props.routeParams
        }
    }

    componentDidMount() {
        this.searchTable();
        this.props.getUsers({
            projectId: this.props.projectId,
            currentPage: 1,
            pageSize: 1000
        })
    }

    searchTable(args) {
        const params = Object.assign({
            timeSort: 'desc',
            tableName: '',
        }, args);

        ajax.searchTable(params).then(res => {
            if(res.code === 1) {
                this.setState({
                    tableList: res.data
                })
            }
        });
    }

    handleTableChange = (pagination, filters) => {
        if (filters.tableName) {
            const isDeleted = filters.tableName[0]
            this.searchTable({
                isDeleted,
            })
            this.setState({
                isDeleted,
            })
        }
    }

    showTableListPage(page, pageSize) {
        const { isDeleted } = this.state;
        this.searchTable({
            pageIndex: page,
            isDeleted,
        });
    }

    showTableLog(table) {
        const { tableId, tableName } = table;
        this.setState({
            tableLog: { tableId, tableName }
        })
    }

    onFilter = (value, record) => {
        this.searchTable({
            isDeleted: value,
        })
    }

    render() {
        const the = this;
        const columns = [{
            title: '表名',
            dataIndex: 'tableName',
            key: 'tableName',
            render(text, record) {
                return <a href="javascript:void(0)"
                    onClick={ the.showTableLog.bind(the, record) }
                >
                    {text}{record.isDeleted === 1 ? '(已删除)' : ''}
                </a>
            },
            filters: [
                { text: '已删除', value: 1 },
                { text: '未删除', value: 0 },
            ],
            filterMultiple: false,
        }, {
            title: '最近变更时间',
            dataIndex: 'lastDataChangeTime',
            key: 'lastDataChangeTime',
            render(text, record) {
                return moment(text).format('YYYY-MM-DD HH:mm:ss')
            }
        }];
        const { tableList, tableLog } = this.state;
        const { data, currentPage, pageSize, totalPage, totalCount } = tableList;
        const { projectUsers } = this.props;

        return <div className="g-tablelogs">
            <SplitPane split="vertical" minSize={200} defaultSize={300}>
                <div className="m-tablelist">
                    <Search style={{ width: '100%' }}
                        placeholder="按表名搜索"
                        onSearch={ value => { this.searchTable({tableName: value}) } }
                    />
                    {data && <Table columns={ columns }
                        dataSource={ data }
                        style={{ margin: '10 0 10 0' }}
                        pagination={ false }
                        onChange={this.handleTableChange}
                        bordered
                    />}
                    <Pagination
                        pageSize={ 10 }
                        style={{ float: 'right' }}
                        current={ currentPage || 0 }
                        total={ totalCount || 0 }
                        onChange={ this.showTableListPage.bind(this) }
                    />
                </div>
                <div className="m-loglist">
                    {isEmpty(tableLog) ? <p style={{
                        fontSize: 36,
                        color: '#ddd',
                        textAlign: 'center',
                        marginTop: 40
                    }}><Icon type="exclamation-circle-o" /> 未选中任何数据表</p>:
                    <TableLog key={ tableLog.tableId } {...tableLog}
                        projectUsers={ projectUsers }
                    />}
                </div>
            </SplitPane>
        </div>
    }
}

const mapState = state => ({
    projectId: state.project.id,
    projectUsers: state.dataManage.log.projectUsers
});

const mapDispatch = dispatch => ({
    getUsers(params) {
        dispatch(actions.getUsers(params))
    }
});

export default connect(mapState, mapDispatch)(Log);