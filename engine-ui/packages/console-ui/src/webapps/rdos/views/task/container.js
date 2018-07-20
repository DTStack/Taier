import React, { Component } from "react";
import PropTypes from "prop-types";
import { Layout, Spin } from "antd";
import SplitPane from "react-split-pane";
import { connect } from "react-redux";

import Sidebar from "./sidebar";
import SearchTaskModal from "./searchTaskModal";
import { stopSql } from "../../store/modules/offlineTask/editorAction";

const { Content } = Layout;

const propType = {
    children: PropTypes.node
};
const defaultPro = {
    children: []
};

function mapStateToProps(state) {
    return { editor: state.editor };
}

@connect(mapStateToProps)
class Container extends Component {
    constructor(props) {
        super(props);
        this.state = {
            loading: "success"
        };
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

    render() {
        const { children } = this.props;
        return (
            <Layout className="dt-dev-task">
                <SplitPane
                    split="vertical"
                    minSize={200}
                    maxSize="80%"
                    defaultSize={200}
                    primary="first"
                >
                    <div
                        className="bg-w ant-layout-sider"
                        style={{ width: "inherit" }}
                    >
                        <Sidebar />
                        <SearchTaskModal />
                    </div>
                    <Content>
                        <Spin
                            tip="Loading..."
                            size="large"
                            spinning={this.state.loading === "loading"}
                        >
                            <div style={{ width: "100%", height: "100%" }}>
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
