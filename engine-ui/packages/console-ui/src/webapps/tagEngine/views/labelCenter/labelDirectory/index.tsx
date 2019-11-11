import * as React from 'react';

interface IProps {}

interface IState {}

export default class ComponentName extends React.PureComponent<IProps, IState> {
    state: IState = {};
    componentDidMount () {}
    render () {
        return <div className="componentName">标签类目</div>;
    }
}
