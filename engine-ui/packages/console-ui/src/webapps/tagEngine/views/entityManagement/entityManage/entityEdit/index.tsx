import * as React from 'react';
import { hashHistory } from 'react-router';
import { Steps, Button, message as Message } from 'antd';

import BaseForm from './baseForm';
import DimensionInfor from './dimensionInfor';
import AtomicLabel from './atomicLabel';
import FinishPart from './finishPart';
import Breadcrumb from '../../../../components/breadcrumb';
import './style.scss';
import { isEmpty } from 'lodash';
const Step = Steps.Step;

interface IProps {
    location: any;
}

interface IState {
    current: number;
    baseFormVal: any;
    dimensionInfor: any[];
    atomicLabelData: any[];
}

export default class EntityEdit extends React.Component<IProps, IState> {
    state: IState = {
        current: 0,
        baseFormVal: {},
        dimensionInfor: [],
        atomicLabelData: []
    }

    baseFormRef: any = null;

    componentDidMount () {
        console.log('location:', this.props.location)
    }

    getDimensionData = (params: any) => {
        // TODO 根据上一表单信息请求 维度信息
        this.setState({
            dimensionInfor: [
                { id: '1', select: true, isKey: true, isMultiply: false, name: 'xxxxxxx', chName: 'xxx1', type: 'char', propertyNum: 200 },
                { id: '2', select: false, isKey: false, isMultiply: false, name: 'xxxxx', chName: 'xxx2', type: 'number', propertyNum: 100 },
                { id: '3', select: false, isKey: false, isMultiply: false, name: 'xxxxx', chName: 'xxx3', type: 'number', propertyNum: 30 }
            ]
        })
    }

    prev = () => {
        const { current } = this.state;
        this.setState({
            current: current - 1
        })
    }

    next = () => {
        const { current, dimensionInfor } = this.state;
        if (current == 0) {
            this.baseFormRef.props.form.validateFields((err: any, values: any) => {
                if (!err) {
                    this.getDimensionData(values);
                    this.setState({
                        baseFormVal: values,
                        current: current + 1
                    })
                }
            })
        } else if (current == 1) {
            let atomicLabelData: any[] = [];
            let newDimensionInfor: any[] = [];
            dimensionInfor.forEach((item: any) => {
                let chName = isEmpty(item.chName) ? item.name : item.chName;
                if (item.select) {
                    atomicLabelData.push({
                        labelName: chName,
                        dimensionName: chName,
                        type: item.type,
                        labelNum: item.propertyNum,
                        desc: ''
                    })
                }
                newDimensionInfor.push({
                    ...item,
                    chName
                })
            })
            this.setState({
                atomicLabelData,
                dimensionInfor: newDimensionInfor,
                current: current + 1
            })
        }
    }

    handleDimensionDataChange = (data: any) => {
        this.setState({
            dimensionInfor: data
        })
    }

    handleAtomicLabelDataChange = (data: any) => {
        this.setState({
            atomicLabelData: data
        })
    }

    handleSave = () => {
        const { baseFormVal, dimensionInfor, atomicLabelData, current } = this.state;
        console.log(baseFormVal, dimensionInfor, atomicLabelData);
        let labelNameHasEmpty = atomicLabelData.findIndex((item: any) => { return isEmpty(item.labelName) });
        if (labelNameHasEmpty != -1) {
            Message.warning('标签名称不可空！');
        } else {
            this.setState({
                current: current + 1
            })
        }
    }

    handleBack = () => {
        hashHistory.goBack();
    }

    render () {
        const { current, baseFormVal, dimensionInfor, atomicLabelData } = this.state;
        const steps = [{
            title: '编辑基础信息',
            content: <BaseForm key={current} infor={baseFormVal} wrappedComponentRef={(ref: any) => { this.baseFormRef = ref; }} />
        }, {
            title: '设置维度信息',
            content: <DimensionInfor handleChange={this.handleDimensionDataChange} infor={dimensionInfor} />
        }, {
            title: '生成原子标签',
            content: <AtomicLabel infor={atomicLabelData} handleChange={this.handleAtomicLabelDataChange} />
        }, {
            title: '完成',
            content: <FinishPart goBack={this.handleBack} />
        }];
        const breadcrumbNameMap = [
            {
                path: '/entityManage',
                name: '实体管理'
            },
            {
                path: '',
                name: '新增实体'
            }
        ];
        return (
            <div className="entity-edit">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <div className="entity_edit_content">
                    <div className="step_wrap">
                        <Steps current={current}>
                            {steps.map(item => <Step key={item.title} title={item.title} />)}
                        </Steps>
                    </div>
                    <div className="step_content">
                        {steps[current].content}
                        <div className="steps-action">
                            {current == 0 && <Button style={{ marginRight: 8 }}
                                onClick={() => this.handleBack()}
                            > 退出 </Button>}
                            {current > 0 && current < 3 &&
                                <Button style={{ marginRight: 8 }}
                                    onClick={() => this.prev()}
                                > 上一步 </Button>
                            }
                            {current < steps.length - 2 &&
                                <Button type="primary"
                                    onClick={() => this.next()}
                                > 下一步 </Button>
                            }
                            {current === steps.length - 2 && <Button type="primary"
                                onClick={this.handleSave}
                            > 保存 </Button>
                            }
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
