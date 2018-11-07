import React, { Component } from "react";
import { Form, Radio, Input, Select, Button, Row, Collapse, Icon, Modal, message } from "antd";
// import { formItemLayout} from "../../../../consts/index"
import API from '../../../../api';
import CopyIcon from "main/components/copy-icon";

import Editor from 'widgets/editor';

import { DDL_placeholder } from "../../../../comm/DDLCommon"
import HelpDoc, { relativeStyle } from '../../../../components/helpDoc';




const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const Panel = Collapse.Panel;


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

const scortScopeList = [
  {
    title: 'LOCAL_SORT',
    value: 0
  },{
    title: 'NO_SORT',
    value: 1
  },{
    title: 'BATCH_SORT',
    value: 2
  },{
    title: 'GLOBAL_SORT',
    value: 3
  }
]
const formItemLayout = {
  
  labelCol: {
    xs: { span: 24 },
    sm: { span: 7 },
},
wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
},
}

export default class StepOne extends Component{
  constructor(){
    super();

    this.state = {
      databaseList: [],
      sortScopeList: [],
      downIcon: false,
      short: false,
      customLifeCycle: '',
    }
  }

  handleCancel = ()=>{

  }

  componentDidMount(){
    // this.getDataBases();
  }

  // getDataBases = async (params) => {
  //   const result = await API.getDatabases();
  //   if (result.code === 1) {
  //       this.setState({
  //           databaseList: result.data,
  //       })
  //   }
  // }


  next = ()=>{
    const {form} = this.props;
    form.validateFields((err,values)=>{
      console.log(values)
      // if(values.life_cycle === -1){
      //   values.life_cycle = this.state.customLifeCycle;
      // }
      if(!err){
        this.props.handleNextStep();
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

  handleShortLiftCycleChange = (e)=>{
    console.log(e)
    this.state.customLifeCycle = e.target.value;
    this.props.saveNewTableData([{key:"shortLisyCycle", value:e.target.value}])
  }

  handleDdlChange(value) {
    this._DDL = value;
  }
  handleDDLCreateTable = ()=>{
    
    const { tabData } = this.props;
    let formData = tabData.tableItem;
    if(!formData.databaseId){
      message.error('请选择数据库')
      return;
    }
    console.log({sql:this._DDL,databaseId: formData.databaseId})
    if(!this.DDL){
      API.createTableByDDL({sql:this._DDL,databaseId: formData.databaseId}).then(res=>{
        if(res.code === 1){
          this._DDL = undefined;
          // 设置值
          this.DDLEditor.setValue('');
          this.setState({
              showDDL: false
          });
          message.success('创建成功');
          this.props.toTableDetail({databaseId: res.data.databaseId, id:res.data.id});
        }
      })
    }else{
      message.error('请输入建表语句')
    }
  }

  handleCancel = ()=>{
    this._DDL = undefined;
    this.DDLEditor.setValue('');
    this.setState({
        showDDL: false
    })
  }


  render(){
    const { getFieldDecorator, getFieldsValue } = this.props.form;
    const { tabData,databaseList } = this.props;
    let formData = tabData.tableItem;
    console.log(formData)
    return (
      <Row className="step-one-container step-container">
        <div className="btn-box">
          <Button className="ddl-btn" type="primary" onClick={()=>{this.setState({showDDL: true})}}>DDL建表</Button>
        </div>
        <Form>
          <FormItem
          {...formItemLayout}
          label="数据库">
            {
              getFieldDecorator('databaseId',{
                rules: [
                  {required: true, message: '数据库不可为空'},
                ],
                initialValue: formData.databaseId || undefined
              })(
                  <Select getPopupContainer={triggerNode => triggerNode.parentNode} style={{width: 570,marginRight:10}}>
                  {
                    databaseList.map(o=>(
                      <Option key={o.id} value={o.id}>{o.name}</Option>
                    ))
                  }
                  </Select>
              )
            }
          </FormItem>
          <FormItem
          {...formItemLayout}
          label="表名">
            {
              getFieldDecorator('tableName',{
                rules: [
                  {required: true, message:'表明不可为空'}
                ],
                initialValue: formData.tableName || undefined
              })(
                <Input style={{width: 570,marginRight:10 }} placeholder="请输入表名"/>
              )
            }
          </FormItem>
          <FormItem
          {...formItemLayout}
          label="描述">
            {
              getFieldDecorator('desc',{
                rules: [],
                initialValue: formData.desc || undefined
              })(
                <Input.TextArea style={{width: 570,marginRight:10,height: 90 }} placeholder="请输入描述信息"/>
              )
            }
          </FormItem>
          <FormItem
          {...formItemLayout}
          label="类型">
            {
              getFieldDecorator('type',{
                rules: [],
                initialValue: formData.type || 0
              })(
                <RadioGroup>
                  <Radio value={0}>内部表</Radio>
                  <Radio value={1}>外部表</Radio>
                </RadioGroup>
              )
            }
          </FormItem>
          {getFieldsValue().type === 1 && <FormItem
          {...formItemLayout}
          label="表地址">
            {
              getFieldDecorator('location',{
                rules: [
                  {required: true, message: '外部表地址不能为空'}
                ],
                initialValue: formData.location || undefined
              })(
                <Input style={{width: 570,marginRight:10 }}/>
              )
            }
          </FormItem>}
          <FormItem
          {...formItemLayout}
          label="生命周期">
              <span >
                {
                  getFieldDecorator('lifeCycle',{
                    rules: [
                      {required: true, message: '生命周期不能为空'}
                    ],
                    initialValue: formData.lifeCycle || 90
                  })(
                    <Select getPopupContainer={triggerNode => triggerNode.parentNode}  onChange={this.handleSelectChange} style={{width: getFieldsValue().lifeCycle === -1?78:570,height: 36,marginRight:10}}>
                    {options.map(o=>(
                      <Option key={o.value} value={o.value}>{o.name}</Option>
                    ))}
                    </Select>
                  )
                }
                {
                getFieldsValue().lifeCycle === -1 &&
                  <Input style={{width: 570,marginRight:10 }} size="large" style={{width: 340,height: 36, marginLeft: 10}} defaultValue={this.state.customLifeCycle} onChange={(e)=>{this.handleShortLiftCycleChange(e)}}/>
                }
              </span>
          </FormItem>
          <FormItem
          {...formItemLayout}
          label="Sort Scope">
            {
              getFieldDecorator('sortScope',{
                rules: [
                  {required: true, message: 'Sort Scope不可为空'}
                ],
                initialValue: formData.sortScope || 0
              })(
                <Select getPopupContainer={triggerNode => triggerNode.parentNode} style={{width: 570,marginRight:10}}>
                  {
                    scortScopeList.map(o=>(
                      <Option key={o.value} value={o.value}>{o.title}</Option>
                    ))
                  }
                </Select>
              )
            }
            <HelpDoc style={relativeStyle} doc="sortScope" />
          </FormItem>
          <FormItem
          {...formItemLayout}
          label="Block Size">
            {
              getFieldDecorator('blockSize',{
                rules: [
                  {required: true, message: 'Block不可为空'}
                ],
                initialValue: formData.blockSize || 1024
              })(
                <Input style={{width: 570,marginRight:10 }}/>
              )
            }
            <HelpDoc style={relativeStyle} doc="blockSize" />
          </FormItem>
          <Collapse onChange={()=>this.setState({downIcon:!this.state.downIcon})}>
            <Panel  showArrow={false} header={<span>压缩配置&nbsp;<Icon type={this.state.downIcon?"caret-down":"caret-up"}/></span>} key="1">
              <FormItem
              {...formItemLayout}
              label="压缩模式">
              {
                getFieldDecorator('compactType',{
                  initialValue: formData.compactType || 0
                })(
                  <RadioGroup>
                    <Radio value={0}>Major</Radio>
                    <Radio value={1}>Minor</Radio>
                  </RadioGroup>
                )
              }
              <HelpDoc style={relativeStyle} doc="compressMode" />
              </FormItem>
              <FormItem
              {...formItemLayout}
              label="MAJOR_COMPACTION_SIZE">
              {
                getFieldDecorator('compactionSize',{
                  initialValue: formData.compactionSize || '1024'
                })(
                  <Input style={{width: 570,marginRight:10 }}/>
                )
              }
              <HelpDoc style={relativeStyle} doc="marjorCompactionSize" />
              </FormItem>
              <FormItem
              {...formItemLayout}
              label="AUTO_LOAD_MERGE">
              {
                getFieldDecorator('autoLoadMerge',{
                  initialValue: formData.autoLoadMerge || 0
                })(
                  <RadioGroup>
                    <Radio value={0}>关闭</Radio>
                    <Radio value={1}>打开</Radio>
                  </RadioGroup>
                )
              }
              <HelpDoc style={relativeStyle} doc="autoLoadMerge" />
              </FormItem>
              <FormItem
              {...formItemLayout}
              label="COMPACTION_LEVEL_THRESHOLD：">
              {
                getFieldDecorator('levelThreshold',{
                  initialValue:formData.levelThreshold || '4,3'
                })(
                  <Input style={{width: 570,marginRight:10 }}/>
                )
              }
              <HelpDoc style={relativeStyle} doc="compactionLevelThreshold" />
              </FormItem>
              <FormItem
              {...formItemLayout}
              label="COMPACTION_PRESERVE_SEGMENTS：">
              {
                getFieldDecorator('preserveSegments',{
                  initialValue:formData.preserveSegments || '0'
                })(
                  <Input style={{width: 570,marginRight:10 }}/>
                )
              }
              <HelpDoc style={relativeStyle} doc="compactionPreserveSegments" />
              </FormItem>
              <FormItem
              {...formItemLayout}
              label="ALLOWED_COMPACTION_DAYS：">
              {
                getFieldDecorator('allowCompactionDays',{
                  initialValue:formData.allowCompactionDays || '0'
                })(
                  <Input style={{width: 570,marginRight:10 }}/>
                )
              }
              <HelpDoc style={relativeStyle} doc="allowedCompactionDays" />
              </FormItem>
            </Panel>
          </Collapse>
        </Form>
        <div className="nav-btn-box">
              <Button onClick={this.props.handleCancel} style={{width: 90}}>取消</Button>
              <Button type="primary" onClick={this.next} style={{width: 90}}>下一步</Button>
        </div>
        <Modal
        destroyOnClose={true}
        visible={this.state.showDDL}
        onOk={this.handleDDLCreateTable}
        onCancel={this.handleCancel}
        title={(
          <span>DDL建表<CopyIcon title="复制模版" style={{ marginLeft: "8px" }} copyText={DDL_placeholder} /></span>
        )}
        maskClosable={false}>
            <Editor
                style={{ height: "400px" }}
                placeholder={DDL_placeholder}
                options={{readOnly:false}}
                language="dtsql"
                options={{ readOnly: false } }
                onChange={this.handleDdlChange.bind(this)}
                value={this._DDL} editorInstanceRef={(e) => { this.DDLEditor = e }}
            />
        </Modal>
      </Row>
    )
  }
}
StepOne = Form.create({
  onValuesChange(props, changedValues) {
    console.log(props)
    let p = [];
    for(let key in changedValues){
      p.push({
        key:key,
        value:changedValues[key]
      })
    }
    props.saveNewTableData(p)
  }
})(StepOne)