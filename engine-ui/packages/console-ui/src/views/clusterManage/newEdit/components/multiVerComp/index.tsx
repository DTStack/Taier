import * as React from 'react'
import InitailComp from './initialComp'

interface IProps {
    comp: any;
    versionData: any;
}

interface IState {
    intailVersion: string;
}

export default class MultiVersionComp extends React.Component<IProps, IState> {
    state: IState = {
        intailVersion: ''
    }

    handleVersion = (version: string) => {
        this.setState({ intailVersion: version })
    }

    render () {
        const { comp, versionData } = this.props
        const { intailVersion } = this.state

        return <div>
            {
                !comp?.hadoopVersion && !intailVersion && <InitailComp
                    comp={comp}
                    versionData={versionData}
                    handleVersion={this.handleVersion}
                />
            }
        </div>
    }
}
