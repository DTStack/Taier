import React from 'react';
import './style.scss';

const MENU_ITEM_HEIGHT = 25;
const MENU_PADDING = 20;

export default class CtxMenu extends React.Component {
    constructor (props) {
        super(props);

        this.showMenu = this.showMenu.bind(this);
        this.hideMenu = this.hideMenu.bind(this)
        this.state = {
            show: false,
            x: 0,
            y: 0
        };
    }

    componentDidMount () {
        this.box.addEventListener('contextmenu', this.showMenu, false);
        document.addEventListener('click', this.hideMenu, false);
    }

    // eslint-disable-next-line
    componentWillUnmount () {
        this.box.removeEventListener('contextmenu', this.showMenu, false);
        document.removeEventListener('click', this.hideMenu, false);
    }

    hideMenu (e) {
        if (!this.state.show) return;
        this.setState({
            show: false
        });
    }

    showMenu (e) {
        e.preventDefault();
        this.setState({
            show: true,
            x: e.clientX,
            y: e.clientY
        });
    }

    render () {
        const { children, operations, id } = this.props;
        const { show, x, y } = this.state;

        const viewHeight = document.body.offsetHeight; // 可视区高度
        const distanceToBottom = viewHeight - y;
        const menuHeight = operations.length * MENU_ITEM_HEIGHT + MENU_PADDING;
        const menuTop = distanceToBottom > menuHeight ? y : y - menuHeight;

        return <span ref={ el => this.box = el } >
            { children }
            { show && <div
                className={`ctx-menu ${operations.length === 0 ? 'f-dn' : ''}`}
                ref={ el => this.menu = el }
                style={{ left: x, top: menuTop, zIndex: 1006 }}
            >
                <ul className="ctx-menu-list" >
                    { operations.map((o, i) => {
                        return <li
                            onClick={ (e) => {
                                e.stopPropagation();
                                o.cb.call()
                            } }
                            className="ctx-list-li"
                            key={ `${id}-${i}` }
                        >
                            <a
                                href="javascript:void(0)"
                                className="ctx-list-a"
                            >
                                {o.txt}
                            </a>
                        </li>
                    }) }
                </ul>
            </div> }
            { show && <div className="mask"
                ref={ el => this.mask = el }
                style={{ position: 'absolute',
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    zIndex: 1005
                }}
            ></div> }
        </span>
    }
}
