import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Modal, Form, Select, Input, Checkbox, message } from 'antd';
import { formItemLayout, CLUSTER_TYPES_VALUE, otherClustersOptions, huaWeiOptions } from '../../consts';
import { updateEngineList } from '../../actions/console';
import { hashHistory } from 'react-router';
import Api from '../../api/console';

const FormItem = Form.Item;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
const defaultCheckedValue = ['HDFS', 'YARN']; // 必选引擎

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
        indeterminate: false,
        checkedList: defaultCheckedValue,
        checkAll: false
    }
    changeClusterType = (value) => {
        this.setState({
            checkAll: false
        })
        if (value === CLUSTER_TYPES_VALUE.HUAWEI) {
            this.setState({
                checkedList: []
            })
        } else {
            this.setState({
                checkedList: defaultCheckedValue
            })
        }
    }
    getOtherClustersOptionsValue = () => {
        let otherClusterValues = [];
        otherClustersOptions.map(item => {
            otherClusterValues.push(item.value)
        })
        return otherClusterValues
    }
    onChange = (checkedList) => {
        const { getFieldValue } = this.props.form;
        const isHuaweiType = getFieldValue('type') === CLUSTER_TYPES_VALUE.HUAWEI;
        this.setState({
            checkedList,
            // indeterminate: !!checkedList.length && (checkedList.length < isHuaweiType ? huaWeiOptions.length : otherClustersOptions.length),
            checkAll: isHuaweiType ? checkedList.length === huaWeiOptions.length : checkedList.length === otherClustersOptions.length
        })
    }
    onCheckAllChange = (e) => {
        const { getFieldValue } = this.props.form;
        const isHuaweiType = getFieldValue('type') === CLUSTER_TYPES_VALUE.HUAWEI;
        this.setState({
            checkedList: e.target.checked ? (isHuaweiType ? huaWeiOptions : this.getOtherClustersOptionsValue())
                : isHuaweiType ? [] : defaultCheckedValue,
            checkAll: e.target.checked
        })
    }
    checkEngines = () => {
        if (this.state.checkedList.length === 0) {
            message.error('可选引擎不可为空！')
            return false
        }
        return true
    }
    addCluster = () => {
        this.props.form.validateFields(['type', 'name', 'engines'], {}, (err, value) => {
            if (!err && this.checkEngines()) {
                // dispatch enginelist
                this.props.updateEngineList(this.state.checkedList)
                Api.addCluster({
                    type: value.type,
                    name: value.name,
                    engines: this.state.checkedList
                }).then(res => {
                    if (res.code === 1) {
                        this.props.onCancel()
                        message.success('集群新增成功！')
                    }
                })
                // 采用接口返回数据
                hashHistory.push({
                    pathname: '/console/clusterManage/editCluster',
                    state: {
                        mode: 'new',
                        enginelist: this.state.checkedList,
                        clusterName: value.name,
                        clusterType: value.type
                        // clusterConfig: res.data.res.data,
                        // clusterId: res.data.id,
                        // totalNode: res.data.totalNode,
                        // totalMemory: res.data.totalMemory,
                        // totalCore: res.data.totalCore
                    }
                })
            }
        })
    }
    render () {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { indeterminate, checkAll, checkedList } = this.state;
        const engineType = getFieldValue('type');
        const isHaveRequiredEngine = engineType == CLUSTER_TYPES_VALUE.APACHEHADOOP || engineType == CLUSTER_TYPES_VALUE.CLOUDERA || engineType == undefined
        const isHuaweiEngine = engineType == CLUSTER_TYPES_VALUE.HUAWEI
        return (
            <Modal
                title='新增集群'
                visible={this.props.visible}
                onCancel={this.props.onCancel}
                onOk={this.addCluster}
            >
                <Form>
                    <FormItem
                        label='集群类型'
                        {...formItemLayout}
                    >
                        {getFieldDecorator('type', {
                            rules: [{
                                required: true,
                                message: '集群类型不可为空！'
                            }],
                            initialValue: CLUSTER_TYPES_VALUE.APACHEHADOOP
                        })(
                            <Select
                                onChange={this.changeClusterType}
                                placeholder='请选择集群类型'
                            >
                                <Option value={CLUSTER_TYPES_VALUE.APACHEHADOOP}>Apache Hadoop</Option>
                                <Option value={CLUSTER_TYPES_VALUE.HUAWEI}>华为</Option>
                                <Option value={CLUSTER_TYPES_VALUE.CLOUDERA}>Cloudera</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        label="集群标识"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('name', {
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
                        label="必选引擎"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('requiredEngines', {
                            rules: [{
                                required: true,
                                message: ''
                            }]
                        })(
                            <span>{isHaveRequiredEngine ? 'HDFS、YARN' : '无'}</span>
                        )}
                    </FormItem>
                    <FormItem
                        label={<span>
                            <span style={{ color: '#f04134', fontSize: '12px', fontFamily: 'SimSun' }}>* </span>
                            <span>可选引擎</span>
                        </span>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('engines', {
                            // rules: [{
                            //     validator: function (rule, value, callback) {
                            //         if (checkedList.length === 0) {
                            //             const error = '可选引擎不可为空！'
                            //             callback(error)
                            //             return;
                            //         }
                            //         callback();
                            //     }
                            // }]
                        })(
                            <div>
                                <Checkbox
                                    indeterminate={indeterminate}
                                    onChange={this.onCheckAllChange}
                                    checked={checkAll}
                                >全选</Checkbox>
                                <CheckboxGroup
                                    options={isHuaweiEngine ? huaWeiOptions : otherClustersOptions}
                                    value={checkedList}
                                    onChange={this.onChange}
                                />
                            </div>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(NewClusterModal);
