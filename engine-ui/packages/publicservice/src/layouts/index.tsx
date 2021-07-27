import * as React from 'react';
import { connect } from 'react-redux';
import { Layout } from 'antd';
//dt-common not exports complied outputs
import Navigator from 'dt-common/src/components/nav';
import classnames from 'classnames';
import './style';

declare var APP_CONF: any;
const IMG_URL = APP_CONF.IMG_URL || '';
const judge = window.location.hash.indexOf('data-model') > -1;

const initState = {
  collapsed: false,
  mode: 'inline',
  navData: [],
  authPaths: [],
  DATA_SOURCE_IMG: judge
    ? IMG_URL + '/assets/imgs/data_model.png'
    : IMG_URL + '/assets/imgs/api_logo.png',
  menuItems: [
    {
      id: 1,
      name: judge ? '数据模型' : '数据源中心',
      menuClass: 'horizontal-menu-item',
      enable: true,
      needRoot: false,
    },
  ],
  visibleNavigator: true,
};
type IState = typeof initState;

interface IProps {
  children?: React.ReactElement;
  app?: {
    disableExt: boolean;
    disableMessage: boolean;
  };
  location: any;
}

const { Content } = Layout;
@(connect((state: any) => {
  return {
    apps: state.apps,
    app: state.app,
    licenseApps: state.licenseApps,
    permission: state.permission,
    user: state.user,
  };
}) as any)
class BasicLayout extends React.PureComponent<IProps, IState> {
  state: IState = {
    ...initState,
  };

  componentDidMount() {
    const query = this.props.location.query;
    if (query.iframe === 'true') {
      this.setState({
        visibleNavigator: false,
      });
    }
  }

  render() {
    const app = {
      ...this.props.app,
      disableExt: true,
    };
    const { visibleNavigator } = this.state;

    const navigatorProps = {
      ...this.props,
      ...{ app },
      menuItems: this.state.menuItems,
      logo: (
        <div className="logo-img">
          <img src={this.state.DATA_SOURCE_IMG} alt="logo" />
          <span className="logo-header">
            {judge ? 'DataModel' : '系统管理'}
          </span>
        </div>
      ),
    };

    return (
      <Layout
        className={classnames({
          'dt-assets-container': true,
          'without-navigator': !visibleNavigator,
        })}>
        {visibleNavigator ? (
          <>
            <Navigator {...navigatorProps} />
            <Layout className="assets-container dt-container">
              <Layout>
                <Content>{this.props.children}</Content>
              </Layout>
            </Layout>
          </>
        ) : (
          this.props.children
        )}
      </Layout>
    );
  }
}
export default BasicLayout;
