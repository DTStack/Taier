import React from "react";
import { Card, Table, Modal, Select } from "antd";
import moment from "moment";

import Api from "../../api/console"
import ChangeResourceModal from "../../components/changeResource"

const PAGE_SIZE = 10;
const Option = Select.Option;

class ResourceManage extends React.Component {
    state = {
        resource:{},
        dataSource: [],
        table: {
            pageIndex: 1,
            total: 0,
            loading: true
        },
        changeModalVisible:false
    }
    componentDidMount() {
        this.getResourceList();
    }
    getResourceList() {
        const { table } = this.state;
        const { pageIndex } = table;
        Api.getResourceList({
            currentPage: pageIndex,
            pageSize: PAGE_SIZE
        })
            .then(
                (res) => {
                    if (res.code == 1) {
                        this.setState({
                            dataSource: res.data.data,
                            table: {
                                ...table,
                                loading: false,
                                total: res.data.totalCount
                            }
                        })
                    } else {
                        this.setState({
                            table: {
                                ...table,
                                loading: false
                            }
                        })
                    }
                }
            )
    }
    getPagination() {
        const { pageIndex, total } = this.state.table;
        return {
            current: pageIndex,
            pageSize: PAGE_SIZE,
            total: total
        }
    }
    initTableColumns() {
        return [
            {
                title: "集群名称",
                dataIndex: "clusterName"
            },
            {
                title: "资源队列",
                dataIndex: "queueName"
            },
            {
                title: "最小容量（%）",
                dataIndex: "capacity",
                width: "150px",
                render(text) {
                    return text * 100;
                }
            },
            {
                title: "最大容量（%）",
                dataIndex: "maxCapacity",
                width: "150px",
                render(text) {
                    return text * 100;
                }
            },
            {
                title: "绑定租户",
                dataIndex: "name"
            },
            {
                title: "修改时间",
                dataIndex: "gmtModified",
                width: "200px",
                render(text) {
                    return new moment(text).format("YYYY-MM-DD HH:mm:ss")
                }
            },
            {
                title: "操作",
                dataIndex: "deal",
                render:(text,record)=> {
                    return <a onClick={this.changeResource.bind(this,record)}>修改</a>
                }
            }
        ]
    }
    changeResource(resource){
        this.setState({
            resource:resource,
            changeModalVisible:true
        })
    }
    changeUserValue(value) {
        this.setState({
            selectUser: value
        })
    }
    selectUser(value) {
        const { selectUserList } = this.state;
        this.setState({
            selectUser: "",
            selectUserList: selectUserList.concat(value)
        })
    }
    closeModal(){
        this.setState({
            changeModalVisible:false
        })
    }
    getUserOptions() {
        const { userList, selectUserList } = this.state;
        const result = [];
        for (let i = 0; i < userList.length; i++) {
            const user = userList[i];
            if (selectUserList.indexOf(user.id) == -1) {
                result.push(<Option value={user.id}>{user.name}</Option>)
            }
        }
        return result;
    }
    render() {
        const { dataSource, table, changeModalVisible, resource } = this.state;
        const { loading } = table;
        const columns = this.initTableColumns();
        return (
            <div className="contentBox">
                <Card
                    style={{ width: "900px", color: "rgba(1,1,1,0.84)" }}
                    noHovering
                >
                    <h2>什么是资源管理</h2>
                    <p style={{ marginTop: "20px" }}>资源管理是以租户为单位进行计算资源的分配，当您需要多个租户，并且每个租户分配不同比例的资源容量时需要使用本功能，例如：您的集群有10个节点，每个节点的配置为8核16GB内存，总资源为80核160GB内存，那么您可以新建“销售”和“开发”2个租户，为销售租户分配30%的资源容量，为开发租户分配70%的资源容量。</p>
                    <p style={{ marginTop: "20px" }}>注意:</p>
                    <ul>
                        <li>1、“资源队列”仅包括集群的内存和CPU；</li>
                        <li>2、只支持将资源队列绑定到租户，暂时不支持绑定到项目；</li>
                        <li>3、每个资源队列的最大容量之和等于100%；</li>
                        <li>4、以资源队列的形式分配资源，资源队列的维护在配置文件中，本模块只是将资源队列绑定到租户；</li>
                        <li>5、可能已有的任务占用了较多的资源，导致更新配置后不会立即生效，需要等待已占用的资源释放；</li>
                    </ul>
                </Card>
                <Table
                    rowKey={(record) => {
                        return record.clusterId + "~" + record.queueId
                    }}
                    style={{ marginTop: "20px" }}
                    className="m-table no-card-table"
                    pagination={this.getPagination()}
                    loading={loading}
                    dataSource={dataSource}
                    columns={columns}
                />
                <ChangeResourceModal
                    visible={changeModalVisible}
                    onCancel={this.closeModal.bind(this)}
                    resource={resource}
                 />
            </div>
        )
    }
}

export default ResourceManage;