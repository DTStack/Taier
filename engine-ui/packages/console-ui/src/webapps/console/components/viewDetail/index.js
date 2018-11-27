/*
* @Author: 12574
* @Date:   2018-09-18 16:58:30
* @Last Modified by:   12574
* @Last Modified time: 2018-09-28 11:43:51
*/
import React, { Component } from 'react';
import { Modal, Table } from 'antd';
import moment from 'moment';
import CodeEditor from 'widgets/editor';
class ViewDetail extends Component {
    state = {
        editor: {
            sql: '',
            cursor: undefined,
            sync: true
        }
    }
    render () {
        const { editor } = this.state;
        return (
            <Modal
                title="任务详情"
                width={650}
                onCancel={this.props.onCancel}
                onOk={this.props.onCancel}
                visible={this.props.visible}
            >
                <CodeEditor
                    style={{ height: '400px', marginTop: '1px' }}
                    // onChange={this.props.sqlOnChange}
                    value={JSON.stringify(this.props.resource, null, 2)}
                    language="ini"
                    options={
                        {
                            readOnly: true,
                            minimap: {
                                enabled: false
                            }
                        }
                    }
                    // // cursor={this.props.editor.cursor}
                    sync={true}
                />
            </Modal>
        )
    }
}
export default ViewDetail;
