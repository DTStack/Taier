/*
* @Author: 12574
* @Date:   2018-09-18 16:58:30
* @Last Modified by:   12574
* @Last Modified time: 2018-09-28 11:43:51
*/
import * as React from 'react';
import { Modal } from 'antd';
import CodeEditor from 'dt-common/src/widgets/editor';
class ViewDetail extends React.Component<any, any> {
    state: any = {
        editor: {
            sql: '',
            cursor: undefined,
            sync: true
        }
    }
    render () {
        const { title } = this.props;
        return (
            <Modal
                title={title || '任务详情'}
                width={650}
                onCancel={this.props.onCancel}
                onOk={this.props.onCancel}
                visible={this.props.visible}
            >
                <CodeEditor
                    style={{ height: '400px', marginTop: '1px' }}
                    value={this.props.resource}
                    language="ini"
                    options={
                        {
                            readOnly: true,
                            minimap: {
                                enabled: false
                            }
                        }
                    }
                    sync={true}
                />
            </Modal>
        )
    }
}
export default ViewDetail;
