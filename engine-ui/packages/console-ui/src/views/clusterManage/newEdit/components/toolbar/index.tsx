import * as React from 'react'
import { Popconfirm, Button, message } from 'antd'
import Api from '../../../../../api/console'
import { COMPONENT_CONFIG_NAME } from '../../const'

import { handleComponentTemplate, handleComponentConfigAndCustom,
    handleComponentConfig, isNeedTemp, handleCustomParam,
    isKubernetes } from '../../help'
interface IProps {
    form: any;
    comp: any;
    clusterInfo: any;
    initialCompData: any[];
    saveComp: Function;
    testConnects: Function;
}

interface IState {
    loading: boolean;
}
export default class ToolBar extends React.PureComponent<IProps, IState> {
    state: IState = {
        loading: false
    }

    onOk = () => {
        const { form, comp, clusterInfo, saveComp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
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
            const currentComp = values[typeCode]
            let componentConfig: any
            if (!isNeedTemp(typeCode)) componentConfig = JSON.stringify(handleComponentConfigAndCustom(values[typeCode], typeCode))
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
                hadoopVersion: currentComp.hadoopVersion ?? '',
                isMetadata: currentComp.isMetadata ? 1 : 0,
                componentTemplate: isNeedTemp(typeCode)
                    ? (!currentComp.customParam ? '[]' : JSON.stringify(handleCustomParam(currentComp.customParam)))
                    : JSON.stringify(handleComponentTemplate(values[typeCode], comp)),
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
                        // id: res.data.id,
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
        this.props.testConnects(typeCode, (loading: boolean) => {
            this.setState({ loading })
        })
    }

    onConfirm = () => {
        const { form, comp, initialCompData } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const initialComp = initialCompData.find(comp => comp.componentTypeCode == typeCode)

        form.setFieldsValue({
            [typeCode]: {
                componentConfig: handleComponentConfig({
                    componentConfig: JSON.parse(initialComp.componentConfig)
                }, true)
                // customParam: JSON.parse(initialComp.componentTemplate)
            }
        })
    }

    render () {
        const { loading } = this.state
        const typeCode = this.props.comp?.componentTypeCode ?? ''

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
                <Button style={{ marginLeft: 8 }} loading={loading} ghost onClick={this.testConnects}>
                    测试{`${COMPONENT_CONFIG_NAME[typeCode]}`}连通性
                </Button>
                <Button style={{ marginLeft: 8 }} type="primary" onClick={this.onOk}>
                    保存{`${COMPONENT_CONFIG_NAME[typeCode]}`}组件
                </Button>
            </div>
        )
    }
}
