import React, { Component } from 'react'
import {
    Row, Col, Modal, Tag, Icon,Tooltip,Table,
    message, Select, Collapse, Button,Radio,Popover
} from '_antd@2.13.11@antd'

import utils from 'utils'
import Api from '../../../api'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import TaskVersion from '../offline/taskVersion';

const Option = Select.Option;
const Panel = Collapse.Panel;
const RadioGroup = Radio.Group;
const { Column, ColumnGroup } = Table;

class outPutOrigin extends Component {

    render(){
        const data = [{
            key: '1',
            firstName: 'John',
            lastName: 'Brown',
            age: 32,
            address: 'New York No. 1 Lake Park',
        }];
        const { handleInputChange,index } = this.props;
        return (
            <Row className="title-content">
                <Col style={{marginBottom: 20}}>
                    <Row gutter={16}>
                        <Col span="6" ><span className="left-type"> 类型 : </span></Col>
                        <Col span="18" >
                            <Select defaultValue="lucy" className="right-select" onChange={(v)=>{handleInputChange("type",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" disabled>Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        </Col>
                    </Row>
                </Col>
                <Col style={{marginBottom: 20}}>
                    <Row gutter={16}>
                        <Col span="6" ><span className="left-type"> 数据源 : </span></Col>
                        <Col span="18" >
                            <Select defaultValue="lucy" className="right-select" onChange={(v)=>{handleInputChange("dataOrigin",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" disabled>Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        </Col>
                    </Row>
                </Col>
                <Col style={{marginBottom: 20}}>
                    <Row gutter={16}>
                        <Col span="6" ><span className="left-type"> Topic : </span></Col>
                        <Col span="18" >
                            <Select defaultValue="lucy" className="right-select" onChange={(v)=>{handleInputChange("topic",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" disabled>Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        </Col>
                    </Row>
                </Col>
                <Col style={{marginBottom: 20}}>
                    <Row gutter={16}>
                        <Col span="6" > 
                            <Tooltip title="该表是kafka中的topic映射而成，可以以SQL的方式使用它。">
                                <span className="left-type"> Table <Icon type="question-circle-o" /> : </span>
                            </Tooltip>
                        </Col>
                        <Col span="18" >
                            <Select defaultValue="lucy" className="right-select" onChange={(v)=>{handleInputChange("table",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" disabled>Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        </Col>
                    </Row>
                </Col>
                <Col style={{marginBottom: 20}}>
                    <Row gutter={16}>
                        <Col span="6" ><span className="left-type"> 字段 : </span></Col>
                        <Col span="18" >
                            <Radio.Group   defaultValue={1} className="right-select" onChange={(v)=>{handleInputChange("model",index,v)}}>
                                <Radio.Button value={1}>键值模式</Radio.Button>
                                <Radio.Button value={2}>脚本模式</Radio.Button>
                            </Radio.Group>
                        </Col>
                    </Row>
                </Col>
                <Col style={{marginBottom: 20}}>
                    <Table dataSource={data} pagination={false} >
                        <Column
                            title="First Name"
                            dataIndex="firstName"
                            key="firstName"
                        />
                        <Column
                            title="Last Name"
                            dataIndex="lastName"
                            key="lastName"
                        />
                    </Table>
                    <div style={{padding: "0 20"}}>
                        <Button className="stream-btn" type="dashed" style={{borderRadius: 5}} onClick={()=>{handleInputChange("column",index)}}>
                            <Icon type="plus" /><span> 添加输入</span>
                        </Button>
                    </div>
                </Col>
                <Col style={{marginBottom: 20}}>
                    <Row gutter={16}>
                        <Col span="6" ><span className="left-type"> 时间特征 : </span></Col>
                        <Col span="18" >
                            <RadioGroup  defaultValue={1} className="right-select" onChange={(v)=>{handleInputChange("timeType",index,v)}}>
                                <Radio value={1}>ProcTime</Radio>
                                <Radio value={2}>EventTime</Radio>
                            </RadioGroup>
                        </Col>
                    </Row>
                </Col>
                <Col style={{marginBottom: 20}}>
                    <Row gutter={16}>
                        <Col span="6" ><span className="left-type"> 时间列 : </span></Col>
                        <Col span="18" >
                            <Select defaultValue="lucy" className="right-select" onChange={(v)=>{handleInputChange("timeColum",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" disabled>Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        </Col>
                    </Row>
                </Col>
                <Col>
                    <Row gutter={16}>
                        <Col span="6" ><span className="left-type"> 别名 : </span></Col>
                        <Col span="18" >
                            <Select defaultValue="lucy" className="right-select" onChange={(v)=>{handleInputChange("alias",index,v)}}>
                                <Option value="jack">Jack</Option>
                                <Option value="lucy">Lucy</Option>
                                <Option value="disabled" disabled>Disabled</Option>
                                <Option value="Yiminghe">yiminghe</Option>
                            </Select>
                        </Col>
                    </Row>
                </Col>
            </Row>
        )
    }
}

export default class InputPanel extends Component {

    state = {
        visibleAlterRes: false,
        tabTemplate: [],
        panelActiveKey: [],
        popoverVisible: [],
        panelColumn: [],
        groupButton: 1,
        radioGroup: 1,
    }

    changeInputTabs = (type,index) =>{
        let { tabTemplate, panelActiveKey, popoverVisible } = this.state;
        if(type==="add"){
            tabTemplate.push(outPutOrigin);
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex)
        }else{
            tabTemplate.splice(index,1);
            panelActiveKey = this.changeActiveKey(index);
            popoverVisible[index] = false;
        }
        this.setState({
            tabTemplate,
            panelActiveKey,
            popoverVisible
        })
    }

    changeActiveKey = (index) => {// 删除导致key改变,处理被改变key的值
        const {panelActiveKey} = this.state;
        const deleteActiveKey = `${index+1}`;
        const deleteActiveKeyIndex  = panelActiveKey.indexOf(deleteActiveKey);
        if(deleteActiveKeyIndex > -1){
            panelActiveKey.splice(deleteActiveKeyIndex,1)
        }
        return panelActiveKey.map(v=>{
            return Number(v) > Number(index) ? `${Number(v) -1}`: v
        });
    }

    handleActiveKey = (key) => {
        let { panelActiveKey } = this.state;
        panelActiveKey = key
        this.setState({
            panelActiveKey,
        })
    }
      
    handleInputChange = (type,index,value) => {
       const { panelColumn } = this.state;
        console.log('type,index,value',type,index,value);
        
       this.setState({
            panelColumn
       })
    }

    handleChangeRadio = (value) => {
        this.setState({
            radioGroup: value
        })
    }

    handleSizeChange = (e) => {
        console.log(e.target.value);
        this.setState({
            groupButton: e.target.value
        })
      }
    
    deletePopover = () => {

    }

    handlePopoverVisibleChange = (e,index,visible) => {
        let { popoverVisible } = this.state;
        popoverVisible[index] = visible;
        if (e) {
            e.stopPropagation();//阻止删除按钮点击后冒泡到panel
            if(visible){//只打开一个Popover提示
                popoverVisible = popoverVisible.map( (v,i) => {
                    return index == i ? true : false
                })
            }
        }
        this.setState({ popoverVisible });
    }

    panelHeader = (index) => {
        const { popoverVisible } = this.state;
        const popoverContent = <div className="input-panel-title">
            <div style={{padding: "8 0 12"}}> <Icon type="exclamation-circle" style={{color: "#faad14",}}/>  你确定要删除此输入源吗？</div>
            <div style={{textAlign: "right",padding: "0 0 8"}}>
                <Button style={{marginRight: 8}} size="small" onClick={()=>{this.handlePopoverVisibleChange(null,index,false)}}>取消</Button>
                <Button type="primary" size="small" onClick={()=>{this.changeInputTabs('delete',index)}}>确定</Button>
            </div>
        </div>
        return <div className="input-panel-title">
            <span>{` 输入源 ${index+1} (仅支持Json)`}</span>
            <Popover
                trigger="click"
                placement="topLeft"
                content={popoverContent}
                visible={popoverVisible[index]}
                onClick={(e)=>{this.handlePopoverVisibleChange(e,index,!popoverVisible[index])}}
            >
                <span className="title-icon input-panel-title" ><Icon type="delete" /></span>
            </Popover>
        </div>
    }

    render() {
        const { tabTemplate,panelActiveKey } = this.state;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse activeKey={panelActiveKey}  onChange={this.handleActiveKey} className="input-panel">
                    {
                        tabTemplate.map( (OutPutOrigin,index) => {
                            return  (
                                <Panel header={this.panelHeader(index)} key={index+1} style={{borderRadius: 5}}>
                                    <OutPutOrigin index={index} handleInputChange={this.handleInputChange}/>
                                </Panel>
                            )
                        })
                    }
                </Collapse>
                <Button className="stream-btn" onClick={()=> {this.changeInputTabs('add')}} style={{borderRadius: 5}}><Icon type="plus" /><span> 添加输入</span></Button>
            </div>
        )
    }
}
