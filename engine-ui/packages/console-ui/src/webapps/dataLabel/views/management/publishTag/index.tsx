import * as React from 'react';
import { Steps } from 'antd';

import StepOne from './stepOne';
import StepTwo from './stepTwo';
import StepThree from './stepThree';
import GoBack from 'main/components/go-back';
import Api from '../../../api/apiManage';

const Step = Steps.Step;

export default class PublishTag extends React.Component<any, any> {
    state: any = {
        current: 0,
        editStatus: 'new',
        basicInfo: {},
        paramsConfig: {},
        tagId: this.props.routeParams.tagId,
        tagType: 1
    }

    componentDidMount () {
        const { tagId } = this.state;

        Api.getApiInfo({ tagId }).then((res: any) => {
            if (res.code === 1) {
                let data = res.data;
                let basicInfo: any = {
                    name: data.name,
                    catalogueId: data.catalogueId,
                    tagDesc: data.tagDesc,
                    tagRange: data.tagRange,
                    reqLimit: data.reqLimit,
                    respLimit: data.respLimit,
                    dataSourceId: data.dataSourceId,
                    identityColumn: data.identityColumn,
                    identityId: data.identityId,
                    originTable: data.originTable,
                    originColumn: data.originColumn
                };

                this.setState({
                    basicInfo: basicInfo,
                    paramsConfig: {
                        inputParams: data.inputParam,
                        outputParams: data.outputParam
                    },
                    tagType: data.type
                });
            }
        });

        if (this.props.route.path.indexOf('editApi') > -1) {
            this.setState({ editStatus: 'edit' });
        }
    }

    navToStep = (value: any) => {
        this.setState({ current: value });
    }

    changeBasicInfo = (obj: any) => {
        let basicInfo: any = { ...this.state.basicInfo, ...obj };
        this.setState({ basicInfo });
    }

    changeParamsConfig = (obj: any) => {
        let paramsConfig: any = { ...this.state.paramsConfig, ...obj };
        this.setState({ paramsConfig });
    }

    render () {
        const { tagId, tagType, current, basicInfo, paramsConfig, editStatus } = this.state;
        const steps = [
            {
                title: '基本属性',
                content: <StepOne
                    tagType={tagType}
                    currentStep={current}
                    navToStep={this.navToStep}
                    basicInfo={basicInfo}
                    editStatus={editStatus}
                    changeBasicInfo={this.changeBasicInfo}
                />
            },
            {
                title: '参数配置',
                content: <StepTwo
                    tagId={tagId}
                    currentStep={current}
                    navToStep={this.navToStep}
                    basicInfo={basicInfo}
                    paramsConfig={paramsConfig}
                    changeParamsConfig={this.changeParamsConfig}
                />
            },
            {
                title: '完成',
                content: <StepThree
                    currentStep={current}
                    navToStep={this.navToStep}
                />
            }
        ];

        return (
            <div className="box-1">
                <h1 className="box-title">
                    <GoBack />
                    <span className="m-l-8">
                        标签发布
                    </span>
                </h1>

                <div className="steps-container">
                    <Steps current={current}>
                        { steps.map((item: any) => <Step key={item.title} title={item.title} />) }
                    </Steps>
                    { steps[current].content }
                </div>
            </div>
        )
    }
}
