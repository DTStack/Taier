import * as React from 'react'
import { Popconfirm, Button, message } from 'antd'
import Api from '../../../../api/console';

import { handleComponentTemplate, handleComponentConfig } from '../help'
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
        // 整理相关参数
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
                    componentCode: typeCode,
                    componentTemplate: JSON.stringify(handleComponentTemplate(values[typeCode], comp)),
                    componentConfig: JSON.stringify(handleComponentConfig(values[typeCode]))
                }
                Api.saveComponent({
                    ...params,
                    clusterId: clusterInfo.clusterId,
                    clusterName: clusterInfo.clusterName,
                    resources1: currentComp?.uploadFileName ?? ''
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

    render () {
        return (
            <div className="c-toolbar__container">
                <Popconfirm
                    title="确认取消当前更改？"
                    okText="确认"
                    cancelText="取消"
                >
                    <Button style={{ width: 88 }}>取消</Button>
                </Popconfirm>
                <Button type="primary" style={{ marginLeft: 8, width: 88 }} onClick={this.onOk}>保存</Button>
            </div>
        )
    }
}
