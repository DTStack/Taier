import React, { Component } from 'react';
import { Input, Table, Select, Icon, Button, Row, Checkbox, notification } from 'antd'
import API from '../../../../api';
import HelpDoc, { relativeStyle } from '../../../../components/helpDoc';



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
  },{
    name: 'DECIMAL',
    value: 'DECIMAL'
  }
]
const partition_mode = [
  {
    name: '标准',
    value: 0
  },{
    name: 'Hash',
    value: 1
  },{
    name: 'Range',
    value: 2
  },{
    name: 'List',
    value: 3
  }
]

const decimalPrecision = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38]
const decimalScale = [0,1,2,3,4,5,6,7,8,9]
export default class StepTwo extends Component{
  constructor(props){
    super();
    this.state = {
      columns: [],
      partitions: {},
      bucketInfo: {}
    }
  }

  componentDidMount(){
    const { columns=[], 
      partitions, 
      bucketInfo,
    } = this.props.tabData.tableItem;

    this.setState({
      columns: columns,
      partitions: partitions,
      bucketInfo: bucketInfo
    })
  }
  componentWillReceiveProps(nextProps){
    const { columns=[], 
      partitions,
      bucketInfo
    } = nextProps.tabData.tableItem;
    bucketInfo.bucketNumber = bucketInfo.bucketNumber;

    this.setState({
      columns: columns,
      partitions: partitions,
      bucketInfo: bucketInfo
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
    let {columns, partitions,bucketInfo} = this.state;
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
      partitions.columns.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      partitions.columns[partitions.columns.length] = {
        _fid: _fid + 1,
        name: '',
        type: '',
        comment: '',
      }
      this.setState({
        partitions
      })
    }else if(flag === 3){
      bucketInfo.infos.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      bucketInfo.infos[bucketInfo.infos.length] = {
        _fid: _fid + 1,
        name: '',
        type: '',
        comment: '',
      }
      this.setState({
        bucketInfo
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
    let {columns,partitions, bucketInfo} = this.state;

    flag === 1?columns.splice(columns.indexOf(record),1):flag===2?partitions.columns.splice(partitions.columns.indexOf(record),1):bucketInfo.infos.splice(bucketInfo.infos.indexOf(record),1);

    
    this.setState({
      columns: columns,
      partitions,
      bucketInfo
    })
    this.saveDataToStorage();
  }

  move = (record,flag,type)=>{
    //type 1上移 2下移
    // let mid = {};
    let {columns, partitions,bucketInfo} = this.state;
    let list = flag === 1?columns:flag === 2?partitions.columns:bucketInfo.infos;
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
        partitions
      })
    }else{
      this.setState({
        bucketInfo
      })
    }


    this.saveDataToStorage();
  }
  /**
   * 保存输入的值
   */
  saveDataToStorage = ()=>{
    const {columns, partitions, bucketInfo} = this.state;
    this.props.saveNewTableData([{
      key: 'columns',
      value: columns
    },{
      key: 'partitions',
      value: partitions
    },{
      key: 'bucketInfo',
      value: bucketInfo
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

  handleBucketChange = (e,record)=>{
    console.log(e)
    record.flagIndex = e;
    const {columns} = this.state;
    e = columns[e];
    record.name = e.name;
    record.type = e.type;
    this.saveDataToStorage();
  }

  handleDECIMALSelectChange = (e,record,flag)=>{
    if(flag === 1){
      record.precision = e
    }else{
      record.scale = e;
    }
    this.saveDataToStorage();
  }

  handlePartitionModeChange = (e)=>{
    console.log(e)
    let {partitions} = this.state;
    partitions.partitionType = e;
    partitions.columns = e === 0?[]:[
      {
        _fid: 0,
        name: '',
        type: '',
        comment: '',
      }
    ];
    this.setState({
      partitions: partitions
    },()=>{
      console.log(this.state.partitions)
    })
    this.saveDataToStorage();
  }
  handlePartitionParamChange = (e)=>{
    let {partitions} = this.state;
    partitions.partConfig = e.target.value;
    this.saveDataToStorage();
  }
  handleBarrelDataParamCahnge = (e)=>{
    let {bucketInfo} = this.state;
    bucketInfo.bucketNumber = e.target.value;
    // this.setState({
    //   bucketInfo
    // })
    this.saveDataToStorage();
  }
  getBucketNumber = ()=>{
    console.log(this.state.bucketInfo.bucketNumber)
    return this.state.bucketInfo.bucketNumber
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
            <Select  style={{width: 159}}  defaultValue={text?text:undefined} onChange={(e)=>this.handleSelectChange(e,record)}>
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
    let col_bucket = [
      {
        title: '字段名',
        dataIndex: 'name',
        render: (text,record)=>(
          <Select defaultValue={record.flagIndex} style={{width: 159}} onChange={(e)=>this.handleBucketChange(e,record)}>
            {
              this.state.columns.map(o=>{

                return o.name? <Option key={o._fid} value={this.state.columns.indexOf(o)}>{o.name}</Option>:''
              })
            }
          </Select>
        )
      },{
        title: '字段类型',
        dataIndex: 'type',
        render: (text,record)=>(
          <span style={{fontSize: 12,width: 159,display:'block'}}>{text}</span>
        )
      },{
        title: '注释',
        dataIndex: 'comment',
        render: (text,record)=>(
          <Input style={{width: 159, }}  defaultValue={text} onChange={(e)=>this.handleCommentChange(e,record)}/>
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
        dataIndex: 'name',
        render: (text,record)=>(
          <Input style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleNameChange(e,record)}/>
        )
      },{
        title: '字段类型',
        dataIndex: 'type',
        render: (text,record)=>(
          <span>
          <Select getPopupContainer={()=>document.getElementById('form-box')} style={{width: record.type === 'DECIMAL'?90:159,marginRight: 5}}  defaultValue={text?text:undefined} onChange={(e)=>this.handleSelectChange(e,record)}>
            {
              field_type.map(o=>{
                return <Option key={o.value} value={o.value}>{o.name}</Option>
              })
            }
          </Select>

          {
            record.type === 'DECIMAL' && 
            <span>
              <Select style={{width: 50,marginRight: 5}}  defaultValue={record.precision?record.precision:undefined} onChange={(e)=>this.handleDECIMALSelectChange(e,record,1)}>
                {
                  decimalPrecision.map(o=>{
                    return <Option key={o} value={o}>{o}</Option>
                  })
                }
              </Select>
              <Select style={{width: 50,marginRight: 5}}  defaultValue={record.scale?record.scale:undefined} onChange={(e)=>this.handleDECIMALSelectChange(e,record,2)}>
                {
                  decimalScale.map(o=>{
                    return <Option key={o} value={o}>{o}</Option>
                  })
                }
              </Select>
              
            <HelpDoc style={relativeStyle} doc="decimalType" />
            </span>
          }
          </span>
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
          <Checkbox disabled={record.type==='DOUBLE'} defaultChecked={text===1?record.type!=='DOUBLE'?true:false:false} onChange={(e)=>this.handleSortColumn(e,record)}></Checkbox>
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
    
    return flag===1?col_field:flag===2?col:flag===4?col_bucket:col_noaction;
  }

  render(){
    const {columns,partitions,bucketInfo} = this.state;
    console.log(columns)
    return (
      <Row className="step-two-container step-container" id="table-panel">
        <div className="table-panel" id="field_panel">
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
        <div className="table-panel" style={{marginBottom: 40}} id="parti_panel">
          <div className="area-title-container">
            <span className="title">分区信息</span>
            {/* <a href="javascript:;"><Icon className="icon" type="question-circle-o"/>如何添加复杂的分区格式?</a> */}
          </div>
          <div style={{marginBottom: 10}}>
            <span>分区模式：</span>
            <Select getPopupContainer={()=>document.getElementById('table-panel')} style={{width: 100}} value={partitions.partitionType} onChange={this.handlePartitionModeChange}>
              {
                partition_mode.map(o=>{
                  return (<Option key={o.value} value={o.value}>{o.name}</Option>)
                })
              }
            </Select>
          </div>
            {
              partitions.partitionType === 1?
              <div className="partitionParam-box" style={{marginBottom: 10}}>
                <span>分区数量：</span>
                <Input defaultValue={partitions.partConfig} style={{width: 200}} placeholder="1-1000之间的正整数" onChange={this.handlePartitionParamChange}/>个
              </div> : partitions.partitionType === 2?
              <div className="partitionParam-box" style={{marginBottom: 10,display: 'flex'}}>
                <span>范围：</span>
                <Input.TextArea defaultValue={partitions.partConfig} style={{height: 50, width: 300}} placeholder="多个范围之间用英文逗号间隔" onChange={this.handlePartitionParamChange}/>
              </div> : partitions.partitionType === 3 && 
              <div className="partitionParam-box" style={{marginBottom: 10,display: 'flex'}}>
                <span>分区名称：</span>
                <Input.TextArea defaultValue={partitions.partConfig} style={{height: 50, width: 300}} placeholder="多个分区名用英文逗号间隔" onChange={this.handlePartitionParamChange}/>
              </div>
            }
          <Table
          columns={partitions.partitionType === 0?this.getTableCol(2):this.getTableCol(3)}
          dataSource={partitions.columns || []}
          rowKey="_fid"
          pagination={false}
          size="small"
          ></Table>

          {partitions.partitionType === 0 && <a className="btn" href="javascript:;" onClick={()=>this.addNewLine(2)}><Icon className="icon" type="plus-circle-o" />添加分区字段</a>}
        </div>
        <div className="table-panel" id="bucket_panel">
          <div className="area-title-container">
            <span className="title">分桶信息</span>
          </div>
          <div style={{marginBottom: 10}}>
            <span>分桶数量：</span>
            <Input style={{width: 100,marginRight: 4}} value={bucketInfo.bucketNumber} placeholder="1-1000之间的正整数" onChange={this.handleBarrelDataParamCahnge}/>个
          </div>
          <Table
          columns={this.getTableCol(4)}
          dataSource={bucketInfo.infos || []}
          rowKey="_fid"
          pagination={false}
          size="small"
          ></Table>
          <a className="btn" href="javascript:;" onClick={()=>this.addNewLine(3)}><Icon className="icon" type="plus-circle-o" />添加分桶字段</a>
        </div>

        <div className="nav-btn-box">
              <Button onClick={this.props.handleLastStep} style={{width: 90}}>上一步</Button>
              <Button type="primary" onClick={this.props.handleSave} style={{width: 90}}>下一步</Button>
        </div>
      </Row>
    )
  }
}
