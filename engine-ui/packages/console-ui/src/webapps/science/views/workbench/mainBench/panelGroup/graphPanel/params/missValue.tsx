import * as React from 'react';
import { Form, Tabs, Button, message, Radio, Input, Select } from 'antd';
import { formItemLayout } from './index';
import { MemorySetting as BaseMemorySetting, ChooseModal as BaseChooseModal } from './typeChange';
import { isEmpty, cloneDeep, debounce, set, get } from 'lodash';
import { INPUT_TYPE, TASK_ENUM, COMPONENT_TYPE } from '../../../../../../consts';
import api from '../../../../../../api/experiment';

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
/* 选择字段弹出框 */
class ChooseModal extends BaseChooseModal {
    disabledType: string;
    constructor (props: any) {
        super(props);
        this.disabledType = '';
    }
    initTargetKeys = () => {
        const { data } = this.props;
        const { backupSource } = this.state;
        const chooseData = data.col || [];
        const targetKeys = chooseData.map((item: any) => {
            return item.key;
        });
        const sourceData = cloneDeep(backupSource);
        this.setState({
            targetKeys,
            sourceData
        });
    }
    getSourceData = () => {
        const { currentTab, componentId, disableKeys, data } = this.props;
        const targetEdges = currentTab.graphData && currentTab.graphData.filter((o: any) => {
            return o.edge && o.target.data.id == componentId
        })
        const chooseData = data.col || [];
        const targetKeys = chooseData.map((item: any) => {
            return item.key;
        });
        if (targetEdges.length) {
            const targetEdge = targetEdges.find((o: any) => o.outputType === INPUT_TYPE.MISS_VALUE_INPUT_NORMAL);
            if (!targetEdge) return;
            this.setState({
                loading: true
            });
            this.props.getColumns().then((resData: any) => {
                if (resData) {
                    let sourceData: any = [];
                    for (const key in resData) {
                        if (resData.hasOwnProperty(key)) {
                            const element = resData[key];
                            sourceData.push({
                                key,
                                type: element,
                                disabled: targetKeys.indexOf(key) == -1 && (disableKeys.indexOf(key) > -1 || element == this.disabledType)
                            })
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
        chooseModalVisibleIndex: null,
        columns: null
    }
    handleChoose = (index: number) => {
        this.setState({
            chooseModalVisibleIndex: index
        });
    }
    async getColumns () {
        const { currentTab, componentId } = this.props;
        const targetEdges = currentTab.graphData && currentTab.graphData.filter((o: any) => {
            return o.edge && o.target.data.id == componentId
        })
        if (this.state.columns) {
            return this.state.columns;
        }
        if (targetEdges.length) {
            const targetEdge = targetEdges.find((o: any) => o.outputType === INPUT_TYPE.MISS_VALUE_INPUT_NORMAL);
            if (targetEdge) {
                let res = await api.getInputTableColumns({ taskId: componentId, inputType: targetEdge.inputType });
                if (res && res.code == 1) {
                    this.setState({ columns: res.data });
                    return res.data;
                }
            }
        }
        return null;
    }
    handelOk = (index: number, targetObjects: any) => {
        const { data } = this.props;
        let { params } = data;
        const newParams = [...params];
        newParams[index] = {
            ...newParams[index],
            col: targetObjects
        }
        this.props.handleSaveComponent('params', newParams);
    }
    addNewParam () {
        const { data } = this.props;
        let { params } = data;
        params = params || [];
        const newParams = [...params, {
            col: [],
            method: 'default'
        }];
        this.props.handleSaveComponent('params', newParams);
    }
    deleteParam (index: number) {
        const { data } = this.props;
        let { params } = data;
        params = params || [];
        const newParams = [...params];
        newParams.splice(index, 1)
        this.props.handleSaveComponent('params', newParams);
    }
    handleCancel = () => {
        this.setState({
            chooseModalVisibleIndex: null
        });
    }
    renderBtnContent (data: any) {
        return (isEmpty(data) || data.col.length == 0) ? '选择字段' : `已选择${data.col.length}个字段`;
    }
    renderGroup () {
        const { data, componentId, currentTab, form } = this.props;
        const { getFieldDecorator, getFieldValue } = form;
        const { chooseModalVisibleIndex } = this.state;
        let { params } = data;
        if (!params) {
            return null;
        }
        const btnStyle: any = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        const allKeys = params.map((param: any) => {
            return param.col.map((c: any) => {
                return c.key
            })
        }).flat();
        return <React.Fragment>
            <div className="chooseWrap">
                <ChooseModal
                    getColumns={this.getColumns.bind(this)}
                    currentTab={currentTab}
                    componentId={componentId}
                    data={params[chooseModalVisibleIndex]}
                    targetKeys={params[chooseModalVisibleIndex]}
                    disableKeys={allKeys}
                    visible={chooseModalVisibleIndex != null}
                    onOK={this.handelOk.bind(this, chooseModalVisibleIndex)}
                    onCancel={this.handleCancel} />
            </div>
            {params.map((param: any, index: number) => {
                const { method } = param;
                const specifyOrigin = getFieldValue(`params[${index}].specifyOrigin`);
                return <React.Fragment key={index}>
                    <FormItem
                        label={'填充字段'}
                        colon={false}
                        {...formItemLayout}
                    >
                        <Button style={btnStyle} onClick={this.handleChoose.bind(this, index)}>{this.renderBtnContent(param)}</Button>
                    </FormItem>
                    <FormItem
                        label=''
                        colon={false}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`params[${index}].method`, {
                            rules: [{ required: false }]
                        })(
                            <RadioGroup>
                                <Radio value={'default'}>default模式</Radio>
                                <Radio value={'specify'}>specify模式</Radio>
                                <Radio value={'replace'}>replace模式</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    {(() => {
                        switch (method) {
                            case 'default': {
                                return <React.Fragment>
                                    <FormItem
                                        label='原值'
                                        colon={false}
                                        {...formItemLayout}
                                    >
                                        <Input disabled value='null' />
                                    </FormItem>
                                    <FormItem
                                        label='替换为'
                                        colon={false}
                                        {...formItemLayout}
                                    >
                                        <Input disabled value='0或者字符串' />
                                    </FormItem>
                                </React.Fragment>
                            }
                            case 'specify': {
                                return <React.Fragment>
                                    <FormItem
                                        label='原值'
                                        colon={false}
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator(`params[${index}].specifyOrigin`, {
                                            rules: [{ required: false }]
                                        })(
                                            <Select>
                                                <Option value='number'>Null（数值型）</Option>
                                                <Option value='string'>Null（String型）</Option>
                                            </Select>
                                        )}
                                    </FormItem>
                                    <FormItem
                                        label='替换为'
                                        colon={false}
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator(`params[${index}].${specifyOrigin}`, {
                                            rules: [{ required: false }]
                                        })(
                                            <Input />
                                        )}
                                    </FormItem>
                                </React.Fragment>
                            }
                            case 'replace': {
                                return <React.Fragment>
                                    <FormItem
                                        label='原值'
                                        colon={false}
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator(`params[${index}].rawValue`, {
                                            rules: [{ required: false }]
                                        })(
                                            <Input />
                                        )}
                                    </FormItem>
                                    <FormItem
                                        label='替换为'
                                        colon={false}
                                        {...formItemLayout}
                                    >
                                        {getFieldDecorator(`params[${index}].newValue`, {
                                            rules: [{ required: false }]
                                        })(
                                            <Input />
                                        )}
                                    </FormItem>
                                </React.Fragment>
                            }
                        }
                    })()}
                    <Button style={{ width: '100%', marginBottom: '10px', background: '#fff' }} type='danger' onClick={this.deleteParam.bind(this, index)}>删除</Button>
                </React.Fragment>
            })}
        </React.Fragment>
    }
    render () {
        return (
            <Form className="params-form">
                {this.renderGroup()}
                <Button style={{ width: '100%' }} onClick={this.addNewParam.bind(this)}>增加转换</Button>
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
class MissValue extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field: any, filedValue: any) => {
        const { currentTab, componentId, data, changeContent } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.DATA_MERGE.MISS_VALUE];
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
        let { data, componentId, currentTab } = this.props;
        const WrapFieldSetting = Form.create<any>({
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const { params } = data;
                if (!params) {
                    return null;
                }
                let values: any = {};
                params.map((param: any, index: number) => {
                    values[`params[${index}].method`] = { value: param.method };
                    values[`params[${index}].specifyOrigin`] = { value: param.string || param.string == '' ? 'string' : 'number' };
                    values[`params[${index}].number`] = { value: param.number };
                    values[`params[${index}].string`] = { value: param.string };
                    values[`params[${index}].rawValue`] = { value: param.rawValue };
                    values[`params[${index}].newValue`] = { value: param.newValue };
                })
                return values;
            },
            onFieldsChange: (props: any, changedFields: any) => {
                console.log(changedFields);
                const { params } = props.data;
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.errors && !element.validating && !element.dirty) {
                            const tmpData = {
                                params: cloneDeep(params)
                            }
                            const isSpecifyOrigin = /\.specifyOrigin$/.test(key);
                            set(tmpData, key, element.value)
                            if (isSpecifyOrigin) {
                                let splitKeyArr = key.split('.');
                                let paramKey = splitKeyArr.slice(0, splitKeyArr.length - 1).join();
                                let tmpParam = get(tmpData, paramKey);
                                tmpParam.string = element.value == 'string' ? '' : null;
                                tmpParam.number = element.value == 'string' ? null : 0;
                            }
                            props.handleSaveComponent('params', tmpData.params);
                        }
                    }
                }
            }
        })(FieldSetting)
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

export default MissValue;
