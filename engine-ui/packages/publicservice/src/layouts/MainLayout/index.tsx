import * as React from 'react';
import { Layout } from 'antd';
import { connect } from 'react-redux';
import TopBar from './topBar';
import Foot from 'components/footer';
import { withRouter } from 'react-router';
import * as global from '@/pages/global/redux/action';
import ErrorBoundary from '@/components/ErrorBoundary';
import { bindActionCreators } from 'redux';
import './style.scss';

interface IProps {
	getNavData: (params: any) => void;
	location: any;
	children: any;
	navData: any;
	isTopHide:any;
}
interface IState {
	loading: boolean;
}
@connect(
	(state) => ({ ...state.global }),
	(dispatch) => bindActionCreators({ ...global }, dispatch)
)
class MainLayout extends React.Component<IProps, IState> {
	constructor(IProps: any) {
		super(IProps);
	}
	state: IState = {
		loading: false,
	};
	componentDidMount() {
		this.props.getNavData({});
	}

	render() {
		const { location, children, navData,isTopHide } = this.props;
		return (
			<Layout>
				<ErrorBoundary>
				   {isTopHide&&<TopBar location={location} navData={navData} />}
					<div style={{top:isTopHide?"64px":"0px"}} className="main-layout">{children}</div>
					<Foot />
				</ErrorBoundary>
			</Layout>
		);
	}
}
export default withRouter(MainLayout);
