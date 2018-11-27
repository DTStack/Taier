import React, { Component } from 'react';
import { Table } from 'antd'

export default class PaneIndex extends Component {
    constructor (props) {
        super(props);
        this.state = {
            paginationParams: {
                current: 1,
                total: 0,
                pageSize: 10
            },
            dataList: []
        }
    }
    componentDidMount () {
        this.initData(this.props)
    }

    componentWillReceiveProps (nextProps) {
        this.initData(nextProps)
    }

  initData = (props) => {
      console.log(props)
      let { dataList, paginationParams } = this.state;
      paginationParams.current = 1;
      paginationParams.total = props.indexData.length;

      dataList = props.indexData.slice(0, paginationParams.pageSize);
      this.setState({
          dataList: dataList,
          paginationParams: paginationParams
      })
  }

  handleTableChange = (pagination, sorter, filter) => {
      let { paginationParams, dataList } = this.state;
      let data = this.props.indexData;

      paginationParams.current = pagination.current;
      console.log((paginationParams.current - 1) * paginationParams.pageSize, paginationParams.current * paginationParams.pageSize)
      dataList = data.slice((paginationParams.current - 1) * paginationParams.pageSize, paginationParams.current * paginationParams.pageSize);
      console.log(dataList)
      this.setState({
          dataList: dataList,
          paginationParams: paginationParams
      })
  }

  render () {
      // const {indexData} = this.props;
      const { paginationParams, dataList } = this.state;
      const tableCol = [
          {
              title: '索引名称',
              dataIndex: 'name'
          }, {
              title: '字段',
              dataIndex: 'lastDDLTime',
              render: (text, record) => {
                  let d = new Date(text);
                  return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()} ${d.getHours()}:${d.getMinutes()}:${d.getSeconds()}`
              }
          }, {
              title: '索引类型',
              dataIndex: 'storeSize'
          }, {
              title: '备注',
              dataIndex: 'comment'
          }
      ]
      return (
          <div className="partition-container">
              <Table
                  size="small"
                  columns={tableCol}
                  dataSource={dataList}
                  rowKey="partId"
                  pagination={paginationParams}
                  onChange={this.handleTableChange}></Table>
          </div>
      )
  }
}
