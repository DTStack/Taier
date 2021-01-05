import * as React from 'react'
import { Popconfirm, Button, message } from 'antd'
import Api from '../../../../../api/console'
import { COMPONENT_CONFIG_NAME } from '../../const'

import { handleComponentTemplate, handleComponentConfigAndCustom,
    handleComponentConfig, isNeedTemp, handleCustomParam, getParamsByTemp } from '../../help'
interface IProps {
    form: any;
    comp: any;
    clusterInfo: any;
    initialCompData: any[];
    saveComp: Function;
}

export default class ToolBar extends React.PureComponent<IProps, any> {
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
            // const arr = getModifyComp(values, initialCompData)
            const currentComp = values[typeCode]
            const params = {
                storeType: currentComp?.storeType ?? '',
                principal: currentComp?.principal ?? '',
                principals: currentComp?.principals ?? [],
                hadoopVersion: currentComp.hadoopVersion ?? '',
                componentTemplate: isNeedTemp(typeCode)
                    ? (!currentComp.customParam ? '[]' : JSON.stringify(handleCustomParam(currentComp.customParam)))
                    : JSON.stringify(handleComponentTemplate(values[typeCode], comp)),
                componentConfig: isNeedTemp(typeCode) ? JSON.stringify(currentComp?.specialConfig) : JSON.stringify(handleComponentConfigAndCustom(values[typeCode]))
            }
            // TODO resources2, kerberosFileName 这个两个参数后期可以去掉
            Api.saveComponent({
                ...params,
                clusterId: clusterInfo.clusterId,
                componentCode: typeCode,
                clusterName: clusterInfo.clusterName,
                resources1: currentComp?.uploadFileName ?? '',
                resources2: '',
                kerberosFileName: ''
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

    onConfirm = () => {
        const { form, comp, initialCompData } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const initialComp = initialCompData.find(comp => comp.componentTypeCode == typeCode)

        form.setFieldsValue({
            [typeCode]: {
                componentConfig: handleComponentConfig({
                    componentConfig: JSON.parse(initialComp.componentConfig)
                }, true),
                customParam: getParamsByTemp(JSON.parse(initialComp.componentTemplate))
            }
        })
    }

    render () {
        const typeCode = this.props.comp?.componentTypeCode ?? ''
        // const { onConfirm } = this.props
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
                <Button style={{ marginLeft: 8 }} type="primary" onClick={this.onOk}>保存{`${COMPONENT_CONFIG_NAME[typeCode]}`}组件</Button>
            </div>
        )
    }
}
