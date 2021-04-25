import * as React from 'react';
import { Icon, Tooltip, Modal } from 'antd';
import { isArray } from 'lodash';

const TEST_STATUS: any = {
    SUCCESS: true,
    FAIL: false
}
export default class TestRestIcon extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {}
    }
    // show err message
    showDetailErrMessage (engine: any) {
        Modal.error({
            title: `错误信息`,
            content: `${engine.errorMsg}`,
            zIndex: 1061
        })
    }
    matchCompTest (testResult: any) {
        switch (testResult?.result) {
            case TEST_STATUS.SUCCESS: {
                return <Icon className='success-icon' type="check-circle" theme="filled" />
            }
            case TEST_STATUS.FAIL: {
                return <Tooltip
                    title={
                        <a
                            style={{ color: '#fff', overflow: 'scroll' }}
                            onClick={ this.showDetailErrMessage.bind(this, testResult)}
                        >
                            {!isArray(testResult?.errorMsg) ? <span>{testResult?.errorMsg}</span>
                                : testResult?.errorMsg?.map(msg => {
                                    return <p key={msg.componentVersion}>{ msg.componentVersion ? (msg.componentVersion + ' : ') : '' }{msg.errorMsg}</p>
                                })}
                        </a>
                    }
                    placement='right'

                >
                    <Icon className='err-icon' type="close-circle" theme="filled" />
                </Tooltip>
            }
            default: {
                return null
            }
        }
    }
    render () {
        const { testStatus } = this.props;
        return this.matchCompTest(testStatus);
    }
}
