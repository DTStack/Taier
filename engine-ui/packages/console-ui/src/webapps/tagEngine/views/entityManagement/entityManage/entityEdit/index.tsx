import * as React from 'react';
import { hashHistory } from 'react-router';
import { Steps, Button, message as Message } from 'antd';

import BaseForm from './baseForm';
import DimensionInfor from './dimensionInfor';
import AtomicLabel from './atomicLabel';
import FinishPart from './finishPart';
import Breadcrumb from '../../../../components/breadcrumb';
import './style.scss';
import { isEmpty, get } from 'lodash';
import { API } from '../../../../api/apiMap';
const Step = Steps.Step;

interface IProps {
    location: any;
}

interface IState {
    current: number;
    baseFormVal: any;
    dimensionInfor: any[];
    atomicLabelData: any[];

    dsOptions: any[];
    tableOptions: any[];
    tableColOptions: any[];
    attrTypeOptions: any[];
}

export default class EntityEdit extends React.Component<IProps, IState> {
    state: IState = {
        current: 0,
        baseFormVal: {},
        dimensionInfor: [],
        atomicLabelData: [],

        dsOptions: [],
        tableOptions: [],
        tableColOptions: [],
        attrTypeOptions: []
    }

    baseFormRef: any = null;

    componentDidMount () {
        let id = get(this.props.location, 'state.id');
        this.selectDataSource();
        this.getLabelType();
        if (id) {
            let values = get(this.props.location, 'state');
            this.setState({
                baseFormVal: values || {}
            })
            this.getDataTableList(false, values.dataSourceId);
            this.getColumnList(false, values.dataSourceId, values.dataSourceTable, id);
        }
    }

    selectDataSource = () => {
        API.selectDataSource().then((res: any) => {
            const { data = [], code } = res;
            if (code === 1) {
                this.setState({
                    dsOptions: data.map(item => {
                        return { label: item.dataName, value: item.id };
                    })
                });
            }
        })
    }

    getLabelType = () => {
        API.getLabelType().then((res: any) => {
            const { data = [], code } = res;
            if (code === 1) {
                this.setState({
                    attrTypeOptions: data.map(item => {
                        return { label: item.desc, value: item.val };
                    })
                });
            }
        })
    }

    getDataTableList = (setEmpty, id) => {
        if (setEmpty) {
            this.setState({
                tableOptions: []
            })
            return;
        }
        API.getDataTableList({
            dataSourceId: id
        }).then((res: any) => {
            const { data = [], code } = res;
            if (code === 1) {
                let tableOptions = data.map(item => {
                    return { label: item, value: item }
                })
                this.setState({
                    tableOptions
                });
            }
        })
    }

    getColumnList = (setEmpty, dataSourceId, index, entityId?: any) => {
        if (setEmpty) {
            this.setState({
                tableColOptions: []
            })
            return;
        }
        API.getColumnList({
            dataSourceId,
            index,
            entityId: entityId || undefined
        }).then((res: any) => {
            const { data = [], code } = res;
            if (code === 1) {
                this.setState({
                    tableColOptions: data.map(item => {
                        return { label: item.entityAttr, value: item.entityAttr };
                    }),
                    dimensionInfor: data
                });
            }
        })
    }

    prev = () => {
        const { current } = this.state;
        this.setState({
            current: current - 1
        })
    }

    next = () => {
        const { current, dimensionInfor, baseFormVal } = this.state;
        if (current == 0) {
            this.baseFormRef.props.form.validateFields((err: any, values: any) => {
                if (!err) {
                    let resultBV = { ...values };
                    if (baseFormVal.id) {
                        resultBV.id = baseFormVal.id;
                    }
                    this.setState({
                        baseFormVal: resultBV,
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
        const { current, baseFormVal, dimensionInfor, atomicLabelData, dsOptions, tableOptions, tableColOptions, attrTypeOptions } = this.state;
        const steps = [{
            title: '编辑基础信息',
            content: <BaseForm
                key={current}
                infor={baseFormVal}
                dsOptions={dsOptions}
                tableOptions={tableOptions}
                tableColOptions={tableColOptions}
                getDataTableList={this.getDataTableList}
                getColumnList={this.getColumnList}
                wrappedComponentRef={(ref: any) => { this.baseFormRef = ref; }}
            />
        }, {
            title: '设置维度信息',
            content: <DimensionInfor
                handleChange={this.handleDimensionDataChange}
                infor={dimensionInfor}
                baseInfor={baseFormVal}
                attrTypeOptions={attrTypeOptions}
            />
        }, {
            title: '生成原子标签',
            content: <AtomicLabel
                infor={atomicLabelData}
                baseInfor={baseFormVal}
                handleChange={this.handleAtomicLabelDataChange}
            />
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
