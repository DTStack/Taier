import * as React from 'react';

import { Button, Dropdown, Menu, Icon } from 'antd';

export default class Toolbar extends React.Component<any, any> {
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

    renderRun = () => {
        const {
            isRunning, onRun, onStop, enableRun, disableRun
        } = this.props;

        return enableRun ? [
            <Button
                className='ide-toolbar__run'
                key="btnRun"
                onClick={onRun}
                loading={isRunning}
                disabled={disableRun || isRunning}
                icon="play-circle-o"
            >
                {' '}
                运行
            </Button>,
            <Button
                key="btnStop"
                onClick={onStop}
                icon="pause-circle-o"
                disabled={!isRunning}
            >
                停止
            </Button>
        ] : '';
    }

    render () {
        const {
            onFormat, enableFormat, disableEdit,
            leftCustomButton, rightCustomButton, customToobar
        } = this.props;

        return (
            <div className="toolbar ide-toolbar clear-offset">
                {
                    customToobar
                }
                {
                    this.renderRun()
                }
                {
                    enableFormat &&
                    <Button
                        icon="appstore-o"
                        onClick={onFormat}
                    >
                        格式化
                    </Button>
                }
                {!disableEdit && <Dropdown overlay={this.editMenu()} trigger={['click']}>
                    <Button icon="edit">
                        编辑<Icon type="down" />
                    </Button>
                </Dropdown>}
                {
                    leftCustomButton
                }
                <span style={{ float: 'right', position: 'relative', marginLeft: 'auto', marginRight: '18px', lineHeight: '28px', zIndex: 901 }}>
                    {rightCustomButton}
                </span>
            </div>
        );
    }
}
