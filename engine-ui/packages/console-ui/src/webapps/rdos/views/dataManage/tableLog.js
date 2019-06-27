import React from 'react';
import { connect } from 'react-redux';
import {
    Input, Button, Table,
    Form, DatePicker, Select, Card
} from 'antd';
import moment from 'moment';

import ajax from '../../api/dataManage';
import * as UserAction from '../../store/modules/user'

const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;
const Option = Select.Option;
const PAGE_SIZE = 10;

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
            isDeleted: 1,
            pagination: {
                pageSize: 10,
                pageIndex: 1
            },
            loading: false,
            total: 0
        };
    }

    componentDidMount () {
        const { belongProjectId } = this.props.editRecord
        this.search();
        this.props.getUsers(belongProjectId)
    }
    handleTableChange = (pagination, filters, sorter) => {
        const page = Object.assign(this.state.pagination, { pageIndex: pagination.current })
        const form = this.getFormParams();
        this.setState({
            pagination: page
        }, () => {
            this.doSearch(Object.assign(form, page))
        })
    }
    render () {
        const { tableId, tableName, projectUsers } = this.props;
        const { total, loading } = this.state;
        const { pageIndex } = this.state.pagination;
        const pagination = {
            current: pageIndex,
            pageSize: PAGE_SIZE,
            total: total
        }
        const { logs } = this.state;
        const columns = [{
            title: '变更时间',
            width: 200,
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            render (text, record) {
                return moment(text).format('YYYY-MM-DD HH:mm')
            }
        }, {
            title: '操作人',
            width: 200,
            dataIndex: 'userId',
            key: 'userId',
            render (text, record) {
                return <span>{record.userName}</span>
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
                        loading={loading}
                        style={{ borderBottom: 0 }}
                        dataSource={logs.data}
                        onChange={this.handleTableChange}
                        pagination={pagination}
                    />
                </Card>
            </div>
        </div>
    }
    search () {
        const params = this.getFormParams();
        this.doSearch(params);
    }

    getFormParams () {
        const params = this.searchForm.getFieldsValue();
        if (params.range) {
            var [startTime, endTime] = params.range;
            startTime = startTime && startTime.set({
                'hour': 0,
                'minute': 0,
                'second': 0
            }).format('X');
            endTime = endTime && endTime.set({
                'hour': 23,
                'minute': 59,
                'second': 59
            }).format('X');
        }

        delete params.range;
        return { startTime, endTime, ...params };
    }

    doSearch (params) {
        const reqParams = Object.assign(params, this.state.pagination)
        this.setState({ loading: true })
        ajax.searchLog(reqParams).then(res => {
            if (res.code === 1) {
                this.setState({
                    logs: res.data || [],
                    total: res.data.totalCount,
                    loading: false
                });
            } else {
                this.setState({ loading: false })
            }
        })
    }
}

const mapState = state => ({
    projectUsers: state.projectUsers
});

const mapDispatch = dispatch => ({
    getUsers (projectId) {
        dispatch(UserAction.getProjectUsers(projectId))
    }
});

export default connect(mapState, mapDispatch)(TableLog);
