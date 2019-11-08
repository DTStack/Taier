import * as React from 'react';

interface IProps {}
interface IState {}

export default class DictionaryManage extends React.PureComponent<IProps, IState> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState={

    }
    componentDidMount () {

    }
    render () {
        return (
            <div className="dictionaryManage">
              字典管理
            </div>
        )
    }
}
