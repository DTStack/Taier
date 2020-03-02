import * as React from 'react';
import { Modal, Form, Input, Checkbox, message, Radio } from 'antd';
import { cloneDeep } from 'lodash';

import {
    COMPONENT_TYPE_VALUE, HADOOP_GROUP_VALUE,
    formItemLayout, ENGINE_TYPE_ARRAY, ENGINE_TYPE_NAME
} from '../../consts';

const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;

const defaultCheckedValue: any = [ // 必选引擎值
    COMPONENT_TYPE_VALUE.HDFS, COMPONENT_TYPE_VALUE.YARN, COMPONENT_TYPE_VALUE.SFTP
];

// 新增集群、增加组件、增加引擎共用组件
class AddEngineModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            checkedList: defaultCheckedValue,
            checkAll: false
        }
    }

    // 转化成checkedList可用数据
    exChangeCheckedListData = (data: any) => {
        let recoginzeValue: any = [];
        data.map((item: any) => {
            recoginzeValue.push(item.value)
        })
        return recoginzeValue
    }

    onChange = (checkedList: any) => {
        this.setState({
            checkedList,
            checkAll: checkedList.length === HADOOP_GROUP_VALUE.length
        })
    }

    onCheckAllChange = (e: any) => {
        const notAddComponetVal = this.filterExistData();
        this.setState({
            checkedList: e.target.checked ? (this.exChangeCheckedListData(notAddComponetVal))
                : defaultCheckedValue,
            checkAll: e.target.checked
        })
    }

    /**
     * 添加集群数据转换
     */
    wrapParams = (value: any) => {
        const engines = value.engineTypes;
        const { singleMode } = this.props;
        const params: any = {};
        const getComponentCodes = (engineName: string) => {
            if (engineName === ENGINE_TYPE_NAME.HADOOP) {
                return this.state.checkedList;
            } else if (engineName === ENGINE_TYPE_NAME.LIBRA) {
                return [COMPONENT_TYPE_VALUE.LIBRASQL];
            } else if (engineName === ENGINE_TYPE_NAME.TI_DB) {
                return [COMPONENT_TYPE_VALUE.TIDB_SQL];
            }
        }
        if (singleMode) {
            params.engineName = engines;
            params.componentTypeCodeList = getComponentCodes(engines);
        } else {
            params.clusterName = value.clusterName;
            params.engineList = engines.map(engineName => {
                return {
                    engineName: engineName,
                    componentTypeCodeList: getComponentCodes(engineName)
                }
            });
        }

        return params
    }

    /**
     * 获取新增集群参数
     */
    onSubmit = () => {
        const { onOk, form } = this.props;
        const { validateFields } = form;

        let params: any = {
            reqParams: {},
            canSubmit: false
        }
        validateFields((err: any, value: any) => {
            if (!err) {
                params.reqParams = this.wrapParams(value);
                params.canSubmit = true;
                onOk(params);
            }
        })
    }

    /**
     * 校验新增引擎是否已存在
     */
    validateIsExistEngine = () => {
        const { getFieldValue } = this.props.form;
        const { engineList } = this.props;
        const engineSelected = getFieldValue('engineName');
        const flag = engineList.some((item: any) => item.engineName == engineSelected)
        if (flag) {
            message.error(`引擎已存在！`)
            return false
        } else {
            return true
        }
    }

    /**
     * 增加组件时，筛选出未添加的组件数据
     */
    filterExistData = () => {
        const { hadoopComponentData = [] } = this.props;
        let copyOptVal = cloneDeep(HADOOP_GROUP_VALUE);
        for (let i = 0; i < copyOptVal.length; i++) {
            if (copyOptVal[i].hasOwnProperty('disabled')) {
                delete copyOptVal[i]['disabled']
            }
        }
        return copyOptVal
            .filter((obj: any) => !hadoopComponentData.some((item: any) => item.componentTypeCode == obj.value))
    }

    renderEngineComponents = () => {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { checkAll, checkedList } = this.state;
        // 新增组件筛选已添加组件
        const options = HADOOP_GROUP_VALUE;

        const engines = getFieldValue('engineTypes');
        const checkedHadoop = engines.indexOf(ENGINE_TYPE_NAME.HADOOP) > -1;
        const checkedLibra = engines.indexOf(ENGINE_TYPE_NAME.LIBRA) > -1;
        const checkedTiDB = engines.indexOf(ENGINE_TYPE_NAME.TI_DB) > -1;

        return (
            <React.Fragment>
                <FormItem
                    label="Hadoop组件"
                    style={{ marginBottom: '4px' }}
                    className={checkedHadoop ? 'c-formItem__checked' : ''}
                    {...formItemLayout}
                >
                    {getFieldDecorator('engines', {
                        rules: [{
                            required: checkedHadoop,
                            message: ''
                        }],
                        initialValue: checkedList
                    })(
                        <div>
                            <Checkbox
                                onChange={this.onCheckAllChange}
                                checked={checkAll}
                            >全选</Checkbox>
                            <CheckboxGroup
                                options={options}
                                value={checkedList}
                                onChange={this.onChange}
                            />
                        </div>
                    )}
                </FormItem>
                <FormItem
                    label="LibrA组件"
                    {...formItemLayout}
                    style={{ marginBottom: '4px' }}
                    className={checkedLibra ? 'c-formItem__checked' : ''}
                >
                    {getFieldDecorator('librarequiredEngines', {
                        rules: [{
                            required: checkedLibra,
                            message: ''
                        }],
                        initialValue: COMPONENT_TYPE_VALUE.LIBRASQL
                    })(
                        <span>LibrA SQL</span>
                    )}
                </FormItem>
                <FormItem
                    label="TiDB组件"
                    {...formItemLayout}
                    style={{ marginBottom: '4px' }}
                    className={checkedTiDB ? 'c-formItem__checked' : ''}
                >
                    {getFieldDecorator('tidbrequiredEngines', {
                        rules: [{
                            required: checkedTiDB,
                            message: ''
                        }],
                        initialValue: COMPONENT_TYPE_VALUE.TIDB_SQL
                    })(
                        <span>TiDB SQL</span>
                    )}
                </FormItem>
            </React.Fragment>
        );
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { title, visible, onCancel, singleMode, existEngines } = this.props;

        const isEdit = existEngines && existEngines.length > 0;
        const initialEngineValues = [];
        const engineCheckboxOptions = ENGINE_TYPE_ARRAY.map(engine => {
            const exist = existEngines && existEngines.find(o => o.engineName.toLowerCase() == engine.value.toLowerCase());
            if (exist) initialEngineValues.push(engine.value);
            return {
                value: engine.value,
                label: engine.name,
                disabled: exist
            }
        });

        return (
            <Modal
                title={title}
                visible={visible}
                onCancel={onCancel}
                onOk={this.onSubmit}
            >
                <Form>
                    {
                        !isEdit ? <FormItem
                            label="集群标识"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('clusterName', {
                                rules: [{
                                    required: true,
                                    message: '集群标识不可为空！'
                                }, {
                                    pattern: /^[a-z0-9_]{1,64}$/i,
                                    message: '集群标识不能超过64字符，支持英文、数字、下划线'
                                }]
                            })(
                                <Input placeholder="请输入集群标识" />
                            )}
                        </FormItem> : null
                    }
                    <FormItem
                        label="引擎类型"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('engineTypes', {
                            rules: [{
                                required: true,
                                message: '引擎类型不可为空！'
                            }],
                            initialValue: isEdit ? initialEngineValues : [ENGINE_TYPE_NAME.HADOOP]
                        })(
                            singleMode
                                ? <RadioGroup options={engineCheckboxOptions}/>
                                : <CheckboxGroup options={engineCheckboxOptions} />
                        )}
                    </FormItem>
                    {
                        this.renderEngineComponents()
                    }
                </Form>
            </Modal>
        )
    }
}
export default Form.create<any>()(AddEngineModal);
