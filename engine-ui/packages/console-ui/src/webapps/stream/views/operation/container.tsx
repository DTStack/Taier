import * as React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'

import * as UserAction from '../../store/modules/user'

const propType: any = {
    children: PropTypes.node
}
const defaultPro: any = {
    children: []
}

class Container extends React.Component<any, any> {
    state: any = {
        collapsed: false,
        mode: 'inline'
    };
    static defaultProps: any;
    static propTypes: any;

    componentDidMount () {
        this.initUsers(this.props.project);
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { project = {} } = nextProps;
        const { project: old_project = {} } = this.props;
        if (old_project.id != project.id) {
            console.log(old_project.id, project.id)
            this.initUsers(project);
        }
    }
    initUsers (project: any) {
        const { id } = project;
        if (id) {
            this.props.dispatch(UserAction.getProjectUsers());
        }
    }
    onCollapse = (collapsed: any) => {
        this.setState({
            collapsed,
            mode: collapsed ? 'vertical' : 'inline'
        });
    }

    render () {
        const { children } = this.props
        return (
            <div className="dt-operation">
                {children || "i'm container."}
            </div>
        )
    }
}
Container.propTypes = propType
Container.defaultProps = defaultPro
export default connect((state: any) => {
    return {
        project: state.project
    }
})(Container)
