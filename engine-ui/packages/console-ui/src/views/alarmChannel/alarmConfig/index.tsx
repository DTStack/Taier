import React, { useState, useEffect } from 'react'
import { Breadcrumb, Card, Button, Form,
    Modal, Icon, Popconfirm, message } from 'antd'
import Api from '../../../api/console'

import FormConfig from '../../clusterManage/newEdit/formConfig'
import { COMPONENT_TYPE_VALUE } from '../../clusterManage/newEdit/const'
import { validateConfig, getConfig, getTemplate,
    formItemLayout, getInitailConfig } from './help'

const confirm = Modal.confirm

interface IState {
    loading: boolean;
    componentConfig: string;
    componentTemplate: string;
}

const AlarmConfig: React.FC = (props: any) => {
    const [state, setState] = useState<IState>({
        loading: false,
        componentConfig: JSON.stringify({}),
        componentTemplate: JSON.stringify([])
    })

    const comp = {
        componentTypeCode: COMPONENT_TYPE_VALUE.SFTP,
        componentTemplate: state.componentTemplate,
        componentConfig: state.componentConfig
    }

    useEffect(() => {
        const getAlarmConfig = async () => {
            const res = await Api.getAlarmConfig();
            if (res && res?.code == 1) {
                const { componentConfig = JSON.stringify({}), componentTemplate = JSON.stringify([]) } = res?.data
                setState(state => ({ ...state, componentConfig, componentTemplate }))
            }
        }
        getAlarmConfig().then()
    }, [])

    const onCancle = () => {
        props.form.setFieldsValue({
            [comp.componentTypeCode]: { componentConfig: getInitailConfig(comp) }
        })
    }

    const onOk = (isTestConnect?: boolean) => {
        props.form.validateFields((err: any, values: any) => {
            console.log(err, values)
            if (err) {
                message.error('请检查配置')
                return
            }
            const currentComp = values[comp.componentTypeCode]
            if (!isTestConnect) {
                saveConfig(currentComp)
                return
            }
            testConect(currentComp)
        })
    }

    const saveConfig = (currentComp: any) => {
        const params = {
            componentConfig: JSON.stringify(getConfig(currentComp, comp)),
            componentTemplate: JSON.stringify(getTemplate(currentComp, comp))
        }
        if (validateConfig(currentComp, comp)) {
            message.success('保存成功')
            setState(state => ({ ...state, ...params }))
            return
        }
        confirm({
            title: '确认变更配置？',
            content: '配置变更，可能导致告警通道无法使用，确认变更？',
            icon: <Icon style={{ color: '#FAAD14' }} type="exclamation-circle" theme="filled" />,
            okText: '保存',
            cancelText: '取消',
            onOk: async () => {
                const res = await Api.updateAlarmConfig({ ...params })
                if (res.code !== 1) {
                    message.success('保存失败')
                    return
                }
                message.success('保存成功')
                setState(state => ({ ...state, ...params }))
            },
            onCancel: () => {}
        })
    }

    const testConect = async (currentComp: any) => {
        const params = {
            componentConfig: JSON.stringify(getConfig(currentComp, comp)),
            componentTemplate: JSON.stringify(getTemplate(currentComp, comp))
        }
        if (!validateConfig(currentComp, comp)) {
            message.error('SFTP配置参数变更未保存，请先保存再测试组件连通性')
            return
        }
        setState(state => ({ ...state, loading: true }))
        const res = await Api.testAlarmConfig()
        if (res.code !== 1) {
            message.error('测试连通性失败')
            return
        }
        message.success('测试连通性成功')
        setState({ ...state, ...params, loading: false })
    }

    return (
        <div className='alarm-config__wrapper'>
            <Breadcrumb>
                <Breadcrumb.Item> <a onClick={() => {
                    props.router.push('/console/alarmChannel')
                }}>告警通道</a></Breadcrumb.Item>
                <Breadcrumb.Item>SFTP配置</Breadcrumb.Item>
            </Breadcrumb>
            <Card bordered={false}>
                <FormConfig
                    view={false}
                    form={props.form}
                    itemLayout={formItemLayout}
                    comp={comp}
                />
                <footer className='alarm-config__footer'>
                    <Popconfirm
                        title="确认取消当前更改？"
                        okText="确认"
                        cancelText="取消"
                        onConfirm={() => onCancle()}
                    >
                        <Button>取消</Button>
                    </Popconfirm>
                    <Button ghost loading={state.loading} onClick={() => onOk(true)}>测试SFTP连通性</Button>
                    <Button type='primary' onClick={() => onOk()}>保存SFTP组件</Button>
                </footer>
            </Card>
        </div>
    )
}

export default Form.create()(AlarmConfig)
