import * as React from 'react';
import { connect } from 'react-redux';

import {
    Button, message, Modal, Form, Select
} from 'antd'

import utils from 'utils'
import { getContainer } from 'funcs'

import API from '../../../../../api'
import { formItemLayout, DATA_SOURCE, DATA_SOURCE_TEXT } from '../../../../../comm/const'
import { isKafka } from '../../../../../comm'
import { actions as collectionActions } from '../../../../../store/modules/realtimeTask/collection';

const FormItem = Form.Item;
const Option = Select.Option;

const sourceTypes: any = [DATA_SOURCE.MYSQL, DATA_SOURCE.POLAR_DB, DATA_SOURCE.BEATS];

class ImportTemplateForm extends React.Component<any, any> {
    state: any = {
        sourceType: undefined,
        targetType: undefined
    }

    sourceTypeChange (value: any) {
        const { setFieldsValue } = this.props.form;

        this.setState({
            sourceType: value
        }, () => {
            setFieldsValue({
                sourceId: undefined
            })
        })
    }

    targetTypeChange (value: any) {
        const { setFieldsValue } = this.props.form;

        this.setState({
            targetType: value
        }, () => {
            setFieldsValue({
                targetSourceId: undefined
            })
        })
    }

    getSourceList () {
        const { sourceType } = this.state;
        const { dataSourceList = [] } = this.props;

        return dataSourceList
            .filter((src: any) => {
                return src.type == sourceType;
            })
            .map((src: any) => {
                const optionName = {
                    name: src.dataName
                }
                return (
                    <Option
                        key={src.id}
                        // name={src.dataName}
                        value={`${src.id}`}
                        {...optionName}
                    >
                        {src.dataName}({DATA_SOURCE_TEXT[src.type]})
                    </Option>
                )
            })
    }

    getTargetList () {
        const { targetType } = this.state;
        const { dataSourceList = [] } = this.props;
        return dataSourceList
            .filter((src: any) => {
                return src.type == targetType;
            })
            .map((src: any) => {
                const optionName = {
                    name: src.dataName
                }
                return <Option key={src.id} value={`${src.id}`} {...optionName}>
                    {src.dataName}({DATA_SOURCE_TEXT[src.type]})
                </Option>
            })
    }

    getTemplateFromNet () {
        const { validateFields } = this.props.form;
        const { id } = this.props;

        validateFields(
            (err: any, values: any) => {
                if (!err) {
                    let params: any = {
                        'id': id,
                        'sourceMap': {
                            'sourceId': values.sourceId,
                            'type': values.sourceType
                        },
                        'targetMap': {
                            'sourceId': values.targetSourceId,
                            'type': values.targetType
                        },
                        'taskId': id
                    };

                    API.getRealtimeCollectionTemplate(params)
                        .then(
                            (res: any) => {
                                if (res.code == 1) {
                                    message.success('导入成功');
                                    this.props.onSuccess(res.data);
                                }
                            }
                        )
                }
            }
        )
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { execConfirmVisible } = this.props;
        const { sourceType } = this.state;
        const isBeats = sourceType == DATA_SOURCE.BEATS;

        const sourceTypeOptions = Object.keys(DATA_SOURCE)
            .filter((key: keyof typeof DATA_SOURCE) => {
                const val = DATA_SOURCE[key];
                if (sourceTypes.indexOf(val) === -1 && !isKafka(val)) {
                    return false
                }
                return true
            })
            .map(
                (key: keyof typeof DATA_SOURCE) => {
                    const val = DATA_SOURCE[key];
                    return <Option key={val} value={val.toString()}>{DATA_SOURCE_TEXT[val]}</Option>
                }
            )
        const targetTypeOptions = Object.keys(DATA_SOURCE)
            .filter((key: keyof typeof DATA_SOURCE) => {
                const val = DATA_SOURCE[key];
                if (!isKafka(val) && val != DATA_SOURCE.HIVE) {
                    return false
                }
                return true
            })
            .map(
                (key: keyof typeof DATA_SOURCE) => {
                    const val = DATA_SOURCE[key];
                    return <Option key={val} value={val.toString()}>{DATA_SOURCE_TEXT[val]}</Option>
                }
            )

        return (
            <Modal
                maskClosable
                visible={execConfirmVisible}
                title="导入模版"
                onCancel={this.props.onCancel}
                onOk={this.getTemplateFromNet.bind(this)}
                getContainer={() => getContainer('JS_cata_modal')}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="来源类型"
                        hasFeedback
                    >
                        {getFieldDecorator('sourceType', {
                            rules: [{
                                required: true, message: '来源类型不可为空！'
                            }]
                        })(
                            <Select
                                placeholder="请选择来源类型"
                                onChange={this.sourceTypeChange.bind(this)}
                            >
                                {sourceTypeOptions}
                            </Select>
                        )}
                    </FormItem>
                    {!isBeats && <FormItem
                        style={{ marginBottom: '35px' }}
                        {...formItemLayout}
                        label="数据源"
                        hasFeedback
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [{
                                required: true, message: '数据源不可为空！'
                            }]
                        })(
                            <Select
                                placeholder="请选择数据源"
                            >
                                {this.getSourceList()}
                            </Select>
                        )}
                    </FormItem>}
                    <FormItem
                        {...formItemLayout}
                        label="目标类型"
                        hasFeedback
                    >
                        {getFieldDecorator('targetType', {
                            rules: [{
                                required: true, message: '目标类型不可为空！'
                            }]
                        })(
                            <Select
                                placeholder="请选择目标类型"
                                onChange={this.targetTypeChange.bind(this)}
                            >
                                {targetTypeOptions}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="数据源"
                        hasFeedback
                    >
                        {getFieldDecorator('targetSourceId', {
                            rules: [{
                                required: true, message: '数据源不可为空！'
                            }]
                        })(
                            <Select
                                placeholder="请选择目标数据源"
                            >
                                {this.getTargetList()}
                            </Select>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const WrapTemplateForm = Form.create<any>()(ImportTemplateForm);

@(connect((state: any) => {
    const { currentPage } = state.realtimeTask;

    return {
        dataSourceList: currentPage.dataSourceList
    };
}, (dispatch: any) => {
    return {
        getDataSource: () => {
            dispatch(collectionActions.getDataSource());
        }
    }
}) as any)
class CollectionToolbar extends React.Component<any, any> {
    state: any = {
        execConfirmVisible: false,
        key: null
    }

    componentDidMount () {
        const { getDataSource } = this.props;
        getDataSource();
    }

    importTemplate () {
        this.setState({
            execConfirmVisible: true
        })
    }

    onSuccess = (template: any) => {
        const data: any = {
            merged: true
        }

        data.sqlText = utils.jsonFormat(template, 0);

        this.setState({
            execConfirmVisible: false
        });

        this.props.editorChange(data)
    }

    render () {
        const { execConfirmVisible, key } = this.state
        const titleFix = { title: '导入模版' };
        return (
            <span>
                <Button
                    onClick={this.importTemplate.bind(this)}
                    // title="导入模版"
                    icon="plus-circle-o"
                    style={{ marginLeft: '0px' }}
                    {...titleFix}
                >
                    导入模版
                </Button>
                <WrapTemplateForm
                    key={key}
                    execConfirmVisible={execConfirmVisible}
                    onCancel={
                        () => {
                            this.setState({ execConfirmVisible: false, key: Math.random() })
                        }
                    }
                    onSuccess={this.onSuccess.bind(this)}
                    {...this.props} />

            </span>
        )
    }
}

export default CollectionToolbar;
