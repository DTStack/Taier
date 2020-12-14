import * as React from 'react'
import { Popconfirm, Button, message } from 'antd'
import Api from '../../../../api/console';

import { handleComponentTemplate, handleComponentConfigAndCustom,
    handleComponentConfig, isNeedTemp, handleCustomParam } from '../help'
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
            if (err) {
                if (Object.keys(err).includes(String(typeCode))) {
                    message.error('请检查配置');
                    return;
                }
            }
            if (!err) {
                // const arr = getModifyComp(values, initialCompData)
                const currentComp = values[typeCode]
                const params = {
                    storeType: currentComp?.storeType ?? '',
                    principal: currentComp?.principal ?? '',
                    principals: currentComp?.principals ?? '',
                    hadoopVersion: currentComp.hadoopVersion ?? '',
                    componentTemplate: isNeedTemp(typeCode) ? JSON.stringify(handleCustomParam(currentComp.customParam)) : JSON.stringify(handleComponentTemplate(values[typeCode], comp)),
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
                            uploadFileName: currentComp?.uploadFileName ?? '',
                            kerberosFileName: currentComp?.kerberosFileName ?? ''
                        })
                        message.success('保存成功')
                    }
                })
            }
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
            }
        })
    }

    render () {
        return (
            <div className="c-toolbar__container">
                <Popconfirm
                    title="确认取消当前更改？"
                    okText="确认"
                    cancelText="取消"
                    onConfirm={this.onConfirm}
                >
                    <Button style={{ width: 88 }}>取消</Button>
                </Popconfirm>
                <Button type="primary" style={{ marginLeft: 8, width: 88 }} onClick={this.onOk}>保存</Button>
            </div>
        )
    }
}
