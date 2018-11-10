import React, {Component} from 'react';
import { Tabs, Table, Radio, Checkbox } from 'antd'

const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

export default class PaneBucket extends Component{

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
      bucketNumber: 0
    }
  }
  componentDidMount(){
    console.log(this.state.paginationParams)
    this.initData(this.props.data);
  }

  componentWillReceiveProps(nextProps){
    console.log(this.state.paginationParams)
    this.initData(nextProps.data);
  }

  initData = (props)=>{
    this.setState({
      bucketNumber: props.bucketNumber,
      dataList: props.infos
    })
  }

  handleTableChange = (pagination,filters,sorter)=>{
    console.log(pagination)
    let {dataList, columnData, partData, paginationParams} = this.state
    let data = dataList

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
    const {paginationParams, dataList, bucketNumber} = this.state;
    const tableCOl = [{
        title: '字段名称',
        dataIndex: 'name',
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
        分桶数量：{bucketNumber}
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