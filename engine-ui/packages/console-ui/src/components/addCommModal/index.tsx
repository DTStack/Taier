import * as React from 'react';
import { Modal, Form, Input, Checkbox, message } from 'antd';
import { cloneDeep } from 'lodash';
import EngineSelect from 'dt-common/src/components/engineSelect';

import { formItemLayout, ENGINE_TYPE_ARRAY, ENGINE_TYPE_NAME,
    COMPONENT_TYPE_VALUE, HADOOP_GROUP_VALUE } from '../../consts';
const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;
const defaultCheckedValue: any = [COMPONENT_TYPE_VALUE.HDFS, COMPONENT_TYPE_VALUE.YARN, COMPONENT_TYPE_VALUE.SFTP]; // 必选引擎值
const defaultEngine = ENGINE_TYPE_NAME.HADOOP // hadoop

// 新增集群、增加组件、增加引擎共用组件
class AddCommModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            checkedList: props.isAddComp ? [] : defaultCheckedValue,
            checkAll: false,
            isAddMultiple: false // 新增集群时是否添加多个引擎
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
        const { isAddComp } = this.props;
        const notAddComponetVal = this.filterExistData();
        this.setState({
            checkedList: e.target.checked ? (isAddComp ? this.exChangeCheckedListData(notAddComponetVal) : this.exChangeCheckedListData(HADOOP_GROUP_VALUE))
                : (isAddComp ? [] : defaultCheckedValue),
            checkAll: e.target.checked
        })
    }
    /**
     * 添加集群数据转换
     */
    getServerParams = (value: any) => {
        const { getFieldValue } = this.props.form;
        const hadoopOption = getFieldValue('engineName')
        const libraOption = getFieldValue('libraEngineName')

        const isHadoop = hadoopOption === ENGINE_TYPE_NAME.HADOOP || libraOption === ENGINE_TYPE_NAME.HADOOP; // 添加了hadoop
        const isLibra = hadoopOption === ENGINE_TYPE_NAME.LIBRA || libraOption === ENGINE_TYPE_NAME.LIBRA; // 添加了libra

        let params: any = {};
        params.engineList = [];
        params.clusterName = value.clusterName;
        if (isHadoop) {
            params.engineList.push({
                engineName: ENGINE_TYPE_NAME.HADOOP,
                componentTypeCodeList: this.state.checkedList
            })
        }
        if (isLibra) {
            params.engineList.push({
                engineName: ENGINE_TYPE_NAME.LIBRA,
                componentTypeCodeList: [COMPONENT_TYPE_VALUE.LIBRA_SQL]
            })
        }
        return params
    }
    /**
     * 校验是否选择同类引擎
     */
    validateEngineType = () => {
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
    /**
     *  校验是否选择组件
     */
    validateComponent = () => {
        const { checkedList } = this.state;
        const notAddComponetVal = this.filterExistData() || [];
        let validate = false;
        const notAddCompontLength = notAddComponetVal.length;
        if (checkedList.length > 0) {
            validate = true
        } else if (checkedList.length === 0 && notAddCompontLength != 0) {
            message.error('请选择增加的组件！')
        } else {
            return;
        }
        return validate
    }
    /**
     * 获取新增集群参数
     */
    getAddClusterParams () {
        const { validateFields } = this.props.form;
        const { isAddMultiple } = this.state;
        let validateParams = ['clusterName', 'engineName'];
        if (isAddMultiple) {
            validateParams.push('libraEngineName')
        }
        let params: any = {
            reqParams: {},
            canSubmit: false
        }
        validateFields(validateParams, {}, (err: any, value: any) => {
            if (!err) {
                const isDiffentEngine = this.validateEngineType();
                if (isDiffentEngine) {
                    params.canSubmit = true
                    params.reqParams = this.getServerParams(value);
                }
            }
        })
        return params
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
     * 获取增加引擎和增加组件参数
     */
    getEngineAndCompParams () {
        const { getFieldValue } = this.props.form;
        const engineName = getFieldValue('engineName');
        const { isAddComp } = this.props;
        const isHadoop = engineName == ENGINE_TYPE_NAME.HADOOP;
        let params: any = {
            reqParams: {},
            canSubmit: false
        }
        const validate = isAddComp ? this.validateComponent() : this.validateIsExistEngine();
        if (validate) { // 校验通过
            params.reqParams = {
                engineName,
                componentTypeCodeList: isHadoop || isAddComp ? this.state.checkedList : [COMPONENT_TYPE_VALUE.LIBRA_SQL]
            }
            params.canSubmit = true
        }
        return params
    }
    addEngine = () => {
        const { setFieldsValue } = this.props.form;
        this.setState({
            isAddMultiple: true
        }, () => { setFieldsValue({ libraEngineName: defaultEngine }) })
    }
    delEngine = () => {
        const { setFieldsValue } = this.props.form;
        this.setState({
            isAddMultiple: false
        })
        setFieldsValue({ libraEngineName: '' })
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
    renderDiffentEngine = (flag: any) => {
        const { getFieldDecorator } = this.props.form;
        const { isAddComp } = this.props;
        const { checkAll, checkedList } = this.state;
        const notAddComponetVal = this.filterExistData();
        // 新增组件筛选已添加组件
        const options = isAddComp ? notAddComponetVal : HADOOP_GROUP_VALUE;
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
                            <span>{'HDFS、YARN、SFTP'}</span>
                        )}
                    </FormItem>
                }
                {
                    notAddComponetVal.length == 0 ? (
                        <div style={{ textAlign: 'center' }}>暂无组件可添加</div>
                    ) : (
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
                    )
                }
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
                        initialValue: COMPONENT_TYPE_VALUE.LIBRA_SQL
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
        const { isAddMultiple } = this.state;
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
                                isAddCluster && !isAddMultiple && <a className='engine-opera' onClick={this.addEngine}>添加引擎</a>
                            }
                        </FormItem>
                    }
                    {this.renderDiffentEngine(hadoopFlag)}

                    {/* 新增集群modal中添加多个引擎 */}
                    {
                        isAddMultiple ? <React.Fragment>
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
                                    isAddCluster && isAddMultiple && <a className='engine-opera' onClick={this.delEngine}>删除引擎</a>
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
export default Form.create<any>()(AddCommModal);
