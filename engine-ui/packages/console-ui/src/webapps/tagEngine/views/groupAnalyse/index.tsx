import * as React from 'react';
interface IProps {}
interface IState {}

export default class GroupAnalyse extends React.PureComponent<IProps, IState> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState={

    }
    componentDidMount () {

    }
    render () {
        return (
            <div className="componentName">
            群组分析
            </div>
        )
    }
}
