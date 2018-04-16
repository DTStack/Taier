import React, { Component } from 'react';
import { Tooltip, message } from 'antd';

export default class ClipBoard extends Component {

    state = {
        
    }

    componentWillUnmount() {
        this.removeFake();
    }

    copy = (value) => {
        this.removeFake();

        this.fakeHandlerCallback = () => this.removeFake();
        this.fakeHandler = document.body.addEventListener('click', this.fakeHandlerCallback) || true;

        this.fakeElem = document.createElement('textarea');
        // Prevent zooming on iOS
        this.fakeElem.style.fontSize = '12pt';

        // Reset box model
        this.fakeElem.style.border = '0';
        this.fakeElem.style.padding = '0';
        this.fakeElem.style.margin = '0';

        // Move element out of screen horizontally
        this.fakeElem.style.position = 'absolute';
        this.fakeElem.style.left = '-9999px';

        // Move element to the same position vertically
        let yPosition = window.pageYOffset || document.documentElement.scrollTop;
        this.fakeElem.style.top = `${yPosition}px`;

        this.fakeElem.setAttribute('readonly', '');
        this.fakeElem.value = value;

        document.body.appendChild(this.fakeElem);
        this.fakeElem.select();

        this.copyText();
    }

    removeFake() {
        if (this.fakeHandler) {
            document.body.removeEventListener('click', this.fakeHandlerCallback);
            this.fakeHandler = null;
            this.fakeHandlerCallback = null;
        }

        if (this.fakeElem) {
            document.body.removeChild(this.fakeElem);
            this.fakeElem = null;
        }
    }

    copyText() {
        let succeeded;

        try {
            succeeded = document.execCommand('copy');
        }
        catch (err) {
            succeeded = false;
        }

        this.handleResult(succeeded);
    }

    handleResult(succeeded) {
        if (succeeded) {
             message.success('复制成功');
        } else {
             message.error('不支持');
        }
    }

    render() {
        const { value } = this.props;

        return <Tooltip 
            placement="right" 
            overlayClassName="copy-area"
            overlayStyle={{ cursor: 'pointer' }}
            title={
                <span onClick={this.copy.bind(this, value)}>复制</span>
            }
        >
            {value}
        </Tooltip>
    }
}