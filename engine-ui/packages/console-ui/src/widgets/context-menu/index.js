import React, { Component } from 'react'
import './style.scss'
/* eslint-disable */

export class MenuItem extends Component {
    render() {
        return (
            <li {...this.props}
                 className="context-list-li">
                <a className="context-list-a"
                    data-value={this.props.value}>
                    {this.props.children}
                </a>
            </li>
        )
    }
}

export class ContextMenu extends Component {

    constructor(props){
        super(props);
        this.toggleMenu = this.toggleMenu.bind(this)
        this.removeMenu = this.removeMenu.bind(this)
    }

    componentDidMount() {
        document.addEventListener('contextmenu', this.toggleMenu, false);
        document.addEventListener("click", this.removeMenu, false);
    }

    componentWillUnmount() {
        document.removeEventListener('contextmenu', this.toggleMenu, false);
        document.removeEventListener("click", this.removeMenu, false);
    }

    toggleMenu(evt) {
        const { targetClassName, onChange } = this.props
        const selfEle = this.selfEle
        if (!selfEle) return;
        const parent = this.findParent(evt.target, targetClassName);

        if (parent) {

            this.hideAll()

            let style = selfEle.style;
            style.display = "block";

            const pointerY = evt.clientY;
            const pointerX = evt.clientX;
            const viewHeight = document.body.offsetHeight; // 可视区高度
            const distanceToBottom = viewHeight - pointerY;
            const menuHeight = selfEle.offsetHeight;
            const menuTop = distanceToBottom > menuHeight ? pointerY : pointerY - menuHeight;
     
            style.cssText = `
                top: ${menuTop}px;
                left: ${pointerX}px;
                display: block;
            `
            if (onChange) {
                onChange(parent)
            }
            evt.preventDefault();
        }
    }

    hideAll() {
        const allEles = document.querySelectorAll('.context-menu')
        for (let i = 0 ; i < allEles.length; i++) {
            allEles[i].style.display = 'none';
        }
    }

    removeMenu(evt) {
        const { targetClassName } = this.props
        if (!this.selfEle) return
        const style = this.selfEle.style
        if (evt.which === 1 ) { // When mouse right click
            if (style.display !== 'block') return
            if (evt.target.nodeType !== 1) {
                 style.display = "none"; 
            } else {
                let parent = this.findParent(evt.target, targetClassName)
                if (!parent) { style.display = "none"; }
            }
        }
    }

    findParent(child, selector) {
        try {
            if (!selector || !child) return;
            selector = selector.toLowerCase();
            let node = child;
            while(node) {
                if (node.nodeType === 1) { // just hand dom element
                    const className = node.getAttribute('class');
                    if (className && className.includes(selector)) return node;
                }
                node = node.parentNode;
            }
        } catch(e) {
            throw new Error(e)
        }
        return null;
    }
    
    render() {
        return (
            <div ref={(e) => { this.selfEle = e } } className="context-menu" style={{ display: 'none' }}>
                <ul className="context-menu-list">
                    {this.props.children}
                </ul>
            </div>
        )
    }
}
/* eslint-disable */
