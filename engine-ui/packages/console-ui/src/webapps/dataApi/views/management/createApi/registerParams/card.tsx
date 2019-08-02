import * as React from 'react';

class RegisterParamsCard extends React.Component<any, any> {
    render () {
        const { title, extra, className, ...others } = this.props;
        return (
            <div {...others} className={`c-register-params__content__card ${className || ''}`}>
                <div className='c-register-params__content__card__head'>
                    <span className='c-register-params__content__card__title'>{title}</span>
                    <div>{extra}</div>
                </div>
                <div className='c-register-params__content__card__main'>
                    {this.props.children}
                </div>
            </div>
        )
    }
}
export default RegisterParamsCard;
