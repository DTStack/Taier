import React from 'react';

import { Form, Input, Row, Switch, Checkbox, Alert } from 'antd';

import EngineConfigItem from '../../components/engineForm/configItem';
import {
    ENGINE_SOURCE_TYPE
} from '../../comm/const';

const FormItem = Form.Item;
const TextArea = Input.TextArea;

export const metaFormLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 3 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
    }
}

class WorkspaceForm extends React.Component {
    render () {
        const { getFieldDecorator } = this.props.form;
        const { projectList = {}, hasHadoop, hasLibra } = this.props;
        return (
            <Form>
                <FormItem
                    label='项目标识'
                    {...metaFormLayout}
                >
                    {getFieldDecorator('projectName', {
                        rules: [{
                            required: true,
                            message: '请输入项目标识'
                        }, {
                            pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
                            message: '项目标识只能由字母、数字、下划线组成，且长度不超过64个字符!'
                        }]
                    })(
                        <Input
                            style={{ width: '340px' }}
                            placeholder='英文字母开头，由英文字母、数字、下划线组成'
                        />
                    )}
                </FormItem>
                <FormItem
                    label='显示名称'
                    {...metaFormLayout}
                >
                    {getFieldDecorator('projectAlias', {
                        rules: [{
                            required: false,
                            message: '请输入显示名称'
                        }]
                    })(
                        <Input
                            style={{ width: '340px' }}
                            placeholder="请输入显示名称"
                        />
                    )}
                </FormItem>
                <FormItem
                    label='描述'
                    {...metaFormLayout}
                >
                    {getFieldDecorator('projectDesc', {
                        rules: [{
                            required: false,
                            message: '描述请控制在200个字符以内'
                        }, {
                            max: 200,
                            message: '描述不得超过200个字符！'
                        }]
                    })(
                        <TextArea
                            style={{ width: '340px' }}
                            placeholder="请输入描述"
                            autosize={{ minRows: 2, maxRows: 7 }}
                        />
                    )}
                </FormItem>
                <Row>
                    <span className="c-createWorkspace__h1">
                        高级配置
                    </span>
                    <FormItem
                        label='启动周期调度'
                        {...metaFormLayout}
                    >
                        {getFieldDecorator('enableCycleSchedule', {
                            rules: [{
                                required: true
                            }],
                            initialValue: true
                        })(
                            <Switch
                                checkedChildren="开"
                                unCheckedChildren="关"
                                defaultChecked={true}
                            />
                        )}
                    </FormItem>
                    <FormItem
                        label='下载SELECT结果'
                        {...metaFormLayout}
                    >
                        {getFieldDecorator('isAllowDownload', {
                            rules: [{
                                required: true
                            }],
                            initialValue: true
                        })(
                            <Switch
                                checkedChildren="开"
                                unCheckedChildren="关"
                                defaultChecked={true}
                            />
                        )}
                    </FormItem>
                </Row>
                <Row>
                    {(hasHadoop || hasLibra) ? (
                        <span className="c-createWorkspace__h1">
                            计算引擎配置
                        </span>
                    ) : null}
                    {
                        hasHadoop ? (
                            <React.Fragment>
                                <FormItem
                                    style={{ marginBottom: 0 }}
                                >
                                    {getFieldDecorator('enableHadoop', {
                                        rules: [],
                                        initialValue: true
                                    })(
                                        <Checkbox defaultChecked={true}>Hadoop</Checkbox>
                                    )}
                                </FormItem>
                                <Alert
                                    className='l-createWorkspace__wanring'
                                    message={<div>
                                        <p>系统需对接到Spark Thrift Server上，并使用Hive Metastore存储元数据，默认由Spark作为计算引擎，同时支持Python、Shell等类型。</p>
                                        <p>创建：建立一个新的Spark Thrift Server</p>
                                        <p>对接已有Database / Schema，可将已有表导入本平台进行管理，原系统内的数据本身不会移动或改变，在导入进行过程中，请勿执行表结构变更操作。</p>
                                    </div>}
                                    type="warning"
                                    showIcon
                                    closable
                                />
                                <div className="bd" style={{ background: '#f5f5f5', padding: '10px' }}>
                                    <EngineConfigItem
                                        {...this.props}
                                        formParentField="hadoop"
                                        formItemLayout={metaFormLayout}
                                        targetDb={projectList}
                                        engineType={ENGINE_SOURCE_TYPE.HADOOP}
                                    />
                                </div>
                            </React.Fragment>
                        ) : null
                    }
                    {
                        hasLibra ? (
                            <React.Fragment>
                                <FormItem
                                    style={{ marginBottom: 0, marginTop: 20 }}
                                >
                                    {getFieldDecorator('enableLibrA', {
                                        rules: [],
                                        initialValue: true
                                    })(
                                        <Checkbox defaultChecked={true}>HUAWEI LibrA</Checkbox>
                                    )}
                                </FormItem>
                                <div className="bd" style={{ background: '#f5f5f5', padding: '10px' }}>
                                    <EngineConfigItem
                                        {...this.props}
                                        formParentField="libra"
                                        formItemLayout={metaFormLayout}
                                        targetDb={projectList}
                                        engineType={ENGINE_SOURCE_TYPE.LIBRA}
                                    />
                                </div>
                            </React.Fragment>
                        ) : null
                    }
                </Row>
            </Form>
        )
    }
}
export default Form.create({
    mapPropsToFields: (props) => {
        return {
            projectName: {
                value: props.projectName
            },
            projectAlias: {
                value: props.projectAlias
            },
            projectDesc: {
                value: props.projectDesc
            },
            catalogueId: {
                value: props.catalogueId
            },
            lifecycle: {
                value: props.lifecycle
            }
        }
    },
    onValuesChange: (props, values) => {
        if (props.onChange) props.onChange(values);
    }
})(WorkspaceForm);
