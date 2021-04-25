import * as React from 'react'
import { Popconfirm, Button, message, Modal, Icon } from 'antd'
import Api from '../../../../../api/console'
import { COMPONENT_CONFIG_NAME, COMP_ACTION, VERSION_TYPE } from '../../const'

import { handleComponentTemplate, handleComponentConfigAndCustom,
    handleComponentConfig, isNeedTemp, handleCustomParam,
    isKubernetes, isMultiVersion } from '../../help'
interface IProps {
    form: any;
    comp: any;
    clusterInfo: any;
    mulitple?: boolean;
    saveComp: (params: any, type?: string) => void;
    handleConfirm?: (action: string, comps: any | any[], mulitple?: boolean) => void;
    testConnects?: Function;
}

interface IState {
    loading: boolean;
}
export default class ToolBar extends React.PureComponent<IProps, IState> {
    state: IState = {
        loading: false
    }

    onOk = () => {
        const { form, comp, clusterInfo, saveComp, mulitple } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''

        // 整理相关参数, 更新初始值
        form.validateFields(null, {}, (err: any, values: any) => {
            console.log(err, values)
            if (err && Object.keys(err).includes(String(typeCode))) {
                message.error('请检查配置');
                return;
            }

            /**
             * componentTemplate yarn等组件直接传自定义参数，其他组件需处理自定义参数和入group中
             * componentConfig yarn等组件传值specialConfig，合并自定义参数，其他组件需处理自定义参数合并到对应config中
             */
            let currentComp = values[typeCode]
            if (mulitple && hadoopVersion) { currentComp = values[typeCode][hadoopVersion] }

            let componentConfig: any
            if (!isNeedTemp(typeCode)) {
                componentConfig = JSON.stringify(handleComponentConfigAndCustom(currentComp, typeCode))
            }
            if (isNeedTemp(typeCode)) {
                componentConfig = JSON.stringify({
                    ...currentComp?.specialConfig,
                    ...handleCustomParam(currentComp.customParam, true)
                })
            }
            if (isKubernetes(typeCode)) componentConfig = JSON.stringify(currentComp?.specialConfig)

            const params = {
                storeType: currentComp?.storeType ?? '',
                principal: currentComp?.principal ?? '',
                principals: currentComp?.principals ?? [],
                hadoopVersion: mulitple ? hadoopVersion : currentComp.hadoopVersion ?? '',
                isMetadata: currentComp.isMetadata ? 1 : 0,
                componentTemplate: isNeedTemp(typeCode)
                    ? (!currentComp.customParam ? '[]' : JSON.stringify(handleCustomParam(currentComp.customParam)))
                    : JSON.stringify(handleComponentTemplate(currentComp, comp)),
                componentConfig
            }
            /**
             * TODO LIST
             * resources2, kerberosFileName 这个两个参数后期可以去掉
             * 保存组件后不加上组件id，防止出现上传文件后立即点击不能下载的现象，后续交互优化
             */
            Api.saveComponent({
                ...params,
                clusterId: clusterInfo.clusterId,
                componentCode: typeCode,
                clusterName: clusterInfo.clusterName,
                resources1: currentComp?.uploadFileName ?? '',
                resources2: '',
                kerberosFileName: currentComp?.kerberosFileName?.name ?? ''
            }).then((res: any) => {
                if (res.code == 1) {
                    saveComp({
                        ...params,
                        id: res.data.id,
                        componentTypeCode: typeCode,
                        uploadFileName: currentComp?.uploadFileName ?? '',
                        kerberosFileName: currentComp?.kerberosFileName ?? ''
                    })
                    message.success('保存成功')
                }
            })
        })
    }

    testConnects = () => {
        const typeCode = this.props.comp?.componentTypeCode ?? ''
        const hadoopVersion = isMultiVersion(typeCode) ? this.props.comp?.hadoopVersion : ''
        this.props.testConnects({ typeCode, hadoopVersion }, (loading: boolean) => {
            this.setState({ loading })
        })
    }

    onConfirm = () => {
        const { form, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = isMultiVersion(typeCode) ? this.props.comp?.hadoopVersion : ''
        const componentConfig = handleComponentConfig({
            componentConfig: comp?.componentConfig ? JSON.parse(comp?.componentConfig) : {}
        }, true)
        const fieldValue = isMultiVersion(typeCode)
            ? { [hadoopVersion]: { componentConfig } } : { componentConfig }

        form.setFieldsValue({ [typeCode]: fieldValue })
    }

    showModal = () => {
        const { comp, handleConfirm, mulitple } = this.props
        Modal.confirm({
            title: '确认要删除组件？',
            content: '此操作执行后不可逆，是否确认将当前组件删除？',
            icon: <Icon type="close-circle" theme="filled" style={{ color: '#FF5F5C' }} />,
            okText: '删除',
            okType: 'danger',
            cancelText: '取消',
            onOk: () => {
                handleConfirm(COMP_ACTION.DELETE, comp, mulitple)
            },
            onCancel: () => {
                console.log('Cancel')
            }
        })
    }

    render () {
        const { loading } = this.state
        const { comp, mulitple } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''
        const defaultText = COMPONENT_CONFIG_NAME[typeCode]
        const multipleText = VERSION_TYPE[typeCode] + ' ' + (Number(hadoopVersion) / 100).toFixed(2)

        if (isMultiVersion(typeCode) && !mulitple) {
            return (
                <div className="c-toolbar__container">
                    <Button style={{ marginLeft: 8 }} onClick={this.showModal}>删除{`${defaultText}`}组件</Button>
                </div>
            )
        }

        return (
            <div className="c-toolbar__container">
                <Popconfirm
                    title="确认取消当前更改？"
                    okText="确认"
                    cancelText="取消"
                    onConfirm={this.onConfirm}
                >
                    <Button>取消</Button>
                </Popconfirm>
                <Button style={{ marginLeft: 8 }} onClick={this.showModal}>
                    { mulitple ? `删除${multipleText}组件` : `删除${defaultText}组件` }
                </Button>
                <Button style={{ marginLeft: 8 }} loading={loading} ghost onClick={this.testConnects}>
                    { mulitple ? `测试${multipleText}连通性` : `测试${defaultText}连通性` }
                </Button>
                <Button style={{ marginLeft: 8 }} type="primary" onClick={this.onOk}>
                    { mulitple ? `保存${multipleText}组件` : `保存${defaultText}组件` }
                </Button>
            </div>
        )
    }
}
