import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Icon, Steps, Button, Form, Radio, Select, Input, Row, Col, Table, DatePicker, TimePicker, message } from 'antd';
// import Keymap from './keymap';
import StepOne from './stepOne';
import StepTwo from './stepTwo';
import StepThree from './stepThree';
import StepFour from './stepFour';
import DiffSettingTable from './diffSettingTable';
import { dataCheckActions } from '../../../actions/dataCheck';
import { dataSourceActions } from '../../../actions/dataSource';
import '../../../styles/views/dataCheck.scss';

const Step = Steps.Step;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const mapStateToProps = state => {
    const { dataCheck, dataSource } = state;
    return { dataCheck, dataSource }
}

const mapDispatchToProps = dispatch => ({
    getDataSourcesType(params) {
        dispatch(dataSourceActions.getDataSourcesType(params));
    },
    getDataSourcesTable(params) {
        dispatch(dataSourceActions.getDataSourcesTable(params));
    },
    getDataSourcesPart(params) {
        dispatch(dataSourceActions.getDataSourcesPart(params));
    },
    getDataSourcesPreview(params) {
        dispatch(dataSourceActions.getDataSourcesPreview(params));
    },
    getCheckDetail(params) {
        dispatch(dataCheckActions.getCheckDetail(params));
    },
    editCheck(params) {
        dispatch(dataCheckActions.editCheck(params));
    },
    changeParams(params) {
        dispatch(dataCheckActions.changeParams(params));
    }
})

@connect(mapStateToProps, mapDispatchToProps)
export default class DataCheckEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            current: 0,
            params: {
                origin: {
                    dataSourceId: undefined,
                    table: '',
                    column: '',
                },
                target: {
                    dataSourceId: undefined,
                    table: '',
                    column: '',
                },
                setting: {
                    matchNull: undefined,
                    diverseNum: undefined,
                    matchCase: '',
                    diverseAbsolute: ''
                },
                scheduleConf: '',
                executeType: '',
                mappedPK: {},
                notifyVO: {
                    receivers: '',
                    sendTypes: '',
                    bizType: '',
                    status: '',
                },
                sourceType: undefined,
                originTable: '',
                originPart: '',
                originColumn: [],
                targetTable: '',
                targetPart: '',
                targetColumn: []
            }
        }
    }

    componentDidMount() {
        this.props.getDataSourcesType();
    }

    // changeParams = (obj) => {
    //     let params = Object.assign({}, this.state.params, obj);
    //     this.setState({ params });
    //     console.log(this)
    // }

    changeParams = (obj) => {
        let params = {...this.state.params, ...obj};
        this.setState({ params });
        console.log(this,'params')
    }

    next = () => {
        const current = this.state.current + 1;
        this.setState({ current });
    }

    prev = () => {
        const current = this.state.current - 1;
        this.setState({ current });
    }

    navToStep = (value) => {
        console.log(value)
        this.setState({ current: value });
    }

    componentWillReceiveProps(nextProps) {
        
    }
 
    render() {
        const { dataCheck } = this.props;
        const { current } = this.state;

        const steps = [
            {
                title: '选择左侧表', content: <StepOne
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                />
            },
            {
                title: '选择右侧表', content: <StepTwo
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                />
            },
            {
                title: '选择字段', content: <StepThree
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                />
            },
            {
                title: '执行配置', content: <StepFour
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                />
            }
        ];
        return (
            <div className="inner-container check-setting">
                <Button>
                    <Link to="/dq/dataCheck">
                        <Icon type="rollback m-r-8" />返回
                    </Link>
                </Button>

                <h3>新建逐行校验</h3>

                <div className="batch-body">
                    <Steps current={current}>
                        { steps.map(item => <Step key={item.title} title={item.title} />) }
                    </Steps>
                    { steps[current].content }
                </div>
            </div>
        )
    }
}

