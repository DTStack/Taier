import * as React from 'react';
import { Steps } from 'antd';
import Breadcrumb from '../../../components/breadcrumb';
import StepOne from './components/stepOne';
import StepTwo from './components/stepTwo';
import StepTree from './components/stepThree';
import './style.scss';

const { Step } = Steps;

interface IProps {
    router: any;
}
interface IState {
    current: number;
    stepsValues: any[];
}
export default class CreateLabel extends React.PureComponent<IProps, IState> {
    constructor (props: any) {
        super(props);
    }
    state: IState = {
        current: 0,
        stepsValues: []
    };
    componentDidMount () { }
    onPrev = () => {
        let current = this.state.current - 1;
        if (current < 0) {
            current = 0;
            this.props.router.goBack()
        }
        this.setState({ current });
    }
    onNext = (values: any) => {
        const { stepsValues, current } = this.state;
        const newCurrent = current + 1;
        if (newCurrent > 2) {
            this.props.router.goBack();
        } else {
            stepsValues[current] = values;
            if (newCurrent == 2) {
                let params = Object.assign({ id: 0 }, stepsValues[0], values);
                this.saveEntityConfig(params);
            } else {
                this.setState({ current: newCurrent, stepsValues });
            }
        }
    }
    saveEntityConfig = (params: any) => {
        this.setState({ current: 2 });
    }
    render () {
        const { current } = this.state;
        const breadcrumbNameMap = [
            {
                path: '/labelCenter',
                name: '标签管理'
            },
            {
                path: '/createLabel',
                name: '新建标签'
            }
        ];
        return (
            <div className="create-label">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <div className="create_label_content">
                    <Steps current={current}>
                        <Step title="设置基本信息" />
                        <Step title="设置标签规则" />
                        <Step title="完成配置" />
                    </Steps>
                    <div className="step_content">

                        <StepOne onPrev={this.onPrev} isShow={current == 0} onNext={this.onNext} />
                        <StepTwo isShow={current == 1} onPrev={this.onPrev} onNext={this.onNext} />
                        {
                            current == 2 && <StepTree onPrev={this.onPrev} onNext={this.onNext} />
                        }
                    </div>
                </div>
            </div>
        );
    }
}
