import React, {Component} from 'react';
import {Table} from 'antd'

export default class PanePartition extends Component{

  constructor(props){
    super(props);
    this.state = {
      paginationParams:{
        current: 1,
        total: 0,
        pageSize: 10
      },
      dataList: [],
    }
  }
  componentDidMount(){
    this.initData(this.props)
  }

  componentWillReceiveProps(nextProps){
    this.initData(nextProps)
  }

  initData = (props) => {
    console.log(props)
    let { dataList, paginationParams } = this.state;
    paginationParams.current = 1;
    paginationParams.total = props.partitions.length;

    dataList = props.partitions.slice(0,paginationParams.pageSize);
    this.setState({
      dataList: dataList,
      paginationParams: paginationParams
    })
  }


  handleTableChange = (pagination,sorter,filter)=>{
    let {paginationParams, dataList} = this.state;
    let data = this.props.partitions;

    paginationParams.current = pagination.current;
    console.log((paginationParams.current-1)*paginationParams.pageSize,paginationParams.current * paginationParams.pageSize)
    dataList = data.slice((paginationParams.current-1)*paginationParams.pageSize,paginationParams.current * paginationParams.pageSize);
console.log(dataList)
    this.setState({
      dataList: dataList,
      paginationParams: paginationParams
    })
  }

  render(){
    // const {partitions} = this.props;
    const {paginationParams, dataList} = this.state;
    const tableCol = [
      {
        title: '分区名',
        dataIndex: 'name'
      },{
        title: '更新时间',
        dataIndex: 'lastDDLTime',
        render: (text,record)=>{
          let d = new Date(text);
          return `${d.getFullYear()}-${d.getMonth()+1}-${d.getDate()} ${d.getHours()}:${d.getMinutes()}:${d.getSeconds()}`
        }
      },{
        title: '存储量',
        dataIndex: 'storeSize',
      }
    ]
    return(
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