import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Modal, Form, Input, Checkbox, message } from 'antd';
import EngineSelect from '../../../../webapps/rdos/components/engineSelect';
import { formItemLayout, ENGINE_TYPE_ARRAY, ENGINE_TYPE_NAME, COMPONENT_TYPE_VALUE, hadoopEngineOptionsValue } from '../../consts';
import { updateEngineList } from '../../actions/console';
import { hashHistory } from 'react-router';
import Api from '../../api/console';

const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;
const defaultCheckedValue = [COMPONENT_TYPE_VALUE.HDFS, COMPONENT_TYPE_VALUE.YARN]; // 必选引擎值
const defaultEngine = ENGINE_TYPE_NAME.HADOOP // hadoop
function mapStateToProps (state) {
    return {
        consoleUser: state.consoleUser
    }
}
function mapDispatchToProps (dispatch) {
    return {
        updateEngineList (params) {
            dispatch(updateEngineList(params))
        }
    }
}
@connect(mapStateToProps, mapDispatchToProps)
class NewClusterModal extends Component {
    state = {
        checkedList: defaultCheckedValue,
        checkAll: false,
        isShowLibra: false // 是否添加Libra引擎
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
        this.setState({
            checkedList: e.target.checked ? this.tranEngineData() : defaultCheckedValue,
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
        console.log('ServerParams', params)
        return params
    }
    validateEngine = () => {
        const { getFieldValue } = this.props.form;
        const hadoopOption = getFieldValue('engineName')
        const libraOption = getFieldValue('libraEngineName')
        let flag = true;
        console.log(hadoopOption, libraOption)
        if (hadoopOption === libraOption) {
            message.error('不能选择同类的引擎！')
            flag = false
        }
        return flag
    }
    addCluster = () => {
        this.props.form.validateFields(['clusterName', 'engineName', 'libraEngineName'], {}, (err, value) => {
            if (!err) {
                if (this.validateEngine()) {
                    this.getServerParams(value);
                    const params = this.getServerParams(value);
                    // dispatch enginelist
                    // this.props.updateEngineList(this.state.checkedList)
                    Api.addCluster({ ...params }).then(res => {
                        if (res.code === 1) {
                            this.props.onCancel()
                            message.success('集群新增成功！')
                            // 采用接口返回数据
                            hashHistory.push({
                                pathname: '/console/clusterManage/editCluster',
                                state: {
                                    mode: 'new',
                                    enginelist: this.state.checkedList,
                                    clusterName: value.clusterName,
                                    clusterType: value.type,
                                    totalNode: res.data.totalNode,
                                    totalMemory: res.data.totalMemory,
                                    totalCore: res.data.totalCore,
                                    clusterConfig: res.data,
                                    clusterId: res.data.id
                                }
                            })
                        }
                    })
                }
            }
        })
    }
    addEngine = () => {
        this.setState({
            isShowLibra: true
        })
    }
    delEngine = () => {
        this.setState({
            isShowLibra: false
        })
    }
    renderDiffentEngine = (flag) => {
        const { getFieldDecorator } = this.props.form;
        const { checkAll, checkedList } = this.state;
        return (
            flag ? <React.Fragment>
                <FormItem
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
                <FormItem
                    label={<span>
                        <span style={{ color: '#f04134', fontSize: '12px', fontFamily: 'SimSun' }}>* </span>
                        <span>可选组件</span>
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
                                options={hadoopEngineOptionsValue}
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
        const { isShowLibra } = this.state;
        const hadoopOption = getFieldValue('engineName')
        const libraOption = getFieldValue('libraEngineName')
        const hadoopFlag = hadoopOption === ENGINE_TYPE_NAME.HADOOP || hadoopOption == undefined;
        const libraFlag = libraOption === ENGINE_TYPE_NAME.HADOOP || libraOption == undefined;
        return (
            <Modal
                title='新增集群'
                visible={this.props.visible}
                onCancel={this.props.onCancel}
                onOk={this.addCluster}
            >
                <Form>
                    <FormItem
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
                    <FormItem
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
                            />
                        )}
                        {
                            isShowLibra ? null : <a className='engine-opera' onClick={this.addEngine}>添加引擎</a>
                        }
                    </FormItem>
                    {this.renderDiffentEngine(hadoopFlag)}
                    {/* libra */}
                    {
                        isShowLibra ? <React.Fragment>
                            <div className='dashed-line'></div>
                            <FormItem
                                label="引擎类型"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('libraEngineName', {
                                    rules: [{
                                        required: true,
                                        message: '引擎类型不可为空！'
                                    }],
                                    initialValue: defaultEngine
                                })(
                                    <EngineSelect
                                        placeholder='请选择引擎类型'
                                        tableTypes={ENGINE_TYPE_ARRAY}
                                    />
                                )}
                                {
                                    isShowLibra ? <a className='engine-opera' onClick={this.delEngine}>删除引擎</a> : null
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
export default Form.create()(NewClusterModal);
