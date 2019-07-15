import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Layout, Spin } from 'antd';
import SplitPane from 'react-split-pane';
import { connect } from 'react-redux';

import Sidebar from './sidebar';
import SearchTaskModal from './searchTaskModal';
import { stopSql, getEditorThemeClassName } from '../../store/modules/editor/editorAction';
import { getUploadStatus, UPLOAD_STATUS } from '../../store/modules/uploader'

const { Content } = Layout;

const propType = {
    children: PropTypes.node
};
const defaultPro = {
    children: []
};

function mapStateToProps (state) {
    return { editor: state.editor, uploader: state.uploader };
}
function mapDispatchToProps (dispatch) {
    return {
        getUploadStatus: (params) => {
            dispatch(getUploadStatus(params))
        },
        stopSql: (currentTab, currentTabData, isSilent) => {
            dispatch(stopSql(currentTab, currentTabData, isSilent))
        }
    }
}
@connect(mapStateToProps, mapDispatchToProps)
class Container extends Component {
    constructor (props) {
        super(props);
        this.state = {
            loading: 'success'
        };
        this.loadIDETheme(this.props.editor.options.theme);
    }

    componentDidMount () {
        if (process.env.NODE_ENV === 'production') {
            window.addEventListener('beforeunload', this.beforeunload, false);
        }
        // Load uploader status from cache
        this.loadUploader();
    }

    componentWillUnmount () {
        const { stopSql, editor } = this.props;
        const running = editor.running;
        // 清楚所有运行中的tabs状态
        for (let i in running) {
            stopSql(running[i], null, true);
        }
        window.removeEventListener('beforeunload', this.beforeunload, false);
        this.unloadIDETheme();
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        if (nextProps.editor.options.theme !== this.props.editor.options.theme) {
            this.loadIDETheme(nextProps.editor.options.theme);
        }
    }

    beforeunload = e => {
        // eslint-disable-next-line
        const confirmationMessage = "\o/";
        (e || window.event).returnValue = confirmationMessage; // Gecko + IE
        return confirmationMessage; // Webkit, Safari, Chrome
    };

    showLoading = () => {
        const self = this;
        this.setState({ loading: 'loading' });
        setTimeout(() => {
            self.setState({ loading: 'success' });
        }, 200);
    };

    /**
     * If have unfinished upload task,
     * this function will to get upload status from cache data,
     * and goto call server side to get newest upload status.
     */
    loadUploader = () => {
        const { uploader, getUploadStatus } = this.props;
        const haveUndoneUpload = uploader && uploader.status !== UPLOAD_STATUS.READY && uploader.queryParams !== '';
        if (haveUndoneUpload) {
            getUploadStatus({
                queryParams: uploader.queryParams,
                fileName: uploader.fileName
            })
        }
    }

    loadIDETheme = (theme) => {
        console.log('componentDidMount 离线计算：', this.props.editor)
        const claName = getEditorThemeClassName(theme);
        document.body.className = claName;
    }

    unloadIDETheme = () => {
        console.log('componentWillUnmount 离线计算：', this.props.editor);
        document.body.className = '';
    }

    render () {
        const { children } = this.props;
        return (
            <Layout className="dt-dev-task">
                <SplitPane
                    split="vertical"
                    minSize={230}
                    maxSize="80%"
                    defaultSize={230}
                    primary="first"
                >
                    <div
                        className="ant-layout-sider"
                        style={{ width: 'inherit', height: '100%' }}
                    >
                        <Sidebar />
                        <SearchTaskModal />
                    </div>
                    <Content style={{ height: '100%' }}>
                        <Spin
                            tip="Loading..."
                            size="large"
                            spinning={this.state.loading === 'loading'}
                        >
                            <div className='c-task__main-box'>
                                {children || "i'm container."}
                            </div>
                        </Spin>
                    </Content>
                </SplitPane>
            </Layout>
        );
    }
}
Container.propTypes = propType;
Container.defaultProps = defaultPro;

export default Container;
