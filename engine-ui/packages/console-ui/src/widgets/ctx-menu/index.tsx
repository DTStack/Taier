import * as React from 'react';
import './style.scss';

export default class CtxMenu extends React.Component<any, any> {
    constructor (props: any) {
        super(props);

        this.showMenu = this.showMenu.bind(this);
        this.hideMenu = this.hideMenu.bind(this);
    }

    box: any;
    menu: any;
    mask: any;

    componentDidMount () {
        this.box.addEventListener('contextmenu', this.showMenu, false);
        document.addEventListener('click', this.hideMenu, false);
    }

    // eslint-disable-next-line
    componentWillUnmount () {
        this.box.removeEventListener('contextmenu', this.showMenu, false);
        document.removeEventListener('click', this.hideMenu, false);
    }

    hideMenu (e: any) {
        if (!this.menu) return;
        const style = this.menu.style;
        style.display = 'none';
    }

    findParent (child: any, selector: any) {
        try {
            if (!selector || !child) return;
            selector = selector.toLowerCase();
            let node = child;
            while (node) {
                if (node.nodeType === 1) {
                    const className = node.getAttribute('class');
                    if (className && className.includes(selector)) return node;
                }
                node = node.parentNode;
            }
        } catch (e) {
            throw new Error(e);
        }
        return null;
    }

    hideAll = () => {
        let allEles: any = document.querySelectorAll('.ctx-menu');
        for (let i = 0; i < allEles.length; i++) {
            allEles[i].style.display = 'none';
        }
    }

    viewHeight: number = document.body.offsetHeight || 0;

    showMenu (e: any) {
        e.preventDefault();
        const { ctxMenuWrapperClsName } = this.props;
        const menu = this.menu;
        if (!menu) return;
        const parent = this.findParent(e.target, ctxMenuWrapperClsName);
        if (parent) {
            this.hideAll();
            let style = menu.style;
            style.display = 'block';

            const pointerY = e.clientY;
            const pointerX = e.clientX;
            const distanceToBottom = this.viewHeight - pointerY;
            const menuHeight = menu.offsetHeight;
            const menuTop = distanceToBottom > menuHeight ? pointerY : pointerY - menuHeight;

            style.cssText = `
                top: ${menuTop}px;
                left: ${pointerX}px;
                display: block;
                z-index: 1006;
            `
        }
    }

    render () {
        const { children, operations, id } = this.props;

        return <span ref={ (el: any) => this.box = el } >
            { children }
            { <div
                className={`ctx-menu ${operations.length === 0 ? 'f-dn' : ''}`}
                ref={ (el: any) => this.menu = el }
                style={{ display: 'none' }}
            >
                <ul className="ctx-menu-list" >
                    { operations.map((o: any, i: any) => {
                        return <li
                            onClick={ (e: any) => {
                                e.stopPropagation();
                                this.hideAll();
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
            { <div className="mask"
                ref={ (el: any) => this.mask = el }
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
