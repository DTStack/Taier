import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Button,
    Form,
    message
} from 'antd';
import RuleList from '../ruleList';

import { ruleConfigActions } from '../../../actions/ruleConfig';

const mapStateToProps = state => {
    const { ruleConfig, common } = state;
    return { ruleConfig, common };
};

const mapDispatchToProps = dispatch => ({
    getRuleFunction (params) {
        dispatch(ruleConfigActions.getRuleFunction(params));
    },
    getTableColumn (params) {
        dispatch(ruleConfigActions.getTableColumn(params));
    }
});

@connect(
    mapStateToProps,
    mapDispatchToProps
)
class StepTwo extends Component {
    componentDidMount () {
        const { editParams } = this.props;

        this.props.getRuleFunction();
        this.props.getTableColumn({
            sourceId: editParams.dataSourceId,
            tableName: editParams.tableName
        });
    }

    prev = () => {
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    };

    next = () => {
        const { currentStep, navToStep, editParams } = this.props;

        if (editParams.rules.length) {
            if (editParams.rules.find((r) => { return r.isEdit })) {
                message.warn('请先保存修改中的内容')
            } else {
                navToStep(currentStep + 1);
            }
        } else {
            message.error('请添加监控规则');
        }

        // if (editParams.rules.length && isEmpty(currentRule)) {
        //     navToStep(currentStep + 1);
        // } else {
        //     message.error('请添加监控规则');
        // }
    };

    render () {
        const { editParams, ruleConfig } = this.props;
        const { rules } = editParams;
        const { tableColumn } = ruleConfig;
        return (
            <div>
                <div className="steps-content">
                    <RuleList
                        couldSaveAll
                        style={{ padding: '0px 25px' }}
                        tableColumn={tableColumn}
                        data={editParams}
                        getInitData={function () {
                            return rules;
                        }}
                        onRuleListChange={(ruleList) => {
                            this.props.changeParams({ rules: ruleList });
                        }}
                    />
                </div>

                <div className="steps-action">
                    <Button onClick={this.prev}>上一步</Button>
                    <Button
                        className="m-l-8"
                        type="primary"
                        onClick={this.next}
                    >
                        下一步
                    </Button>
                </div>
            </div>
        );
    }
}

export default Form.create()(StepTwo);
