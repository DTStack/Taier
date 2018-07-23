import React, { Component } from "react";
import { assign } from 'lodash';
import { Button, Dropdown, Icon, Menu } from "antd";

import API from "../../../../api";
import { setCurrentPage } from '../../../../store/modules/realtimeTask/browser';

export default class Toolbar extends Component {

    sqlFormat = () => {

        const { currentTabData, dispatch } = this.props;
        const params = {
            sql: currentTabData.sqlText || ""
        };

        API.streamSqlFormat(params).then(res => {
            if (res.data) {
                const data = {
                    merged: true,
                    sqlText: res.data,
                    id: currentTabData.id,
                };
                const currentPage = assign(currentTabData, data);
                dispatch(setCurrentPage(currentPage));
            }
        });
    };

    editMenu = () => {
        return (
            <Menu>
                <Menu.Item key="find">查找（Cmd/Ctrl）+ F</Menu.Item>
                <Menu.Item key="replace">替换（Cmd/Ctrl）+ F</Menu.Item>
                <Menu.Item key="commandPane">命令面板 (F1)</Menu.Item>
            </Menu>
        )
    }

    viewMenu = () => {
        const { changeEditorTheme } = this.props;
        return (
            <Menu onClick={({key}) => {changeEditorTheme(key)}}>
                <Menu.Item key="vs">默认</Menu.Item>
                <Menu.Item key="vs-dark">黑色</Menu.Item>
                <Menu.Item key="hc-black">高对比黑色</Menu.Item>
            </Menu>
        )
    }

    render() {
        return (
            <div className="ide-header bd-bottom">
                <div className="ide-toolbar toolbar clear-offset">
                    <Button
                        icon="appstore-o"
                        title="格式化"
                        onClick={this.sqlFormat}
                    >
                        格式化
                    </Button>
                </div>
            </div>
        );
    }
}
