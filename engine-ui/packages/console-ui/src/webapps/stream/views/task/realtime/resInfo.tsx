import * as React from 'react'
import {
    Modal
} from 'antd'

import utils from 'utils'
import { getContainer } from 'funcs';

class ResInfoModal extends React.Component<any, any> {
    render () {
        const {
            title, data,
            handCancel, visible
        } = this.props
        return (
            <div id="JS_res_modal">
                <Modal
                    title={title}
                    wrapClassName="vertical-center-modal"
                    visible={visible}
                    onCancel={handCancel}
                    footer={null}
                    cancelText="关闭"
                    getContainer={() => getContainer('JS_res_modal')}
                >
                    <div className="ant-table ant-table-bordered bd-top bd-left" >
                        <table>
                            <tbody className="ant-table-tbody" >
                                <tr><td>资源名称</td><td>{data.resourceName}</td></tr>
                                <tr><td>责任人</td><td>{data.createUser ? data.createUser.userName : ''}</td></tr>
                                <tr>
                                    <td>资源类型</td>
                                    <td>{data.resourceType === 1 ? 'jar' : 'file'}</td>
                                </tr>
                                <tr>
                                    <td>最近修改时间</td>
                                    <td>{utils.formatDateTime(data.gmtModified)}</td>
                                </tr>
                                <tr><td>描述</td><td>{data.resourceDesc}</td></tr>
                            </tbody>
                        </table>
                    </div>
                </Modal>
            </div>
        )
    }
}
export default ResInfoModal
