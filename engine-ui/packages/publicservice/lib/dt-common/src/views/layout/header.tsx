import * as React from 'react';
import { connect } from 'react-redux';

import Navigator from '../../components/nav';
import { getHeaderLogo } from '../../consts';
import { compareEnableApp } from '../../funcs';

declare var window: any;

@(connect((state: any) => {
  return {
    user: state.user,
    apps: state.apps,
    licenseApps: state.licenseApps,
    routing: state.routing,
  };
}) as any)
class Header extends React.Component<any, any> {
  constructor(props: any) {
    super(props);
    this.state = {};
  }

  render() {
    const { apps, licenseApps, user } = this.props;
    const logo = (
      <React.Fragment>
        <img alt="logo" src={getHeaderLogo()} />
        <span className="c-header__title c-header__title--main">
          {window.APP_CONF.prefix}
        </span>
      </React.Fragment>
    );
    const settingMenus = [
      {
        id: 'admin/audit',
        name: '安全审计',
        link: `/admin/audit?app=rdos`,
        enable: user.isRoot,
        enableIcon: true,
        className: 'icon_safe',
      },
    ];
    return (
      <Navigator
        logo={logo}
        menuItems={compareEnableApp(apps, licenseApps, true)}
        licenseApps={licenseApps}
        settingMenus={settingMenus}
        {...this.props}
      />
    );
  }
}
export default Header;
