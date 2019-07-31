import * as React from 'react';
import { Table, notification } from 'antd'
import API from '../../../../api'
import moment from 'moment'

const partMode: any = ['标准', 'Hash', 'Range', 'List']
export default class PanePartition extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            paginationParams: {
                current: 1,
                total: 0,
                pageSize: 10
            },
            dataList: [],
            partitionParam: {},
            notStard: false
        }
    }
    componentDidMount () {
    // this.getData();
    // this.initData(this.props)

    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
    // this.initData(nextProps)
    // this.getData();
        if (nextProps.tableDetail.partitionType !== 0) {
            this.setState({
                notStard: true
            })
            this.setState({
                partitionParam: nextProps.dataList
            }, () => {
                console.log(this.state.partitionParam)
            })
        } else {
            this.setState({
                dataList: nextProps.dataList,
                total: nextProps.dataList.length
            })
        }
    }

    // initData = (props: any) => {
    //   console.log(props)
    //   let { dataList, paginationParams } = this.state;
    //   paginationParams.current = 1;
    //   paginationParams.total = props.partitions.length;

    //   dataList = props.partitions.slice(0,paginationParams.pageSize);
    //   this.setState({
    //     dataList: dataList,
    //     paginationParams: paginationParams
    //   })
    // }

    getData = () => {
        API.getTablePartiton({
            tableId: this.props.tableDetail.id,
            pageIndex: this.state.paginationParams.current,
            pageSize: this.state.paginationParams.pageSize
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    dataList: res.data.data
                })
            } else {
                notification.error(({
                    title: '提示',
                    description: res.message
                } as any))
            }
        })
    }

    handleTableChange = (pagination: any, sorter: any, filter: any) => {
        this.setState({
            paginationParams: {
                ...this.state.paginationParams,
                current: pagination.current
            }
        }, () => {
            this.getData();
        })
    }

    render () {
        // const {partitions} = this.props;
        const { paginationParams, dataList } = this.state;
        const notStardCol: any = [
            {
                title: '分区模式',
                dataIndex: 'type'
            }, {
                title: this.props.tableDetail.partitionType === 1 ? '分区数量：' : this.props.tableDetail.partitionType === 2 ? '范围：' : '分区名称:',
                dataIndex: 'value'
            }
        ]
        const tableCol: any = [
            {
                title: '分区名',
                dataIndex: 'name'
            }, {
                title: '更新时间',
                dataIndex: 'lastDDLTime',
                render: (text: any, record: any) => {
                    return moment(record.lastDDLTime * 1000).format('YYYY-MM-DD')
                }
            }, {
                title: '存储量',
                dataIndex: 'storeSize'
            }
        ]
        return (
            <div className="partition-container">
                {
                    (!this.state.notStard && <Table
                        size="small"
                        columns={tableCol}
                        dataSource={dataList}
                        rowKey="partId"
                        pagination={paginationParams}
                        onChange={this.handleTableChange}></Table>) ||
            <Table
                size="small"
                columns={notStardCol}
                dataSource={[
                    { type: partMode[this.props.tableDetail.partitionType],
                        value: this.state.partitionParam.partConfig }]}
                rowKey="type"></Table>
                }
            </div>
        )
    }
}
