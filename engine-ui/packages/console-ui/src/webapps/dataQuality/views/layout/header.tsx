import * as React from 'react'
import { connect } from 'react-redux'

import Navigator from 'main/components/nav';
import { getHeaderLogo } from 'main/consts';
import { setProject } from '../../actions/project';

@(connect((state: any) => {
    return {
        user: state.user,
        apps: state.apps,
        routing: state.routing,
        app: state.app,
        licenseApps: state.licenseApps,
        projects: state.project.projectList,
        project: state.project.currentProject
    };
}, (dispatch: any) => {
    return {
        setProject (project: any) {
            return dispatch(setProject(project))
        }
    }
}) as any)
class Header extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {};
    }
    fixArrayIndex = (arr: any) => {
        let fixArrChildrenApps: any = [];
        if (arr && arr.length > 1) {
            arr.map((item: any) => {
                switch (item.name) {
                    case '概览':
                        fixArrChildrenApps[0] = item;
                        break;
                    case '任务查询':
                        fixArrChildrenApps[1] = item;
                        break;
                    case '规则配置':
                        fixArrChildrenApps[2] = item;
                        break;
                    case '逐行校验':
                        fixArrChildrenApps[3] = item;
                        break;
                    case '数据源管理':
                        fixArrChildrenApps[4] = item;
                        break;
                    case '项目管理':
                        fixArrChildrenApps[5] = item;
                        break;
                }
            })
            return fixArrChildrenApps
        } else {
            return []
        }
    }
    render () {
        const { app, licenseApps } = this.props;
        const baseUrl = '/dataQuality.html#';
        const fixArrChildrenApps = this.fixArrayIndex(licenseApps[3] && licenseApps[3].children);
        const overviewNav = fixArrChildrenApps[0];
        const taskQueryNav = fixArrChildrenApps[1];
        const ruleNav = fixArrChildrenApps[2];
        const dataCheckNav = fixArrChildrenApps[3];
        const dataSourceNav = fixArrChildrenApps[4];
        const projectNav = fixArrChildrenApps[5];
        const menuItems: any = [
            {
                id: 'dq/overview',
                name: '概览',
                link: `${baseUrl}/dq/overview`,
                enable: overviewNav && overviewNav.isShow
            },
            {
                id: 'dq/taskQuery',
                name: '任务查询',
                link: `${baseUrl}/dq/taskQuery`,
                enable: taskQueryNav && taskQueryNav.isShow
            },
            {
                id: 'dq/rule',
                name: '规则配置',
                link: `${baseUrl}/dq/rule`,
                enable: ruleNav && ruleNav.isShow
            },
            {
                id: 'dq/dataCheck',
                name: '逐行校验',
                link: `${baseUrl}/dq/dataCheck`,
                enable: dataCheckNav && dataCheckNav.isShow
            },
            {
                id: 'dq/dataSource',
                name: '数据源管理',
                link: `${baseUrl}/dq/dataSource`,
                enable: dataSourceNav && dataSourceNav.isShow
            },
            {
                id: 'dq/project',
                name: '项目管理',
                link: `${baseUrl}/dq/project`,
                enable: projectNav && projectNav.isShow
            }
        ];

        const logo = (
            <React.Fragment>
                <img
                    className='c-header__logo c-header__logo--dq'
                    alt="logo"
                    src={getHeaderLogo(app.id)}
                />
                <span className='c-header__title c-header__title--dq'>
                    {window.APP_CONF.prefix ? `${window.APP_CONF.prefix}.` : ''}{window.APP_CONF.name}
                </span>
            </React.Fragment>
        );
        return <Navigator
            logo={logo}
            menuItems={menuItems}
            licenseApps={licenseApps}
            {...this.props}
            showHelpSite={true}
            helpUrl='/public/helpSite/valid/v3.0/Summary.html'
        />;
    }
}
export default Header;
