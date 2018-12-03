import React from 'react';
import { connect } from 'react-redux';
import {
    Input, Button, Table, Pagination,
    Form, DatePicker, Select, Card
} from 'antd';
import moment from 'moment';

import ajax from '../../api/dataManage';
import * as UserAction from '../../store/modules/user'

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
        this.props.getUsers()
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

const mapState = state => ({
    projectUsers: state.projectUsers
});

const mapDispatch = dispatch => ({
    getUsers () {
        dispatch(UserAction.getProjectUsers())
    }
});

export default connect(mapState, mapDispatch)(TableLog);
