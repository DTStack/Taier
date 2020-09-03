import * as React from 'react';
import { Layout, Menu } from 'antd';
import {
	MenuFoldOutlined,
	MenuUnfoldOutlined,
} from '@ant-design/icons';
const { SubMenu } = Menu;
const { Sider } = Layout;
import {Link } from 'react-router-dom';
import './style.scss';

declare var frontConf;
interface IProps {
	navData: any;
	location: any;
	openKeys: any;
	history: any;
	setOpenKeys: any;
}
interface IState {
	collapsed: boolean;
	openKeys: Array<any>;
	selectedKeys: Array<any>;
	sideData: Array<any>;
}
export default class SideBar extends React.Component<IProps, IState> {
	constructor(IProps: any) {
		super(IProps);
	}
	state: IState = {
		collapsed: false,
		selectedKeys: [],
		openKeys: ['/parking-special'],
		sideData: [],
	};

	componentDidMount() {}

	logout() {
		this.props.history.push('/auth/login');
	}
	toggle = () => {
		this.setState({
			collapsed: !this.state.collapsed,
		});
	};
	linkToPath = (url) => {
		this.props.history.push(url);
	};
	openKeys = (keys) => {
		// const{setOpenKeys}=this.props;
		this.setState({ openKeys: keys });
		// setOpenKeys(keys);
	};
	renderMenu = (data, authCode) => {
		if (data.children.length) {
			return (
				 (
					<SubMenu
						key={data.permissionUrl}
						title={
							<span>
								{data.permissionIcon && (
									<i
										style={{ paddingRight: '5px' }}
										className={`iconfont icon${data.permissionIcon}`}
									></i>
								)}
								<span>{data.permissionName}</span>
							</span>
						}
					>
						{data.children.map((d, i) => this.renderMenu(d, authCode))}
					</SubMenu>
				)
			);
		} else {
			return (
		  	(true && 	
      <Menu.Item key={data.permissionUrl}>
						{data.permissionIcon && (
							<i
								style={{ paddingRight: '5px' }}
								className={`iconfont icon${data.permissionIcon}`}
								onClick={this.linkToPath.bind(this, data.permissionUrl)}
							></i>
						)}
						<span>
							<Link to={data.permissionUrl}>{data.permissionName}</Link>
						</span>
					</Menu.Item>
				)
			);
		}
	};
	render() {
    const { location, navData } = this.props;
		let menuKeys = location.pathname.match(/\/\w*/g);
		return (
			<Sider
				width={200}
				className="side-nav"
				style={{ background: 'rgba(0, 21, 41, 1)', overflow: 'auto' }}
				trigger={null}
				collapsible
				collapsed={this.state.collapsed}
			>
				{frontConf.NAV_STRETCH && (
					<div className="fold-btn">
						{this.state.collapsed ? (
							<MenuUnfoldOutlined onClick={this.toggle.bind(this)} />
						) : (
							<MenuFoldOutlined onClick={this.toggle.bind(this)} />
						)}
					</div>
				)}
				<Menu
					selectedKeys={[menuKeys.length > 3 ? menuKeys.slice(0, 3).join('') : location.pathname]}
					openKeys={this.state.openKeys}
					onOpenChange={this.openKeys.bind(this)}
					mode="inline"
					theme="dark"
					className="side-menu"
				>
					{navData.map((menu) => this.renderMenu(menu, ''))}
				</Menu>
			</Sider>
		);
	}
}
