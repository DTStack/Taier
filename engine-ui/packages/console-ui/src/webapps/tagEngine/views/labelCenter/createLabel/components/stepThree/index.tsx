import * as React from 'react';
import Result from '../../../../../components/result/index';
import './style.scss';

interface IProps{
    onNext: Function;
    onPrev: Function;
}

interface IState{
    second: number;
}

export default class StepThree extends React.PureComponent<IProps, IState> {
    constructor (props: IProps) {
        super(props);
    }
    state: IState={
        second: 3
    }
    timer: any
    componentDidMount () {
        this.timer = setInterval(() => {
            let { second } = this.state;
            second = second - 1;
            this.setState({
                second: second > 0 ? second : 0
            }, () => {
                if (second < 0) {
                    this.props.onNext()
                }
            })
        }, 1000)
    }
    componentWillUnmount () {
        clearInterval(this.timer)
    }
    onHandleClick = () => {
        this.props.onNext();
    }
    render () {
        const { second } = this.state;
        return (
            <div className="StepThree">
                <Result
                    status="success"
                    title="实体配置完成"
                    subTitle={`${second}秒后自动返回`}
                />
            </div>
        )
    }
}
