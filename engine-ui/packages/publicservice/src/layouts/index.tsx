import * as React from 'react';
import { connect } from 'react-redux';
import { Layout } from 'antd';
import ErrorBoundary from '@/components/ErrorBoundary';
//dt-common not exports complied outputs
import Navigator from 'lib/dt-common/src/components/nav';
import './style';

const initState = {
  collapsed: false,
  mode: 'inline',
  navData: [],
  authPaths: [],
};
type IState = typeof initState;

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
class BasicLayout extends React.PureComponent {
  state: IState = {
    ...initState,
  };

  render() {
    return (
      <ErrorBoundary>
        <Layout className="dt-assets-container">
          <Navigator {...this.props} />
          <Layout className="assets-container dt-container">
            <Layout>
              <Content>{this.props.children}</Content>
            </Layout>
          </Layout>
        </Layout>
      </ErrorBoundary>
    );
  }
}
export default BasicLayout;
