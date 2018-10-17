import React, { Component } from 'react';
import { Input, Table, Select, Icon, Button } from 'antd'

const Option = Select.Option;

// const index_list = [];
// const area_list = [];

export default class StepThree extends Component{
  constructor(props){
    super();
    this.state = {
      index_list: [],
    }
  }


  componentWillMount(){
    const { formData } = this.props;
    this.state.index_list = formData.index_list || [];
  }
  componentWillReceiveProps(nextProps){
    const { formData } = nextProps;
    this.setState({
      index_list: formData.index_list || []
    })
  }

  next = ()=>{
    this.props.handleNextStep();
  }
  last = ()=>{
    
  }

  addNewLine = ()=>{
    let {index_list,area_list} = this.state;
    let _fid = 0;
    index_list.map(o=>{
      if(o._fid>_fid)
        _fid = o._fid
    })
    index_list[index_list.length] = {
      _fid: _fid + 1,
      name: '',
      field_type: 'STRING',
      index_type: 'STRING',
      comment: ''
    }
    this.setState({
      index_list: index_list
    })
  }

  handleNameChange = (e,record)=>{
    record.name = e.target.value;
    this.saveDataToStorage();
  }
  handleSelectChange = (e,record)=>{
    record.field_type = e;
    this.saveDataToStorage();
  }
  handleIndexTypeChange = (e,record)=>{
    record.index_type = e;
    this.saveDataToStorage();
  }
  handleCommentChange = (e,record)=>{
    record.comment = e.target.value;
    this.saveDataToStorage();
  }

  remove = (record,flag)=>{
    let {index_list} = this.state;

    index_list.splice(index_list.indexOf(record),1)

    this.setState({
      index_list: index_list,
    })
    this.saveDataToStorage();
  }

  move = (record,type)=>{
    //type 1上移 2下移
    // let mid = {};
    let {index_list} = this.state;
    let list = index_list;
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

      this.setState({
        index_list: list
      })
    
      this.saveDataToStorage();
  }

  /**
   * 保存输入的值
   */
  saveDataToStorage = ()=>{
    const {index_list} = this.state;
    this.props.saveNewTableData([{
      key: 'index_list',
      value: index_list
    }])
  }

  getTableCol = ()=>{
    let tableCol = [
      {
        title: '索引名称',
        dataIndex: 'name',
        render: (text,record)=>(
          <Input style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleNameChange(e,record)}/>
        )
      },{
        title: '字段类型',
        dataIndex: 'field_type',
        render: (text,record)=>(
          <Select style={{width: 159}}  defaultValue={text?text:STRING} onChange={(e)=>this.handleSelectChange(e,record)}>
            <Option value="STRING">STRING</Option>
            <Option value="INT">INT</Option>
            <Option value="LONG">LONG</Option>
            <Option value="BLOG">BLOG</Option>
          </Select>
        )
      },{
        title: '索引类型',
        dataIndex: 'index_type',
        render: (text,record)=>(
          <Select style={{width: 159}}  defaultValue={text?text:STRING} onChange={(e)=>this.handleIndexTypeChange(e,record)}>
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
            <a href="javascript:;" onClick={()=>this.move(record,1)}>上移</a>
            <span className="line"/>
            <a href="javascript:;" onClick={()=>this.move(record,2)}>下移</a>
            <span className="line"/>
            <a href="javascript:;" onClick={()=>this.remove(record)}>删除</a>
          </span>
        )
      }
    ]

    return tableCol;
  }

  

  render(){
    let {index_list,area_list} = this.state;
    

    return (
      <div className="step-two-container step-container">
        <div className="table-panel">
          <Table 
          columns={this.getTableCol(1)}
          dataSource={index_list}
          rowKey="_fid"
          pagination={false}
          size="small"
          ></Table>
          <a className="btn" href="javascript:;" onClick={()=>this.addNewLine(1)}><Icon className="icon" type="plus-circle-o"  />添加字段</a>
        </div>

        <div className="nav-btn-box">
              <Button onClick={this.props.handleLastStep}>上一步</Button>
              <Button type="primary" onClick={this.next}>下一步</Button>
        </div>
      </div>
    )
  }
}
