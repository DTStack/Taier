import * as React from 'react';

export default class KeyCombiner extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            currentKeys: {}
        };
    }

    componentDidMount () {
        addEventListener('keydown', this.bindEvent, false)
        addEventListener('keyup', this.bindEvent, false)
    }

    componentWillUnmount () {
        removeEventListener('keydown', this.bindEvent, false)
        removeEventListener('keyup', this.bindEvent, false)
        this.setState({ currentKeys: {} })
    }

    bindEvent = (target: any) => {
        const { onTrigger, keyMap } = this.props;

        const keyCode = target.keyCode;
        const isKeyDown = target.type === 'keydown';

        if (!isKeyDown) {
            this.setState({
                currentKeys: {}
            })
            return;
        };

        if (keyMap[keyCode] === true) {
            const currentKeys = Object.assign(this.state.currentKeys, {
                [keyCode]: isKeyDown
            });

            this.setState({
                currentKeys
            })

            let keyAllRight = true;
            for (let key in keyMap) {
                if (!currentKeys[key]) {
                    keyAllRight = false;
                    break;
                }
            }
            if (keyAllRight) {
                onTrigger(target);
            }
        }
    }

    render () {
        return this.props.children;
    }
}
