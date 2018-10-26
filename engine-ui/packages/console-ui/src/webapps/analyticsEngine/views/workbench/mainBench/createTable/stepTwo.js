import React, { Component } from 'react';
import { Input, Table, Select, Icon, Button, Row, Checkbox } from 'antd'

const Option = Select.Option;

// const columns = [];
// const partitions = [];
const field_type = [
  {
    name: 'SMALLINT',
    value: 'SMALLINT',
  },{
    name: 'INT/INTEGER',
    value: 'INT/INTEGER',
  },{
    name: 'BIGINT',
    value: 'BIGINT',
  },{
    name: 'DOUBLE',
    value: 'DOUBLE',
  },{
    name: 'TIMESTAMP',
    value: 'TIMESTAMP',
  },{
    name: 'DATE',
    value: 'DATE',
  },{
    name: 'STRING',
    value: 'STRING',
  },{
    name: 'CHAR',
    value: 'CHAR',
  },{
    name: 'VARCHAR',
    value: 'VARCHAR',
  },{
    name: 'BOOLEAN',
    value: 'BOOLEAN',
  },
]

export default class StepTwo extends Component{
  constructor(props){
    super();
    this.state = {
      columns: [],
      partitions: [],
    }
  }

  componentDidMount(){
    const { columns=[], partitions=[] } = this.props.tabData.tableItem;
    this.setState({
      columns: columns ,
      partitions: partitions 
    })
    console.log(columns)
    console.log(partitions)
  }
  componentWillReceiveProps(nextProps){
    const { columns=[], partitions=[]  } = nextProps.tabData.tableItem;

    this.setState({
      columns: columns,
      partitions: partitions
    },()=>{
      console.log(this.state.columns)
      console.log(this.state.partitions)
    })
  }
  componentWillUpdate(){
    console.log(this.state.columns)
    console.log(this.state.partitions)
  }

  next = ()=>{
    this.props.handleNextStep();
  }

  addNewLine = (flag)=>{
    let {columns,partitions} = this.state;
    let _fid = 0;
    if(flag === 1){
      columns.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      columns[columns.length] = {
        _fid: _fid + 1,
        columnName: '',
        columnType: '',
        invert: 0,
        dictionary: 0,
        sortColumn: 0,
        comment: ''
      }
      this.setState({
        columns: columns
      })
    }else if(flag === 2){
      partitions.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      partitions[partitions.length] = {
        _fid: _fid + 1,
        columnName: '',
        columnType: '',
        comment: '',
      }
      this.setState({
        partitions: partitions
      })
    }
  }

  handleNameChange = (e,record)=>{
    let {columns,partitions} = this.state;
    record.columnName = e.target.value;
    console.log(columns)
    this.saveDataToStorage();
  }
  handleSelectChange = (e,record)=>{
    let {columns, partitions} = this.state;
    record.columnType = e;
    this.saveDataToStorage();
  }
  handleCommentChange = (e,record)=>{
    let {columns,partitions} = this.state;
    record.comment = e.target.value;
    this.saveDataToStorage();
  }

  remove = (record,flag)=>{
    let {columns,partitions} = this.state;

    flag === 1?columns.splice(columns.indexOf(record),1):partitions.splice(partitions.indexOf(record),1);

    this.setState({
      columns: columns,
      partitions: partitions
    })
    this.saveDataToStorage();
  }

  move = (record,flag,type)=>{
    //type 1上移 2下移
    // let mid = {};
    let {columns,partitions} = this.state;
    let list = flag === 1?columns:partitions;
    console.log(type)
    console.log( list.indexOf(record) )
    console.log(list.length)
    
    if((type === 1 && list.indexOf(record) === 0) || (type === 2 && list.indexOf(record) === list.length-1))
      return

    let x = list.indexOf(record), y = type === 1?list.indexOf(record)-1:list.indexOf(record)+1;

    let midId = list[y]._fid;
    let midItem = list[x]

    list[y]._fid = -1;
    
    list[x] = list[y]; // fid=-1
    list[y] = midItem

    list[x]._fid = midId

    console.log(list)

    if(flag === 1)
      this.setState({
        columns: list
      })
    else
      this.setState({
        partitions: list
      })

    
      this.saveDataToStorage();
  }
  /**
   * 保存输入的值
   */
  saveDataToStorage = ()=>{
    const {columns, partitions} = this.state;
    this.props.saveNewTableData([{
      key: 'columns',
      value: columns
    },{
      key: 'partitions',
      value: partitions
    }])
  }

  handleInvert = (e,record)=>{
    record.invert = e.target.checked?1:0
    this.saveDataToStorage();

  }

  handleDictionary = (e,record)=>{
    record.dictionary = e.target.checked?1:0
    this.saveDataToStorage();

  }

  handleSortColumn = (e,record)=>{
    record.sortColumn = e.target.checked?1:0
    this.saveDataToStorage();

  }

  getTableCol = (flag)=>{
    let col = [
      {
        title: '字段名',
        dataIndex: 'columnName',
        render: (text,record)=>(
          <Input style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleNameChange(e,record)}/>
        )
      },{
        title: '字段类型',
        dataIndex: 'columnType',
        render: (text,record)=>(
          <Select style={{width: 159}}  defaultValue={text?text:undefined} onChange={(e)=>this.handleSelectChange(e,record)}>
            {
              field_type.map(o=>{
                return <Option key={o.value} value={o.value}>{o.name}</Option>
              })
            }
          </Select>
        )
      },{
        title: '注释',
        dataIndex: 'comment',
        render: (text,record)=>(
          <Input style={{width: 159}}  defaultValue={text} onChange={(e)=>this.handleCommentChange(e,record)}/>
        )
      },{
        title: '操作',
        dataIndex: 'action',
        render: (text,record)=>(
          <span className="action-span">
            <a href="javascript:;" onClick={()=>this.move(record,flag,1)}>上移</a>
            <span className="line"/>
            <a href="javascript:;" onClick={()=>this.move(record,flag,2)}>下移</a>
            <span className="line"/>
            <a href="javascript:;" onClick={()=>this.remove(record,flag)}>删除</a>
          </span>
        )
      }
    ]

    let col_field = [
      {
        title: '字段名',
        dataIndex: 'columnName',
        render: (text,record)=>(
          <Input style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleNameChange(e,record)}/>
        )
      },{
        title: '字段类型',
        dataIndex: 'columnType',
        render: (text,record)=>(
          <Select style={{width: 159}}  defaultValue={text?text:undefined} onChange={(e)=>this.handleSelectChange(e,record)}>
            {
              field_type.map(o=>{
                return <Option key={o.value} value={o.value}>{o.name}</Option>
              })
            }
          </Select>
        )
      },{
        title: '倒排索引',
        dataIndex: 'invert',
        render: (text,record)=>(
          <Checkbox defaultValue={text===1?true:false} onChange={(e)=>this.handleInvert(e,record)}></Checkbox>
        )
      },{
        title: '字典编码',
        dataIndex: 'dictionary',
        render: (text,record)=>(
          <Checkbox defaultValue={text===1?true:false} onChange={(e)=>this.handleDictionary(e,record)}></Checkbox>
        )
      },{
        title: '多维索引',
        dataIndex: 'sortColumn',
        render: (text,record)=>(
          <Checkbox defaultValue={text===1?true:false} onChange={(e)=>this.handleSortColumn(e,record)}></Checkbox>
        )
      },{
        title: '注释',
        dataIndex: 'comment',
        render: (text,record)=>(
          <Input style={{width: 159}}  defaultValue={text} onChange={(e)=>this.handleCommentChange(e,record)}/>
        )
      },{
        title: '操作',
        dataIndex: 'action',
        render: (text,record)=>(
          <span className="action-span">
            <a href="javascript:;" onClick={()=>this.move(record,flag,1)}>上移</a>
            <span className="line"/>
            <a href="javascript:;" onClick={()=>this.move(record,flag,2)}>下移</a>
            <span className="line"/>
            <a href="javascript:;" onClick={()=>this.remove(record,flag)}>删除</a>
          </span>
        )
      }
    ]
    
    return flag===1?col_field:col;
  }

  render(){
    const {columns,partitions} = this.state;
    console.log(columns)
    console.log(partitions)
    return (
      <Row className="step-two-container step-container">
        <div className="table-panel">
          <span className="title">权限管理</span>
          <Table 
          columns={this.getTableCol(1)}
          dataSource={columns}
          rowKey="_fid"
          pagination={false}
          size="small"
          ></Table>
          <a className="btn" href="javascript:;" onClick={()=>this.addNewLine(1)}><Icon className="icon" type="plus-circle-o"  />添加字段</a>
        </div>
        <div className="table-panel">
          <div className="area-title-container">
            <span className="title">分区信息</span>
            <a href="javascript:;"><Icon className="icon" type="question-circle-o"/>如何添加复杂的分区格式?</a>
          </div>
          <Table 
          columns={this.getTableCol(2)}
          dataSource={partitions}
          rowKey="_fid"
          pagination={false}
          size="small"
          ></Table>
          <a className="btn" href="javascript:;" onClick={()=>this.addNewLine(2)}><Icon className="icon" type="plus-circle-o" />添加字段</a>
        </div>

        <div className="nav-btn-box">
              <Button onClick={this.props.handleLastStep}>上一步</Button>
              <Button type="primary" onClick={this.props.handleSave}>下一步</Button>
        </div>
      </Row>
    )
  }
}
