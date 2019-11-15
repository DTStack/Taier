import * as React from 'react';
import { Tabs, Form, Button, Select, message } from 'antd';
import { MemorySetting as BaseMemorySetting, ChooseModal as BaseChooseModal } from './typeChange';
import { formItemLayout } from './index';
import { isEmpty, cloneDeep, debounce, get } from 'lodash';
import api from '../../../../../../api/experiment';
import { TASK_ENUM, COMPONENT_TYPE } from '../../../../../../consts';
import HelpDoc from '../../../../../../components/helpDoc';
import { renderNumberFormItem } from './helper';

const TabPane = Tabs.TabPane;
const Option = Select.Option;
const FormItem = Form.Item;
/* 选择字段弹出框 */
class ChooseModal extends BaseChooseModal {
    constructor (props: any) {
        super(props);
    }
    // 重写钩子函数，实现由父级更新soureceData
    static getDerivedStateFromProps (nextProps: any, prevState: any) {
        if (nextProps.sourceData.length != 0) {
            return {
                sourceData: nextProps.sourceData,
                loading: nextProps.loading
            }
        } else {
            return null;
        }
    }
    componentDidUpdate (prevProps: any, prevState: any, snapshot: any) {
        if (prevState.sourceData.length != this.state.sourceData.length) {
            this.initTargetKeys();
        }
    }
    initTargetKeys = () => {
        // 继承重写该方法
        const { data, transferField } = this.props;
        const { backupSource } = this.state;
        const chooseData = data || [];
        const targetKeys = chooseData.map((item: any) => {
            return item.key;
        });
        const sourceData = cloneDeep(backupSource);
        sourceData.forEach((item: any) => {
            if (targetKeys.findIndex((o: any) => o === item.key) > -1) {
                item.type = transferField || item.type;
            }
        });
        this.setState({
            targetKeys,
            sourceData
        });
    }
}
/* 参数设置 */
class ParamSetting extends React.PureComponent<any, any> {
    state: any = {
    }
    handleChange = (id: string, value: any) => {
        this.props.form.validateFieldsAndScroll([id], (err: any, values: any) => {
            if (!err) {
                this.props.handleSaveComponent(id, value);
            }
        });
    }
    handleSubmit (name: any, value: any) {
        this.props.form.validateFieldsAndScroll([name], (err: any, values: any) => {
            if (!err) {
                this.props.handleSaveComponent(name, value);
            }
        });
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        return (
            <Form className="params-form">
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '聚类数',
                    key: 'nClusters',
                    max: 1000,
                    step: 1,
                    initialValue: 10,
                    isInt: true
                }, getFieldDecorator)}
                <FormItem
                    label='距离度量方式'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('distance', {
                        initialValue: 'Euclidean',
                        rules: [{ required: false }]
                    })(
                        <Select placeholder="请选择距离度量方式" onSelect={this.handleChange.bind(this, 'distance')}>
                            <Option key={'euclidean'} value={'euclidean'}>Euclidean</Option>
                            {/* 后端以及算法同学暂不支持, 暂先屏蔽 */}
                            {/* <Option key={'cosine'} value={'cosine'}>Cosine</Option>
                            <Option key={'cityblock'} value={'cityblock'}>Cityblock</Option> */}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    label='质心初始化方法'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('init', {
                        initialValue: 'Random',
                        rules: [{ required: false }]
                    })(
                        <Select placeholder="请选择质心初始化方法" onSelect={this.handleChange.bind(this, 'init')}>
                            <Option key={'random'} value={'random'}>Random</Option>
                            <Option key={'first-k'} value={'first-k'}>First K</Option>
                            <Option key={'uniform'} value={'uniform'}>Uniform</Option>
                            <Option key={'k-means++'} value={'k-means++'}>K-means++</Option>
                            <Option key={'specify'} value={'specify'}>使用初始质心表</Option>
                        </Select>
                    )}
                </FormItem>
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '最大迭代次数',
                    key: 'maxIter',
                    max: 1000,
                    step: 1,
                    initialValue: 100,
                    isInt: true
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '收敛标准',
                    key: 'tol',
                    max: null,
                    step: 0.1,
                    initialValue: 0.1
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '初始随机种子',
                    key: 'randomState',
                    max: 10000,
                    step: 1,
                    initialValue: 1,
                    isInt: true
                }, getFieldDecorator)}
            </Form>
        )
    }
}
/* 字段设置 */
class FieldSetting extends React.PureComponent<any, any> {
    state: any = {
        chooseModalVisible: false,
        chooseLabelModalVisible: false,
        columns: [],
        fetching: false
    }
    getColumns = () => {
        if (this.state.columns.length > 0) {
            /**
             * 此处是为了减少请求次数
             */
            return;
        }
        const { currentTab, componentId, data } = this.props;
        const targetEdge = currentTab.graphData.find((o: any) => {
            return o.edge && o.target.data.id == componentId
        })
        if (targetEdge) {
            this.setState({
                fetching: true
            })
            api.getInputTableColumns({ taskId: componentId, inputType: targetEdge.inputType }).then((res: any) => {
                if (res.code === 1) {
                    let columns: any = [];
                    for (const key in res.data) {
                        if (res.data.hasOwnProperty(key)) {
                            const element = res.data[key];
                            columns.push({
                                key,
                                type: element,
                                disabled: key === get(data, 'append.key', '')
                            })
                        }
                    }
                    this.setState({
                        columns
                    })
                }
                this.setState({
                    fetching: false
                })
            })
        }
    }
    handleChoose = (type: string) => {
        this.getColumns();
        if (type == 'col') {
            this.setState({
                chooseModalVisible: true
            });
        } else {
            this.setState({
                chooseLabelModalVisible: true
            });
        }
    }
    handelOk = (key: string, targetObjects: any) => {
        this.props.handleSaveComponent(key, targetObjects);
    }
    handleCancel = (type: string) => {
        if (type == 'col') {
            this.setState({
                chooseModalVisible: false
            });
        } else {
            this.setState({
                chooseLabelModalVisible: false
            });
        }
    }
    getBtnContent (data: any) {
        return (isEmpty(data) || data.length === 0) ? '选择字段' : `已选择${data.length}个字段`;
    }
    render () {
        const { chooseModalVisible, chooseLabelModalVisible, columns, fetching } = this.state;
        const { data, currentTab, componentId } = this.props;
        const btnStyle: any = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        return (
            <Form className="params-form">
                <FormItem
                    label={<div style={{ display: 'inline-block' }}>特征列<span className="supplementary">支持double、int类型字段</span></div>}
                    colon={false}
                    required
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose.bind(this, 'col')}>{this.getBtnContent(data.col)}</Button>
                </FormItem>
                <FormItem
                    label={<div style={{ display: 'inline-block' }}>附加列<HelpDoc style={{ top: 2 }} doc='additionalColumn' /></div>}
                    colon={false}
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose.bind(this, 'append')}>{this.getBtnContent(data.append)}</Button>
                </FormItem>
                <div className="chooseWrap">
                    <ChooseModal
                        loading={fetching}
                        sourceData={columns}
                        currentTab={currentTab}
                        componentId={componentId}
                        data={data.col}
                        onOK={this.handelOk.bind(this, 'col')}
                        visible={chooseModalVisible}
                        onCancel={this.handleCancel.bind(this, 'col')} />
                    <ChooseModal
                        loading={fetching}
                        wrapContanier='.labelWrap'
                        sourceData={columns}
                        currentTab={currentTab}
                        componentId={componentId}
                        data={data.append}
                        onOK={this.handelOk.bind(this, 'append')}
                        visible={chooseLabelModalVisible}
                        onCancel={this.handleCancel.bind(this, 'append')} />
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
class GbdtRegression extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field: any, filedValue: any) => {
        const { data, currentTab, componentId, changeContent } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.MACHINE_LEARNING.KMEANS_UNION];
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            [fieldName]: {
                ...data
            }
        }
        if (field) {
            params[fieldName][field] = filedValue
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
                const values: any = {
                    append: { value: (!data.append || isEmpty(data.append)) ? '' : data.append.key }
                }
                return values;
            }
        })(FieldSetting);
        const WrapParamSetting = Form.create({
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    nClusters: { value: data.nClusters },
                    distance: { value: data.distance },
                    init: { value: data.init },
                    maxIter: { value: data.maxIter },
                    tol: { value: data.tol },
                    randomState: { value: data.randomState }
                }
                return values;
            }
        })(ParamSetting);
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
                    <WrapFieldSetting
                        data={data}
                        handleSaveComponent={this.handleSaveComponent}
                        currentTab={currentTab}
                        componentId={componentId} />
                </TabPane>
                <TabPane tab="参数设置" key="2">
                    <WrapParamSetting data={data} handleSaveComponent={this.handleSaveComponent} />
                </TabPane>
                <TabPane tab="内存设置" key="3">
                    <WrapMemorySetting data={data} handleSaveComponent={this.handleSaveComponent} />
                </TabPane>
            </Tabs>
        );
    }
}

export default GbdtRegression;
