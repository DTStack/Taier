import React, { useState, useEffect } from 'react';
import { Modal, Form, Select, Icon, Tooltip, Input, message } from 'antd';

import FormItem from '../publicForm'

import { useEnv } from '../customHooks'
import Api from '../../api/console'
import { formItemLayout, specFormItemLayout } from '../../consts'
const Option = Select.Option;

const DynamicForm = (props: any) => {
    const { getFieldDecorator, lineList, taskTypeName, deleteItem, type } = props
    const lineTitle = Object.keys(lineList)
    return (
        <>
            <header className='c-header-dynamicform' >
                <span>{`${taskTypeName}任务`}</span>
                <span style={{ cursor: 'pointer' }} onClick={() => deleteItem(type)}><Icon type="delete" /></span>
            </header>
            <div style={{ paddingBottom: 10 }}>
                {
                    lineTitle.map((item) => (
                        <FormItem
                            key={item}
                            name={item.replace(/(\.)/g, '-')}
                            formOptions={{
                                label: item,
                                ...specFormItemLayout
                            }}
                            getFieldDecorator={getFieldDecorator}
                            fieldDecoratorOptions={{
                            }}
                        >
                            <Input style={{ width: '90%' }} placeholder={lineList[item]} />
                            {/* <span style={{ marginLeft: 10 }} >{sourcetype.includes(item) ? 'm' : ''}</span> */}
                        </FormItem>
                    ))
                }
            </div>
        </>
    )
}

const CustomModal: React.FC = (props: any) => {
    const { form: { getFieldDecorator, setFieldsValue }, visible, onOk, onCancel, title, isBindTenant,
        tenantInfo, clusterId } = props
    const [typeList, setTypeList] = useState({ current: undefined, union: [] })
    const [dataList, setDataList] = useState([])
    const [queueList, setQueueList] = useState([])

    // 切换集群
    useEffect(() => {
        if (clusterId) {
            Api.getTaskResourceTemplate({}).then((res: any) => {
                console.log(res)
                if (res.code === 1) {
                    setDataList(res.data)
                }
            })
            const { clusterList } = props;
            const {queueList}=useEnv(clusterId,props?.form,clusterList)
            setQueueList(queueList)
        }
        return () => {
            setTypeList({ current: undefined, union: [] })
            setDataList([])
            setQueueList([])
        }
    }, [clusterId, props])

    const getServiceParam = () => {
        // let params: any = {
        //     canSubmit: false,
        //     reqParams: {}
        // }
        // const { getFieldsValue, validateFields } = props?.form;
        // const reqParams = getFieldsValue();
        // const { tenantInfo = {}, isBindTenant } = props;
        // validateFields((err: any) => {
        //     if (!err) {
        //         params.canSubmit = true;
        //         params.reqParams = isBindTenant ? reqParams : Object.assign(reqParams, { tenantId: tenantInfo.tenantId }); // 切换队列覆盖默认值name
        //     }
        // })
        // return params
        const { validateFields } = props?.form;
        validateFields((err: any, value) => {
            if (!err) {
                console.log(value)
            }
        })
    }

    const addTaskType = () => {
        const { current, union } = typeList
        if (typeof current === 'undefined' || isNaN(current)) {
            message.warning('请先选择任务类型')
            return
        }
        if (union.length !== 0 && union.includes(current)) {
            message.warning('您已添加过此项')
        }
        setTypeList({ current, union: [...union, current] })
        setFieldsValue({ resourceType: current })
    }

    const changeCurrent = (e) => {
        setTypeList({ ...typeList, current: Number(e) })
    }

    const actionDom = () => {
        return (
            <div className='o-div--actionDom'
                onClick={addTaskType}
            >
                <Icon className='o-icon--actionDom' type="plus-circle" />添加资源限制
            </div>
        )
    }

    const removeType = (type) => {
        const { union } = typeList
        console.log(union, type)
        if (typeof type !== 'undefined') {
            const deleteIndex = union.indexOf(type)
            console.log(deleteIndex)
            deleteIndex !== -1 && setTypeList({ ...typeList, union: [...union.filter((numType) => numType !== type)] })
        }
    }

    return (
        <Modal
            title={title}
            visible={visible}
            onOk={() => { onOk(getServiceParam()) }}
            onCancel={onCancel}
            width='600px'
            className={isBindTenant ? 'no-padding-modal' : ''}
        >
            <Form>
                <FormItem
                    name='queueId'
                    getFieldDecorator={getFieldDecorator}
                    formOptions={{
                        label: (
                            <span>
                                    资源队列&nbsp;
                                <Tooltip title="指Yarn上分配的资源队列，若下拉列表中无全部队列，请前往“多集群管理”页面的具体集群中刷新集群">
                                    <Icon type="question-circle-o" />
                                </Tooltip>
                            </span>
                        ),
                        ...formItemLayout
                    }}
                    fieldDecoratorOptions={{
                        rules: [{
                            required: true,
                            message: '租户不可为空！'
                        }],
                        initialValue: tenantInfo && `${tenantInfo.tenantName}`
                    }}
                >
                    <Select
                        allowClear
                        placeholder='请选择资源队列'
                    >
                        {queueList.map((item: any) => {
                            return <Option key={`${item.queueId}`} value={`${item.queueId}`}>{item.queueName}</Option>
                        })}
                    </Select>
                </FormItem>
                <FormItem
                    name='resourceType'
                    getFieldDecorator={getFieldDecorator}
                    formOptions={{
                        label: (
                            <span>
                                    资源限制&nbsp;
                                <Tooltip title="设置租户下单个离线任务在临时运行和周期运行时能使用的最大资源数，任务的环境参数设置超出此限制将导致任务提交或运行失败。保存变更后立即生效。">
                                    <Icon type="question-circle-o" />
                                </Tooltip>
                            </span>
                        ),
                        ...formItemLayout
                    }}
                    fieldDecoratorOptions={{
                        rules: [{
                            required: true,
                            message: '请选择任务类型'
                        }]
                    }}
                    bottomSolt={(
                        actionDom()
                    )}
                >
                    <Select
                        allowClear
                        placeholder='请选择任务类型'
                        onChange={(e) => changeCurrent(e)}
                    >
                        {dataList.map((item: any) => {
                            return <Option key={`${item.taskType}`} value={`${item.taskType}`}>{item.taskTypeName}</Option>
                        })}
                    </Select>
                </FormItem>
                {
                    typeList.union.map(item => {
                        return dataList.map((type, key) => {
                            return (
                                <div key={key} className='o-block--dynamic' >
                                    {type.taskType === item
                                        ? (<DynamicForm
                                            type={item}
                                            getFieldDecorator={getFieldDecorator}
                                            lineList={type.params}
                                            taskTypeName={type.taskTypeName}
                                            deleteItem={removeType}
                                        />) : null}
                                </div>
                            )
                        })
                    })
                }
            </Form>

        </Modal>
    )
}
const areEqual = (prevprops, nextprops) => {
    if (prevprops.visible !== nextprops.visible) return false
    return true
}

export default Form.create<any>()(React.memo(CustomModal, areEqual));
