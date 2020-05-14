import * as React from 'react';
import { Layout } from 'antd';
import { connect } from 'react-redux';
import SideBar from './sideBar';
import { isUndefined } from 'lodash';
import MainLayout from 'layouts/mainLayout';
import * as global from '@/pages/global/redux/action';
import { bindActionCreators } from 'redux';
import './style.scss';

export default (moduleName, isTopHide, isSideHide) => {
	return connect(
		(state) => ({ ...state.global }),
		(dispatch) => bindActionCreators({ ...global }, dispatch)
	)((props) => {
		const findTopNavChildren = (navData) => {
			const matchedTopNav = navData ? navData.find((nav) => nav.permissionName === moduleName) : [];
			return isUndefined(matchedTopNav) || isUndefined(matchedTopNav.children) ? [] : matchedTopNav.children;
		};
		const { location, navData, history, children } = props;

		return (
			<MainLayout>
				<Layout>
					{isSideHide && (
						<SideBar
							openKeys={props.openKeys}
							setOpenKeys={props.setOpenKeys}
							location={location}
							history={history}
							navData={findTopNavChildren(navData)}
						/>
					)}
					<div className="side-layout">{children}</div>
				</Layout>
			</MainLayout>
		);
	});
};
