import React, { Component } from 'react';
import { Input, Table, Select, Icon, Button } from 'antd'

const Option = Select.Option;

// const field_list = [];
// const area_list = [];

export default class StepTwo extends Component{
  constructor(props){
    super();
    this.state = {
      field_list: [],
      area_list: [],
    }
  }

  componentWillMount(){
    const { formData } = this.props;
    this.state.field_list = formData.field_list || [];
    this.state.area_list = formData.area_list || [];
  }
  componentWillReceiveProps(nextProps){
    const { formData } = this.props;
    this.setState({
      field_list: formData.field_list || [],
      area_list: formData.area_list || []
    })
  }

  next = ()=>{
    this.props.handleNextStep();
  }

  addNewLine = (flag)=>{
    let {field_list,area_list} = this.state;
    let _fid = 0;
    if(flag === 1){
      field_list.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      field_list[field_list.length] = {
        _fid: _fid + 1,
        name: '',
        type: 'STRING',
        comment: ''
      }
      this.setState({
        field_list: field_list
      })
    }else if(flag === 2){
      area_list.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      area_list[area_list.length] = {
        _fid: _fid + 1,
        name: '',
        type: 'STRING',
        comment: '',
      }
      this.setState({
        area_list: area_list
      })
    }
  }

  handleNameChange = (e,record)=>{
    let {field_list,area_list} = this.state;
    record.name = e.target.value;
    console.log(field_list)
    this.saveDataToStorage();
  }
  handleSelectChange = (e,record)=>{
    let {field_list, area_list} = this.state;
    record.type = e;
    this.saveDataToStorage();
  }
  handleCommentChange = (e,record)=>{
    let {field_list,area_list} = this.state;
    record.comment = e.target.value;
    this.saveDataToStorage();
  }

  remove = (record,flag)=>{
    let {field_list,area_list} = this.state;

    flag === 1?field_list.splice(field_list.indexOf(record),1):area_list.splice(area_list.indexOf(record),1);

    this.setState({
      field_list: field_list,
      area_list: area_list
    })
    this.saveDataToStorage();
  }

  move = (record,flag,type)=>{
    //type 1上移 2下移
    // let mid = {};
    let {field_list,area_list} = this.state;
    let list = flag === 1?field_list:area_list;
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
        field_list: list
      })
    else
      this.setState({
        area_list: list
      })

    
      this.saveDataToStorage();
  }

  /**
   * 保存输入的值
   */
  saveDataToStorage = ()=>{
    const {field_list, area_list} = this.state;
    this.props.saveNewTableData([{
      key: 'field_list',
      value: field_list
    },{
      key: 'area_list',
      value: area_list
    }])
  }

  getTableCol = (flag)=>{
    let tableCol = [
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
          <Select style={{width: 159}}  defaultValue={text?text:STRING} onChange={(e)=>this.handleSelectChange(e,record)}>
            <Option value="STRING">STRING</Option>
            <Option value="INT">INT</Option>
            <Option value="LONG">LONG</Option>
            <Option value="BLOG">BLOG</Option>
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

    return tableCol;
  }

  render(){
    let {field_list,area_list} = this.state;
    return (
      <div className="step-two-container step-container">
        <div className="table-panel">
          <span className="title">权限管理</span>
          <Table 
          columns={this.getTableCol(1)}
          dataSource={field_list}
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
          dataSource={area_list}
          rowKey="_fid"
          pagination={false}
          size="small"
          ></Table>
          <a className="btn" href="javascript:;" onClick={()=>this.addNewLine(2)}><Icon className="icon" type="plus-circle-o" />添加字段</a>
        </div>

        <div className="nav-btn-box">
              <Button onClick={this.props.handleLastStep}>上一步</Button>
              <Button type="primary" onClick={this.next}>下一步</Button>
        </div>
      </div>
    )
  }
}
