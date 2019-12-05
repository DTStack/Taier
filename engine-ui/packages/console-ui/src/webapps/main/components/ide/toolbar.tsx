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
            isRunning, onRun, onStop, enableRun, disableRun, runningMenu
        } = this.props;

        const runBtn = (<Button
            onClick={onRun}
            loading={isRunning}
            disabled={disableRun || isRunning}
            {...{ title: '立即运行' }}
            icon="play-circle-o"
            style={{ marginLeft: '0px' }}
        >
            运行 { runningMenu ? <Icon type="down" /> : null }
        </Button>);

        return enableRun ? <span>
            {
                runningMenu ?
                <Dropdown overlay={runningMenu || null} trigger={['hover']}>
                    { runBtn }
                </Dropdown> : runBtn
            }
            <Button
                key="btnStop"
                onClick={onStop}
                icon="pause-circle-o"
                {...{ title: '立即停止' }}
                disabled={!isRunning}
            >
                停止
            </Button>
        </span> : null;
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
                    leftCustomButton
                }
                {
                    enableFormat &&
                    <Button
                        icon="appstore-o"
                        {...{ title: '格式化' }}
                        onClick={onFormat}
                    >
                        格式化
                    </Button>
                }
                {!disableEdit && <Dropdown overlay={this.editMenu()} trigger={['click']}>
                    <Button icon="edit" {...{ title: '编辑' }}>
                        编辑<Icon type="down" />
                    </Button>
                </Dropdown>}
                <span style={{ float: 'right', position: 'relative', marginRight: '18px', lineHeight: '28px', zIndex: 901 }}>
                    {rightCustomButton}
                </span>
            </div>
        );
    }
}
