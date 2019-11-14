/* eslint-disable @typescript-eslint/camelcase */
import * as React from 'react';
import { Form, Tabs, Button, Input, message } from 'antd';
import { formItemLayout } from './index';
import { MemorySetting as BaseMemorySetting, ChooseModal as BaseChooseModal } from './typeChange';
import { isEmpty, cloneDeep, debounce } from 'lodash';
import api from '../../../../../../api/experiment';
import { INPUT_TYPE } from '../../../../../../consts';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
/* 选择字段弹出框 */
class ChooseModal extends BaseChooseModal {
    warpClassName: any;
    disabledType: any;
    constructor (props: any) {
        super(props);
        this.state = {
            sourceData: []
        }
        this.warpClassName = props.wrapContanier || '.chooseWrap';
    }
    initTargetKeys = () => {
        const { data, transferField } = this.props;
        const { backupSource } = this.state;
        const chooseData = data || [];
        const targetKeys = chooseData.map((item: any) => {
            return item.key;
        });
        const sourceData = cloneDeep(backupSource);
        sourceData.forEach((item: any) => {
            if (targetKeys.findIndex((o: any) => o === item.key) > -1) {
                item.type = transferField;
            }
        });
        this.setState({
            targetKeys,
            sourceData
        });
    }
    getSourceData = () => {
        const { currentTab, componentId } = this.props;
        const targetEdges = currentTab.graphData && currentTab.graphData.filter((o: any) => {
            return o.edge && o.target.data.id == componentId
        })
        if (targetEdges.length) {
            const targetEdge = targetEdges.find((o: any) => o.outputType === INPUT_TYPE.PREDICT_INPUT_DATA);
            if (!targetEdge) return;
            this.setState({
                loading: true
            });
            api.getInputTableColumns({ taskId: componentId, inputType: targetEdge.inputType }).then((res: any) => {
                if (res.code === 1) {
                    let sourceData: any = [];
                    for (const key in res.data) {
                        if (res.data.hasOwnProperty(key)) {
                            const element = res.data[key];
                            if (this.disabledType) {
                                sourceData.push({
                                    key,
                                    type: element,
                                    disabled: element === this.disabledType
                                })
                            } else {
                                sourceData.push({
                                    key,
                                    type: element
                                })
                            }
                        }
                    }
                    this.setState({
                        sourceData,
                        backupSource: cloneDeep(sourceData)
                    }, () => {
                        this.initTargetKeys();
                    })
                }
                this.setState({
                    loading: false
                })
            })
        }
    }
}
/* 字段设置 */
class FieldSetting extends React.PureComponent<any, any> {
    state: any = {
        chooseModalVisible: false,
        labelModalVisible: false
    }
    handleChoose = (flag: any) => {
        if (flag === 'append') {
            this.setState({
                labelModalVisible: true
            })
        } else {
            this.setState({
                chooseModalVisible: true
            });
        }
    }
    handelOk = (key: any, targetObjects: any) => {
        this.props.handleSaveComponent(key, targetObjects);
    }
    handleCancel = () => {
        this.setState({
            chooseModalVisible: false,
            labelModalVisible: false
        });
    }
    getBtnContent (data: any) {
        return (isEmpty(data) || data.length === 0) ? '选择字段' : `已选择${data.length}个字段`;
    }
    render () {
        const { chooseModalVisible, labelModalVisible } = this.state;
        const { getFieldDecorator } = this.props.form;
        const { data, currentTab, componentId } = this.props;
        const btnStyle: any = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        return (
            <Form className="params-form">
                <FormItem
                    label={<div style={{ display: 'inline-block' }}>特征类<span className="supplementary">默认全选</span></div>}
                    colon={false}
                    required
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={() => this.handleChoose('col')}>{this.getBtnContent(data.col)}</Button>
                </FormItem>
                <FormItem
                    label={<div style={{ display: 'inline-block' }}>原样输出列<span className="supplementary">推荐添加label列，方便评估</span></div>}
                    colon={false}
                    required
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={() => this.handleChoose('append')}>{this.getBtnContent(data.append)}</Button>
                </FormItem>
                <FormItem
                    colon={false}
                    label='输出结果列名'
                    {...formItemLayout}
                >
                    {getFieldDecorator('result_col', {
                        rules: [
                            { required: true, message: '请输入输出结果列名' },
                            {
                                pattern: /^(\w){0,}$/,
                                message: '表名称只能由字母、数字、下划线组成!'
                            }
                        ]
                    })(
                        <Input placeholder="请输入输出结果列名" />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='输出分数列名'
                    {...formItemLayout}
                >
                    {getFieldDecorator('score_col', {
                        rules: [
                            { required: true, message: '请输入输出分数列名' },
                            {
                                pattern: /^(\w){0,}$/,
                                message: '表名称只能由字母、数字、下划线组成!'
                            }
                        ]
                    })(
                        <Input placeholder="请输入输出分数列名" />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='输出详细列名'
                    {...formItemLayout}
                >
                    {getFieldDecorator('detail_col', {
                        rules: [
                            { required: true, message: '请输入输出详细列名' },
                            {
                                pattern: /^(\w){0,}$/,
                                message: '表名称只能由字母、数字、下划线组成!'
                            }
                        ]
                    })(
                        <Input placeholder="请输入输出详细列名" />
                    )}
                </FormItem>
                <div className="chooseWrap">
                    <ChooseModal
                        currentTab={currentTab}
                        componentId={componentId}
                        data={data.col}
                        transferField='double'
                        visible={chooseModalVisible}
                        onOK={(value: any) => this.handelOk('col', value)}
                        onCancel={this.handleCancel} />
                </div>
                <div className="labelWrap">
                    <ChooseModal
                        currentTab={currentTab}
                        componentId={componentId}
                        wrapContanier='.labelWrap'
                        data={data.append}
                        transferField='double'
                        visible={labelModalVisible}
                        onOK={(value: any) => this.handelOk('append', value)}
                        onCancel={this.handleCancel} />
                </div>
            </Form>
        )
    }
}
/* 内存设置 */
class MemorySetting extends BaseMemorySetting {
    constructor (props: any) {
        super(props)
    }
}
/* main页面 */
class DataPredict extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    /**
     * 统一处理保存
     */
    handleSaveComponent = (field: any, filedValue: any) => {
        const { data, currentTab, componentId, changeContent } = this.props;
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            predictComponent: {
                ...data
            }
        }
        if (field) {
            params.predictComponent[field] = filedValue
        }
        api.addOrUpdateTask(params).then((res: any) => {
            if (res.code == 1) {
                currentComponentData.data = { ...params, ...res.data };
                changeContent({}, currentTab);
            } else {
                message.warning('保存失败');
            }
        })
    }
    render () {
        const { data, currentTab, componentId } = this.props;
        const WrapFieldSetting = Form.create({
            mapPropsToFields: (props: any) => {
                const { data } = props;
                /* eslint-disable */
                const values: any = {
                    result_col: { value: data.result_col },
                    score_col: { value: data.score_col },
                    detail_col: { value: data.detail_col }
                }
                /* eslint-enable */
                return values;
            },
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.errors && !element.validating && !element.dirty) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            }
        })(FieldSetting);
        const WrapMemorySetting = Form.create({
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.errors && !element.validating && !element.dirty) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            },
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    workerMemory: { value: data.workerMemory },
                    workerCores: { value: data.workerCores }
                }
                return values;
            }
        })(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="字段设置" key="1">
                    <WrapFieldSetting data={data} handleSaveComponent={this.handleSaveComponent} currentTab={currentTab} componentId={componentId} />
                </TabPane>
                <TabPane tab="内存设置" key="2">
                    <WrapMemorySetting data={data} handleSaveComponent={this.handleSaveComponent} />
                </TabPane>
            </Tabs>
        );
    }
}

export default DataPredict;
