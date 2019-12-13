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
    entityId: string|number;

}
interface IState {
    current: number;
    stepsValues: any[];
    preData: any;
    tagId: number|string;
}
export default class EditAtomicLabel extends React.PureComponent<IProps, IState> {
    constructor (props: any) {
        super(props);
    }
    state: IState = {
        current: 0,
        preData: {},
        stepsValues: [],
        tagId: ''
    };
    componentDidMount () {
        const { location } = this.props;
        const { tagId } = location.query;
        if (tagId) {
            this.setState({
                tagId
            })
            this.getEditorDetailVo(tagId)
        }
    }
    getEditorDetailVo = (tagId) => {
        API.getEditorDetailVo({
            tagId
        }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                this.setState({
                    preData: Object.assign({}, data.tagAtomRuleVo, data.tagDetailVo)
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
                let params = Object.assign({ tagId: tagId }, stepsValues[0], values);
                this.saveEntityConfig(params);
            } else {
                this.setState({ current: newCurrent, stepsValues });
            }
        }
    }
    saveEntityConfig = (params: any) => {
        const { tagId, tagName, tagDesc, tagCateId, tagDictId, tagDictName = '', dictValueVoList } = params;
        const newParams: any = {
            tagId,
            tagName,
            tagDesc,
            moveTagCateId: tagCateId
        }
        if (tagDictId) {
            newParams.referenceDictId = tagDictId
        } else {
            newParams.dictParam = {
                id: null,
                name: tagDictName,
                desc: tagDictName,
                type: 0,
                dictValueParamList: dictValueVoList
            }
        }
        this.editorAtomTagRule(newParams)
    }
    editorAtomTagRule = (params) => {
        API.editorAtomTagRule(params).then(res => {
            const { code } = res;
            if (code === 1) {
                this.setState({ current: 2 });
            }
        })
    }
    render () {
        const { current, preData } = this.state;
        const { location } = this.props;
        const { entityId, tagId } = location.query;
        const breadcrumbNameMap = [
            {
                path: '/labelCenter',
                name: '标签管理'
            },
            {
                path: '/editAtomicLabel',
                name: '编辑原子标签'
            }
        ];
        return (
            <div className="editAtomicLabel">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <div className="create_label_content">
                    <Steps current={current}>
                        <Step title="设置基本信息" />
                        <Step title="设置标签规则" />
                        <Step title="完成配置" />
                    </Steps>
                    <div className="step_content">

                        <StepOne onPrev={this.onPrev} data={preData} tagId={tagId} entityId={entityId} isShow={current == 0} onNext={this.onNext} />
                        <StepTwo isShow={current == 1} data={preData} entityId={entityId} onPrev={this.onPrev} onNext={this.onNext} />
                        {
                            current == 2 && <StepTree onPrev={this.onPrev} onNext={this.onNext} />
                        }
                    </div>
                </div>
            </div>
        );
    }
}
