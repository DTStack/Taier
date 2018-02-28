import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Icon, Steps, Button, Form, Radio, Select, Input, Row, Col, Table, message } from 'antd';
import { dataCheckActions } from '../../../actions/dataCheck';
import '../../../styles/views/dataCheck.scss';

const Step = Steps.Step;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const mapStateToProps = state => {
    const { dataCheck } = state;
    return { dataCheck }
}

const mapDispatchToProps = dispatch => ({
    getCheckDetail(params) {
        dispatch(dataCheckActions.getCheckDetail(params));
    },
    editCheck(params) {
        dispatch(dataCheckActions.editCheck(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class DataCheckEdit extends Component {
    constructor(props) {
        super(props);
        const steps = [
            {
                title: '选择左侧表'
            }, {
                title: '选择右侧表'
            }, {
                title: '选择字段'
            }, {
                title: '执行配置'
            }
        ];
        this.state = {
            current: 0,
            steps: steps
        }
    }

    componentDidMount() {
        
    }

    next = () => {
        const current = this.state.current + 1;
        this.setState({ current });
    }

    prev = () => {
        const current = this.state.current - 1;
        this.setState({ current });
    }

    componentWillReceiveProps(nextProps) {
        
    }
 
    render() {
        const { dataCheck } = this.props;
        const formItemLayout = {
            labelCol: {
                xs: { span: 24 },
                sm: { span: 6 },
            },
            wrapperCol: {
                xs: { span: 24 },
                sm: { span: 12 },
            },
        };
        const { current, steps } = this.state;

        return (
            <div className="content check-setting">
                <Button>
                    <Link to="/dq/dataCheck">
                        <Icon type="rollback m-r-8" />返回
                    </Link>
                </Button>
                <h3>新建逐行校验</h3>
                <div className="batch-body">
                    <Steps current={current}>
                        { steps.map(item => <Step key={item.title} title={item.title}/>) }
                    </Steps>
                    <div className="steps-content">
                        { current == 0 && <StepOne ref="step1" formItemLayout={formItemLayout} {...this.props} />}
                        { current == 1 && <StepTwo ref="step2" formItemLayout={formItemLayout} {...this.props} />}
                        { current == 2 && <StepThree formItemLayout={formItemLayout} />}
                    </div>
                    <div className="steps-action">
                        {
                            current === 0 
                            && 
                            <Row>
                                <Button><Link to="/dq/dataCheck">取消</Link></Button>
                                <Button className="m-l-8" type="primary" onClick={ this.next } style={{ marginLeft: 8 }}>
                                    下一步
                                </Button>
                            </Row>
                        }
                        {
                            (current === 1 || current === 2)
                            && 
                            <Row>
                                <Button onClick={ this.prev }>上一步</Button>
                                <Button className="m-l-8" type="primary" onClick={ this.next }>下一步</Button>
                            </Row>
                        }
                        {
                            current === 3 
                            && 
                            <Row>
                                <Button onClick={ this.prev }>上一步</Button>
                                <Button className="m-l-8" type="primary" onClick={ this.reload }>继续添加</Button>
                                <Button className="m-l-8" type="primary"><Link to="/setting/dbcollectormanage">校验列表</Link></Button>
                            </Row>
                        }
                    </div>
                </div>
            </div>
        )
    }
}

class StepOne extends Component {
    constructor(props) {
        super(props);
        this.state = {
        }
    }
    
    componentDidMount() {

    }

    initColumns = () => {
        return [{
            title: '左侧表',
            dataIndex: 'originTableName',
            key: 'originTableName',
        }, {
            title: '分区',
            dataIndex: 'originPartitionColumn',
            key: 'originPartitionColumn',
        }, 
        {
            title: '右侧表',
            dataIndex: 'targetTableName',
            key: 'targetTableName',
        }, {
            title: '分区',
            dataIndex: 'targetPartitionColumn',
            key: 'targetPartitionColumn',
        }, {
            title: '校验结果',
            dataIndex: 'status',
            key: 'status',
            filters: [{
                text: '无差异',
                value: '0',
            }, {
                text: '有差异',
                value: '1',
            }, {
                text: '进行中',
                value: '2',
            }, {
                text: '未开始',
                value: '3',
            }],
            filterMultiple: false,
            onFilter: (value, record) => console.log(value,record),
        }, {
            title: '差异总数',
            dataIndex: 'diverseNum',
            key: 'diverseNum',
            sorter: true
        }, {
            title: '差异比例',
            dataIndex: 'diverseRatio',
            key: 'diverseRatio',
            sorter: true
        }, {
            title: '最近修改人',
            dataIndex: 'executeUserId',
            key: 'executeUserId',
        }, {
            title: '执行时间',
            dataIndex: 'executeTime',
            key: 'executeTime',
        }]
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = this.props.formItemLayout;

        return (
            <div className="steps-main">
                <Form>
                    <FormItem {...formItemLayout} label="选择数据源" style={{ marginTop: 24 }}>
                        {
                            getFieldDecorator('dbType', {
                                rules: [{ required: true }], initialValue: '1'
                            })(
                                <Select onChange={ this.handleDbTypeChange }>
                                    <Option value="1">MySQL</Option>
                                    <Option value="2">Oracle</Option>
                                    <Option value="3">SQL Server</Option>
                                </Select>
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="选择表" style={{ marginTop: 24 }}>
                        {
                            getFieldDecorator('originTableName', {
                                rules: [{ required: true }], initialValue: '1'
                            })(
                                <Select onChange={ this.handleDbTypeChange }>
                                    <Option value="1">DQ_test</Option>
                                    <Option value="2">DQ_Setting</Option>
                                    <Option value="3">DQ_page</Option>
                                </Select>
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="选择分区" style={{ marginBottom: 5 }}>
                        {
                            getFieldDecorator('originPartitionColumn', {
                                rules: [{ required: true, message: '分区' }]
                            })(
                                <Input placeholder="输入分区名称" />
                            )
                        }
                    </FormItem>
                    <Row className="table-view">
                        <Col span={12} offset={6}>
                            <p>分区支持系统函数，例如:</p>
                            <p dangerouslySetInnerHTML={{ __html: '${bdp.system.bizdate}，业务日期变量' }}></p>
                            <p>————此处需补充其他变量</p>
                            <p>如果分区还不存在，可以直接输入未来会存在的分区名，详细的操作请参考<a>《帮助文档》</a></p>
                            <Button>数据预览</Button>
                            <Table 
                                rowKey="id"
                                className="m-table"
                                columns={this.initColumns()} 
                                dataSource={[]}
                            />
                        </Col>
                    </Row>
                </Form>
            </div>
        )
    }
}
StepOne = Form.create()(StepOne);


class StepTwo extends Component {
    constructor(props) {
        super(props);
        this.state = {
            collect_type: '1',
            show_sql_modal: false,
            sql_text: '',
            incrementField: undefined
        };
    }

    componentDidMount() {
        // const { edit_info, db_info } = this.props.db_access;

        // 编辑任务
        // if (edit_info.querySql) {
        //     let new_params = Object.assign({}, db_info, {
        //         querySql: edit_info.querySql
        //     });
        //     this.props.getSqlResult(new_params);

        //     this.setState((prevState, props) => ({
        //         collect_type: edit_info.collectorType,
        //         incrementField: edit_info.incrementField
        //     }));

        //     this.props.form.setFieldsValue({
        //         collectorName: edit_info.collectorName,
        //         querySql: edit_info.querySql,
        //         uniqueFields: edit_info.uniqueFields,
        //         collectorFrequency: edit_info.collectorFrequency,
        //         appName: edit_info.appName,
        //         tag: edit_info.tag,
        //         keepType: edit_info.keepType
        //     });
        // }

        // this.props.getKeepType();
    }

    initColumns = () => {
        return [{
            title: '左侧表',
            dataIndex: 'originTableName',
            key: 'originTableName',
        }, {
            title: '分区',
            dataIndex: 'originPartitionColumn',
            key: 'originPartitionColumn',
        }, 
        {
            title: '右侧表',
            dataIndex: 'targetTableName',
            key: 'targetTableName',
        }, {
            title: '分区',
            dataIndex: 'targetPartitionColumn',
            key: 'targetPartitionColumn',
        }, {
            title: '校验结果',
            dataIndex: 'status',
            key: 'status',
            filters: [{
                text: '无差异',
                value: '0',
            }, {
                text: '有差异',
                value: '1',
            }, {
                text: '进行中',
                value: '2',
            }, {
                text: '未开始',
                value: '3',
            }],
            filterMultiple: false,
            onFilter: (value, record) => console.log(value,record),
        }, {
            title: '差异总数',
            dataIndex: 'diverseNum',
            key: 'diverseNum',
            sorter: true
        }, {
            title: '差异比例',
            dataIndex: 'diverseRatio',
            key: 'diverseRatio',
            sorter: true
        }, {
            title: '最近修改人',
            dataIndex: 'executeUserId',
            key: 'executeUserId',
        }, {
            title: '执行时间',
            dataIndex: 'executeTime',
            key: 'executeTime',
        }]
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { formItemLayout } = this.props;

        return (
            <div className="steps-main">
                <Form>
                    <FormItem {...formItemLayout} label="选择表" style={{ marginTop: 24 }}>
                        {
                            getFieldDecorator('targetTableName', {
                                rules: [{ required: true }], initialValue: '1'
                            })(
                                <Select style={{ width: 300 }} onChange={ this.handleDbTypeChange }>
                                    <Option value="1">DQ_test</Option>
                                    <Option value="2">DQ_Setting</Option>
                                    <Option value="3">DQ_page</Option>
                                </Select>
                            )
                        }
                    </FormItem>
                    <FormItem {...formItemLayout} label="选择分区" style={{ marginBottom: 5 }}>
                        {
                            getFieldDecorator('targetPartitionColumn', {
                                rules: [{ required: true, message: '分区' }]
                            })(
                                <Input style={{ width: 300 }} placeholder="输入分区名称" />
                            )
                        }
                    </FormItem>
                    <Row className="table-view">
                        <Col span={12} offset={6}>
                            <p>分区支持系统函数，例如:</p>
                            <p dangerouslySetInnerHTML={{ __html: '${bdp.system.bizdate}，业务日期变量' }}></p>
                            <p>————此处需补充其他变量</p>
                            <p>如果分区还不存在，可以直接输入未来会存在的分区名，详细的操作请参考<a>《帮助文档》</a></p>
                            <Button>数据预览</Button>
                            <Table 
                                rowKey="id"
                                className="m-table"
                                columns={this.initColumns()} 
                                dataSource={[]}
                            />
                        </Col>
                    </Row>
                </Form>
            </div>
        );
    }
}
StepTwo = Form.create()(StepTwo);


class StepThree extends Component {
    render() {
        return (
            <div className="steps-main">
                <Icon type="check-circle" style={{ fontSize: 24, color: '#00CC99' }} /><span style={{ fontSize: 22, color: '#00CC99', marginLeft: 8 }}>配置完成</span>
            </div>
        )
    }
}

class StepFour extends Component {
    render() {
        return (
            <div className="steps-main">
                <Icon type="check-circle" style={{ fontSize: 24, color: '#00CC99' }} /><span style={{ fontSize: 22, color: '#00CC99', marginLeft: 8 }}>配置完成</span>
            </div>
        )
    }
}
