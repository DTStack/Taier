import * as React from 'react';
import { Dropdown, Menu, Layout } from 'antd';
import './style.less';
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
        <Menu.Item key="logout">
          退出登录
        </Menu.Item>
      </Menu>
    );
    return (
      <Header>
        <div className="header-left">
          <span>系统名称</span>
        </div>
        <div className="header-middle"></div>
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
