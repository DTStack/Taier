import { Component } from 'react';
import { Form } from 'antd';
import BaseLoginFrom from './login/baseLoginFrom';
import background from './public/background.jpg';
import cover from './public/login-cover.png';

import '@/styles/style.css';

class LoginContainer extends Component {
    constructor(props) {
        super(props);
        document.title = '登录';
        this.state = {
            loading: true,
            sysId: '',
            sysType: 0,
            showButton: false,
            loginUrl: '',
            defineIntoUIC: false,
        };
    }

    getRenderLoginForm = () => {
        const { form, config } = this.props;
        const { sysType, loginUrl, showButton } = this.state;

        return (
            <BaseLoginFrom
                config={config}
                form={form}
                loginUrl={loginUrl}
                sysType={sysType}
                showButton={showButton}
            />
        );
    };

    render() {
        return (
            <div className="login-container">
                <img className="c-login__bg" alt="" src={background} />
                <div className="c-login__wrap">
                    <img
                        alt=""
                        style={{ width: 540, height: 540 }}
                        src={cover}
                    />
                    <div className="c-login__container">
                        <div
                            className="c-login__container__title"
                            style={{ color: '#000' }}
                        >
                            欢迎登录 DAGScheduleX
                        </div>
                        <Form
                            className="c-login__container__form"
                            layout="vertical"
                            hideRequiredMark={true}
                        >
                            {this.getRenderLoginForm()}
                        </Form>
                    </div>
                </div>
            </div>
        );
    }
}

LoginContainer = Form.create()(LoginContainer);
export default LoginContainer;
