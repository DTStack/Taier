import * as React from 'react'

import Editor from 'widgets/code-editor'

import Api from '../../../../../api'

const editorOptions: any = {
    mode: 'text',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true
}

class Failover extends React.Component<any, any> {
    state: any = {
        logInfo: ''
    }
    isUnmount = false;
    componentDidMount () {
        this.getLog();
    }
    async getLog () {
        const data = this.props.data;
        if (!data || !data.id) {
            return;
        }
        let res: any;
        res = await Api.getFailoverLogsByTaskId({ taskId: data.id });
        if (res && res.code == 1) {
            this.setState({
                logInfo: res.data
            });
        }
    }
    getBaseInfo () {
        const { isShow } = this.props;
        const { logInfo } = this.state;
        /**
         * 不显示的时候这里不能渲染，
         * 因为Editor和echarts绘图的时候会计算当前dom大小
         * 不显示的时候大小为0，会造成显示错误
         */
        if (!isShow) {
            return null;
        }
        return <div style={{ paddingLeft: '8px', height: '100%', background: '#f7f7f7' }}>
            <div style={{ height: '100%' }}>
                <Editor style={{ height: '100%' }} sync value={logInfo || ''} options={editorOptions} />
            </div>
        </div>
    }
    render () {
        return this.getBaseInfo();
    }
}

export default Failover;
