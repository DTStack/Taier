import React from 'react';

import { Form, Select, Radio, Input } from 'antd';

import CatalogueSelect from '../../components/catalogueSelect';
import LifeCycleSelect from '../../components/lifeCycleSelect';

import {
    ENGINE_SOURCE_TYPE, PROJECT_CREATE_MODEL
} from '../../comm/const';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

class EngineConfigItem extends React.Component {
    initialTypeRadios () {
        const { engineType } = this.props;
        switch (engineType) {
            case ENGINE_SOURCE_TYPE.HADOOP: {
                return [
                    <Radio value={PROJECT_CREATE_MODEL.NORMAL} key={PROJECT_CREATE_MODEL.NORMAL}>创建</Radio>,
                    <Radio value={PROJECT_CREATE_MODEL.IMPORT} key={PROJECT_CREATE_MODEL.IMPORT}>对接已有Spark Thrift Server</Radio>
                ]
            }
            case ENGINE_SOURCE_TYPE.LIBRA: {
                return [
                    <Radio value={PROJECT_CREATE_MODEL.NORMAL} key={PROJECT_CREATE_MODEL.NORMAL}>创建Schema</Radio>,
                    <Radio value={PROJECT_CREATE_MODEL.IMPORT} key={PROJECT_CREATE_MODEL.IMPORT}>对接已有LibrA Schema</Radio>
                ]
            }
            default: return '对接未知类型';
        }
    }
    renderDbOptions = (dataBase) => {
        return [].map(item => {
            return <Option key={`${item}`} value={item}>{item}</Option>
        })
    }
    render () {
        const {
            onPreviewMetaData,
            targetDb,
            formParentField,
            formItemLayout
            // disabledTargets
        } = this.props;
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const hadoopDb = targetDb[ENGINE_SOURCE_TYPE.HADOOP] || [];
        const libraDb = targetDb[ENGINE_SOURCE_TYPE.LIBRA] || [];
        const parentField = formParentField ? `${formParentField}` : '';
        const isHadoop = formParentField == 'hadoop';
        const dataBase = isHadoop ? hadoopDb : libraDb
        const engineType = isHadoop ? ENGINE_SOURCE_TYPE.HADOOP : ENGINE_SOURCE_TYPE.LIBRA;
        const createModel = getFieldValue(`${parentField}.createModel`);
        return (
            <React.Fragment>
                <FormItem
                    label='引擎类型'
                    style={{ display: 'none' }}
                    {...formItemLayout}
                >
                    {getFieldDecorator(`${parentField}.engineType`, {
                        initialValue: `${engineType}`
                    })(
                        <Input/>
                    )}
                </FormItem>
                <FormItem
                    label='初始化方式'
                    style={{ marginBottom: createModel === PROJECT_CREATE_MODEL.NORMAL ? 0 : 24 }}
                    {...formItemLayout}
                >
                    {getFieldDecorator(`${parentField}.createModel`, {
                        rules: [],
                        initialValue: PROJECT_CREATE_MODEL.NORMAL
                    })(
                        <RadioGroup>
                            {this.initialTypeRadios()}
                        </RadioGroup>
                    )}
                </FormItem>
                {
                    createModel === PROJECT_CREATE_MODEL.IMPORT ? <React.Fragment>
                        <FormItem
                            label='对接目标'
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${parentField}.database`, {
                                rules: [{
                                    required: true,
                                    message: '请选择对接目标'
                                }]
                            })(
                                <Select
                                    style={{ maxWidth: '400px' }}
                                    placeholder="请选择对接目标"
                                >
                                    {this.renderDbOptions(dataBase)}
                                </Select>
                            )}
                            <a onClick={onPreviewMetaData} style={{ marginLeft: 5 }}>预览元数据</a>
                        </FormItem>
                        <FormItem
                            label='所属类目'
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${parentField}.catalogueId`, {
                                rules: [{
                                    required: true,
                                    message: '请选择所属类目'
                                }]
                            })(
                                <CatalogueSelect
                                    style={{ maxWidth: '400px' }}
                                    showSearch
                                    placeholder="请选择所属类目"
                                />
                            )}
                        </FormItem>
                        <FormItem
                            label='生命周期'
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${parentField}.lifecycle`, {
                                rules: [{
                                    required: true,
                                    message: '生命周期不可为空'
                                }]
                            })(
                                <LifeCycleSelect width={100} inputWidth={175} />
                            )}
                        </FormItem>

                    </React.Fragment> : null
                }
            </React.Fragment>
        )
    }
}
export default EngineConfigItem;
