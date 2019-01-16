import React from 'react';

import { Button } from 'antd';

import RegisterParamsNav, { NAV_KEYS } from './nav';
import ErrorCode from './errorCode';
import Params from './params';
import Result from './result';

const ContentMap = {
    [NAV_KEYS.PARAMS]: Params,
    [NAV_KEYS.ERRORCODE]: ErrorCode,
    [NAV_KEYS.RESULT]: Result
}
class RegisterParams extends React.Component {
    state = {
        menuSelect: NAV_KEYS.PARAMS
    }
    pass () {
        return true;
    }
    prev () {
        this.props.prev();
    }
    cancelAndSave () {
        const { cancelAndSave } = this.props;
        cancelAndSave({});
    }
    updateData (data) {
        const { registerParams } = this.props;
        this.props.dataChange({
            ...registerParams,
            ...data
        }, true)
    }
    render () {
        const { menuSelect } = this.state;
        const { registerParams } = this.props;
        const Content = ContentMap[menuSelect];
        return (
            <div>
                <div className="steps-content">
                    <div className='c-register-params'>
                        <div className='c-register-params__nav'>
                            <RegisterParamsNav
                                value={menuSelect}
                                onChange={(value) => {
                                    this.setState({
                                        menuSelect: value
                                    })
                                }}
                            />
                        </div>
                        <div className='c-register-params__content'>
                            <Content
                                data={registerParams}
                                updateData={this.updateData.bind(this)}
                            />
                        </div>
                    </div>
                </div>
                <div
                    className="steps-action"
                >
                    {
                        <Button onClick={this.cancelAndSave.bind(this)}>
                            保存并退出
                        </Button>
                    }
                    {
                        <Button style={{ marginLeft: 8 }} onClick={() => this.prev()}>上一步</Button>
                    }
                    {
                        <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.pass()}>下一步</Button>
                    }

                </div>
            </div>
        )
    }
}
export default RegisterParams;
