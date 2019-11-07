import * as React from 'react';
import './style.scss';
interface IProps{

}

interface IState{

}

export default class componentName extends React.PureComponent<IProps, IState> {
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
