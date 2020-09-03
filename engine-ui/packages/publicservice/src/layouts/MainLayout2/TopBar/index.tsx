import * as React from 'react';
import { Dropdown, Menu } from 'antd';
import { PicCenterOutlined, LoginOutlined, UserOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import {history} from 'utils/index'
declare const frontConf;
declare const _;
import './style.scss';

interface IProps {
	location: any;
	navData: any;
}
interface IState {
	loading: boolean;
	userName: string;
	homeUrl: string;
}

export default class TopBar extends React.Component<IProps, IState> {
	constructor(IProps) {
    super(IProps);

  }
  
  state:IState={
    loading:false,
    userName: 'kangaoo',
    homeUrl: '/'
  }
	shouldComponentUpdate(nextProps, nextState) {
		return this.props !== nextProps;
	}
	componentDidMount() {}

	logout() {
		history.push('/login')
	}

	redirectTop = (item, key) => {
    console.log(item,key);
		history.push(key);
	};
	handleItem = (item) => {
		if (item.key == 2) {
			window.location.href = frontConf.UIC_HOST;
		} else if (item.key == 3) {
			this.logout();
		}
	};
	render() {
		const { navData, location } = this.props;
		const { homeUrl, userName } = this.state;
    let menuKeys = location.pathname.split('/');
    console.log(location,'location',menuKeys);
		const topMenu = (
			<Menu
				mode="horizontal"
				theme="dark"
				selectedKeys={[`/${menuKeys[1]}`, menuKeys.join('/')]}
				onClick={this.redirectTop.bind(this)}
			>
				{navData.map((item, idx) => (
					<Menu.Item key={item.permissionUrl}>
						<Link to={item.permissionUrl}>{item.permissionName}</Link>
					</Menu.Item>
				))}
			</Menu>
		);
		const selfMenu = (
			<Menu onClick={this.handleItem.bind(this)}>
				<Menu.Item key="1">
					<UserOutlined />
					{userName}
				</Menu.Item>
				<Menu.Item key="2">
					<PicCenterOutlined /> 用户中心
				</Menu.Item>
				<Menu.Item key="3">
					<LoginOutlined />
					退出登录
				</Menu.Item>
			</Menu>
		);
		return (
        <div className="top-header">
				<div className="header-left">
					<Link to={homeUrl}>
						<img src={frontConf.COMPANY_LOGO} alt="logo" />
					</Link>
				</div>
				<div className="header-middle">{topMenu}</div>
				<div className="header-right">
					<Dropdown overlay={selfMenu}>
				  	<UserOutlined />
					</Dropdown>
				</div>
        </div>
		);
	}
}
