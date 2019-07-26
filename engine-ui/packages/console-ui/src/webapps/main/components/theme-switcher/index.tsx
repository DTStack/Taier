import * as React from 'react';
import { Button, Dropdown, Menu, Icon } from 'antd';

class ThemeSwitcher extends React.Component<any, any> {
    viewMenu = () => {
        const { onThemeChange, editorTheme } = this.props;

        return (
            <Menu selectedKeys={[editorTheme]} onClick={({ key }) => { onThemeChange(key) }}>
                <Menu.Item key="vs">默认</Menu.Item>
                <Menu.Item key="vs-dark">深色</Menu.Item>
                <Menu.Item key="hc-black">高对比深色</Menu.Item>
            </Menu>
        )
    }

    render () {
        return (
            <Dropdown overlay={this.viewMenu()} trigger={['click']}>
                <Button icon="skin" title="主题" style={{ paddingLeft: 0 }}>
                    主题<Icon type="down" />
                </Button>
            </Dropdown>
        )
    }
}

export default ThemeSwitcher
