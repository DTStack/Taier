import React, { Component } from "react";
import { Form, Radio, Input, Select, Button, Row, Collapse, Icon } from "antd";
import { formItemLayout} from "../../../../consts/index"


const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const Panel = Collapse.Panel;

export default class StepOne extends Component{
  constructor(){
    super();

    this.state = {
      databaseList: [],
      sortScopeList: [],
      downIcon: true,
    }
  }

  handleCancel = ()=>{

  }

  next = ()=>{
    const {form} = this.props;
    form.validateFields((err,values)=>{
      console.log(values)
      if(!err){
        this.props.handleNextStep();
      }
    })
  }



  render(){
    const { getFieldDecorator, getFieldsValue } = this.props.form;
    const { formData } = this.props;
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
                initialValue: formData.database || undefined
              })(
                  <Select>
                  <Option value="ss">ss</Option>

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
                initialValue: formData.table_name || undefined
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
          label="Sort Scope">
            {
              getFieldDecorator('sortScope',{
                rules: [
                  {required: true, message: 'Sort Scope不可为空'}
                ],
                initialValue: formData.sort_scope || undefined
              })(
                <Select>
                  <Option value="ss">ss</Option>
                  {
                    this.state.sortScopeList.map(o=>(
                      <Option key={o.id} value={o.id}/>
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
                initialValue: formData.block_size || undefined
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
                  initialValue: formData.MAJOR_COMPACTION_SIZE || undefined
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
                  initialValue: formData.AUTO_LOAD_MERGE || 0
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
                  initialValue:formData.COMPACTION_LEVEL_THRESHOLD || undefined
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
                  initialValue:formData.COMPACTION_PRESERVE_SEGMENTS || undefined
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
                  initialValue:formData.ALLOWED_COMPACTION_DAYS || undefined
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