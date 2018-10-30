import React, {Component} from 'react';
import {Row, Table, Button, Input, Form, Select, Icon,Checkbox, message, notification, Modal} from 'antd';
import API from '../../../../api';

const confirm = Modal.confirm;

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

const field_types = [
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


export default class EditTable extends Component{
  constructor(props){
    super(props)
    this.state = {
      customLifeCycle: 0,
      short: false,
      tableDetail: {columns:[]},
    }
  }
  componentDidMount(){
    const { tableDetail } = this.props.data;
    tableDetail.columns = tableDetail.columns || [];
    tableDetail.partitions = tableDetail.partitions || [];
    if([3,7,30,90,365].indexOf(tableDetail.lifeDay) === -1){
      this.state.customLifeCycle = tableDetail.lifeDay
      tableDetail.lifeDay === -1;
    }

    this.setState({
      tableDetail: tableDetail
    })
  }
  componentWillReceiveProps(nextProps){
    const { tableDetail } = nextProps.data;

    tableDetail.columns = tableDetail.columns || [];
    tableDetail.partitions = tableDetail.partitions || [];

    if([3,7,30,90,365].indexOf(tableDetail.lifeDay) === -1){
      tableDetail.lifeDay === -1;
      this.state.customLifeCycle = tableDetail.lifeDay
    }

    this.setState({
      tableDetail: tableDetail
    })
  }
  saveInfo = ()=>{
    const {form} = this.props;
    form.validateFields((err,value)=>{
      console.log(value)
      if(!err){
        if(value.lifeDay === -1){
          value.lifeDay = this.state.customLifeCycle;
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
      tableDetail.columns.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      tableDetail.columns.push({
        _fid: _fid + 1,
        name: '',
        type: '',
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
      tableDetail.partitions.map(o=>{
        if(o._fid>_fid)
          _fid = o._fid
      })
      tableDetail.partitions.push({
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
  handleDelTable = ()=>{
    let self = this;
    const {databaseId, id} = this.props.data.tableDetail;
    confirm({
      title: '删除表后无法恢复，确认将其删除？',
      onOk(){
        API.dropTable({databaseId, id}).then(res=>{
          if(res.code === 1){
            message.success('删除成功');
            self.props.closeTab();
            self.props.loadCatalogue();
          }else{
            notification.error({
              message: '提示',
              description: res.message
            })
          }
        })
      }
    })
    
  }


  move = (record,flag,type)=>{
    //type 1上移 2下移
    // let mid = {};
    let {tableDetail} = this.state;
    let list = flag === 1?tableDetail.columns:tableDetail.partitions;
    console.log(type)
    console.log( list.indexOf(record) )
    console.log(list.length)

    //是否到顶
    if((type === 1 && list.indexOf(record) === 0) || (type === 2 && list.indexOf(record) === list.length-1))
      return


    //只可以在新增行中上下移动
    if((flag === 2 && (!list[list.indexOf(record)+1].isNew)) || (flag === 1 && (!list[list.indexOf(record)-1].isNew))){
      return;
    }

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

    this.saveDataToStorage();
  }


  remove = (record,flag)=>{
    let {tableDetail} = this.state;

    flag === 1?tableDetail.columns.splice(tableDetail.columns.indexOf(record),1):tableDetail.partitions.splice(tableDetail.partitions.indexOf(record),1);

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
      key: 'columns',
      value: tableDetail.columns
    },{
      key: 'partitions',
      value: tableDetail.partitions
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
                {field_types.map(o=>{
                  return <Option key={o.value} value={o.value}>{o.name}</Option>
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

    return (
      <div className="edit-table-container" style={{marginBottom: 50}}>
        <Row className="panel">
          <div className="title">基本信息</div>
          <Form>
            <FormItem
             
            label="表名">
              {
                getFieldDecorator('tableName',{
                  rules: [
                    {required: true, message: '表名不可为空'}
                  ],
                  initialValue: tableDetail.tableName || undefined
                })(
                  <Input style={{width: 430, height: 36}}/>
                )
              }
            </FormItem>
            <FormItem
             
            label="生命周期">
              <span >
                {
                  getFieldDecorator('lifeDay',{
                    rules: [
                      {required: true, message: '生命周期不能为空'}
                    ],
                    initialValue: tableDetail.lifeDay || undefined
                  })(
                    <Select onChange={this.handleSelectChange} style={{width: getFieldsValue().lifeDay === -1?78:430,height: 36}}>
                    {options.map(o=>(
                      <Option key={o.value} value={o.value}>{o.name}</Option>
                    ))}
                    </Select>
                  )
                }
                {
                getFieldsValue().lifeDay === -1 &&
                  <Input size="large" style={{width: 340,height: 36, marginLeft: 10}} defaultValue={this.state.customLifeCycle} onChange={(e)=>{this.state.customLifeCycle = e}}/>
                }
              </span>
            </FormItem>
            <FormItem
             
            label="描述">
              {
                getFieldDecorator('tableDesc',{
                  rules: [
                    {required: true, message:'描述不可为空'}
                  ],
                  initialValue: tableDetail.tableDesc || undefined
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
            dataSource={tableDetail.columns}
            pagination={false}>
            </Table>
          <a className="btn" style={{marginTop: 16, display: 'block'}} href="javascript:;" onClick={()=>this.addNewLine(1)}><Icon style={{marginRight: 5}} className="icon" type="plus-circle-o" />添加字段</a>
        </Row>
        <Button type="danger"  style={{marginLeft: 20, width: 90,height: 30}} onClick={this.handleDelTable}>删除</Button>
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