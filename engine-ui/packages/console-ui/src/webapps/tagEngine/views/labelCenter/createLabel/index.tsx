import * as React from 'react';
import { Steps } from 'antd';
import Breadcrumb from '../../../components/breadcrumb';
import StepOne from './components/stepOne';
import StepTwo from './components/stepTwo';
import StepTree from './components/stepThree';
import { API } from '../../../api/apiMap';
import './style.scss';

const { Step } = Steps;

interface IProps {
    router?: any;
    location?: any;
}
interface IState {
    current: number;
    stepsValues: any[];
    data: any;
    tagId: number;
}
export default class CreateLabel extends React.PureComponent<IProps, IState> {
    constructor (props: any) {
        super(props);
    }
    state: IState = {
        current: 0,
        stepsValues: [],
        data: {},
        tagId: null
    };
    componentDidMount () {
        const { location } = this.props;
        const { tagId } = location.query;
        if (tagId) {
            this.setState({
                tagId
            })
            this.getDeriveTagVO(tagId)
        }
    }
    getDeriveTagVO = (tagId) => { // 获取标签详情
        API.getDeriveTagVO({ tagId }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                this.setState({
                    data: data
                })
            }
        })
    }
    onPrev = () => {
        let current = this.state.current - 1;
        if (current < 0) {
            current = 0;
            this.props.router.goBack()
        }
        this.setState({ current });
    }
    onNext = (values: any) => {
        const { stepsValues, current, tagId } = this.state;
        const newCurrent = current + 1;
        if (newCurrent > 2) {
            this.props.router.goBack();
        } else {
            stepsValues[current] = values;
            if (newCurrent == 2) {
                let params = Object.assign({ id: tagId }, stepsValues[0], values);
                this.saveEntityConfig(params);
            } else {
                this.setState({ current: newCurrent, stepsValues });
            }
        }
    }
    saveEntityConfig = (data: any) => {
        let params = Object.assign({}, data, {
            tags: data.tags.map(item => {
                return {
                    tagValueId: item.tagValueId,
                    tagValue: item.label,
                    param: JSON.stringify(item.params)
                }
            })
        })
        this.addOrUpdateDeriveTag(params);
    }
    addOrUpdateDeriveTag = (params) => {
        API.addOrUpdateDeriveTag(params).then(res => {
            const { code } = res;
            if (code == 1) {
                this.setState({ current: 2 });
            }
        })
    }
    render () {
        const { current, data } = this.state;
        const { location } = this.props;
        const { entityId, tagId } = location.query;
        const breadcrumbNameMap = [
            {
                path: '/labelCenter',
                name: '标签管理'
            },
            {
                path: '/createLabel',
                name: tagId ? '编辑衍生标签' : '新建衍生标签'
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

                        <StepOne onPrev={this.onPrev} data={data} entityId={entityId} isShow={current == 0} onNext={this.onNext} />
                        <StepTwo isShow={current == 1} tagId={tagId} data={data} entityId={entityId} onPrev={this.onPrev} onNext={this.onNext} />
                        {
                            current == 2 && <StepTree onPrev={this.onPrev} onNext={this.onNext} />
                        }
                    </div>
                </div>
            </div>
        );
    }
}
