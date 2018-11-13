import React, {Component} from 'react';
import { Tabs, Table, Radio, Checkbox } from 'antd'

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
    console.log(this.state.paginationParams)
    this.initData(this.props);
  }

  componentWillReceiveProps(nextProps){
    console.log(this.state.paginationParams)
    this.initData(nextProps);
  }

  initData = (props)=>{
    console.log(props)
    let {paginationParams} = this.state;
    this.state.columnData = props.data.columnData || [];
    this.state.partData = props.data.partData || [];

    let data = this.state.dataType === 'column'?this.state.columnData:this.state.partData;
    console.log(data)
    if(data && data.length===0){
      this.state.paginationParams.total = 0;
      this.setState({
        dataList: [],
        paginationParams: this.state.paginationParams
      })
      return;
    }

    this.state.paginationParams.total = data.length || 0;
    console.log(this.state.paginationParams)
    console.log((this.state.paginationParams.current-1) * this.state.paginationParams.pageSize)
    console.log(data)
    console.log(data.slice(10,10))
    // this.state.paginationParams.current = 1;

    this.state.dataList = data.slice((this.state.paginationParams.current-1) * this.state.paginationParams.pageSize,paginationParams.current * paginationParams.pageSize)
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
    const tableCOl = [{
        title: '字段名称',
        dataIndex: 'name',
      },{
        title: '倒排索引',
        dataIndex: 'invert',
        render: (text,record)=>(
          text === 0?'-':
          <Checkbox disabled={true} defaultChecked={text===1?true:false} onChange={(e)=>this.handleInvert(e,record)}></Checkbox>
        )
      },{
        title: '字典编码',
        dataIndex: 'dictionary',
        render: (text,record)=>(
          text === 0?'-':
          <Checkbox disabled={true} defaultChecked={text===1?true:false} onChange={(e)=>this.handleDictionary(e,record)}></Checkbox>
        )
      },{
        title: '多维索引',
        dataIndex: 'sortColumn',
        render: (text,record)=>(
          text === 0?'-':
          <Checkbox disabled={true} defaultChecked={text===1?true:false} onChange={(e)=>this.handleSortColumn(e,record)}></Checkbox>
        )
      },{
        title: '类型',
        dataIndex: 'type',
      },{
        title: '注释',
        dataIndex: 'comment',
        render: (text,record)=>(
          text?text:'-'
        )
      }
    ]
    return(
      <div className="pane-field-container">
        <div className="func-box" style={{marginBottom: 10}}>
          <RadioGroup  onChange={this.changeData} defaultValue="column">
            <RadioButton value="column">非分区字段</RadioButton>
            <RadioButton value="partition">分区字段</RadioButton>
          </RadioGroup>

          <span style={{color: 'rgb(204, 204, 204)'}}>共{this.state.paginationParams.total}个字段</span>
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