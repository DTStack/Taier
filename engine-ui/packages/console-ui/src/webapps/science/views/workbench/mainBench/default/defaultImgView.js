import React from 'react';

class DefaultImgView extends React.Component {
    render () {
        return (
            <div className='c-default-page'>
                <div className='c-default-page__container'>
                    <p className='c-default-page__text'>{this.props.text}</p>
                    <div className='c-default-page__img__container'>
                        <img className='c-default-page__img' src={this.props.imgSrc} />
                        {this.props.imgButton}
                    </div>
                </div>
            </div>
        )
    }
}
export default DefaultImgView;
