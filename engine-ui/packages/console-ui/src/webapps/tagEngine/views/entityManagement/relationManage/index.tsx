import * as React from 'react';

interface IProps {}
interface IState {}

export default class RelationManage extends React.PureComponent<IProps, IState> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState={

    }
    componentDidMount () {

    }
    render () {
        return (
            <div className="relationManage">
              关系管理
            </div>
        )
    }
}
