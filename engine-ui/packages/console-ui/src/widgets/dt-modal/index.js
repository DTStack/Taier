import React, { Component } from 'react';
import { Modal, Icon } from 'antd';
import FullScreen from 'widgets/fullscreen';

const defaultModalStyle = {
    width: 800,
    height: 560
};
export default class DTModal extends Component {
    state = {
        modalStyle: defaultModalStyle
    }

    onFullscreen = (isFullscreen) => {
        const { style, width } = this.props;
        if (isFullscreen) {
            this.setState({
                modalStyle: {
                    ...defaultModalStyle,
                    ...style,
                    width
                }
            })
        } else {
            this.setState({
                modalStyle: {
                    width: '100%',
                    height: '100%',
                    margin: 0,
                    padding: 0,
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0
                }
            })
        }
    }

    renderToolbox = () => {
        const { toolbox, fullscreen } = this.props;
        return (
            <div
                style={{
                    position: 'absolute',
                    right: '48px',
                    top: '-30px'
                }}
                className="dt-modal-toolbox"
            >
                {
                    !fullscreen || fullscreen === true
                        ? <FullScreen
                            fullIcon={<Icon className="alt" type="arrows-alt" />}
                            exitFullIcon={<Icon className="alt" type="shrink" />}
                            isShowTitle={false}
                            onFullscreen={this.onFullscreen}
                        /> : null
                }
                { toolbox }
            </div>
        )
    }

    render () {
        const { children, style } = this.props;
        const { modalStyle } = this.state;
        const applyStyle = { ...style, ...modalStyle };
        return <Modal
            {...this.props}
            width={modalStyle.width}
            style={applyStyle}
        >
            {this.renderToolbox()}
            { children }
        </Modal>
    }
}
