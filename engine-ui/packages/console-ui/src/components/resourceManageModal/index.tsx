import React, { useState, useEffect, useRef } from 'react';
import { Modal, Form, Select, Icon, Tooltip, Input, message } from 'antd';

import { useEnv } from '../customHooks'
import Api from '../../api/console'
import { formItemLayout, specFormItemLayout, sourcetype } from '../../consts'

const Option = Select.Option;
const FormItem = Form.Item
const { confirm } = Modal

const DynamicForm = (props: any) => {
    const { getFieldDecorator, lineList, taskTypeName, deleteItem, type, initialList } = props
    const lineTitle = Object.keys(lineList)
    return (
        <>
            <header className='c-header-dynamicform' >
                <span>{`${taskTypeName}任务`}</span>
                <span style={{ cursor: 'pointer' }} onClick={() => deleteItem(type)}>
                    <Icon type="delete" />
                </span>
            </header>
            <div style={{ paddingBottom: 10 }}>
                {
                    lineTitle.map((item) => (
                        <FormItem
                            key={item}
                            label={item}
                            {...specFormItemLayout}
                        >
                            {getFieldDecorator(`${type} ${item.replace(/(\.)/g, '-')}`, {
                                initialValue: initialList?.[item] || undefined
                            })(
                                <Input style={{ width: '90%' }} placeholder={lineList[item]} />
                            )}
                            <span style={{ marginLeft: 10 }} >{sourcetype.includes(item) ? 'm' : ''}</span>
                        </FormItem>
                    ))
                }
            </div>
        </>
    )
}

const CustomModal: React.FC = (props: any) => {
    const { form, form: { getFieldDecorator, setFieldsValue, resetFields },
        visible, onOk, onCancel, title, isBindTenant,
        clusterId, tenantId, queueId, clusterList } = props
    const [isLoading, setLoading] = useState(false)
    const [typeList, setTypeList] = useState({ current: undefined, union: [] })
    const [dataList, setDataList] = useState([])
    const [initialList, setInitialList] = useState({})
    const prevVisible = useRef(null)
    const { queueList } = useEnv({ clusterId, form, visible: prevVisible.current !== visible && visible === true, clusterList })
    // 切换集群
    useEffect(() => {
        if (prevVisible.current !== visible) {
            if (visible === true) {
                Api.queryTaskResourceLimits({ dtUicTenantId: tenantId }).then((res: any) => {
                    if (res.code === 1) {
                        const union = []
                        const biginitial = {}
                        res.data.forEach(item => {
                            union.push(item.taskType)
                            biginitial[`${item.taskType}`] = item.resourceLimit
                            // setInitialList({...initialList,[`${item.taskType}`]:item.resourceLimit})
                        })
                        setInitialList(biginitial)
                        setTypeList(prev => { return { ...prev, union } })
                    }
                })
                Api.getTaskResourceTemplate({}).then((res: any) => {
                    if (res.code === 1) {
                        setDataList(res.data)
                    }
                })
            } else {
                resetFields()
                setTypeList({ current: undefined, union: [] })
                setDataList([])
            }
        }
        prevVisible.current = visible
    }, [resetFields, tenantId, visible])

    const getServiceParam = () => {
        const { validateFields } = props?.form;

        validateFields((err: any, value) => {
            if (!err) {
                const taskTypeResourceJson = JSON.stringify(typeList.union.map(item => {
                    return dataList.map(task => {
                        if (item === task.taskType) {
                            const blockList = Object.keys(task.params)
                            const params = { taskType: item, resourceParams: {} }
                            blockList.forEach(head => {
                                params.resourceParams[head] = value?.[`${item} ${head}`.replace(/(\.)/g, '-')]
                            })
                            return params
                        }
                    })
                }).map(arrayItem => {
                    return arrayItem.filter(element => element !== undefined)[0]
                }))
                setLoading(true)
                Api.switchQueue({ queueId: value?.queueId, tenantId, taskTypeResourceJson }).then((res: any) => {
                    if (res.code === 1) {
                        message.success('提交成功')
                        return onOk()
                    }
                    message.error('提交失败')
                }).finally(() => {
                    setLoading(false)
                })
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
            message.warning('该任务的资源限制已存在！')
            return
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
        if (typeof type !== 'undefined') {
            const deleteIndex = union.indexOf(type)
            deleteIndex !== -1 && setTypeList({ ...typeList, union: [...union.filter((numType) => numType !== type)] })
        }
    }

    const returnPromject = () => {
        confirm({
            title: '是否保存配置？',
            content: '若不保存编辑内容，再次打开时会进行重置',
            okText: '保存',
            cancelText: '返回',
            onOk () {
                getServiceParam()
            },
            onCancel () {
                onCancel()
            }
        });
    }
    return (
        <Modal
            title={title}
            visible={visible}
            onOk={() => getServiceParam()}
            onCancel={() => returnPromject()}
            width='600px'
            confirmLoading={isLoading}
            className={isBindTenant ? 'no-padding-modal' : ''}
        >
            <Form>
                <FormItem
                    label={
                        (<span>
                            资源队列&nbsp;
                            <Tooltip title="指Yarn上分配的资源队列，若下拉列表中无全部队列，请前往“多集群管理”页面的具体集群中刷新集群">
                                <Icon type="question-circle-o" />
                            </Tooltip>
                        </span>)
                    }
                    {...formItemLayout}
                >
                    {getFieldDecorator('queueId', {
                        rules: [{
                            required: true,
                            message: '租户不可为空！'
                        }],
                        initialValue: queueId || undefined
                    })(
                        <Select
                            allowClear
                            placeholder='请选择资源队列'
                        >
                            {queueList.map((item: any) => {
                                return <Option key={item.queueId} value={item.queueId}>{item.queueName}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    label={(
                        <span>
                            资源限制&nbsp;
                            <Tooltip title="设置租户下单个离线任务在临时运行和周期运行时能使用的最大资源数，任务的环境参数设置超出此限制将导致任务提交或运行失败。保存变更后立即生效。">
                                <Icon type="question-circle-o" />
                            </Tooltip>
                        </span>
                    )}
                    {...formItemLayout}
                >
                    {getFieldDecorator('resourceType', {
                    })(
                        <Select
                            allowClear
                            placeholder='请选择任务类型'
                            onChange={(e) => changeCurrent(e)}
                        >
                            {dataList.map((item: any) => {
                                return <Option key={item.taskType} value={item.taskType}>{item.taskTypeName}</Option>
                            })}
                        </Select>
                    )}
                    {actionDom()}
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
                                            initialList={initialList[item]}
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
    if (prevprops?.visible !== nextprops?.visible || nextprops?.visible === true) return false
    return true
}

export default Form.create<any>()(React.memo(CustomModal, areEqual))
