import React from 'react';
import { connect } from 'react-redux';

import utils from 'utils';
import { appUriDict } from 'main/consts';
import GlobalLoading from '../../components/globalLoading';

import * as ProjectAction from '../../store/modules/project'

@connect(state => {
    return {
        licenseApps: state.licenseApps
    }
})
class GateWay extends React.Component {
    componentDidMount () {
        this.routerDispatch();
    }
    routerDispatch () {
        const { dispatch, router } = this.props;
        const projectId = utils.getParameterByName('projectId');
        const redirectUri = utils.getParameterByName('redirect_uri');
        if (projectId) {
            dispatch(ProjectAction.getProject(projectId));
        }
        if (redirectUri) {
            switch (redirectUri) {
                case appUriDict.RDOS.OPERATION: {
                    router.push('operation');
                    break;
                }
                case appUriDict.RDOS.DEVELOP: {
                    router.push('offline/task');
                    break;
                }
                case appUriDict.RDOS.OPERATION_MANAGER: {
                    router.push('operation/offline-management');
                    break;
                }
            }
        }
    }
    render () {
        return <div>
            <GlobalLoading />
        </div>
    }
}
export default GateWay;
