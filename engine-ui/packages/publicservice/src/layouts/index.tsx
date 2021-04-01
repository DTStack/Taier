import * as React from 'react';
import { connect } from 'react-redux';
import { Layout } from 'antd';
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

interface IProps {
  children?: React.ReactElement;
  app?: {
    disableExt: boolean;
    disableMessage: boolean;
  };
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

  render() {
    const app = {
      ...this.props.app,
      disableExt: true,
    };

    const navigatorProps = { ...this.props, ...{ app } };
    return (
      <Layout className="dt-assets-container">
        <Navigator {...navigatorProps} />
        <Layout className="assets-container dt-container">
          <Layout>
            <Content>{this.props.children}</Content>
          </Layout>
        </Layout>
      </Layout>
    );
  }
}
export default BasicLayout;
