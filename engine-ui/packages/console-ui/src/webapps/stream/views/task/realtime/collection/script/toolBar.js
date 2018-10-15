import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Button, message, Modal, Form, Select,
} from 'antd'

import utils from 'utils'
import { getContainer } from 'funcs'

import API from '../../../../../api'
import { DatabaseType } from '../../../../../components/status';
import { formItemLayout, DATA_SOURCE } from '../../../../../comm/const'
import { actions as collectionActions } from '../../../../../store/modules/realtimeTask/collection';

const FormItem = Form.Item;
const Option = Select.Option;


class ImportTemplateForm extends Component {

    state = {
        sourceType: undefined,
        targetType: undefined
    }

    sourceTypeChange(value) {
        const { setFieldsValue } = this.props.form;

        this.setState({
            sourceType: value
        },
            () => {
                setFieldsValue({
                    sourceId: undefined
                })
            })
    }

    targetTypeChange(value) {
        const { setFieldsValue } = this.props.form;

        this.setState({
            targetType: value
        },
            () => {
                setFieldsValue({
                    targetSourceId: undefined
                })
            })

    }

    getSourceList() {
        const { sourceType } = this.state;
        const { dataSourceList=[] } = this.props;

        return dataSourceList
            .filter(src => {
                return src.type == sourceType;
            })
            .map(src => {
                return <Option key={src.id} name={src.dataName} value={`${src.id}`}>
                    {src.dataName}( <DatabaseType value={src.type} />  )
                </Option>
            })
    }

    getTargetList() {
        const { targetType } = this.state;
        const { dataSourceList=[] } = this.props;

        return dataSourceList
            .filter(src => {
                return src.type == targetType;
            })
            .map(src => {
                return <Option key={src.id} name={src.dataName} value={`${src.id}`}>
                    {src.dataName}( <DatabaseType value={src.type} /> )
                </Option>
            })

    }

    getTemplateFromNet(){
        const { validateFields } = this.props.form;
        const { id } = this.props;

        validateFields(
            (err, values)=>{
                if(!err){
                    let params={
                        "id":id,
                        "sourceMap":{
                            "sourceId":values.sourceId,
                            "type":values.sourceType,
                        },
                        "targetMap":{
                            "sourceId":values.targetSourceId,
                            "type":values.targetType
                        },
                        "taskId":id
                    };

                    API.getRealtimeCollectionTemplate(params)
                    .then(
                        (res)=>{
                            if(res.code==1){
                                message.success("导入成功");
                                this.props.onSuccess(res.data);
                            }
                        }
                    )
                }
            }
        )
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { dataSourceList, execConfirmVisible } = this.props;

        const sourceTypeOptions = Object.keys(DATA_SOURCE)
        .filter((key)=>{
            const val = DATA_SOURCE[key];
            if(val!=DATA_SOURCE.MYSQL){
                return false
            }
            return true
        })
        .map(
            (key) => {
                const val = DATA_SOURCE[key];
                return <Option key={val} value={val.toString()}><DatabaseType value={val} /></Option>
            }
        )
        const targetTypeOptions = Object.keys(DATA_SOURCE)
        .filter((key)=>{
            const val = DATA_SOURCE[key];
            if(val!=DATA_SOURCE.KAFKA){
                return false
            }
            return true
        })
        .map(
            (key) => {
                const val = DATA_SOURCE[key];
                return <Option key={val} value={val.toString()}><DatabaseType value={val} /></Option>
            }
        )

        return (
            <div id="JS_import_modal">
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
                                required: true, message: '来源类型不可为空！',
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
                    <FormItem
                        style={{ marginBottom: "35px" }}
                        {...formItemLayout}
                        label="数据源"
                        hasFeedback
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [{
                                required: true, message: '数据源不可为空！',
                            }]
                        })(
                            <Select
                                placeholder="请选择数据源"
                            >
                                {this.getSourceList()}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="目标类型"
                        hasFeedback
                    >
                        {getFieldDecorator('targetType', {
                            rules: [{
                                required: true, message: '目标类型不可为空！',
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
                                required: true, message: '数据源不可为空！',
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
            </div>
        )
    }
}
const WrapTemplateForm = Form.create()(ImportTemplateForm);


@connect(state => {
    const { currentPage } = state.realtimeTask;

    return {
        dataSourceList: currentPage.dataSourceList,
    };
}, dispatch => {
    return {
        getDataSource: () => {
            dispatch(collectionActions.getDataSource());
        },
    }
})
class CollectionToolbar extends Component {

    state = {
        execConfirmVisible: false
    }

    componentDidMount() {
        const { getDataSource } = this.props;
        getDataSource();
    }

    importTemplate() {
        this.setState({
            execConfirmVisible: true
        })
    }

    onSuccess = (template) => {
        const { dispatch } = this.props;
        const data = {
            merged: true,
        }

        data.sqlText = utils.jsonFormat(template);

        this.setState({
            execConfirmVisible: false
        });

        this.props.editorChange(data)
    }

    render() {
        const { execConfirmVisible } = this.state

        return (
            <span>
                <Button
                    onClick={this.importTemplate.bind(this)}
                    title="导入模版"
                    icon="plus-circle-o"
                    style={{ marginLeft: '0px' }}>导入模版
                </Button>
                <WrapTemplateForm
                    execConfirmVisible={execConfirmVisible}
                    onCancel={
                        () => {
                            this.setState({ execConfirmVisible: false })
                        }
                    }
                    onSuccess={this.onSuccess.bind(this)}
                    {...this.props} />

            </span>
        )
    }
}

export default CollectionToolbar;