import * as React from 'react';
import { Icon, Tooltip, Modal } from 'antd';
// import { COMPONENT_TYPE_VALUE } from '../../consts'
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
            content: `${engine.errorMsg}`
        })
    }
    matchCompTest (testResult: any) {
        switch (testResult.result) {
            case TEST_STATUS.SUCCESS: {
                return <Icon className='success-icon' type="check-circle" />
            }
            case TEST_STATUS.FAIL: {
                return <Tooltip
                    title={
                        <a
                            style={{ color: '#fff' }}
                            onClick={ this.showDetailErrMessage.bind(this, testResult)}
                        >
                            {testResult.errorMsg}
                        </a>
                    }
                    placement='right'

                >
                    <Icon className='err-icon' type="close-circle" />
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
