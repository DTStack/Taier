import React, { Component } from "react";

import { Button, Dropdown, Menu, Icon } from "antd";

export default class Toolbar extends Component {

    editMenu = () => {
        const { onFileEdit } = this.props;
        return (
            <Menu onClick={({ key }) => { onFileEdit(key) }}>
                <Menu.Item key="find">查找（Cmd/Ctrl）+ F</Menu.Item>
                <Menu.Item key="replace">替换（Cmd/Ctrl）+ F</Menu.Item>
                <Menu.Item key="commandPane">命令面板 (F1)</Menu.Item>
            </Menu>
        )
    }

    viewMenu = () => {
        const { onThemeChange, editorTheme } = this.props;
        return (
            <Menu selectedKeys={[editorTheme]} onClick={({ key }) => { onThemeChange(key) }}>
                <Menu.Item key="vs">默认</Menu.Item>
                <Menu.Item key="vs-dark">黑色</Menu.Item>
                <Menu.Item key="hc-black">高对比黑色</Menu.Item>
            </Menu>
        )
    }

    renderRun = () => {
        const {
            isRunning, onRun, onStop, enableRun
        } = this.props;

        return enableRun ? [
            <Button
                key="btnRun"
                onClick={onRun}
                loading={isRunning}
                disabled={isRunning}
                title="立即运行"
                icon="play-circle-o"
                style={{ marginLeft: "0px" }}
            >
                {" "}
                运行
            </Button>,
            <Button
                key="btnStop"
                onClick={onStop}
                icon="pause-circle-o"
                title="立即停止"
                disabled={!isRunning}
            >
                停止
            </Button>
        ] : '';
    }


    render() {
        const {
            onFormat, enableFormat, leftCustomButton, rightCustomButton
        } = this.props;

        return (
            <div className="toolbar ide-toolbar clear-offset">
                {
                    this.renderRun()
                }
                {leftCustomButton}
                {
                    enableFormat &&
                    <Button
                        icon="appstore-o"
                        title="格式化"
                        onClick={onFormat}
                    >
                        格式化
                    </Button>
                }
                <Dropdown overlay={this.editMenu()} trigger={['click']}>
                    <Button icon="edit" title="编辑">
                        编辑<Icon type="down" />
                    </Button>
                </Dropdown>
                <Dropdown overlay={this.viewMenu()} trigger={['click']}>
                    <Button icon="skin" title="主题">
                        主题<Icon type="down" />
                    </Button>
                </Dropdown>
                <span style={{ float: "right", position: "relative", marginRight: "18px", lineHeight: "28px", zIndex: "901" }}>
                    {rightCustomButton}
                </span>
            </div>
        );
    }
}
