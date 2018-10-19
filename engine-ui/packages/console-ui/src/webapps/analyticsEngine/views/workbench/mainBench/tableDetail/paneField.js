import React, {Component} from 'react';
import { Tabs, Table, Radio } from 'antd'

const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

export default class PaneField extends Component{

  constructor(props){
    super(props);
    this.state = {
      paginationParams:{
        current: 1,
        total: 0,
        pageSize: 10,
      },
      dataList: [],
      columnData: [],
      partData: [],
      dataType: 'column',
    }
  }
  changeData = (e)=>{
    this.setState({
      dataType:e.target.value
    },()=>this.initData(this.props))
  }
  componentDidMount(){
    this.initData(this.props);
  }

  componentWillReceiveProps(nextProps){
    this.initData(nextProps);
  }

  initData = (props)=>{
    this.state.columnData = props.data.columnData;
    this.state.partData = props.data.partData;

    let data = this.state.dataType === 'column'?this.state.columnData:this.state.partData;
    console.log(data)
    if(data.length===0){
      this.paginationParams.total = 0;
      this.setState({
        dataList: [],
        paginationParams: paginationParams
      })
      return;
    }

    this.state.paginationParams.total = data.length;
    this.state.paginationParams.current = 1;

    this.state.dataList = data.slice(0,this.state.paginationParams.pageSize)
    this.setState({
      dataList: this.state.dataList,
      paginationParams: this.state.paginationParams
    })
  }

  handleTableChange = (pagination,filters,sorter)=>{
    console.log(pagination)
    let {dataList, columnData, partData, paginationParams} = this.state
    let data = this.state.dataType === 'column'?columnData:partData

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
    const {paginationParams, dataList} = this.state;
    const tableCOl = [
      {
        title: '序号',
        dataIndex: 'id',
      },{
        title: '字段名称',
        dataIndex: 'columnName',
      },{
        title: '类型',
        dataIndex: 'columnType',
      },{
        title: '注释',
        dataIndex: 'comment'
      }
    ]
    return(
      <div className="pane-field-container">
        <div className="func-box">
          <RadioGroup style={{margin: '10px 0'}} onChange={this.changeData} defaultValue="column">
            <RadioButton value="column">非分区字段</RadioButton>
            <RadioButton value="partition">分区字段</RadioButton>
          </RadioGroup>

          <span style={{color: 'rgb(204, 204, 204)'}}>共{dataList.length}个字段</span>
        </div>
        <Table
        columns={tableCOl}
        size="small"
        dataSource={dataList}
        rowKey="id"
        pagination={this.state.paginationParams}
        onChange={this.handleTableChange}>
        </Table>
      </div>
    )
  }
}