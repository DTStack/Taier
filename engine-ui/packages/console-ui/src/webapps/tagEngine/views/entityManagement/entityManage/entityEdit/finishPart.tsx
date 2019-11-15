import * as React from 'react';
import { Icon } from 'antd';
import './style.scss';

let timer: any = null;

interface IProps {
    goBack: any;
}

interface IState {
    count: number;
}

export default class FinishPart extends React.Component<IProps, IState> {
    state: IState = {
        count: 3
    }

    componentDidMount () {
        timer = setInterval(() => {
            const { count } = this.state;
            if (count === 0) {
                clearInterval(timer);
                timer = null;
                this.props.goBack();
            } else {
                this.setState({
                    count: count - 1
                })
            }
        }, 1000)
    }

    componentWillUnmount () {
        if (timer) {
            clearInterval(timer);
            timer = null;
        }
    }

    render () {
        const { count } = this.state;
        return (
            <div className="ee-finish-part">
                <Icon type="check-circle" style={{ color: '#00A755', fontSize: 40 }} />
                <span style={{ fontSize: 14, color: '#333333', margin: '20px 0px 8px' }}>实体配置完成</span>
                <span>{count}秒后自动返回</span>
            </div>
        )
    }
}
