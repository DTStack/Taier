import * as React from 'react'
import { IStreamTaskProps } from '@/interface'
import Editor from '@/components/codeEditor'

const Api = {} as any

interface IState {
    logInfo: string;
}

interface IProps {
    data: IStreamTaskProps | undefined;
    isShow: boolean;
}

const editorOptions = {
    mode: 'text',
    lineNumbers: true,
    readOnly: true,
    autofocus: false,
    indentWithTabs: true,
    smartIndent: true
}

class Failover extends React.Component<IProps, IState> {
    state: IState = { logInfo: '' }

    componentDidMount () {
        this.getLog();
    }

    componentDidUpdate (oldProps: IProps) {
        // 回到该 tab 后重新请求
        if (!oldProps.isShow && this.props.isShow) {
            this.getLog();
        }
    }

    async getLog () {
        const data = this.props.data;
        if (!data?.id) return
        let res: any;
        res = await Api.getFailoverLogsByTaskId({ taskId: data.id });
        if (res?.code == 1) {
            this.setState({
                logInfo: res?.data
            });
        }
    }

    render () {
        const { isShow } = this.props;
        const { logInfo } = this.state;
        /**
         * 不显示的时候这里不能渲染，
         * 因为Editor和echarts绘图的时候会计算当前dom大小
         * 不显示的时候大小为0，会造成显示错误
         */
        return isShow ? 
            <div style={{ marginLeft: '20px', paddingLeft: '8px', height: 'calc(100% - 56px)', background: '#f7f7f7' }}>
                <div style={{ height: '100%' }}>
                    <Editor style={{ height: '100%' }} sync value={logInfo || ''} options={editorOptions} />
                </div>
            </div> : null
    }
}

export default Failover;
