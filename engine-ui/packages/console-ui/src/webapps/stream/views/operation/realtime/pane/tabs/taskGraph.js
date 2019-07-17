import React from 'react'

import StreamDetailGraph from './graph'

class TaskGraph extends React.Component {
    getBaseInfo () {
        const { data = {}, isShow } = this.props;
        if (!isShow) {
            return null;
        }
        return (
            <StreamDetailGraph data={data} />
        )
    }
    render () {
        return (
            <div>
                {this.getBaseInfo()}
            </div>
        )
    }
}

export default TaskGraph;
