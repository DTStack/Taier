import React, { Component } from 'react'
import {
    Row, Col, Modal, Tag, Icon,Tooltip,Table,
    message, Select, Collapse, Button,Radio
} from '_antd@2.13.11@antd'

import utils from 'utils'
import Api from '../../../api'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import TaskVersion from '../offline/taskVersion';

const Option = Select.Option;
const Panel = Collapse.Panel;
const RadioGroup = Radio.Group;
const { Column, ColumnGroup } = Table;

export default class InputPanel extends Component {

    state = {
        visibleAlterRes: false,
        resList: [],
    }

    callback = (key) => {
        console.log(key);
      }
      
    handleChange = (value) => {

    }

    handleSizeChange = (e) => {
        console.log(e.target.value);
      }
    

    outPutOrigin = () => {

        const data = [{
            key: '1',
            firstName: 'John',
            lastName: 'Brown',
            age: 32,
            address: 'New York No. 1 Lake Park',
          }, {
            key: '2',
            firstName: 'Jim',
            lastName: 'Green',
            age: 42,
            address: 'London No. 1 Lake Park',
          }];
        const content = <Row className="title-content">
            <Col style={{marginBottom: 20}}>
                <Row gutter={16}>
                    <Col span="6" ><span className="left-type"> 类型 : </span></Col>
                    <Col span="18" >
                        <Select defaultValue="lucy" className="right-select"  onChange={this.handleChange}>
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
                        <Select defaultValue="lucy" className="right-select"  onChange={this.handleChange}>
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
                        <Select defaultValue="lucy" className="right-select"  onChange={this.handleChange}>
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
                        <Select defaultValue="lucy" className="right-select"  onChange={this.handleChange}>
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
                        <Radio.Group  value={1} className="right-select" onChange={this.handleSizeChange}>
                            <Radio.Button value={1}>键值模式</Radio.Button>
                            <Radio.Button value={2}>脚本模式</Radio.Button>
                        </Radio.Group>
                    </Col>
                </Row>
            </Col>
            <Col style={{marginBottom: 20}}>
                <Table dataSource={data}>
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
            </Col>
            <Col style={{marginBottom: 20}}>
                <Row gutter={16}>
                    <Col span="6" ><span className="left-type"> 时间特征 : </span></Col>
                    <Col span="18" >
                        <RadioGroup className="right-select"  style={{marginTop: 5}} onChange={this.handleChange} value={this.state.value}>
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
                        <Select defaultValue="lucy" className="right-select"  onChange={this.handleChange}>
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
                        <Select defaultValue="lucy" className="right-select"  onChange={this.handleChange}>
                            <Option value="jack">Jack</Option>
                            <Option value="lucy">Lucy</Option>
                            <Option value="disabled" disabled>Disabled</Option>
                            <Option value="Yiminghe">yiminghe</Option>
                        </Select>
                    </Col>
                </Row>
            </Col>
        </Row>
        return content;
    }

    render() {
        const text = `
            A dog is a type of domesticated animal.
            Known for its loyalty and faithfulness,
            it can be found as a welcome guest in many households across the world.
            `;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse defaultActiveKey={['1']} onChange={this.callback} className="input-panel">
                    <Panel header=" 1" key="1">
                        {this.outPutOrigin()}
                    </Panel>
                    <Panel header="This is panel header 2" key="2">
                    <p>{text}</p>
                    </Panel>
                    <Panel header="This is panel header 3" key="3" disabled>
                    <p>{text}</p>
                    </Panel>
                </Collapse>
                <Button className="stream-btn"><Icon type="plus" /><span> 添加输入</span></Button>
            </div>
        )
    }
}
