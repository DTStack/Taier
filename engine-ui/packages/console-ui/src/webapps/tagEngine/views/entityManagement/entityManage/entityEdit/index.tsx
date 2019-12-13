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
    alreadyAtomTagAtrrs: any[];

    dsOptions: any[];
    tableOptions: any[];
    tableColOptions: any[];
    attrTypeOptions: any[];
    attrTypeMap: any;
}

export default class EntityEdit extends React.Component<IProps, IState> {
    state: IState = {
        current: 0,
        baseFormVal: {},
        dimensionInfor: [],
        atomicLabelData: [],
        alreadyAtomTagAtrrs: [],

        dsOptions: [],
        tableOptions: [],
        tableColOptions: [],
        attrTypeOptions: [],
        attrTypeMap: {}
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
                    }),
                    attrTypeMap: data.reduce((pre, curr) => {
                        return {
                            ...pre,
                            [curr.val]: curr.desc
                        }
                    }, {})
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
                let tableColOptions = [];
                let alreadyAtomTagAtrrs = [];
                data.forEach(item => {
                    tableColOptions.push({ label: item.entityAttr, value: item.entityAttr });
                    if (item.isAtomTag) {
                        alreadyAtomTagAtrrs.push(item.entityAttr);
                    }
                })
                this.setState({
                    tableColOptions,
                    dimensionInfor: data,
                    alreadyAtomTagAtrrs
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
        const { current, dimensionInfor, baseFormVal, attrTypeOptions } = this.state;
        if (current == 0) {
            this.baseFormRef.props.form.validateFields((err: any, values: any) => {
                if (!err) {
                    let resultBV = { ...values };
                    let newDimensionInfor: any[] = [];
                    if (baseFormVal.id) {
                        resultBV.id = baseFormVal.id;
                        let newArrs: any[] = [];
                        let oldArrs: any[] = [];
                        let keyArr: any = {};
                        dimensionInfor.forEach(item => {
                            let newItem = {
                                ...item,
                                dataType: item.dataType || (attrTypeOptions[0] && attrTypeOptions[0].value),
                                isAtomTag: (!item.id || item.isAtomTag) ? Boolean(true) : Boolean(false),
                                isPrimaryKey: item.entityAttr == resultBV.entityPrimaryKey
                            };
                            if (item.id) {
                                if (newItem.isPrimaryKey) {
                                    keyArr = newItem;
                                } else {
                                    oldArrs.push(newItem);
                                }
                            } else {
                                newArrs.push(newItem);
                            }
                        })
                        newDimensionInfor = [
                            ...newArrs,
                            keyArr,
                            ...oldArrs
                        ]
                    } else {
                        dimensionInfor.forEach(item => {
                            let newItem = {
                                ...item,
                                dataType: item.dataType || (attrTypeOptions[0] && attrTypeOptions[0].value),
                                isAtomTag: true,
                                isPrimaryKey: item.entityAttr == resultBV.entityPrimaryKey
                            };
                            if (newItem.isPrimaryKey) {
                                newDimensionInfor.unshift(newItem);
                            } else {
                                newDimensionInfor.push(newItem);
                            }
                        })
                    }
                    this.setState({
                        baseFormVal: resultBV,
                        current: current + 1,
                        dimensionInfor: newDimensionInfor
                    })
                }
            })
        } else if (current == 1) {
            let atomicLabelData: any[] = [];
            let newDimensionInfor: any[] = [];
            dimensionInfor.forEach((item: any) => {
                let entityAttrCn = isEmpty(item.entityAttrCn) ? item.entityAttr : item.entityAttrCn;
                if (item.isAtomTag) {
                    atomicLabelData.push({
                        entityAttr: item.entityAttr,
                        tagName: item.tagName || entityAttrCn.slice(0, 80),
                        dimensionName: entityAttrCn,
                        type: item.dataType,
                        labelNum: item.tagValueCount,
                        tagDesc: item.tagDesc || '',
                        tagId: item.tagId || undefined,
                        tagDictId: item.tagDictId || undefined
                    })
                }
                newDimensionInfor.push({
                    ...item,
                    entityAttrCn
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
            atomicLabelData: [...data]
        })
    }

    handleSave = () => {
        const { baseFormVal, dimensionInfor, atomicLabelData, current } = this.state;
        let labelNameHasEmpty = atomicLabelData.findIndex((item: any) => { return isEmpty(item.tagName) });
        if (labelNameHasEmpty != -1) {
            Message.warning('标签名称不可空！');
        } else {
            let params: any = {
                ...baseFormVal,
                tagParamList: dimensionInfor.map(item => {
                    let tagInfor = {};
                    let tagItem = atomicLabelData.find((ele: any) => { return ele.entityAttr == item.entityAttr });
                    if (tagItem) {
                        tagInfor = {
                            tagName: tagItem.tagName,
                            tagDictId: tagItem.tagDictId || undefined,
                            tagDictParam: tagItem.tagDictParam || undefined,
                            tagDesc: tagItem.tagDesc || '',
                            tagId: tagItem.tagId || undefined
                        }
                    }
                    return {
                        ...tagInfor,
                        entityAttrId: item.id || undefined,
                        entityAttr: item.entityAttr,
                        entityAttrCn: item.entityAttrCn,
                        attrValueCount: item.tagValueCount,
                        isPrimaryKey: item.isPrimaryKey,
                        isMultipleValue: item.isMultipleValue,
                        dataType: item.dataType
                    }
                })
            }
            API.saveEntity(params).then((res: any) => {
                const { code } = res;
                if (code === 1) {
                    Message.success(get(baseFormVal, 'id') ? '修改成功！' : '新增成功！');
                    this.setState({
                        current: current + 1
                    });
                }
            })
        }
    }

    handleBack = () => {
        hashHistory.goBack();
    }

    render () {
        const { current, baseFormVal, dimensionInfor, atomicLabelData, dsOptions, tableOptions, tableColOptions, attrTypeOptions, attrTypeMap, alreadyAtomTagAtrrs } = this.state;
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
                alreadyAtomTagAtrrs={alreadyAtomTagAtrrs}
                attrTypeOptions={attrTypeOptions}
            />
        }, {
            title: '生成原子标签',
            content: <AtomicLabel
                infor={atomicLabelData}
                baseInfor={baseFormVal}
                handleChange={this.handleAtomicLabelDataChange}
                attrTypeMap={attrTypeMap}
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
                name: get(baseFormVal, 'id') ? '编辑实体' : '新增实体'
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
