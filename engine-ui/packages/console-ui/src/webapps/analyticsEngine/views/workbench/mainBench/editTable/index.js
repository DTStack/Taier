import React, {Component} from 'react';
import {Row, Table, Button, Input, Form, Select, Icon,Checkbox} from 'antd';
import { formItemLayout } from "../../../../consts/index"

const FormItem = Form.Item;
const Option = Select.Option;

const options = [{
  name:'3天',
  value: 3
}, {
  name: '7天',
  value: 7
}, {
  name: '30天',
  value: 30
}, {
  name: '90天',
  value: 90
}, {
  name: '365天',
  value: 365
},{
  name: '自定义',
  value: -1,
}]
const fieldTypes = [
  {
    title: 'STRING',
    value: 'STRING'
  }
]
const indexTypes = [
  {
    title: 'STRING',
    value: 'STRING'
  }
]


export default class EditTable extends Component{
  constructor(props){
    super(props)
    this.state = {
      customLifeCycle: 0,
      short: false,
      tableDetail: {fieldList:[]},
    }
  }
  componentDidMount(){
    const { tableDetail } = this.props.data;
    tableDetail.fieldList = tableDetail.fieldList || [];
    tableDetail.indexList = tableDetail.indexList || [];

    this.setState({
      tableDetail: tableDetail
    })
  }
  componentWillReceiveProps(nextProps){
    const { tableDetail } = nextProps.data;

    tableDetail.fieldList = tableDetail.fieldList || [];
    tableDetail.indexList = tableDetail.indexList || [];

    this.setState({
      tableDetail: tableDetail
    })
  }
  saveInfo = ()=>{
    const {form} = this.props;
    form.validateFields((err,value)=>{
      console.log(value)
      if(!err){
        if(value.life_cycle === -1){
          value.life_cycle = this.state.customLifeCycle;
        }
        console.log(value)
      }
    })
  }
  handleSelectChange = (e)=>{
    if(e === -1){
      this.setState({
        short: true
      })
    }
  }
  handleFieldNameChange = (e,record)=>{
    record.name = e.target.value;
    this.saveDataToStorage();
  }
  handleFieldTypeChange = (e,record)=>{
    record.type = e;
    this.saveDataToStorage();
  }
  handleFieldCommentChange = (e,record)=>{
    record.comment = e.target.value;
    this.saveDataToStorage();
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


  addNewLine = (flag)=>{
    console.log(this.state.tableDetail)
    let {tableDetail} = this.state;
    let _fid = 0;

    if(flag === 1){
      //字段
      tableDetail.fieldList.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      tableDetail.fieldList.push({
        _fid: _fid + 1,
        columnName: '',
        columnType: '',
        comment: '',
        invert: 1,
        dictionary: 0,
        sortColumn: 0,
        isNew: true
      })
      this.setState({
        tableDetail: tableDetail
      })
    }else if(flag === 2){
      //索引
      tableDetail.indexList.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      tableDetail.indexList.push({
        _fid: _fid + 1,
        name: '',
        field_type: '',
        index_type: '',
        comment: '',
        isNew: true
      })
      this.setState({
        tableDetail: tableDetail
      })
    }
  }


  move = (record,flag,type)=>{
    //type 1上移 2下移
    // let mid = {};
    let {tableDetail} = this.state;
    let list = flag === 1?tableDetail.fieldList:tableDetail.indexList;
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
      tableDetail: tableDetail
    })

    // if(flag === 1)
    //   this.setState({
    //     field_list: list
    //   })
    // else
    //   this.setState({
    //     area_list: list
    //   })

    
    this.saveDataToStorage();
  }


  remove = (record,flag)=>{
    let {tableDetail} = this.state;

    flag === 1?tableDetail.fieldList.splice(tableDetail.fieldList.indexOf(record),1):tableDetail.indexList.splice(tableDetail.indexList.indexOf(record),1);

    console.log(tableDetail)
    this.setState({
      tableDetail: tableDetail
    })
    this.saveDataToStorage();
  }

  handleIndexTypeChange = (e,record)=>{
    record.index_type = e;
    this.saveDataToStorage();
  }


  /**
   * 保存输入的值
   */
  saveDataToStorage = ()=>{
    const {tableDetail} = this.state;
    this.props.saveEditTableInfo([{
      key: 'fieldList',
      value: tableDetail.fieldList
    },{
      key: 'indexList',
      value: tableDetail.indexList
    }])
  }


  render(){
    const { tableDetail } = this.state;
    const { getFieldDecorator, getFieldsValue } = this.props.form;
    const tableCol_field = [
      {
        title: '字段名称',
        dataIndex: 'name',
        render: (text,record)=>{
          if(record.isNew){
            return <Input style={{width: 159, height: 26}} defaultValue={text} onChange={(e)=>this.handleFieldNameChange(e,record)}/>
          }else
            return text
        }
      },{
        title: '字段类型',
        dataIndex: 'type',
        render: (text,record)=>{
          if(record.isNew){
            return <Select style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleFieldTypeChange(e,record)}>
                {fieldTypes.map(o=>{
                  return <Option key={o.value} value={o.value}>{o.title}</Option>
                })}
              </Select>
          }else
            return text
        }
      },{
        title: '倒排索引',
        dataIndex: 'invert',
        render: (text,record)=>(
          <Checkbox disabled={!record.isNew} defaultChecked={text===1?true:false} onChange={(e)=>this.handleInvert(e,record)}></Checkbox>
        )
      },{
        title: '字典编码',
        dataIndex: 'dictionary',
        render: (text,record)=>(
          <Checkbox disabled={!record.isNew} defaultChecked={text===1?true:false} onChange={(e)=>this.handleDictionary(e,record)}></Checkbox>
        )
      },{
        title: '多维索引',
        dataIndex: 'sortColumn',
        render: (text,record)=>(
          <Checkbox disabled={!record.isNew} defaultChecked={text===1?true:false} onChange={(e)=>this.handleSortColumn(e,record)}></Checkbox>
        )
      },{
        title: '注释内容',
        dataIndex: 'comment',
        render: (text,record)=>{
          if(record.isNew){
            return <Input style={{width: 159, height: 26}} defaultValue={text} onChange={(e)=>this.handleFieldCommentChange(e,record)}/>
          }else
            return text
        }
      },{
        title: '操作',
        dataIndex: 'action',
        render: (text,record)=>{
          if(record.isNew){
            return <span className="action-span">
            <a href="javascript:;" onClick={()=>this.move(record,1,1)}>上移</a>
            <span className="line"/>
            <a href="javascript:;" onClick={()=>this.move(record,1,2)}>下移</a>
            <span className="line"/>
            <a href="javascript:;" onClick={()=>this.remove(record,1)}>删除</a>
            </span>
          }
        }
      }
    ]
    const tableCol_index = [
      {
        title: '索引名称',
        dataIndex: 'name',
        render: (text,record)=>{
          if(record.isNew){
            return <Input defaultValue={text} onChange={(e)=>this.handleFieldNameChange(e,record)}/>
          }else
            return text;
        }
      },{
        title: '字段类型',
        dataIndex: 'field_type',
        render: (text,record)=>{
          if(record.isNew){
            return <Select style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleFieldTypeChange(e,record)}>
                {fieldTypes.map(o=>{
                  return <Option value={o.value}>{o.title}</Option>
                })}
              </Select>
          }else
            return text
        }
      },{
        title: '索引类型',
        dataIndex: 'index_type',
        render: (text,record)=>{
          if(record.isNew){
            return <Select style={{width: 159}} defaultValue={text} onChange={(e)=>this.handleIndexTypeChange(e,record)}>
                {indexTypes.map(o=>{
                  return <Option value={o.value}>{o.title}</Option>
                })}
              </Select>
          }else
            return text
        }
      },{
        title: '备注',
        dataIndex: 'comment',
        render: (text,record)=>{
          return <Input defaultValue={text} onChange={(e)=>this.handleFieldCommentChange(e,record)}/>
        }
      },{
        title: '操作',
        dataIndex: 'action',
        render: (text,record)=>(
          <span className="action-span">
            <a onClick={()=>this.remove(record,2)}>删除</a>
          </span>
        )
      }
    ]

    return (
      <div className="edit-table-container" style={{marginBottom: 50}}>
        <Row className="panel">
          <div className="title">基本信息</div>
          <Form>
            <FormItem
             
            label="表名">
              {
                getFieldDecorator('table_name',{
                  rules: [
                    {required: true, message: '表名不可为空'}
                  ],
                  initialValue: tableDetail.table_name || undefined
                })(
                  <Input style={{width: 430, height: 36}}/>
                )
              }
            </FormItem>
            <FormItem
             
            label="生命周期">
              <span >
                {
                  getFieldDecorator('life_cycle',{
                    rules: [
                      {required: true, message: '生命周期不能为空'}
                    ],
                    initialValue: tableDetail.life_cycle || undefined
                  })(
                    <Select onChange={this.handleSelectChange} style={{width: getFieldsValue().life_cycle === '-1'?78:430,height: 36}}>
                    {options.map(o=>(
                      <Option key={o.value}>{o.name}</Option>
                    ))}
                    </Select>
                  )
                }
                {
                getFieldsValue().life_cycle === '-1' &&
                  <Input size="large" style={{width: 340,height: 36, marginLeft: 10}} defaultValue={this.state.customLifeCycle} onChange={(e)=>{this.state.customLifeCycle = e}}/>
                }
              </span>
            </FormItem>
            <FormItem
             
            label="描述">
              {
                getFieldDecorator('desc',{
                  rules: [
                    {required: true, message:'描述不可为空'}
                  ],
                  initialValue: tableDetail.desc || undefined
                })(
                  <Input style={{width: 430, height: 36}}/>
                )
              }
            </FormItem>
          </Form>
        </Row>

        <Row className="panel table-box">
            <div className="title">字段信息</div>
            <Table
            size="small"
            className="table-small"
            columns={tableCol_field}
            rowKey="_fid"
            dataSource={tableDetail.fieldList}
            pagination={false}>
            </Table>
          <a className="btn" style={{marginTop: 16, display: 'block'}} href="javascript:;" onClick={()=>this.addNewLine(1)}><Icon style={{marginRight: 5}} className="icon" type="plus-circle-o" />添加字段</a>
        </Row>
        <Button type="primary" style={{marginLeft: 20, width: 90,height: 30}} onClick={this.props.saveTableInfo}>保存</Button>
      </div>
    )
  }
}
EditTable = Form.create({
  onValuesChange(props, changedValues) {
    console.log(props)
    let p = [];
    for(let key in changedValues){
      p.push({
        key:key,
        value:changedValues[key]
      })
    }
    props.saveEditTableInfo(p)
  }
})(EditTable)