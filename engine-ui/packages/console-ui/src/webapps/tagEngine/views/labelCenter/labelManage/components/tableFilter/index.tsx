import * as React from "react";
import { Table, message as Message, Select } from "antd";
import { Link } from "react-router";
import "./style.scss";


interface IProps {
    history?: any;
}
interface IState {
    loading: boolean;
    pageNo: number;
    pageSize: number;
    total: number;
    dataSource: any;
    visible: boolean;

}
const Option = Select.Option;
export default class TableFilter extends React.PureComponent<
    IProps,
    IState
    > {
    state: IState = {
        loading: false,
        pageNo: 1,
        pageSize: 10,
        total: 100,
        visible: false,
        dataSource: []
    };
    componentDidMount () {
        this.loadMainData();
    }
    loadMainData () {
        // const {pageNo,pageSize} = this.state;
        this.setState({
            dataSource: [{
                id: 1,
                tagName: '年龄',
                status: '正常运行',
                num: 100
            },
            {
                id: 2,
                tagName: '年龄',
                status: '正常运行',
                num: 100
            }]
        })

    }
    handleChange = () => {
        this.setState({
            pageNo: 1,
            pageSize: 10,
            loading: true
        })
        this.loadMainData();
    }
    onTableChange = (pagination: any, filters: any, sorter: any) => {
        const { current, pageSize } = pagination;
        this.setState(
            {
                pageNo: current,
                pageSize: pageSize,
                loading: true
            },
            () => {
                this.loadMainData();
            }
        );
    };
    render () {
        const { pageNo, pageSize, loading, total, dataSource } = this.state;
        const columns = [
            {
                title: "标签名",
                dataIndex: "tagName",
                key: "tagName",
                render: (text: any) => {
                    return <Link to='#'>{text}</Link>
                }
            },
            {
                title: "状态",
                dataIndex: "status",
                key: "status"
            },
            {
                title: "样本数量",
                dataIndex: "num",
                key: "num",
                sorter:true
            }
        ];
        return (
            <div className="table-filter">
                <div className="title">已选择200个标签</div>
                <div className="table_filter_content">
                    <div className="select_wrap">
                        <div className="select_item">
                            <span className="label">标签状态：</span>
                            <Select defaultValue="全部" style={{ width: 120 }} onChange={this.handleChange}>
                                <Option value="jack">全部</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        </div>
                        <div className="select_item">
                            <span className="label">标签类型：</span>
                            <Select defaultValue="全部" style={{ width: 120 }} onChange={this.handleChange}>
                                <Option value="jack">全部</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        </div>
                    </div>
                    <Table
                        dataSource={dataSource}
                        columns={columns}
                        loading={loading}
                        rowKey="id"
                        onChange={this.onTableChange}
                        pagination={{
                            current: pageNo,
                            pageSize: pageSize,
                            total: total,
                            showSizeChanger: true,
                            showQuickJumper: true,
                            showTotal: () => (
                                <div>
                                    总共 <a>{total}</a> 条数据,每页显示{pageSize}条
                </div>
                            )
                        }}
                    />
                </div>
            </div>
        );
    }
}
