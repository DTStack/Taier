import * as React from 'react';
import { Form, Tabs, Button, Checkbox, message } from 'antd';
import { formItemLayout } from './index';
import { MemorySetting as BaseMemorySetting, ChooseModal as BaseChooseModal } from './typeChange';
import { isEmpty, cloneDeep, debounce } from 'lodash';
import { INPUT_TYPE, TASK_ENUM, COMPONENT_TYPE } from '../../../../../../consts';
import api from '../../../../../../api/experiment';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
/* 选择字段弹出框 */
class ChooseModal extends BaseChooseModal {
    disabledType: string;
    constructor (props: any) {
        super(props);
        this.disabledType = '';
    }
    initTargetKeys = () => {
        const { data, transferField } = this.props;
        const { backupSource } = this.state;
        const chooseData = data.col || [];
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
        if (targetEdges) {
            const targetEdge = targetEdges.find((o: any) => o.outputType === INPUT_TYPE.NORMAL);
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
        chooseModalVisible: false
    }
    handleChoose = () => {
        this.setState({
            chooseModalVisible: true
        });
    }
    handelOk = (targetObjects: any) => {
        this.props.handleSaveComponent('col', targetObjects);
    }
    handleCancel = () => {
        this.setState({
            chooseModalVisible: false
        });
    }
    render () {
        const { chooseModalVisible } = this.state;
        const { getFieldDecorator } = this.props.form;
        const { data, componentId, currentTab } = this.props;
        const btnStyle: any = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        const btnContent = (isEmpty(data) || data.col.length == 0) ? '选择字段' : `已选择${data.col.length}个字段`
        return (
            <Form className="params-form">
                <FormItem
                    label="默认全选"
                    colon={false}
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose}>{btnContent}</Button>
                    <div style={{ display: 'grid', gridTemplateColumns: '82px auto' }}>
                        {getFieldDecorator('keepOriginal', {
                            valuePropName: 'checked',
                            getValueFromEvent: (e: any) => {
                                if (!e || !e.target) {
                                    return e;
                                }
                                const { target } = e;
                                return target.type === 'checkbox' ? (target.checked ? 1 : 0) : target.value;
                            }
                        })(
                            <Checkbox style={{ fontSize: 13 }}>保留原列</Checkbox>
                        )}
                        <div className="supplementary" style={{ paddingTop: 5, lineHeight: 1.5 }}>{'若保留，原列名不变，处理过的列增加"stdized_"前缀'}</div>
                    </div>
                </FormItem>
                <div className="chooseWrap">
                    <ChooseModal
                        currentTab={currentTab}
                        componentId={componentId}
                        data={data}
                        transferField='double'
                        visible={chooseModalVisible}
                        onOK={this.handelOk}
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
class Standardization extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field: any, filedValue: any) => {
        const { currentTab, componentId, data, changeContent } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.DATA_MERGE.STANDARD];
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
        const { data, componentId, currentTab } = this.props;
        const WrapFieldSetting = Form.create({
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            },
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    // eslint-disable-next-line @typescript-eslint/camelcase
                    keepOriginal: { value: data.keepOriginal === 1 }
                }
                return values;
            }
        })(FieldSetting)
        const WrapMemorySetting = Form.create({
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty) {
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

export default Standardization;
