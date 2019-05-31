import React from 'react';

import { Form, Select, Radio } from 'antd';

import CatalogueSelect from '../../components/catalogueSelect';
import LifeCycleSelect from '../../components/lifeCycleSelect';

import {
    ENGINE_SOURCE_TYPE
} from '../../comm/const';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

class EngineConfigItem extends React.Component {
    state = {
        createType: 1
    }
    componentDidUpdate (prevProps, prevState, snapshot) {
        if (prevProps.engineType !== this.props.engineType) {
            this.setState({
                createType: 1
            })
        }
    }

    initialTypeRadios () {
        const { engineType } = this.props;
        switch (engineType) {
            case ENGINE_SOURCE_TYPE.HADOOP: {
                return [
                    <Radio value={1} key={1}>创建</Radio>,
                    <Radio value={2} key={2}>对接已有Spark Thrift Server</Radio>
                ]
            }
            case ENGINE_SOURCE_TYPE.LIBRA: {
                return [
                    <Radio value={1} key={1}>创建Schema</Radio>,
                    <Radio value={2} key={2}>对接已有LibrA Schema</Radio>
                ]
            }
            default: return '对接未知类型';
        }
    }

    onInitialTypeChange = (e) => {
        this.setState({ createType: parseInt(e.target.value, 10) })
    }

    render () {
        const {
            onPreviewMetaData,
            targets,
            formParentField,
            formItemLayout,
            disabledTargets
        } = this.props;
        const { createType } = this.state;
        const { getFieldDecorator } = this.props.form;
        console.log('createType:', createType);
        const addTargets = targets && targets.map(
            item => (
                <Option
                    key={item.value}
                    disabled={disabledTargets && disabledTargets.indexOf(item.value) > -1} // 禁用指定目标
                    value={item.value.toString()}
                >
                    {item.name}
                </Option>
            )
        )

        const parentField = formParentField ? `${formParentField}.` : '';

        return (
            <React.Fragment>
                <FormItem
                    label='初始化方式'
                    style={{ marginBottom: createType === 1 ? 0 : 24 }}
                    {...formItemLayout}
                >
                    <RadioGroup
                        value={this.state.createType}
                        onChange={this.onInitialTypeChange}>
                        {this.initialTypeRadios()}
                    </RadioGroup>
                </FormItem>
                {
                    createType === 2 ? <React.Fragment>
                        <FormItem
                            label='对接目标'
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${parentField}.target`, {
                                rules: [{
                                    required: true,
                                    message: '请选择对接目标'
                                }]
                            })(
                                <Select
                                    style={{ maxWidth: '400px' }}
                                    placeholder="请选择对接目标"
                                >
                                    { addTargets }
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
