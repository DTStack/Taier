import * as React from 'react'
import { message, Modal } from 'antd'
import Editor from 'dt-common/src/widgets/code-editor'
import { debounce } from 'lodash'
import Api from '../../../../api/console'

const editorStyle: any = { height: '100%' }

interface IProps {
    visible: boolean;
    krbconfig: string;
    onCancel: Function;
}

interface IState {
    krb5Content: string;
}

const editorOptions: any = {
    mode: 'simpleConfig',
    lineNumbers: false,
    readOnly: false,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true
}

export default class KerberosModal extends React.Component<IProps, IState> {
    state: IState = {
        krb5Content: ''
    }

    _editor: any;

    editorParamsChange = (preValue: string, nextValue: string) => {
        this.setState({
            krb5Content: nextValue
        })
    }

    debounceEditorChange = debounce(this.editorParamsChange, 300, { 'maxWait': 2000 })

    onOK = async () => {
        const { onCancel } = this.props
        const { krb5Content } = this.state
        const res = await Api.updateKrb5Conf({ krb5Content })
        if (res.code == 1) {
            onCancel()
            message.success('更新成功')
        }
    }

    render () {
        const { visible, onCancel, krbconfig } = this.props
        return <Modal
            title="合并后的krb5.conf"
            visible={visible}
            onCancel={() => onCancel()}
            onOk={this.onOK}
        >
            <div style={editorStyle}>
                <Editor
                    sync
                    value={krbconfig || ''}
                    className="c-kerberosModal__edior"
                    ref={(e: any) => this._editor = e}
                    style={{ height: '100%' }}
                    options={editorOptions}
                    onChange={this.debounceEditorChange.bind(this)}
                />
            </div>
        </Modal>
    }
}
