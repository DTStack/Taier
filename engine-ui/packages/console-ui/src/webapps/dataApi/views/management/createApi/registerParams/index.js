import React from 'react';

import { Button } from 'antd';
class RegisterParams extends React.Component {
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
    render () {
        return (
            <div>
                <div className="steps-content">
                    register
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
