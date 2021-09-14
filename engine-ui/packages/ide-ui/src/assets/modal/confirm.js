"use strict";

function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.default = confirm;

const React = _interopRequireWildcard(require("react"));

const ReactDOM = _interopRequireWildcard(require("react-dom"));

const _classnames = _interopRequireDefault(require("classnames"));

const _icon = _interopRequireDefault(require("antd/lib/icon"));

const _Modal = _interopRequireWildcard(require("antd/lib/modal/Modal"));

const _ActionButton = _interopRequireDefault(require("./ActionButton.js"));

const _locale = require("antd/lib/modal/locale");

const _warning = _interopRequireDefault(require("antd/lib/_util/warning"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

function _getRequireWildcardCache() { if (typeof WeakMap !== "function") return null; const cache = new WeakMap(); _getRequireWildcardCache = function _getRequireWildcardCache() { return cache; }; return cache; }

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } if (obj === null || _typeof(obj) !== "object" && typeof obj !== "function") { return { "default": obj }; } const cache = _getRequireWildcardCache(); if (cache && cache.has(obj)) { return cache.get(obj); } const newObj = {}; const hasPropertyDescriptor = Object.defineProperty && Object.getOwnPropertyDescriptor; for (const key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) { const desc = hasPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : null; if (desc && (desc.get || desc.set)) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } newObj.default = obj; if (cache) { cache.set(obj, newObj); } return newObj; }

function _extends() { _extends = Object.assign || function (target) { for (let i = 1; i < arguments.length; i++) { const source = arguments[i]; for (const key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

const IS_REACT_16 = !!ReactDOM.createPortal;

const ConfirmDialog = function ConfirmDialog(props) {
    const onCancel = props.onCancel;
    const onOk = props.onOk;
    const close = props.close;
    const zIndex = props.zIndex;
    const afterClose = props.afterClose;
    const visible = props.visible;
    const keyboard = props.keyboard;
    const centered = props.centered;
    const getContainer = props.getContainer || (() => document.querySelector('div[id="app"]'));
    const maskStyle = props.maskStyle;
    const okButtonProps = props.okButtonProps;
    const cancelButtonProps = props.cancelButtonProps;
    const _props$iconType = props.iconType;
    const iconType = _props$iconType === void 0 ? 'question-circle' : _props$iconType;
    (0, _warning.default)(!('iconType' in props), 'Modal', "The property 'iconType' is deprecated. Use the property 'icon' instead."); // 支持传入{ icon: null }来隐藏`Modal.confirm`默认的Icon

    const icon = props.icon === undefined ? iconType : props.icon;
    const okType = props.okType || 'primary';
    const prefixCls = props.prefixCls || 'ide-ui-modal';
    const contentPrefixCls = "".concat(prefixCls, "-confirm"); // 默认为 true，保持向下兼容

    const okCancel = 'okCancel' in props ? props.okCancel : true;
    const width = props.width || 416;
    const style = props.style || {};
    const mask = props.mask === undefined ? true : props.mask; // 默认为 false，保持旧版默认行为

    const maskClosable = props.maskClosable === undefined ? false : props.maskClosable;
    const runtimeLocale = (0, _locale.getConfirmLocale)();
    const okText = props.okText || (okCancel ? runtimeLocale.okText : runtimeLocale.justOkText);
    const cancelText = props.cancelText || runtimeLocale.cancelText;
    const autoFocusButton = props.autoFocusButton === null ? false : props.autoFocusButton || 'ok';
    const transitionName = props.transitionName || 'zoom';
    const maskTransitionName = props.maskTransitionName || 'fade';
    const classString = (0, _classnames.default)(contentPrefixCls, "".concat(contentPrefixCls, "-").concat(props.type), props.className);
    const cancelButton = okCancel && React.createElement(_ActionButton.default, {
        actionFn: onCancel,
        closeModal: close,
        autoFocus: autoFocusButton === 'cancel',
        buttonProps: Object.assign({ prefixCls:'ide-ui' }, cancelButtonProps)
    }, cancelText);
    const iconNode = typeof icon === 'string' ? React.createElement(_icon.default, {
        type: icon
    }) : icon;
    return React.createElement(_Modal.default, {
        prefixCls: prefixCls,
        className: classString,
        wrapClassName: (0, _classnames.default)(_defineProperty({}, "".concat(contentPrefixCls, "-centered"), !!props.centered)),
        onCancel: function onCancel() {
            return close({
                triggerCancel: true
            });
        },
        visible: visible,
        title: "",
        transitionName: transitionName,
        footer: "",
        maskTransitionName: maskTransitionName,
        mask: mask,
        maskClosable: maskClosable,
        maskStyle: maskStyle,
        style: style,
        width: width,
        zIndex: zIndex,
        afterClose: afterClose,
        keyboard: keyboard,
        centered: centered,
        getContainer: getContainer
    }, React.createElement("div", {
        className: "".concat(contentPrefixCls, "-body-wrapper")
    }, React.createElement("div", {
        className: "".concat(contentPrefixCls, "-body")
    }, iconNode, props.title === undefined ? null : React.createElement("span", {
        className: "".concat(contentPrefixCls, "-title")
    }, props.title), React.createElement("div", {
        className: "".concat(contentPrefixCls, "-content")
    }, props.content)), React.createElement("div", {
        className: "".concat(contentPrefixCls, "-btns")
    }, cancelButton, React.createElement(_ActionButton.default, {
        type: okType,
        actionFn: onOk,
        closeModal: close,
        autoFocus: autoFocusButton === 'ok',
        buttonProps: Object.assign({ prefixCls:'ide-ui' }, okButtonProps)
    }, okText))));
};

function confirm(config) {
    const div = document.createElement('div');
    document.body.appendChild(div); // eslint-disable-next-line no-use-before-define

    let currentConfig = _extends(_extends({}, config), {
        close: close,
        visible: true
    });

    function destroy() {
        const unmountResult = ReactDOM.unmountComponentAtNode(div);

        if (unmountResult && div.parentNode) {
            div.parentNode.removeChild(div);
        }

        for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
        }

        const triggerCancel = args.some(function (param) {
            return param && param.triggerCancel;
        });

        if (config.onCancel && triggerCancel) {
            config.onCancel.apply(config, args);
        }

        for (let i = 0; i < _Modal.destroyFns.length; i++) {
            const fn = _Modal.destroyFns[i]; // eslint-disable-next-line no-use-before-define

            if (fn === close) {
                _Modal.destroyFns.splice(i, 1);

                break;
            }
        }
    }

    function render(props) {
        ReactDOM.render(React.createElement(ConfirmDialog, props), div);
    }

    function close() {
        for (var _len2 = arguments.length, args = new Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
            args[_key2] = arguments[_key2];
        }

        currentConfig = _extends(_extends({}, currentConfig), {
            visible: false,
            afterClose: destroy.bind.apply(destroy, [this].concat(args))
        });

        if (IS_REACT_16) {
            render(currentConfig);
        } else {
            destroy.apply(void 0, args);
        }
    }

    function update(newConfig) {
        currentConfig = _extends(_extends({}, currentConfig), newConfig);
        render(currentConfig);
    }

    render(currentConfig);

    _Modal.destroyFns.push(close);

    return {
        destroy: close,
        update: update
    };
}
// # sourceMappingURL=confirm.js.map
