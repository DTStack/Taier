import React, {Component} from 'react';
import {Table} from 'antd'

export default class PaneData extends Component{

  constructor(props){
    super(props);
    this.state = {
      paginationParams:{
        current: 1,
        total: 0,
        pageSize: 3
      },
      dataList: [],
      tableCol: [],
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
    this.state.dataList = [];
    this.state.tableCol = [];
    let { dataList, paginationParams, tableCol } = this.state;

    props.previewList[0].map(o=>{
      tableCol.push({
        title: o,
        dataIndex: o
      })
    })
    
    props.previewList.shift();
    for(let item in props.previewList){
      console.log(props.previewList[item])
      let j = {};
      let row = [];
      props.previewList[item].map((o,i)=>{
        let key = tableCol[i].dataIndex;
        console.log(key)
        j[key] = o;
        console.log(j)
        row.push(j)
      })
      dataList.push(row.pop())
    }

console.log(tableCol)
console.log(dataList)
    paginationParams.current = 1;
    paginationParams.total = props.previewList.length;

    // dataList = props.previewList.slice(0,paginationParams.pageSize);
    this.setState({
      dataList: dataList,
      paginationParams: paginationParams,
      tableCol: tableCol
    })
  }


  handleTableChange = (pagination,sorter,filter)=>{
    let {paginationParams, dataList, tableCol} = this.state;
    let data = this.props.previewList;
    console.log(data)

    paginationParams.current = pagination.current;
    console.log((paginationParams.current-1)*paginationParams.pageSize,paginationParams.current * paginationParams.pageSize)
    data = data.slice((paginationParams.current-1)*paginationParams.pageSize,paginationParams.current * paginationParams.pageSize);

    for(let item in data){
      console.log(data[item])
      let j = {};
      let row = [];
      data[item].map((o,i)=>{
        let key = tableCol[i].dataIndex;
        console.log(key)
        j[key] = o;
        console.log(j)
        row.push(j)
      })
      dataList.push(row.pop())
    }
    
console.log(dataList)
    this.setState({
      dataList: dataList,
      paginationParams: paginationParams
    })
  }

  render(){
    // const {previewList} = this.props;
    const {paginationParams, dataList, tableCol} = this.state;
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