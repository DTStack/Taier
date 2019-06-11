import React, { Component } from 'react';
import { Modal, Form, Input, Checkbox, message } from 'antd';
import EngineSelect from '../../../../webapps/rdos/components/engineSelect';
import { formItemLayout, ENGINE_TYPE, ENGINE_TYPE_ARRAY, ENGINE_TYPE_NAME,
    COMPONENT_TYPE_VALUE, hadoopEngineOptionsValue, noDisablehadoopEngineOptionsValue } from '../../consts';
const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;
const defaultCheckedValue = [COMPONENT_TYPE_VALUE.HDFS, COMPONENT_TYPE_VALUE.YARN]; // 必选引擎值
const defaultEngine = ENGINE_TYPE_NAME.HADOOP // hadoop
class AddCommModal extends Component {
    constructor (props) {
        super(props);
        this.state = {
            checkedList: props.isAddComp ? [] : defaultCheckedValue,
            checkAll: false,
            isAdd: false // 添加引擎
        }
    }
    tranEngineData = () => {
        let hadoopOptionValue = [];
        hadoopEngineOptionsValue.map(item => {
            hadoopOptionValue.push(item.value)
        })
        return hadoopOptionValue
    }
    onChange = (checkedList) => {
        this.setState({
            checkedList,
            checkAll: checkedList.length === hadoopEngineOptionsValue.length
        })
    }
    onCheckAllChange = (e) => {
        const { isAddComp } = this.props;
        this.setState({
            checkedList: e.target.checked ? this.tranEngineData() : isAddComp ? [] : defaultCheckedValue,
            checkAll: e.target.checked
        })
    }
    getServerParams = (value) => {
        const { getFieldValue } = this.props.form;
        const hadoopOption = getFieldValue('engineName')
        const libraOption = getFieldValue('libraEngineName')
        const isHadoop = hadoopOption === ENGINE_TYPE_NAME.HADOOP || libraOption === ENGINE_TYPE_NAME.HADOOP;
        const isLibra = hadoopOption === ENGINE_TYPE_NAME.LIBRA || libraOption === ENGINE_TYPE_NAME.LIBRA;
        let params = {};
        params.engineList = [];
        params.clusterName = value.clusterName;
        if (isHadoop) {
            params.engineList.push({
                engineName: value.engineName || value.libraEngineName,
                componentTypeCodeList: this.state.checkedList
            })
        }
        if (isLibra) {
            params.engineList.push({
                engineName: value.libraEngineName || value.engineName,
                componentTypeCodeList: [COMPONENT_TYPE_VALUE.LIBRASQL]
            })
        }
        return params
    }
    // 添加集群
    isSelectSameEngine = () => {
        const { getFieldValue } = this.props.form;
        const hadoopOption = getFieldValue('engineName')
        const libraOption = getFieldValue('libraEngineName')
        let flag = true;
        if (hadoopOption === libraOption) {
            message.error('不能选择同类的引擎！')
            flag = false
        }
        return flag
    }
    isSelectComp = () => {
        let validate = false
        if (this.state.checkedList.length === 0) {
            message.error('请选择增加的组件！')
        } else {
            validate = true
        }
        return validate
    }
    getAddClusterParams () {
        const { validateFields } = this.props.form;
        let params = {
            reqParams: {},
            canSubmit: false
        }
        validateFields(this.state.isAdd ? ['clusterName', 'engineName', 'libraEngineName'] : ['clusterName', 'engineName'], {}, (err, value) => {
            if (!err) {
                if (this.isSelectSameEngine()) {
                    params.canSubmit = true
                    params.reqParams = this.getServerParams(value);
                }
            }
        })
        return params
    }
    getEngineAndCompParams () {
        const { getFieldValue } = this.props.form;
        const engineName = getFieldValue('engineName');
        const { isAddComp } = this.props;
        const isHadoop = engineName == ENGINE_TYPE_NAME.HADOOP;
        let engineId = isHadoop || isAddComp ? ENGINE_TYPE.HADOOP : ENGINE_TYPE.LIBRA // 增加组件无引擎选择，目前只有hadoop可以添加
        let params = {
            reqParams: {},
            canSubmit: false
        }
        if (this.isSelectComp) { // 增加组件校验
            params.reqParams = {
                engineName,
                engineId,
                componentTypeCodeList: isHadoop || isAddComp ? this.state.checkedList : [COMPONENT_TYPE_VALUE.LIBRASQL]
            }
            params.canSubmit = true
        }
        return params
    }
    addEngine = () => {
        const { setFieldsValue } = this.props.form;
        this.setState({
            isAdd: true
        }, () => { setFieldsValue({ libraEngineName: defaultEngine }) })
    }
    delEngine = () => {
        const { setFieldsValue } = this.props.form;
        this.setState({
            isAdd: false
        })
        setFieldsValue({ libraEngineName: '' })
    }
    renderDiffentEngine = (flag) => {
        const { getFieldDecorator } = this.props.form;
        const { isAddComp } = this.props;
        const { checkAll, checkedList } = this.state;
        // 新增组件都可选
        const options = isAddComp ? noDisablehadoopEngineOptionsValue : hadoopEngineOptionsValue;
        return (
            flag ? <React.Fragment>
                {
                    !isAddComp && <FormItem // 添加组件时不显示
                        label="必选组件"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('requiredEngines', {
                            rules: [{
                                required: true,
                                message: ''
                            }]
                        })(
                            <span>{'HDFS、YARN'}</span>
                        )}
                    </FormItem>
                }
                <FormItem
                    label={<span>
                        <span style={{ color: '#f04134', fontSize: '12px', fontFamily: 'SimSun' }}>* </span>
                        <span>{isAddComp ? '增加组件' : '可选组件'}</span>
                    </span>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('engines', {
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
            </React.Fragment> : <React.Fragment>
                <FormItem
                    label="必选组件"
                    {...formItemLayout}
                >
                    {getFieldDecorator('librarequiredEngines', {
                        rules: [{
                            required: true,
                            message: ''
                        }],
                        initialValue: COMPONENT_TYPE_VALUE.LIBRASQL
                    })(
                        <span>LibrA SQL</span>
                    )}
                </FormItem>
            </React.Fragment>
        )
    }
    render () {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { title, visible, onCancel, onOk, isAddCluster, isAddComp } = this.props;
        const { isAdd } = this.state;
        const hadoopOption = getFieldValue('engineName')
        const libraOption = getFieldValue('libraEngineName')
        const hadoopFlag = hadoopOption === ENGINE_TYPE_NAME.HADOOP || hadoopOption == undefined;
        const libraFlag = libraOption === ENGINE_TYPE_NAME.HADOOP || libraOption == undefined;
        return (
            <Modal
                title={title}
                visible={visible}
                onCancel={onCancel}
                onOk={() => { onOk(isAddCluster ? this.getAddClusterParams() : this.getEngineAndCompParams()) }}
            >
                <Form>
                    {
                        isAddCluster && <FormItem
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
                        </FormItem>
                    }
                    {
                        !isAddComp && <FormItem
                            label="引擎类型"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('engineName', {
                                rules: [{
                                    required: true,
                                    message: '引擎类型不可为空！'
                                }],
                                initialValue: defaultEngine
                            })(
                                <EngineSelect
                                    placeholder='请选择引擎类型'
                                    tableTypes={ENGINE_TYPE_ARRAY}
                                    onChange={() => { this.setState({ checkedList: defaultCheckedValue, checkAll: false }) }}
                                />
                            )}
                            {
                                isAddCluster && !isAdd && <a className='engine-opera' onClick={this.addEngine}>添加引擎</a>
                            }
                        </FormItem>
                    }
                    {this.renderDiffentEngine(hadoopFlag)}

                    {/* 新增集群modal中添加多个引擎 */}
                    {
                        isAdd ? <React.Fragment>
                            <div className='dashed-line'></div>
                            <FormItem
                                label="引擎类型"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('libraEngineName', {
                                    rules: [{
                                        required: true,
                                        message: '引擎类型不可为空！'
                                    }]
                                })(
                                    <EngineSelect
                                        placeholder='请选择引擎类型'
                                        tableTypes={ENGINE_TYPE_ARRAY}
                                    />
                                )}
                                {
                                    isAddCluster && isAdd && <a className='engine-opera' onClick={this.delEngine}>删除引擎</a>
                                }
                            </FormItem>
                            {this.renderDiffentEngine(libraFlag)}
                        </React.Fragment> : null
                    }
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(AddCommModal);
