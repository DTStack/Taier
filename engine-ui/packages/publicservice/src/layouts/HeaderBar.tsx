import * as React from 'react';
import { Dropdown, Menu, Layout } from 'antd';
import './style.scss';
declare const frontConf;

const { Header } = Layout;

interface IProps {
  location: any;
}
interface IState {}

class HeaderBar extends React.Component<IProps, IState> {
  constructor(IProps) {
    super(IProps);
  }

  logout() {
    window.sessionStorage.clear();
    window.open(`${frontConf.BACK_HOST}/login`);
  }

  handleItemClick = () => {
    this.logout();
  };

  render() {
    const selfMenu = (
      <Menu onClick={this.handleItemClick}>
        <Menu.Item key="logout">退出登录</Menu.Item>
      </Menu>
    );
    return (
      <Header className="top">
        <div className="header-left">
          <span>系统管理</span>
        </div>
        <div className="header-middle">
          <span>数据源</span>
          <span>数据模型</span>
        </div>
        <div className="header-right">
          <Dropdown overlay={selfMenu}>
            <span style={{ color: '#fff' }}>
              {sessionStorage.getItem('userName')}
            </span>
          </Dropdown>
        </div>
      </Header>
    );
  }
}

export default HeaderBar;
