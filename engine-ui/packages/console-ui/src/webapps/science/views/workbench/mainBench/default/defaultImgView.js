import React from 'react';

class DefaultImgView extends React.Component {
    render () {
        return (
            <div className='c-default-page'>
                <img className='c-default-page__img' src={this.props.imgSrc} />
            </div>
        )
    }
}
export default DefaultImgView;
