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
    value: 'INT',
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
const partition_mode = [
  {
    name: '标准',
    value: 'stard'
  },{
    name: 'Hash',
    value: 'hash'
  },{
    name: 'Range',
    value: 'range'
  },{
    name: 'List',
    value: 'list'
  }
]
export default class StepTwo extends Component{
  constructor(props){
    super();
    this.state = {
      columns: [],
      partitions: [],
      partitionsData: {},
      barrelData: {}
    }
  }

  componentDidMount(){
    const { columns=[], 
      partitionsData, 
      barrelData
    } = this.props.tabData.tableItem;

    this.setState({
      columns: columns,
      partitionsData: partitionsData,
      barrelData: barrelData
    })
  }
  componentWillReceiveProps(nextProps){
    const { columns=[], 
      partitionsData,
      barrelData
    } = nextProps.tabData.tableItem;

    this.setState({
      columns: columns,
      partitionsData: partitionsData,
      barrelData: barrelData
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
    let {columns,partitions, partitionsData,barrelData} = this.state;
    let _fid = 0;
    if(flag === 1){
      columns.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      columns[columns.length] = {
        _fid: _fid + 1,
        name: '',
        type: '',
        invert: 1,
        dictionary: 1,
        sortColumn: 1,
        comment: ''
      }
      this.setState({
        columns: columns
      })
    }else if(flag === 2){
      partitionsData.columns.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      partitionsData.columns[partitionsData.columns.length] = {
        _fid: _fid + 1,
        name: '',
        type: '',
        comment: '',
      }
      this.setState({
        partitionsData
      })
    }else if(flag === 3){
      barrelData.columns.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      barrelData.columns[barrelData.columns.length] = {
        _fid: _fid + 1,
        name: '',
        type: '',
        comment: '',
      }
      this.setState({
        barrelData
      })
    }
  }

  handleNameChange = (e,record)=>{
    let {columns,partitions} = this.state;
    record.name = e.target.value;
    console.log(columns)
    this.saveDataToStorage();
  }
  handleSelectChange = (e,record)=>{
    let {columns, partitions} = this.state;
    record.type = e;
    this.saveDataToStorage();
  }
  handleCommentChange = (e,record)=>{
    let {columns,partitions} = this.state;
    record.comment = e.target.value;
    this.saveDataToStorage();
  }

  remove = (record,flag)=>{
    let {columns,partitions,partitionsData, barrelData} = this.state;

    flag === 1?columns.splice(columns.indexOf(record),1):flag===2?partitionsData.columns.splice(partitionsData.columns.indexOf(record),1):barrelData.columns.splice(barrelData.columns.indexOf(record),1);

    
    this.setState({
      columns: columns,
      partitionsData,
      barrelData
    })
    this.saveDataToStorage();
  }

  move = (record,flag,type)=>{
    //type 1上移 2下移
    // let mid = {};
    let {columns,partitions, partitionsData,barrelData} = this.state;
    let list = flag === 1?columns:flag === 2?partitionsData.columns:barrelData.columns;
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
    else if(flag===2){
      this.setState({
        partitionsData
      })
    }else{
      this.setState({
        barrelData
      })
    }

    
      this.saveDataToStorage();
  }
  /**
   * 保存输入的值
   */
  saveDataToStorage = ()=>{
    const {columns, partitions, partitionsData, barrelData} = this.state;
    this.props.saveNewTableData([{
      key: 'columns',
      value: columns
    },{
      key: 'partitionData',
      value: partitionsData
    },{
      key: 'barrelData',
      value: barrelData
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

  handlePartitionModeChange = (e)=>{
    console.log(e)
    let {partitionsData} = this.state;
    partitionsData.partitionMode = e;
    partitionsData.columns = e === 'stard'?[]:[
      {
        _fid: 0,
        name: '',
        type: '',
        comment: '',
      }
    ];
    this.setState({
      partitionsData: partitionsData
    })
    this.saveDataToStorage();
  }
  handlePartitionParamChange = (e)=>{
    let {partitionsData} = this.state;
    partitionsData[`${partitionsData.partitionMode}Param`] = e.target.value;
    this.saveDataToStorage();
  }
  handleBarrelDataParamCahnge = (e)=>{
    let {barrelData} = this.state;
    barrelData.barrelNum = e.target.value;
    this.setState({
      barrelData
    })
    this.saveDataToStorage();
  }
  getTableCol = (flag)=>{
    let col = [
      {
        title: '字段名',
        dataIndex: 'name',
        render: (text,record)=>(
          <Input style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleNameChange(e,record)}/>
        )
      },{
        title: '字段类型',
        dataIndex: 'type',
        render: (text,record)=>(
          <Select style={{width: 159}}  defaultValue={text?text:undefined} onChange={(e)=>this.handleSelectChange(e,record)}>
            {
              field_type.map(o=>{
                return (<Option key={o.value} value={o.value}>{o.name}</Option>)
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

    let col_noaction = [
      {
        title: '字段名',
        dataIndex: 'name',
        render: (text,record)=>(
          <Input style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleNameChange(e,record)}/>
        )
      },{
        title: '字段类型',
        dataIndex: 'type',
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
      }
    ]

    let col_field = [
      {
        title: '字段名',
        dataIndex: 'name',
        render: (text,record)=>(
          <Input style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleNameChange(e,record)}/>
        )
      },{
        title: '字段类型',
        dataIndex: 'type',
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
          <Checkbox defaultChecked={text===1?true:false} onChange={(e)=>this.handleInvert(e,record)}></Checkbox>
        )
      },{
        title: '字典编码',
        dataIndex: 'dictionary',
        render: (text,record)=>(
          <Checkbox defaultChecked={text===1?true:false} onChange={(e)=>this.handleDictionary(e,record)}></Checkbox>
        )
      },{
        title: '多维索引',
        dataIndex: 'sortColumn',
        render: (text,record)=>(
          <Checkbox defaultChecked={text===1?true:false} onChange={(e)=>this.handleSortColumn(e,record)}></Checkbox>
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
    
    return flag===1?col_field:flag===2?col:col_noaction;
  }

  render(){
    const {columns,partitions,partitionsData,barrelData} = this.state;
    console.log(columns)
    console.log(partitions)
    return (
      <Row className="step-two-container step-container">
        <div className="table-panel">
          <span className="title">字段信息</span>
          <Table 
          columns={this.getTableCol(1)}
          dataSource={columns}
          rowKey="_fid"
          pagination={false}
          size="small"
          ></Table>
          <a className="btn" href="javascript:;" onClick={()=>this.addNewLine(1)}><Icon className="icon" type="plus-circle-o"  />添加字段</a>
        </div>
        <div className="table-panel" style={{marginBottom: 40}}>
          <div className="area-title-container">
            <span className="title">分区信息</span>
            {/* <a href="javascript:;"><Icon className="icon" type="question-circle-o"/>如何添加复杂的分区格式?</a> */}
          </div>
          <div style={{marginBottom: 10}}>
            <span>分区模式：</span>
            <Select style={{width: 100}} value={partitionsData.partitionMode} onChange={this.handlePartitionModeChange}>
              {
                partition_mode.map(o=>{
                  return (<Option key={o.value} value={o.value}>{o.name}</Option>)
                })
              }
            </Select>
          </div>
            {
              partitionsData.partitionMode === 'hash'?
              <div className="partitionParam-box" style={{marginBottom: 10}}>
                <span>分区数量：</span>
                <Input defaultValue={partitionsData.hashParam} style={{width: 200}} placeholder="1-1000之间的正整数" onChange={this.handlePartitionParamChange}/>个
              </div> : partitionsData.partitionMode === 'range'?
              <div className="partitionParam-box" style={{marginBottom: 10,display: 'flex'}}>
                <span>范围：</span>
                <Input.TextArea defaultValue={partitionsData.rangeParam} style={{height: 50, width: 300}} placeholder="多个范围之间用英文逗号间隔" onChange={this.handlePartitionParamChange}/>
              </div> : partitionsData.partitionMode === 'list' && 
              <div className="partitionParam-box" style={{marginBottom: 10,display: 'flex'}}>
                <span>分区名称：</span>
                <Input.TextArea defaultValue={partitionsData.listParam} style={{height: 50, width: 300}} placeholder="多个分区名用英文逗号间隔" onChange={this.handlePartitionParamChange}/>
              </div>
            }
          <Table
          columns={partitionsData.partitionMode === 'stard'?this.getTableCol(2):this.getTableCol(3)}
          dataSource={partitionsData.columns || []}
          rowKey="_fid"
          pagination={false}
          size="small"
          ></Table>

          {partitionsData.partitionMode === 'stard' && <a className="btn" href="javascript:;" onClick={()=>this.addNewLine(2)}><Icon className="icon" type="plus-circle-o" />添加分区字段</a>}
        </div>
        <div className="table-panel">
          <div className="area-title-container">
            <span className="title">分桶信息</span>
          </div>
          <div style={{marginBottom: 10}}>
            <span>分桶数量：</span>
            <Input defaultValue={barrelData.barrelNum} style={{width: 200}} placeholder="1-1000之间的正整数" onChange={this.handleBarrelDataParamCahnge}/>个
          </div>
          <Table
          columns={this.getTableCol(2)}
          dataSource={barrelData.columns || []}
          rowKey="_fid"
          pagination={false}
          size="small"
          ></Table>
          <a className="btn" href="javascript:;" onClick={()=>this.addNewLine(3)}><Icon className="icon" type="plus-circle-o" />添加分桶字段</a>
        </div>

        <div className="nav-btn-box">
              <Button onClick={this.props.handleLastStep}>上一步</Button>
              <Button type="primary" onClick={this.props.handleSave}>下一步</Button>
        </div>
      </Row>
    )
  }
}
