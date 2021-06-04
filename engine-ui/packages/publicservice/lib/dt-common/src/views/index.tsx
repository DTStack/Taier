import * as React from 'react';
import { connect } from 'react-redux';
import { cloneDeep, get } from 'lodash';
import { hashHistory } from 'react-router';

import { ChromeDownload, Cookies, NotFound } from 'dt-react-component';
import { Cookie } from 'dt-utils';

import {
  initNotification,
  isCurrentProjectChanged,
  isCookieBeProjectType,
  loopIsIntercept,
  getCurrentPath,
} from '../funcs';

import utils from '../../src/utils';

import * as apps from '../consts/defaultApps';

import { getLicenseApp, getDefaultApps } from '../actions/app';
import GlobalLoading from './layout/loading';
import { getInitUser } from '../actions/user';
import userActions from '../consts/userActions';
import http from '../api';
import Header from './layout/header';

const defaultPro: any = {
  children: [],
};

initNotification();
@(connect((state: any) => {
  return {
    user: state.user,
    licenseApps: state.licenseApps,
    routing: state.routing,
    isLicenseLoaded: state.isLicenseLoaded,
    apps: state.apps,
    app: state.app,
  };
}) as any)
class Main extends React.Component<any, any> {
  static defaultProps = defaultPro;

  componentDidMount() {
    const { user } = this.props;
    const userAction = getInitUser();
    this.props.dispatch(userAction);
    this.checkRoot(user);
    this.props.dispatch(getLicenseApp());
    this.props.dispatch(getDefaultApps());
    this.isEnableLicenseApp();
    this.handleHideInDom();
  }
  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps: any) {
    const { user } = nextProps;

    if (this.props.user.dtuicUserId != user.dtuicUserId && user.dtuicUserId) {
      this.checkRoot(user);
    }
    if (this.props.routing) {
      const currentUrl =
        this.props.routing.locationBeforeTransitions.pathname +
        this.props.routing.locationBeforeTransitions.search;
      const prevUrl =
        nextProps.routing.locationBeforeTransitions.pathname +
        nextProps.routing.locationBeforeTransitions.search;
      if (currentUrl != prevUrl) {
        this.isEnableLicenseApp();
      }
    }
  }
  componentDidUpdate(prevProps: any, prevState: any) {
    if (
      this.props.licenseApps.length > 0 &&
      prevProps.licenseApps !== this.props.licenseApps
    ) {
      this.isEnableLicenseApp();
    }
    if (this.props.apps.length > 0 && prevProps.apps !== this.props.apps) {
      this.handleHideInDom();
    }
  }

  // rdos
  fixRdosChildrenApps = (arr: any) => {
    let fixRdosChildrenApps: any = [];
    if (arr && arr.length > 1) {
      arr.map((item: any) => {
        switch (item.name) {
          case '数据源':
            fixRdosChildrenApps[0] = item;
            break;
          case '数据开发':
            fixRdosChildrenApps[1] = item;
            break;
          case '运维中心':
            fixRdosChildrenApps[2] = item;
            break;
          case '数据地图':
            fixRdosChildrenApps[3] = item;
            break;
          case '数据模型':
            fixRdosChildrenApps[4] = item;
            break;
          case '项目管理':
            fixRdosChildrenApps[5] = item;
            break;
        }
      });
      return fixRdosChildrenApps;
    } else {
      return arr;
    }
  };
  // stream
  fixStreamChildrenApps = (arr: any) => {
    let fixStreamChildrenApps: any = [];
    if (arr && arr.length > 1) {
      arr.map((item: any) => {
        switch (item.name) {
          case '数据源':
            fixStreamChildrenApps[0] = item;
            break;
          case '数据开发':
            fixStreamChildrenApps[1] = item;
            break;
          case '任务运维':
          case '运维中心':
            fixStreamChildrenApps[2] = item;
            break;
          case '项目管理':
            fixStreamChildrenApps[3] = item;
            break;
        }
      });
      return fixStreamChildrenApps;
    } else {
      return arr;
    }
  };
  // analyticsEngine
  fixAnalyChildrenApps = (arr: any) => {
    let fixAnalyChildrenApps: any = [];
    if (arr && arr.length > 1) {
      arr.map((item: any) => {
        switch (item.name) {
          case '数据库管理':
            fixAnalyChildrenApps[0] = item;
            break;
          case '表管理':
            fixAnalyChildrenApps[1] = item;
            break;
        }
      });
      return fixAnalyChildrenApps;
    } else {
      return arr;
    }
  };
  // dataQuality
  fixQualityChildrenApps = (arr: any) => {
    let fixQualityChildrenApps: any = [];
    if (arr && arr.length > 1) {
      arr.map((item: any) => {
        switch (item.name) {
          case '概览':
            fixQualityChildrenApps[0] = item;
            break;
          case '任务查询':
            fixQualityChildrenApps[1] = item;
            break;
          case '规则配置':
            fixQualityChildrenApps[2] = item;
            break;
          case '逐行校验':
            fixQualityChildrenApps[3] = item;
            break;
          case '数据源管理':
            fixQualityChildrenApps[4] = item;
            break;
        }
      });
      return fixQualityChildrenApps;
    } else {
      return arr;
    }
  };
  // Api
  fixApiChildrenApps = (arr: any) => {
    let fixApiChildrenApps: any = [];
    if (arr && arr.length > 1) {
      arr.map((item: any) => {
        switch (item.name) {
          case '概览':
            fixApiChildrenApps[0] = item;
            break;
          case 'API市场':
            fixApiChildrenApps[1] = item;
            break;
          case '我的API':
            fixApiChildrenApps[2] = item;
            break;
          case 'API管理':
            fixApiChildrenApps[3] = item;
            break;
          case '授权与安全':
            fixApiChildrenApps[4] = item;
            break;
          case '数据源管理':
            fixApiChildrenApps[5] = item;
            break;
        }
      });
      return fixApiChildrenApps;
    } else {
      return arr;
    }
  };
  fixScienceChildrenApps = (arr: any) => {
    let fixScienceChildrenApps: any = [];
    if (arr && arr.length > 1) {
      arr.map((item: any) => {
        switch (item.name) {
          case '算法实验':
            fixScienceChildrenApps[0] = item;
            break;
          case '运维中心':
            fixScienceChildrenApps[1] = item;
            break;
          case '数据管理':
            fixScienceChildrenApps[2] = item;
            break;
        }
      });
      return fixScienceChildrenApps;
    } else {
      return arr;
    }
  };
  // license禁用app url 跳转到首页
  isEnableLicenseApp() {
    let { licenseApps, isLicenseLoaded } = this.props;
    const pathAddress = getCurrentPath();
    // 成功返回数据
    if (licenseApps && licenseApps.length) {
      let fixLicenseApps = cloneDeep(licenseApps);
      let licenseMap: any = {};
      fixLicenseApps.forEach((licenseApp: any) => {
        let newChildren: any = [];
        switch (licenseApp.id) {
          case apps.rdosApp.id: {
            newChildren = this.fixRdosChildrenApps(licenseApp.children);
            break;
          }
          case apps.streamApp.id: {
            newChildren = this.fixStreamChildrenApps(licenseApp.children);
            break;
          }
          case apps.aeApp.id: {
            newChildren = this.fixAnalyChildrenApps(licenseApp.children);
            break;
          }
          case apps.dqApp.id: {
            newChildren = this.fixQualityChildrenApps(licenseApp.children);
            break;
          }
          case apps.daApp.id: {
            newChildren = this.fixApiChildrenApps(licenseApp.children);
            break;
          }
          case apps.scienceApp.id: {
            newChildren = this.fixScienceChildrenApps(licenseApp.children);
            break;
          }
        }
        licenseMap[licenseApp.id] = {
          ...licenseApp,
          children: newChildren,
        };
      });
      // rdosAPP
      const rdosApp = licenseMap[apps.rdosApp.id];
      const isRdosShow = get(rdosApp, 'isShow');
      const isRdosDataSource = get(rdosApp, 'children[0].isShow');
      const isRdosTask = get(rdosApp, 'children[1].isShow');
      const isRdosOpera = get(rdosApp, 'children[2].isShow');
      const isRdosMap = get(rdosApp, 'children[3].isShow');
      const isRdosModal = get(rdosApp, 'children[4].isShow');
      const isRdosPro = get(rdosApp, 'children[5].isShow');
      // streamAPP
      const streamApp = licenseMap[apps.streamApp.id];
      const isStream = get(streamApp, 'isShow');
      const isStreamDataSource = get(streamApp, 'children[0].isShow');
      const isStreamTask = get(streamApp, 'children[1].isShow');
      const isStreamOpera = get(streamApp, 'children[2].isShow');
      const isStreamPro = get(streamApp, 'children[3].isShow');
      // analyticsEngine
      const analyApp = licenseMap[apps.aeApp.id];
      const isAna = analyApp.isShow;
      // dataQuality
      const qualityApp = licenseMap[apps.dqApp.id];
      const isQuali = get(qualityApp, 'isShow');
      const isQualiOver = get(qualityApp, 'children[0].isShow');
      const isQualiTaskSearch = get(qualityApp, 'children[1].isShow');
      const isQualiRule = get(qualityApp, 'children[2].isShow');
      const isQualiVali = get(qualityApp, 'children[3].isShow');
      const isQualiDataSource = get(qualityApp, 'children[4].isShow');
      // dataApi
      const apiApp = licenseMap[apps.daApp.id];
      const isDataApi = get(apiApp, 'isShow');
      const isApiover = get(apiApp, 'children[0].isShow');
      const isApiMarket = get(apiApp, 'children[1].isShow');
      const isApiMine = get(apiApp, 'children[2].isShow');
      const isApiMana = get(apiApp, 'children[3].isShow');
      const isApiSafe = get(apiApp, 'children[4].isShow');
      const isApiDataSource = get(apiApp, 'children[5].isShow');
      // science
      const scienceApp = licenseMap[apps.scienceApp.id];
      const isScience = get(scienceApp, 'isShow');
      const isScienceDevelop = get(scienceApp, 'children[0].isShow');
      const isScienceOperation = get(scienceApp, 'children[1].isShow');
      const isScienceSource = get(scienceApp, 'children[2].isShow');
      // 判断条件存入数组
      const arr = [
        // rdos
        {
          url: 'batch.html',
          isShow: !isRdosShow,
        },
        {
          url: 'batch.html#/offline/task',
          isShow: !isRdosTask,
        },
        {
          url: 'batch.html#/operation',
          isShow: !isRdosOpera,
        },
        {
          url: 'batch.html#/database',
          isShow: !isRdosDataSource,
        },
        {
          url: 'batch.html#/project',
          isShow: !isRdosPro,
        },
        {
          url: 'batch.html#/data-manage',
          isShow: !isRdosMap,
        },
        {
          url: 'batch.html#/data-model',
          isShow: !isRdosModal,
        },
        {
          url: 'admin/user?app=rdos',
          isShow: !isRdosShow,
        },
        {
          url: 'admin/role?app=rdos',
          isShow: !isRdosShow,
        },
        {
          url: 'message?app=rdos',
          isShow: !isRdosShow,
        },
        // stream
        {
          url: 'stream.html',
          isShow: !isStream,
        },
        {
          url: 'stream.html#/database',
          isShow: !isStreamDataSource,
        },
        {
          url: 'stream.html#/realtime',
          isShow: !isStreamTask,
        },
        {
          url: 'stream.html#/project',
          isShow: !isStreamPro,
        },
        {
          url: 'stream.html#/operation',
          isShow: !isStreamOpera,
        },
        {
          url: 'admin/user?app=stream',
          isShow: !isStream,
        },
        {
          url: 'admin/role?app=stream',
          isShow: !isStream,
        },
        {
          url: 'message?app=stream',
          isShow: !isStream,
        },
        // analyticsEngine
        {
          url: 'analytics.html',
          isShow: !isAna,
        },
        {
          url: 'admin/user?app=analyticsEngine',
          isShow: !isAna,
        },
        {
          url: 'admin/role?app=analyticsEngine',
          isShow: !isAna,
        },
        // dataQuality
        {
          url: 'dataQuality.html',
          isShow: !isQuali,
        },
        {
          url: 'dataQuality.html#/dq/overview',
          isShow: !isQualiOver,
        },
        {
          url: 'dataQuality.html#/dq/taskQuery',
          isShow: !isQualiTaskSearch,
        },
        {
          url: 'dataQuality.html#/dq/rule',
          isShow: !isQualiRule,
        },
        {
          url: 'dataQuality.html#/dq/dataCheck',
          isShow: !isQualiVali,
        },
        {
          url: 'dataQuality.html#/dq/dataSource',
          isShow: !isQualiDataSource,
        },
        {
          url: 'admin/user?app=dataQuality',
          isShow: !isQuali,
        },
        {
          url: 'admin/role?app=dataQuality',
          isShow: !isQuali,
        },
        {
          url: 'message?app=dataQuality',
          isShow: !isQuali,
        },
        // dataApi
        {
          url: 'dataApi.html',
          isShow: !isDataApi,
        },
        {
          url: 'dataApi.html#/api',
          isShow: !isApiover,
        },
        {
          url: 'dataApi.html#/api/market',
          isShow: !isApiMarket,
        },
        {
          url: 'dataApi.html#/api/mine',
          isShow: !isApiMine,
        },
        {
          url: 'dataApi.html#/api/manage',
          isShow: !isApiMana,
        },
        {
          url: 'dataApi.html#/api/approvalAndsecurity',
          isShow: !isApiSafe,
        },
        {
          url: 'dataApi.html#/api/dataSource',
          isShow: !isApiDataSource,
        },
        {
          url: '/admin/user?app=dataApi',
          isShow: !isDataApi,
        },
        {
          url: '/admin/role?app=dataApi',
          isShow: !isDataApi,
        },
        {
          url: 'message?app=dataApi',
          isShow: !isDataApi,
        },
        // 数据科学
        {
          url: 'science.html',
          isShow: !isScience,
        },
        {
          url: 'science.html#/science/workbench',
          isShow: !isScienceDevelop,
        },
        {
          url: 'science.html#/science/operation',
          isShow: !isScienceOperation,
        },
        {
          url: 'science.html#/science/source',
          isShow: !isScienceSource,
        },
        {
          url: '/admin/user?app=science',
          isShow: !isScience,
        },
        {
          url: '/admin/role?app=science',
          isShow: !isScience,
        },
        {
          url: 'message?app=science',
          isShow: !isScience,
        },
      ];
      loopIsIntercept(pathAddress, arr);
    }
    // 用户未上传license,返回空数组情况
    if (isLicenseLoaded && licenseApps && licenseApps.length == 0) {
      if (pathAddress.indexOf('index.html') == -1) {
        window.location.href = '/';
      }
    }
  }
  checkRoot(user: any) {
    if (user && user.dtuicUserId) {
      http
        .checkRoot({ userId: user.dtuicUserId })
        .then((res: any) => {
          if (res.code == 1) {
            this.props.dispatch({
              type: userActions.UPDATE_USER,
              data: {
                isRoot: true,
              },
            });
          } else {
            this.props.dispatch({
              type: userActions.UPDATE_USER,
              data: {
                isRoot: false,
              },
            });
          }
        })
        .catch((e: any) => {
          console.error(e);
        });
    }
  }

  onFieldsChanged = (fields: any) => {
    if (fields.length > 0 && !document.hasFocus()) {
      let shouldReload = false;
      let shouldToHomePageCookie = ['dt_token', 'dt_tenant_id', 'dt_user_id'];
      let shouldTHP = false; // 需要回到当前子项目首页
      for (let i = 0; i < fields.length; i++) {
        let key = fields[i].key;
        shouldTHP = shouldTHP || shouldToHomePageCookie.includes(key);
        if (isCookieBeProjectType(key)) {
          if (isCurrentProjectChanged(key)) {
            shouldReload = true;
            break;
          }
        } else {
          shouldReload = true;
          break;
        }
      }
      if (shouldReload) {
        if (shouldTHP) {
          // 当 'dt_token', 'dt_tenant_id', 'dt_user_id' 修改时 需要清空所有的项目ID cookie
          Cookie.deleteCookie('project_id');
          Cookie.deleteCookie('science_project_id');
          Cookie.deleteCookie('stream_project_id');
          Cookie.deleteCookie('api_project_id');
          Cookie.deleteCookie('dq_project_id');
          Cookie.deleteCookie('tag_project_id');
        }
        if (shouldTHP && this.props.location.pathname != '/') {
          hashHistory.push('/');
        } else {
          window.location.reload();
        }
      }
    }
  };

  handleHideInDom = () => {
    const { apps, app } = this.props;
    const logo = document.querySelectorAll('.c-header__logo');
    const menuRight = document.querySelectorAll('.menu-right');
    const hideConfigItem =
      apps.filter((item: any) => item.id === app.id)[0] || {};
    if (logo && hideConfigItem.hideLogo) {
      logo.forEach((o: any) => {
        o.style.visibility = 'hidden';
      });
    }
    if (menuRight && hideConfigItem.hideMenuRight) {
      menuRight.forEach((o: any) => {
        o.style.visibility = 'hidden';
      });
    }
  };

  render() {
    let { children, licenseApps } = this.props;
    let browserCheck = utils.browserCheck();
    if (!licenseApps) {
      children = <GlobalLoading />;
    }
    if (!browserCheck) {
      children = (
        <div>
          <Header />
          <ChromeDownload />
        </div>
      );
    }
    return (
      (
        <Cookies
          watchFields={[
            // 当页面cookie如下字段的值发生变更时会触发页面刷新
            'dt_token',
            'dt_tenant_id',
            'dt_user_id',
            'project_id',
            'science_project_id',
            'stream_project_id',
            'api_project_id',
            'dq_project_id',
            'tag_project_id',
          ]}
          onFieldsChanged={this.onFieldsChanged}>
          {children}
        </Cookies>
      ) || <NotFound />
    );
  }
}

export default Main;
