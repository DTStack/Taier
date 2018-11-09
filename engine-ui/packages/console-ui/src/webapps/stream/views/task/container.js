import React, { Component } from "react";
import PropTypes from "prop-types";
import { Layout, Spin } from "antd";
import SplitPane from "react-split-pane";
import { connect } from "react-redux";

import Sidebar from "./sidebar";
import Default from './realtime/default';
import TaskIndex from './realtime';
import SearchTaskModal from "./searchTaskModal";
import { stopSql, getEditorThemeClassName } from "../../store/modules/editor/editorAction";

const { Content } = Layout;

const propType = {
    children: PropTypes.node
};
const defaultPro = {
    children: []
};

function mapStateToProps(state) {
    return { editor: state.editor, pages: state.realtimeTask.pages };
}

@connect(mapStateToProps)
class Container extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: "success"
        };
        this.loadIDETheme(this.props.editor.options.theme);

    }

    componentDidMount() {
        if (process.env.NODE_ENV === 'production') {
            window.addEventListener('beforeunload', this.beforeunload, false);
        }
    }

    componentWillUnmount() {
        const { dispatch, editor } = this.props;
        const running = editor.running;
        //清楚所有运行中的tabs状态
        for (let i in running) {
            dispatch(stopSql(running[i], null, true));
        }
        window.removeEventListener("beforeunload", this.beforeunload, false);
        this.unloadIDETheme();
    }

    componentWillReceiveProps (nextProps) {
        if (nextProps.editor.options.theme !== this.props.editor.options.theme) {
            this.loadIDETheme(nextProps.editor.options.theme);
        }
    }

    beforeunload = e => {
        /* eslint-disable */
        const confirmationMessage = "\o/";
        (e || window.event).returnValue = confirmationMessage; // Gecko + IE
        return confirmationMessage; // Webkit, Safari, Chrome
        /* eslint-disable */
    };

    showLoading = () => {
        const self = this;
        this.setState({ loading: "loading" });
        setTimeout(() => {
            self.setState({ loading: "success" });
        }, 200);
    };

    loadIDETheme = (theme) => {
        console.log("componentDidMount 开发套件：", this.props.editor)
        const claName = getEditorThemeClassName(theme); 
        document.body.className = claName;
    }

    unloadIDETheme = () => {
        console.log("componentWillUnmount 开发套件：", this.props.editor);
        document.body.className = "";
    }

    render() {
        const { pages } = this.props;
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
                        style={{ width: "inherit", height: '100%' }}
                    >
                        <Sidebar />
                        <SearchTaskModal />
                    </div>
                    <Content style={{height: '100%'}}>
                        <Spin
                            tip="Loading..."
                            size="large"
                            spinning={this.state.loading === "loading"}
                        >
                            <div style={{ width: "100%", height: "100%" }}>
                                { 
                                    pages && pages.length > 0 ? 
                                    <TaskIndex /> : <Default />
                                }
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
