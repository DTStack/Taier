/* eslint-disable no-unused-vars */
import React, { PureComponent } from 'react';
import { Form, Tabs, Button, Checkbox, Modal, Transfer, message } from 'antd';
import { formItemLayout } from './index';
import { MemorySetting as BaseMemorySetting, ChooseModal as BaseChooseModal } from './typeChange';
import { isEmpty, cloneDeep, debounce } from 'lodash';
import api from '../../../../../../api/experiment';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
/* 选择字段弹出框 */
class ChooseModal extends BaseChooseModal {
    constructor (props) {
        super(props);
        this.disabledType = 'string';
    }
    initTargetKeys = () => {
        const { data, transferField } = this.props;
        const { backupSource } = this.state;
        const chooseData = data.col || [];
        const targetKeys = chooseData.map((item) => {
            return item.key;
        });
        const sourceData = cloneDeep(backupSource);
        sourceData.forEach((item) => {
            if (targetKeys.findIndex(o => o === item.key) > -1) {
                item.type = transferField;
            }
        });
        this.setState({
            targetKeys,
            sourceData
        });
    }
}
/* 字段设置 */
class FieldSetting extends PureComponent {
    state = {
        chooseModalVisible: false
    }
    handleChoose = () => {
        this.setState({
            chooseModalVisible: true
        });
    }
    handelOk = (targetObjects) => {
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
        const btnStyle = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        const btnContent = (isEmpty(data) || data.colList.length == 0) ? '选择字段' : `已选择${data.colList.length}个字段`
        return (
            <Form className="params-form">
                <FormItem
                    label="默认全选"
                    colon={false}
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose}>{btnContent}</Button>
                    <div style={{ display: 'grid', gridTemplateColumns: '78px auto' }}>
                        {getFieldDecorator('is_save_old', {
                            valuePropName: 'checked',
                            getValueFromEvent: (e) => {
                                if (!e || !e.target) {
                                    return e;
                                }
                                const { target } = e;
                                return target.type === 'checkbox' ? (target.checked ? 1 : 0) : target.value;
                            }
                        })(
                            <Checkbox>保留原列</Checkbox>
                        )}
                        <div className="supplementary" style={{ paddingTop: 5, lineHeight: 1.5 }}>{'若保留，原列名不变，处理过的列增加"nornalized_"前缀'}</div>
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
    constructor (props) {
        super(props)
    }
}
/* main页面 */
class Normalise extends PureComponent {
    constructor (props) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field, filedValue) => {
        const { currentTab, componentId, data, changeContent } = this.props;
        const currentComponentData = currentTab.graphData.find(o => o.data.id === componentId);
        const params = {
            ...currentComponentData.data,
            normalizationComponent: {
                ...data
            }
        }
        if (field) {
            params.normalizationComponent[field] = filedValue
        }
        api.addOrUpdateTask(params).then((res) => {
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
            onFieldsChange: (props, changedFields) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            },
            mapPropsToFields: (props) => {
                const { data } = props;
                const values = {
                    is_save_old: { value: data.is_save_old === 1 }
                }
                return values;
            }
        })(FieldSetting)
        const WrapMemorySetting = Form.create({
            onFieldsChange: (props, changedFields) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            },
            mapPropsToFields: (props) => {
                const { data } = props;
                const values = {
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

export default Normalise;
