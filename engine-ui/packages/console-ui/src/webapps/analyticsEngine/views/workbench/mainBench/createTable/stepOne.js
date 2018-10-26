import React, { Component } from "react";
import { Form, Radio, Input, Select, Button, Row, Collapse, Icon } from "antd";
import { formItemLayout} from "../../../../consts/index"
import API from '../../../../api';



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
    value: 'LOCAL_SORT'
  },{
    title: 'NO_SORT',
    value: 'NO_SORT'
  },{
    title: 'BATCH_SORT',
    value: 'BATCH_SORT'
  },{
    title: 'GLOBAL_SORT',
    value: 'GLOBAL_SORT'
  }
]

export default class StepOne extends Component{
  constructor(){
    super();

    this.state = {
      databaseList: [],
      sortScopeList: [],
      downIcon: true,
      short: false,
      customLifeCycle: '',
    }
  }

  handleCancel = ()=>{

  }

  componentDidMount(){
    this.getDataBases();
  }

  getDataBases = async (params) => {
    const result = await API.getDatabases();
    if (result.code === 1) {
        this.setState({
            databaseList: result.data,
        })
    }
}


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


  render(){
    const { getFieldDecorator, getFieldsValue } = this.props.form;
    const { tabData } = this.props;
    let formData = tabData.tableItem;
    console.log(formData)
    return (
      <Row className="step-one-container step-container">
        <Form>
          <FormItem
          {...formItemLayout}
          label="数据库">
            {
              getFieldDecorator('databaseId',{
                rules: [
                  {required: true, message: '数据库不可为空'},
                ],
                initialValue: formData.databaseId || this.props.tabData.databaseId || undefined
              })(
                  <Select>
                  {
                    this.state.databaseList.map(o=>(
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
                <Input placeholder="请输入表名"/>
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
                <Input placeholder="请输入描述信息"/>
              )
            }
          </FormItem>
          <FormItem
          {...formItemLayout}
          label="类型">
            {
              getFieldDecorator('type',{
                rules: [],
                initialValue: formData.type || 1
              })(
                <RadioGroup>
                  <Radio value={1}>内部表</Radio>
                  <Radio value={2}>外部表</Radio>
                </RadioGroup>
              )
            }
          </FormItem>
          <FormItem
          {...formItemLayout}
          label="生命周期">
              <span >
                {
                  getFieldDecorator('lifeCycle',{
                    rules: [
                      {required: true, message: '生命周期不能为空'}
                    ],
                    initialValue: formData.lifeCycle || undefined
                  })(
                    <Select onChange={this.handleSelectChange} style={{width: getFieldsValue().lifeCycle === '-1'?78:430,height: 36}}>
                    {options.map(o=>(
                      <Option key={o.value}>{o.name}</Option>
                    ))}
                    </Select>
                  )
                }
                {
                getFieldsValue().lifeCycle === '-1' &&
                  <Input size="large" style={{width: 340,height: 36, marginLeft: 10}} defaultValue={this.state.customLifeCycle} onChange={(e)=>{this.handleShortLiftCycleChange(e)}}/>
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
                initialValue: formData.sortScope || undefined
              })(
                <Select>
                  {
                    scortScopeList.map(o=>(
                      <Option key={o.value} value={o.value}>{o.title}</Option>
                    ))
                  }
                </Select>
              )
            }
          </FormItem>
          <FormItem
          {...formItemLayout}
          label="Block 大小">
            {
              getFieldDecorator('blockSize',{
                rules: [
                  {required: true, message: 'Block不可为空'}
                ],
                initialValue: formData.blockSize || undefined
              })(
                <Input/>
              )
            }
          </FormItem>
          <Collapse defaultActiveKey="1" onChange={()=>this.setState({downIcon:!this.state.downIcon})}>
            <Panel  showArrow={false} header={<span>压缩配置&nbsp;<Icon fill="#999999" type={this.state.downIcon?"caret-down":"caret-up"}/></span>} key="1">
              <FormItem
              {...formItemLayout}
              label="MAJOR_COMPACTION_SIZE">
              {
                getFieldDecorator('compactionSize',{
                  initialValue: formData.compactionSize || undefined
                })(
                  <Input/>
                )
              }
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
                    <Radio value={1}>外部表</Radio>
                  </RadioGroup>
                )
              }
              </FormItem>
              <FormItem
              {...formItemLayout}
              label="COMPACTION_LEVEL_THRESHOLD：">
              {
                getFieldDecorator('levelThreshold',{
                  initialValue:formData.levelThreshold || undefined
                })(
                  <Input/>
                )
              }
              </FormItem>
              <FormItem
              {...formItemLayout}
              label="COMPACTION_PRESERVE_SEGMENTS：">
              {
                getFieldDecorator('preserveSegments',{
                  initialValue:formData.preserveSegments || undefined
                })(
                  <Input/>
                )
              }
              </FormItem>
              <FormItem
              {...formItemLayout}
              label="ALLOWED_COMPACTION_DAYS：">
              {
                getFieldDecorator('allowCompactionDays',{
                  initialValue:formData.allowCompactionDays || undefined
                })(
                  <Input/>
                )
              }
              </FormItem>
            </Panel>
          </Collapse>
        </Form>
        <div className="nav-btn-box">
              <Button onClick={this.handleCancel}>取消</Button>
              <Button type="primary" onClick={this.next}>下一步</Button>
        </div>
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