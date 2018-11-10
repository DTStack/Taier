import React, {Component} from 'react';
import {Table,notification} from 'antd'

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
      previewList: [],
    }
  }
  componentDidMount(){
    // this.initData(this.props)
    // this.getData();
    this.state.tableCol = [];
    this.state.dataList = [];

    this.processData(this.props.data);
  }

  componentWillReceiveProps(nextProps){
    this.state.tableCol = [];
    this.state.dataList = [];
    this.processData(nextProps.data);
    // this.initData(nextProps)
  }


  // getData = ()=>{
  //   API.getPreviewData({
  //     tableId: this.props.tableDateil.id,
  //     databaseId: this.props.tableDateil.databaseId,
  //   }).then(res=>{
  //     if(res.code === 1){
  //       this.state.previewList = res.data;
  //       this.processData(res.data);
  //     }else{
  //       notification.error({
  //         title: '提示',
  //         description: res.message
  //       })
  //     }
  //   })
  // }

  processData = (list)=>{
    if(list.length===0) return;
    let { dataList, paginationParams, tableCol } = this.state;

    
    list[0].map(o=>{
      tableCol.push({
        title: o,
        dataIndex: o
      })
    })

    list.shift();
    for(let item in list){
      let j = {};
      let row = [];
      list[item].map((o,i)=>{
        let key = tableCol[i].dataIndex;
        j[key] = o && o.toString();
        row.push(j)
      })
      dataList.push(row.pop())
    }


    paginationParams.current = 1;
    paginationParams.total = list.length;


    this.setState({
      tableCol: tableCol,
      dataList: dataList,
      paginationParams: paginationParams
    },()=>{
      console.log(this.state.dataList)
      console.log(this.state.tableCol)
    })
  }


//   initData = (props) => {
//     console.log(props)
//     this.state.dataList = [];
//     this.state.tableCol = [];
//     let { dataList, paginationParams, tableCol } = this.state;
//     if(!props.previewList)
//       return;

//     props.previewList[0].map(o=>{
//       tableCol.push({
//         title: o,
//         dataIndex: o
//       })
//     })

//     props.previewList.shift();
//     for(let item in props.previewList){
//       console.log(props.previewList[item])
//       let j = {};
//       let row = [];
//       props.previewList[item].map((o,i)=>{
//         let key = tableCol[i].dataIndex;
//         console.log(key)
//         j[key] = o;
//         console.log(j)
//         row.push(j)
//       })
//       dataList.push(row.pop())
//     }

// console.log(tableCol)
// console.log(dataList)
//     paginationParams.current = 1;
//     paginationParams.total = props.previewList.length;

//     // dataList = props.previewList.slice(0,paginationParams.pageSize);
//     this.setState({
//       dataList: dataList,
//       paginationParams: paginationParams,
//       tableCol: tableCol
//     })
//   }


  handleTableChange = (pagination,sorter,filter)=>{
    let {paginationParams, previewList, dataList, tableCol} = this.state;
    let data = previewList;
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
        scroll={{x: 1500}}
        dataSource={dataList}
        rowKey="partId"
        pagination={paginationParams}
        onChange={this.handleTableChange}></Table>
      </div>
    )
  }
}