import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import { Steps, Button, Icon } from 'antd';

import StepOne from './stepOne';
import StepTwo from './stepTwo';
import StepThree from './stepThree';
import StepFour from './stepFour';
import DiffSettingTable from './diffSettingTable';

import { dataCheckActions } from '../../../actions/dataCheck';
import { dataSourceActions } from '../../../actions/dataSource';
import DCApi from '../../../api/dataCheck';

import '../../../styles/views/dataCheck.scss';

const Step = Steps.Step;

const mapStateToProps = state => {
    const { dataCheck, dataSource } = state;
    return { dataCheck, dataSource }
}

const mapDispatchToProps = dispatch => ({
    getDataSourcesList(params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
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
    }
})

@connect(mapStateToProps, mapDispatchToProps)
export default class DataCheckEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            current: 0,
            editParams: {
                origin: {},
                target: {},
                setting: {},
                scheduleConf: '',
                executeType: 0,
                mappedPK: {},
                notifyVO: {},
            },
            editStatus: 'new'
        }
    }

    componentWillMount() {
        const { routeParams } = this.props;
        // this.props.getDataSourcesList();
        if (!isEmpty(routeParams)) {
            this.setState({ editStatus: 'edit' });
            // this.props.getCheckDetail({ verifyId: routeParams.verifyId });
            DCApi.getCheckDetail({ verifyId: routeParams.verifyId }).then((res) => {
                if (res.code === 1) {
                    this.setState({ 
                        editParams: { ...this.state.editParams, 
                            origin: res.data.origin,
                            target: res.data.target,
                            setting: res.data.setting,
                            scheduleConf: res.data.scheduleConf,
                            executeType: res.data.executeType,
                            mappedPK: res.data.mappedPK,
                            notifyVO: res.data.notifyVO
                        }
                    });
                }
            });
        }
        console.log(this,'edit')
    }

    componentDidMount() {
        
    }

    changeParams = (obj) => {
        let editParams = { ...this.state.editParams, ...obj };
        this.setState({ editParams });
        console.log(this,'editParams')
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
        this.setState({ current: value });
    }

    componentWillReceiveProps(nextProps) {
        
    }
 
    render() {
        const { current, editParams, editStatus } = this.state;
        const steps = [
            {
                title: '选择左侧表', content: <StepOne
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '选择右侧表', content: <StepTwo
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '选择字段', content: <StepThree
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
                />
            },
            {
                title: '执行配置', content: <StepFour
                    currentStep={current}
                    navToStep={this.navToStep}
                    {...this.props} 
                    editParams={editParams}
                    editStatus={editStatus}
                    changeParams={this.changeParams}
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

