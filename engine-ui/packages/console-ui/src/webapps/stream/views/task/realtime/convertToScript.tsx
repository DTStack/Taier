import * as React from 'react';
import { Button, Modal } from 'antd';
import { connect } from 'react-redux';
import { convertToScriptMode } from '../../../store/modules/realtimeTask/browser';
const confirm = Modal.confirm;
class ConvertToScript extends React.Component<any, any> {
    state = {}
    onConvertDataSyncToScriptMode = () => {
        const { currentPage, convertToScriptMode } = this.props;
        console.log('currentPage', currentPage)
        confirm({
            title: '转换为脚本',
            content: (<div>
                <p style={{ color: '#f04134' }}>此操作不可逆，是否继续？</p>
                <p>当前为向导模式，配置简单快捷，脚本模式可灵活配置更多参数，定制化程度高</p>
            </div>),
            okText: '确认',
            cancelText: '取消',
            onOk: function () {
                convertToScriptMode(currentPage)
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    render () {
        const convertToScriptModeBtn = (<Button
            disabled={this.props.isLocked}
            icon="swap"
            {... { title: '向导模式转换为脚本模式' }}
            onClick={this.onConvertDataSyncToScriptMode}>
                转换为脚本
        </Button>);
        return (
            convertToScriptModeBtn
        )
    }
}
const mapState = (state: any) => {
    const currentPage = state.realtimeTask.currentPage
    return {
        pages: state.realtimeTask.pages,
        currentPage: currentPage,
        user: state.user,
        project: state.project
    }
};

const mapDispatch = (dispatch: any) => {
    return {
        convertToScriptMode: function (data: any) {
            dispatch(convertToScriptMode(data))
        }
    };
}

export default connect(mapState, mapDispatch)(ConvertToScript);
