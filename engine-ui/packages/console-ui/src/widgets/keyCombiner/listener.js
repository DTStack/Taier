import React from 'react';

export default class KeyEventListener extends React.Component {
    constructor (props) {
        super(props);
    }

    componentDidMount () {
        addEventListener('keydown', this.bindEvent, false)
        addEventListener('keyup', this.bindEvent, false)
    }

    componentWillUnmount () {
        removeEventListener('keydown', this.bindEvent, false)
        removeEventListener('keyup', this.bindEvent, false)
    }

    bindEvent = (target) => {
        const { onKeyDown, onKeyUp } = this.props;
        const isKeyDown = target.type === 'keydown';

        if (isKeyDown && onKeyDown) {
            onKeyDown(target)
        } else if (!isKeyDown && onKeyUp) {
            onKeyUp(target)
        }
    }

    render () {
        return this.props.children;
    }
}
