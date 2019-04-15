import React, { PureComponent } from 'react'
import '../../styles/views/index/index.scss';
import Welcome from './welcome';
import ProjectsList from './projectsList';
class Container extends PureComponent {
    state = {
        isProject: false
    }
    toggleProject = () => {
        this.setState({
            isProject: !this.state.isProject
        })
    }
    render () {
        const { isProject } = this.state;
        return (
            <>
                {isProject ? <ProjectsList toggle={this.toggleProject} /> : <Welcome toggle={this.toggleProject} />}
            </>
        )
    }
}
export default Container
