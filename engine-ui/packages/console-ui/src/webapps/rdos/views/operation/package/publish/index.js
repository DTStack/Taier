import React from "react";
import { Card, Table, Form, Select, DatePicker, Input, message, Popconfirm, Badge } from "antd";
import moment from "moment";
import { connect } from "react-redux";
import utils from "utils";

import Api from "../../../../api"
import PublishModal from "./publishModal";
import { publishStatus } from "../../../../comm/const";

const { RangePicker } = DatePicker;
const FormItem = Form.Item;
const Option = Select.Option;
const Search = Input.Search;

@connect(state => {
    return {
        project: state.project
    }
})
class PackagePublish extends React.Component {
    state = {
        users: [],
        publishVisible: false,
        publishModalData: {},
        packageList: [],
        packageName: null,
        publishUserId: null,
        applyUserId: null,
        status: null,
        publishTime: [moment().subtract(30, 'days'), new moment()],
        applyTime: [moment().subtract(30, 'days'), new moment()],
        tableParams: {
            filter: {},
            sorter: {},
            pagination: {
                current: 1,
                pageSize: 20,
                total: 0
            }
        }
    }
    componentDidMount() {
        this.getPackageList();
        this.getUsers();
    }
    getUsers() {
        const { project } = this.props;

        Api.getProjectUsers({
            projectId: project.id,
            currentPage: 1,
            pageSize: 99999
        }).then((res) => {
            if (res.code === 1) {
                this.setState({ users: res.data.data })
            }
        })
    }
    getPackageList() {
        const {
            tableParams,
            packageName, publishUserId, applyUserId,
            status, publishTime, applyTime
        } = this.state;
        const sorter = tableParams.sorter;
        let sort;
        let orderBy;
        if (sorter && sorter.columnKey) {
            sort = (sorter.order === 'descend') ? 'desc' : 'asc';
            orderBy = sorter.columnKey
        }
        Api.getPackageList({
            publishUserId,
            applyUserId,
            status,
            publishTimeStart: (publishTime && publishTime[0]) ? publishTime[0].valueOf() : null,
            publishTimeEnd: (publishTime && publishTime[1]) ? publishTime[1].valueOf() : null,
            applyTimeStart: (applyTime && applyTime[0]) ? applyTime[0].valueOf() : null,
            applyTimeEnd: (applyTime && applyTime[1]) ? applyTime[1].valueOf() : null,
            packageName,
            pageSize: tableParams.pagination.pageSize,
            pageIndex: tableParams.pagination.current,
            sort,
            orderBy
        })
            .then(
                (res) => {
                    if (res.code == 1) {
                        this.setState({
                            packageList: res.data.data,
                            tableParams: {
                                ...tableParams,
                                pagination: {
                                    ...tableParams.pagination,
                                    total: res.data.totalCount
                                }
                            }
                        })
                    }
                }
            )
    }
    initColumns() {
        return [{
            title: "发布包",
            dataIndex: "name"
        }, {
            title: "申请人",
            dataIndex: "applyUser"
        }, {
            title: "申请时间",
            dataIndex: "gmt_create",
            sorter: true,
            render(n, record) {
                return utils.formatDateTime(record.gmtCreate)
            }
        }, {
            title: "发布人",
            dataIndex: "publishUser"
        }, {
            title: "发布时间",
            dataIndex: "gmt_modified",
            sorter: true,
            render(n, record) {
                return record.gmtModified && utils.formatDateTime(record.gmtModified)
            }
        }, {
            title: "发布描述",
            dataIndex: "comment",
            width: "230px"
        }, {
            title: "发布状态",
            dataIndex: "status",
            render(status) {
                switch (status) {
                    case publishStatus.UNSUBMIT: {
                        return (<span>
                            <Badge status="warning" text="待发布" />
                        </span>)
                    }
                    case publishStatus.FAIL: {
                        return (<span>
                            <Badge status="error" text="发布失败" />
                        </span>)
                    }
                    case publishStatus.SUCCESS: {
                        return (<span>
                            <Badge status="success" text="发布成功" />
                        </span>)
                    }
                }
            }
        }, {
            title: "操作",
            dataIndex: "deal",
            render: (n, record) => {
                const status = record.status;
                switch (status) {
                    case publishStatus.UNSUBMIT:
                    case publishStatus.FAIL: {
                        return <span>
                            <a onClick={this.viewPackage.bind(this, record)}>查看</a>
                            <span className="ant-divider"></span>
                            <Popconfirm title="确定删除该发布包吗?" onConfirm={this.deletePackage.bind(this, record.id)} okText="确定" cancelText="取消">
                                <a>删除</a>
                            </Popconfirm>
                            <span className="ant-divider"></span>
                            <Popconfirm title="确定发布吗?" onConfirm={this.publishPackage.bind(this, record.id)} okText="确定" cancelText="取消">
                                <a>发布</a>
                            </Popconfirm>
                        </span>
                    }
                    case publishStatus.SUCCESS: {
                        return <span>
                            <a onClick={this.viewPackage.bind(this, record)}>查看</a>
                        </span>
                    }
                }
            },
            width: "180px"
        }]
    }
    viewPackage(record) {
        this.setState({
            publishModalData: record,
            publishVisible: true
        })
    }
    deletePackage(id) {
        const { mode } = this.props;
        Api.deletePackage({
            packageId: id
        }, mode).then(
            (res) => {
                if (res.code == 1) {
                    message.success("删除成功")
                }
                this.getPackageList();
            }
        )
    }
    publishPackage(id) {
        const { mode } = this.props;
        Api.publishPackage({
            packageId: id
        }, mode)
            .then(
                (res) => {
                    this.getPackageList();
                    if (res.code == 1) {
                        message.success("发布成功")
                    }
                }
            )
    }
    selectChange(key, value) {
        this.setState({
            [key]: value
        }, this.getPackageList)
    }
    onTableChange(pagination, filters, sorter) {
        this.setState({
            tableParams: {
                pagination,
                filters,
                sorter
            }
        }, this.getPackageList)
    }
    disabledDate(currentDate) {
        const now = new moment;
        if (currentDate > now) {
            return true
        }
        return false;
    }
    dateChange(key, dates) {
        this.setState({
            [key]: dates
        }, this.getPackageList)
    }
    getTableTitle = () => {
        const { users } = this.state;
        const bussinessDate = [moment().subtract(30, 'days'), new moment()]
        return (
            <Form
                style={{ marginTop: "10px" }}
                layout="inline"
            >
                <FormItem
                    label=""
                >
                    <Search
                        size="default"
                        placeholder="输入发布包名称"
                        style={{ width: 120 }}
                        onSearch={this.selectChange.bind(this, 'packageName')}
                    />
                </FormItem>
                <FormItem
                    label="发布人"
                >
                    <Select allowClear size="default" onChange={this.selectChange.bind(this, 'publishUserId')} style={{ width: 110 }}>
                        {users.map(
                            (user) => {
                                return <Option key={user.userId} value={user.userId}>{user.user.userName}</Option>
                            }
                        )}
                    </Select>
                </FormItem>
                <FormItem
                    label="申请人"
                >
                    <Select allowClear size="default" onChange={this.selectChange.bind(this, 'applyUserId')} style={{ width: 110 }}>
                        {users.map(
                            (user) => {
                                return <Option key={user.userId} value={user.userId}>{user.user.userName}</Option>
                            }
                        )}
                    </Select>
                </FormItem>
                <FormItem
                    label="发布状态"
                >
                    <Select allowClear size="default" onChange={this.selectChange.bind(this, 'status')} style={{ width: 110 }}>
                        <Option value={publishStatus.UNSUBMIT}>待发布</Option>
                        <Option value={publishStatus.SUCCESS}>已发布</Option>
                        <Option value={publishStatus.FAIL}>发布失败</Option>
                    </Select>
                </FormItem>
                <FormItem
                    label="发布日期"
                >
                    <RangePicker
                        onChange={this.dateChange.bind(this, 'publishTime')}
                        disabledDate={this.disabledDate}
                        size="default"
                        style={{ width: 170 }}
                        defaultValue={bussinessDate || null}
                    />
                </FormItem>
                <FormItem
                    label="申请日期"
                >
                    <RangePicker
                        onChange={this.dateChange.bind(this, 'applyTime')}
                        disabledDate={this.disabledDate}
                        size="default"
                        style={{ width: 170 }}
                        defaultValue={bussinessDate || null}
                    />
                </FormItem>
            </Form>
        )
    }
    render() {
        const { packageList, tableParams, publishVisible, publishModalData } = this.state;
        const { mode } = this.props;
        return (
            <div className="m-card">
                <PublishModal
                    data={publishModalData}
                    isPublish={true}
                    mode={mode}
                    onCancel={() => { this.setState({ publishVisible: false }) }}
                    onOk={() => { this.setState({ publishVisible: false }); this.getPackageList() }}
                    visible={publishVisible}
                />
                <Card
                    noHovering
                    bordered={false}
                    title={this.getTableTitle()}
                >
                    <Table
                        className="m-table"
                        columns={this.initColumns()}
                        pagination={tableParams.pagination}
                        dataSource={packageList}
                        onChange={this.onTableChange.bind(this)}
                    />
                </Card>
            </div>
        )
    }
}

export default PackagePublish;