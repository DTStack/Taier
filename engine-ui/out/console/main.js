(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else if(typeof exports === 'object')
		exports["DTConsoleApp"] = factory();
	else
		root["DTConsoleApp"] = factory();
})(window, function() {
return (window["webpackJsonp_console-ui"] = window["webpackJsonp_console-ui"] || []).push([[3],{

/***/ "+5i3":
/***/ (function(module, exports, __webpack_require__) {

// extracted by mini-css-extract-plugin

/***/ }),

/***/ "+CR5":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("yq1k");

__webpack_require__("2B1R");

__webpack_require__("+2oP");

__webpack_require__("sMBO");

__webpack_require__("qePV");

__webpack_require__("zKZe");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("T63A");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("rB9j");

__webpack_require__("JTJg");

__webpack_require__("EnZy");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _card = _interopRequireDefault(__webpack_require__("N9UN"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _tooltip = _interopRequireDefault(__webpack_require__("d1El"));

var _icon = _interopRequireDefault(__webpack_require__("Pbn2"));

var _checkbox = _interopRequireDefault(__webpack_require__("g4D/"));

var _upload = _interopRequireDefault(__webpack_require__("B8+X"));

var _extends2 = _interopRequireDefault(__webpack_require__("pVnL"));

var _radio = _interopRequireDefault(__webpack_require__("qPIi"));

var _breadcrumb = _interopRequireDefault(__webpack_require__("Y/VR"));

var _regenerator = _interopRequireDefault(__webpack_require__("o0o1"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

__webpack_require__("ls82");

var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__("yXPU"));

var _slicedToArray2 = _interopRequireDefault(__webpack_require__("J4zp"));

var _input = _interopRequireDefault(__webpack_require__("iJl9"));

var _select = _interopRequireDefault(__webpack_require__("FAat"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var _react = _interopRequireWildcard(__webpack_require__("q1tI"));

var _lodash = __webpack_require__("LvDl");

var _utils = _interopRequireDefault(__webpack_require__("j+Cx"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

var _consts = __webpack_require__("RzPm");

var _help = __webpack_require__("OHU0");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

var FormItem = _form["default"].Item;
var Option = _select["default"].Option;
var TextArea = _input["default"].TextArea;
var wrapperCol = {
  wrapperCol: {
    xs: {
      span: 24
    },
    sm: {
      span: 9
    }
  }
};

var AlarmRule = function AlarmRule(props) {
  var _props$location$state, _props$location$state2, _ref7, _getFieldValue$name, _getFieldValue;

  var _useState = (0, _react.useState)([]),
      _useState2 = (0, _slicedToArray2["default"])(_useState, 2),
      fileList = _useState2[0],
      setFileList = _useState2[1];

  var _props$form = props.form,
      getFieldDecorator = _props$form.getFieldDecorator,
      getFieldValue = _props$form.getFieldValue,
      validateFields = _props$form.validateFields,
      setFieldsValue = _props$form.setFieldsValue;
  var id = (_props$location$state = props.location.state) === null || _props$location$state === void 0 ? void 0 : _props$location$state.id;
  var ruleData = (_props$location$state2 = props.location.state) === null || _props$location$state2 === void 0 ? void 0 : _props$location$state2.ruleData;

  var isEmail = getFieldValue('alertGateType') === _consts.ALARM_TYPE.EMAIL;

  var getChannelModeOpts = function getChannelModeOpts() {
    var alertGateType = getFieldValue('alertGateType');

    switch (alertGateType) {
      case _consts.ALARM_TYPE.MSG:
        {
          return _consts.CHANNEL_MODE.sms.map(function (item, index) {
            return /*#__PURE__*/_react["default"].createElement(Option, {
              value: item.value,
              key: "".concat(index)
            }, item.title);
          });
        }

      case _consts.ALARM_TYPE.EMAIL:
        {
          return _consts.CHANNEL_MODE.mail.map(function (item, index) {
            return /*#__PURE__*/_react["default"].createElement(Option, {
              value: item.value,
              key: "".concat(index)
            }, item.title);
          });
        }

      case _consts.ALARM_TYPE.DING:
        {
          return _consts.CHANNEL_MODE.dingTalk.map(function (item, index) {
            return /*#__PURE__*/_react["default"].createElement(Option, {
              value: item.value,
              key: "".concat(index)
            }, item.title);
          });
        }

      default:
        return [];
    }
  };

  var getChannelConfText = function getChannelConfText() {
    var alertGateCode = getFieldValue('alertGateCode') || '';
    var text = '';

    if (alertGateCode === _consts.CHANNEL_MODE_VALUE.SMS_YP) {
      text = _consts.CHANNEL_CONF_TEXT.SMS_YP;
    } else if (alertGateCode === _consts.CHANNEL_MODE_VALUE.MAIL_DT) {
      text = _consts.CHANNEL_CONF_TEXT.MAIL_DT;
    } else if (alertGateCode.includes('jar')) {
      text = _consts.CHANNEL_CONF_TEXT.JAR;
    } else if (alertGateCode.includes('api')) {
      text = _consts.CHANNEL_CONF_TEXT.API;
    } else {
      text = _consts.CHANNEL_CONF_TEXT.CUSTOM;
    }

    return text;
  };

  var testAlarm = function testAlarm() {
    validateFields( /*#__PURE__*/function () {
      var _ref = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee(err, values) {
        var _ref2, _values$file$file, _values$file, testKey, testValue, res;

        return _regenerator["default"].wrap(function _callee$(_context) {
          while (1) {
            switch (_context.prev = _context.next) {
              case 0:
                if (err) {
                  _context.next = 7;
                  break;
                }

                testKey = (0, _help.textAlertKey)(values.alertGateType);
                testValue = values.alertGateType !== _consts.ALARM_TYPE.CUSTOM ? values[testKey].split(',') : '';
                _context.next = 5;
                return _console["default"].testAlert(Object.assign({}, values, (0, _defineProperty2["default"])({
                  id: id || '',
                  filePath: (ruleData === null || ruleData === void 0 ? void 0 : ruleData.filePath) || '',
                  isDefault: values.isDefault ? 1 : 0,
                  file: (_ref2 = (_values$file$file = values === null || values === void 0 ? void 0 : (_values$file = values.file) === null || _values$file === void 0 ? void 0 : _values$file.file) !== null && _values$file$file !== void 0 ? _values$file$file : values === null || values === void 0 ? void 0 : values.file) !== null && _ref2 !== void 0 ? _ref2 : ''
                }, testKey, testValue)));

              case 5:
                res = _context.sent;

                if (res.code === 1) {
                  _message2["default"].success('消息已发送');
                }

              case 7:
              case "end":
                return _context.stop();
            }
          }
        }, _callee);
      }));

      return function (_x, _x2) {
        return _ref.apply(this, arguments);
      };
    }());
  };

  var goBack = function goBack() {
    props.router.push('/console/alarmChannel');
  };

  var handleSubmit = function handleSubmit() {
    validateFields( /*#__PURE__*/function () {
      var _ref3 = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee2(err, values) {
        var _ref4, _values$file$file2, _values$file2, res, msg;

        return _regenerator["default"].wrap(function _callee2$(_context2) {
          while (1) {
            switch (_context2.prev = _context2.next) {
              case 0:
                if (err) {
                  _context2.next = 5;
                  break;
                }

                _context2.next = 3;
                return _console["default"].addOrUpdateAlarmRule(Object.assign({}, values, {
                  id: id || '',
                  isDefault: values.isDefault ? 1 : 0,
                  file: (_ref4 = (_values$file$file2 = values === null || values === void 0 ? void 0 : (_values$file2 = values.file) === null || _values$file2 === void 0 ? void 0 : _values$file2.file) !== null && _values$file$file2 !== void 0 ? _values$file$file2 : values === null || values === void 0 ? void 0 : values.file) !== null && _ref4 !== void 0 ? _ref4 : ''
                }));

              case 3:
                res = _context2.sent;

                if (res.code === 1) {
                  msg = id ? '编辑成功' : '新增成功';

                  _message2["default"].success(msg);

                  goBack();
                }

              case 5:
              case "end":
                return _context2.stop();
            }
          }
        }, _callee2);
      }));

      return function (_x3, _x4) {
        return _ref3.apply(this, arguments);
      };
    }());
  };

  var validAlertKey = function validAlertKey(rule, value, callBack) {
    if (value && !isEmail) {
      if (!_consts.NUM_COMMA.test(value)) callBack('请输入正确格式的手机号码');
      var phone = value.split(',');
      phone.forEach(function (p) {
        if (!_consts.PHONE_REG.test(p) && p.length) callBack('请输入正确格式的手机号码');
      });
    } else if (value && isEmail) {
      if (!_consts.EMAIL_COMMA.test(value)) callBack('请输入正确格式的邮箱账号');
      var email = value.split(',');
      email.forEach(function (e) {
        if (!_consts.EMAIL_REG.test(e) && e.length) callBack('请输入正确格式的邮箱账号');
      });
    }

    callBack();
  };

  var validataFileType = function validataFileType(file, messages, callBack) {
    var reg = /\.(jar)$/;
    var name = file.name;

    if (name && !reg.test(name.toLocaleLowerCase())) {
      _message2["default"].warning(messages);

      return;
    }

    callBack && callBack();
  };

  var uploadProp = {
    name: 'file',
    accept: '.jar',
    beforeUpload: function beforeUpload(file) {
      console.log(file);
      validataFileType(file, 'jar文件只能是.jar文件', function () {
        var fileList = [file];
        fileList = fileList.slice(-1);
        setFileList(fileList);
      });
      return false;
    },
    onRemove: function onRemove() {
      setFileList([]);
      setFieldsValue({
        file: undefined
      });
    },
    fileList: fileList
  };
  var uploadConfigProp = {
    name: 'configFile',
    accept: '.jar',
    beforeUpload: function beforeUpload(file) {
      validataFileType(file, '配置文件只能是.jar文件', function () {
        setFieldsValue((0, _defineProperty2["default"])({}, "file", file));
      });
      return false;
    },
    fileList: []
  };
  var testText = isEmail ? '邮箱' : '手机号码';
  var alertKey = (0, _help.textAlertKey)(getFieldValue('alertGateType'));

  var isCreate = _utils["default"].getParameterByName('isCreate');

  var alertGateType = getFieldValue('alertGateType');
  var alertGateCode = getFieldValue('alertGateCode');

  var handleAlertGateType = function handleAlertGateType() {
    getFieldValue('file') && setFieldsValue((0, _defineProperty2["default"])({}, "file", ''));
    setFileList([]);
  };

  return /*#__PURE__*/_react["default"].createElement("div", {
    className: "alarm-rule__wrapper"
  }, /*#__PURE__*/_react["default"].createElement(_breadcrumb["default"], null, /*#__PURE__*/_react["default"].createElement(_breadcrumb["default"].Item, null, " ", /*#__PURE__*/_react["default"].createElement("a", {
    onClick: function onClick() {
      props.router.push('/console/alarmChannel');
    }
  }, "\u544A\u8B66\u901A\u9053")), /*#__PURE__*/_react["default"].createElement(_breadcrumb["default"].Item, null, "".concat(isCreate ? '新增告警通道' : "".concat(ruleData === null || ruleData === void 0 ? void 0 : ruleData.alertGateName)))), /*#__PURE__*/_react["default"].createElement(_card["default"], {
    bordered: false
  }, /*#__PURE__*/_react["default"].createElement(_form["default"], null, /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({}, _objectSpread(_objectSpread({}, _consts.formItemCenterLayout), wrapperCol), {
    label: "\u901A\u9053\u7C7B\u578B"
  }), getFieldDecorator('alertGateType', {
    rules: [{
      required: true,
      message: '请选择通道类型'
    }],
    initialValue: _consts.ALARM_TYPE.MSG
  })( /*#__PURE__*/_react["default"].createElement(_radio["default"].Group, {
    name: "channelMode",
    onChange: handleAlertGateType,
    disabled: !isCreate
  }, Object.entries(_consts.ALARM_TYPE_TEXT).map(function (_ref5) {
    var _ref6 = (0, _slicedToArray2["default"])(_ref5, 2),
        key = _ref6[0],
        value = _ref6[1];

    return /*#__PURE__*/_react["default"].createElement(_radio["default"], {
      key: key,
      value: Number(key)
    }, value);
  })))), (0, _help.showAlertGateCode)(alertGateType) && /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({}, _consts.formItemCenterLayout, {
    label: "\u901A\u9053\u6A21\u5F0F"
  }), getFieldDecorator('alertGateCode', {
    rules: [{
      required: true,
      message: '请选择通道模式'
    }]
  })( /*#__PURE__*/_react["default"].createElement(_select["default"], null, getChannelModeOpts()))), (alertGateCode === null || alertGateCode === void 0 ? void 0 : alertGateCode.includes('jar')) && !(0, _help.showConfigFile)(alertGateType) ? /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({}, _consts.formItemCenterLayout, {
    label: "\u4E0A\u4F20\u6587\u4EF6"
  }), getFieldDecorator('file', {
    rules: [{
      required: false
    }]
  })( /*#__PURE__*/_react["default"].createElement(_upload["default"], uploadProp, /*#__PURE__*/_react["default"].createElement("a", {
    href: "javascript:;"
  }, "\u9009\u62E9jar\u6587\u4EF6"))), (ruleData === null || ruleData === void 0 ? void 0 : ruleData.filePath) && !getFieldValue('file') && /*#__PURE__*/_react["default"].createElement("span", null, ruleData === null || ruleData === void 0 ? void 0 : ruleData.filePath)) : null, /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({}, _consts.formItemCenterLayout, {
    label: "\u901A\u9053\u6807\u8BC6"
  }), getFieldDecorator('alertGateSource', {
    rules: [{
      required: true,
      message: '请输入通道标识'
    }, {
      max: 32,
      message: '通道标识不超过32个字符'
    }, {
      pattern: /^[A-Za-z0-9_]+$/,
      message: '只支持英文、数字、下划线'
    }]
  })( /*#__PURE__*/_react["default"].createElement(_input["default"], {
    disabled: !!id
  }))), /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({}, _consts.formItemCenterLayout, {
    label: "\u901A\u9053\u540D\u79F0"
  }), getFieldDecorator('alertGateName', {
    rules: [{
      required: true,
      message: '请输入通道名称'
    }, {
      max: 32,
      message: '通道名称不超过32个字符'
    }, {
      pattern: /^[^\s]*$/,
      message: '不允许填写空格'
    }]
  })( /*#__PURE__*/_react["default"].createElement(_input["default"], {
    placeholder: "\u8BF7\u8F93\u5165\u901A\u9053\u540D\u79F0\uFF0C\u4E0D\u8D85\u8FC732\u4E2A\u5B57\u7B26"
  }))), (0, _help.showIsDefault)(alertGateType) && /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({}, _consts.formItemCenterLayout, {
    label: ' ',
    colon: false
  }), getFieldDecorator('isDefault', {
    valuePropName: 'checked',
    initialValue: false
  })( /*#__PURE__*/_react["default"].createElement(_checkbox["default"], null, "\u8BBE\u7F6E\u4E3A\u9ED8\u8BA4\u901A\u9053")), /*#__PURE__*/_react["default"].createElement(_tooltip["default"], {
    title: "\u5404\u5E94\u7528\u7684\u544A\u8B66\u8D70\u9ED8\u8BA4\u901A\u9053\uFF0C\u6545\u9ED8\u8BA4\u901A\u9053\u9700\u8C28\u614E\u8BBE\u7F6E\uFF0C\u652F\u6301\u7528\u6237\u540E\u7EED\u66F4\u6539\uFF0C\u6BCF\u4E2A\u901A\u9053\u7C7B\u6709\u4E14\u4EC5\u6709\u4E00\u4E2A\u9ED8\u8BA4\u901A\u9053\u3002",
    arrowPointAtCenter: true
  }, /*#__PURE__*/_react["default"].createElement(_icon["default"], {
    type: "info-circle"
  }))), (0, _help.showConfigFile)(alertGateType) && /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({}, _objectSpread(_objectSpread({}, _consts.formItemCenterLayout), wrapperCol), {
    label: "\u914D\u7F6E\u6587\u4EF6"
  }), getFieldDecorator('file', {
    rules: [{
      required: true,
      message: '文件不可为空！'
    }],
    initialValue: ruleData === null || ruleData === void 0 ? void 0 : ruleData.filePath
  })( /*#__PURE__*/_react["default"].createElement("div", null)), /*#__PURE__*/_react["default"].createElement("div", {
    className: "c-alarmRule__config"
  }, /*#__PURE__*/_react["default"].createElement(_upload["default"], uploadConfigProp, /*#__PURE__*/_react["default"].createElement(_button["default"], {
    style: {
      width: 164
    },
    icon: "upload"
  }, "\u70B9\u51FB\u4E0A\u4F20")), /*#__PURE__*/_react["default"].createElement("span", {
    className: "config-desc"
  }, "\u4EC5\u652F\u6301jar\u683C\u5F0F\uFF0C", /*#__PURE__*/_react["default"].createElement("a", {
    href: "/api/console/service/alert/downloadJar?alertGateType=".concat(alertGateType)
  }, "\u67E5\u770B\u914D\u7F6E\u6587\u4EF6\u8BF4\u660E"))), getFieldValue('file') && /*#__PURE__*/_react["default"].createElement("span", {
    className: "config-file"
  }, /*#__PURE__*/_react["default"].createElement(_icon["default"], {
    type: "paper-clip"
  }), (_ref7 = (_getFieldValue$name = (_getFieldValue = getFieldValue('file')) === null || _getFieldValue === void 0 ? void 0 : _getFieldValue.name) !== null && _getFieldValue$name !== void 0 ? _getFieldValue$name : getFieldValue('file')) !== null && _ref7 !== void 0 ? _ref7 : '', /*#__PURE__*/_react["default"].createElement(_icon["default"], {
    type: "delete",
    onClick: function onClick() {
      setFieldsValue((0, _defineProperty2["default"])({}, "file", ''));
    }
  }))), (0, _help.showAlertGateJson)(alertGateCode, alertGateType) ? /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({}, _consts.formItemCenterLayout, {
    label: "\u901A\u9053\u914D\u7F6E\u4FE1\u606F"
  }), getFieldDecorator('alertGateJson', {
    rules: [{
      required: true,
      message: '请输入通道配置信息'
    }]
  })( /*#__PURE__*/_react["default"].createElement(TextArea, {
    placeholder: getChannelConfText(),
    rows: 6
  }))) : null, (0, _help.showAlertTemplete)(alertGateType, alertGateCode) ? /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({}, _consts.formItemCenterLayout, {
    label: "\u901A\u77E5\u6D88\u606F\u6A21\u7248"
  }), getFieldDecorator('alertTemplate', {
    rules: [{
      required: true,
      message: '请输入通知消息模版'
    }]
  })( /*#__PURE__*/_react["default"].createElement(TextArea, {
    placeholder: "\u8BF7\u6309\u7167\u6B64\u683C\u5F0F\u586B\u5199\uFF1A<\u4F01\u4E1A\u540D\u79F0>$" + "{message}\uFF0C\u8BF7\u53CA\u65F6\u5904\u7406",
    rows: 4
  }))) : null, (0, _help.canTestAlarm)(alertGateType) ? /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({}, _consts.formItemCenterLayout, {
    label: " ",
    colon: false
  }), getFieldDecorator("".concat(alertKey), {
    rules: [{
      required: false
    }, {
      validator: validAlertKey
    }],
    initialValue: ''
  })(alertGateType !== _consts.ALARM_TYPE.CUSTOM ? /*#__PURE__*/_react["default"].createElement(_input["default"], {
    placeholder: "\u8F93\u5165".concat(testText, "\u6D4B\u8BD5\u53F7\u7801\uFF0C\u591A\u4E2A").concat(testText, "\u7528\u82F1\u6587\u9017\u53F7\u9694\u5F00"),
    addonAfter: /*#__PURE__*/_react["default"].createElement("span", {
      onClick: function onClick() {
        testAlarm();
      }
    }, "\u70B9\u51FB\u6D4B\u8BD5")
  }) : /*#__PURE__*/_react["default"].createElement(_button["default"], {
    ghost: true,
    onClick: function onClick() {
      testAlarm();
    }
  }, "\u6D88\u606F\u53D1\u9001\u6D4B\u8BD5"))) : null), /*#__PURE__*/_react["default"].createElement("footer", null, /*#__PURE__*/_react["default"].createElement(_button["default"], {
    onClick: function onClick() {
      goBack();
    }
  }, "\u53D6\u6D88"), /*#__PURE__*/_react["default"].createElement(_button["default"], {
    type: "primary",
    onClick: function onClick() {
      handleSubmit();
    }
  }, "\u786E\u5B9A"))));
};

var _default = _form["default"].create({
  onValuesChange: function onValuesChange(props, fields) {
    if (fields.hasOwnProperty('alertGateType')) {
      props.form.setFieldsValue({
        alertGateCode: undefined,
        alertGateJson: undefined
      });
    }
  },
  mapPropsToFields: function mapPropsToFields(props) {
    var _props$location$state3;

    var ruleData = (_props$location$state3 = props.location.state) === null || _props$location$state3 === void 0 ? void 0 : _props$location$state3.ruleData;
    if (!ruleData) return;
    var keyValMap = {};
    var newRuleData = (0, _lodash.cloneDeep)(ruleData);

    for (var _i = 0, _Object$entries = Object.entries(newRuleData); _i < _Object$entries.length; _i++) {
      var _Object$entries$_i = (0, _slicedToArray2["default"])(_Object$entries[_i], 2),
          key = _Object$entries$_i[0],
          value = _Object$entries$_i[1];

      keyValMap = Object.assign({}, keyValMap, (0, _defineProperty2["default"])({}, key, _form["default"].createFormField({
        value: value
      })));
    }

    console.log('keyValMap', keyValMap);
    return keyValMap;
  }
})(AlarmRule);

exports["default"] = _default;

/***/ }),

/***/ "06UE":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("yXV3");

__webpack_require__("oVuX");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _const = __webpack_require__("j1Tt");

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var ModifyCompsModal = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(ModifyCompsModal, _React$Component);

  var _super = _createSuper(ModifyCompsModal);

  function ModifyCompsModal() {
    (0, _classCallCheck2["default"])(this, ModifyCompsModal);
    return _super.apply(this, arguments);
  }

  (0, _createClass2["default"])(ModifyCompsModal, [{
    key: "render",
    value: function render() {
      var _this$props = this.props,
          _onOk = _this$props.onOk,
          _onCancel = _this$props.onCancel,
          deleteComps = _this$props.deleteComps,
          addComps = _this$props.addComps,
          visible = _this$props.visible;
      var compsName = deleteComps.map(function (code) {
        return _const.COMPONENT_CONFIG_NAME[code];
      });
      var isRadio = [_const.COMPONENT_TYPE_VALUE.YARN, _const.COMPONENT_TYPE_VALUE.KUBERNETES].indexOf(addComps[0]) > -1;
      return /*#__PURE__*/React.createElement(_modal["default"], {
        title: "\u4FEE\u6539\u7EC4\u4EF6\u914D\u7F6E",
        onOk: function onOk() {
          return _onOk();
        },
        onCancel: function onCancel() {
          return _onCancel();
        },
        visible: visible,
        className: "c-clusterManage__modal"
      }, isRadio ? /*#__PURE__*/React.createElement("span", null, "\u5207\u6362\u5230 ", _const.COMPONENT_CONFIG_NAME[addComps[0]], " \u540E ", compsName[0], " \u7684\u914D\u7F6E\u4FE1\u606F\u5C06\u4E22\u5931\uFF0C\u786E\u8BA4\u5207\u6362\u5230 ", _const.COMPONENT_CONFIG_NAME[addComps[0]], "\uFF1F") : /*#__PURE__*/React.createElement("span", null, "\u5220\u9664 ", compsName.join('、'), " \u7EC4\u4EF6\u540E\u76F8\u5E94\u914D\u7F6E\u4FE1\u606F\u5C06\u4E22\u5931\uFF0C\u786E\u5B9A\u5220\u9664 ", compsName.join('、'), " \u7EC4\u4EF6\uFF1F"));
    }
  }]);
  return ModifyCompsModal;
}(React.Component);

exports["default"] = ModifyCompsModal;

/***/ }),

/***/ "2Cp/":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = _default;

var _consoleActions = __webpack_require__("o4I+");

var _lodash = __webpack_require__("LvDl");

var defaultState = {
  tenantList: []
};

function _default() {
  var state = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : defaultState;
  var action = arguments.length > 1 ? arguments[1] : undefined;

  switch (action.type) {
    case _consoleActions.userActions.SET_TENANT_LIST:
      {
        var list = action.data;
        var newState = (0, _lodash.cloneDeep)(state);
        newState.tenantList = list;
        return newState;
      }

    default:
      return state;
  }
}

/***/ }),

/***/ "6aLI":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("wLYn");

__webpack_require__("zKZe");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("cDke");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("4l63");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _card = _interopRequireDefault(__webpack_require__("N9UN"));

var _table = _interopRequireDefault(__webpack_require__("DtFj"));

var _icon = _interopRequireDefault(__webpack_require__("Pbn2"));

var _toConsumableArray2 = _interopRequireDefault(__webpack_require__("RIqP"));

var _row = _interopRequireDefault(__webpack_require__("9xET"));

var _col = _interopRequireDefault(__webpack_require__("ZPTe"));

var _dropdown = _interopRequireDefault(__webpack_require__("ZvzK"));

var _checkbox = _interopRequireDefault(__webpack_require__("g4D/"));

var _menu = _interopRequireDefault(__webpack_require__("Jv8k"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _taggedTemplateLiteral2 = _interopRequireDefault(__webpack_require__("VkAN"));

var _radio = _interopRequireDefault(__webpack_require__("qPIi"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _lodash = __webpack_require__("LvDl");

var _utils = _interopRequireDefault(__webpack_require__("j+Cx"));

var _styledComponents = _interopRequireDefault(__webpack_require__("9ObM"));

__webpack_require__("+5i3");

var _clusterFunc = __webpack_require__("IiER");

var _viewDetail = _interopRequireDefault(__webpack_require__("Ej2X"));

var _killTask = _interopRequireDefault(__webpack_require__("BjRx"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

var _goBack = _interopRequireDefault(__webpack_require__("V2yy"));

var _killAllTask = _interopRequireDefault(__webpack_require__("L56I"));

var _consts = __webpack_require__("RzPm");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

function _templateObject() {
  var data = (0, _taggedTemplateLiteral2["default"])(["\n    margin-left: 24px;\n"]);

  _templateObject = function _templateObject() {
    return data;
  };

  return data;
}

var PAGE_SIZE = 10;
var RadioGroup = _radio["default"].Group;
var RadioButton = _radio["default"].Button;

var HeaderColTxt = _styledComponents["default"].span(_templateObject());

var getURLParam = _utils["default"].getParameterByName;

var TaskDetail = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(TaskDetail, _React$Component);

  var _super = _createSuper(TaskDetail);

  function TaskDetail() {
    var _this;

    (0, _classCallCheck2["default"])(this, TaskDetail);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      dataSource: [],
      table: {
        pageIndex: 1,
        total: 0,
        loading: false
      },
      jobName: '',
      clusterName: undefined,
      node: undefined,
      jobResource: undefined,
      // 实例资源
      engineType: undefined,
      // 查看详情
      isShowViewDetail: false,
      // 查看任务参数
      isShowTaskParams: false,
      resource: {},
      // modal 所要操作的 record
      // 杀任务
      // 多个任务id
      killIds: [],
      isShowKill: false,
      killResource: {},
      // 单选框值
      radioValue: null,
      // 更多任务列表记录值
      selectedRowKeys: [],
      isShowAllKill: false,
      isKillAllTasks: false,
      // 是否杀死全部任务
      killTaskInfo: []
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "getDetailTaskList", function () {
      var _this$state = _this.state,
          node = _this$state.node,
          jobResource = _this$state.jobResource,
          radioValue = _this$state.radioValue;
      var table = _this.state.table;
      var pageIndex = table.pageIndex;

      _this.setState({
        selectedRowKeys: [],
        killTaskInfo: []
      });

      if (jobResource) {
        _this.setState({
          table: _objectSpread(_objectSpread({}, table), {}, {
            loading: true
          })
        });

        _console["default"].getViewDetail({
          nodeAddress: node,
          pageSize: PAGE_SIZE,
          currentPage: pageIndex,
          stage: radioValue,
          jobResource: jobResource
        }).then(function (res) {
          if (res.code == 1) {
            _this.setState({
              dataSource: (0, _lodash.get)(res, 'data.data', []),
              table: _objectSpread(_objectSpread({}, table), {}, {
                loading: false,
                total: (0, _lodash.get)(res, 'data.totalCount', 0)
              })
            });
          } else {
            _this.setState({
              table: _objectSpread(_objectSpread({}, table), {}, {
                loading: false
              })
            });
          }
        });
      }
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onTableChange", function (pagination, filters, sorter) {
      var table = Object.assign(_this.state.table, {
        pageIndex: pagination.current
      });

      _this.setState({
        table: table,
        selectedRowKeys: [],
        killTaskInfo: []
      }, function () {
        _this.getDetailTaskList();
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleKillSelect", function () {
      var selected = _this.state.selectedRowKeys;

      if (!selected || selected.length <= 0) {
        _message2["default"].error('您没有选择任何任务！');

        return false;
      }

      _this.setState({
        isKillAllTasks: false,
        isShowAllKill: true
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onCheckAllChange", function (e) {
      var selectedRowKeys = [];
      var killTaskInfo = [];

      if (e.target.checked) {
        selectedRowKeys = _this.state.dataSource.map(function (item) {
          return item.jobId;
        });
        killTaskInfo = _this.state.dataSource.map(function (item) {
          var jobId = item.jobId,
              jobType = item.jobType,
              engineType = item.engineType,
              clusterName = item.clusterName;
          return {
            jobId: jobId,
            jobType: jobType,
            engineType: engineType,
            clusterName: clusterName
          };
        });
      }

      _this.setState({
        selectedRowKeys: selectedRowKeys,
        checkAll: e.target.checked,
        killTaskInfo: killTaskInfo
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleKillAll", function (e) {
      _this.setState({
        isShowAllKill: true,
        isKillAllTasks: true
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "tableFooter", function (currentPageData) {
      var _this$state2 = _this.state,
          selectedRowKeys = _this$state2.selectedRowKeys,
          dataSource = _this$state2.dataSource;
      var indeterminate = !!selectedRowKeys.length && selectedRowKeys.length < dataSource.length;
      var checked = !!dataSource.length && selectedRowKeys.length === dataSource.length;
      var menu = /*#__PURE__*/React.createElement(_menu["default"], {
        onClick: _this.handleKillAll
      }, /*#__PURE__*/React.createElement(_menu["default"].Item, {
        key: "1",
        style: {
          width: 118
        }
      }, "\u6740\u6B7B\u5168\u90E8\u4EFB\u52A1"));
      return /*#__PURE__*/React.createElement(_row["default"], {
        className: "table-footer"
      }, /*#__PURE__*/React.createElement(_col["default"], null, /*#__PURE__*/React.createElement(_checkbox["default"], {
        style: {
          padding: '0 16px 0 0'
        },
        indeterminate: indeterminate,
        checked: checked,
        onChange: _this.onCheckAllChange
      }, "\u5168\u9009"), /*#__PURE__*/React.createElement(_dropdown["default"].Button, {
        style: {
          padding: '0 16px 0 20px'
        },
        size: "small",
        trigger: ['click'],
        onClick: _this.handleKillSelect,
        type: "primary",
        overlay: menu
      }, "\u6740\u6B7B\u9009\u4E2D\u4EFB\u52A1")));
    });
    return _this;
  }

  (0, _createClass2["default"])(TaskDetail, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      var _this2 = this;

      this.setState({
        node: getURLParam('node'),
        clusterName: getURLParam('clusterName'),
        engineType: getURLParam('engineType'),
        jobResource: getURLParam('jobResource'),
        radioValue: getURLParam('jobStage')
      }, function () {
        _this2.getDetailTaskList();
      });
    } // 获取详细任务

  }, {
    key: "changeJobPriority",
    // 请求置顶调整接口
    value: function changeJobPriority(record) {
      // 获取集群
      var _this$state3 = this.state,
          jobResource = _this$state3.jobResource,
          radioValue = _this$state3.radioValue;
      var msg = parseInt(radioValue, 10) === _consts.JobStage.Saved ? '插入队列头成功' : '置顶成功';
      return _console["default"].stickJob({
        jobId: record.jobId,
        jobResource: jobResource
      }).then(function (res) {
        if (res.code == 1) {
          _message2["default"].success(msg);

          return true;
        }
      });
    } // 获取分页信息

  }, {
    key: "getPagination",
    value: function getPagination() {
      var _this$state$table = this.state.table,
          pageIndex = _this$state$table.pageIndex,
          total = _this$state$table.total;
      return {
        current: pageIndex,
        pageSize: PAGE_SIZE,
        total: total
      };
    } // 表格换页

  }, {
    key: "viewDetails",
    // 查看详情
    value: function viewDetails(record) {
      this.setState({
        isShowViewDetail: true,
        resource: record
      });
    }
  }, {
    key: "showTaskParams",
    value: function showTaskParams(record) {
      var taskParams = (0, _lodash.get)(JSON.parse(record.jobInfo), 'taskParams', '');
      this.setState({
        isShowTaskParams: true,
        resource: taskParams
      });
    }
  }, {
    key: "handleCloseViewModal",
    value: function handleCloseViewModal() {
      this.setState({
        isShowViewDetail: false,
        isShowTaskParams: false,
        resource: null
      });
    } // 杀任务

  }, {
    key: "killTask",
    value: function killTask(record) {
      this.setState({
        isShowKill: true,
        killResource: record
      });
    }
  }, {
    key: "handleCloseKill",
    value: function handleCloseKill() {
      this.setState({
        isShowKill: false,
        isShowAllKill: false
      });
    } // kill

  }, {
    key: "killSuccess",
    value: function killSuccess(killId) {
      this.setState({
        killIds: (0, _toConsumableArray2["default"])(this.state.killIds).concat(killId)
      });
    }
  }, {
    key: "autoRefresh",
    value: function autoRefresh() {
      this.getDetailTaskList();
    } // 置顶

  }, {
    key: "stickTask",
    value: function stickTask(record) {
      var _this3 = this;

      this.changeJobPriority(record).then(function (isSuccess) {
        if (isSuccess) {
          _this3.getDetailTaskList();
        }
      });
    }
  }, {
    key: "changeRadioValue",
    // 改变单选框值
    value: function changeRadioValue(e) {
      var table = this.state.table;
      this.setState({
        selectedRowKeys: [],
        killTaskInfo: [],
        table: _objectSpread(_objectSpread({}, table), {}, {
          total: 0,
          pageIndex: 1
        }),
        radioValue: e.target.value
      }, this.getDetailTaskList);
    }
  }, {
    key: "initTableColumns",
    value: function initTableColumns() {
      var _this4 = this;

      var table = this.state.table;
      return [{
        title: '序号',
        dataIndex: 'id',
        width: '80px',
        render: function render(text, record, index) {
          return PAGE_SIZE * (table.pageIndex - 1) + (index + 1);
        }
      }, {
        title: '任务名称',
        dataIndex: 'jobName',
        render: function render(text, record) {
          return record.jobName;
        },
        width: '350px'
      }, {
        title: '状态',
        dataIndex: 'status',
        render: function render(text, record) {
          return (0, _clusterFunc.displayTaskStatus)(text);
        }
      }, {
        title: '节点',
        dataIndex: 'nodeAddress'
      }, {
        title: '已等待',
        dataIndex: 'waitTime',
        render: function render(text, record) {
          return record.waitTime;
        }
      }, {
        title: '提交时间',
        dataIndex: 'generateTime',
        render: function render(text) {
          return _utils["default"].formatDateTime(text);
        }
      }, {
        title: '租户',
        dataIndex: 'tenantName'
      }, {
        title: '操作',
        dataIndex: 'deal',
        render: function render(text, record, index) {
          var isSaved = record.stage === _consts.JobStage.Saved;
          var isQueueing = record.stage === _consts.JobStage.Queueing;
          var stickTxt = isQueueing ? '置顶' : isSaved ? '插入队列头' : null;
          return /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("a", {
            onClick: _this4.viewDetails.bind(_this4, record)
          }, "\u67E5\u770B\u8BE6\u60C5"), /*#__PURE__*/React.createElement("span", {
            className: "ant-divider"
          }), /*#__PURE__*/React.createElement("a", {
            onClick: _this4.killTask.bind(_this4, record)
          }, "\u6740\u4EFB\u52A1"), stickTxt ? /*#__PURE__*/React.createElement("span", null, /*#__PURE__*/React.createElement("span", {
            className: "ant-divider"
          }), /*#__PURE__*/React.createElement("a", {
            onClick: _this4.stickTask.bind(_this4, record, index)
          }, stickTxt)) : null, /*#__PURE__*/React.createElement("span", {
            className: "ant-divider"
          }), /*#__PURE__*/React.createElement("a", {
            onClick: _this4.showTaskParams.bind(_this4, record)
          }, "\u4EFB\u52A1\u53C2\u6570"));
        }
      }];
    }
  }, {
    key: "render",
    value: function render() {
      var _this5 = this;

      var columns = this.initTableColumns();
      var _this$state4 = this.state,
          killResource = _this$state4.killResource,
          resource = _this$state4.resource,
          node = _this$state4.node,
          isKillAllTasks = _this$state4.isKillAllTasks,
          isShowTaskParams = _this$state4.isShowTaskParams,
          dataSource = _this$state4.dataSource,
          table = _this$state4.table,
          selectedRowKeys = _this$state4.selectedRowKeys,
          killTaskInfo = _this$state4.killTaskInfo,
          radioValue = _this$state4.radioValue,
          isShowViewDetail = _this$state4.isShowViewDetail,
          isShowKill = _this$state4.isShowKill,
          isShowAllKill = _this$state4.isShowAllKill,
          clusterName = _this$state4.clusterName,
          jobResource = _this$state4.jobResource;
      var total = table.total,
          loading = table.loading;
      var rowSelection = {
        onChange: function onChange(selectedRowKeys, selectedRows) {
          _this5.setState({
            selectedRowKeys: selectedRowKeys,
            killTaskInfo: selectedRows.map(function (item) {
              var jobId = item.jobId,
                  jobType = item.jobType,
                  engineType = item.engineType;
              return {
                jobId: jobId,
                jobType: jobType,
                engineType: engineType
              };
            })
          });
        },
        selectedRowKeys: selectedRowKeys
      };
      var cardTitle = /*#__PURE__*/React.createElement("div", {
        style: {
          fontSize: '12px',
          color: '#333333',
          fontWeight: 'normal'
        }
      }, /*#__PURE__*/React.createElement("span", null, "\u603B\u4EFB\u52A1\u6570\uFF1A", table.total || 0), /*#__PURE__*/React.createElement(HeaderColTxt, null, "\u96C6\u7FA4\uFF1A", clusterName), /*#__PURE__*/React.createElement(HeaderColTxt, null, "\u8282\u70B9\uFF1A", node || '-'), /*#__PURE__*/React.createElement(HeaderColTxt, null, "\u8BA1\u7B97\u7C7B\u578B\uFF1A", jobResource), /*#__PURE__*/React.createElement("span", {
        className: "right pointer",
        onClick: this.getDetailTaskList
      }, /*#__PURE__*/React.createElement(_icon["default"], {
        type: "sync"
      })));
      var totalModel = isKillAllTasks ? radioValue === 1 ? 0 : 1 : undefined;
      return /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("div", {
        style: {
          margin: 20
        }
      }, /*#__PURE__*/React.createElement(_goBack["default"], {
        size: "default",
        type: "textButton",
        style: {
          fontSize: '16px',
          color: '#333333',
          top: 0,
          letterSpacing: 0
        }
      }, " \u8FD4\u56DE")), /*#__PURE__*/React.createElement("div", {
        className: "box-2 m-card"
      }, /*#__PURE__*/React.createElement(_card["default"], {
        title: cardTitle
      }, /*#__PURE__*/React.createElement("div", {
        style: {
          margin: '16px 20px'
        }
      }, /*#__PURE__*/React.createElement(RadioGroup, {
        onChange: this.changeRadioValue.bind(this),
        value: this.state.radioValue
      }, Object.getOwnPropertyNames(_consts.JobStageText).map(function (statusValue) {
        return /*#__PURE__*/React.createElement(RadioButton, {
          key: statusValue,
          value: statusValue
        }, _consts.JobStageText[statusValue]);
      }))), /*#__PURE__*/React.createElement(_table["default"], {
        rowKey: function rowKey(record) {
          return "".concat(record.jobId);
        },
        loading: loading,
        className: "dt-table-border",
        pagination: this.getPagination(),
        rowSelection: rowSelection,
        dataSource: dataSource,
        columns: columns,
        onChange: this.onTableChange,
        footer: this.tableFooter
      }))), /*#__PURE__*/React.createElement(_viewDetail["default"], {
        visible: isShowViewDetail,
        onCancel: this.handleCloseViewModal.bind(this),
        resource: JSON.stringify(resource, null, 2)
      }), /*#__PURE__*/React.createElement(_viewDetail["default"], {
        title: "\u4EFB\u52A1\u53C2\u6570",
        visible: isShowTaskParams,
        onCancel: this.handleCloseViewModal.bind(this),
        resource: resource
      }), /*#__PURE__*/React.createElement(_killTask["default"], {
        visible: isShowKill,
        onCancel: this.handleCloseKill.bind(this),
        killResource: killResource,
        killSuccess: this.killSuccess.bind(this),
        autoRefresh: this.autoRefresh.bind(this),
        node: node,
        jobResource: this.state.jobResource,
        stage: this.state.radioValue
      }), /*#__PURE__*/React.createElement(_killAllTask["default"], {
        visible: isShowAllKill,
        totalModel: totalModel,
        onCancel: this.handleCloseKill.bind(this),
        killSuccess: this.killSuccess.bind(this),
        autoRefresh: this.autoRefresh.bind(this),
        killResource: killTaskInfo,
        node: node,
        totalSize: total,
        stage: this.state.radioValue,
        jobName: this.state.jobName,
        jobResource: this.state.jobResource,
        clusterName: this.state.clusterName
      }));
    }
  }]);
  return TaskDetail;
}(React.Component);

var _default = TaskDetail;
exports["default"] = _default;

/***/ }),

/***/ "6cUG":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("DQNa");

__webpack_require__("wLYn");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _tooltip = _interopRequireDefault(__webpack_require__("d1El"));

var _icon = _interopRequireDefault(__webpack_require__("Pbn2"));

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

// import { COMPONENT_TYPE_VALUE } from '../../consts'
var TEST_STATUS = {
  SUCCESS: true,
  FAIL: false
};

var TestRestIcon = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(TestRestIcon, _React$Component);

  var _super = _createSuper(TestRestIcon);

  function TestRestIcon(props) {
    var _this;

    (0, _classCallCheck2["default"])(this, TestRestIcon);
    _this = _super.call(this, props);
    _this.state = {};
    return _this;
  } // show err message


  (0, _createClass2["default"])(TestRestIcon, [{
    key: "showDetailErrMessage",
    value: function showDetailErrMessage(engine) {
      _modal["default"].error({
        title: "\u9519\u8BEF\u4FE1\u606F",
        content: "".concat(engine.errorMsg),
        zIndex: 1061
      });
    }
  }, {
    key: "matchCompTest",
    value: function matchCompTest(testResult) {
      switch (testResult.result) {
        case TEST_STATUS.SUCCESS:
          {
            return /*#__PURE__*/React.createElement(_icon["default"], {
              className: "success-icon",
              type: "check-circle"
            });
          }

        case TEST_STATUS.FAIL:
          {
            return /*#__PURE__*/React.createElement(_tooltip["default"], {
              title: /*#__PURE__*/React.createElement("a", {
                style: {
                  color: '#fff',
                  overflow: 'scroll'
                },
                onClick: this.showDetailErrMessage.bind(this, testResult)
              }, testResult.errorMsg),
              placement: "right"
            }, /*#__PURE__*/React.createElement(_icon["default"], {
              className: "err-icon",
              type: "close-circle"
            }));
          }

        default:
          {
            return null;
          }
      }
    }
  }, {
    key: "render",
    value: function render() {
      var testStatus = this.props.testStatus;
      return this.matchCompTest(testStatus);
    }
  }]);
  return TestRestIcon;
}(React.Component);

exports["default"] = TestRestIcon;

/***/ }),

/***/ "90CZ":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("TeQF");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _slicedToArray2 = _interopRequireDefault(__webpack_require__("J4zp"));

var _react = __webpack_require__("q1tI");

var _consts = __webpack_require__("RzPm");

function useEnv(_ref) {
  var clusterId = _ref.clusterId,
      form = _ref.form,
      clusterList = _ref.clusterList,
      visible = _ref.visible;

  var _useState = (0, _react.useState)([]),
      _useState2 = (0, _slicedToArray2["default"])(_useState, 2),
      queueList = _useState2[0],
      setQueueList = _useState2[1];

  var _useState3 = (0, _react.useState)({
    hasHadoop: false,
    hasLibra: false,
    hasTiDB: false,
    hasOracle: false,
    hasKubernetes: false,
    hasGreenPlum: false,
    hasPresto: false
  }),
      _useState4 = (0, _slicedToArray2["default"])(_useState3, 2),
      env = _useState4[0],
      setEnv = _useState4[1];

  (0, _react.useEffect)(function () {
    var _currentCluster$;

    if (!clusterId) return;
    var currentCluster = clusterList.filter(function (clusItem) {
      return (clusItem === null || clusItem === void 0 ? void 0 : clusItem.clusterId) == clusterId;
    }); // 选中当前集群

    var currentEngineList = (currentCluster === null || currentCluster === void 0 ? void 0 : (_currentCluster$ = currentCluster[0]) === null || _currentCluster$ === void 0 ? void 0 : _currentCluster$.engines) || [];
    var hadoopEngine = currentEngineList.filter(function (item) {
      return item.engineType == _consts.ENGINE_TYPE.HADOOP;
    });
    var libraEngine = currentEngineList.filter(function (item) {
      return item.engineType == _consts.ENGINE_TYPE.LIBRA;
    });
    var tiDBEngine = currentEngineList.filter(function (item) {
      return item.engineType == _consts.ENGINE_TYPE.TI_DB;
    });
    var oracleEngine = currentEngineList.filter(function (item) {
      return item.engineType == _consts.ENGINE_TYPE.ORACLE;
    });
    var greenPlumEngine = currentEngineList.filter(function (item) {
      return item.engineType == _consts.ENGINE_TYPE.GREEN_PLUM;
    });
    var prestoEngine = currentEngineList.filter(function (item) {
      return item.engineType == _consts.ENGINE_TYPE.PRESTO;
    });
    var kubernetesEngine = currentEngineList.filter(function (item) {
      return item.resourceType == _consts.RESOURCE_TYPE.KUBERNETES;
    });

    if (visible) {
      var _hadoopEngine$;

      setEnv({
        hasHadoop: hadoopEngine.length >= 1,
        hasLibra: libraEngine.length >= 1,
        hasTiDB: tiDBEngine.length > 0,
        hasKubernetes: kubernetesEngine.length >= 1,
        hasOracle: oracleEngine.length > 0,
        hasGreenPlum: greenPlumEngine.length > 0,
        hasPresto: prestoEngine.length > 0
      });
      setQueueList((hadoopEngine === null || hadoopEngine === void 0 ? void 0 : (_hadoopEngine$ = hadoopEngine[0]) === null || _hadoopEngine$ === void 0 ? void 0 : _hadoopEngine$.queues) || []);
    } else {
      form.resetFields(['queueId']);
      setEnv({
        hasKubernetes: false,
        hasHadoop: false,
        hasLibra: false,
        hasTiDB: false,
        hasOracle: false,
        hasGreenPlum: false,
        hasPresto: false
      });
      setQueueList([]);
    }
  }, [clusterId, clusterList, visible, form]);
  return {
    env: env,
    queueList: queueList
  };
}

var _default = useEnv;
exports["default"] = _default;

/***/ }),

/***/ "A9pX":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

Object.defineProperty(exports, "__esModule", {
  value: true
});
Object.defineProperty(exports, "useEnv", {
  enumerable: true,
  get: function get() {
    return _useEnv["default"];
  }
});

var _useEnv = _interopRequireDefault(__webpack_require__("90CZ"));

/***/ }),

/***/ "AwqB":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("oVuX");

__webpack_require__("07d7");

__webpack_require__("5s+n");

__webpack_require__("R5XZ");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

__webpack_require__("bZMm");

var _progressBar = _interopRequireDefault(__webpack_require__("wuId"));

var _funcs = __webpack_require__("9jh+");

var _interceptor = __webpack_require__("pWBi");

var Http = /*#__PURE__*/function () {
  function Http() {
    (0, _classCallCheck2["default"])(this, Http);
  }

  (0, _createClass2["default"])(Http, [{
    key: "get",
    value: function get(url, params) {
      // GET请求
      var newUrl = params ? this.build(url, params) : url;
      return this.request(newUrl, {
        method: 'GET'
      });
    }
  }, {
    key: "post",
    value: function post(url, body) {
      // POST请求
      var options = {
        method: 'POST'
      };
      if (body) options.body = JSON.stringify(body);
      return this.request(url, options);
    } // account 相关接口需要设置默认请求头

  }, {
    key: "postWithDefaultHeader",
    value: function postWithDefaultHeader(url, body) {
      // POST请求
      var options = {
        method: 'POST'
      };
      options.headers = this.defaultHeader();
      if (body) options.body = JSON.stringify(body);
      return this.request(url, options);
    }
  }, {
    key: "postAsFormData",
    value: function postAsFormData(url, params) {
      var options = {
        method: 'POST'
      };
      if (params) options.body = this.buildFormData(params);
      return this.request(url, options);
    }
  }, {
    key: "postForm",
    value: function postForm(url, form) {
      var options = {
        method: 'POST'
      };
      if (form) options.body = new FormData(form);
      return this.request(url, options);
    }
  }, {
    key: "request",
    value: function request(url, options) {
      _progressBar["default"].show();

      options.credentials = 'same-origin';
      return fetch(url, options).then(_interceptor.authBeforeFormate).then(function (response) {
        setTimeout(function () {
          _progressBar["default"].hide();
        }, 300);
        return response.json();
      }).then(_interceptor.authAfterFormated)["catch"](function (err) {
        _progressBar["default"].hide();

        console.error(url + ':' + err);
        (0, _funcs.singletonNotification)('请求异常', '服务器可能出了点问题, 请稍后再试！');
        /* eslint-disable-next-line */
        // return new Promise.reject(err);

        return Promise.reject(err);
      });
    }
  }, {
    key: "defaultHeader",
    value: function defaultHeader() {
      // 默认头
      var header = {
        'Accept': '*/*',
        'Content-Type': 'application/json'
      };
      return header;
    }
  }, {
    key: "build",
    value: function build(url, params) {
      // URL构建方法
      var ps = [];

      if (params) {
        for (var p in params) {
          if (p) {
            ps.push(p + '=' + encodeURIComponent(params[p]));
          }
        }
      }

      return url + '?' + ps.join('&');
    }
  }, {
    key: "buildFormData",
    value: function buildFormData(params) {
      if (params) {
        var data = new FormData();

        for (var p in params) {
          if (p) {
            data.append(p, params[p]);
          }
        }

        return data;
      }
    }
  }]);
  return Http;
}();

var _default = new Http();

exports["default"] = _default;

/***/ }),

/***/ "BSqx":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

var _interopRequireWildcard = __webpack_require__("284h");

__webpack_require__("DQNa");

__webpack_require__("sMBO");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _extends2 = _interopRequireDefault(__webpack_require__("pVnL"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _reactRedux = __webpack_require__("/MKj");

var _nav = _interopRequireDefault(__webpack_require__("1Zy+"));

var _consts = __webpack_require__("/g5M");

var _dec, _class;

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var Header = (_dec = (0, _reactRedux.connect)(function (state) {
  return {
    user: state.user,
    apps: state.apps,
    routing: state.routing,
    common: state.common,
    app: state.app,
    licenseApps: state.licenseApps
  };
}), _dec(_class = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(Header, _React$Component);

  var _super = _createSuper(Header);

  function Header(props) {
    var _this;

    (0, _classCallCheck2["default"])(this, Header);
    _this = _super.call(this, props);
    _this.state = {};
    return _this;
  }

  (0, _createClass2["default"])(Header, [{
    key: "render",
    value: function render() {
      var baseUrl = '/console';
      var _this$props = this.props,
          app = _this$props.app,
          licenseApps = _this$props.licenseApps;
      var menuItems = [{
        id: 'queueManage',
        name: '队列管理',
        link: "".concat(baseUrl, "/queueManage"),
        enable: true
      }, {
        id: 'resourceManage',
        name: '资源管理',
        link: "".concat(baseUrl, "/resourceManage"),
        enable: true
      }, {
        id: 'alarmChannel',
        name: '告警通道',
        link: "".concat(baseUrl, "/alarmChannel"),
        enable: true
      }, {
        id: 'clusterManage',
        name: '多集群管理',
        link: "".concat(baseUrl, "/clusterManage"),
        enable: true
      }];
      var logo = /*#__PURE__*/React.createElement("div", {
        className: "logo dt-header-log-wrapper",
        style: {
          "float": 'left'
        }
      }, /*#__PURE__*/React.createElement("img", {
        className: "c-header__logo",
        alt: "logo",
        src: (0, _consts.getHeaderLogo)(app.id)
      }), /*#__PURE__*/React.createElement("span", {
        className: "c-header__title"
      }, window.APP_CONF.prefix ? "".concat(window.APP_CONF.prefix, ".") : '', window.APP_CONF.name));
      return /*#__PURE__*/React.createElement(_nav["default"], (0, _extends2["default"])({
        logo: logo,
        menuItems: menuItems,
        licenseApps: licenseApps
      }, this.props));
    }
  }]);
  return Header;
}(React.Component)) || _class);
var _default = Header;
exports["default"] = _default;

/***/ }),

/***/ "BjRx":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("DQNa");

__webpack_require__("wLYn");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var KillTask = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(KillTask, _React$Component);

  var _super = _createSuper(KillTask);

  function KillTask() {
    (0, _classCallCheck2["default"])(this, KillTask);
    return _super.apply(this, arguments);
  }

  (0, _createClass2["default"])(KillTask, [{
    key: "killTask",
    // 请求杀任务接口
    value: function killTask() {
      var _this = this;

      var _this$props = this.props,
          killResource = _this$props.killResource,
          jobResource = _this$props.jobResource,
          stage = _this$props.stage,
          node = _this$props.node;

      _console["default"].killTasks({
        jobIdList: [killResource.jobId],
        stage: stage,
        jobResource: jobResource,
        nodeAddress: node
      }).then(function (res) {
        if (res.code == 1) {
          _this.props.killSuccess(killResource.jobId);

          _message2["default"].success('操作成功');

          _this.props.autoRefresh(); // 异步,成功之后才能关闭


          _this.props.onCancel();
        }
      });
    }
  }, {
    key: "confirmKilltask",
    value: function confirmKilltask() {
      this.killTask();
    }
  }, {
    key: "render",
    value: function render() {
      return /*#__PURE__*/React.createElement(_modal["default"], {
        title: "\u6740\u4EFB\u52A1",
        visible: this.props.visible,
        onCancel: this.props.onCancel,
        onOk: this.confirmKilltask.bind(this)
      }, /*#__PURE__*/React.createElement("p", null, "\u662F\u5426\u8981\u6740\u6B7B\u6B64\u4EFB\u52A1?"));
    }
  }]);
  return KillTask;
}(React.Component);

var _default = KillTask;
exports["default"] = _default;

/***/ }),

/***/ "C+lx":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("DQNa");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _extends2 = _interopRequireDefault(__webpack_require__("pVnL"));

var _input = _interopRequireDefault(__webpack_require__("iJl9"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _consts = __webpack_require__("RzPm");

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var FormItem = _form["default"].Item; // 新增集群、增加组件、增加引擎共用组件

var AddEngineModal = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(AddEngineModal, _React$Component);

  var _super = _createSuper(AddEngineModal);

  function AddEngineModal() {
    var _this;

    (0, _classCallCheck2["default"])(this, AddEngineModal);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onSubmit", function () {
      var _this$props = _this.props,
          onOk = _this$props.onOk,
          form = _this$props.form;
      var validateFields = form.validateFields;
      validateFields(function (err, value) {
        if (!err) {
          onOk({
            clusterName: value.clusterName
          });
        }
      });
    });
    return _this;
  }

  (0, _createClass2["default"])(AddEngineModal, [{
    key: "render",
    value: function render() {
      var getFieldDecorator = this.props.form.getFieldDecorator;
      var _this$props2 = this.props,
          title = _this$props2.title,
          visible = _this$props2.visible,
          onCancel = _this$props2.onCancel;
      return /*#__PURE__*/React.createElement(_modal["default"], {
        title: title,
        visible: visible,
        onCancel: onCancel,
        onOk: this.onSubmit,
        className: "c-clusterManage__modal"
      }, /*#__PURE__*/React.createElement(_form["default"], null, /*#__PURE__*/React.createElement(FormItem, (0, _extends2["default"])({
        label: "\u96C6\u7FA4\u540D\u79F0"
      }, _consts.formItemLayout), getFieldDecorator('clusterName', {
        rules: [{
          required: true,
          message: '集群标识不可为空！'
        }, {
          pattern: /^[a-z0-9_]{1,64}$/i,
          message: '集群标识不能超过64字符，支持英文、数字、下划线'
        }]
      })( /*#__PURE__*/React.createElement(_input["default"], {
        placeholder: "\u8BF7\u8F93\u5165\u96C6\u7FA4\u6807\u8BC6"
      })))));
    }
  }]);
  return AddEngineModal;
}(React.Component);

var _default = _form["default"].create()(AddEngineModal);

exports["default"] = _default;

/***/ }),

/***/ "C4JW":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = exports.CONSOLE_BASE_UPLOAD_URL = exports.CONSOLE_BASE_URL = void 0;
// 从config文件全局读取
var CONSOLE_BASE_URL = '/node';
exports.CONSOLE_BASE_URL = CONSOLE_BASE_URL;
var CONSOLE_BASE_UPLOAD_URL = '/node';
exports.CONSOLE_BASE_UPLOAD_URL = CONSOLE_BASE_UPLOAD_URL;
var _default = {
  // ===== 用户相关 ===== //
  DA_GET_USER_BY_ID: "".concat(CONSOLE_BASE_URL, "/user/getUserById"),
  // 根据用户ID获取用户
  DA_GET_USER_AUTH_BY_ID: "".concat(CONSOLE_BASE_URL, "/user/getUserById"),
  // 根据用户ID获取用户权限
  GET_TENANT_LIST: "".concat(CONSOLE_BASE_URL, "/tenant/listTenant"),
  // 租户列表
  GET_RESOURCE_USER_LIST: "".concat(CONSOLE_BASE_URL, "/tenant/listByQueueId"),
  // 获取资源已绑定的租户
  BIND_USER_TO_RESOURCE: "".concat(CONSOLE_BASE_URL, "/tenant/addToQueue"),
  // 绑定用户到资源队列
  CONFIRM_SWITCH_QUEUE: "".concat(CONSOLE_BASE_URL, "/tenant/updateQueueId"),
  // 确认切换队列
  // 集群
  GET_RESOURCE_LIST: "".concat(CONSOLE_BASE_URL, "/cluster/pageQueue"),
  // 查看资源列表
  GET_CLUSTER_LIST: "".concat(CONSOLE_BASE_URL, "/cluster/pageQuery"),
  // 查看集群列表
  UPDATE_CLUSTER: "".concat(CONSOLE_BASE_UPLOAD_URL, "/upload/cluster/update"),
  // 更新集群信息
  NEW_CLUSTER: "".concat(CONSOLE_BASE_UPLOAD_URL, "/upload/cluster/add"),
  // 新建集群
  TEST_CLUSTER_CONNECT: "".concat(CONSOLE_BASE_URL, "/cluster/testConnect"),
  // 测试集群联通性
  UPLOAD_CLUSTER_RESOURCE: "".concat(CONSOLE_BASE_UPLOAD_URL, "/upload/cluster/config"),
  // 上传集群资源配置包
  GET_CLUSTER: "".concat(CONSOLE_BASE_URL, "/cluster/getOne"),
  // 获取集群信息
  GET_QUEUE_LISTS: "".concat(CONSOLE_BASE_URL, "/cluster/listQueues"),
  // 集群下队列列表
  // 任务管理
  GET_CLUSTER_DETAIL: "".concat(CONSOLE_BASE_URL, "/console/overview"),
  // 概览-获取集群
  GET_CLUSTER_SELECT: "".concat(CONSOLE_BASE_URL, "/cluster/clusters"),
  // 概览-集群下拉列表
  GET_NODEADDRESS_SELECT: "".concat(CONSOLE_BASE_URL, "/console/nodeAddress"),
  // 获取节点下拉
  // 根据节点搜索
  SEARCH_TASKNAME_LIST: "".concat(CONSOLE_BASE_URL, "/console/searchJob"),
  // 明细-根据任务名搜索任务
  SEARCH_TASKNAME_FUZZY: "".concat(CONSOLE_BASE_URL, "/console/listNames"),
  // 明细-模糊查询任务名
  KILL_TASK: "".concat(CONSOLE_BASE_URL, "/console/stopJob"),
  // 明细-杀任务
  KILL_TASKS: "".concat(CONSOLE_BASE_URL, "/console/stopJobList"),
  // 明细-杀全部任务或选中任务
  KILL_ALL_TASK: "".concat(CONSOLE_BASE_URL, "/console/stopAll"),
  // 杀全部任务
  JOB_STICK: "".concat(CONSOLE_BASE_URL, "/console/jobStick"),
  // 置顶任务
  GET_ENGINE_LIST: "".concat(CONSOLE_BASE_URL, "/console/engineTypes"),
  // 引擎列表
  GET_GROUP_LIST: "".concat(CONSOLE_BASE_URL, "/console/groups"),
  // group列表
  GET_VIEW_DETAIL: "".concat(CONSOLE_BASE_URL, "/console/groupDetail"),
  // 查看明细 和搜索条件
  CHANGE_JOB_PRIORITY: "".concat(CONSOLE_BASE_URL, "/console/jobPriority"),
  // 顺序调整调整优先级
  GET_CLUSTER_RESOURCES: "".concat(CONSOLE_BASE_URL, "/console/clusterResources"),
  // 查看剩余资源
  // 4.0版本
  GET_CLUSTER_INFO: "".concat(CONSOLE_BASE_URL, "/component/cluster/getCluster"),
  UPLOAD_RESOURCE: "".concat(CONSOLE_BASE_UPLOAD_URL, "/upload/component/config"),
  // 上传配置文件
  DOWNLOAD_RESOURCE: "".concat(CONSOLE_BASE_UPLOAD_URL, "/download/component/downloadFile"),
  // 下载配置文件
  DELETE_CLUSTER: "".concat(CONSOLE_BASE_UPLOAD_URL, "/cluster/deleteCluster"),
  // 删除集群
  DELETE_COMPONENT: "".concat(CONSOLE_BASE_URL, "/component/delete"),
  GET_COMPONENT_VERSION: "".concat(CONSOLE_BASE_URL, "/component/getComponentVersion"),
  TEST_CONNECTS: "".concat(CONSOLE_BASE_URL, "/component/testConnects"),
  SAVE_COMPONENT: "".concat(CONSOLE_BASE_URL, "/upload/component/addOrUpdateComponent"),
  CLOSE_KERBEROS: "".concat(CONSOLE_BASE_URL, "/component/closeKerberos"),
  GET_VERSION: "".concat(CONSOLE_BASE_URL, "/component/getComponentVersion"),
  ADD_CLUSTER: "".concat(CONSOLE_BASE_URL, "/component/addOrCheckClusterWithName"),
  // 新增集群
  GET_LOADTEMPLATE: "".concat(CONSOLE_BASE_UPLOAD_URL, "/component/loadTemplate"),
  // 获取上传模板
  UPLOAD_KERBEROSFILE: "".concat(CONSOLE_BASE_UPLOAD_URL, "/upload/component/hadoopKerberosConfig"),
  // 上传kerberos认证文件
  GET_KERBEROSFILE: "".concat(CONSOLE_BASE_URL, "/component/getHadoopKerberosFile"),
  // 获取上传过的kerberos认证文件的信息内容
  TEST_COMPONENT_CONNECT: "".concat(CONSOLE_BASE_URL, "/component/testConnections"),
  TEST_COMPONENT_CONNECT_KERBEROS: "".concat(CONSOLE_BASE_UPLOAD_URL, "/upload/service/component/testConnections"),
  // 测试连通性只要有组件开启kerberos认证就掉该接口
  ADD_COMPONENT: "".concat(CONSOLE_BASE_URL, "/component/addComponent"),
  SAVE_COMPONENT_KERBEROS: "".concat(CONSOLE_BASE_UPLOAD_URL, "/upload/service/component/updateWithKerberos"),
  // 开启kerberos认证的保存接口
  DELETE_KERBEROS: "".concat(CONSOLE_BASE_URL, "/component/rmKerberosConfig"),
  // 删除Haddop Kerberos认证文件
  ADD_ENGINE: "".concat(CONSOLE_BASE_URL, "/engine/addEngine"),
  ADD_ENGINS: "".concat(CONSOLE_BASE_URL, "/engine/addEngines"),
  UPDATE_CLUSTER_VERSION: "".concat(CONSOLE_BASE_URL, "/cluster/updateGlobalConfig"),
  // 更新hadoop版本
  // 4.1版本
  GET_COMPONENTSTORE: "".concat(CONSOLE_BASE_URL, "/component/getComponentStore"),
  // 获取存储组件列表
  PARSE_KERBEROS: "".concat(CONSOLE_BASE_URL, "/upload/component/parseKerberos"),
  UPLOAD_KERBEROS: "".concat(CONSOLE_BASE_URL, "/upload/component/uploadKerberos"),
  // 上传kerberos文件
  UPDATE_KRB5CONF: "".concat(CONSOLE_BASE_URL, "/component/updateKrb5Conf"),
  // 更新krb5文件
  // 资源管理
  GET_ALL_CLUSTER: "".concat(CONSOLE_BASE_URL, "/cluster/getAllCluster"),
  SEARCH_TENANT: "".concat(CONSOLE_BASE_URL, "/tenant/pageQuery"),
  GET_QUEUE: "".concat(CONSOLE_BASE_URL, "/engine/getQueue"),
  BIND_TENANT: "".concat(CONSOLE_BASE_URL, "/tenant/bindingTenant"),
  LDAP_ACCOUNT_BIND: "".concat(CONSOLE_BASE_URL, "/account/bindAccountList"),
  SWITCH_QUEUE: "".concat(CONSOLE_BASE_URL, "/tenant/bindingQueue"),
  BIND_NAME_SPACE: "".concat(CONSOLE_BASE_URL, "/tenant/bindNamespace"),
  GET_TASKLIMITS: "".concat(CONSOLE_BASE_URL, "/tenant/queryTaskResourceLimits"),
  REFRESH_QUEUE: "".concat(CONSOLE_BASE_URL, "/component/refresh"),
  TASK_RESOURCE: "".concat(CONSOLE_BASE_URL, "/console/getTaskResourceTemplate"),
  // TiDB 引擎账号绑定
  ACCOUNT_UNBIND_LIST: "".concat(CONSOLE_BASE_URL, "/account/getTenantUnBandList"),
  ACCOUNT_BIND: "".concat(CONSOLE_BASE_URL, "/account/bindAccount"),
  UPDATE_ACCOUNT_BIND: "".concat(CONSOLE_BASE_URL, "/account/updateBindAccount"),
  ACCOUNT_BIND_LIST: "".concat(CONSOLE_BASE_URL, "/account/pageQuery"),
  ACCOUNT_UNBIND: "".concat(CONSOLE_BASE_URL, "/account/unbindAccount"),
  // 告警通道
  ADD_OR_UPDATE_ALARM: "".concat(CONSOLE_BASE_URL, "/alert/edit"),
  GET_ALARM_RULE_LIST: "".concat(CONSOLE_BASE_URL, "/alert/page"),
  DEL_ALARM_RULE_LIST: "".concat(CONSOLE_BASE_URL, "/alert/delete"),
  GET_ALARM_BY_ID: "".concat(CONSOLE_BASE_URL, "/alert/getByAlertId"),
  SET_DEFAULT_ALERT: "".concat(CONSOLE_BASE_URL, "/alert/setDefaultAlert"),
  TEST_ALERT: "".concat(CONSOLE_BASE_URL, "/alert/testAlert")
};
exports["default"] = _default;

/***/ }),

/***/ "Efuq":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

var _interopRequireWildcard = __webpack_require__("284h");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _reactRouter = __webpack_require__("dtw8");

var _notFund = _interopRequireDefault(__webpack_require__("Yetp"));

var _views = _interopRequireDefault(__webpack_require__("o8do"));

var _message = _interopRequireDefault(__webpack_require__("/U8/"));

var _list = _interopRequireDefault(__webpack_require__("aOFy"));

var _detail = _interopRequireDefault(__webpack_require__("fsgv"));

var _admin = _interopRequireDefault(__webpack_require__("94w5"));

var _user = _interopRequireDefault(__webpack_require__("gYnx"));

var _role = _interopRequireDefault(__webpack_require__("NJGX"));

var _add = _interopRequireDefault(__webpack_require__("8Gfk"));

var _edit = _interopRequireDefault(__webpack_require__("wmNS"));

var _audit = _interopRequireDefault(__webpack_require__("CaGh"));

var _views2 = _interopRequireDefault(__webpack_require__("KQ7z"));

var _queueManage = _interopRequireDefault(__webpack_require__("zqmc"));

var _resourceManage = _interopRequireDefault(__webpack_require__("HGpX"));

var _clusterManage = _interopRequireDefault(__webpack_require__("Uu3x"));

var _newEdit = _interopRequireDefault(__webpack_require__("RclC"));

var _taskDetail = _interopRequireDefault(__webpack_require__("6aLI"));

var _alarmChannel = _interopRequireDefault(__webpack_require__("XdBI"));

var _alarmRule = _interopRequireDefault(__webpack_require__("+CR5"));

// 继承主应用的的公共View组件
// 数据API
// ======= 测试 =======
// const Test = asyncComponent(() => import('./views/test')
// .then((module: any) => module.default), { name: 'testPage' })
var _default = /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "/",
  component: _views["default"]
}, /*#__PURE__*/React.createElement(_reactRouter.IndexRedirect, {
  to: "/console"
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "/message",
  component: _message["default"]
}, /*#__PURE__*/React.createElement(_reactRouter.IndexRoute, {
  component: _list["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "list",
  component: _list["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "detail/:msgId",
  component: _detail["default"]
})), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "/admin",
  component: _admin["default"]
}, /*#__PURE__*/React.createElement(_reactRouter.IndexRoute, {
  component: _user["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "user",
  component: _user["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "role",
  component: _role["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "role/add",
  component: _add["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "role/edit/:roleId",
  component: _edit["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "audit",
  component: _audit["default"]
})), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "/console",
  component: _views2["default"]
}, /*#__PURE__*/React.createElement(_reactRouter.IndexRoute, {
  component: _queueManage["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "queueManage",
  component: _queueManage["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "queueManage/detail",
  component: _taskDetail["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "resourceManage",
  component: _resourceManage["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "clusterManage",
  component: _clusterManage["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "clusterManage/editCluster",
  component: _newEdit["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "alarmChannel",
  component: _alarmChannel["default"]
}), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "alarmChannel/alarmRule",
  component: _alarmRule["default"]
})), /*#__PURE__*/React.createElement(_reactRouter.Route, {
  path: "/*",
  component: _notFund["default"]
}));

exports["default"] = _default;

/***/ }),

/***/ "Ej2X":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("DQNa");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _editor = _interopRequireDefault(__webpack_require__("zN5T"));

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var ViewDetail = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(ViewDetail, _React$Component);

  var _super = _createSuper(ViewDetail);

  function ViewDetail() {
    var _this;

    (0, _classCallCheck2["default"])(this, ViewDetail);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      editor: {
        sql: '',
        cursor: undefined,
        sync: true
      }
    });
    return _this;
  }

  (0, _createClass2["default"])(ViewDetail, [{
    key: "render",
    value: function render() {
      var title = this.props.title;
      return /*#__PURE__*/React.createElement(_modal["default"], {
        title: title || '任务详情',
        width: 650,
        onCancel: this.props.onCancel,
        onOk: this.props.onCancel,
        visible: this.props.visible
      }, /*#__PURE__*/React.createElement(_editor["default"], {
        style: {
          height: '400px',
          marginTop: '1px'
        },
        value: this.props.resource,
        language: "ini",
        options: {
          readOnly: true,
          minimap: {
            enabled: false
          }
        },
        sync: true
      }));
    }
  }]);
  return ViewDetail;
}(React.Component);

var _default = ViewDetail;
exports["default"] = _default;

/***/ }),

/***/ "FZsQ":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


__webpack_require__("DQNa");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.giveMeAKey = giveMeAKey;

function giveMeAKey() {
  return new Date().getTime() + '' + ~~(Math.random() * 100000);
}

/***/ }),

/***/ "HGpX":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("wLYn");

__webpack_require__("zKZe");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("4l63");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _card = _interopRequireDefault(__webpack_require__("N9UN"));

var _extends2 = _interopRequireDefault(__webpack_require__("pVnL"));

var _table = _interopRequireDefault(__webpack_require__("DtFj"));

var _row = _interopRequireDefault(__webpack_require__("9xET"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _col = _interopRequireDefault(__webpack_require__("ZPTe"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _regenerator = _interopRequireDefault(__webpack_require__("o0o1"));

__webpack_require__("ls82");

var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__("yXPU"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _input = _interopRequireDefault(__webpack_require__("iJl9"));

var _tabs = _interopRequireDefault(__webpack_require__("j7zX"));

var _select = _interopRequireDefault(__webpack_require__("FAat"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _lodash = __webpack_require__("LvDl");

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

var _consts = __webpack_require__("RzPm");

var _clusterFunc = __webpack_require__("IiER");

var _bindCommModal = _interopRequireDefault(__webpack_require__("s7lY"));

var _resourceManageModal = _interopRequireDefault(__webpack_require__("UANx"));

var _resourceView = _interopRequireDefault(__webpack_require__("jRo7"));

var _bindAccount = _interopRequireDefault(__webpack_require__("kYLF"));

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var FormItem = _form["default"].Item;
var Option = _select["default"].Option;
var TabPane = _tabs["default"].TabPane;
var Search = _input["default"].Search;
var PAGESIZE = 20;

var ResourceManage = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(ResourceManage, _React$Component);

  var _super = _createSuper(ResourceManage);

  function ResourceManage() {
    var _this;

    (0, _classCallCheck2["default"])(this, ResourceManage);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      tableData: [],
      clusterList: [],
      engineList: [],
      queryParams: {
        clusterId: '',
        engineType: '',
        tenantName: '',
        pageSize: PAGESIZE,
        currentPage: 1,
        clusterName: ''
      },
      loading: false,
      total: 0,
      tenantModal: false,
      queueModal: false,
      manageModal: false,
      tenantInfo: '',
      isHaveHadoop: false,
      isHaveLibra: false,
      queueList: [] // hadoop资源队列

    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "requestEnd", true);
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "searchTenant", function () {
      var queryParams = _this.state.queryParams;

      _this.setState({
        loading: true
      });

      _console["default"].searchTenant(queryParams).then(function (res) {
        if (res.code === 1) {
          _this.setState({
            tableData: (0, _lodash.get)(res, 'data.data', []),
            total: (0, _lodash.get)(res, 'data.totalCount', 0),
            loading: false
          });
        } else {
          _this.setState({
            loading: false
          });
        }
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "initList", /*#__PURE__*/(0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee() {
      var res, data, engineList, initCluster, initEngine;
      return _regenerator["default"].wrap(function _callee$(_context) {
        while (1) {
          switch (_context.prev = _context.next) {
            case 0:
              _context.next = 2;
              return _console["default"].getAllCluster();

            case 2:
              res = _context.sent;

              if (res.code === 1) {
                data = res.data || [];
                engineList = data[0] && data[0].engines || [];
                initCluster = data[0] || [];
                initEngine = engineList[0] || [];

                _this.setState({
                  clusterList: data,
                  queryParams: Object.assign(_this.state.queryParams, {
                    clusterId: initCluster.clusterId,
                    engineType: initEngine.engineType
                  }),
                  clusterName: initCluster.clusterName,
                  engineList: engineList,
                  loading: true
                }, _this.searchTenant);
              }

            case 4:
            case "end":
              return _context.stop();
          }
        }
      }, _callee);
    })));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "clusterOptions", function () {
      var clusterList = _this.state.clusterList;
      return clusterList.map(function (item) {
        return /*#__PURE__*/React.createElement(Option, {
          key: "".concat(item.clusterId),
          value: "".concat(item.clusterId)
        }, item.clusterName);
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleChangeCluster", function (value) {
      var clusterList = _this.state.clusterList;
      var currentCluster;
      currentCluster = clusterList.filter(function (clusItem) {
        return clusItem.clusterId == value;
      }); // 选中当前集群

      var currentEngineList = currentCluster[0] && currentCluster[0].engines || [];
      var queryParams = Object.assign(_this.state.queryParams, {
        clusterId: value,
        engineType: currentEngineList[0] && currentEngineList[0].engineType,
        tenantName: '',
        pageSize: PAGESIZE,
        currentPage: 1
      });

      _this.setState({
        engineList: currentEngineList,
        queryParams: queryParams,
        clusterName: currentCluster[0].clusterName
      }, _this.searchTenant);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "changeTenantName", function (value) {
      var queryParams = Object.assign(_this.state.queryParams, {
        tenantName: value
      });

      _this.setState({
        queryParams: queryParams
      }, _this.searchTenant);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleTableChange", function (pagination, filters, sorter) {
      var queryParams = Object.assign(_this.state.queryParams, {
        currentPage: pagination.current
      });

      _this.setState({
        queryParams: queryParams
      }, _this.searchTenant);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleEngineTab", function (key) {
      var queryParams = Object.assign(_this.state.queryParams, {
        engineType: key,
        tenantName: '',
        currentPage: 1
      });

      _this.setState({
        queryParams: queryParams
      }, _this.searchTenant);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "clickSwitchQueue", function (record) {
      _this.setState({
        manageModal: true,
        tenantInfo: record
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "clickSwitchNamespace", function (record) {
      _this.setState({
        queueModal: true,
        tenantInfo: record
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "initHadoopColumns", function () {
      return [{
        title: '租户',
        dataIndex: 'tenantName',
        render: function render(text, record) {
          return text;
        }
      }, {
        title: '资源队列',
        dataIndex: 'queue',
        render: function render(text, record) {
          return text;
        }
      }, {
        title: '最小容量（%）',
        dataIndex: 'minCapacity',
        render: function render(text, record) {
          return text;
        }
      }, {
        title: '最大容量（%）',
        dataIndex: 'maxCapacity',
        render: function render(text, record) {
          return text;
        }
      }, {
        title: '操作',
        dataIndex: 'deal',
        render: function render(text, record) {
          return /*#__PURE__*/React.createElement("a", {
            onClick: function onClick() {
              _this.clickSwitchQueue(record);
            }
          }, "\u8D44\u6E90\u7BA1\u7406");
        }
      }];
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "initKubernetesColumns", function () {
      return [{
        title: '租户',
        dataIndex: 'tenantName'
      }, {
        title: 'Namespace',
        dataIndex: 'queue'
      }, {
        title: '操作',
        dataIndex: 'action',
        width: 200,
        render: function render(text, record) {
          return /*#__PURE__*/React.createElement("a", {
            onClick: function onClick() {
              _this.clickSwitchNamespace(record);
            }
          }, "\u5207\u6362Namespace");
        }
      }];
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "initDefaultColumns", function () {
      return [{
        title: '租户',
        dataIndex: 'tenantName',
        render: function render(text, record) {
          return text;
        }
      }];
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "getColumn", function (isHadoop, resourceType) {
      var column = _this.initHadoopColumns();

      if (!isHadoop) column = _this.initDefaultColumns();
      if (isHadoop && (0, _clusterFunc.isKubernetesEngine)(resourceType)) column = _this.initKubernetesColumns();
      return column;
    });
    return _this;
  }

  (0, _createClass2["default"])(ResourceManage, [{
    key: "componentDidMount",
    // 请求结束
    value: function componentDidMount() {
      this.initList();
    }
  }, {
    key: "bindTenant",
    value: function bindTenant(params) {
      var _this2 = this;

      var canSubmit = params.canSubmit,
          reqParams = params.reqParams;

      if (canSubmit && this.requestEnd) {
        this.requestEnd = false;

        _console["default"].bindTenant(_objectSpread({}, reqParams)).then(function (res) {
          if (res.code === 1) {
            _this2.setState({
              tenantModal: false
            });

            _message2["default"].success('租户绑定成功');

            _this2.searchTenant();
          }

          _this2.requestEnd = true;
        });
      }
    }
  }, {
    key: "switchQueue",
    value: function switchQueue(params) {
      var _this3 = this;

      var reqParams = params.reqParams,
          hasKubernetes = params.hasKubernetes;

      if (hasKubernetes) {
        _console["default"].bindNamespace({
          clusterId: reqParams.clusterId,
          namespace: reqParams.namespace,
          queueId: reqParams.queueId,
          tenantId: reqParams.tenantId
        }).then(function (res) {
          if (res.code === 1) {
            _this3.setState({
              queueModal: false
            });

            _message2["default"].success('切换队列成功');

            _this3.searchTenant();
          }
        });
      } else {
        _console["default"].switchQueue(_objectSpread({}, reqParams)).then(function (res) {
          if (res.code === 1) {
            _this3.setState({
              queueModal: false
            });

            _message2["default"].success('切换队列成功');

            _this3.searchTenant();
          }
        });
      }
    }
  }, {
    key: "sourceManage",
    value: function sourceManage(params) {
      var _this4 = this;

      this.setState({
        manageModal: false,
        tenantInfo: ''
      }, function () {
        return _this4.initList();
      });
    }
  }, {
    key: "showTenant",
    value: function showTenant() {
      this.setState({
        tenantModal: true
      });
    }
  }, {
    key: "render",
    value: function render() {
      var _this5 = this,
          _tenantInfo$tenantNam;

      var _this$state = this.state,
          tableData = _this$state.tableData,
          queryParams = _this$state.queryParams,
          total = _this$state.total,
          loading = _this$state.loading,
          engineList = _this$state.engineList,
          clusterList = _this$state.clusterList,
          tenantModal = _this$state.tenantModal,
          queueModal = _this$state.queueModal,
          modalKey = _this$state.modalKey,
          clusterName = _this$state.clusterName,
          tenantInfo = _this$state.tenantInfo,
          manageModal = _this$state.manageModal;
      var kubernetesEngine = (0, _clusterFunc.isKubernetesEngine)(engineList[0] && engineList[0].resourceType);
      var pagination = {
        current: queryParams.currentPage,
        pageSize: PAGESIZE,
        total: total
      };
      console.log('console:', this.state);
      return /*#__PURE__*/React.createElement("div", {
        className: "resource-wrapper"
      }, /*#__PURE__*/React.createElement(_row["default"], null, /*#__PURE__*/React.createElement(_col["default"], {
        span: 12
      }, /*#__PURE__*/React.createElement(_form["default"], {
        className: "m-form-inline",
        layout: "inline"
      }, /*#__PURE__*/React.createElement(FormItem, {
        label: "\u96C6\u7FA4"
      }, /*#__PURE__*/React.createElement(_select["default"], {
        className: "dt-form-shadow-bg",
        style: {
          width: '264px'
        },
        placeholder: "\u8BF7\u9009\u62E9\u96C6\u7FA4",
        value: "".concat(queryParams.clusterId),
        onChange: this.handleChangeCluster
      }, this.clusterOptions())))), /*#__PURE__*/React.createElement(_col["default"], {
        span: 12
      }, /*#__PURE__*/React.createElement(_button["default"], {
        className: "terent-button",
        type: "primary",
        onClick: function onClick() {
          _this5.setState({
            tenantModal: true
          });
        }
      }, "\u7ED1\u5B9A\u65B0\u79DF\u6237"))), /*#__PURE__*/React.createElement("div", {
        className: "resource-content"
      }, /*#__PURE__*/React.createElement(_card["default"], {
        className: "console-tabs resource-tab-width",
        bordered: false
      }, /*#__PURE__*/React.createElement(_tabs["default"], (0, _extends2["default"])({
        tabPosition: "left",
        defaultActiveKey: "".concat(engineList[0] && engineList[0].engineType),
        onChange: this.handleEngineTab,
        activeKey: "".concat(queryParams.engineType)
      }, {
        forceRender: true
      }), engineList && engineList.map(function (item) {
        var engineType = item.engineType,
            resourceType = item.resourceType;
        var isHadoop = (0, _clusterFunc.isHadoopEngine)(engineType);
        var engineName = isHadoop && (0, _clusterFunc.isKubernetesEngine)(resourceType) ? 'Kubernetes' : _consts.ENGIN_TYPE_TEXT[engineType];
        return /*#__PURE__*/React.createElement(TabPane, {
          className: "tab-pane-wrapper",
          tab: engineName,
          key: "".concat(engineType)
        }, /*#__PURE__*/React.createElement(_tabs["default"], {
          key: "".concat(engineType, "-tenant"),
          className: "engine-detail-tabs",
          tabPosition: "top",
          animated: false
        }, (0, _clusterFunc.isHadoopEngine)(engineType) ? /*#__PURE__*/React.createElement(TabPane, {
          tab: "\u8D44\u6E90\u5168\u666F",
          key: "showResource"
        }, /*#__PURE__*/React.createElement(_resourceView["default"], {
          key: "".concat(clusterName),
          clusterName: clusterName
        })) : null, /*#__PURE__*/React.createElement(TabPane, {
          tab: "\u79DF\u6237\u7ED1\u5B9A",
          key: "bindTenant"
        }, /*#__PURE__*/React.createElement("div", {
          style: {
            margin: 15
          }
        }, /*#__PURE__*/React.createElement(Search, {
          style: {
            width: '200px',
            marginBottom: '20px'
          },
          placeholder: "\u6309\u79DF\u6237\u540D\u79F0\u641C\u7D22",
          value: queryParams.tenantName,
          onChange: function onChange(e) {
            _this5.setState({
              queryParams: Object.assign(_this5.state.queryParams, {
                tenantName: e.target.value
              })
            });
          },
          onSearch: _this5.changeTenantName
        }), /*#__PURE__*/React.createElement(_table["default"], {
          className: "dt-table-border",
          loading: loading,
          rowKey: function rowKey(record, index) {
            return "".concat(index, "-").concat(record.tenantName);
          },
          columns: _this5.getColumn(isHadoop, resourceType),
          dataSource: tableData,
          pagination: pagination,
          onChange: _this5.handleTableChange
        }))), (0, _clusterFunc.isTiDBEngine)(engineType) || (0, _clusterFunc.isOracleEngine)(engineType) || (0, _clusterFunc.isGreenPlumEngine)(engineType) ? /*#__PURE__*/React.createElement(TabPane, {
          tab: "\u8D26\u53F7\u7ED1\u5B9A",
          key: "bindAccount"
        }, /*#__PURE__*/React.createElement(_bindAccount["default"], {
          key: "".concat(queryParams.clusterId, "-").concat(engineType),
          engineType: parseInt(engineType, 10),
          clusterId: queryParams.clusterId
        })) : null));
      })))), /*#__PURE__*/React.createElement(_bindCommModal["default"], {
        title: "\u7ED1\u5B9A\u65B0\u79DF\u6237",
        visible: tenantModal,
        clusterList: clusterList,
        isBindTenant: true,
        onCancel: function onCancel() {
          _this5.setState({
            tenantModal: false
          });
        },
        onOk: this.bindTenant.bind(this)
      }), /*#__PURE__*/React.createElement(_bindCommModal["default"], {
        key: modalKey,
        title: kubernetesEngine ? '切换namespace' : '切换队列',
        visible: queueModal,
        isBindTenant: false,
        isBindNamespace: kubernetesEngine,
        clusterList: clusterList,
        tenantInfo: this.state.tenantInfo,
        clusterId: queryParams.clusterId,
        disabled: true,
        onCancel: function onCancel() {
          _this5.setState({
            queueModal: false,
            tenantInfo: ''
          });
        },
        onOk: this.switchQueue.bind(this)
      }), /*#__PURE__*/React.createElement(_resourceManageModal["default"], {
        title: "\u8D44\u6E90\u7BA1\u7406 (".concat((_tenantInfo$tenantNam = tenantInfo.tenantName) !== null && _tenantInfo$tenantNam !== void 0 ? _tenantInfo$tenantNam : '', ")"),
        visible: manageModal,
        isBindTenant: false,
        clusterList: clusterList,
        tenantInfo: this.state.tenantInfo,
        clusterId: queryParams.clusterId,
        disabled: true,
        tenantId: tenantInfo === null || tenantInfo === void 0 ? void 0 : tenantInfo.tenantId,
        queue: tenantInfo === null || tenantInfo === void 0 ? void 0 : tenantInfo.queue,
        queueId: tenantInfo === null || tenantInfo === void 0 ? void 0 : tenantInfo.queueId,
        onCancel: function onCancel() {
          _this5.setState({
            manageModal: false,
            tenantInfo: ''
          });
        },
        onOk: this.sourceManage.bind(this)
      }));
    }
  }]);
  return ResourceManage;
}(React.Component);

var _default = ResourceManage;
exports["default"] = _default;

/***/ }),

/***/ "Hc45":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("yq1k");

__webpack_require__("4mDm");

__webpack_require__("oVuX");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("T63A");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("rB9j");

__webpack_require__("JfAA");

__webpack_require__("JTJg");

__webpack_require__("EnZy");

__webpack_require__("3bBZ");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _row = _interopRequireDefault(__webpack_require__("9xET"));

var _col = _interopRequireDefault(__webpack_require__("ZPTe"));

var _slicedToArray2 = _interopRequireDefault(__webpack_require__("J4zp"));

var _extends2 = _interopRequireDefault(__webpack_require__("pVnL"));

var _tooltip = _interopRequireDefault(__webpack_require__("d1El"));

var _input = _interopRequireDefault(__webpack_require__("iJl9"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _checkbox = _interopRequireDefault(__webpack_require__("g4D/"));

var _select = _interopRequireDefault(__webpack_require__("FAat"));

var _radio = _interopRequireDefault(__webpack_require__("qPIi"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _lodash = __webpack_require__("LvDl");

var _const = __webpack_require__("j1Tt");

var _help = __webpack_require__("LNB4");

var _consts = __webpack_require__("RzPm");

var _customParams = _interopRequireDefault(__webpack_require__("pXCp"));

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var FormItem = _form["default"].Item;
var RadioGroup = _radio["default"].Group;
var Option = _select["default"].Option;
var CheckboxGroup = _checkbox["default"].Group;

var FormConfig = /*#__PURE__*/function (_React$PureComponent) {
  (0, _inherits2["default"])(FormConfig, _React$PureComponent);

  var _super = _createSuper(FormConfig);

  function FormConfig() {
    var _this;

    (0, _classCallCheck2["default"])(this, FormConfig);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderOptoinsType", function (temp) {
      var view = _this.props.view;

      switch (temp.type) {
        case _const.CONFIG_ITEM_TYPE.RADIO:
          return /*#__PURE__*/React.createElement(RadioGroup, {
            disabled: view
          }, temp.values.map(function (comp) {
            return /*#__PURE__*/React.createElement(_radio["default"], {
              key: comp.key,
              value: comp.value
            }, comp.key);
          }));

        case _const.CONFIG_ITEM_TYPE.SELECT:
          return /*#__PURE__*/React.createElement(_select["default"], {
            disabled: view,
            style: {
              width: 200
            }
          }, temp.values.map(function (comp) {
            return /*#__PURE__*/React.createElement(Option, {
              key: comp.key,
              value: comp.value
            }, comp.key);
          }));

        case _const.CONFIG_ITEM_TYPE.CHECKBOX:
          return /*#__PURE__*/React.createElement(CheckboxGroup, {
            disabled: view,
            className: "c-componentConfig__checkboxGroup"
          }, temp.values.map(function (comp) {
            return /*#__PURE__*/React.createElement(_checkbox["default"], {
              key: comp.key,
              value: "".concat(comp.value)
            }, comp.key);
          }));

        case _const.CONFIG_ITEM_TYPE.PASSWORD:
          return /*#__PURE__*/React.createElement(_input["default"].Password, {
            disabled: view,
            style: {
              maxWidth: 680
            },
            visibilityToggle: false
          });

        default:
          return /*#__PURE__*/React.createElement(_input["default"], {
            disabled: view,
            style: {
              maxWidth: 680
            }
          });
      }
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderConfigItem", function (temp, groupKey) {
      var _comp$componentTypeCo;

      var _this$props = _this.props,
          form = _this$props.form,
          comp = _this$props.comp;
      var typeCode = (_comp$componentTypeCo = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo !== void 0 ? _comp$componentTypeCo : '';
      var initialValue = temp.key === 'deploymode' && !(0, _lodash.isArray)(temp.value) ? temp.value.split() : temp.value;
      var fieldName = groupKey ? "".concat(typeCode, ".componentConfig.").concat(groupKey) : "".concat(typeCode, ".componentConfig");
      return !temp.id && /*#__PURE__*/React.createElement(FormItem, (0, _extends2["default"])({
        label: /*#__PURE__*/React.createElement(_tooltip["default"], {
          title: temp.key
        }, /*#__PURE__*/React.createElement("span", {
          className: "c-formConfig__label"
        }, temp.key)),
        key: temp.key
      }, _consts.formItemLayout), form.getFieldDecorator("".concat(fieldName, ".").concat(temp.key.split('.').join('%')), {
        rules: [{
          required: temp.required,
          message: "\u8BF7\u8F93\u5165".concat(temp.key)
        }],
        initialValue: initialValue
      })(_this.renderOptoinsType(temp)));
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "rendeConfigForm", function () {
      var _comp$componentTypeCo2, _getValueByJson;

      var _this$props2 = _this.props,
          comp = _this$props2.comp,
          form = _this$props2.form,
          view = _this$props2.view;
      var typeCode = (_comp$componentTypeCo2 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo2 !== void 0 ? _comp$componentTypeCo2 : '';
      var template = (_getValueByJson = (0, _help.getValueByJson)(comp === null || comp === void 0 ? void 0 : comp.componentTemplate)) !== null && _getValueByJson !== void 0 ? _getValueByJson : [];
      var isHaveGroup = false;
      return /*#__PURE__*/React.createElement(React.Fragment, null, template.map(function (temps) {
        // 根据GROUP类型的模版对象的依赖值渲染单个配置项
        // 每个组件添加自定义参数
        if (temps.type == _const.CONFIG_ITEM_TYPE.GROUP) {
          var _form$getFieldValue;

          isHaveGroup = true;
          var dependencyValue = (_form$getFieldValue = form.getFieldValue("".concat(typeCode, ".componentConfig.").concat(temps.dependencyKey))) !== null && _form$getFieldValue !== void 0 ? _form$getFieldValue : [];

          if (dependencyValue.includes(temps === null || temps === void 0 ? void 0 : temps.dependencyValue) || !temps.dependencyValue) {
            return /*#__PURE__*/React.createElement("div", {
              className: "c-formConfig__group",
              key: temps.key
            }, /*#__PURE__*/React.createElement("div", {
              className: "group__title"
            }, temps.key), /*#__PURE__*/React.createElement("div", {
              className: "group__content"
            }, temps.values.map(function (temp) {
              return _this.renderConfigItem(temp, temps.key);
            }), /*#__PURE__*/React.createElement(_customParams["default"], {
              typeCode: typeCode,
              form: form,
              view: view,
              template: temps
            })));
          }
        } else if (temps.dependencyValue) {
          var _form$getFieldValue2;

          var _dependencyValue = (_form$getFieldValue2 = form.getFieldValue("".concat(typeCode, ".componentConfig.").concat(temps.dependencyKey))) !== null && _form$getFieldValue2 !== void 0 ? _form$getFieldValue2 : '';

          if (_dependencyValue == (temps === null || temps === void 0 ? void 0 : temps.dependencyValue)) {
            return _this.renderConfigItem(temps);
          }
        } else {
          return _this.renderConfigItem(temps);
        }
      }), !isHaveGroup && template.length ? /*#__PURE__*/React.createElement(_customParams["default"], {
        typeCode: typeCode,
        form: form,
        view: view,
        template: template,
        maxWidth: 680
      }) : null);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderKubernetsConfig", function () {
      var _comp$componentTypeCo3, _ref, _form$getFieldValue3;

      var _this$props3 = _this.props,
          comp = _this$props3.comp,
          form = _this$props3.form;
      var typeCode = (_comp$componentTypeCo3 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo3 !== void 0 ? _comp$componentTypeCo3 : '';
      var config = (_ref = (_form$getFieldValue3 = form.getFieldValue("".concat(typeCode, ".specialConfig"))) !== null && _form$getFieldValue3 !== void 0 ? _form$getFieldValue3 : comp === null || comp === void 0 ? void 0 : comp.componentConfig) !== null && _ref !== void 0 ? _ref : '';
      return /*#__PURE__*/React.createElement(React.Fragment, null, config ? /*#__PURE__*/React.createElement("div", {
        className: "c-formConfig__kubernetsContent"
      }, "\u914D\u7F6E\u6587\u4EF6\u53C2\u6570\u5DF2\u88AB\u52A0\u5BC6\uFF0C\u6B64\u5904\u4E0D\u4E88\u663E\u793A") : null, form.getFieldDecorator("".concat(typeCode, ".specialConfig"), {
        initialValue: config || {}
      })( /*#__PURE__*/React.createElement(React.Fragment, null)));
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderYarnOrHdfsConfig", function () {
      var _comp$componentTypeCo4, _getValueByJson2, _getValueByJson3, _form$getFieldValue4;

      var _this$props4 = _this.props,
          comp = _this$props4.comp,
          view = _this$props4.view,
          form = _this$props4.form;
      var typeCode = (_comp$componentTypeCo4 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo4 !== void 0 ? _comp$componentTypeCo4 : '';
      var template = (_getValueByJson2 = (0, _help.getValueByJson)(comp === null || comp === void 0 ? void 0 : comp.componentTemplate)) !== null && _getValueByJson2 !== void 0 ? _getValueByJson2 : [];
      var compConfig = (_getValueByJson3 = (0, _help.getValueByJson)(comp === null || comp === void 0 ? void 0 : comp.componentConfig)) !== null && _getValueByJson3 !== void 0 ? _getValueByJson3 : {};
      var config = (_form$getFieldValue4 = form.getFieldValue("".concat(typeCode, ".specialConfig"))) !== null && _form$getFieldValue4 !== void 0 ? _form$getFieldValue4 : compConfig;
      var keyAndValue = Object.entries(config);
      return /*#__PURE__*/React.createElement(React.Fragment, null, keyAndValue.map(function (_ref2) {
        var _ref3 = (0, _slicedToArray2["default"])(_ref2, 2),
            key = _ref3[0],
            value = _ref3[1];

        return /*#__PURE__*/React.createElement(_row["default"], {
          key: key,
          className: "zipConfig-item"
        }, /*#__PURE__*/React.createElement(_col["default"], {
          className: "formitem-textname",
          span: _consts.formItemLayout.labelCol.sm.span + 2
        }, /*#__PURE__*/React.createElement(_tooltip["default"], {
          title: key,
          placement: "topRight"
        }, /*#__PURE__*/React.createElement("span", {
          className: "form-text-name"
        }, key)), /*#__PURE__*/React.createElement("span", null, "\uFF1A")), /*#__PURE__*/React.createElement(_col["default"], {
          className: "formitem-textvalue",
          span: _consts.formItemLayout.wrapperCol.sm.span + 1
        }, "".concat(value)));
      }), form.getFieldDecorator("".concat(typeCode, ".specialConfig"), {
        initialValue: config || {}
      })( /*#__PURE__*/React.createElement(React.Fragment, null)), keyAndValue.length > 0 ? /*#__PURE__*/React.createElement(_customParams["default"], {
        typeCode: typeCode,
        form: form,
        comp: comp,
        view: view,
        template: template,
        labelCol: _consts.formItemLayout.labelCol.sm.span + 2,
        wrapperCol: _consts.formItemLayout.wrapperCol.sm.span - 3
      }) : null);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderComponentsConfig", function () {
      var _comp$componentTypeCo5;

      var comp = _this.props.comp;
      var typeCode = (_comp$componentTypeCo5 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo5 !== void 0 ? _comp$componentTypeCo5 : '';

      switch (typeCode) {
        case _const.COMPONENT_TYPE_VALUE.YARN:
        case _const.COMPONENT_TYPE_VALUE.HDFS:
          return _this.renderYarnOrHdfsConfig();

        case _const.COMPONENT_TYPE_VALUE.KUBERNETES:
          return _this.renderKubernetsConfig();

        case _const.COMPONENT_TYPE_VALUE.SFTP:
        case _const.COMPONENT_TYPE_VALUE.TIDB_SQL:
        case _const.COMPONENT_TYPE_VALUE.LIBRA_SQL:
        case _const.COMPONENT_TYPE_VALUE.ORACLE_SQL:
        case _const.COMPONENT_TYPE_VALUE.IMPALA_SQL:
        case _const.COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL:
        case _const.COMPONENT_TYPE_VALUE.PRESTO_SQL:
        case _const.COMPONENT_TYPE_VALUE.FLINK:
        case _const.COMPONENT_TYPE_VALUE.SPARK:
        case _const.COMPONENT_TYPE_VALUE.DTYARNSHELL:
        case _const.COMPONENT_TYPE_VALUE.LEARNING:
        case _const.COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
        case _const.COMPONENT_TYPE_VALUE.NFS:
        case _const.COMPONENT_TYPE_VALUE.HIVE_SERVER:
          {
            return _this.rendeConfigForm();
          }

        default:
          return null;
      }
    });
    return _this;
  }

  (0, _createClass2["default"])(FormConfig, [{
    key: "render",
    value: function render() {
      return /*#__PURE__*/React.createElement("div", {
        className: "c-formConfig__container"
      }, this.renderComponentsConfig());
    }
  }]);
  return FormConfig;
}(React.PureComponent);

exports["default"] = FormConfig;

/***/ }),

/***/ "IdH2":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("zKZe");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.testStatus = testStatus;
exports.showRequireStatus = showRequireStatus;
exports.updateRequiredStatus = exports.updateTestStatus = void 0;

var _mirrorCreator = _interopRequireDefault(__webpack_require__("zbcj"));

var _index = __webpack_require__("RzPm");

var clusterActions = (0, _mirrorCreator["default"])(['UPDATE_TEST_RESULT', 'UPDATE_REQUIRED_STATUS']); // actions

var updateTestStatus = function updateTestStatus(data) {
  return {
    type: clusterActions.UPDATE_TEST_RESULT,
    data: data
  };
};

exports.updateTestStatus = updateTestStatus;

var updateRequiredStatus = function updateRequiredStatus(data) {
  return {
    type: clusterActions.UPDATE_REQUIRED_STATUS,
    data: data
  };
}; // reducer


exports.updateRequiredStatus = updateRequiredStatus;

function testStatus() {
  var state = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : _index.DEFAULT_COMP_TEST;
  var action = arguments.length > 1 ? arguments[1] : undefined;

  switch (action.type) {
    case clusterActions.UPDATE_TEST_RESULT:
      {
        var data = action.data;
        return Object.assign({}, state, data);
      }

    default:
      return state;
  }
}

function showRequireStatus() {
  var state = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : _index.DEFAULT_COMP_REQUIRED;
  var action = arguments.length > 1 ? arguments[1] : undefined;

  switch (action.type) {
    case clusterActions.UPDATE_REQUIRED_STATUS:
      {
        var data = action.data;
        return Object.assign({}, state, data);
      }

    default:
      return state;
  }
}

/***/ }),

/***/ "IiER":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("QWBl");

__webpack_require__("oVuX");

__webpack_require__("2B1R");

__webpack_require__("E9XD");

__webpack_require__("pDQq");

__webpack_require__("zKZe");

__webpack_require__("tkto");

__webpack_require__("rB9j");

__webpack_require__("EnZy");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.validateCompParams = validateCompParams;
exports.exChangeComponentConf = exChangeComponentConf;
exports.showTestResult = showTestResult;
exports.validateAllRequired = validateAllRequired;
exports.displayTaskStatus = displayTaskStatus;
exports.myUpperCase = myUpperCase;
exports.myLowerCase = myLowerCase;
exports.toChsKeys = toChsKeys;
exports.isHadoopEngine = isHadoopEngine;
exports.isLibraEngine = isLibraEngine;
exports.isTiDBEngine = isTiDBEngine;
exports.isOracleEngine = isOracleEngine;
exports.isGreenPlumEngine = isGreenPlumEngine;
exports.isKubernetesEngine = isKubernetesEngine;

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _index = __webpack_require__("RzPm");

// cluster function

/**
 * 返回不同组件校验参数
 * @param componentValue 组件
 */
function validateCompParams(componentValue) {
  switch (componentValue) {
    case _index.COMPONENT_TYPE_VALUE.FLINK:
      {
        return _index.validateFlinkParams;
      }

    case _index.COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
      {
        // hive <=> Spark Thrift Server
        console.log(_index.validateHiveParams);
        return _index.validateHiveParams;
      }

    case _index.COMPONENT_TYPE_VALUE.CARBONDATA:
      {
        return _index.validateCarbonDataParams;
      }

    case _index.COMPONENT_TYPE_VALUE.IMPALA_SQL:
      {
        return _index.validateImpalaSqlParams;
      }

    case _index.COMPONENT_TYPE_VALUE.SPARK:
      {
        return _index.validateSparkParams;
      }

    case _index.COMPONENT_TYPE_VALUE.DTYARNSHELL:
      {
        return _index.validateDtYarnShellParams;
      }

    case _index.COMPONENT_TYPE_VALUE.LEARNING:
      {
        return _index.validateLearningParams;
      }

    case _index.COMPONENT_TYPE_VALUE.HIVE_SERVER:
      {
        return _index.validateHiveServerParams;
      }

    case _index.COMPONENT_TYPE_VALUE.HDFS:
      {
        return [];
      }

    case _index.COMPONENT_TYPE_VALUE.YARN:
      {
        return [];
      }

    case _index.COMPONENT_TYPE_VALUE.LIBRA_SQL:
      {
        return _index.validateLibraParams;
      }

    case _index.COMPONENT_TYPE_VALUE.SFTP:
      {
        return _index.validateSftpDataParams;
      }

    default:
      {
        return null;
      }
  }
}
/**
 * hadoop,libra服务端数据转化
 * 接口数据一次全部返回，这里合并处理
 * @param hadoopComp hadoop参数配置项
 * @param libraComp libra参数配置项
 */


function exChangeComponentConf() {
  var hadoopComp = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : [];
  var libraComp = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : [];
  var tidbComp = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : [];
  var oracleComp = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : [];
  var greenPlumComp = arguments.length > 4 && arguments[4] !== undefined ? arguments[4] : [];
  var comp = hadoopComp.concat(libraComp, tidbComp, oracleComp, greenPlumComp);
  var componentConf = {};
  comp.map(function (item) {
    var componentTypeCode = item && item.componentTypeCode;
    componentConf = Object.assign(componentConf, (0, _defineProperty2["default"])({}, _index.COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode], item.config));
  });
  return componentConf;
}
/**
 * 引擎显示测试结果
 * @param testResults 测试结果
 * @param engineType 引擎类型
 */


function showTestResult(testResults, engineType) {
  var testStatus = {};
  var isHadoop = isHadoopEngine(engineType);
  testResults && testResults.map(function (comp) {
    switch (comp.componentTypeCode) {
      case _index.COMPONENT_TYPE_VALUE.FLINK:
        {
          testStatus = Object.assign(testStatus, {
            flinkTestResult: isHadoop ? comp : {} // 区分Hadoop, libra，单独显示

          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
        {
          testStatus = Object.assign(testStatus, {
            sparkThriftTestResult: isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.CARBONDATA:
        {
          testStatus = Object.assign(testStatus, {
            carbonTestResult: isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.IMPALA_SQL:
        {
          testStatus = Object.assign(testStatus, {
            impalaSqlTestResult: isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.SPARK:
        {
          testStatus = Object.assign(testStatus, {
            sparkTestResult: isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.DTYARNSHELL:
        {
          testStatus = Object.assign(testStatus, {
            dtYarnShellTestResult: isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.LEARNING:
        {
          testStatus = Object.assign(testStatus, {
            learningTestResult: isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.HDFS:
        {
          testStatus = Object.assign(testStatus, {
            hdfsTestResult: isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.YARN:
        {
          testStatus = Object.assign(testStatus, {
            yarnTestResult: isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.HIVE_SERVER:
        {
          testStatus = Object.assign(testStatus, {
            hiveServerTestResult: isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.LIBRA_SQL:
        {
          testStatus = Object.assign(testStatus, {
            libraSqlTestResult: !isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.TIDB_SQL:
        {
          testStatus = Object.assign(testStatus, {
            tidbSqlTestResult: !isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.ORACLE_SQL:
        {
          testStatus = Object.assign(testStatus, {
            oracleSqlTestResult: !isHadoop ? comp : {}
          });
          break;
        }

      case _index.COMPONENT_TYPE_VALUE.SFTP:
        {
          testStatus = Object.assign(testStatus, {
            sftpTestResult: isHadoop ? comp : {}
          });
          break;
        }

      default:
        {
          testStatus = Object.assign(testStatus, {});
        }
    }
  });
  return testStatus;
}
/**
 * 校验组件必填项未填标识
 * @param tabCompData 不同engine的组件数据
 */


function validateAllRequired(validateFieldsAndScroll, tabCompData) {
  var obj = {};
  tabCompData && tabCompData.map(function (item) {
    validateFieldsAndScroll(validateCompParams(item.componentTypeCode), {
      force: true,
      scroll: {
        offsetBottom: 150
      }
    }, function (err, values) {
      console.log(err, item);

      if (item.componentTypeCode == _index.COMPONENT_TYPE_VALUE.FLINK) {
        if (!err) {
          obj = Object.assign(obj, {
            flinkShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            flinkShowRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER) {
        if (!err) {
          obj = Object.assign(obj, {
            hiveShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            hiveShowRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.CARBONDATA) {
        if (!err) {
          obj = Object.assign(obj, {
            carbonShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            carbonShowRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.IMPALA_SQL) {
        if (!err) {
          obj = Object.assign(obj, {
            impalaSqlRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            impalaSqlRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.HIVE_SERVER) {
        if (!err) {
          obj = Object.assign(obj, {
            hiveServerShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            hiveServerShowRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.SPARK) {
        if (!err) {
          obj = Object.assign(obj, {
            sparkShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            sparkShowRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.DTYARNSHELL) {
        if (!err) {
          obj = Object.assign(obj, {
            dtYarnShellShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            dtYarnShellShowRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.LEARNING) {
        if (!err) {
          obj = Object.assign(obj, {
            learningShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            learningShowRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.HDFS) {
        if (!err) {
          obj = Object.assign(obj, {
            hdfsShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            hdfsShowRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.YARN) {
        if (!err) {
          obj = Object.assign(obj, {
            yarnShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            yarnShowRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.LIBRA_SQL) {
        if (!err) {
          obj = Object.assign(obj, {
            libraShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            libraShowRequired: true
          });
        }
      } else if (item.componentTypeCode === _index.COMPONENT_TYPE_VALUE.SFTP) {
        if (!err) {
          obj = Object.assign(obj, {
            sftpShowRequired: false
          });
        } else {
          obj = Object.assign(obj, {
            sftpShowRequired: true
          });
        }
      } else {
        console.log('error');
      }
    });
  });
  return obj;
}

function displayTaskStatus(status) {
  switch (status) {
    case _index.TASK_STATE.UNSUBMIT:
      return 'UNSUBMIT';

    case _index.TASK_STATE.CREATED:
      return 'CREATED';

    case _index.TASK_STATE.SCHEDULED:
      return 'SCHEDULED';

    case _index.TASK_STATE.DEPLOYING:
      return 'DEPLOYING';

    case _index.TASK_STATE.RUNNING:
      return 'RUNNING';

    case _index.TASK_STATE.FINISHED:
      return 'FINISHED';

    case _index.TASK_STATE.CANCELLING:
      return 'CANCELLING';

    case _index.TASK_STATE.CANCELED:
      return 'CANCELED';

    case _index.TASK_STATE.FAILED:
      return 'FAILED';

    case _index.TASK_STATE.SUBMITFAILD:
      return 'SUBMITFAILD';

    case _index.TASK_STATE.SUBMITTING:
      return 'SUBMITTING';

    case _index.TASK_STATE.RESTARTING:
      return 'RESTARTING';

    case _index.TASK_STATE.MANUALSUCCESS:
      return 'MANUALSUCCESS';

    case _index.TASK_STATE.KILLED:
      return 'KILLED';

    case _index.TASK_STATE.SUBMITTED:
      return 'SUBMITTED';

    case _index.TASK_STATE.NOTFOUND:
      return 'NOTFOUND';

    case _index.TASK_STATE.WAITENGINE:
      return 'WAITENGINE';

    case _index.TASK_STATE.WAITCOMPUTE:
      return 'WAITCOMPUTE';

    case _index.TASK_STATE.FROZEN:
      return 'FROZEN';

    case _index.TASK_STATE.ENGINEACCEPTED:
      return 'ENGINEACCEPTED';

    case _index.TASK_STATE.ENGINEDISTRIBUTE:
      return 'ENGINEDISTRIBUTE';

    case _index.TASK_STATE.COMPUTING:
      return 'COMPUTING';

    case _index.TASK_STATE.PARENTFAILED:
      return 'PARENTFAILED';

    case _index.TASK_STATE.FAILING:
      return 'FAILING';

    case _index.TASK_STATE.EXPIRE:
      return 'EXPIRE';

    default:
      return null;
  }
} // 表单字段. => 驼峰转化


function myUpperCase(obj) {
  var after = {};
  var keys = [];
  var values = [];
  var newKeys = []; // . --> 驼峰

  for (var i in obj) {
    if (obj.hasOwnProperty(i)) {
      keys.push(i);
      values.push(obj[i]);
    }
  }

  keys.forEach(function (item, index) {
    var itemSplit = item.split('.');
    var newItem = itemSplit[0];

    for (var _i = 1; _i < itemSplit.length; _i++) {
      var letters = itemSplit[_i].split('');

      var firstLetter = letters.shift();
      firstLetter = firstLetter.toUpperCase();
      letters.unshift(firstLetter);
      newItem += letters.join('');
    }

    newKeys[index] = newItem;
  });

  for (var _i2 = 0; _i2 < values.length; _i2++) {
    after[newKeys[_i2]] = values[_i2];
  }

  return after;
} // 驼峰 => .转化


function myLowerCase(obj) {
  var after = {};
  var alphabet = 'QWERTYUIOPLKJHGFDSAZXCVBNM';

  for (var i in obj) {
    var isKerberos = i === 'openKerberos' || i === 'kerberosFile';
    console.log(i);

    if (obj.hasOwnProperty(i) && !isKerberos) {
      var keySplit = void 0;
      keySplit = i.split('');

      for (var j = 0; j < keySplit.length; j++) {
        if (keySplit[j] == '.') {
          keySplit.splice(j, 1);
          keySplit[j] = keySplit[j].toUpperCase();
        } else if (alphabet.indexOf(keySplit[j]) != -1) {
          keySplit[j] = keySplit[j].toLowerCase();
          keySplit.splice(j, 0, '.');
          j++;
        }
      }

      var keySplitStr = keySplit.join(''); // keySplit = keySplit.join('');

      after[keySplitStr] = obj[i];
    } else {
      after[i] = obj[i];
    }
  }

  return after;
}
/**
 * PYspark两字段需转化(spark.yarn.appMasterEnv.PYSPARK_PYTHON,
 * spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON)
 * @param obj 传入对象
 * @param keyMap key映射关系
 */


function toChsKeys(obj, keyMap) {
  return Object.keys(obj).reduce(function (newObj, key) {
    var newKey = keyMap[key] || key;
    newObj[newKey] = obj[key];
    return newObj;
  }, {});
} // 是否是hadoop引擎


function isHadoopEngine(engineType) {
  return engineType == _index.ENGINE_TYPE.HADOOP;
} // 是否是Libra引擎


function isLibraEngine(engineType) {
  return engineType == _index.ENGINE_TYPE.LIBRA;
} // 是否是TiDB引擎


function isTiDBEngine(engineType) {
  return engineType == _index.ENGINE_TYPE.TI_DB;
}

function isOracleEngine(engineType) {
  return engineType == _index.ENGINE_TYPE.ORACLE;
}

function isGreenPlumEngine(engineType) {
  return engineType == _index.ENGINE_TYPE.GREEN_PLUM;
}

function isKubernetesEngine(resourceType) {
  return resourceType == _index.RESOURCE_TYPE.KUBERNETES;
}

/***/ }),

/***/ "JtgN":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("yXV3");

__webpack_require__("DQNa");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("rB9j");

__webpack_require__("JfAA");

__webpack_require__("UxlC");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _lodash = __webpack_require__("LvDl");

var echarts = _interopRequireWildcard(__webpack_require__("ProS"));

__webpack_require__("Ynxi");

__webpack_require__("AH3D");

__webpack_require__("zRKj");

__webpack_require__("0o9m");

__webpack_require__("75ce");

__webpack_require__("lLGD");

__webpack_require__("wDdD");

__webpack_require__("B+YJ");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var Chart = /*#__PURE__*/function (_React$PureComponent) {
  (0, _inherits2["default"])(Chart, _React$PureComponent);

  var _super = _createSuper(Chart);

  function Chart(props) {
    var _this;

    (0, _classCallCheck2["default"])(this, Chart);
    _this = _super.call(this, props);
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "chart", null);
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "styleFormat", function (param) {
      if (typeof param === 'string') {
        return param.indexOf('%') > -1 ? param : param.replace(/[px]/ig, '') + 'px';
      }

      return param + 'px';
    });
    _this.state = {
      myChart: null
    };
    return _this;
  }

  (0, _createClass2["default"])(Chart, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      var option = this.props.option;
      var myChart = echarts.init(this.chart);
      myChart.setOption(option);
      this.setState({
        myChart: myChart
      });
    }
  }, {
    key: "componentWillUnmount",
    value: function componentWillUnmount() {
      this.state.myChart.dispose();
    }
  }, {
    key: "componentDidUpdate",
    value: function componentDidUpdate(prevProps) {
      var option = this.props.option;

      if (!(0, _lodash.isEqual)(prevProps.option, option)) {
        this.state.myChart.setOption(option);
      }
    }
  }, {
    key: "render",
    value: function render() {
      var _this2 = this;

      var _this$props = this.props,
          width = _this$props.width,
          height = _this$props.height,
          style = _this$props.style;

      var styleParams = _objectSpread(_objectSpread({}, style), {}, {
        width: width ? this.styleFormat(width) : '100%',
        height: height ? this.styleFormat(height) : '300px'
      });

      return /*#__PURE__*/React.createElement("div", {
        ref: function ref(chart) {
          return _this2.chart = chart;
        },
        style: styleParams
      });
    }
  }]);
  return Chart;
}(React.PureComponent);

exports["default"] = Chart;

/***/ }),

/***/ "Jwcm":
/***/ (function(module, exports, __webpack_require__) {

var map = {
	"./af": "fGOk",
	"./af.js": "fGOk",
	"./ar": "NA4M",
	"./ar-dz": "aKgi",
	"./ar-dz.js": "aKgi",
	"./ar-kw": "n/mU",
	"./ar-kw.js": "n/mU",
	"./ar-ly": "pQX1",
	"./ar-ly.js": "pQX1",
	"./ar-ma": "w01r",
	"./ar-ma.js": "w01r",
	"./ar-sa": "wr7u",
	"./ar-sa.js": "wr7u",
	"./ar-tn": "1JqQ",
	"./ar-tn.js": "1JqQ",
	"./ar.js": "NA4M",
	"./az": "frUk",
	"./az.js": "frUk",
	"./be": "Kzdf",
	"./be.js": "Kzdf",
	"./bg": "kzMK",
	"./bg.js": "kzMK",
	"./bm": "dECj",
	"./bm.js": "dECj",
	"./bn": "2EUL",
	"./bn-bd": "zYqe",
	"./bn-bd.js": "zYqe",
	"./bn.js": "2EUL",
	"./bo": "P4WH",
	"./bo.js": "P4WH",
	"./br": "Nboc",
	"./br.js": "Nboc",
	"./bs": "w7cA",
	"./bs.js": "w7cA",
	"./ca": "7qF5",
	"./ca.js": "7qF5",
	"./cs": "kdFV",
	"./cs.js": "kdFV",
	"./cv": "d5nx",
	"./cv.js": "d5nx",
	"./cy": "oqmD",
	"./cy.js": "oqmD",
	"./da": "HRfI",
	"./da.js": "HRfI",
	"./de": "6Qh/",
	"./de-at": "dDk5",
	"./de-at.js": "dDk5",
	"./de-ch": "16v3",
	"./de-ch.js": "16v3",
	"./de.js": "6Qh/",
	"./dv": "clJt",
	"./dv.js": "clJt",
	"./el": "ghSt",
	"./el.js": "ghSt",
	"./en-au": "9dKt",
	"./en-au.js": "9dKt",
	"./en-ca": "RNfk",
	"./en-ca.js": "RNfk",
	"./en-gb": "/ZhO",
	"./en-gb.js": "/ZhO",
	"./en-ie": "eMWa",
	"./en-ie.js": "eMWa",
	"./en-il": "2PPo",
	"./en-il.js": "2PPo",
	"./en-in": "B8/a",
	"./en-in.js": "B8/a",
	"./en-nz": "UH3a",
	"./en-nz.js": "UH3a",
	"./en-sg": "hMLE",
	"./en-sg.js": "hMLE",
	"./eo": "Tm3F",
	"./eo.js": "Tm3F",
	"./es": "vk5j",
	"./es-do": "rsJe",
	"./es-do.js": "rsJe",
	"./es-mx": "1RNr",
	"./es-mx.js": "1RNr",
	"./es-us": "Xkex",
	"./es-us.js": "Xkex",
	"./es.js": "vk5j",
	"./et": "AWKA",
	"./et.js": "AWKA",
	"./eu": "Iu2c",
	"./eu.js": "Iu2c",
	"./fa": "pIT2",
	"./fa.js": "pIT2",
	"./fi": "Haq1",
	"./fi.js": "Haq1",
	"./fil": "7ktv",
	"./fil.js": "7ktv",
	"./fo": "KF/g",
	"./fo.js": "KF/g",
	"./fr": "iQnT",
	"./fr-ca": "20lO",
	"./fr-ca.js": "20lO",
	"./fr-ch": "HOuY",
	"./fr-ch.js": "HOuY",
	"./fr.js": "iQnT",
	"./fy": "L8HS",
	"./fy.js": "L8HS",
	"./ga": "JfHF",
	"./ga.js": "JfHF",
	"./gd": "Dpu1",
	"./gd.js": "Dpu1",
	"./gl": "ExjP",
	"./gl.js": "ExjP",
	"./gom-deva": "Eq6X",
	"./gom-deva.js": "Eq6X",
	"./gom-latn": "TDMO",
	"./gom-latn.js": "TDMO",
	"./gu": "8DYW",
	"./gu.js": "8DYW",
	"./he": "TfNJ",
	"./he.js": "TfNJ",
	"./hi": "LCJX",
	"./hi.js": "LCJX",
	"./hr": "mgyo",
	"./hr.js": "mgyo",
	"./hu": "jyAN",
	"./hu.js": "jyAN",
	"./hy-am": "gJ5D",
	"./hy-am.js": "gJ5D",
	"./id": "j8ll",
	"./id.js": "j8ll",
	"./is": "BwMI",
	"./is.js": "BwMI",
	"./it": "4qWY",
	"./it-ch": "koY9",
	"./it-ch.js": "koY9",
	"./it.js": "4qWY",
	"./ja": "Ed6V",
	"./ja.js": "Ed6V",
	"./jv": "E83j",
	"./jv.js": "E83j",
	"./ka": "h+9h",
	"./ka.js": "h+9h",
	"./kk": "jWaN",
	"./kk.js": "jWaN",
	"./km": "pB5h",
	"./km.js": "pB5h",
	"./kn": "zBYn",
	"./kn.js": "zBYn",
	"./ko": "muss",
	"./ko.js": "muss",
	"./ku": "aprn",
	"./ku.js": "aprn",
	"./ky": "BRrQ",
	"./ky.js": "BRrQ",
	"./lb": "bivp",
	"./lb.js": "bivp",
	"./lo": "3bJ2",
	"./lo.js": "3bJ2",
	"./lt": "fPIu",
	"./lt.js": "fPIu",
	"./lv": "aa9q",
	"./lv.js": "aa9q",
	"./me": "rnXJ",
	"./me.js": "rnXJ",
	"./mi": "akII",
	"./mi.js": "akII",
	"./mk": "/YAm",
	"./mk.js": "/YAm",
	"./ml": "6iKh",
	"./ml.js": "6iKh",
	"./mn": "AwXb",
	"./mn.js": "AwXb",
	"./mr": "YR9Z",
	"./mr.js": "YR9Z",
	"./ms": "K3fb",
	"./ms-my": "s4bu",
	"./ms-my.js": "s4bu",
	"./ms.js": "K3fb",
	"./mt": "RuBm",
	"./mt.js": "RuBm",
	"./my": "j4kB",
	"./my.js": "j4kB",
	"./nb": "XE/L",
	"./nb.js": "XE/L",
	"./ne": "lPYD",
	"./ne.js": "lPYD",
	"./nl": "IESz",
	"./nl-be": "1ks/",
	"./nl-be.js": "1ks/",
	"./nl.js": "IESz",
	"./nn": "zVTy",
	"./nn.js": "zVTy",
	"./oc-lnc": "xqAI",
	"./oc-lnc.js": "xqAI",
	"./pa-in": "BOSQ",
	"./pa-in.js": "BOSQ",
	"./pl": "ks4Z",
	"./pl.js": "ks4Z",
	"./pt": "ltco",
	"./pt-br": "fCRd",
	"./pt-br.js": "fCRd",
	"./pt.js": "ltco",
	"./ro": "LwTW",
	"./ro.js": "LwTW",
	"./ru": "0VC5",
	"./ru.js": "0VC5",
	"./sd": "WIT5",
	"./sd.js": "WIT5",
	"./se": "NYWr",
	"./se.js": "NYWr",
	"./si": "c3WN",
	"./si.js": "c3WN",
	"./sk": "aF4r",
	"./sk.js": "aF4r",
	"./sl": "Z6bS",
	"./sl.js": "Z6bS",
	"./sq": "hZCb",
	"./sq.js": "hZCb",
	"./sr": "Nf9U",
	"./sr-cyrl": "+EWi",
	"./sr-cyrl.js": "+EWi",
	"./sr.js": "Nf9U",
	"./ss": "FCc6",
	"./ss.js": "FCc6",
	"./sv": "oXDp",
	"./sv.js": "oXDp",
	"./sw": "aZ6y",
	"./sw.js": "aZ6y",
	"./ta": "qevR",
	"./ta.js": "qevR",
	"./te": "lGRI",
	"./te.js": "lGRI",
	"./tet": "KBOm",
	"./tet.js": "KBOm",
	"./tg": "lpa9",
	"./tg.js": "lpa9",
	"./th": "rroe",
	"./th.js": "rroe",
	"./tk": "8nxk",
	"./tk.js": "8nxk",
	"./tl-ph": "brQ1",
	"./tl-ph.js": "brQ1",
	"./tlh": "EDp5",
	"./tlh.js": "EDp5",
	"./tr": "W7ED",
	"./tr.js": "W7ED",
	"./tzl": "T4aH",
	"./tzl.js": "T4aH",
	"./tzm": "0Fko",
	"./tzm-latn": "uR89",
	"./tzm-latn.js": "uR89",
	"./tzm.js": "0Fko",
	"./ug-cn": "WWZ6",
	"./ug-cn.js": "WWZ6",
	"./uk": "bBYQ",
	"./uk.js": "bBYQ",
	"./ur": "nPf7",
	"./ur.js": "nPf7",
	"./uz": "aCsK",
	"./uz-latn": "6V5J",
	"./uz-latn.js": "6V5J",
	"./uz.js": "aCsK",
	"./vi": "j8C8",
	"./vi.js": "j8C8",
	"./x-pseudo": "lCME",
	"./x-pseudo.js": "lCME",
	"./yo": "k/U0",
	"./yo.js": "k/U0",
	"./zh-cn": "SYDO",
	"./zh-cn.js": "SYDO",
	"./zh-hk": "SPlU",
	"./zh-hk.js": "SPlU",
	"./zh-mo": "RCbV",
	"./zh-mo.js": "RCbV",
	"./zh-tw": "5wCU",
	"./zh-tw.js": "5wCU"
};


function webpackContext(req) {
	var id = webpackContextResolve(req);
	return __webpack_require__(id);
}
function webpackContextResolve(req) {
	if(!__webpack_require__.o(map, req)) {
		var e = new Error("Cannot find module '" + req + "'");
		e.code = 'MODULE_NOT_FOUND';
		throw e;
	}
	return map[req];
}
webpackContext.keys = function webpackContextKeys() {
	return Object.keys(map);
};
webpackContext.resolve = webpackContextResolve;
module.exports = webpackContext;
webpackContext.id = "Jwcm";

/***/ }),

/***/ "K4z+":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("fbCW");

__webpack_require__("x0AG");

__webpack_require__("QWBl");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var _extends2 = _interopRequireDefault(__webpack_require__("pVnL"));

var _table = _interopRequireDefault(__webpack_require__("DtFj"));

var _alert = _interopRequireDefault(__webpack_require__("ATwu"));

var _icon = _interopRequireDefault(__webpack_require__("Pbn2"));

var _input = _interopRequireDefault(__webpack_require__("iJl9"));

var _toConsumableArray2 = _interopRequireDefault(__webpack_require__("RIqP"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _select = _interopRequireDefault(__webpack_require__("FAat"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _consts = __webpack_require__("RzPm");

var _help = __webpack_require__("FZsQ");

var _lodash = __webpack_require__("LvDl");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var Option = _select["default"].Option;
var MAX_ACCOUNT_NUM = 20;

var BindAccountModal = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(BindAccountModal, _React$Component);

  var _super = _createSuper(BindAccountModal);

  function BindAccountModal(props) {
    var _this;

    (0, _classCallCheck2["default"])(this, BindAccountModal);
    _this = _super.call(this, props);
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "addTableData", function () {
      _this.setState(function (prevState) {
        return {
          tableData: [].concat((0, _toConsumableArray2["default"])(prevState.tableData), [{
            id: (0, _help.giveMeAKey)()
          }])
        };
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "deleteTableData", function (record) {
      var tableData = _this.state.tableData;
      var newTableData = (0, _lodash.cloneDeep)(tableData);
      newTableData = newTableData.filter(function (table) {
        return table.id !== record.id;
      });

      _this.setState({
        tableData: newTableData
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleValueChange", function (key, value, record) {
      var tableData = _this.state.tableData;
      var newTableData = (0, _lodash.cloneDeep)(tableData);
      var index = newTableData.findIndex(function (t) {
        return t.id == record.id;
      });
      if (index > -1) newTableData[index][key] = value;

      _this.setState({
        tableData: newTableData
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onCancel", function (e) {
      _this.props.onCancel(e);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onSubmit", function () {
      var _this$props = _this.props,
          form = _this$props.form,
          data = _this$props.data,
          userList = _this$props.userList,
          onOk = _this$props.onOk;
      var isEdit = data !== null && data !== undefined;

      if (!isEdit) {
        var tableData = _this.state.tableData;
        var newTableData = tableData.map(function (table) {
          var selectedUser = userList.find(function (u) {
            return u.userId == table.bindUserId;
          });
          return _objectSpread(_objectSpread({}, table), {}, {
            password: '',
            username: selectedUser === null || selectedUser === void 0 ? void 0 : selectedUser.userName,
            emali: selectedUser === null || selectedUser === void 0 ? void 0 : selectedUser.userName
          });
        });
        onOk(newTableData);
        return;
      }

      form.validateFields(function (err, user) {
        if (!err) {
          user.id = (0, _lodash.get)(data, 'id', '');
          user.bindUserId = '';
          var selectedUser = userList.find(function (u) {
            return u.userId == user.bindUserId;
          });

          if (selectedUser) {
            user.username = selectedUser.userName;
            user.email = selectedUser.userName;
            user.bindUserId = selectedUser.bindUserId;
          }

          onOk(user);
        }
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "initColumns", function () {
      var userList = _this.props.userList;
      return [{
        title: '产品账号',
        dataIndex: 'bindUserId',
        render: function render(text, record) {
          return /*#__PURE__*/React.createElement(_select["default"], {
            defaultValue: text,
            style: {
              width: 160
            },
            onChange: function onChange(value) {
              return _this.handleValueChange('bindUserId', value, record);
            }
          }, userList.map(function (user) {
            return /*#__PURE__*/React.createElement(Option, {
              key: "".concat(user.userId),
              value: user.userId
            }, user.userName);
          }));
        }
      }, {
        title: 'LDAP账号',
        dataIndex: 'name',
        render: function render(text, record) {
          return /*#__PURE__*/React.createElement(_input["default"], {
            onChange: function onChange(e) {
              return _this.handleValueChange('name', e.target.value, record);
            },
            style: {
              width: 160
            },
            value: text
          });
        }
      }, {
        title: null,
        dataIndex: 'action',
        width: 40,
        render: function render(text, record) {
          return /*#__PURE__*/React.createElement(_icon["default"], {
            type: "delete",
            onClick: function onClick() {
              return _this.deleteTableData(record);
            }
          });
        }
      }];
    });
    _this.state = {
      tableData: [{
        id: (0, _help.giveMeAKey)()
      }]
    };
    return _this;
  }

  (0, _createClass2["default"])(BindAccountModal, [{
    key: "render",
    value: function render() {
      var tableData = this.state.tableData;
      var _this$props2 = this.props,
          title = _this$props2.title,
          visible = _this$props2.visible,
          data = _this$props2.data;
      var getFieldDecorator = this.props.form.getFieldDecorator;
      return /*#__PURE__*/React.createElement(_modal["default"], {
        className: "no-padding-modal",
        closable: true,
        title: title,
        visible: visible,
        onCancel: this.onCancel,
        onOk: this.onSubmit
      }, /*#__PURE__*/React.createElement(_alert["default"], {
        style: {
          border: 0
        },
        message: "\u4EA7\u54C1\u8D26\u53F7\u7ED1\u5B9ALDAP\u8D26\u53F7\u540E\u4EFB\u52A1\u63D0\u4EA4\u81F3Yarn\u4E0A\u5C06\u5BF9\u63A5\u7528\u6237",
        type: "info",
        showIcon: true
      }), /*#__PURE__*/React.createElement("div", {
        style: {
          padding: '12px 20px'
        }
      }, !data ? /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_table["default"], {
        className: "c-ldap__bindModal",
        columns: this.initColumns(),
        dataSource: tableData,
        pagination: false
      }), tableData.length > MAX_ACCOUNT_NUM ? /*#__PURE__*/React.createElement("a", {
        style: {
          color: '#BFBFBF'
        }
      }, /*#__PURE__*/React.createElement(_icon["default"], {
        type: "plus"
      }), "\u65B0\u589E") : /*#__PURE__*/React.createElement("a", {
        style: {
          color: '#3F87FF'
        },
        onClick: this.addTableData
      }, /*#__PURE__*/React.createElement(_icon["default"], {
        type: "plus"
      }), "\u65B0\u589E")) : /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_form["default"], null, /*#__PURE__*/React.createElement(_form["default"].Item, (0, _extends2["default"])({
        key: "bindUserId",
        label: "\u4EA7\u54C1\u8D26\u53F7"
      }, _consts.formItemLayout), getFieldDecorator('bindUserId', {
        rules: [{
          required: true,
          message: '产品账号不可为空！'
        }],
        initialValue: (0, _lodash.get)(data, 'username', '')
      })( /*#__PURE__*/React.createElement(_input["default"], {
        disabled: true
      }))), /*#__PURE__*/React.createElement(_form["default"].Item, (0, _extends2["default"])({
        key: "bindLdapAccount",
        label: "LDAP\u8D26\u53F7"
      }, _consts.formItemLayout), getFieldDecorator('name', {
        rules: [{
          required: true,
          message: 'LDAP账号不可为空！'
        }],
        initialValue: (0, _lodash.get)(data, 'name', '')
      })( /*#__PURE__*/React.createElement(_input["default"], null)))))));
    }
  }]);
  return BindAccountModal;
}(React.Component);

var _default = _form["default"].create()(BindAccountModal);

exports["default"] = _default;

/***/ }),

/***/ "KQ7z":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

var _interopRequireWildcard = __webpack_require__("284h");

__webpack_require__("DQNa");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _propTypes = _interopRequireDefault(__webpack_require__("17x9"));

var _reactRedux = __webpack_require__("/MKj");

var _header = _interopRequireDefault(__webpack_require__("BSqx"));

var _app = __webpack_require__("q+0Y");

var _defaultApps = __webpack_require__("IB8W");

var _dec, _class, _class2, _temp;

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var propType = {
  children: _propTypes["default"].node
};
var defaultPro = {
  children: []
};

var mapStateToProps = function mapStateToProps(state) {
  var common = state.common;
  return {
    common: common
  };
};

var
/* eslint-disable */
Main = (_dec = (0, _reactRedux.connect)(mapStateToProps), _dec(_class = (_temp = _class2 = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(Main, _React$Component);

  var _super = _createSuper(Main);

  function Main() {
    (0, _classCallCheck2["default"])(this, Main);
    return _super.apply(this, arguments);
  }

  (0, _createClass2["default"])(Main, [{
    key: "componentWillMount",
    value: function componentWillMount() {
      var dispatch = this.props.dispatch;
    }
  }, {
    key: "componentDidMount",
    value: function componentDidMount() {
      var dispatch = this.props.dispatch;
      dispatch((0, _app.updateApp)(_defaultApps.consoleApp));
    }
  }, {
    key: "render",
    value: function render() {
      var children = this.props.children;
      var header = /*#__PURE__*/React.createElement(_header["default"], null);
      return /*#__PURE__*/React.createElement("div", {
        className: "main"
      }, header, /*#__PURE__*/React.createElement("div", {
        className: "container overflow-x-hidden",
        id: "JS_console_container"
      }, children || '加载中....'));
    }
  }]);
  return Main;
}(React.Component), (0, _defineProperty2["default"])(_class2, "defaultProps", defaultPro), (0, _defineProperty2["default"])(_class2, "propTypes", propType), _temp)) || _class); // Main.propTypes = propType
// Main.defaultProps = defaultPro

var _default = Main;
exports["default"] = _default;

/***/ }),

/***/ "KnRw":
/***/ (function(module, exports, __webpack_require__) {

var map = {
	"./af": "/Tvm",
	"./af.js": "/Tvm",
	"./ar": "yRKQ",
	"./ar-dz": "2Mgn",
	"./ar-dz.js": "2Mgn",
	"./ar-kw": "SyK9",
	"./ar-kw.js": "SyK9",
	"./ar-ly": "Km2t",
	"./ar-ly.js": "Km2t",
	"./ar-ma": "k+3Q",
	"./ar-ma.js": "k+3Q",
	"./ar-sa": "dbzX",
	"./ar-sa.js": "dbzX",
	"./ar-tn": "E7L0",
	"./ar-tn.js": "E7L0",
	"./ar.js": "yRKQ",
	"./az": "marO",
	"./az.js": "marO",
	"./be": "c9Cq",
	"./be.js": "c9Cq",
	"./bg": "Rxhz",
	"./bg.js": "Rxhz",
	"./bm": "Zf0k",
	"./bm.js": "Zf0k",
	"./bn": "qbSa",
	"./bn-bd": "Dicg",
	"./bn-bd.js": "Dicg",
	"./bn.js": "qbSa",
	"./bo": "aPmz",
	"./bo.js": "aPmz",
	"./br": "V/F2",
	"./br.js": "V/F2",
	"./bs": "CToL",
	"./bs.js": "CToL",
	"./ca": "GuxH",
	"./ca.js": "GuxH",
	"./cs": "qrel",
	"./cs.js": "qrel",
	"./cv": "wv9F",
	"./cv.js": "wv9F",
	"./cy": "7JVd",
	"./cy.js": "7JVd",
	"./da": "PtRy",
	"./da.js": "PtRy",
	"./de": "xhqU",
	"./de-at": "bJtj",
	"./de-at.js": "bJtj",
	"./de-ch": "fRjz",
	"./de-ch.js": "fRjz",
	"./de.js": "xhqU",
	"./dv": "tEna",
	"./dv.js": "tEna",
	"./el": "vE8+",
	"./el.js": "vE8+",
	"./en-au": "aGvD",
	"./en-au.js": "aGvD",
	"./en-ca": "TyIU",
	"./en-ca.js": "TyIU",
	"./en-gb": "xbvL",
	"./en-gb.js": "xbvL",
	"./en-ie": "8wpQ",
	"./en-ie.js": "8wpQ",
	"./en-il": "O112",
	"./en-il.js": "O112",
	"./en-in": "vfvX",
	"./en-in.js": "vfvX",
	"./en-nz": "mRnY",
	"./en-nz.js": "mRnY",
	"./en-sg": "fIpc",
	"./en-sg.js": "fIpc",
	"./eo": "nEfC",
	"./eo.js": "nEfC",
	"./es": "QqXE",
	"./es-do": "OH6n",
	"./es-do.js": "OH6n",
	"./es-mx": "QajH",
	"./es-mx.js": "QajH",
	"./es-us": "VqXm",
	"./es-us.js": "VqXm",
	"./es.js": "QqXE",
	"./et": "/BjE",
	"./et.js": "/BjE",
	"./eu": "lS/Y",
	"./eu.js": "lS/Y",
	"./fa": "Xl2a",
	"./fa.js": "Xl2a",
	"./fi": "pKmA",
	"./fi.js": "pKmA",
	"./fil": "zqRz",
	"./fil.js": "zqRz",
	"./fo": "1sIW",
	"./fo.js": "1sIW",
	"./fr": "t+gs",
	"./fr-ca": "ApVP",
	"./fr-ca.js": "ApVP",
	"./fr-ch": "mR/r",
	"./fr-ch.js": "mR/r",
	"./fr.js": "t+gs",
	"./fy": "zrXP",
	"./fy.js": "zrXP",
	"./ga": "oh3e",
	"./ga.js": "oh3e",
	"./gd": "7WNt",
	"./gd.js": "7WNt",
	"./gl": "EkHg",
	"./gl.js": "EkHg",
	"./gom-deva": "PB9U",
	"./gom-deva.js": "PB9U",
	"./gom-latn": "IlNN",
	"./gom-latn.js": "IlNN",
	"./gu": "SIlg",
	"./gu.js": "SIlg",
	"./he": "RWgf",
	"./he.js": "RWgf",
	"./hi": "fVgC",
	"./hi.js": "fVgC",
	"./hr": "QeL5",
	"./hr.js": "QeL5",
	"./hu": "UGq4",
	"./hu.js": "UGq4",
	"./hy-am": "powx",
	"./hy-am.js": "powx",
	"./id": "+gpe",
	"./id.js": "+gpe",
	"./is": "3azS",
	"./is.js": "3azS",
	"./it": "T/vC",
	"./it-ch": "BQHH",
	"./it-ch.js": "BQHH",
	"./it.js": "T/vC",
	"./ja": "n/+T",
	"./ja.js": "n/+T",
	"./jv": "pOn/",
	"./jv.js": "pOn/",
	"./ka": "5BLU",
	"./ka.js": "5BLU",
	"./kk": "i4p7",
	"./kk.js": "i4p7",
	"./km": "Rdvl",
	"./km.js": "Rdvl",
	"./kn": "x+k7",
	"./kn.js": "x+k7",
	"./ko": "+Fhz",
	"./ko.js": "+Fhz",
	"./ku": "4wLu",
	"./ku.js": "4wLu",
	"./ky": "dDLa",
	"./ky.js": "dDLa",
	"./lb": "Gudj",
	"./lb.js": "Gudj",
	"./lo": "VShb",
	"./lo.js": "VShb",
	"./lt": "5eMR",
	"./lt.js": "5eMR",
	"./lv": "JdG9",
	"./lv.js": "JdG9",
	"./me": "VVnP",
	"./me.js": "VVnP",
	"./mi": "oQ6b",
	"./mi.js": "oQ6b",
	"./mk": "tXUT",
	"./mk.js": "tXUT",
	"./ml": "uPxb",
	"./ml.js": "uPxb",
	"./mn": "p9cx",
	"./mn.js": "p9cx",
	"./mr": "b8N1",
	"./mr.js": "b8N1",
	"./ms": "Wnnz",
	"./ms-my": "b+tO",
	"./ms-my.js": "b+tO",
	"./ms.js": "Wnnz",
	"./mt": "V6YG",
	"./mt.js": "V6YG",
	"./my": "rt8X",
	"./my.js": "rt8X",
	"./nb": "xyXD",
	"./nb.js": "xyXD",
	"./ne": "4vTz",
	"./ne.js": "4vTz",
	"./nl": "6QMr",
	"./nl-be": "LMGY",
	"./nl-be.js": "LMGY",
	"./nl.js": "6QMr",
	"./nn": "Qsvx",
	"./nn.js": "Qsvx",
	"./oc-lnc": "Q7AQ",
	"./oc-lnc.js": "Q7AQ",
	"./pa-in": "oA7N",
	"./pa-in.js": "oA7N",
	"./pl": "C/jI",
	"./pl.js": "C/jI",
	"./pt": "V7OV",
	"./pt-br": "b3iz",
	"./pt-br.js": "b3iz",
	"./pt.js": "V7OV",
	"./ro": "TwjK",
	"./ro.js": "TwjK",
	"./ru": "YJSy",
	"./ru.js": "YJSy",
	"./sd": "4xdR",
	"./sd.js": "4xdR",
	"./se": "j5S3",
	"./se.js": "j5S3",
	"./si": "Am5L",
	"./si.js": "Am5L",
	"./sk": "sSmc",
	"./sk.js": "sSmc",
	"./sl": "9iY4",
	"./sl.js": "9iY4",
	"./sq": "cnMZ",
	"./sq.js": "cnMZ",
	"./sr": "ss7R",
	"./sr-cyrl": "tGLK",
	"./sr-cyrl.js": "tGLK",
	"./sr.js": "ss7R",
	"./ss": "mYhl",
	"./ss.js": "mYhl",
	"./sv": "IGtO",
	"./sv.js": "IGtO",
	"./sw": "p49z",
	"./sw.js": "p49z",
	"./ta": "VVil",
	"./ta.js": "VVil",
	"./te": "YQaq",
	"./te.js": "YQaq",
	"./tet": "2u18",
	"./tet.js": "2u18",
	"./tg": "oil7",
	"./tg.js": "oil7",
	"./th": "gJLt",
	"./th.js": "gJLt",
	"./tk": "IxH1",
	"./tk.js": "IxH1",
	"./tl-ph": "ogwM",
	"./tl-ph.js": "ogwM",
	"./tlh": "0bHV",
	"./tlh.js": "0bHV",
	"./tr": "BYDG",
	"./tr.js": "BYDG",
	"./tzl": "IUMd",
	"./tzl.js": "IUMd",
	"./tzm": "94jC",
	"./tzm-latn": "Mgge",
	"./tzm-latn.js": "Mgge",
	"./tzm.js": "94jC",
	"./ug-cn": "4xjM",
	"./ug-cn.js": "4xjM",
	"./uk": "NrFT",
	"./uk.js": "NrFT",
	"./ur": "U58I",
	"./ur.js": "U58I",
	"./uz": "orAx",
	"./uz-latn": "jCvl",
	"./uz-latn.js": "jCvl",
	"./uz.js": "orAx",
	"./vi": "t2dB",
	"./vi.js": "t2dB",
	"./x-pseudo": "XFnJ",
	"./x-pseudo.js": "XFnJ",
	"./yo": "qKGQ",
	"./yo.js": "qKGQ",
	"./zh-cn": "3O3J",
	"./zh-cn.js": "3O3J",
	"./zh-hk": "Fu0X",
	"./zh-hk.js": "Fu0X",
	"./zh-mo": "sCN4",
	"./zh-mo.js": "sCN4",
	"./zh-tw": "2Pdx",
	"./zh-tw.js": "2Pdx"
};


function webpackContext(req) {
	var id = webpackContextResolve(req);
	return __webpack_require__(id);
}
function webpackContextResolve(req) {
	if(!__webpack_require__.o(map, req)) {
		var e = new Error("Cannot find module '" + req + "'");
		e.code = 'MODULE_NOT_FOUND';
		throw e;
	}
	return map[req];
}
webpackContext.keys = function webpackContextKeys() {
	return Object.keys(map);
};
webpackContext.resolve = webpackContextResolve;
module.exports = webpackContext;
webpackContext.id = "KnRw";

/***/ }),

/***/ "L56I":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("wLYn");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("R5XZ");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = exports.RedTxt = void 0;

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _taggedTemplateLiteral2 = _interopRequireDefault(__webpack_require__("VkAN"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _styledComponents = _interopRequireDefault(__webpack_require__("9ObM"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

function _templateObject() {
  var data = (0, _taggedTemplateLiteral2["default"])(["\n    color: #FF5F5C;\n"]);

  _templateObject = function _templateObject() {
    return data;
  };

  return data;
}

var RedTxt = _styledComponents["default"].span(_templateObject());

exports.RedTxt = RedTxt;

var KillAllTask = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(KillAllTask, _React$Component);

  var _super = _createSuper(KillAllTask);

  function KillAllTask() {
    var _this;

    (0, _classCallCheck2["default"])(this, KillAllTask);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      confirmLoading: false
    });
    return _this;
  }

  (0, _createClass2["default"])(KillAllTask, [{
    key: "killTask",
    // 请求杀任务接口
    value: function killTask() {
      var _this2 = this;

      var _this$props$killResou = this.props.killResource,
          killResource = _this$props$killResou === void 0 ? [] : _this$props$killResou;
      var params = this.getReqParams();

      _console["default"].killTasks(params).then(function (res) {
        if (res.code == 1) {
          // 杀死全部任务为异步有延迟，需要延迟执行刷新数据操作
          setTimeout(function () {
            _message2["default"].success('操作成功');

            _this2.props.killSuccess(killResource);

            _this2.props.autoRefresh();

            _this2.props.onCancel();

            _this2.setState({
              confirmLoading: false
            });
          }, 1000);
        } else {
          _this2.setState({
            confirmLoading: false
          });
        }
      });
    }
  }, {
    key: "getReqParams",
    value: function getReqParams() {
      var _this$props = this.props,
          _this$props$killResou2 = _this$props.killResource,
          killResource = _this$props$killResou2 === void 0 ? [] : _this$props$killResou2,
          node = _this$props.node,
          stage = _this$props.stage,
          jobResource = _this$props.jobResource,
          totalModel = _this$props.totalModel;
      var params = {
        stage: stage,
        jobResource: jobResource,
        jobIdList: [],
        nodeAddress: node
      };
      var isKillAll = totalModel !== undefined;

      if (isKillAll) {
        params.jobIdList = []; // Kill All when array is null
      } else {
        // 杀死选中的任务
        params.jobIdList = killResource.map(function (job) {
          return job.jobId;
        });
      }

      console.log('params:', params);
      return params;
    }
  }, {
    key: "confirmKillTask",
    value: function confirmKillTask() {
      this.setState({
        confirmLoading: true
      });
      this.killTask();
    }
  }, {
    key: "render",
    value: function render() {
      var totalModel = this.props.totalModel;
      var isKillAll = totalModel !== undefined;
      var title = isKillAll ? "\u6740\u6B7B\u5168\u90E8\u4EFB\u52A1" : "\u6740\u6B7B\u9009\u4E2D\u4EFB\u52A1";
      var htmlText = isKillAll ? /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement(RedTxt, null, "\u672C\u64CD\u4F5C\u5C06\u6740\u6B7B\u5217\u8868\uFF08\u8DE8\u5206\u9875\uFF09\u4E2D\u7684\u5168\u90E8\u4EFB\u52A1\uFF0C\u4E0D\u4EC5\u662F\u5F53\u524D\u9875"), /*#__PURE__*/React.createElement("br", null), /*#__PURE__*/React.createElement(RedTxt, null, "\u6740\u6B7B\u8FD0\u884C\u4E2D\u7684\u4EFB\u52A1\u9700\u8981\u8F83\u957F\u65F6\u95F4")) : /*#__PURE__*/React.createElement(RedTxt, null, "\u672C\u64CD\u4F5C\u5C06\u6740\u6B7B\u5217\u8868\uFF08\u975E\u8DE8\u5206\u9875\uFF09\u4E2D\u7684\u9009\u4E2D\u4EFB\u52A1");
      return /*#__PURE__*/React.createElement(_modal["default"], {
        title: title,
        visible: this.props.visible,
        okText: title,
        okType: "danger",
        confirmLoading: this.state.confirmLoading,
        onCancel: this.props.onCancel,
        onOk: this.confirmKillTask.bind(this)
      }, htmlText);
    }
  }]);
  return KillAllTask;
}(React.Component);

var _default = KillAllTask;
exports["default"] = _default;

/***/ }),

/***/ "LNB4":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("4Brf");

__webpack_require__("0oug");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("pjDv");

__webpack_require__("yXV3");

__webpack_require__("J30X");

__webpack_require__("4mDm");

__webpack_require__("oVuX");

__webpack_require__("2B1R");

__webpack_require__("+2oP");

__webpack_require__("DQNa");

__webpack_require__("sMBO");

__webpack_require__("qePV");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("T63A");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("B6y2");

__webpack_require__("rB9j");

__webpack_require__("JfAA");

__webpack_require__("YGK4");

__webpack_require__("PKPk");

__webpack_require__("EnZy");

__webpack_require__("FZtP");

__webpack_require__("3bBZ");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.isNeedTemp = isNeedTemp;
exports.isKubernetes = isKubernetes;
exports.isHaveGroup = isHaveGroup;
exports.getActionType = getActionType;
exports.isSourceTab = isSourceTab;
exports.initialScheduling = initialScheduling;
exports.giveMeAKey = giveMeAKey;
exports.isViewMode = isViewMode;
exports.isFileParam = isFileParam;
exports.isOtherVersion = isOtherVersion;
exports.isSameVersion = isSameVersion;
exports.getCustomerParams = getCustomerParams;
exports.getCompsId = getCompsId;
exports.getValueByJson = getValueByJson;
exports.needZipFile = needZipFile;
exports.handleCustomParam = handleCustomParam;
exports.getParamsByTemp = getParamsByTemp;
exports.handleComponentTemplate = handleComponentTemplate;
exports.handleComponentConfig = handleComponentConfig;
exports.handleComponentConfigAndCustom = handleComponentConfigAndCustom;
exports.getInitialComp = getInitialComp;
exports.getModifyComp = getModifyComp;

var _toConsumableArray2 = _interopRequireDefault(__webpack_require__("RIqP"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _slicedToArray2 = _interopRequireDefault(__webpack_require__("J4zp"));

var _lodash = _interopRequireDefault(__webpack_require__("LvDl"));

var _const = __webpack_require__("j1Tt");

function _createForOfIteratorHelper(o, allowArrayLike) { var it; if (typeof Symbol === "undefined" || o[Symbol.iterator] == null) { if (Array.isArray(o) || (it = _unsupportedIterableToArray(o)) || allowArrayLike && o && typeof o.length === "number") { if (it) o = it; var i = 0; var F = function F() {}; return { s: F, n: function n() { if (i >= o.length) return { done: true }; return { done: false, value: o[i++] }; }, e: function e(_e) { throw _e; }, f: F }; } throw new TypeError("Invalid attempt to iterate non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); } var normalCompletion = true, didErr = false, err; return { s: function s() { it = o[Symbol.iterator](); }, n: function n() { var step = it.next(); normalCompletion = step.done; return step; }, e: function e(_e2) { didErr = true; err = _e2; }, f: function f() { try { if (!normalCompletion && it["return"] != null) it["return"](); } finally { if (didErr) throw err; } } }; }

function _unsupportedIterableToArray(o, minLen) { if (!o) return; if (typeof o === "string") return _arrayLikeToArray(o, minLen); var n = Object.prototype.toString.call(o).slice(8, -1); if (n === "Object" && o.constructor) n = o.constructor.name; if (n === "Map" || n === "Set") return Array.from(o); if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen); }

function _arrayLikeToArray(arr, len) { if (len == null || len > arr.length) len = arr.length; for (var i = 0, arr2 = new Array(len); i < len; i++) { arr2[i] = arr[i]; } return arr2; }

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

// 是否为yarn、hdfs、Kubernetes组件
function isNeedTemp(typeCode) {
  return [_const.COMPONENT_TYPE_VALUE.YARN, _const.COMPONENT_TYPE_VALUE.HDFS, _const.COMPONENT_TYPE_VALUE.KUBERNETES].indexOf(typeCode) > -1;
}

function isKubernetes(typeCode) {
  return _const.COMPONENT_TYPE_VALUE.KUBERNETES == typeCode;
}

function isHaveGroup(typeCode) {
  return [_const.COMPONENT_TYPE_VALUE.FLINK, _const.COMPONENT_TYPE_VALUE.SPARK, _const.COMPONENT_TYPE_VALUE.LEARNING, _const.COMPONENT_TYPE_VALUE.DTYARNSHELL].indexOf(typeCode) > -1;
}

function getActionType(mode) {
  switch (mode) {
    case 'view':
      return '查看集群';

    case 'new':
      return '新增集群';

    case 'edit':
      return '编辑集群';

    default:
      return '';
  }
}

function isSourceTab(activeKey) {
  return activeKey == _const.TABS_TITLE_KEY.SOURCE;
}

function initialScheduling() {
  var arr = [];
  return Object.values(_const.TABS_TITLE_KEY).map(function (tabKey) {
    return arr[tabKey] = [];
  });
}

function giveMeAKey() {
  return new Date().getTime() + '' + ~~(Math.random() * 100000);
}

function isViewMode(mode) {
  return mode == 'view';
}

function isFileParam(key) {
  return ['kerberosFileName', 'uploadFileName'].indexOf(key) > -1;
}

function isOtherVersion(code) {
  return [_const.COMPONENT_TYPE_VALUE.FLINK, _const.COMPONENT_TYPE_VALUE.SPARK, _const.COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER, _const.COMPONENT_TYPE_VALUE.HIVE_SERVER].indexOf(code) > -1;
}

function isSameVersion(code) {
  return [_const.COMPONENT_TYPE_VALUE.HDFS, _const.COMPONENT_TYPE_VALUE.YARN].indexOf(code) > -1;
} // 模版中存在id则为自定义参数


function getCustomerParams(temps) {
  return temps.filter(function (temp) {
    return temp.id;
  });
}

function getCompsId(currentComps, typeCodes) {
  var ids = [];
  currentComps.forEach(function (comp) {
    if (typeCodes.indexOf(comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) > -1 && (comp === null || comp === void 0 ? void 0 : comp.id)) {
      ids.push(comp.id);
    }
  });
  return ids;
}

function getValueByJson(value) {
  return value ? JSON.parse(value) : null;
}

function needZipFile(type) {
  return [_const.FILE_TYPE.KERNEROS, _const.FILE_TYPE.CONFIGS].indexOf(type) > -1;
}
/**
 * @param param
 * 处理单条自定义参数的key\value值
 * 处理数据结构为%1532398855125918-key, %1532398855125918-value
 * 返回数据结构[{key: key, value: value, id: id}]
 */


function handleSingleParam(params) {
  var customParamArr = [];
  var customParamConfig = [];
  if (!params) return {};

  for (var _i = 0, _Object$entries = Object.entries(params); _i < _Object$entries.length; _i++) {
    var _Object$entries$_i = (0, _slicedToArray2["default"])(_Object$entries[_i], 2),
        keys = _Object$entries$_i[0],
        values = _Object$entries$_i[1];

    if (values && _lodash["default"].isString(values) && _lodash["default"].isString(keys)) {
      var p = keys.split('%')[1].split('-');
      customParamArr[p[0]] = _objectSpread(_objectSpread({}, customParamArr[p[0]]), {}, (0, _defineProperty2["default"])({}, p[1], values));
    }
  }

  for (var key in customParamArr) {
    var config = {};
    config.key = customParamArr[key].key;
    config.value = customParamArr[key].value;
    config.id = key;
    customParamConfig.push(config);
  }

  return customParamConfig;
}
/**
 * @param param 自定义参数对象
 * @param turnp 转化为{key:value}型数据，仅支持不含group类型组组件
 * 先组内处理自定义参数再处理普通类型的自定义参数
 * 返回数据结构
 * [{
 *  group: {
 *    key: key,
 *    value: value
 *  }
 * }]
 */


function handleCustomParam(params, turnp) {
  var customParam = [];
  if (!params) return {};

  for (var _i2 = 0, _Object$entries2 = Object.entries(params); _i2 < _Object$entries2.length; _i2++) {
    var _Object$entries2$_i = (0, _slicedToArray2["default"])(_Object$entries2[_i2], 2),
        key = _Object$entries2$_i[0],
        value = _Object$entries2$_i[1];

    if (value && !_lodash["default"].isString(value)) {
      customParam = [].concat((0, _toConsumableArray2["default"])(customParam), [(0, _defineProperty2["default"])({}, key, handleSingleParam(value))]);
    }
  }

  if (turnp) {
    var config = {};

    var _iterator = _createForOfIteratorHelper(customParam.concat(handleSingleParam(params))),
        _step;

    try {
      for (_iterator.s(); !(_step = _iterator.n()).done;) {
        var item = _step.value;
        config[item.key] = item.value;
      }
    } catch (err) {
      _iterator.e(err);
    } finally {
      _iterator.f();
    }

    return config;
  }

  return customParam.concat(handleSingleParam(params));
}
/**
 * @param temp 初始模版值
 * 处理初始模版值返回只包含自定义参数的键值
 * 返回结构如下
 * {
 *   %1532398855125918-key: key,
 *   %1532398855125918-value: value,
 *     group: {
 *        %1532398855125918-key: key,
 *        %1532398855125918-value: value,
 *     }
 * }
 */


function getParamsByTemp(temp) {
  var batchParams = {};
  temp.forEach(function (item) {
    if (item.type == _const.CONFIG_ITEM_TYPE.GROUP) {
      var params = {};
      item.values.forEach(function (groupItem) {
        if (groupItem.id) {
          var _groupItem$key, _groupItem$value;

          params['%' + groupItem.id + '-key'] = (_groupItem$key = groupItem === null || groupItem === void 0 ? void 0 : groupItem.key) !== null && _groupItem$key !== void 0 ? _groupItem$key : '';
          params['%' + groupItem.id + '-value'] = (_groupItem$value = groupItem === null || groupItem === void 0 ? void 0 : groupItem.value) !== null && _groupItem$value !== void 0 ? _groupItem$value : '';
        }
      });
      batchParams[item.key] = params;
    }

    if (item.id) {
      var _item$key, _item$value;

      batchParams['%' + item.id + '-key'] = (_item$key = item === null || item === void 0 ? void 0 : item.key) !== null && _item$key !== void 0 ? _item$key : '';
      batchParams['%' + item.id + '-value'] = (_item$value = item === null || item === void 0 ? void 0 : item.value) !== null && _item$value !== void 0 ? _item$value : '';
    }
  });
  return batchParams;
} // 后端需要value值加单引号处理


function handleSingQuoteKeys(val, key) {
  var singQuoteKeys = ['c.NotebookApp.ip', 'c.NotebookApp.token', 'c.NotebookApp.default_url'];
  var newVal = val;
  singQuoteKeys.forEach(function (singlekey) {
    if (singlekey === key && val.indexOf("'") === -1) {
      newVal = "'".concat(val, "'");
    }
  });
  return newVal;
}
/**
 * @param comp 表单组件值
 * componentTemplate用于表单回显值
 * 需要包含表单对应的value
 * 和并自定义参数
 */


function handleComponentTemplate(comp, initialCompData) {
  var newComponentTemplate = JSON.parse(initialCompData.componentTemplate).filter(function (v) {
    return !v.id;
  });
  var componentConfig = handleComponentConfig(comp);
  var customParamConfig = handleCustomParam(comp.customParam);
  var isGroup = false; // componentTemplate 存入 componentConfig 对应值

  var _loop = function _loop() {
    var _Object$entries3$_i = (0, _slicedToArray2["default"])(_Object$entries3[_i3], 2),
        key = _Object$entries3$_i[0],
        values = _Object$entries3$_i[1];

    if (!_lodash["default"].isString(values) && !_lodash["default"].isArray(values)) {
      var _loop2 = function _loop2() {
        var _Object$entries4$_i = (0, _slicedToArray2["default"])(_Object$entries4[_i4], 2),
            groupKey = _Object$entries4$_i[0],
            value = _Object$entries4$_i[1];

        newComponentTemplate.map(function (temps) {
          if (temps.key == key) {
            temps.values = temps.values.filter(function (temp) {
              return !temp.id;
            });
            temps.values.map(function (temp) {
              if (temp.key == groupKey) {
                temp.value = value;
              }
            });
          }
        });
      };

      for (var _i4 = 0, _Object$entries4 = Object.entries(values); _i4 < _Object$entries4.length; _i4++) {
        _loop2();
      }
    } else {
      newComponentTemplate.map(function (temps) {
        if (temps.key == key) temps.value = values;
      });
    }
  };

  for (var _i3 = 0, _Object$entries3 = Object.entries(componentConfig); _i3 < _Object$entries3.length; _i3++) {
    _loop();
  }

  if (Object.values(customParamConfig).length == 0) {
    return newComponentTemplate;
  } // 和并自定义参数


  for (var config in customParamConfig) {
    var _customParamConfig$co;

    if (!((_customParamConfig$co = customParamConfig[config]) === null || _customParamConfig$co === void 0 ? void 0 : _customParamConfig$co.id)) {
      isGroup = true;

      var _loop3 = function _loop3() {
        var _Object$entries5$_i = (0, _slicedToArray2["default"])(_Object$entries5[_i5], 2),
            key = _Object$entries5$_i[0],
            value = _Object$entries5$_i[1];

        newComponentTemplate.map(function (temp) {
          if (temp.key == key && temp.type == _const.CONFIG_ITEM_TYPE.GROUP) {
            temp.values = temp.values.concat(value);
          }
        });
      };

      for (var _i5 = 0, _Object$entries5 = Object.entries(customParamConfig[config]); _i5 < _Object$entries5.length; _i5++) {
        _loop3();
      }
    }
  }

  if (!isGroup) return newComponentTemplate.concat(customParamConfig);
  return newComponentTemplate;
}
/**
 * @param comp
 * @param turnp 格式 => 为tue时对应componentConfig格式为{%-key:value}
 * 返回componentConfig
 */


function handleComponentConfig(comp, turnp) {
  // 处理componentConfig
  var componentConfig = {};

  for (var _i6 = 0, _Object$entries6 = Object.entries(comp.componentConfig); _i6 < _Object$entries6.length; _i6++) {
    var _Object$entries6$_i = (0, _slicedToArray2["default"])(_Object$entries6[_i6], 2),
        key = _Object$entries6$_i[0],
        values = _Object$entries6$_i[1];

    componentConfig[key] = values;

    if (!_lodash["default"].isString(values) && !_lodash["default"].isArray(values)) {
      var groupConfig = {};

      for (var _i7 = 0, _Object$entries7 = Object.entries(values); _i7 < _Object$entries7.length; _i7++) {
        var _Object$entries7$_i = (0, _slicedToArray2["default"])(_Object$entries7[_i7], 2),
            groupKey = _Object$entries7$_i[0],
            value = _Object$entries7$_i[1];

        if (turnp) {
          groupConfig[groupKey.split('.').join('%')] = value;
        } else {
          groupConfig[groupKey.split('%').join('.')] = handleSingQuoteKeys(value, groupKey.split('%').join('.'));
        }
      }

      componentConfig[key] = groupConfig;
    }
  }

  return componentConfig;
}
/**
 * @param comp
 * @param typeCode
 * 返回包含自定义参数的componentConfig
 * typeCode识别是否有组类别
 */


function handleComponentConfigAndCustom(comp, typeCode) {
  // 处理componentConfig
  var componentConfig = handleComponentConfig(comp); // 自定义参数和componentConfig和并

  var customParamConfig = handleCustomParam(comp.customParam);

  if (isHaveGroup(typeCode) && customParamConfig.length) {
    var _iterator2 = _createForOfIteratorHelper(customParamConfig),
        _step2;

    try {
      for (_iterator2.s(); !(_step2 = _iterator2.n()).done;) {
        var config = _step2.value;

        for (var key in config) {
          var _iterator3 = _createForOfIteratorHelper(config[key]),
              _step3;

          try {
            for (_iterator3.s(); !(_step3 = _iterator3.n()).done;) {
              var groupConfig = _step3.value;
              componentConfig[key] = _objectSpread(_objectSpread({}, componentConfig[key]), {}, (0, _defineProperty2["default"])({}, groupConfig.key, groupConfig.value));
            }
          } catch (err) {
            _iterator3.e(err);
          } finally {
            _iterator3.f();
          }
        }
      }
    } catch (err) {
      _iterator2.e(err);
    } finally {
      _iterator2.f();
    }
  }

  if (!isHaveGroup(typeCode) && Object.values(customParamConfig).length) {
    var _iterator4 = _createForOfIteratorHelper(customParamConfig),
        _step4;

    try {
      for (_iterator4.s(); !(_step4 = _iterator4.n()).done;) {
        var item = _step4.value;
        componentConfig = _objectSpread(_objectSpread({}, componentConfig), {}, (0, _defineProperty2["default"])({}, item.key, item.value));
      }
    } catch (err) {
      _iterator4.e(err);
    } finally {
      _iterator4.f();
    }
  }

  return componentConfig;
}

function getInitialComp(initialCompDataArr, typeCode) {
  var initialCompData = {};

  var _iterator5 = _createForOfIteratorHelper(initialCompDataArr),
      _step5;

  try {
    for (_iterator5.s(); !(_step5 = _iterator5.n()).done;) {
      var comps = _step5.value;

      var _iterator6 = _createForOfIteratorHelper(comps),
          _step6;

      try {
        for (_iterator6.s(); !(_step6 = _iterator6.n()).done;) {
          var item = _step6.value;

          if (item.componentTypeCode == typeCode) {
            initialCompData = item;
          }
        }
      } catch (err) {
        _iterator6.e(err);
      } finally {
        _iterator6.f();
      }
    }
  } catch (err) {
    _iterator5.e(err);
  } finally {
    _iterator5.f();
  }

  return initialCompData;
}
/**
 * @param comps 已渲染各组件表单值
 * @param initialCompData 各组件初始值
 *
 * 通过比对表单值和初始值对比是否变更
 * 返回含有组件code数组
 *
 */


function getModifyComp(comps, initialCompData) {
  /**
  * 基本参数对比
  * 文件对比，只比较文件名称
  */
  var defaulParams = ['storeType', 'principal', 'hadoopVersion', 'kerberosFileName', 'uploadFileName'];
  var modifyComps = new Set();

  for (var _i8 = 0, _Object$entries8 = Object.entries(comps); _i8 < _Object$entries8.length; _i8++) {
    var _Object$entries8$_i = (0, _slicedToArray2["default"])(_Object$entries8[_i8], 2),
        typeCode = _Object$entries8$_i[0],
        comp = _Object$entries8$_i[1];

    var initialComp = getInitialComp(initialCompData, Number(typeCode));

    var _iterator7 = _createForOfIteratorHelper(defaulParams),
        _step7;

    try {
      for (_iterator7.s(); !(_step7 = _iterator7.n()).done;) {
        var _initialComp$param$na, _initialComp$param;

        var param = _step7.value;
        var compValue = comp[param];

        if (isFileParam(param)) {
          var _comp$param$name, _comp$param;

          compValue = (_comp$param$name = (_comp$param = comp[param]) === null || _comp$param === void 0 ? void 0 : _comp$param.name) !== null && _comp$param$name !== void 0 ? _comp$param$name : comp[param];
        }

        if (compValue && !_lodash["default"].isEqual(compValue, (_initialComp$param$na = (_initialComp$param = initialComp[param]) === null || _initialComp$param === void 0 ? void 0 : _initialComp$param.name) !== null && _initialComp$param$na !== void 0 ? _initialComp$param$na : initialComp[param])) {
          modifyComps.add(typeCode);
        }
      }
      /**
       * 除 hdfs、yarn、kerberos组件
       * 对比之前先处理一遍表单的数据和自定义参数, 获取含有自定义参数的componentConfig
       */

    } catch (err) {
      _iterator7.e(err);
    } finally {
      _iterator7.f();
    }

    if (!isNeedTemp(Number(typeCode))) {
      var compConfig = handleComponentConfigAndCustom(comp, Number(typeCode));

      if (!_lodash["default"].isEqual(compConfig, (initialComp === null || initialComp === void 0 ? void 0 : initialComp.componentConfig) ? JSON.parse(initialComp.componentConfig) : {})) {
        modifyComps.add(typeCode);
      }
    } else {
      /** 比对 hdfs、yarn 自定义参数 */
      var temp = getParamsByTemp(JSON.parse(initialComp === null || initialComp === void 0 ? void 0 : initialComp.componentTemplate));

      if ((comp['customParam'] || Object.values(temp).length) && !_lodash["default"].isEqual(comp['customParam'], temp)) {
        modifyComps.add(typeCode);
      }
    }
  }

  return modifyComps;
}

/***/ }),

/***/ "OHU0":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.showAlertGateCode = showAlertGateCode;
exports.showIsDefault = showIsDefault;
exports.showConfigFile = showConfigFile;
exports.showAlertGateJson = showAlertGateJson;
exports.showAlertTemplete = showAlertTemplete;
exports.canTestAlarm = canTestAlarm;
exports.textAlertKey = textAlertKey;

var _consts = __webpack_require__("RzPm");

function showAlertGateCode(alertGateType) {
  return _consts.ALARM_TYPE.CUSTOM !== alertGateType;
}

function showIsDefault(alertGateType) {
  return _consts.ALARM_TYPE.CUSTOM !== alertGateType;
}

function showConfigFile(alertGateType) {
  return _consts.ALARM_TYPE.CUSTOM === alertGateType;
}

function showAlertGateJson(alertGateCode, alertGateType) {
  return _consts.CHANNEL_MODE_VALUE.DING_DT !== alertGateCode || _consts.ALARM_TYPE.CUSTOM === alertGateType;
}

function showAlertTemplete(alertGateType, alertGateCode) {
  return _consts.ALARM_TYPE.EMAIL === alertGateType && _consts.CHANNEL_MODE_VALUE.MAIL_DT === alertGateCode || _consts.ALARM_TYPE.CUSTOM === alertGateType;
}

function canTestAlarm(alertGateType) {
  return _consts.ALARM_TYPE.DING !== alertGateType;
}

function textAlertKey(type) {
  return type === _consts.ALARM_TYPE.EMAIL ? 'emails' : 'phones';
}

/***/ }),

/***/ "Ps9q":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SCHEDULE_TYPE = exports.pieOption = exports.ALARM_HIGHT = exports.ALARM_DEFAULT = void 0;
var ALARM_DEFAULT = 40;
exports.ALARM_DEFAULT = ALARM_DEFAULT;
var ALARM_HIGHT = 70;
exports.ALARM_HIGHT = ALARM_HIGHT;
var pieOption = {
  // 第一个图表
  series: [{
    type: 'pie',
    hoverAnimation: false,
    // 鼠标经过的特效
    radius: ['72%', '80%'],
    startAngle: 210,
    labelLine: {
      normal: {
        show: false
      }
    },
    label: {
      normal: {
        position: 'center'
      }
    },
    data: [{
      value: ALARM_DEFAULT,
      itemStyle: {
        normal: {
          color: '#16DE9A'
        }
      }
    }, {
      value: ALARM_HIGHT - ALARM_DEFAULT,
      itemStyle: {
        normal: {
          color: '#FFB310'
        }
      }
    }, {
      value: ALARM_HIGHT - ALARM_DEFAULT,
      itemStyle: {
        normal: {
          color: '#FF5F5C'
        }
      }
    }, {
      value: 50,
      itemStyle: {
        normal: {
          label: {
            show: false
          },
          labelLine: {
            show: false
          },
          color: 'rgba(0,0,0,0)',
          borderWidth: 0
        }
      }
    }]
  }, // 上层环形配置
  {
    type: 'pie',
    hoverAnimation: false,
    // 鼠标经过的特效
    radius: ['52%', '70%'],
    startAngle: 210,
    labelLine: {
      normal: {
        show: false
      }
    },
    label: {
      normal: {
        position: 'center'
      }
    },
    data: [{
      value: 75,
      itemStyle: {
        normal: {
          color: '#FF5F5C'
        }
      },
      label: {
        normal: {
          formatter: '{c}%',
          position: 'center',
          show: true,
          textStyle: {
            fontSize: 12,
            fontWeight: 600,
            color: '#333333'
          }
        }
      }
    }, {
      value: 75,
      itemStyle: {
        normal: {
          label: {
            show: false
          },
          labelLine: {
            show: false
          },
          color: 'rgba(0,0,0,0)',
          borderWidth: 0
        }
      }
    }]
  }]
};
exports.pieOption = pieOption;
var SCHEDULE_TYPE = {
  Capacity: 'capacityScheduler',
  Fair: 'fairScheduler',
  FIFO: 'fifoScheduler'
};
exports.SCHEDULE_TYPE = SCHEDULE_TYPE;

/***/ }),

/***/ "RHHV":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _http = _interopRequireDefault(__webpack_require__("AwqB"));

var _reqUrls = _interopRequireDefault(__webpack_require__("C4JW"));

/**
 * 系统管理
 */
var _default = {
  // 4.0 版本相关接口
  addCluster: function addCluster(params) {
    return _http["default"].post(_reqUrls["default"].ADD_CLUSTER, params); // 新增集群
  },
  getClusterInfo: function getClusterInfo(params) {
    return _http["default"].post(_reqUrls["default"].GET_CLUSTER_INFO, params);
  },
  uploadResource: function uploadResource(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].UPLOAD_RESOURCE, params);
  },
  deleteComponent: function deleteComponent(params) {
    return _http["default"].post(_reqUrls["default"].DELETE_COMPONENT, params); // 删除组件
  },
  deleteCluster: function deleteCluster(params) {
    return _http["default"].post(_reqUrls["default"].DELETE_CLUSTER, params);
  },
  testConnects: function testConnects(params) {
    return _http["default"].post(_reqUrls["default"].TEST_CONNECTS, params);
  },
  closeKerberos: function closeKerberos(params) {
    return _http["default"].post(_reqUrls["default"].CLOSE_KERBEROS, params);
  },
  getVersionData: function getVersionData(params) {
    return _http["default"].post(_reqUrls["default"].GET_VERSION, params);
  },
  saveComponent: function saveComponent(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].SAVE_COMPONENT, params);
  },
  parseKerberos: function parseKerberos(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].PARSE_KERBEROS, params);
  },
  getCompVersion: function getCompVersion(params) {
    return _http["default"].post(_reqUrls["default"].GET_COMPONENT_VERSION, params);
  },
  getResourceList: function getResourceList(params) {
    return _http["default"].post(_reqUrls["default"].GET_RESOURCE_LIST, params);
  },
  getClusterList: function getClusterList(params) {
    return _http["default"].post(_reqUrls["default"].GET_CLUSTER_LIST, params);
  },
  uploadClusterResource: function uploadClusterResource(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].UPLOAD_CLUSTER_RESOURCE, params);
  },
  getTenantList: function getTenantList(params) {
    return _http["default"].post(_reqUrls["default"].GET_TENANT_LIST, params);
  },
  testCluster: function testCluster(params) {
    return _http["default"].post(_reqUrls["default"].TEST_CLUSTER_CONNECT, params);
  },
  createCluster: function createCluster(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].NEW_CLUSTER, params);
  },
  bindUserToQuere: function bindUserToQuere(params) {
    return _http["default"].post(_reqUrls["default"].BIND_USER_TO_RESOURCE, params);
  },
  updateCluster: function updateCluster(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].UPDATE_CLUSTER, params);
  },
  getQueueLists: function getQueueLists(params) {
    return _http["default"].post(_reqUrls["default"].GET_QUEUE_LISTS, params);
  },
  confirmSwitchQueue: function confirmSwitchQueue(params) {
    return _http["default"].post(_reqUrls["default"].CONFIRM_SWITCH_QUEUE, params);
  },
  // 4.1版本
  // 获取存储组件列表
  getComponentStore: function getComponentStore(params) {
    return _http["default"].post(_reqUrls["default"].GET_COMPONENTSTORE, params);
  },
  // 上传kerberos文件
  uploadKerberos: function uploadKerberos(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].UPLOAD_KERBEROS, params);
  },
  // 更新krb5.conf文件
  updateKrb5Conf: function updateKrb5Conf(params) {
    return _http["default"].post(_reqUrls["default"].UPDATE_KRB5CONF, params);
  },
  // 任务管理模块
  // 概览-获取集群
  getClusterDetail: function getClusterDetail(params) {
    return _http["default"].post(_reqUrls["default"].GET_CLUSTER_DETAIL, params);
  },
  // 概览-集群下拉列表
  getClusterSelect: function getClusterSelect(params) {
    return _http["default"].post(_reqUrls["default"].GET_CLUSTER_SELECT, params);
  },
  // 获取节点下拉
  getNodeAddressSelect: function getNodeAddressSelect(params) {
    return _http["default"].post(_reqUrls["default"].GET_NODEADDRESS_SELECT, params);
  },
  // 根据节点搜索
  // 明细-根据任务名搜索任务
  searchTaskList: function searchTaskList(params) {
    return _http["default"].post(_reqUrls["default"].SEARCH_TASKNAME_LIST, params);
  },
  // 明细-模糊查询任务名
  searchTaskFuzzy: function searchTaskFuzzy(params) {
    return _http["default"].post(_reqUrls["default"].SEARCH_TASKNAME_FUZZY, params);
  },
  // 明细-杀死选中或者杀死全部任务
  killTasks: function killTasks(params) {
    return _http["default"].post(_reqUrls["default"].KILL_TASKS, params);
  },
  killAllTask: function killAllTask(params) {
    return _http["default"].post(_reqUrls["default"].KILL_ALL_TASK, params);
  },
  stickJob: function stickJob(params) {
    return _http["default"].post(_reqUrls["default"].JOB_STICK, params);
  },
  // 引擎列表
  getEngineList: function getEngineList(params) {
    return _http["default"].post(_reqUrls["default"].GET_ENGINE_LIST, params);
  },
  // group列表
  getGroupList: function getGroupList(params) {
    return _http["default"].post(_reqUrls["default"].GET_GROUP_LIST, params);
  },
  // 查看明细 和搜索条件
  getViewDetail: function getViewDetail(params) {
    return _http["default"].post(_reqUrls["default"].GET_VIEW_DETAIL, params);
  },
  // 顺序调整调整优先级
  changeJobPriority: function changeJobPriority(params) {
    return _http["default"].post(_reqUrls["default"].CHANGE_JOB_PRIORITY, params);
  },
  // 查看剩余资源
  getClusterResources: function getClusterResources(params) {
    return _http["default"].post(_reqUrls["default"].GET_CLUSTER_RESOURCES, params);
  },
  getLoadTemplate: function getLoadTemplate(params) {
    return _http["default"].post(_reqUrls["default"].GET_LOADTEMPLATE, params);
  },
  uploadKerberosFile: function uploadKerberosFile(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].UPLOAD_KERBEROSFILE, params);
  },
  getKerberosFile: function getKerberosFile(params) {
    return _http["default"].post(_reqUrls["default"].GET_KERBEROSFILE, params);
  },
  testComponent: function testComponent(params) {
    return _http["default"].post(_reqUrls["default"].TEST_COMPONENT_CONNECT, params);
  },
  testComponentKerberos: function testComponentKerberos(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].TEST_COMPONENT_CONNECT_KERBEROS, params);
  },
  addComponent: function addComponent(params) {
    return _http["default"].post(_reqUrls["default"].ADD_COMPONENT, params);
  },
  saveComponentWithKerberos: function saveComponentWithKerberos(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].SAVE_COMPONENT_KERBEROS, params);
  },
  deleteKerberos: function deleteKerberos(params) {
    return _http["default"].post(_reqUrls["default"].DELETE_KERBEROS, params); // 删除Haddop Kerberos认证文件
  },
  addEngine: function addEngine(params) {
    return _http["default"].post(_reqUrls["default"].ADD_ENGINE, params);
  },
  addEngines: function addEngines(params) {
    return _http["default"].post(_reqUrls["default"].ADD_ENGINS, params);
  },
  updateClusterVersion: function updateClusterVersion(params) {
    return _http["default"].post(_reqUrls["default"].UPDATE_CLUSTER_VERSION, params);
  },
  // 资源管理
  getAllCluster: function getAllCluster(params) {
    return _http["default"].post(_reqUrls["default"].GET_ALL_CLUSTER, params); // 返回数据包含集群下的engine，以及队列
  },
  searchTenant: function searchTenant(params) {
    return _http["default"].post(_reqUrls["default"].SEARCH_TENANT, params);
  },
  getTaskResourceTemplate: function getTaskResourceTemplate(params) {
    return _http["default"].post(_reqUrls["default"].TASK_RESOURCE, params);
  },
  getQueue: function getQueue(params) {
    return _http["default"].post(_reqUrls["default"].GET_QUEUE, params);
  },
  bindTenant: function bindTenant(params) {
    return _http["default"].post(_reqUrls["default"].BIND_TENANT, params);
  },
  switchQueue: function switchQueue(params) {
    return _http["default"].post(_reqUrls["default"].SWITCH_QUEUE, params);
  },
  bindNamespace: function bindNamespace(params) {
    return _http["default"].post(_reqUrls["default"].BIND_NAME_SPACE, params);
  },
  queryTaskResourceLimits: function queryTaskResourceLimits(params) {
    return _http["default"].post(_reqUrls["default"].GET_TASKLIMITS, params);
  },

  /**
   * 告警通道
   */
  addOrUpdateAlarmRule: function addOrUpdateAlarmRule(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].ADD_OR_UPDATE_ALARM, params);
  },
  getAlarmRuleList: function getAlarmRuleList(params) {
    return _http["default"].post(_reqUrls["default"].GET_ALARM_RULE_LIST, params);
  },
  deleteAlarmRule: function deleteAlarmRule(params) {
    return _http["default"].post(_reqUrls["default"].DEL_ALARM_RULE_LIST, params);
  },
  getByAlertId: function getByAlertId(params) {
    return _http["default"].post(_reqUrls["default"].GET_ALARM_BY_ID, params);
  },
  setDefaultAlert: function setDefaultAlert(params) {
    return _http["default"].post(_reqUrls["default"].SET_DEFAULT_ALERT, params);
  },
  testAlert: function testAlert(params) {
    return _http["default"].postAsFormData(_reqUrls["default"].TEST_ALERT, params);
  },
  refreshQueue: function refreshQueue(params) {
    return _http["default"].post(_reqUrls["default"].REFRESH_QUEUE, params);
  }
};
exports["default"] = _default;

/***/ }),

/***/ "RJc4":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("J30X");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("sMBO");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("rB9j");

__webpack_require__("JfAA");

__webpack_require__("EnZy");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _icon = _interopRequireDefault(__webpack_require__("Pbn2"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _regenerator = _interopRequireDefault(__webpack_require__("o0o1"));

__webpack_require__("ls82");

var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__("yXPU"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _select = _interopRequireDefault(__webpack_require__("FAat"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _reqUrls = _interopRequireDefault(__webpack_require__("C4JW"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

var _uploadFileBtn = _interopRequireDefault(__webpack_require__("htex"));

var _kerberosModal = _interopRequireDefault(__webpack_require__("hmv9"));

var _const = __webpack_require__("j1Tt");

var _help = __webpack_require__("LNB4");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var FormItem = _form["default"].Item;
var Option = _select["default"].Option;

var FileConfig = /*#__PURE__*/function (_React$PureComponent) {
  (0, _inherits2["default"])(FileConfig, _React$PureComponent);

  var _super = _createSuper(FileConfig);

  function FileConfig() {
    var _loading;

    var _this;

    (0, _classCallCheck2["default"])(this, FileConfig);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      loading: (_loading = {}, (0, _defineProperty2["default"])(_loading, _const.FILE_TYPE.KERNEROS, false), (0, _defineProperty2["default"])(_loading, _const.FILE_TYPE.PARAMES, false), (0, _defineProperty2["default"])(_loading, _const.FILE_TYPE.CONFIGS, false), _loading),
      visible: false,
      principals: [],
      krbconfig: ''
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleVersion", function (version) {
      var _comp$componentTypeCo;

      var _this$props = _this.props,
          comp = _this$props.comp,
          handleCompVersion = _this$props.handleCompVersion;
      var typeCode = (_comp$componentTypeCo = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo !== void 0 ? _comp$componentTypeCo : '';
      handleCompVersion(typeCode, version);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderCompsVersion", function () {
      var _comp$componentTypeCo2;

      var getFieldDecorator = _this.props.form.getFieldDecorator;
      var _this$props2 = _this.props,
          versionData = _this$props2.versionData,
          comp = _this$props2.comp,
          view = _this$props2.view,
          commVersion = _this$props2.commVersion;
      var typeCode = (_comp$componentTypeCo2 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo2 !== void 0 ? _comp$componentTypeCo2 : '';
      var version = (0, _help.isOtherVersion)(typeCode) ? versionData[_const.VERSION_TYPE[typeCode]] : versionData.hadoopVersion;
      var initialValue = (0, _help.isOtherVersion)(typeCode) ? _const.DEFAULT_COMP_VERSION[typeCode] : versionData.hadoopVersion[0].value;
      initialValue = (comp === null || comp === void 0 ? void 0 : comp.hadoopVersion) || initialValue;
      if ((0, _help.isSameVersion)(typeCode)) initialValue = commVersion || initialValue;
      return /*#__PURE__*/React.createElement(FormItem, {
        label: "\u7EC4\u4EF6\u7248\u672C",
        colon: false,
        key: "".concat(typeCode, ".hadoopVersion")
      }, getFieldDecorator("".concat(typeCode, ".hadoopVersion"), {
        initialValue: initialValue
      })( /*#__PURE__*/React.createElement(_select["default"], {
        style: {
          width: 172
        },
        disabled: view,
        onChange: _this.handleVersion
      }, version.map(function (ver) {
        return /*#__PURE__*/React.createElement(Option, {
          value: ver.value,
          key: ver.key
        }, ver.key);
      }))));
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "getPrincipalsList", /*#__PURE__*/function () {
      var _ref = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee(file) {
        var _comp$componentTypeCo3;

        var _this$props3, form, comp, typeCode, res, _res$data, _res$data$;

        return _regenerator["default"].wrap(function _callee$(_context) {
          while (1) {
            switch (_context.prev = _context.next) {
              case 0:
                _this$props3 = _this.props, form = _this$props3.form, comp = _this$props3.comp;
                typeCode = (_comp$componentTypeCo3 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo3 !== void 0 ? _comp$componentTypeCo3 : '';
                _context.next = 4;
                return _console["default"].parseKerberos({
                  fileName: file
                });

              case 4:
                res = _context.sent;

                if (res.code == 1) {
                  _this.setState({
                    principals: (_res$data = res.data) !== null && _res$data !== void 0 ? _res$data : []
                  });

                  form.setFieldsValue((0, _defineProperty2["default"])({}, typeCode, {
                    principal: (_res$data$ = res === null || res === void 0 ? void 0 : res.data[0]) !== null && _res$data$ !== void 0 ? _res$data$ : '',
                    principals: res.data
                  }));
                }

              case 6:
              case "end":
                return _context.stop();
            }
          }
        }, _callee);
      }));

      return function (_x) {
        return _ref.apply(this, arguments);
      };
    }());
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "downloadFile", function (type) {
      var _comp$componentTypeCo4;

      var _this$props4 = _this.props,
          form = _this$props4.form,
          clusterInfo = _this$props4.clusterInfo,
          comp = _this$props4.comp;
      var typeCode = (_comp$componentTypeCo4 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo4 !== void 0 ? _comp$componentTypeCo4 : '';
      var version = form.getFieldValue(typeCode + '.hadoopVersion') || '';
      var a = document.createElement('a');
      var param = (comp === null || comp === void 0 ? void 0 : comp.id) ? "?componentId=".concat(comp.id, "&") : '?';
      param = param + "type=".concat(type, "&componentType=").concat(typeCode, "&hadoopVersion=").concat(version, "&clusterName=").concat(clusterInfo === null || clusterInfo === void 0 ? void 0 : clusterInfo.clusterName);
      a.href = "".concat(_reqUrls["default"].DOWNLOAD_RESOURCE).concat(param);
      a.click();
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "validateFileType", function (val) {
      var result = /\.(zip)$/.test(val.toLocaleLowerCase());

      if (val && !result) {
        _message2["default"].warning('配置文件只能是zip文件!');
      }

      return result;
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "uploadFile", /*#__PURE__*/function () {
      var _ref2 = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee2(file, loadingType, callBack) {
        var _comp$componentTypeCo5;

        var _this$props5, comp, form, clusterInfo, typeCode, res, _clusterInfo$clusterI, params;

        return _regenerator["default"].wrap(function _callee2$(_context2) {
          while (1) {
            switch (_context2.prev = _context2.next) {
              case 0:
                _this$props5 = _this.props, comp = _this$props5.comp, form = _this$props5.form, clusterInfo = _this$props5.clusterInfo;
                typeCode = (_comp$componentTypeCo5 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo5 !== void 0 ? _comp$componentTypeCo5 : '';

                _this.setState(function (preState) {
                  return {
                    loading: _objectSpread(_objectSpread({}, preState.loading), {}, (0, _defineProperty2["default"])({}, loadingType, true))
                  };
                });

                if (!((0, _help.needZipFile)(loadingType) && !_this.validateFileType(file === null || file === void 0 ? void 0 : file.name))) {
                  _context2.next = 6;
                  break;
                }

                _this.setState(function (preState) {
                  return {
                    loading: _objectSpread(_objectSpread({}, preState.loading), {}, (0, _defineProperty2["default"])({}, loadingType, false))
                  };
                });

                return _context2.abrupt("return");

              case 6:
                if (!(loadingType == _const.FILE_TYPE.KERNEROS)) {
                  _context2.next = 14;
                  break;
                }

                params = {
                  kerberosFile: file,
                  clusterId: (_clusterInfo$clusterI = clusterInfo === null || clusterInfo === void 0 ? void 0 : clusterInfo.clusterId) !== null && _clusterInfo$clusterI !== void 0 ? _clusterInfo$clusterI : '',
                  componentCode: typeCode
                };
                _context2.next = 10;
                return _console["default"].uploadKerberos(params);

              case 10:
                res = _context2.sent;

                _this.getPrincipalsList(file);

                _context2.next = 17;
                break;

              case 14:
                _context2.next = 16;
                return _console["default"].uploadResource({
                  fileName: file,
                  componentType: typeCode
                });

              case 16:
                res = _context2.sent;

              case 17:
                if (!(res.code == 1)) {
                  _context2.next = 29;
                  break;
                }

                _context2.t0 = loadingType;
                _context2.next = _context2.t0 === _const.FILE_TYPE.KERNEROS ? 21 : _context2.t0 === _const.FILE_TYPE.PARAMES ? 23 : _context2.t0 === _const.FILE_TYPE.CONFIGS ? 25 : 27;
                break;

              case 21:
                _this.setState({
                  krbconfig: res.data
                });

                return _context2.abrupt("break", 27);

              case 23:
                form.setFieldsValue((0, _defineProperty2["default"])({}, typeCode, {
                  componentConfig: _objectSpread({}, (0, _help.handleComponentConfig)({
                    componentConfig: res.data[0]
                  }, true))
                }));
                return _context2.abrupt("break", 27);

              case 25:
                form.setFieldsValue((0, _defineProperty2["default"])({}, typeCode, {
                  specialConfig: res.data[0]
                }));
                return _context2.abrupt("break", 27);

              case 27:
                callBack && callBack();

                _message2["default"].success('文件上传成功');

              case 29:
                _this.setState(function (preState) {
                  return {
                    loading: _objectSpread(_objectSpread({}, preState.loading), {}, (0, _defineProperty2["default"])({}, loadingType, false))
                  };
                });

              case 30:
              case "end":
                return _context2.stop();
            }
          }
        }, _callee2);
      }));

      return function (_x2, _x3, _x4) {
        return _ref2.apply(this, arguments);
      };
    }());
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "deleteKerFile", function () {
      var comp = _this.props.comp;
      if (!comp.id) return;

      _console["default"].closeKerberos({
        componentId: comp.id
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderKerberosFile", function () {
      var _comp$componentTypeCo6;

      var _this$props6 = _this.props,
          comp = _this$props6.comp,
          view = _this$props6.view;
      var loading = _this.state.loading;
      var typeCode = (_comp$componentTypeCo6 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo6 !== void 0 ? _comp$componentTypeCo6 : '';
      return /*#__PURE__*/React.createElement(_uploadFileBtn["default"], {
        label: "Hadoop Kerberos\u8BA4\u8BC1\u6587\u4EF6",
        fileInfo: {
          typeCode: typeCode,
          name: 'kerberosFileName',
          value: comp.kerberosFileName,
          desc: '仅支持.zip格式',
          loading: loading[_const.FILE_TYPE.KERNEROS],
          uploadProps: {
            name: 'kerberosFile',
            accept: '.zip',
            type: _const.FILE_TYPE.KERNEROS
          }
        },
        view: view,
        form: _this.props.form,
        uploadFile: _this.uploadFile,
        icons: /*#__PURE__*/React.createElement(React.Fragment, null, !view && /*#__PURE__*/React.createElement(_icon["default"], {
          type: "edit",
          style: {
            right: !(comp === null || comp === void 0 ? void 0 : comp.id) ? 20 : 40
          },
          onClick: function onClick() {
            return _this.setState({
              visible: true
            });
          }
        }), (comp === null || comp === void 0 ? void 0 : comp.id) && /*#__PURE__*/React.createElement(_icon["default"], {
          type: "download",
          style: {
            right: view ? 0 : 20
          },
          onClick: function onClick() {
            return _this.downloadFile(_const.FILE_TYPE.KERNEROS);
          }
        })),
        deleteFile: _this.deleteKerFile
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderParamsFile", function () {
      var _comp$componentTypeCo7;

      var _this$props7 = _this.props,
          comp = _this$props7.comp,
          view = _this$props7.view;
      var loading = _this.state.loading;
      var typeCode = (_comp$componentTypeCo7 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo7 !== void 0 ? _comp$componentTypeCo7 : '';
      return /*#__PURE__*/React.createElement(_uploadFileBtn["default"], {
        fileInfo: {
          typeCode: typeCode,
          name: 'paramsFile',
          value: comp.paramsFile,
          desc: '仅支持json格式',
          loading: loading[_const.FILE_TYPE.PARAMES],
          uploadProps: {
            name: 'paramsFile',
            accept: '.json',
            type: _const.FILE_TYPE.PARAMES
          }
        },
        view: view,
        form: _this.props.form,
        uploadFile: _this.uploadFile,
        notDesc: true,
        label: /*#__PURE__*/React.createElement("span", null, "\u53C2\u6570\u6279\u91CF\u4E0A\u4F20", /*#__PURE__*/React.createElement("span", {
          className: "c-fileConfig__downloadTemp",
          onClick: function onClick() {
            return _this.downloadFile(_const.FILE_TYPE.PARAMES);
          }
        }, (comp === null || comp === void 0 ? void 0 : comp.id) ? '下载参数' : '下载模板'))
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderConfigsFile", function () {
      var _comp$componentTypeCo8;

      var _this$props8 = _this.props,
          comp = _this$props8.comp,
          view = _this$props8.view;
      var loading = _this.state.loading;
      var typeCode = (_comp$componentTypeCo8 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo8 !== void 0 ? _comp$componentTypeCo8 : '';
      return /*#__PURE__*/React.createElement(_uploadFileBtn["default"], {
        label: "\u914D\u7F6E\u6587\u4EF6",
        deleteIcon: true,
        fileInfo: {
          typeCode: typeCode,
          name: 'uploadFileName',
          value: comp.uploadFileName,
          desc: _const.CONFIG_FILE_DESC[typeCode],
          loading: loading[_const.FILE_TYPE.CONFIGS],
          uploadProps: {
            name: 'uploadFileName',
            accept: '.zip',
            type: _const.FILE_TYPE.CONFIGS
          }
        },
        view: view,
        form: _this.props.form,
        uploadFile: _this.uploadFile,
        rules: [{
          required: true,
          message: "\u914D\u7F6E\u6587\u4EF6\u4E3A\u7A7A"
        }],
        icons: (comp === null || comp === void 0 ? void 0 : comp.id) && /*#__PURE__*/React.createElement(_icon["default"], {
          type: "download",
          style: {
            right: 0
          },
          onClick: function onClick() {
            return _this.downloadFile(_const.FILE_TYPE.CONFIGS);
          }
        })
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderStorageComponents", function () {
      var _comp$componentTypeCo9, _saveCompsData$;

      var _this$props9 = _this.props,
          comp = _this$props9.comp,
          form = _this$props9.form,
          saveCompsData = _this$props9.saveCompsData,
          view = _this$props9.view;
      var typeCode = (_comp$componentTypeCo9 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo9 !== void 0 ? _comp$componentTypeCo9 : '';
      if (saveCompsData.length === 0) return;
      var storeTypeFlag = false;

      for (var item in saveCompsData) {
        if (saveCompsData[item].key === _const.COMPONENT_TYPE_VALUE.HDFS) {
          storeTypeFlag = true;
          break;
        }
      }

      var storeType = (comp === null || comp === void 0 ? void 0 : comp.storeType) || (storeTypeFlag ? _const.COMPONENT_TYPE_VALUE.HDFS : saveCompsData === null || saveCompsData === void 0 ? void 0 : (_saveCompsData$ = saveCompsData[0]) === null || _saveCompsData$ === void 0 ? void 0 : _saveCompsData$.key);
      return /*#__PURE__*/React.createElement(FormItem, {
        label: "\u5B58\u50A8\u7EC4\u4EF6",
        colon: false,
        key: "".concat(typeCode, ".storeType")
      }, form.getFieldDecorator("".concat(typeCode, ".storeType"), {
        initialValue: storeType
      })( /*#__PURE__*/React.createElement(_select["default"], {
        style: {
          width: 172
        },
        disabled: view
      }, saveCompsData.map(function (ver) {
        return /*#__PURE__*/React.createElement(Option, {
          value: ver.key,
          key: ver.key
        }, ver.value);
      }))));
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderPrincipal", function () {
      var _comp$componentTypeCo10, _form$getFieldValue, _principalsList, _ref3, _comp$principal, _comp$principals;

      var _this$props10 = _this.props,
          comp = _this$props10.comp,
          form = _this$props10.form,
          view = _this$props10.view;
      var principals = _this.state.principals;
      var principalsList = principals;
      var typeCode = (_comp$componentTypeCo10 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo10 !== void 0 ? _comp$componentTypeCo10 : '';
      var kerberosFile = (_form$getFieldValue = form.getFieldValue(typeCode + '.kerberosFileName')) !== null && _form$getFieldValue !== void 0 ? _form$getFieldValue : comp === null || comp === void 0 ? void 0 : comp.kerberosFileName;

      if (!principals.length && !Array.isArray(comp === null || comp === void 0 ? void 0 : comp.principals) && (comp === null || comp === void 0 ? void 0 : comp.principals)) {
        principalsList = comp === null || comp === void 0 ? void 0 : comp.principals.split(',');
      }

      if (((_principalsList = principalsList) === null || _principalsList === void 0 ? void 0 : _principalsList.length) == 0 || !kerberosFile) return;
      return /*#__PURE__*/React.createElement(FormItem, {
        label: "principal",
        colon: false,
        key: "".concat(typeCode, ".principal")
      }, form.getFieldDecorator("".concat(typeCode, ".principal"), {
        initialValue: (_ref3 = (_comp$principal = comp === null || comp === void 0 ? void 0 : comp.principal) !== null && _comp$principal !== void 0 ? _comp$principal : principals[0]) !== null && _ref3 !== void 0 ? _ref3 : ''
      })( /*#__PURE__*/React.createElement(_select["default"], {
        style: {
          width: 172
        },
        disabled: view
      }, principalsList.map(function (ver, key) {
        return /*#__PURE__*/React.createElement(Option, {
          value: ver,
          key: key
        }, ver);
      }))), form.getFieldDecorator("".concat(typeCode, ".principals"), {
        initialValue: (_comp$principals = comp === null || comp === void 0 ? void 0 : comp.principals) !== null && _comp$principals !== void 0 ? _comp$principals : ''
      })( /*#__PURE__*/React.createElement(React.Fragment, null)));
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "hanleVisible", function (krbconfig) {
      _this.setState({
        visible: false,
        krbconfig: krbconfig
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderFileConfig", function () {
      var _this$props$comp$comp, _this$props11, _this$props11$comp;

      var typeCode = (_this$props$comp$comp = (_this$props11 = _this.props) === null || _this$props11 === void 0 ? void 0 : (_this$props11$comp = _this$props11.comp) === null || _this$props11$comp === void 0 ? void 0 : _this$props11$comp.componentTypeCode) !== null && _this$props$comp$comp !== void 0 ? _this$props$comp$comp : '';

      switch (typeCode) {
        case _const.COMPONENT_TYPE_VALUE.YARN:
        case _const.COMPONENT_TYPE_VALUE.HDFS:
          {
            return /*#__PURE__*/React.createElement(React.Fragment, null, _this.renderCompsVersion(), _this.renderConfigsFile(), _this.renderKerberosFile(), _this.renderPrincipal());
          }

        case _const.COMPONENT_TYPE_VALUE.KUBERNETES:
          {
            return /*#__PURE__*/React.createElement(React.Fragment, null, _this.renderConfigsFile(), _this.renderKerberosFile(), _this.renderPrincipal());
          }

        case _const.COMPONENT_TYPE_VALUE.SFTP:
        case _const.COMPONENT_TYPE_VALUE.NFS:
          {
            return _this.renderParamsFile();
          }

        case _const.COMPONENT_TYPE_VALUE.ORACLE_SQL:
        case _const.COMPONENT_TYPE_VALUE.LIBRA_SQL:
        case _const.COMPONENT_TYPE_VALUE.TIDB_SQL:
        case _const.COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL:
        case _const.COMPONENT_TYPE_VALUE.PRESTO_SQL:
          {
            return /*#__PURE__*/React.createElement(React.Fragment, null, _this.renderParamsFile(), _this.renderStorageComponents());
          }

        case _const.COMPONENT_TYPE_VALUE.IMPALA_SQL:
        case _const.COMPONENT_TYPE_VALUE.HIVE_SERVER:
        case _const.COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
        case _const.COMPONENT_TYPE_VALUE.SPARK:
        case _const.COMPONENT_TYPE_VALUE.FLINK:
          return /*#__PURE__*/React.createElement(React.Fragment, null, _this.renderCompsVersion(), _this.renderKerberosFile(), _this.renderPrincipal(), _this.renderParamsFile(), _this.renderStorageComponents());

        case _const.COMPONENT_TYPE_VALUE.LEARNING:
        case _const.COMPONENT_TYPE_VALUE.DTYARNSHELL:
          {
            return /*#__PURE__*/React.createElement(React.Fragment, null, _this.renderKerberosFile(), _this.renderPrincipal(), _this.renderParamsFile(), _this.renderStorageComponents());
          }

        default:
          return null;
      }
    });
    return _this;
  }

  (0, _createClass2["default"])(FileConfig, [{
    key: "render",
    value: function render() {
      var comp = this.props.comp;
      var _this$state = this.state,
          visible = _this$state.visible,
          krbconfig = _this$state.krbconfig;
      return /*#__PURE__*/React.createElement("div", {
        className: "c-fileConfig__container"
      }, this.renderFileConfig(), /*#__PURE__*/React.createElement(_kerberosModal["default"], {
        key: "".concat(visible),
        visible: visible,
        krbconfig: krbconfig || comp.mergeKrb5Content || '',
        onCancel: this.hanleVisible
      }));
    }
  }]);
  return FileConfig;
}(React.PureComponent);

exports["default"] = FileConfig;

/***/ }),

/***/ "RclC":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("fbCW");

__webpack_require__("QWBl");

__webpack_require__("pjDv");

__webpack_require__("oVuX");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("wLYn");

__webpack_require__("sMBO");

__webpack_require__("qePV");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("PKPk");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _breadcrumb = _interopRequireDefault(__webpack_require__("Y/VR"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _icon = _interopRequireDefault(__webpack_require__("Pbn2"));

var _regenerator = _interopRequireDefault(__webpack_require__("o0o1"));

__webpack_require__("ls82");

var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__("yXPU"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _tabs = _interopRequireDefault(__webpack_require__("j7zX"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _reactRouter = __webpack_require__("dtw8");

var _ = _interopRequireWildcard(__webpack_require__("LvDl"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

var _help = __webpack_require__("LNB4");

var _const = __webpack_require__("j1Tt");

var _fileConfig = _interopRequireDefault(__webpack_require__("RJc4"));

var _formConfig = _interopRequireDefault(__webpack_require__("Hc45"));

var _toolbar = _interopRequireDefault(__webpack_require__("VDrD"));

var _compsBtn = _interopRequireDefault(__webpack_require__("tgir"));

var _testResultIcon = _interopRequireDefault(__webpack_require__("6cUG"));

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var TabPane = _tabs["default"].TabPane;
var confirm = _modal["default"].confirm;

var EditCluster = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(EditCluster, _React$Component);

  var _super = _createSuper(EditCluster);

  function EditCluster() {
    var _this;

    (0, _classCallCheck2["default"])(this, EditCluster);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      testLoading: false,
      activeKey: 0,
      clusterName: '',
      commVersion: '',
      versionData: {},
      testStatus: {},
      popVisible: _const.TABS_POP_VISIBLE,
      saveCompsData: [],
      initialCompData: (0, _help.initialScheduling)() // 初始各组件的存储值

    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "getDataList", function () {
      var _ref = _this.props.location.state || {},
          cluster = _ref.cluster;

      _console["default"].getClusterInfo({
        clusterId: cluster.clusterId
      }).then(function (res) {
        if (res.code === 1) {
          var initData = (0, _help.initialScheduling)();
          var scheduling = res.data.scheduling;
          scheduling && scheduling.forEach(function (comps) {
            initData[comps.schedulingCode] = comps.components;
          });

          _this.setState({
            initialCompData: initData,
            clusterName: res.data.clusterName
          }, _this.getSaveComponentList);
        }
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "getVersionData", function () {
      _console["default"].getVersionData().then(function (res) {
        if (res.code === 1) {
          _this.setState({
            versionData: res.data
          });
        }
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "getSaveComponentList", /*#__PURE__*/(0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee() {
      var clusterName, res, saveCompsData;
      return _regenerator["default"].wrap(function _callee$(_context) {
        while (1) {
          switch (_context.prev = _context.next) {
            case 0:
              clusterName = _this.state.clusterName;
              _context.next = 3;
              return _console["default"].getComponentStore({
                clusterName: clusterName
              });

            case 3:
              res = _context.sent;

              if (res.code == 1 && res.data) {
                saveCompsData = [];
                res.data.forEach(function (item) {
                  saveCompsData.push({
                    key: item === null || item === void 0 ? void 0 : item.componentTypeCode,
                    value: item === null || item === void 0 ? void 0 : item.componentName
                  });
                });

                _this.setState({
                  saveCompsData: saveCompsData
                });
              }

            case 5:
            case "end":
              return _context.stop();
          }
        }
      }, _callee);
    })));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "getLoadTemplate", /*#__PURE__*/function () {
      var _ref3 = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee2(key, params) {
        var _initialCompData$acti, _initialCompData$acti2;

        var getFieldValue, _this$state, clusterName, initialCompData, activeKey, typeCode, comp, _ref4, _params$compVersion, _ref5, _params$storeType, res;

        return _regenerator["default"].wrap(function _callee2$(_context2) {
          while (1) {
            switch (_context2.prev = _context2.next) {
              case 0:
                getFieldValue = _this.props.form.getFieldValue;
                _this$state = _this.state, clusterName = _this$state.clusterName, initialCompData = _this$state.initialCompData, activeKey = _this$state.activeKey;
                typeCode = key !== null && key !== void 0 ? key : (_initialCompData$acti = initialCompData[activeKey][0]) === null || _initialCompData$acti === void 0 ? void 0 : _initialCompData$acti.componentTypeCode;
                comp = initialCompData[activeKey].find(function (comp) {
                  return comp.componentTypeCode == typeCode;
                });

                if (!(!(0, _help.isNeedTemp)(Number(typeCode)) && !(comp === null || comp === void 0 ? void 0 : comp.componentTemplate) && ((_initialCompData$acti2 = initialCompData[activeKey]) === null || _initialCompData$acti2 === void 0 ? void 0 : _initialCompData$acti2.length) || (params === null || params === void 0 ? void 0 : params.compVersion) || (params === null || params === void 0 ? void 0 : params.storeType))) {
                  _context2.next = 10;
                  break;
                }

                _context2.next = 7;
                return _console["default"].getLoadTemplate({
                  clusterName: clusterName,
                  componentType: typeCode,
                  version: (_ref4 = (_params$compVersion = params === null || params === void 0 ? void 0 : params.compVersion) !== null && _params$compVersion !== void 0 ? _params$compVersion : _const.DEFAULT_COMP_VERSION[typeCode]) !== null && _ref4 !== void 0 ? _ref4 : '',
                  storeType: (_ref5 = (_params$storeType = params === null || params === void 0 ? void 0 : params.storeType) !== null && _params$storeType !== void 0 ? _params$storeType : getFieldValue("".concat(typeCode, ".storeType"))) !== null && _ref5 !== void 0 ? _ref5 : ''
                });

              case 7:
                res = _context2.sent;

                if (res.code == 1) {
                  _this.saveComp({
                    componentTemplate: JSON.stringify(res.data),
                    componentTypeCode: Number(typeCode)
                  });
                }

                _this.getSaveComponentList();

              case 10:
              case "end":
                return _context2.stop();
            }
          }
        }, _callee2);
      }));

      return function (_x, _x2) {
        return _ref3.apply(this, arguments);
      };
    }());
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleCompVersion", function (typeCode, version) {
      if ((0, _help.isSameVersion)(Number(typeCode))) {
        var _this$props$form$setF;

        _this.setState({
          commVersion: version
        });

        _this.props.form.setFieldsValue((_this$props$form$setF = {}, (0, _defineProperty2["default"])(_this$props$form$setF, _const.COMPONENT_TYPE_VALUE.YARN, {
          hadoopVersion: version
        }), (0, _defineProperty2["default"])(_this$props$form$setF, _const.COMPONENT_TYPE_VALUE.HDFS, {
          hadoopVersion: version
        }), _this$props$form$setF));
      }

      _this.getLoadTemplate(typeCode, {
        compVersion: version
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onTabChange", function (key) {
      _this.setState(function (preState) {
        var _objectSpread2;

        return {
          activeKey: Number(key),
          popVisible: _objectSpread(_objectSpread({}, preState.popVisible), {}, (_objectSpread2 = {}, (0, _defineProperty2["default"])(_objectSpread2, preState.activeKey, false), (0, _defineProperty2["default"])(_objectSpread2, Number(key), false), _objectSpread2))
        };
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handlePopVisible", function (visible) {
      _this.setState(function (preState) {
        return {
          popVisible: _objectSpread(_objectSpread({}, preState.popVisible), {}, (0, _defineProperty2["default"])({}, preState.activeKey, visible !== null && visible !== void 0 ? visible : true))
        };
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "turnCompMode", function (type) {
      var _ref6 = _this.props.location.state || {},
          cluster = _ref6.cluster;

      _this.setState({
        testLoading: false
      });

      _reactRouter.hashHistory.push({
        pathname: '/console/clusterManage/editCluster',
        state: {
          mode: type,
          cluster: cluster
        }
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleConfirm", /*#__PURE__*/function () {
      var _ref7 = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee3(addComps, deleteComps) {
        var _res;

        var _this$state2, initialCompData, activeKey, testStatus, newCompData, newTestStatus, currentCompArr, res, componentIds;

        return _regenerator["default"].wrap(function _callee3$(_context3) {
          while (1) {
            switch (_context3.prev = _context3.next) {
              case 0:
                console.log(addComps, deleteComps); // 先删除组件，再添加

                _this$state2 = _this.state, initialCompData = _this$state2.initialCompData, activeKey = _this$state2.activeKey, testStatus = _this$state2.testStatus;
                newCompData = initialCompData;
                newTestStatus = testStatus;
                currentCompArr = newCompData[activeKey];
                componentIds = (0, _help.getCompsId)(currentCompArr, deleteComps);

                if (!componentIds.length) {
                  _context3.next = 10;
                  break;
                }

                _context3.next = 9;
                return _console["default"].deleteComponent({
                  componentIds: componentIds
                });

              case 9:
                res = _context3.sent;

              case 10:
                if (deleteComps.length && (((_res = res) === null || _res === void 0 ? void 0 : _res.code) == 1 || !componentIds.length)) {
                  deleteComps.forEach(function (code) {
                    currentCompArr = currentCompArr.filter(function (comp) {
                      return comp.componentTypeCode != code;
                    });
                    newTestStatus = _objectSpread(_objectSpread({}, newTestStatus), {}, (0, _defineProperty2["default"])({}, code, null));

                    _this.props.form.setFieldsValue((0, _defineProperty2["default"])({}, code, {
                      componentConfig: {},
                      specialConfig: {}
                    }));
                  });
                }

                if (addComps.length) {
                  addComps.forEach(function (code) {
                    currentCompArr.push({
                      componentTypeCode: code,
                      componentName: _const.COMPONENT_CONFIG_NAME[code]
                    });
                  });
                }

                newCompData[activeKey] = currentCompArr;

                _this.setState({
                  initialCompData: newCompData,
                  testStatus: newTestStatus
                }, _this.getLoadTemplate);

              case 14:
              case "end":
                return _context3.stop();
            }
          }
        }, _callee3);
      }));

      return function (_x3, _x4) {
        return _ref7.apply(this, arguments);
      };
    }());
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "saveComp", function (params) {
      var _this$state3 = _this.state,
          activeKey = _this$state3.activeKey,
          initialCompData = _this$state3.initialCompData;

      var newCompData = _.cloneDeep(initialCompData);

      var newComp = initialCompData[activeKey].map(function (comp) {
        if (comp.componentTypeCode == params.componentTypeCode) {
          return _objectSpread(_objectSpread({}, comp), params);
        }

        return comp;
      });
      newCompData[activeKey] = newComp;

      _this.setState({
        initialCompData: newCompData
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleComplete", function () {
      var validateFieldsAndScroll = _this.props.form.validateFieldsAndScroll;
      var initialCompData = _this.state.initialCompData;

      var showConfirm = function showConfirm(arr) {
        var compsName = Array.from(arr).map(function (code) {
          return "\"".concat(_const.COMPONENT_CONFIG_NAME[code], "\"");
        });
        confirm({
          title: "".concat(compsName.join('、'), "\u5C1A\u672A\u4FDD\u5B58\uFF0C\u662F\u5426\u9700\u8981\u4FDD\u5B58\uFF1F"),
          content: null,
          icon: /*#__PURE__*/React.createElement(_icon["default"], {
            style: {
              color: '#FAAD14'
            },
            type: "exclamation-circle",
            theme: "filled"
          }),
          okText: '保存',
          cancelText: '取消',
          onOk: function onOk() {},
          onCancel: function onCancel() {
            _this.props.router.push('/console/clusterManage');
          }
        });
      };

      validateFieldsAndScroll(function (err, values) {
        console.log(err, values);
        var modifyCompsArr = (0, _help.getModifyComp)(values, initialCompData);

        if (!modifyCompsArr.size) {
          _this.props.router.push('/console/clusterManage');

          return;
        }

        showConfirm(modifyCompsArr);
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "testConnects", function () {
      var form = _this.props.form;
      var _this$state4 = _this.state,
          initialCompData = _this$state4.initialCompData,
          clusterName = _this$state4.clusterName;
      form.validateFields(null, {}, function (err, values) {
        console.log(err, values);

        if (err) {
          _message2["default"].error('请检查配置');

          return;
        }

        if (!err) {
          var modifyComps = (0, _help.getModifyComp)(values, initialCompData);

          if (modifyComps.size > 0) {
            console.log(modifyComps);
            var modifyCompsName = Array.from(modifyComps).map(function (code) {
              return _const.COMPONENT_CONFIG_NAME[code];
            });

            _message2["default"].error("\u7EC4\u4EF6 ".concat(modifyCompsName.join('、'), " \u53C2\u6570\u53D8\u66F4\u672A\u4FDD\u5B58\uFF0C\u8BF7\u5148\u4FDD\u5B58\u518D\u6D4B\u8BD5\u7EC4\u4EF6\u8FDE\u901A\u6027"));

            return;
          }

          _this.setState({
            testLoading: true
          });

          _console["default"].testConnects({
            clusterName: clusterName
          }).then(function (res) {
            if (res.code === 1) {
              var testStatus = {};
              res.data.forEach(function (temp) {
                testStatus[temp.componentTypeCode] = _objectSpread({}, temp);
              });

              _this.setState({
                testStatus: testStatus
              });
            }

            _this.setState({
              testLoading: false
            });
          });
        }
      });
    });
    return _this;
  }

  (0, _createClass2["default"])(EditCluster, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      this.getDataList();
      this.getVersionData();
    }
  }, {
    key: "render",
    value: function render() {
      var _this2 = this;

      var _ref8 = this.props.location.state || {},
          mode = _ref8.mode,
          cluster = _ref8.cluster;

      var _this$state5 = this.state,
          clusterName = _this$state5.clusterName,
          activeKey = _this$state5.activeKey,
          initialCompData = _this$state5.initialCompData,
          versionData = _this$state5.versionData,
          saveCompsData = _this$state5.saveCompsData,
          testLoading = _this$state5.testLoading,
          testStatus = _this$state5.testStatus,
          commVersion = _this$state5.commVersion,
          popVisible = _this$state5.popVisible;
      return /*#__PURE__*/React.createElement("div", {
        className: "c-editCluster__containerWrap"
      }, /*#__PURE__*/React.createElement("div", {
        className: "c-editCluster__header"
      }, /*#__PURE__*/React.createElement(_breadcrumb["default"], null, /*#__PURE__*/React.createElement(_breadcrumb["default"].Item, null, /*#__PURE__*/React.createElement("a", {
        onClick: function onClick() {
          _this2.props.router.push('/console/clusterManage');
        }
      }, "\u591A\u96C6\u7FA4\u7BA1\u7406")), /*#__PURE__*/React.createElement(_breadcrumb["default"].Item, null, clusterName)), (0, _help.isViewMode)(mode) ? /*#__PURE__*/React.createElement("span", null, /*#__PURE__*/React.createElement(_button["default"], {
        className: "cluster-btn",
        type: "primary",
        onClick: this.turnCompMode.bind(this, 'edit')
      }, "\u7F16\u8F91")) : /*#__PURE__*/React.createElement("span", null, /*#__PURE__*/React.createElement(_button["default"], {
        className: "cluster-btn",
        ghost: true,
        loading: testLoading,
        onClick: this.testConnects
      }, "\u6D4B\u8BD5\u6240\u6709\u7EC4\u4EF6\u8FDE\u901A\u6027"), /*#__PURE__*/React.createElement(_button["default"], {
        className: "cluster-btn",
        type: "primary",
        onClick: this.handleComplete
      }, "\u5B8C\u6210"))), /*#__PURE__*/React.createElement("div", {
        className: "c-editCluster__container"
      }, /*#__PURE__*/React.createElement(_tabs["default"], {
        tabPosition: "top",
        onChange: this.onTabChange,
        activeKey: "".concat(activeKey),
        className: "c-editCluster__container__commonTabs",
        tabBarExtraContent: /*#__PURE__*/React.createElement("div", {
          className: "c-editCluster__commonTabs__title"
        }, "\u96C6\u7FA4\u914D\u7F6E")
      }, initialCompData.map(function (comps, key) {
        return /*#__PURE__*/React.createElement(TabPane, {
          tab: /*#__PURE__*/React.createElement("div", {
            style: {
              height: 19,
              display: 'flex',
              alignItems: 'center'
            }
          }, /*#__PURE__*/React.createElement("i", {
            className: "iconfont ".concat(_const.TABS_TITLE[key].iconName),
            style: {
              marginRight: 2
            }
          }), _const.TABS_TITLE[key].name),
          key: String(key)
        }, (comps === null || comps === void 0 ? void 0 : comps.length) == 0 && /*#__PURE__*/React.createElement("div", {
          key: activeKey,
          className: "empty-logo"
        }, /*#__PURE__*/React.createElement("img", {
          src: "public/img/emptyLogo.svg"
        })), /*#__PURE__*/React.createElement(_tabs["default"], {
          tabPosition: "left",
          tabBarExtraContent: !(0, _help.isViewMode)(mode) && /*#__PURE__*/React.createElement(_compsBtn["default"], {
            comps: comps,
            popVisible: popVisible[activeKey],
            activeKey: activeKey,
            handleConfirm: _this2.handleConfirm,
            handlePopVisible: _this2.handlePopVisible
          }),
          className: "c-editCluster__container__componentTabs",
          onChange: function onChange(key) {
            return _this2.getLoadTemplate(key);
          }
        }, (comps === null || comps === void 0 ? void 0 : comps.length) > 0 && comps.map(function (comp) {
          var _testStatus$comp$comp;

          return /*#__PURE__*/React.createElement(TabPane, {
            tab: /*#__PURE__*/React.createElement("span", null, comp.componentName, /*#__PURE__*/React.createElement(_testResultIcon["default"], {
              testStatus: (_testStatus$comp$comp = testStatus[comp.componentTypeCode]) !== null && _testStatus$comp$comp !== void 0 ? _testStatus$comp$comp : {}
            })),
            key: "".concat(comp.componentTypeCode)
          }, /*#__PURE__*/React.createElement(_fileConfig["default"], {
            comp: comp,
            view: (0, _help.isViewMode)(mode),
            form: _this2.props.form,
            versionData: versionData,
            commVersion: commVersion,
            saveCompsData: saveCompsData,
            clusterInfo: {
              clusterName: clusterName,
              clusterId: cluster.clusterId
            },
            handleCompVersion: _this2.handleCompVersion
          }), /*#__PURE__*/React.createElement(_formConfig["default"], {
            comp: comp,
            view: (0, _help.isViewMode)(mode),
            form: _this2.props.form
          }), !(0, _help.isViewMode)(mode) && /*#__PURE__*/React.createElement(_toolbar["default"], {
            comp: comp,
            clusterInfo: {
              clusterName: clusterName,
              clusterId: cluster.clusterId
            },
            initialCompData: initialCompData[activeKey],
            form: _this2.props.form,
            saveComp: _this2.saveComp
          }));
        })));
      }))));
    }
  }]);
  return EditCluster;
}(React.Component);

var _default = _form["default"].create()(EditCluster);

exports["default"] = _default;

/***/ }),

/***/ "RnhZ":
/***/ (function(module, exports, __webpack_require__) {

var map = {
	"./af": "K/tc",
	"./af.js": "K/tc",
	"./ar": "jnO4",
	"./ar-dz": "o1bE",
	"./ar-dz.js": "o1bE",
	"./ar-kw": "Qj4J",
	"./ar-kw.js": "Qj4J",
	"./ar-ly": "HP3h",
	"./ar-ly.js": "HP3h",
	"./ar-ma": "CoRJ",
	"./ar-ma.js": "CoRJ",
	"./ar-sa": "gjCT",
	"./ar-sa.js": "gjCT",
	"./ar-tn": "bYM6",
	"./ar-tn.js": "bYM6",
	"./ar.js": "jnO4",
	"./az": "SFxW",
	"./az.js": "SFxW",
	"./be": "H8ED",
	"./be.js": "H8ED",
	"./bg": "hKrs",
	"./bg.js": "hKrs",
	"./bm": "p/rL",
	"./bm.js": "p/rL",
	"./bn": "kEOa",
	"./bn.js": "kEOa",
	"./bo": "0mo+",
	"./bo.js": "0mo+",
	"./br": "aIdf",
	"./br.js": "aIdf",
	"./bs": "JVSJ",
	"./bs.js": "JVSJ",
	"./ca": "1xZ4",
	"./ca.js": "1xZ4",
	"./cs": "PA2r",
	"./cs.js": "PA2r",
	"./cv": "A+xa",
	"./cv.js": "A+xa",
	"./cy": "l5ep",
	"./cy.js": "l5ep",
	"./da": "DxQv",
	"./da.js": "DxQv",
	"./de": "tGlX",
	"./de-at": "s+uk",
	"./de-at.js": "s+uk",
	"./de-ch": "u3GI",
	"./de-ch.js": "u3GI",
	"./de.js": "tGlX",
	"./dv": "WYrj",
	"./dv.js": "WYrj",
	"./el": "jUeY",
	"./el.js": "jUeY",
	"./en-au": "Dmvi",
	"./en-au.js": "Dmvi",
	"./en-ca": "OIYi",
	"./en-ca.js": "OIYi",
	"./en-gb": "Oaa7",
	"./en-gb.js": "Oaa7",
	"./en-ie": "4dOw",
	"./en-ie.js": "4dOw",
	"./en-il": "czMo",
	"./en-il.js": "czMo",
	"./en-nz": "b1Dy",
	"./en-nz.js": "b1Dy",
	"./eo": "Zduo",
	"./eo.js": "Zduo",
	"./es": "iYuL",
	"./es-do": "CjzT",
	"./es-do.js": "CjzT",
	"./es-us": "Vclq",
	"./es-us.js": "Vclq",
	"./es.js": "iYuL",
	"./et": "7BjC",
	"./et.js": "7BjC",
	"./eu": "D/JM",
	"./eu.js": "D/JM",
	"./fa": "jfSC",
	"./fa.js": "jfSC",
	"./fi": "gekB",
	"./fi.js": "gekB",
	"./fo": "ByF4",
	"./fo.js": "ByF4",
	"./fr": "nyYc",
	"./fr-ca": "2fjn",
	"./fr-ca.js": "2fjn",
	"./fr-ch": "Dkky",
	"./fr-ch.js": "Dkky",
	"./fr.js": "nyYc",
	"./fy": "cRix",
	"./fy.js": "cRix",
	"./gd": "9rRi",
	"./gd.js": "9rRi",
	"./gl": "iEDd",
	"./gl.js": "iEDd",
	"./gom-latn": "DKr+",
	"./gom-latn.js": "DKr+",
	"./gu": "4MV3",
	"./gu.js": "4MV3",
	"./he": "x6pH",
	"./he.js": "x6pH",
	"./hi": "3E1r",
	"./hi.js": "3E1r",
	"./hr": "S6ln",
	"./hr.js": "S6ln",
	"./hu": "WxRl",
	"./hu.js": "WxRl",
	"./hy-am": "1rYy",
	"./hy-am.js": "1rYy",
	"./id": "UDhR",
	"./id.js": "UDhR",
	"./is": "BVg3",
	"./is.js": "BVg3",
	"./it": "bpih",
	"./it.js": "bpih",
	"./ja": "B55N",
	"./ja.js": "B55N",
	"./jv": "tUCv",
	"./jv.js": "tUCv",
	"./ka": "IBtZ",
	"./ka.js": "IBtZ",
	"./kk": "bXm7",
	"./kk.js": "bXm7",
	"./km": "6B0Y",
	"./km.js": "6B0Y",
	"./kn": "PpIw",
	"./kn.js": "PpIw",
	"./ko": "Ivi+",
	"./ko.js": "Ivi+",
	"./ky": "lgnt",
	"./ky.js": "lgnt",
	"./lb": "RAwQ",
	"./lb.js": "RAwQ",
	"./lo": "sp3z",
	"./lo.js": "sp3z",
	"./lt": "JvlW",
	"./lt.js": "JvlW",
	"./lv": "uXwI",
	"./lv.js": "uXwI",
	"./me": "KTz0",
	"./me.js": "KTz0",
	"./mi": "aIsn",
	"./mi.js": "aIsn",
	"./mk": "aQkU",
	"./mk.js": "aQkU",
	"./ml": "AvvY",
	"./ml.js": "AvvY",
	"./mn": "lYtQ",
	"./mn.js": "lYtQ",
	"./mr": "Ob0Z",
	"./mr.js": "Ob0Z",
	"./ms": "6+QB",
	"./ms-my": "ZAMP",
	"./ms-my.js": "ZAMP",
	"./ms.js": "6+QB",
	"./mt": "G0Uy",
	"./mt.js": "G0Uy",
	"./my": "honF",
	"./my.js": "honF",
	"./nb": "bOMt",
	"./nb.js": "bOMt",
	"./ne": "OjkT",
	"./ne.js": "OjkT",
	"./nl": "+s0g",
	"./nl-be": "2ykv",
	"./nl-be.js": "2ykv",
	"./nl.js": "+s0g",
	"./nn": "uEye",
	"./nn.js": "uEye",
	"./pa-in": "8/+R",
	"./pa-in.js": "8/+R",
	"./pl": "jVdC",
	"./pl.js": "jVdC",
	"./pt": "8mBD",
	"./pt-br": "0tRk",
	"./pt-br.js": "0tRk",
	"./pt.js": "8mBD",
	"./ro": "lyxo",
	"./ro.js": "lyxo",
	"./ru": "lXzo",
	"./ru.js": "lXzo",
	"./sd": "Z4QM",
	"./sd.js": "Z4QM",
	"./se": "//9w",
	"./se.js": "//9w",
	"./si": "7aV9",
	"./si.js": "7aV9",
	"./sk": "e+ae",
	"./sk.js": "e+ae",
	"./sl": "gVVK",
	"./sl.js": "gVVK",
	"./sq": "yPMs",
	"./sq.js": "yPMs",
	"./sr": "zx6S",
	"./sr-cyrl": "E+lV",
	"./sr-cyrl.js": "E+lV",
	"./sr.js": "zx6S",
	"./ss": "Ur1D",
	"./ss.js": "Ur1D",
	"./sv": "X709",
	"./sv.js": "X709",
	"./sw": "dNwA",
	"./sw.js": "dNwA",
	"./ta": "PeUW",
	"./ta.js": "PeUW",
	"./te": "XLvN",
	"./te.js": "XLvN",
	"./tet": "V2x9",
	"./tet.js": "V2x9",
	"./tg": "Oxv6",
	"./tg.js": "Oxv6",
	"./th": "EOgW",
	"./th.js": "EOgW",
	"./tl-ph": "Dzi0",
	"./tl-ph.js": "Dzi0",
	"./tlh": "z3Vd",
	"./tlh.js": "z3Vd",
	"./tr": "DoHr",
	"./tr.js": "DoHr",
	"./tzl": "z1FC",
	"./tzl.js": "z1FC",
	"./tzm": "wQk9",
	"./tzm-latn": "tT3J",
	"./tzm-latn.js": "tT3J",
	"./tzm.js": "wQk9",
	"./ug-cn": "YRex",
	"./ug-cn.js": "YRex",
	"./uk": "raLr",
	"./uk.js": "raLr",
	"./ur": "UpQW",
	"./ur.js": "UpQW",
	"./uz": "Loxo",
	"./uz-latn": "AQ68",
	"./uz-latn.js": "AQ68",
	"./uz.js": "Loxo",
	"./vi": "KSF8",
	"./vi.js": "KSF8",
	"./x-pseudo": "/X5v",
	"./x-pseudo.js": "/X5v",
	"./yo": "fzPg",
	"./yo.js": "fzPg",
	"./zh-cn": "XDpg",
	"./zh-cn.js": "XDpg",
	"./zh-hk": "SatO",
	"./zh-hk.js": "SatO",
	"./zh-tw": "kOpN",
	"./zh-tw.js": "kOpN"
};


function webpackContext(req) {
	var id = webpackContextResolve(req);
	return __webpack_require__(id);
}
function webpackContextResolve(req) {
	if(!__webpack_require__.o(map, req)) {
		var e = new Error("Cannot find module '" + req + "'");
		e.code = 'MODULE_NOT_FOUND';
		throw e;
	}
	return map[req];
}
webpackContext.keys = function webpackContextKeys() {
	return Object.keys(map);
};
webpackContext.resolve = webpackContextResolve;
module.exports = webpackContext;
webpackContext.id = "RnhZ";

/***/ }),

/***/ "RzPm":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.RESOURCE_TYPE = exports.EMAIL_REG = exports.EMAIL_COMMA = exports.PHONE_REG = exports.NUM_COMMA = exports.pieOption = exports.doubleLineAreaChartOptions = exports.lineAreaChartOptions = exports.rowFormItemLayout = exports.tailFormItemLayout = exports.specFormItemLayout = exports.longLabelFormLayout = exports.formItemCenterLayout = exports.formItemLayout = exports.sourcetype = exports.notExtKeysTidbSql = exports.notExtKeysLibraSql = exports.notExtKeysHiveServer = exports.notExtKeysSparkThrift = exports.notExtKeyDtscriptJupter = exports.notExtKeyDtscriptPython = exports.notExtKeysDtyarnShell = exports.notExtKeysLearning = exports.notExtKeysSpark = exports.notExtKeysFlink = exports.FLINK_KEY_MAP_DOTS = exports.FLINK_KEY_MAP = exports.DTYARNSHELL_KEY_MAP_DOTS = exports.DTYARNSHELL_KEY_MAP = exports.SPARK_KEY_MAP_DOTS = exports.SPARK_KEY_MAP = exports.validateSftpDataParams = exports.validateLibraParams = exports.validateLearningParams = exports.validateDtYarnShellParams = exports.validateSparkParams = exports.validateHiveServerParams = exports.validateImpalaSqlParams = exports.validateCarbonDataParams = exports.validateHiveParams = exports.validateFlinkParams = exports.dataSourceTypes = exports.EXCHANGE_ADMIN_API_STATUS = exports.EXCHANGE_APPLY_STATUS = exports.EXCHANGE_API_STATUS = exports.API_DELETE = exports.API_SYSTEM_STATUS = exports.API_USER_STATUS = exports.API_STATUS = exports.API_METHOD_key = exports.API_METHOD = exports.API_MODE = exports.HADOOP_GROUP_VALUE = exports.DEFAULT_COMP_REQUIRED = exports.DEFAULT_COMP_TEST = exports.ENGINE_TYPE_ARRAY = exports.ENGIN_TYPE_TEXT = exports.ENGINE_TYPE_NAME = exports.ENGINE_TYPE = exports.DATA_SOURCE = exports.JobStageText = exports.JobStage = exports.TASK_STATE = exports.CHANNEL_CONF_TEXT = exports.CHANNEL_MODE = exports.CHANNEL_MODE_VALUE = exports.ALARM_TYPE_TEXT = exports.ALARM_TYPE = exports.COMPUTE_COMPONENTS = exports.STORE_COMPONENTS = exports.SOURCE_COMPONENTS = exports.COMMON_COMPONENTS = exports.TABS_TITLE = exports.TABS_TITLE_KEY = exports.COMPONEMT_CONFIG_NAME_ENUM = exports.COMPONEMT_CONFIG_KEY_ENUM = exports.COMPONEMT_CONFIG_KEYS = exports.COMPONENT_TYPE_VALUE = exports.UPPER_NAME = exports.COMPONENT_CONFIG_NAME = void 0;

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _COMPONEMT_CONFIG_KEY, _COMPONEMT_CONFIG_NAM, _ALARM_TYPE_TEXT, _JobStageText, _ENGIN_TYPE_TEXT;

var COMPONENT_CONFIG_NAME = {
  FLINK: 'Flink',
  SPARK: 'Spark',
  LEARNING: 'Learning',
  DTYARNSHELL: 'DtScript',
  HDFS: 'HDFS',
  YARN: 'YARN',
  SPARK_THRIFT_SERVER: 'SparkThrift',
  CARBONDATA: 'CarbonData ThriftServer',
  LIBRA_SQL: 'LibrA SQL',
  HIVE_SERVER: 'Hive Server',
  SFTP: 'SFTP',
  IMPALA_SQL: 'Impala SQL',
  PRESTO_SQL: 'Presto SQL',
  TIDB_SQL: 'TiDB SQL',
  ORACLE_SQL: 'Oracle SQL',
  GREEN_PLUM_SQL: 'Greenplum SQL',
  KUBERNETES: 'Kubernetes',
  NFS: 'NFS'
};
exports.COMPONENT_CONFIG_NAME = COMPONENT_CONFIG_NAME;
var UPPER_NAME = ['FLINK', 'HDFS', 'SPARK', 'SPARK_THRIFT_SERVER', 'YARN', 'IMPALA_SQL', 'DTYARNSHELL', 'LEARNING', 'HIVE_SERVER'];
exports.UPPER_NAME = UPPER_NAME;
var COMPONENT_TYPE_VALUE = {
  FLINK: 0,
  SPARK: 1,
  LEARNING: 2,
  DTYARNSHELL: 3,
  HDFS: 4,
  YARN: 5,
  SPARK_THRIFT_SERVER: 6,
  CARBONDATA: 7,
  LIBRA_SQL: 8,
  HIVE_SERVER: 9,
  SFTP: 10,
  IMPALA_SQL: 11,
  TIDB_SQL: 12,
  ORACLE_SQL: 13,
  GREEN_PLUM_SQL: 14,
  KUBERNETES: 15,
  PRESTO_SQL: 16,
  NFS: 17
};
exports.COMPONENT_TYPE_VALUE = COMPONENT_TYPE_VALUE;
var COMPONEMT_CONFIG_KEYS = {
  FLINK: 'flinkConf',
  SPARK: 'sparkConf',
  LEARNING: 'learningConf',
  DTYARNSHELL: 'dtscriptConf',
  HDFS: 'hadoopConf',
  YARN: 'yarnConf',
  SPARK_THRIFT_SERVER: 'hiveConf',
  CARBONDATA: 'carbonConf',
  LIBRA_SQL: 'libraConf',
  TI_DB_SQL: 'tidbConf',
  HIVE_SERVER: 'hiveServerConf',
  SFTP: 'sftpConf',
  IMPALA_SQL: 'impalaSqlConf',
  TIDB_SQL: 'tidbConf',
  ORACLE_SQL: 'oracleConf',
  GREEN_PLUM_SQL: 'greenConf',
  KUBERNETES: 'kubernetesConf',
  PRESTO_SQL: 'prestoConf',
  NFS: 'NFS'
}; // 组件对应的key值

exports.COMPONEMT_CONFIG_KEYS = COMPONEMT_CONFIG_KEYS;
var COMPONEMT_CONFIG_KEY_ENUM = (_COMPONEMT_CONFIG_KEY = {}, (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.FLINK, COMPONEMT_CONFIG_KEYS.FLINK), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.SPARK, COMPONEMT_CONFIG_KEYS.SPARK), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.LEARNING, COMPONEMT_CONFIG_KEYS.LEARNING), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.DTYARNSHELL, COMPONEMT_CONFIG_KEYS.DTYARNSHELL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.HDFS, COMPONEMT_CONFIG_KEYS.HDFS), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.YARN, COMPONEMT_CONFIG_KEYS.YARN), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER, COMPONEMT_CONFIG_KEYS.SPARK_THRIFT_SERVER), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.CARBONDATA, COMPONEMT_CONFIG_KEYS.CARBONDATA), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.HIVE_SERVER, COMPONEMT_CONFIG_KEYS.HIVE_SERVER), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.LIBRA_SQL, COMPONEMT_CONFIG_KEYS.LIBRA_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.SFTP, COMPONEMT_CONFIG_KEYS.SFTP), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.TIDB_SQL, COMPONEMT_CONFIG_KEYS.TIDB_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.IMPALA_SQL, COMPONEMT_CONFIG_KEYS.IMPALA_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.ORACLE_SQL, COMPONEMT_CONFIG_KEYS.ORACLE_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL, COMPONEMT_CONFIG_KEYS.GREEN_PLUM_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.KUBERNETES, COMPONEMT_CONFIG_KEYS.KUBERNETES), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.PRESTO_SQL, COMPONEMT_CONFIG_KEYS.PRESTO_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_KEY, COMPONENT_TYPE_VALUE.NFS, COMPONEMT_CONFIG_KEYS.NFS), _COMPONEMT_CONFIG_KEY); // 组件对应的name值

exports.COMPONEMT_CONFIG_KEY_ENUM = COMPONEMT_CONFIG_KEY_ENUM;
var COMPONEMT_CONFIG_NAME_ENUM = (_COMPONEMT_CONFIG_NAM = {}, (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.SPARK, COMPONENT_CONFIG_NAME.SPARK), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.FLINK, COMPONENT_CONFIG_NAME.FLINK), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.LEARNING, COMPONENT_CONFIG_NAME.LEARNING), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.DTYARNSHELL, COMPONENT_CONFIG_NAME.DTYARNSHELL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.HDFS, COMPONENT_CONFIG_NAME.HDFS), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.YARN, COMPONENT_CONFIG_NAME.YARN), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER, COMPONENT_CONFIG_NAME.SPARK_THRIFT_SERVER), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.CARBONDATA, COMPONENT_CONFIG_NAME.CARBONDATA), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.HIVE_SERVER, COMPONENT_CONFIG_NAME.HIVE_SERVER), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.LIBRA_SQL, COMPONENT_CONFIG_NAME.LIBRA_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.SFTP, COMPONENT_CONFIG_NAME.SFTP), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.TIDB_SQL, COMPONENT_CONFIG_NAME.TIDB_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.IMPALA_SQL, COMPONENT_CONFIG_NAME.IMPALA_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.ORACLE_SQL, COMPONENT_CONFIG_NAME.ORACLE_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL, COMPONENT_CONFIG_NAME.GREEN_PLUM_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.KUBERNETES, COMPONENT_CONFIG_NAME.KUBERNETES), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.PRESTO_SQL, COMPONENT_CONFIG_NAME.PRESTO_SQL), (0, _defineProperty2["default"])(_COMPONEMT_CONFIG_NAM, COMPONENT_TYPE_VALUE.NFS, COMPONENT_CONFIG_NAME.NFS), _COMPONEMT_CONFIG_NAM); // Tabs枚举值

exports.COMPONEMT_CONFIG_NAME_ENUM = COMPONEMT_CONFIG_NAME_ENUM;
var TABS_TITLE_KEY = {
  COMMON: 0,
  SOURCE: 1,
  STORE: 2,
  COMPUTE: 3
};
exports.TABS_TITLE_KEY = TABS_TITLE_KEY;
var TABS_TITLE = [{
  schedulingName: '公共组件',
  schedulingCode: TABS_TITLE_KEY.COMMON
}, {
  schedulingName: '资源调度组件',
  schedulingCode: TABS_TITLE_KEY.SOURCE
}, {
  schedulingName: '存储组件',
  schedulingCode: TABS_TITLE_KEY.STORE
}, {
  schedulingName: '计算组件',
  schedulingCode: TABS_TITLE_KEY.COMPUTE
}]; // 公共组件

exports.TABS_TITLE = TABS_TITLE;
var COMMON_COMPONENTS = [{
  componentTypeCode: COMPONENT_TYPE_VALUE.SFTP,
  componentName: COMPONENT_CONFIG_NAME.SFTP
}]; // 资源调度组件组件

exports.COMMON_COMPONENTS = COMMON_COMPONENTS;
var SOURCE_COMPONENTS = [{
  componentTypeCode: COMPONENT_TYPE_VALUE.YARN,
  componentName: COMPONENT_CONFIG_NAME.YARN
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.KUBERNETES,
  componentName: COMPONENT_CONFIG_NAME.KUBERNETES
}]; // 存储组件组件

exports.SOURCE_COMPONENTS = SOURCE_COMPONENTS;
var STORE_COMPONENTS = [{
  componentTypeCode: COMPONENT_TYPE_VALUE.HDFS,
  componentName: COMPONENT_CONFIG_NAME.HDFS
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.NFS,
  componentName: COMPONENT_CONFIG_NAME.NFS
}]; // 计算组件

exports.STORE_COMPONENTS = STORE_COMPONENTS;
var COMPUTE_COMPONENTS = [{
  componentTypeCode: COMPONENT_TYPE_VALUE.SPARK,
  componentName: COMPONENT_CONFIG_NAME.SPARK
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER,
  componentName: COMPONENT_CONFIG_NAME.SPARK_THRIFT_SERVER
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.FLINK,
  componentName: COMPONENT_CONFIG_NAME.FLINK
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.HIVE_SERVER,
  componentName: COMPONENT_CONFIG_NAME.HIVE_SERVER
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.IMPALA_SQL,
  componentName: COMPONENT_CONFIG_NAME.IMPALA_SQL
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.DTYARNSHELL,
  componentName: COMPONENT_CONFIG_NAME.DTYARNSHELL
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.LEARNING,
  componentName: COMPONENT_CONFIG_NAME.LEARNING
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.PRESTO_SQL,
  componentName: COMPONENT_CONFIG_NAME.PRESTO_SQL
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.TIDB_SQL,
  componentName: COMPONENT_CONFIG_NAME.TIDB_SQL
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.LIBRA_SQL,
  componentName: COMPONENT_CONFIG_NAME.LIBRA_SQL
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.ORACLE_SQL,
  componentName: COMPONENT_CONFIG_NAME.ORACLE_SQL
}, {
  componentTypeCode: COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL,
  componentName: COMPONENT_CONFIG_NAME.GREEN_PLUM_SQL
}];
/**
 * 告警通道
 */

exports.COMPUTE_COMPONENTS = COMPUTE_COMPONENTS;
var ALARM_TYPE;
exports.ALARM_TYPE = ALARM_TYPE;

(function (ALARM_TYPE) {
  ALARM_TYPE[ALARM_TYPE["MSG"] = 1] = "MSG";
  ALARM_TYPE[ALARM_TYPE["EMAIL"] = 2] = "EMAIL";
  ALARM_TYPE[ALARM_TYPE["DING"] = 3] = "DING";
  ALARM_TYPE[ALARM_TYPE["CUSTOM"] = 4] = "CUSTOM";
})(ALARM_TYPE || (exports.ALARM_TYPE = ALARM_TYPE = {}));

var ALARM_TYPE_TEXT = (_ALARM_TYPE_TEXT = {}, (0, _defineProperty2["default"])(_ALARM_TYPE_TEXT, ALARM_TYPE.MSG, '短信通道'), (0, _defineProperty2["default"])(_ALARM_TYPE_TEXT, ALARM_TYPE.EMAIL, '邮件通道'), (0, _defineProperty2["default"])(_ALARM_TYPE_TEXT, ALARM_TYPE.DING, '钉钉通道'), (0, _defineProperty2["default"])(_ALARM_TYPE_TEXT, ALARM_TYPE.CUSTOM, '自定义通道'), _ALARM_TYPE_TEXT);
exports.ALARM_TYPE_TEXT = ALARM_TYPE_TEXT;
var CHANNEL_MODE_VALUE = {
  SMS_YP: 'sms_yp',
  SMS_DY: 'sms_dy',
  SMS_API: 'sms_api',
  SMS_JAR: 'sms_jar',
  MAIL_DT: 'mail_dt',
  MAIL_API: 'mail_api',
  MAIL_JAR: 'mail_jar',
  DING_DT: 'ding_dt',
  DING_API: 'ding_api',
  DING_JAR: 'ding_jar'
};
exports.CHANNEL_MODE_VALUE = CHANNEL_MODE_VALUE;
var CHANNEL_MODE = {
  sms: [{
    value: CHANNEL_MODE_VALUE.SMS_JAR,
    title: '扩展插件通道'
  }],
  mail: [{
    value: CHANNEL_MODE_VALUE.MAIL_DT,
    title: '默认邮件通道'
  }, {
    value: CHANNEL_MODE_VALUE.MAIL_JAR,
    title: '扩展插件通道'
  }],
  dingTalk: [{
    value: CHANNEL_MODE_VALUE.DING_DT,
    title: '钉钉机器人'
  }, {
    value: CHANNEL_MODE_VALUE.DING_JAR,
    title: '扩展插件通道'
  }]
};
exports.CHANNEL_MODE = CHANNEL_MODE;
var CHANNEL_CONF_TEXT = {
  JAR: '{"classname":"com.dtstack.sender.sms.xxxsender"}',
  API: '{\n"cookiestore": false,\n"configs": [{\n"url": "",\n"method": "get",\n"header": {},\n"body": {}\n}],\n"context": {}\n} ',
  SMS_YP: '请按照此格式输入配置信息：\n{"yp_api_key":"xxxxxx"}',
  MAIL_DT: '{\n"mail.smtp.host":"smtp.yeah.net",\n"mail.smtp.port":"25",\n"mail.smtp.ssl.enable":"false",\n"mail.smtp.username":"daishu@dtstack.com",\n"mail.smtp.password":"xxxx",\n"mail.smtp.from":"daishu@dtstack.com"\n}',
  CUSTOM: '{"className":"com.Test"}'
}; // 任务状态

exports.CHANNEL_CONF_TEXT = CHANNEL_CONF_TEXT;
var TASK_STATE = {
  UNSUBMIT: 0,
  CREATED: 1,
  SCHEDULED: 2,
  DEPLOYING: 3,
  RUNNING: 4,
  FINISHED: 5,
  CANCELLING: 6,
  CANCELED: 7,
  FAILED: 8,
  SUBMITFAILD: 9,
  SUBMITTING: 10,
  RESTARTING: 11,
  MANUALSUCCESS: 12,
  KILLED: 13,
  SUBMITTED: 14,
  NOTFOUND: 15,
  WAITENGINE: 16,
  WAITCOMPUTE: 17,
  FROZEN: 18,
  ENGINEACCEPTED: 19,
  ENGINEDISTRIBUTE: 20,

  /**
   * 父任务失败
   */
  PARENTFAILED: 21,

  /**
   * 失败中
   */
  FAILING: 22,

  /**
   * 计算中
   */
  COMPUTING: 23,

  /**
   * 过期
   */
  EXPIRE: 24,

  /**
   * 等待资源
   */
  LACKING: 25,

  /**
   * 自动取消
   */
  AUTOCANCELED: 26
}; // JOB 在DB中，未加到优先级队列
// DB(1),
// //JOB 在优先级队列，等待提交
// PRIORITY(2),
// //JOB 因为失败进入重试队列，等待重试的delay时间后，可以重新提交
// RESTART(3),
// //JOB 因为资源不足，处于资源不足等待中
// LACKING(4),
// //JOB 已经提交，处于状态轮询中
// SUBMITTED(5);

/**
 *  实例 stage
 */

exports.TASK_STATE = TASK_STATE;
var JobStage;
exports.JobStage = JobStage;

(function (JobStage) {
  JobStage[JobStage["Saved"] = 1] = "Saved";
  JobStage[JobStage["Queueing"] = 2] = "Queueing";
  JobStage[JobStage["WaitTry"] = 3] = "WaitTry";
  JobStage[JobStage["WaitResource"] = 4] = "WaitResource";
  JobStage[JobStage["Running"] = 5] = "Running";
})(JobStage || (exports.JobStage = JobStage = {}));

var JobStageText = (_JobStageText = {}, (0, _defineProperty2["default"])(_JobStageText, JobStage.Queueing, '队列中'), (0, _defineProperty2["default"])(_JobStageText, JobStage.Saved, '已存储'), (0, _defineProperty2["default"])(_JobStageText, JobStage.WaitTry, '等待重试'), (0, _defineProperty2["default"])(_JobStageText, JobStage.WaitResource, '等待资源'), (0, _defineProperty2["default"])(_JobStageText, JobStage.Running, '运行中'), _JobStageText); // 常量

exports.JobStageText = JobStageText;
var DATA_SOURCE = {
  MYSQL: 1,
  ORACLE: 2,
  SQLSERVER: 3,
  HDFS: 6,
  HIVE: 7,
  HBASE: 8,
  FTP: 9,
  MAXCOMPUTE: 10,
  ADSMAXCOMPUTE: 11,
  NFS: 17,
  TI_DB: 31
};
exports.DATA_SOURCE = DATA_SOURCE;
var ENGINE_TYPE = {
  HADOOP: 1,
  LIBRA: 2,
  TI_DB: 4,
  ORACLE: 5,
  GREEN_PLUM: 6,
  PRESTO: 7
};
exports.ENGINE_TYPE = ENGINE_TYPE;
var ENGINE_TYPE_NAME = {
  HADOOP: 'Hadoop',
  LIBRA: 'LibrA',
  TI_DB: 'TiDB',
  ORACLE: 'Oracle',
  GREEN_PLUM: 'GreenPlum'
};
exports.ENGINE_TYPE_NAME = ENGINE_TYPE_NAME;
var ENGIN_TYPE_TEXT = (_ENGIN_TYPE_TEXT = {}, (0, _defineProperty2["default"])(_ENGIN_TYPE_TEXT, ENGINE_TYPE.HADOOP, 'Hadoop'), (0, _defineProperty2["default"])(_ENGIN_TYPE_TEXT, ENGINE_TYPE.LIBRA, 'LibrA'), (0, _defineProperty2["default"])(_ENGIN_TYPE_TEXT, ENGINE_TYPE.TI_DB, 'TiDB'), (0, _defineProperty2["default"])(_ENGIN_TYPE_TEXT, ENGINE_TYPE.ORACLE, 'Oracle'), (0, _defineProperty2["default"])(_ENGIN_TYPE_TEXT, ENGINE_TYPE.GREEN_PLUM, 'GreenPlum'), (0, _defineProperty2["default"])(_ENGIN_TYPE_TEXT, ENGINE_TYPE.PRESTO, 'Presto'), _ENGIN_TYPE_TEXT);
exports.ENGIN_TYPE_TEXT = ENGIN_TYPE_TEXT;
var ENGINE_TYPE_ARRAY = [{
  // 引擎类型下拉框数据
  name: 'Hadoop',
  value: ENGINE_TYPE_NAME.HADOOP
}, {
  name: 'LibrA',
  value: ENGINE_TYPE_NAME.LIBRA
}, {
  name: 'TiDB',
  value: ENGINE_TYPE_NAME.TI_DB
}, {
  name: 'Oracle',
  value: ENGINE_TYPE_NAME.ORACLE
}, {
  name: 'GreenPlum',
  value: ENGINE_TYPE_NAME.GREEN_PLUM
}];
exports.ENGINE_TYPE_ARRAY = ENGINE_TYPE_ARRAY;
var DEFAULT_COMP_TEST = {
  // 测试结果默认数据
  flinkTestResult: {},
  sparkTestResult: {},
  dtYarnShellTestResult: {},
  learningTestResult: {},
  hdfsTestResult: {},
  yarnTestResult: {},
  sparkThriftTestResult: {},
  carbonTestResult: {},
  hiveServerTestResult: {},
  libraSqlTestResult: {},
  tidbSqlTestResult: {},
  oracleSqlTestResult: {},
  impalaSqlTestResult: {},
  sftpTestResult: {},
  greenPlumSqlTestResult: {}
};
exports.DEFAULT_COMP_TEST = DEFAULT_COMP_TEST;
var DEFAULT_COMP_REQUIRED = {
  // 必填默认数据
  flinkShowRequired: false,
  sparkShowRequired: false,
  dtYarnShellShowRequired: false,
  learningShowRequired: false,
  hdfsShowRequired: false,
  yarnShowRequired: false,
  hiveShowRequired: false,
  carbonShowRequired: false,
  hiveServerShowRequired: false,
  libraShowRequired: false,
  impalaSqlRequired: false,
  sftpShowRequired: false
};
exports.DEFAULT_COMP_REQUIRED = DEFAULT_COMP_REQUIRED;
var HADOOP_GROUP_VALUE = [// hadoop 引擎支持的组件类型(复选框)
{
  label: 'HDFS',
  value: COMPONENT_TYPE_VALUE.HDFS,
  disabled: true
}, {
  label: 'YARN',
  value: COMPONENT_TYPE_VALUE.YARN,
  disabled: true
}, {
  label: 'SFTP',
  value: COMPONENT_TYPE_VALUE.SFTP,
  disabled: true
}, {
  label: 'Flink',
  value: COMPONENT_TYPE_VALUE.FLINK
}, {
  label: 'Spark',
  value: COMPONENT_TYPE_VALUE.SPARK
}, {
  label: 'Learning',
  value: COMPONENT_TYPE_VALUE.LEARNING
}, {
  label: 'DTScript',
  value: COMPONENT_TYPE_VALUE.DTYARNSHELL
}, // DTYarnShell => DTScript
{
  label: 'SparkThrift',
  value: COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER
}, {
  label: 'CarbonData ThriftServer',
  value: COMPONENT_TYPE_VALUE.CARBONDATA
}, {
  label: 'Hive Server',
  value: COMPONENT_TYPE_VALUE.HIVE_SERVER
}, {
  label: 'Impala SQL',
  value: COMPONENT_TYPE_VALUE.IMPALA_SQL
}];
exports.HADOOP_GROUP_VALUE = HADOOP_GROUP_VALUE;
var API_MODE = {
  GUIDE: 0,
  SQL: 1
};
exports.API_MODE = API_MODE;
var API_METHOD = {
  POST: 1 // GET:2

};
/* eslint-disable-next-line */

exports.API_METHOD = API_METHOD;
var API_METHOD_key = {
  1: 'POST',
  2: 'GET'
};
exports.API_METHOD_key = API_METHOD_key;
var API_STATUS = {
  '-1': 'NO_APPLY',
  '0': 'IN_HAND',
  '1': 'PASS',
  '2': 'REJECT',
  '3': 'STOPPED',
  '4': 'DISABLE',
  '5': 'EXPIRED'
};
exports.API_STATUS = API_STATUS;
var API_USER_STATUS = {
  'NO_APPLY': -1,
  'IN_HAND': 0,
  'PASS': 1,
  'REJECT': 2,
  'STOPPED': 3,
  'DISABLE': 4,
  'EXPIRED': 5
};
exports.API_USER_STATUS = API_USER_STATUS;
var API_SYSTEM_STATUS = {
  SUCCESS: 0,
  STOP: 1,
  EDITTING: 2
};
exports.API_SYSTEM_STATUS = API_SYSTEM_STATUS;
var API_DELETE = {
  'YES': 0,
  'NO': 1
};
exports.API_DELETE = API_DELETE;
var EXCHANGE_API_STATUS = {
  '-1': 'nothing',
  0: 'inhand',
  1: 'success',
  2: 'notPass',
  3: 'stop',
  4: 'disabled',
  5: 'expired'
};
exports.EXCHANGE_API_STATUS = EXCHANGE_API_STATUS;
var EXCHANGE_APPLY_STATUS = {
  0: 'notApproved',
  1: 'pass',
  2: 'rejected',
  3: 'stop',
  4: 'disabled',
  5: 'expired'
};
exports.EXCHANGE_APPLY_STATUS = EXCHANGE_APPLY_STATUS;
var EXCHANGE_ADMIN_API_STATUS = {
  0: 'success',
  1: 'stop',
  2: 'editting'
};
exports.EXCHANGE_ADMIN_API_STATUS = EXCHANGE_ADMIN_API_STATUS;
var dataSourceTypes = [// 数据源类型
'未知类型', 'MySql', 'Oracle', 'SQLServer', 'PostgreSQL', 'RDBMS', 'HDFS', 'Hive', 'HBase', 'FTP', 'MaxCompute', 'NFS']; // 检验各组件数据

exports.dataSourceTypes = dataSourceTypes;
var validateFlinkParams = [// flink
'flinkConf.flinkZkAddress', 'flinkConf.flinkHighAvailabilityStorageDir', 'flinkConf.flinkZkNamespace', 'flinkConf.gatewayHost', 'flinkConf.gatewayPort', 'flinkConf.gatewayJobName', 'flinkConf.deleteOnShutdown', 'flinkConf.randomJobNameSuffix', 'flinkConf.typeName', 'flinkConf.clusterMode', 'flinkConf.flinkJarPath', // 'flinkConf.flinkJobHistory',
// 'flinkConf.flinkJobHistory',
// 'flinkConf.flinkJobHistory',
// 'flinkConf.flinkPrincipal',
// 'flinkConf.flinkKeytabPath',
// 'flinkConf.flinkKrb5ConfPath',
// 'flinkConf.zkPrincipal',
// 'flinkConf.zkKeytabPath',
// 'flinkConf.zkLoginName',
'flinkConf.kerberosFile', 'flinkConf.flinkSessionSlotCount'];
exports.validateFlinkParams = validateFlinkParams;
var validateHiveParams = [// hive <=> Spark Thrift Server
'hiveConf.jdbcUrl', 'hiveConf.driverClassName', 'hiveConf.kerberosFile'];
exports.validateHiveParams = validateHiveParams;
var validateCarbonDataParams = [// carbonData
'carbonConf.jdbcUrl', 'carbonConf.kerberosFile'];
exports.validateCarbonDataParams = validateCarbonDataParams;
var validateImpalaSqlParams = [// impalaSql
'impalaSqlConf.jdbcUrl'];
exports.validateImpalaSqlParams = validateImpalaSqlParams;
var validateHiveServerParams = [// carbonData
'hiveServerConf.jdbcUrl', 'hiveServerConf.kerberosFile'];
exports.validateHiveServerParams = validateHiveServerParams;
var validateSparkParams = [// spark
'sparkConf.typeName', 'sparkConf.sparkYarnArchive', 'sparkConf.sparkSqlProxyPath', 'sparkConf.sparkPythonExtLibPath', // 'sparkConf.sparkPrincipal',
// 'sparkConf.sparkKeytabPath',
// 'sparkConf.sparkKrb5ConfPath',
// 'sparkConf.zkPrincipal',
// 'sparkConf.zkKeytabPath',
// 'sparkConf.zkLoginName',
'sparkConf.kerberosFile'];
exports.validateSparkParams = validateSparkParams;
var validateDtYarnShellParams = ['dtscriptConf.jlogstashRoot', 'dtscriptConf.javaHome', 'dtscriptConf.hadoopHomeDir', 'dtscriptConf.kerberosFile'];
exports.validateDtYarnShellParams = validateDtYarnShellParams;
var validateLearningParams = ['learningConf.learningPython3Path', 'learningConf.kerberosFile'];
exports.validateLearningParams = validateLearningParams;
var validateLibraParams = ['libraConf.jdbcUrl', 'libraConf.driverClassName'];
exports.validateLibraParams = validateLibraParams;
var validateSftpDataParams = [// carbonData
'sftpConf.host', 'sftpConf.port', 'sftpConf.path', 'sftpConf.username', 'sftpConf.password']; // 服务器传参与界面渲染 key_map

exports.validateSftpDataParams = validateSftpDataParams;
var SPARK_KEY_MAP = {
  'spark.yarn.appMasterEnv.PYSPARK_PYTHON': 'sparkYarnAppMasterEnvPYSPARK_PYTHON',
  'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON': 'sparkYarnAppMasterEnvPYSPARK_DRIVER_PYTHON'
};
exports.SPARK_KEY_MAP = SPARK_KEY_MAP;
var SPARK_KEY_MAP_DOTS = {
  'sparkYarnAppMasterEnvPYSPARK_PYTHON': 'spark.yarn.appMasterEnv.PYSPARK_PYTHON',
  'sparkYarnAppMasterEnvPYSPARK_DRIVER_PYTHON': 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON'
};
exports.SPARK_KEY_MAP_DOTS = SPARK_KEY_MAP_DOTS;
var DTYARNSHELL_KEY_MAP = {
  // comm
  'java.home': 'javaHome',
  'hadoop.home.dir': 'hadoopHomeDir',
  // python
  'python2.path': 'python2Path',
  'python3.path': 'python3Path',
  // jupyter
  'jupyter.path': 'jupyterPath',
  'c.NotebookApp.open_browser': 'cNotebookAppOpen_browser',
  'c.NotebookApp.allow_remote_access': 'cNotebookAppAllow_remote_access',
  'c.NotebookApp.ip': 'cNotebookAppIp',
  'c.NotebookApp.token': 'cNotebookAppToken',
  'c.NotebookApp.default_url': 'cNotebookAppDefault_url',
  'jupyter.project.root': 'jupyterProjectRoot'
};
exports.DTYARNSHELL_KEY_MAP = DTYARNSHELL_KEY_MAP;
var DTYARNSHELL_KEY_MAP_DOTS = {
  // comm
  'javaHome': 'java.home',
  'hadoopHomeDir': 'hadoop.home.dir',
  // python
  'python2Path': 'python2.path',
  'python3Path': 'python3.path',
  // jupyter
  'jupyterPath': 'jupyter.path',
  'cNotebookAppOpen_browser': 'c.NotebookApp.open_browser',
  'cNotebookAppAllow_remote_access': 'c.NotebookApp.allow_remote_access',
  'cNotebookAppIp': 'c.NotebookApp.ip',
  'cNotebookAppToken': 'c.NotebookApp.token',
  'cNotebookAppDefault_url': 'c.NotebookApp.default_url',
  'jupyterProjectRoot': 'jupyter.project.root'
};
exports.DTYARNSHELL_KEY_MAP_DOTS = DTYARNSHELL_KEY_MAP_DOTS;
var FLINK_KEY_MAP = {
  'yarn.jobmanager.heap.mb': 'yarnJobmanagerHeapMb',
  'yarn.taskmanager.heap.mb': 'yarnTaskmanagerHeapMb',
  'yarn.taskmanager.numberOfTaskSlots': 'yarnTaskmanagerNumberOfTaskSlots',
  'yarn.taskmanager.numberOfTaskManager': 'yarnTaskmanagerNumberOfTaskManager',
  // prometheus相关
  'metrics.reporter.promgateway.class': 'metricsReporterPromgatewayClass',
  'metrics.reporter.promgateway.host': 'metricsReporterPromgatewayHost',
  'metrics.reporter.promgateway.port': 'metricsReporterPromgatewayPort',
  'metrics.reporter.promgateway.jobName': 'metricsReporterPromgatewayJobName',
  'metrics.reporter.promgateway.randomJobNameSuffix': 'metricsReporterPromgatewayRandomJobNameSuffix',
  'metrics.reporter.promgateway.deleteOnShutdown': 'metricsReporterPromgatewayDeleteOnShutdown',
  // flinkJobHistory =>
  'historyserver.web.address': 'historyserverWebAddress',
  'historyserver.web.port': 'historyserverWebPort',
  'high-availability.cluster-id': 'high-availabilityCluster-id',
  'high-availability.zookeeper.path.root': 'high-availabilityZookeeperPathRoot',
  'high-availability.zookeeper.quorum': 'high-availabilityZookeeperQuorum',
  'jobmanager.archive.fs.dir': 'jobmanagerArchiveFsDir',
  'high-availability.storageDir': 'high-availabilityStorageDir'
};
exports.FLINK_KEY_MAP = FLINK_KEY_MAP;
var FLINK_KEY_MAP_DOTS = {
  'yarnJobmanagerHeapMb': 'yarn.jobmanager.heap.mb',
  'yarnTaskmanagerHeapMb': 'yarn.taskmanager.heap.mb',
  'yarnTaskmanagerNumberOfTaskSlots': 'yarn.taskmanager.numberOfTaskSlots',
  'yarnTaskmanagerNumberOfTaskManager': 'yarn.taskmanager.numberOfTaskManager',
  'stateCheckpointsDir': 'state.checkpoints.dir',
  'stateCheckpointsNum-retained': 'state.checkpoints.num-retained',
  // prometheus相关
  'metricsReporterPromgatewayClass': 'metrics.reporter.promgateway.class',
  'metricsReporterPromgatewayHost': 'metrics.reporter.promgateway.host',
  'metricsReporterPromgatewayPort': 'metrics.reporter.promgateway.port',
  'metricsReporterPromgatewayJobName': 'metrics.reporter.promgateway.jobName',
  'metricsReporterPromgatewayRandomJobNameSuffix': 'metrics.reporter.promgateway.randomJobNameSuffix',
  'metricsReporterPromgatewayDeleteOnShutdown': 'metrics.reporter.promgateway.deleteOnShutdown',
  // flinkJobHistory =>
  'historyserverWebAddress': 'historyserver.web.address',
  'historyserverWebPort': 'historyserver.web.port',
  'high-availabilityCluster-id': 'high-availability.cluster-id',
  'high-availabilityZookeeperPathRoot': 'high-availability.zookeeper.path.root',
  'high-availabilityZookeeperQuorum': 'high-availability.zookeeper.quorum',
  'jobmanagerArchiveFsDir': 'jobmanager.archive.fs.dir',
  'high-availabilityStorageDir': 'high-availability.storageDir'
}; // 非用户自定义参数

exports.FLINK_KEY_MAP_DOTS = FLINK_KEY_MAP_DOTS;
var notExtKeysFlink = ['typeName', // 'high-availability',
'high-availability.zookeeper.quorum', 'high-availability.storageDir', 'high-availability.zookeeper.path.root', 'metrics.reporter.promgateway.class', 'metrics.reporter.promgateway.host', 'metrics.reporter.promgateway.port', 'metrics.reporter.promgateway.jobName', 'metrics.reporter.promgateway.randomJobNameSuffix', 'metrics.reporter.promgateway.deleteOnShutdown', 'jarTmpDir', 'flinkPluginRoot', 'remotePluginRootDir', 'clusterMode', 'flinkJarPath', 'historyserver.web.address', 'historyserver.web.port', 'high-availability.cluster-id', // 'flinkPrincipal', 'flinkKeytabPath', 'flinkKrb5ConfPath',
// 'zkPrincipal', 'zkKeytabPath', 'zkLoginName',
'yarn.jobmanager.heap.mb', 'yarn.taskmanager.heap.mb', 'yarn.taskmanager.numberOfTaskSlots', 'yarn.taskmanager.numberOfTaskManager', 'openKerberos', 'kerberosFile', 'flinkSessionSlotCount', 'state.checkpoints.dir', // 'jobmanagerArchiveFsDir',
'jobmanager.archive.fs.dir', 'state.checkpoints.num-retained'];
exports.notExtKeysFlink = notExtKeysFlink;
var notExtKeysSpark = ['typeName', 'sparkYarnArchive', 'sparkSqlProxyPath', 'sparkPythonExtLibPath', 'spark.yarn.appMasterEnv.PYSPARK_PYTHON', 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON', // 'sparkPrincipal', 'sparkKeytabPath',
// 'sparkKrb5ConfPath', 'zkPrincipal', 'zkKeytabPath', 'zkLoginName',
'openKerberos', 'kerberosFile'];
exports.notExtKeysSpark = notExtKeysSpark;
var notExtKeysLearning = ['typeName', 'learning.python3.path', 'learning.python2.path', 'learning.history.address', 'learning.history.webapp.address', 'learning.history.webapp.https.address', 'openKerberos', 'kerberosFile']; // DTscript

exports.notExtKeysLearning = notExtKeysLearning;
var notExtKeysDtyarnShell = ['typeName', 'jlogstash.root', 'pythonConf', 'jupyterConf', 'java.home', 'hadoop.home.dir', 'openKerberos', 'kerberosFile'];
exports.notExtKeysDtyarnShell = notExtKeysDtyarnShell;
var notExtKeyDtscriptPython = ['typeName', 'python2.path', 'python3.path'];
exports.notExtKeyDtscriptPython = notExtKeyDtscriptPython;
var notExtKeyDtscriptJupter = ['typeName', 'jupyter.path', 'c.NotebookApp.open_browser', 'c.NotebookApp.allow_remote_access', 'c.NotebookApp.ip', 'c.NotebookApp.token', 'c.NotebookApp.default_url', 'jupyter.project.root'];
exports.notExtKeyDtscriptJupter = notExtKeyDtscriptJupter;
var notExtKeysSparkThrift = ['jdbcUrl', 'username', 'password', 'driverClassName', 'useConnectionPool', 'maxPoolSize', 'minPoolSize', 'initialPoolSize', 'jdbcIdel', 'maxRows', 'queryTimeout', 'checkTimeout', 'openKerberos', 'kerberosFile'];
exports.notExtKeysSparkThrift = notExtKeysSparkThrift;
var notExtKeysHiveServer = ['driverClassName', 'jdbcUrl', 'username', 'password', 'openKerberos', 'kerberosFile'];
exports.notExtKeysHiveServer = notExtKeysHiveServer;
var notExtKeysLibraSql = ['jdbcUrl', 'username', 'password', 'driverClassName', 'useConnectionPool', 'maxPoolSize', 'minPoolSize', 'initialPoolSize', 'jdbcIdel', 'maxRows', 'queryTimeout', 'checkTimeout'];
exports.notExtKeysLibraSql = notExtKeysLibraSql;
var notExtKeysTidbSql = ['jdbcUrl', 'username', 'password', 'driverClassName', 'useConnectionPool', 'maxPoolSize', 'minPoolSize', 'initialPoolSize', 'jdbcIdel', 'maxRows', 'queryTimeout', 'checkTimeout'];
exports.notExtKeysTidbSql = notExtKeysTidbSql;
var sourcetype = ['executor.memory', 'driver.memory', 'jobmanager.memory.mb', 'taskmanager.memory.mb', 'worker.memory', 'executor.memory'];
exports.sourcetype = sourcetype;
var formItemLayout = {
  // 表单常用布局
  labelCol: {
    xs: {
      span: 24
    },
    sm: {
      span: 6
    }
  },
  wrapperCol: {
    xs: {
      span: 24
    },
    sm: {
      span: 14
    }
  }
};
exports.formItemLayout = formItemLayout;
var formItemCenterLayout = {
  // center
  labelCol: {
    xs: {
      span: 24
    },
    sm: {
      span: 9
    }
  },
  wrapperCol: {
    xs: {
      span: 24
    },
    sm: {
      span: 6
    }
  }
};
exports.formItemCenterLayout = formItemCenterLayout;
var longLabelFormLayout = {
  labelCol: {
    xs: {
      span: 24
    },
    sm: {
      span: 10
    }
  },
  wrapperCol: {
    xs: {
      span: 24
    },
    sm: {
      span: 14
    }
  }
};
exports.longLabelFormLayout = longLabelFormLayout;
var specFormItemLayout = {
  // 表单对称布局
  labelCol: {
    xs: {
      span: 24
    },
    sm: {
      span: 8
    }
  },
  wrapperCol: {
    xs: {
      span: 24
    },
    sm: {
      span: 14
    }
  }
};
exports.specFormItemLayout = specFormItemLayout;
var tailFormItemLayout = {
  // 表单行label居中对齐
  wrapperCol: {
    xs: {
      span: 24,
      offset: 0
    },
    sm: {
      span: 14,
      offset: 6
    }
  }
};
exports.tailFormItemLayout = tailFormItemLayout;
var rowFormItemLayout = {
  // 单行末尾布局
  labelCol: {
    span: 0
  },
  wrapperCol: {
    span: 24
  }
};
exports.rowFormItemLayout = rowFormItemLayout;
var lineAreaChartOptions = {
  // 堆叠折现图默认选项
  title: {
    text: '堆叠区域图',
    textStyle: {
      fontSize: 12
    },
    textAlign: 'left'
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      label: {
        backgroundColor: '#6a7985'
      }
    }
  },
  color: ['#2491F7', '#7460EF', '#26DAD2', '#79E079', '#7A64F3', '#FFDC53', '#9a64fb'],
  legend: {
    data: ['邮件营销', '联盟广告', '视频广告', '直接访问', '搜索引擎']
  },
  toolbox: {
    feature: {
      saveAsImage: {
        show: false
      }
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: [{
    type: 'category',
    boundaryGap: false,
    data: [],
    axisTick: {
      show: true
    },
    axisLine: {
      lineStyle: {
        color: '#DDDDDD'
      }
    },
    axisLabel: {
      textStyle: {
        color: '#666666'
      }
    },
    nameTextStyle: {
      color: '#666666'
    },
    splitLine: {
      color: '#666666'
    }
  }],
  yAxis: [{
    type: 'value',
    axisLabel: {
      formatter: '{value} 个',
      textStyle: {
        color: '#666666',
        baseline: 'bottom'
      }
    },
    nameTextStyle: {
      color: '#666666'
    },
    nameLocation: 'end',
    nameGap: 20,
    axisLine: {
      show: false
    },
    axisTick: {
      show: false
    },
    splitLine: {
      lineStyle: {
        color: '#DDDDDD',
        type: 'dashed'
      }
    }
  }],
  series: []
};
exports.lineAreaChartOptions = lineAreaChartOptions;
var doubleLineAreaChartOptions = {
  // 堆叠折现图默认选项
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      label: {
        backgroundColor: '#6a7985'
      }
    }
  },
  color: ['#2491F7', '#7460EF', '#26DAD2', '#79E079', '#7A64F3', '#FFDC53', '#9a64fb'],
  legend: {
    data: ['邮件营销', '联盟广告', '视频广告', '直接访问', '搜索引擎']
  },
  toolbox: {
    feature: {
      saveAsImage: {
        show: false
      }
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '30',
    top: 40,
    containLabel: true
  },
  xAxis: [{
    type: 'category',
    boundaryGap: false,
    data: [],
    axisTick: {
      show: true
    },
    axisLine: {
      lineStyle: {
        color: '#DDDDDD'
      }
    },
    axisLabel: {
      textStyle: {
        color: '#666666'
      }
    },
    nameTextStyle: {
      color: '#666666'
    },
    splitLine: {
      color: '#666666'
    }
  }],
  yAxis: [{
    nameGap: 25,
    type: 'value',
    name: '调用次数',
    axisLabel: {
      textStyle: {
        color: '#666666',
        baseline: 'bottom'
      }
    },
    nameTextStyle: {
      color: '#666666'
    },
    nameLocation: 'end',
    axisLine: {
      show: false
    },
    axisTick: {
      show: false
    },
    splitLine: false,
    minInterval: 1
  }, {
    nameGap: 25,
    type: 'value',
    name: '失败率 (%)',
    axisLabel: {
      textStyle: {
        color: '#666666',
        baseline: 'bottom'
      }
    },
    nameTextStyle: {
      color: '#666666'
    },
    nameLocation: 'end',
    axisLine: {
      show: false
    },
    axisTick: {
      show: false
    },
    splitLine: {
      lineStyle: {
        color: '#DDDDDD',
        type: 'dashed'
      }
    },
    max: 100
  }],
  series: []
};
exports.doubleLineAreaChartOptions = doubleLineAreaChartOptions;
var pieOption = {
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'horizontal',
    x: 'center',
    y: 'bottom',
    data: ['参数错误', '调用超时', '异常访问', '超出限额', '禁用', '其他'],
    itemWidth: 5,
    itemHeight: 5,
    textStyle: {
      color: '#666'
    }
  },
  series: [{
    name: '错误类型',
    type: 'pie',
    radius: ['35%', '55%'],
    avoidLabelOverlap: false,
    label: {
      normal: {
        show: false,
        position: 'center'
      }
    },
    labelLine: {
      normal: {
        show: false
      }
    },
    data: [{
      value: 335,
      name: '参数错误',
      itemStyle: {
        normal: {
          color: '#1C86EE'
        }
      }
    }, {
      value: 310,
      name: '调用超时',
      itemStyle: {
        normal: {
          color: '#EE9A00'
        }
      }
    }, {
      value: 234,
      name: '异常访问',
      itemStyle: {
        normal: {
          color: '#EE4000'
        }
      }
    }, {
      value: 535,
      name: '超出限额',
      itemStyle: {
        normal: {
          color: '#40E0D0'
        }
      }
    }, {
      value: 1158,
      name: '禁用',
      itemStyle: {
        normal: {
          color: '#71C671'
        }
      }
    }, {
      value: 548,
      name: '其他',
      itemStyle: {
        normal: {
          color: '#A2B5CD'
        }
      }
    }]
  }]
};
exports.pieOption = pieOption;
var NUM_COMMA = /^[0-9,]+$/;
exports.NUM_COMMA = NUM_COMMA;
var PHONE_REG = /^1[3|4|5|6|7|8|9]\d{9}$/;
exports.PHONE_REG = PHONE_REG;
var EMAIL_COMMA = /^[0-9a-zA-Z@,_.-]+$/;
exports.EMAIL_COMMA = EMAIL_COMMA;
var EMAIL_REG = /^[0-9a-zA-Z_.-]+[@][0-9a-zA-Z_.-]+([.][a-zA-Z]+){1,2}$/;
exports.EMAIL_REG = EMAIL_REG;
var RESOURCE_TYPE = {
  YARN: 'YARN',
  KUBERNETES: 'Kubernetes'
};
exports.RESOURCE_TYPE = RESOURCE_TYPE;

/***/ }),

/***/ "UANx":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("yq1k");

__webpack_require__("yXV3");

__webpack_require__("J30X");

__webpack_require__("2B1R");

__webpack_require__("qePV");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("5s+n");

__webpack_require__("p532");

__webpack_require__("rB9j");

__webpack_require__("JTJg");

__webpack_require__("UxlC");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _tooltip = _interopRequireDefault(__webpack_require__("d1El"));

var _toConsumableArray2 = _interopRequireDefault(__webpack_require__("RIqP"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _slicedToArray2 = _interopRequireDefault(__webpack_require__("J4zp"));

var _extends2 = _interopRequireDefault(__webpack_require__("pVnL"));

var _input = _interopRequireDefault(__webpack_require__("iJl9"));

var _icon = _interopRequireDefault(__webpack_require__("Pbn2"));

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var _select = _interopRequireDefault(__webpack_require__("FAat"));

var _react = _interopRequireWildcard(__webpack_require__("q1tI"));

var _customHooks = __webpack_require__("A9pX");

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

var _consts = __webpack_require__("RzPm");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

var Option = _select["default"].Option;
var FormItem = _form["default"].Item;
var confirm = _modal["default"].confirm;

var DynamicForm = function DynamicForm(props) {
  var getFieldDecorator = props.getFieldDecorator,
      lineList = props.lineList,
      taskTypeName = props.taskTypeName,
      deleteItem = props.deleteItem,
      type = props.type,
      initialList = props.initialList;
  var lineTitle = Object.keys(lineList);
  return /*#__PURE__*/_react["default"].createElement(_react["default"].Fragment, null, /*#__PURE__*/_react["default"].createElement("header", {
    className: "c-header-dynamicform"
  }, /*#__PURE__*/_react["default"].createElement("span", null, "".concat(taskTypeName, "\u4EFB\u52A1")), /*#__PURE__*/_react["default"].createElement("span", {
    style: {
      cursor: 'pointer'
    },
    onClick: function onClick() {
      return deleteItem(type);
    }
  }, /*#__PURE__*/_react["default"].createElement(_icon["default"], {
    type: "delete"
  }))), /*#__PURE__*/_react["default"].createElement("div", {
    style: {
      paddingBottom: 10
    }
  }, lineTitle.map(function (item) {
    return /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({
      key: item,
      label: item
    }, _consts.specFormItemLayout), getFieldDecorator("".concat(type, " ").concat(item.replace(/(\.)/g, '-')), {
      initialValue: (initialList === null || initialList === void 0 ? void 0 : initialList[item]) || undefined
    })( /*#__PURE__*/_react["default"].createElement(_input["default"], {
      placeholder: lineList[item]
    })), /*#__PURE__*/_react["default"].createElement("span", {
      style: {
        position: 'absolute',
        right: -20
      }
    }, _consts.sourcetype.includes(item) ? 'm' : ''));
  })));
};

var CustomModal = function CustomModal(props) {
  var form = props.form,
      _props$form = props.form,
      getFieldDecorator = _props$form.getFieldDecorator,
      setFieldsValue = _props$form.setFieldsValue,
      resetFields = _props$form.resetFields,
      visible = props.visible,
      onOk = props.onOk,
      _onCancel = props.onCancel,
      title = props.title,
      isBindTenant = props.isBindTenant,
      clusterId = props.clusterId,
      tenantId = props.tenantId,
      queueId = props.queueId,
      clusterList = props.clusterList;

  var _useState = (0, _react.useState)(false),
      _useState2 = (0, _slicedToArray2["default"])(_useState, 2),
      isLoading = _useState2[0],
      setLoading = _useState2[1];

  var _useState3 = (0, _react.useState)({
    current: undefined,
    union: []
  }),
      _useState4 = (0, _slicedToArray2["default"])(_useState3, 2),
      typeList = _useState4[0],
      setTypeList = _useState4[1];

  var _useState5 = (0, _react.useState)([]),
      _useState6 = (0, _slicedToArray2["default"])(_useState5, 2),
      dataList = _useState6[0],
      setDataList = _useState6[1];

  var _useState7 = (0, _react.useState)({}),
      _useState8 = (0, _slicedToArray2["default"])(_useState7, 2),
      initialList = _useState8[0],
      setInitialList = _useState8[1];

  var prevVisible = (0, _react.useRef)(null);

  var _useEnv = (0, _customHooks.useEnv)({
    clusterId: clusterId,
    form: form,
    visible: visible,
    clusterList: clusterList
  }),
      queueList = _useEnv.queueList; // 切换集群


  (0, _react.useEffect)(function () {
    if (prevVisible.current !== visible) {
      if (visible) {
        _console["default"].queryTaskResourceLimits({
          dtUicTenantId: tenantId
        }).then(function (res) {
          var code = res.code,
              data = res.data;

          if (code === 1) {
            var union = [];
            var biginitial = {};
            Array.isArray(data) && data.forEach(function (item) {
              union.push(item.taskType);
              biginitial["".concat(item.taskType)] = item.resourceLimit; // setInitialList({...initialList,[`${item.taskType}`]:item.resourceLimit})
            });
            setInitialList(biginitial);
            setTypeList(function (prev) {
              return _objectSpread(_objectSpread({}, prev), {}, {
                union: union
              });
            });
          }
        });

        _console["default"].getTaskResourceTemplate({}).then(function (res) {
          if (res.code === 1) {
            setDataList(res.data);
          }
        });
      } else {
        resetFields();
        setTypeList({
          current: undefined,
          union: []
        });
        setDataList([]);
      }
    }

    prevVisible.current = visible;
  }, [resetFields, tenantId, visible]);

  var getServiceParam = function getServiceParam() {
    var _props$form2 = props === null || props === void 0 ? void 0 : props.form,
        validateFields = _props$form2.validateFields;

    validateFields(function (err, value) {
      if (!err) {
        var taskTypeResourceJson = JSON.stringify(typeList.union.map(function (item) {
          return dataList.map(function (task) {
            if (item === task.taskType) {
              var blockList = Object.keys(task.params);
              var params = {
                taskType: item,
                resourceParams: {}
              };
              blockList.forEach(function (head) {
                params.resourceParams[head] = value === null || value === void 0 ? void 0 : value["".concat(item, " ").concat(head).replace(/(\.)/g, '-')];
              });
              return params;
            }
          });
        }).map(function (arrayItem) {
          return arrayItem.filter(function (element) {
            return element !== undefined;
          })[0];
        }));
        setLoading(true);

        _console["default"].switchQueue({
          queueId: value === null || value === void 0 ? void 0 : value.queueId,
          tenantId: tenantId,
          taskTypeResourceJson: taskTypeResourceJson
        }).then(function (res) {
          if (res.code === 1) {
            _message2["default"].success('提交成功');

            return onOk();
          }

          _message2["default"].error('提交失败');
        })["finally"](function () {
          setLoading(false);
        });
      }
    });
  };

  var addTaskType = function addTaskType() {
    var current = typeList.current,
        union = typeList.union;

    if (typeof current === 'undefined' || isNaN(current)) {
      _message2["default"].warning('请先选择任务类型');

      return;
    }

    if (union.length !== 0 && union.includes(current)) {
      _message2["default"].warning('该任务的资源限制已存在！');

      return;
    }

    setTypeList({
      current: current,
      union: [].concat((0, _toConsumableArray2["default"])(union), [current])
    });
    setFieldsValue({
      resourceType: current
    });
  };

  var changeCurrent = function changeCurrent(e) {
    setTypeList(_objectSpread(_objectSpread({}, typeList), {}, {
      current: Number(e)
    }));
  };

  var actionDom = function actionDom() {
    return /*#__PURE__*/_react["default"].createElement("div", {
      className: "o-div--actionDom",
      onClick: addTaskType
    }, /*#__PURE__*/_react["default"].createElement(_icon["default"], {
      className: "o-icon--actionDom",
      type: "plus-circle"
    }), "\u6DFB\u52A0\u8D44\u6E90\u9650\u5236");
  };

  var removeType = function removeType(type) {
    var union = typeList.union;

    if (typeof type !== 'undefined') {
      var deleteIndex = union.indexOf(type);
      deleteIndex !== -1 && setTypeList(_objectSpread(_objectSpread({}, typeList), {}, {
        union: (0, _toConsumableArray2["default"])(union.filter(function (numType) {
          return numType !== type;
        }))
      }));
    }
  };

  var returnPromject = function returnPromject() {
    confirm({
      title: '是否保存配置？',
      content: '若不保存编辑内容，再次打开时会进行重置',
      okText: '保存',
      cancelText: '返回',
      onOk: function onOk() {
        getServiceParam();
      },
      onCancel: function onCancel() {
        _onCancel();
      }
    });
  };

  return /*#__PURE__*/_react["default"].createElement(_modal["default"], {
    title: title,
    visible: visible,
    onOk: function onOk() {
      return getServiceParam();
    },
    onCancel: function onCancel() {
      return returnPromject();
    },
    width: "600px",
    confirmLoading: isLoading,
    className: isBindTenant ? 'no-padding-modal' : ''
  }, /*#__PURE__*/_react["default"].createElement(_form["default"], null, /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({
    label: /*#__PURE__*/_react["default"].createElement("span", null, "\u8D44\u6E90\u961F\u5217\xA0", /*#__PURE__*/_react["default"].createElement(_tooltip["default"], {
      title: "\u6307Yarn\u4E0A\u5206\u914D\u7684\u8D44\u6E90\u961F\u5217\uFF0C\u82E5\u4E0B\u62C9\u5217\u8868\u4E2D\u65E0\u5168\u90E8\u961F\u5217\uFF0C\u8BF7\u524D\u5F80\u201C\u591A\u96C6\u7FA4\u7BA1\u7406\u201D\u9875\u9762\u7684\u5177\u4F53\u96C6\u7FA4\u4E2D\u5237\u65B0\u96C6\u7FA4"
    }, /*#__PURE__*/_react["default"].createElement(_icon["default"], {
      type: "question-circle-o"
    })))
  }, _consts.formItemLayout), getFieldDecorator('queueId', {
    rules: [{
      required: true,
      message: '租户不可为空！'
    }],
    initialValue: queueId || undefined
  })( /*#__PURE__*/_react["default"].createElement(_select["default"], {
    allowClear: true,
    placeholder: "\u8BF7\u9009\u62E9\u8D44\u6E90\u961F\u5217"
  }, queueList.map(function (item) {
    return /*#__PURE__*/_react["default"].createElement(Option, {
      key: item.queueId,
      value: item.queueId
    }, item.queueName);
  })))), /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({
    label: /*#__PURE__*/_react["default"].createElement("span", null, "\u8D44\u6E90\u9650\u5236\xA0", /*#__PURE__*/_react["default"].createElement(_tooltip["default"], {
      title: "\u8BBE\u7F6E\u79DF\u6237\u4E0B\u5355\u4E2A\u79BB\u7EBF\u4EFB\u52A1\u5728\u4E34\u65F6\u8FD0\u884C\u548C\u5468\u671F\u8FD0\u884C\u65F6\u80FD\u4F7F\u7528\u7684\u6700\u5927\u8D44\u6E90\u6570\uFF0C\u4EFB\u52A1\u7684\u73AF\u5883\u53C2\u6570\u8BBE\u7F6E\u8D85\u51FA\u6B64\u9650\u5236\u5C06\u5BFC\u81F4\u4EFB\u52A1\u63D0\u4EA4\u6216\u8FD0\u884C\u5931\u8D25\u3002\u4FDD\u5B58\u53D8\u66F4\u540E\u7ACB\u5373\u751F\u6548\u3002"
    }, /*#__PURE__*/_react["default"].createElement(_icon["default"], {
      type: "question-circle-o"
    })))
  }, _consts.formItemLayout), getFieldDecorator('resourceType', {})( /*#__PURE__*/_react["default"].createElement(_select["default"], {
    allowClear: true,
    placeholder: "\u8BF7\u9009\u62E9\u4EFB\u52A1\u7C7B\u578B",
    onChange: function onChange(e) {
      return changeCurrent(e);
    }
  }, dataList.map(function (item) {
    return /*#__PURE__*/_react["default"].createElement(Option, {
      key: item.taskType,
      value: item.taskType
    }, item.taskTypeName);
  }))), actionDom()), typeList.union.map(function (item) {
    return dataList.map(function (type, key) {
      return /*#__PURE__*/_react["default"].createElement("div", {
        key: key,
        className: "o-block--dynamic"
      }, type.taskType === item ? /*#__PURE__*/_react["default"].createElement(DynamicForm, {
        type: item,
        getFieldDecorator: getFieldDecorator,
        lineList: type.params,
        taskTypeName: type.taskTypeName,
        deleteItem: removeType,
        initialList: initialList[item]
      }) : null);
    });
  })));
};

var areEqual = function areEqual(prevprops, nextprops) {
  if ((prevprops === null || prevprops === void 0 ? void 0 : prevprops.visible) !== (nextprops === null || nextprops === void 0 ? void 0 : nextprops.visible) || (nextprops === null || nextprops === void 0 ? void 0 : nextprops.visible) === true) return false;
  return true;
};

var _default = _form["default"].create()( /*#__PURE__*/_react["default"].memo(CustomModal, areEqual));

exports["default"] = _default;

/***/ }),

/***/ "Uu3x":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("DQNa");

__webpack_require__("wLYn");

__webpack_require__("zKZe");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _table = _interopRequireDefault(__webpack_require__("DtFj"));

var _pagination = _interopRequireDefault(__webpack_require__("s4l/"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _popconfirm = _interopRequireDefault(__webpack_require__("h0/l"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _reactRouter = __webpack_require__("dtw8");

var _moment = _interopRequireDefault(__webpack_require__("wd/R"));

var _addEngineModal = _interopRequireDefault(__webpack_require__("C+lx"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var PAGE_SIZE = 15;

var ClusterManage = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(ClusterManage, _React$Component);

  var _super = _createSuper(ClusterManage);

  function ClusterManage() {
    var _this;

    (0, _classCallCheck2["default"])(this, ClusterManage);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      dataSource: [],
      table: {
        pageIndex: 1,
        total: 0,
        loading: true
      },
      newClusterModal: false,
      editModalKey: ''
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "newCluster", function () {
      _this.setState({
        editModalKey: Math.random(),
        newClusterModal: true
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleTableChange", function (pagination, filters, sorter) {
      var queryParams = Object.assign(_this.state.table, {
        loading: true
      });

      _this.setState({
        table: queryParams
      }, _this.getResourceList);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onPageChange", function (current) {
      _this.setState({
        table: Object.assign(_this.state.table, {
          pageIndex: current,
          loading: true
        })
      }, _this.getResourceList);
    });
    return _this;
  }

  (0, _createClass2["default"])(ClusterManage, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      this.getResourceList();
    }
  }, {
    key: "getResourceList",
    value: function getResourceList() {
      var _this2 = this;

      var table = this.state.table;
      var pageIndex = table.pageIndex;

      _console["default"].getClusterList({
        currentPage: pageIndex,
        pageSize: PAGE_SIZE
      }).then(function (res) {
        if (res.code == 1) {
          _this2.setState({
            dataSource: res.data.data,
            table: _objectSpread(_objectSpread({}, table), {}, {
              loading: false,
              total: res.data.totalCount
            })
          });
        } else {
          _this2.setState({
            table: _objectSpread(_objectSpread({}, table), {}, {
              loading: false
            })
          });
        }
      });
    }
  }, {
    key: "getPagination",
    value: function getPagination() {
      var _this$state$table = this.state.table,
          pageIndex = _this$state$table.pageIndex,
          total = _this$state$table.total;
      return {
        current: pageIndex,
        pageSize: PAGE_SIZE,
        total: total
      };
    }
  }, {
    key: "initTableColumns",
    value: function initTableColumns() {
      var _this3 = this;

      return [{
        title: '集群名称',
        dataIndex: 'clusterName'
      }, {
        title: '修改时间',
        dataIndex: 'gmtModified',
        render: function render(text) {
          return (0, _moment["default"])(text).format('YYYY-MM-DD HH:mm:ss');
        }
      }, {
        title: '操作',
        dataIndex: 'deal',
        width: '170px',
        render: function render(text, record) {
          return /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("a", {
            onClick: _this3.viewCluster.bind(_this3, record)
          }, "\u67E5\u770B"), /*#__PURE__*/React.createElement("span", {
            className: "ant-divider"
          }), /*#__PURE__*/React.createElement(_popconfirm["default"], {
            placement: "topRight",
            title: "\u5220\u9664\u96C6\u7FA4\u540E\u4E0D\u53EF\u6062\u590D\uFF0C\u786E\u8BA4\u5220\u9664\u96C6\u7FA4 ".concat(record.clusterName, "?"),
            onConfirm: _this3.deleteCluster.bind(_this3, record),
            okText: "\u786E\u8BA4",
            cancelText: "\u53D6\u6D88"
          }, /*#__PURE__*/React.createElement("a", null, "\u5220\u9664")));
        }
      }];
    }
  }, {
    key: "deleteCluster",
    value: function deleteCluster(item) {
      var _this4 = this;

      _console["default"].deleteCluster({
        clusterId: item.clusterId
      }).then(function (res) {
        if (res.code === 1) {
          _message2["default"].success('集群删除成功');

          _this4.getResourceList();
        }
      });
    }
  }, {
    key: "viewCluster",
    value: function viewCluster(item) {
      _reactRouter.hashHistory.push({
        pathname: '/console/clusterManage/editCluster',
        state: {
          cluster: item,
          mode: 'view'
        }
      });
    }
  }, {
    key: "onCancel",
    value: function onCancel() {
      this.setState({
        newClusterModal: false
      });
    }
  }, {
    key: "onSubmit",
    value: function onSubmit(params) {
      var _this5 = this;

      _console["default"].addCluster(_objectSpread({}, params)).then(function (res) {
        if (res.code === 1) {
          _this5.onCancel();

          _reactRouter.hashHistory.push({
            pathname: '/console/clusterManage/editCluster',
            state: {
              mode: 'new',
              cluster: res.data
            }
          });

          _message2["default"].success('集群新增成功！');
        }
      });
    }
  }, {
    key: "render",
    value: function render() {
      var _this$state = this.state,
          dataSource = _this$state.dataSource,
          table = _this$state.table,
          newClusterModal = _this$state.newClusterModal,
          editModalKey = _this$state.editModalKey;
      var loading = table.loading;
      var columns = this.initTableColumns();
      var pagination = {
        total: table.total,
        current: table.pageIndex,
        pageSize: PAGE_SIZE,
        size: 'small',
        showTotal: function showTotal(total) {
          return /*#__PURE__*/React.createElement("span", null, "\u5171", /*#__PURE__*/React.createElement("span", {
            style: {
              color: '#3F87FF'
            }
          }, total), "\u6761\u6570\u636E\uFF0C\u6BCF\u9875\u663E\u793A", PAGE_SIZE, "\u6761");
        },
        onChange: this.onPageChange
      };
      return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("div", {
        className: "c-clusterManage__title"
      }, /*#__PURE__*/React.createElement("span", {
        className: "c-clusterManage__title__span"
      }, "\u591A\u96C6\u7FA4\u7BA1\u7406"), /*#__PURE__*/React.createElement(_button["default"], {
        className: "c-clusterManage__title__btn",
        type: "primary",
        onClick: this.newCluster
      }, "\u65B0\u589E\u96C6\u7FA4")), /*#__PURE__*/React.createElement("div", {
        className: "contentBox"
      }, /*#__PURE__*/React.createElement(_table["default"], {
        rowKey: function rowKey(record, index) {
          return "clusterManage-".concat(record.id);
        },
        className: "dt-table-fixed-contain-footer",
        scroll: {
          y: true
        },
        style: {
          height: 'calc(100vh - 150px)'
        },
        pagination: false,
        loading: loading,
        dataSource: dataSource,
        columns: columns,
        onChange: this.handleTableChange,
        footer: function footer() {
          return /*#__PURE__*/React.createElement(_pagination["default"], pagination);
        }
      })), /*#__PURE__*/React.createElement(_addEngineModal["default"], {
        key: editModalKey,
        title: "\u65B0\u589E\u96C6\u7FA4",
        visible: newClusterModal,
        onCancel: this.onCancel.bind(this),
        onOk: this.onSubmit.bind(this)
      }));
    }
  }]);
  return ClusterManage;
}(React.Component);

var _default = ClusterManage;
exports["default"] = _default;

/***/ }),

/***/ "V2yy":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("DQNa");

__webpack_require__("zKZe");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _icon = _interopRequireDefault(__webpack_require__("Pbn2"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _reactRouter = __webpack_require__("dtw8");

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var GoBack = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(GoBack, _React$Component);

  var _super = _createSuper(GoBack);

  function GoBack() {
    var _this;

    (0, _classCallCheck2["default"])(this, GoBack);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "go", function () {
      var _this$props = _this.props,
          url = _this$props.url,
          history = _this$props.history,
          autoClose = _this$props.autoClose;

      if (url) {
        if (history) {
          _reactRouter.browserHistory.push(url);
        } else {
          _reactRouter.hashHistory.push(url);
        }
      } else {
        if (window.history.length == 1) {
          if (autoClose) {
            window.close();
          }
        } else {
          _reactRouter.hashHistory.go(-1);
        }
      }
    });
    return _this;
  }

  (0, _createClass2["default"])(GoBack, [{
    key: "getButtonView",
    value: function getButtonView() {
      var style = this.props.style;
      var iconStyle = {
        cursor: 'pointer',
        fontFamily: 'anticon',
        fontSize: '18px',
        color: 'rgb(148, 168, 198)',
        letterSpacing: '5px',
        position: 'relative',
        top: '2px'
      };

      if (style) {
        Object.assign(iconStyle, style);
      }

      return /*#__PURE__*/React.createElement("span", {
        style: iconStyle,
        onClick: this.go
      }, /*#__PURE__*/React.createElement(_icon["default"], {
        type: 'left-circle-o'
      }), this.props.children);
    }
  }, {
    key: "render",
    value: function render() {
      return this.getButtonView();
    }
  }]);
  return GoBack;
}(React.Component);

exports["default"] = GoBack;

/***/ }),

/***/ "VDrD":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("fbCW");

__webpack_require__("QWBl");

__webpack_require__("yq1k");

__webpack_require__("DQNa");

__webpack_require__("sMBO");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("JTJg");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _popconfirm = _interopRequireDefault(__webpack_require__("h0/l"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

var _const = __webpack_require__("j1Tt");

var _help = __webpack_require__("LNB4");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var ToolBar = /*#__PURE__*/function (_React$PureComponent) {
  (0, _inherits2["default"])(ToolBar, _React$PureComponent);

  var _super = _createSuper(ToolBar);

  function ToolBar() {
    var _this;

    (0, _classCallCheck2["default"])(this, ToolBar);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onOk", function () {
      var _comp$componentTypeCo;

      var _this$props = _this.props,
          form = _this$props.form,
          comp = _this$props.comp,
          clusterInfo = _this$props.clusterInfo,
          saveComp = _this$props.saveComp;
      var typeCode = (_comp$componentTypeCo = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo !== void 0 ? _comp$componentTypeCo : ''; // 整理相关参数, 更新初始值

      form.validateFields(null, {}, function (err, values) {
        var _currentComp$storeTyp, _currentComp$principa, _currentComp$principa2, _currentComp$hadoopVe, _currentComp$uploadFi, _currentComp$kerberos, _currentComp$kerberos2;

        console.log(err, values);

        if (err && Object.keys(err).includes(String(typeCode))) {
          _message2["default"].error('请检查配置');

          return;
        }
        /**
         * componentTemplate yarn等组件直接传自定义参数，其他组件需处理自定义参数和入group中
         * componentConfig yarn等组件传值specialConfig，合并自定义参数，其他组件需处理自定义参数合并到对应config中
         */


        var currentComp = values[typeCode];
        var componentConfig;
        if (!(0, _help.isNeedTemp)(typeCode)) componentConfig = JSON.stringify((0, _help.handleComponentConfigAndCustom)(values[typeCode], typeCode));

        if ((0, _help.isNeedTemp)(typeCode)) {
          componentConfig = JSON.stringify(_objectSpread(_objectSpread({}, currentComp === null || currentComp === void 0 ? void 0 : currentComp.specialConfig), (0, _help.handleCustomParam)(currentComp.customParam, true)));
        }

        if ((0, _help.isKubernetes)(typeCode)) componentConfig = JSON.stringify(currentComp === null || currentComp === void 0 ? void 0 : currentComp.specialConfig);
        var params = {
          storeType: (_currentComp$storeTyp = currentComp === null || currentComp === void 0 ? void 0 : currentComp.storeType) !== null && _currentComp$storeTyp !== void 0 ? _currentComp$storeTyp : '',
          principal: (_currentComp$principa = currentComp === null || currentComp === void 0 ? void 0 : currentComp.principal) !== null && _currentComp$principa !== void 0 ? _currentComp$principa : '',
          principals: (_currentComp$principa2 = currentComp === null || currentComp === void 0 ? void 0 : currentComp.principals) !== null && _currentComp$principa2 !== void 0 ? _currentComp$principa2 : [],
          hadoopVersion: (_currentComp$hadoopVe = currentComp.hadoopVersion) !== null && _currentComp$hadoopVe !== void 0 ? _currentComp$hadoopVe : '',
          componentTemplate: (0, _help.isNeedTemp)(typeCode) ? !currentComp.customParam ? '[]' : JSON.stringify((0, _help.handleCustomParam)(currentComp.customParam)) : JSON.stringify((0, _help.handleComponentTemplate)(values[typeCode], comp)),
          componentConfig: componentConfig
        };
        /**
         * TODO LIST
         * resources2, kerberosFileName 这个两个参数后期可以去掉
         * 保存组件后不加上组件id，防止出现上传文件后立即点击不能下载的现象，后续交互优化
         */

        _console["default"].saveComponent(_objectSpread(_objectSpread({}, params), {}, {
          clusterId: clusterInfo.clusterId,
          componentCode: typeCode,
          clusterName: clusterInfo.clusterName,
          resources1: (_currentComp$uploadFi = currentComp === null || currentComp === void 0 ? void 0 : currentComp.uploadFileName) !== null && _currentComp$uploadFi !== void 0 ? _currentComp$uploadFi : '',
          resources2: '',
          kerberosFileName: (_currentComp$kerberos = currentComp === null || currentComp === void 0 ? void 0 : (_currentComp$kerberos2 = currentComp.kerberosFileName) === null || _currentComp$kerberos2 === void 0 ? void 0 : _currentComp$kerberos2.name) !== null && _currentComp$kerberos !== void 0 ? _currentComp$kerberos : ''
        })).then(function (res) {
          if (res.code == 1) {
            var _currentComp$uploadFi2, _currentComp$kerberos3;

            saveComp(_objectSpread(_objectSpread({}, params), {}, {
              // id: res.data.id,
              componentTypeCode: typeCode,
              uploadFileName: (_currentComp$uploadFi2 = currentComp === null || currentComp === void 0 ? void 0 : currentComp.uploadFileName) !== null && _currentComp$uploadFi2 !== void 0 ? _currentComp$uploadFi2 : '',
              kerberosFileName: (_currentComp$kerberos3 = currentComp === null || currentComp === void 0 ? void 0 : currentComp.kerberosFileName) !== null && _currentComp$kerberos3 !== void 0 ? _currentComp$kerberos3 : ''
            }));

            _message2["default"].success('保存成功');
          }
        });
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onConfirm", function () {
      var _comp$componentTypeCo2;

      var _this$props2 = _this.props,
          form = _this$props2.form,
          comp = _this$props2.comp,
          initialCompData = _this$props2.initialCompData;
      var typeCode = (_comp$componentTypeCo2 = comp === null || comp === void 0 ? void 0 : comp.componentTypeCode) !== null && _comp$componentTypeCo2 !== void 0 ? _comp$componentTypeCo2 : '';
      var initialComp = initialCompData.find(function (comp) {
        return comp.componentTypeCode == typeCode;
      });
      form.setFieldsValue((0, _defineProperty2["default"])({}, typeCode, {
        componentConfig: (0, _help.handleComponentConfig)({
          componentConfig: JSON.parse(initialComp.componentConfig)
        }, true) // customParam: JSON.parse(initialComp.componentTemplate)

      }));
    });
    return _this;
  }

  (0, _createClass2["default"])(ToolBar, [{
    key: "render",
    value: function render() {
      var _this$props$comp$comp, _this$props$comp;

      var typeCode = (_this$props$comp$comp = (_this$props$comp = this.props.comp) === null || _this$props$comp === void 0 ? void 0 : _this$props$comp.componentTypeCode) !== null && _this$props$comp$comp !== void 0 ? _this$props$comp$comp : '';
      return /*#__PURE__*/React.createElement("div", {
        className: "c-toolbar__container"
      }, /*#__PURE__*/React.createElement(_popconfirm["default"], {
        title: "\u786E\u8BA4\u53D6\u6D88\u5F53\u524D\u66F4\u6539\uFF1F",
        okText: "\u786E\u8BA4",
        cancelText: "\u53D6\u6D88",
        onConfirm: this.onConfirm
      }, /*#__PURE__*/React.createElement(_button["default"], null, "\u53D6\u6D88")), /*#__PURE__*/React.createElement(_button["default"], {
        style: {
          marginLeft: 8
        },
        type: "primary",
        onClick: this.onOk
      }, "\u4FDD\u5B58", "".concat(_const.COMPONENT_CONFIG_NAME[typeCode]), "\u7EC4\u4EF6"));
    }
  }]);
  return ToolBar;
}(React.PureComponent);

exports["default"] = ToolBar;

/***/ }),

/***/ "XTUZ":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.bootstrap = bootstrap;
exports.mount = mount;
exports.unmount = unmount;
exports.update = update;

var _regenerator = _interopRequireDefault(__webpack_require__("o0o1"));

__webpack_require__("ls82");

var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__("yXPU"));

__webpack_require__("g/Sg");

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var ReactDOM = _interopRequireWildcard(__webpack_require__("qWSy"));

var _reduxUtils = __webpack_require__("i7vS");

var log = _interopRequireWildcard(__webpack_require__("PlQi"));

var _root = _interopRequireDefault(__webpack_require__("vNIf"));

var App = function App() {
  var rootReducer = __webpack_require__("Z5Xq")["default"];

  var _getStore = (0, _reduxUtils.getStore)(rootReducer, "hash"),
      store = _getStore.store,
      history = _getStore.history;

  return /*#__PURE__*/React.createElement(_root["default"], {
    store: store,
    history: history
  });
};

function render(props) {
  ReactDOM.render( /*#__PURE__*/React.createElement(App, null), props.container ? props.container.querySelector("#app") : document.getElementById("app"));
}

if (!window.__POWERED_BY_QIANKUN__) {
  render({});
}

log.appInfo();
/**
 * bootstrap 只会在微应用初始化的时候调用一次，下次微应用重新进入时会直接调用 mount 钩子，不会再重复触发 bootstrap。
 * 通常我们可以在这里做一些全局变量的初始化，比如不会在 unmount 阶段被销毁的应用级别的缓存等。
 */

function bootstrap() {
  return _bootstrap.apply(this, arguments);
}
/**
 * 应用每次进入都会调用 mount 方法，通常我们在这里触发应用的渲染方法
 */


function _bootstrap() {
  _bootstrap = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee() {
    return _regenerator["default"].wrap(function _callee$(_context) {
      while (1) {
        switch (_context.prev = _context.next) {
          case 0:
            console.log("console-ui app bootstraped");

          case 1:
          case "end":
            return _context.stop();
        }
      }
    }, _callee);
  }));
  return _bootstrap.apply(this, arguments);
}

function mount(_x) {
  return _mount.apply(this, arguments);
}
/**
 * 应用每次 切出/卸载 会调用的方法，通常在这里我们会卸载微应用的应用实例
 */


function _mount() {
  _mount = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee2(props) {
    return _regenerator["default"].wrap(function _callee2$(_context2) {
      while (1) {
        switch (_context2.prev = _context2.next) {
          case 0:
            console.log("console-ui app mount");
            render(props);

          case 2:
          case "end":
            return _context2.stop();
        }
      }
    }, _callee2);
  }));
  return _mount.apply(this, arguments);
}

function unmount(_x2) {
  return _unmount.apply(this, arguments);
}
/**
 * 可选生命周期钩子，仅使用 loadMicroApp 方式加载微应用时生效
 */


function _unmount() {
  _unmount = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee3(props) {
    return _regenerator["default"].wrap(function _callee3$(_context3) {
      while (1) {
        switch (_context3.prev = _context3.next) {
          case 0:
            ReactDOM.unmountComponentAtNode(props.container ? props.container.querySelector("#app") : document.getElementById("app"));

          case 1:
          case "end":
            return _context3.stop();
        }
      }
    }, _callee3);
  }));
  return _unmount.apply(this, arguments);
}

function update(_x3) {
  return _update.apply(this, arguments);
}

function _update() {
  _update = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee4(props) {
    return _regenerator["default"].wrap(function _callee4$(_context4) {
      while (1) {
        switch (_context4.prev = _context4.next) {
          case 0:
            console.log("console-ui update props", props);

          case 1:
          case "end":
            return _context4.stop();
        }
      }
    }, _callee4);
  }));
  return _update.apply(this, arguments);
}

/***/ }),

/***/ "XdBI":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("2B1R");

__webpack_require__("+2oP");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("T63A");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _table = _interopRequireDefault(__webpack_require__("DtFj"));

var _pagination = _interopRequireDefault(__webpack_require__("s4l/"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var _popconfirm = _interopRequireDefault(__webpack_require__("h0/l"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _regenerator = _interopRequireDefault(__webpack_require__("o0o1"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

__webpack_require__("ls82");

var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__("yXPU"));

var _slicedToArray2 = _interopRequireDefault(__webpack_require__("J4zp"));

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _react = _interopRequireWildcard(__webpack_require__("q1tI"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

var _consts = __webpack_require__("RzPm");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

var confirm = _modal["default"].confirm;

var AlarmChannel = function AlarmChannel(props) {
  var _useState = (0, _react.useState)({
    currentPage: 1,
    total: 0,
    pageSize: 15
  }),
      _useState2 = (0, _slicedToArray2["default"])(_useState, 2),
      pagination = _useState2[0],
      setPagination = _useState2[1];

  var _useState3 = (0, _react.useState)({
    alertGateType: [],
    reFreshKey: ''
  }),
      _useState4 = (0, _slicedToArray2["default"])(_useState3, 2),
      params = _useState4[0],
      setParams = _useState4[1];

  var useAlarmList = function useAlarmList(query, pagination) {
    var _useState5 = (0, _react.useState)(false),
        _useState6 = (0, _slicedToArray2["default"])(_useState5, 2),
        loading = _useState6[0],
        setLoading = _useState6[1];

    var _useState7 = (0, _react.useState)([]),
        _useState8 = (0, _slicedToArray2["default"])(_useState7, 2),
        alarmList = _useState8[0],
        setAlarmList = _useState8[1];

    var currentPage = pagination.currentPage,
        pageSize = pagination.pageSize;
    var alertGateType = params.alertGateType,
        reFreshKey = params.reFreshKey;
    (0, _react.useEffect)(function () {
      var getAlarmRuleList = /*#__PURE__*/function () {
        var _ref = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee() {
          var res, _res$data;

          return _regenerator["default"].wrap(function _callee$(_context) {
            while (1) {
              switch (_context.prev = _context.next) {
                case 0:
                  setLoading(true);
                  _context.next = 3;
                  return _console["default"].getAlarmRuleList({
                    currentPage: currentPage,
                    pageSize: pageSize,
                    alertGateType: alertGateType
                  });

                case 3:
                  res = _context.sent;

                  if (res && res.code == 1) {
                    setAlarmList(((_res$data = res.data) === null || _res$data === void 0 ? void 0 : _res$data.data) || []);
                    setPagination(function (state) {
                      return _objectSpread(_objectSpread({}, state), {}, {
                        total: res.data.totalCount
                      });
                    });
                  }

                  setLoading(false);

                case 6:
                case "end":
                  return _context.stop();
              }
            }
          }, _callee);
        }));

        return function getAlarmRuleList() {
          return _ref.apply(this, arguments);
        };
      }();

      getAlarmRuleList().then();
    }, [currentPage, alertGateType, pageSize, reFreshKey]);
    return [{
      loading: loading,
      alarmList: alarmList
    }];
  };

  var refreshTable = function refreshTable() {
    setParams(function (state) {
      return _objectSpread(_objectSpread({}, state), {}, {
        reFreshKey: Math.random()
      });
    });
  };

  var deleteRule = /*#__PURE__*/function () {
    var _ref2 = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee2(id) {
      var res;
      return _regenerator["default"].wrap(function _callee2$(_context2) {
        while (1) {
          switch (_context2.prev = _context2.next) {
            case 0:
              _context2.next = 2;
              return _console["default"].deleteAlarmRule({
                id: id
              });

            case 2:
              res = _context2.sent;

              if (res.code === 1) {
                _message2["default"].success('删除成功！');

                refreshTable();
              }

            case 4:
            case "end":
              return _context2.stop();
          }
        }
      }, _callee2);
    }));

    return function deleteRule(_x) {
      return _ref2.apply(this, arguments);
    };
  }();

  var editAlarm = /*#__PURE__*/function () {
    var _ref3 = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee3(id) {
      var res;
      return _regenerator["default"].wrap(function _callee3$(_context3) {
        while (1) {
          switch (_context3.prev = _context3.next) {
            case 0:
              _context3.next = 2;
              return _console["default"].getByAlertId({
                id: id
              });

            case 2:
              res = _context3.sent;

              if (res.code === 1) {
                props.router.push({
                  pathname: '/console/alarmChannel/alarmRule',
                  state: {
                    id: id,
                    ruleData: res.data || {}
                  }
                });
              }

            case 4:
            case "end":
              return _context3.stop();
          }
        }
      }, _callee3);
    }));

    return function editAlarm(_x2) {
      return _ref3.apply(this, arguments);
    };
  }();

  var initColumns = function initColumns() {
    return [{
      title: '通道名称',
      dataIndex: 'alertGateName',
      render: function render(alertGateName, record) {
        var showText = "".concat(_consts.ALARM_TYPE_TEXT[record.alertGateType].slice(0, 2), "\u9ED8\u8BA4\u901A\u9053");
        return /*#__PURE__*/_react["default"].createElement("span", {
          className: "alarm-name-wrap"
        }, /*#__PURE__*/_react["default"].createElement("span", {
          className: "alarm-name"
        }, alertGateName), record.isDefault ? /*#__PURE__*/_react["default"].createElement(_button["default"], {
          className: "alarm-btn",
          disabled: true
        }, showText) : null);
      }
    }, {
      title: '通道类型',
      dataIndex: 'alertGateType',
      filters: Object.entries(_consts.ALARM_TYPE_TEXT).map(function (_ref4) {
        var _ref5 = (0, _slicedToArray2["default"])(_ref4, 2),
            key = _ref5[0],
            value = _ref5[1];

        return {
          text: value,
          value: key
        };
      }),
      render: function render(text) {
        return _consts.ALARM_TYPE_TEXT[text];
      }
    }, {
      title: '通道标识',
      dataIndex: 'alertGateSource'
    }, {
      title: '操作',
      dataIndex: 'opera',
      render: function render(text, record) {
        var alertId = record.alertId;
        var showText = "".concat(_consts.ALARM_TYPE_TEXT[record.alertGateType].slice(0, 2), "\u9ED8\u8BA4\u901A\u9053");
        return /*#__PURE__*/_react["default"].createElement("span", null, /*#__PURE__*/_react["default"].createElement("a", {
          onClick: function onClick() {
            editAlarm(alertId);
          }
        }, "\u7F16\u8F91"), /*#__PURE__*/_react["default"].createElement("span", {
          className: "ant-divider"
        }), /*#__PURE__*/_react["default"].createElement(_popconfirm["default"], {
          title: "\u786E\u8BA4\u5220\u9664\u8BE5\u544A\u8B66\u901A\u9053\uFF1F",
          okText: "\u786E\u5B9A",
          cancelText: "\u53D6\u6D88",
          onConfirm: function onConfirm() {
            deleteRule(alertId);
          }
        }, /*#__PURE__*/_react["default"].createElement("a", null, "\u5220\u9664")), !record.isDefault && record.alertGateType !== _consts.ALARM_TYPE.CUSTOM && /*#__PURE__*/_react["default"].createElement(_react["default"].Fragment, null, /*#__PURE__*/_react["default"].createElement("span", {
          className: "ant-divider"
        }), /*#__PURE__*/_react["default"].createElement("a", {
          onClick: function onClick() {
            setDefaultChannel(record);
          }
        }, "\u8BBE\u4E3A".concat(showText))));
      }
    }];
  };

  var setDefaultChannel = function setDefaultChannel(record) {
    var alertId = record.alertId,
        alertGateType = record.alertGateType,
        alertGateName = record.alertGateName;
    var showText = "".concat(_consts.ALARM_TYPE_TEXT[alertGateType].slice(0, 2), "\u9ED8\u8BA4\u901A\u9053");
    confirm({
      title: "\u786E\u5B9A\u5C06\u201C".concat(alertGateName, "(\u901A\u9053\u540D\u79F0)\u201D \u8BBE\u4E3A").concat(showText, "\u5417"),
      content: '设置为默认告警通道后，各应用的告警信息将走此通道',
      onOk: function onOk() {
        _console["default"].setDefaultAlert({
          alertId: alertId,
          alertGateType: alertGateType
        }).then(function (res) {
          if (res.code === 1) {
            _message2["default"].success('操作成功');

            refreshTable();
          }
        });
      },
      onCancel: function onCancel() {
        console.log('Cancel');
      }
    });
  };

  var handleTableChange = function handleTableChange(paginations, filters, sorter) {
    setParams(function (state) {
      return _objectSpread(_objectSpread({}, state), {}, {
        alertGateType: filters.alertGateType || []
      });
    });
  };

  var onPageChange = function onPageChange(current) {
    setPagination(function (state) {
      return _objectSpread(_objectSpread({}, state), {}, {
        currentPage: current || 1
      });
    });
  };

  var _useAlarmList = useAlarmList(params, pagination),
      _useAlarmList2 = (0, _slicedToArray2["default"])(_useAlarmList, 1),
      _useAlarmList2$ = _useAlarmList2[0],
      loading = _useAlarmList2$.loading,
      alarmList = _useAlarmList2$.alarmList;

  return /*#__PURE__*/_react["default"].createElement("div", {
    className: "alarm__wrapper"
  }, /*#__PURE__*/_react["default"].createElement(_form["default"], {
    layout: "inline"
  }, /*#__PURE__*/_react["default"].createElement(_form["default"].Item, null, /*#__PURE__*/_react["default"].createElement(_button["default"], {
    type: "primary",
    onClick: function onClick() {
      props.router.push({
        pathname: '/console/alarmChannel/alarmRule',
        query: {
          isCreate: true
        }
      });
    }
  }, "\u65B0\u589E\u544A\u8B66\u901A\u9053"))), /*#__PURE__*/_react["default"].createElement(_table["default"], {
    className: "dt-table-fixed-contain-footer",
    scroll: {
      y: true
    },
    style: {
      height: 'calc(100vh - 154px)'
    },
    loading: loading,
    columns: initColumns(),
    dataSource: alarmList,
    pagination: false,
    onChange: handleTableChange,
    footer: function footer() {
      return /*#__PURE__*/_react["default"].createElement(_pagination["default"], {
        current: pagination.currentPage,
        pageSize: pagination.pageSize,
        size: 'small',
        total: pagination.total,
        onChange: onPageChange,
        showTotal: function showTotal(total) {
          return /*#__PURE__*/_react["default"].createElement("span", null, "\u5171", /*#__PURE__*/_react["default"].createElement("span", {
            style: {
              color: '#3F87FF'
            }
          }, total), "\u6761\u6570\u636E\uFF0C\u6BCF\u9875\u663E\u793A", pagination.pageSize, "\u6761");
        }
      });
    }
  }));
};

var _default = AlarmChannel;
exports["default"] = _default;

/***/ }),

/***/ "Z5Xq":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _redux = __webpack_require__("fvjX");

var _reactRouterRedux = __webpack_require__("L3Ur");

var _consoleUser = _interopRequireDefault(__webpack_require__("2Cp/"));

var _user = __webpack_require__("X7z3");

var _apps = __webpack_require__("Xkvk");

var _message = __webpack_require__("sdwN");

var _cluster = __webpack_require__("IdH2");

// 全局State
var rootReducer = (0, _redux.combineReducers)({
  routing: _reactRouterRedux.routerReducer,
  user: _user.user,
  apps: _apps.apps,
  app: _apps.app,
  msgList: _message.msgList,
  licenseApps: _apps.licenseApps,
  consoleUser: _consoleUser["default"],
  testStatus: _cluster.testStatus,
  showRequireStatus: _cluster.showRequireStatus
});
var _default = rootReducer;
exports["default"] = _default;

/***/ }),

/***/ "ZeNE":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("fbCW");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("R5XZ");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _input = _interopRequireDefault(__webpack_require__("iJl9"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var _extends2 = _interopRequireDefault(__webpack_require__("pVnL"));

var _alert = _interopRequireDefault(__webpack_require__("ATwu"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _select = _interopRequireDefault(__webpack_require__("FAat"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _lodash = __webpack_require__("LvDl");

var _consts = __webpack_require__("RzPm");

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var Option = _select["default"].Option;

var BindAccountModal = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(BindAccountModal, _React$Component);

  var _super = _createSuper(BindAccountModal);

  function BindAccountModal(props) {
    var _this;

    (0, _classCallCheck2["default"])(this, BindAccountModal);
    _this = _super.call(this, props);
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onSubmit", function (callback) {
      var _this$props = _this.props,
          form = _this$props.form,
          data = _this$props.data,
          userList = _this$props.userList;
      var isEdit = data !== null && data !== undefined;
      var validFields = isEdit ? ['username', 'id'] : ['bindUserId'];
      form.validateFields(validFields.concat(['name', 'password']), function (err, user) {
        if (!err) {
          var selectedUser = userList.find(function (u) {
            return u.userId == user.bindUserId;
          });

          if (selectedUser) {
            user.username = selectedUser.userName;
            user.email = selectedUser.userName;
          } // 此处主要是由于后端字段不一致的原因所致


          if (user.id) {
            user.bindUserId = user.id;
          }

          if (callback) {
            callback(user);
            setTimeout(function () {
              return form.resetFields();
            }, 0);
          }
        }
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onOk", function () {
      var onOk = _this.props.onOk;

      _this.onSubmit(onOk);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onUnbind", function () {
      var onUnbind = _this.props.onUnbind;

      _this.onSubmit(onUnbind);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onCancel", function (e) {
      var _this$props2 = _this.props,
          onCancel = _this$props2.onCancel,
          form = _this$props2.form;

      if (onCancel) {
        setTimeout(function () {
          return form.resetFields();
        }, 0);
        onCancel(e);
      }
    });
    _this.state = {};
    return _this;
  }

  (0, _createClass2["default"])(BindAccountModal, [{
    key: "render",
    value: function render() {
      var getFieldDecorator = this.props.form.getFieldDecorator;
      var _this$props3 = this.props,
          visible = _this$props3.visible,
          onCancel = _this$props3.onCancel,
          title = _this$props3.title,
          data = _this$props3.data,
          engineText = _this$props3.engineText,
          userList = _this$props3.userList;
      var isEdit = data !== null && data !== undefined;
      var footer = /*#__PURE__*/React.createElement("div", {
        style: {
          height: '30px'
        }
      }, data ? /*#__PURE__*/React.createElement("span", {
        className: "left"
      }, /*#__PURE__*/React.createElement(_button["default"], {
        onClick: this.onUnbind
      }, "\u89E3\u9664\u7ED1\u5B9A")) : null, /*#__PURE__*/React.createElement("span", {
        className: "right"
      }, /*#__PURE__*/React.createElement(_button["default"], {
        onClick: this.onCancel,
        style: {
          marginRight: 10
        }
      }, "\u53D6\u6D88"), /*#__PURE__*/React.createElement(_button["default"], {
        type: "primary",
        onClick: this.onOk
      }, "\u786E\u5B9A")));
      return /*#__PURE__*/React.createElement(_modal["default"], {
        closable: true,
        title: title,
        visible: visible,
        footer: footer,
        onCancel: onCancel,
        className: 'no-padding-modal'
      }, /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_alert["default"], {
        message: "",
        style: {
          margin: '10px'
        },
        description: "\u6BCF\u4E2A\u4EA7\u54C1\u8D26\u53F7\u7ED1\u5B9A\u4E00\u4E2A".concat(engineText, "\u7528\u6237\uFF0C\u4EFB\u52A1\u63D0\u4EA4\u8FD0\u884C\u3001\u6D4B\u8BD5\u65F6\uFF0C\u4F7F\u7528\u7ED1\u5B9A\u7684").concat(engineText, "\u7528\u6237\u6267\u884C"),
        type: "info",
        showIcon: true
      }), /*#__PURE__*/React.createElement(_form["default"], null, !isEdit ? /*#__PURE__*/React.createElement(_form["default"].Item, (0, _extends2["default"])({
        key: "bindUserId",
        label: "\u4EA7\u54C1\u8D26\u53F7"
      }, _consts.formItemLayout), getFieldDecorator('bindUserId', {
        rules: [{
          required: true,
          message: '产品账号不可为空！'
        }],
        initialValue: undefined
      })( /*#__PURE__*/React.createElement(_select["default"], {
        allowClear: true,
        showSearch: true,
        placeholder: "\u8BF7\u9009\u62E9\u4EA7\u54C1\u8D26\u53F7",
        optionFilterProp: "title"
      }, userList && userList.map(function (user) {
        var uid = "".concat(user.userId);
        var uname = user.userName;
        return /*#__PURE__*/React.createElement(Option, {
          key: uid,
          title: uname,
          value: uid
        }, uname);
      })))) : /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_form["default"].Item, (0, _extends2["default"])({
        key: "username",
        label: "\u4EA7\u54C1\u8D26\u53F7"
      }, _consts.formItemLayout), getFieldDecorator('username', {
        rules: [{
          required: true,
          message: '产品账号不可为空！'
        }],
        initialValue: (0, _lodash.get)(data, 'username', '')
      })( /*#__PURE__*/React.createElement(_input["default"], {
        disabled: isEdit
      }))), /*#__PURE__*/React.createElement(_form["default"].Item, (0, _extends2["default"])({
        key: "id",
        label: "\u4EA7\u54C1\u8D26\u53F7",
        style: {
          display: 'none'
        }
      }, _consts.formItemLayout), getFieldDecorator('id', {
        initialValue: (0, _lodash.get)(data, 'id', undefined)
      })( /*#__PURE__*/React.createElement(_input["default"], null)))), /*#__PURE__*/React.createElement(_form["default"].Item, (0, _extends2["default"])({
        label: "\u6570\u636E\u5E93\u8D26\u53F7"
      }, _consts.formItemLayout), getFieldDecorator('name', {
        rules: [{
          required: true,
          message: '数据库账号不可为空！'
        }],
        initialValue: (0, _lodash.get)(data, 'name', '')
      })( /*#__PURE__*/React.createElement(_input["default"], {
        placeholder: "\u8BF7\u8F93\u5165\u6570\u636E\u5E93\u8D26\u53F7"
      }))), /*#__PURE__*/React.createElement(_form["default"].Item, (0, _extends2["default"])({
        label: "\u6570\u636E\u5E93\u5BC6\u7801"
      }, _consts.formItemLayout), getFieldDecorator('password', {
        rules: [{
          required: false,
          message: '数据库密码不可为空！'
        }],
        initialValue: ""
      })( /*#__PURE__*/React.createElement(_input["default"], {
        type: "password",
        placeholder: "\u8BF7\u8F93\u5165\u6570\u636E\u5E93\u5BC6\u7801"
      }))))));
    }
  }]);
  return BindAccountModal;
}(React.Component);

var _default = _form["default"].create()(BindAccountModal);

exports["default"] = _default;

/***/ }),

/***/ "g/Sg":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


if (window.__POWERED_BY_QIANKUN__) {
  __webpack_require__.p = window.__INJECTED_PUBLIC_PATH_BY_QIANKUN__;
}

/***/ }),

/***/ "hmv9":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("DQNa");

__webpack_require__("wLYn");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _regenerator = _interopRequireDefault(__webpack_require__("o0o1"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

__webpack_require__("ls82");

var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__("yXPU"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _codeEditor = _interopRequireDefault(__webpack_require__("ZEKU"));

var _lodash = __webpack_require__("LvDl");

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var editorStyle = {
  height: '100%'
};
var editorOptions = {
  mode: 'simpleConfig',
  lineNumbers: false,
  readOnly: false,
  autofocus: false,
  indentWithTabs: true,
  smartIndent: true
};

var KerberosModal = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(KerberosModal, _React$Component);

  var _super = _createSuper(KerberosModal);

  function KerberosModal() {
    var _this;

    (0, _classCallCheck2["default"])(this, KerberosModal);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      krb5Content: ''
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "_editor", void 0);
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "editorParamsChange", function (preValue, nextValue) {
      _this.setState({
        krb5Content: nextValue
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "debounceEditorChange", (0, _lodash.debounce)(_this.editorParamsChange, 300, {
      'maxWait': 2000
    }));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onOK", /*#__PURE__*/(0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee() {
      var onCancel, krb5Content, res;
      return _regenerator["default"].wrap(function _callee$(_context) {
        while (1) {
          switch (_context.prev = _context.next) {
            case 0:
              onCancel = _this.props.onCancel;
              krb5Content = _this.state.krb5Content;
              _context.next = 4;
              return _console["default"].updateKrb5Conf({
                krb5Content: krb5Content
              });

            case 4:
              res = _context.sent;

              if (res.code == 1) {
                onCancel(krb5Content);

                _message2["default"].success('更新成功');
              }

            case 6:
            case "end":
              return _context.stop();
          }
        }
      }, _callee);
    })));
    return _this;
  }

  (0, _createClass2["default"])(KerberosModal, [{
    key: "render",
    value: function render() {
      var _this2 = this;

      var _this$props = this.props,
          visible = _this$props.visible,
          _onCancel = _this$props.onCancel,
          krbconfig = _this$props.krbconfig;
      return /*#__PURE__*/React.createElement(_modal["default"], {
        title: "\u5408\u5E76\u540E\u7684krb5.conf",
        visible: visible,
        onCancel: function onCancel() {
          return _onCancel();
        },
        onOk: this.onOK,
        okText: "\u4FDD\u5B58"
      }, /*#__PURE__*/React.createElement("div", {
        style: editorStyle
      }, /*#__PURE__*/React.createElement(_codeEditor["default"], {
        sync: true,
        value: krbconfig || '',
        className: "c-kerberosModal__edior",
        ref: function ref(e) {
          return _this2._editor = e;
        },
        style: {
          height: '100%'
        },
        options: editorOptions,
        onChange: this.debounceEditorChange.bind(this)
      })));
    }
  }]);
  return KerberosModal;
}(React.Component);

exports["default"] = KerberosModal;

/***/ }),

/***/ "htex":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("DQNa");

__webpack_require__("sMBO");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _tooltip = _interopRequireDefault(__webpack_require__("d1El"));

var _icon = _interopRequireDefault(__webpack_require__("Pbn2"));

var _upload = _interopRequireDefault(__webpack_require__("B8+X"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var FormItem = _form["default"].Item;

var UploadFile = /*#__PURE__*/function (_React$PureComponent) {
  (0, _inherits2["default"])(UploadFile, _React$PureComponent);

  var _super = _createSuper(UploadFile);

  function UploadFile() {
    (0, _classCallCheck2["default"])(this, UploadFile);
    return _super.apply(this, arguments);
  }

  (0, _createClass2["default"])(UploadFile, [{
    key: "render",
    value: function render() {
      var _form$getFieldValue$n,
          _form$getFieldValue,
          _this = this;

      var _this$props = this.props,
          label = _this$props.label,
          form = _this$props.form,
          icons = _this$props.icons,
          deleteIcon = _this$props.deleteIcon,
          fileInfo = _this$props.fileInfo,
          uploadFile = _this$props.uploadFile,
          view = _this$props.view,
          rules = _this$props.rules,
          notDesc = _this$props.notDesc,
          deleteFile = _this$props.deleteFile;
      var fileName = (_form$getFieldValue$n = (_form$getFieldValue = form.getFieldValue("".concat(fileInfo.typeCode, ".").concat(fileInfo.name))) === null || _form$getFieldValue === void 0 ? void 0 : _form$getFieldValue.name) !== null && _form$getFieldValue$n !== void 0 ? _form$getFieldValue$n : fileInfo === null || fileInfo === void 0 ? void 0 : fileInfo.value;
      var uploadFileProps = {
        name: fileInfo.uploadProps.name,
        accept: fileInfo.uploadProps.accept,
        beforeUpload: function beforeUpload(file) {
          uploadFile(file, fileInfo.uploadProps.type, function () {
            _this.props.form.setFieldsValue((0, _defineProperty2["default"])({}, "".concat(fileInfo.typeCode, ".").concat(fileInfo.name), file));
          });
          return false;
        },
        fileList: []
      };
      return /*#__PURE__*/React.createElement(FormItem, {
        label: label !== null && label !== void 0 ? label : '参数上传',
        colon: false
      }, form.getFieldDecorator("".concat(fileInfo.typeCode, ".").concat(fileInfo.name), {
        initialValue: (fileInfo === null || fileInfo === void 0 ? void 0 : fileInfo.value) || '',
        rules: rules !== null && rules !== void 0 ? rules : []
      })( /*#__PURE__*/React.createElement("div", null)), !view && /*#__PURE__*/React.createElement("div", {
        className: "c-fileConfig__config"
      }, /*#__PURE__*/React.createElement(_upload["default"], uploadFileProps, /*#__PURE__*/React.createElement(_button["default"], {
        style: {
          width: 172
        },
        icon: "upload",
        loading: fileInfo.loading
      }, "\u70B9\u51FB\u4E0A\u4F20")), /*#__PURE__*/React.createElement("span", {
        className: "config-desc"
      }, fileInfo.desc)), form.getFieldValue("".concat(fileInfo.typeCode, ".").concat(fileInfo.name)) && !notDesc && /*#__PURE__*/React.createElement("span", {
        className: "config-file"
      }, /*#__PURE__*/React.createElement(_icon["default"], {
        type: "paper-clip"
      }), /*#__PURE__*/React.createElement(_tooltip["default"], {
        title: fileName,
        placement: "topLeft"
      }, fileName), icons !== null && icons !== void 0 ? icons : icons, !deleteIcon ? !view && /*#__PURE__*/React.createElement(_icon["default"], {
        type: "delete",
        onClick: function onClick() {
          form.setFieldsValue((0, _defineProperty2["default"])({}, "".concat(fileInfo.typeCode, ".").concat(fileInfo.name), ''));
          deleteFile && deleteFile();
        }
      }) : null));
    }
  }]);
  return UploadFile;
}(React.PureComponent);

exports["default"] = UploadFile;

/***/ }),

/***/ "itRn":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _http = _interopRequireDefault(__webpack_require__("AwqB"));

var _reqUrls = _interopRequireDefault(__webpack_require__("C4JW"));

var _default = {
  getUnbindAccounts: function getUnbindAccounts(params) {
    return _http["default"].post(_reqUrls["default"].ACCOUNT_UNBIND_LIST, params);
  },
  bindAccount: function bindAccount(params) {
    return _http["default"].postWithDefaultHeader(_reqUrls["default"].ACCOUNT_BIND, params);
  },
  ldapBindAccount: function ldapBindAccount(params) {
    return _http["default"].postWithDefaultHeader(_reqUrls["default"].LDAP_ACCOUNT_BIND, params);
  },
  updateBindAccount: function updateBindAccount(params) {
    return _http["default"].postWithDefaultHeader(_reqUrls["default"].UPDATE_ACCOUNT_BIND, params);
  },
  getBindAccounts: function getBindAccounts(params) {
    return _http["default"].post(_reqUrls["default"].ACCOUNT_BIND_LIST, params);
  },
  unbindAccount: function unbindAccount(params) {
    return _http["default"].postWithDefaultHeader(_reqUrls["default"].ACCOUNT_UNBIND, params);
  }
};
exports["default"] = _default;

/***/ }),

/***/ "j1Tt":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.CONFIG_FILE_DESC = exports.DEFAULT_COMP_VERSION = exports.CONFIG_ITEM_TYPE = exports.FILE_TYPE = exports.VERSION_TYPE = exports.CONFIG_BUTTON_TYPE = exports.TABS_TITLE = exports.COMPONENT_CONFIG_NAME = exports.COMPONENT_TYPE_VALUE = exports.TABS_POP_VISIBLE = exports.TABS_TITLE_KEY = void 0;

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _TABS_POP_VISIBLE, _TABS_TITLE, _CONFIG_BUTTON_TYPE, _DEFAULT_COMP_VERSION, _CONFIG_FILE_DESC;

// Tabs枚举值
var TABS_TITLE_KEY = {
  COMMON: 0,
  SOURCE: 1,
  STORE: 2,
  COMPUTE: 3
};
exports.TABS_TITLE_KEY = TABS_TITLE_KEY;
var TABS_POP_VISIBLE = (_TABS_POP_VISIBLE = {}, (0, _defineProperty2["default"])(_TABS_POP_VISIBLE, TABS_TITLE_KEY.COMMON, false), (0, _defineProperty2["default"])(_TABS_POP_VISIBLE, TABS_TITLE_KEY.SOURCE, false), (0, _defineProperty2["default"])(_TABS_POP_VISIBLE, TABS_TITLE_KEY.STORE, false), (0, _defineProperty2["default"])(_TABS_POP_VISIBLE, TABS_TITLE_KEY.COMPUTE, false), _TABS_POP_VISIBLE);
exports.TABS_POP_VISIBLE = TABS_POP_VISIBLE;
var COMPONENT_TYPE_VALUE = {
  FLINK: 0,
  SPARK: 1,
  LEARNING: 2,
  DTYARNSHELL: 3,
  HDFS: 4,
  YARN: 5,
  SPARK_THRIFT_SERVER: 6,
  CARBONDATA: 7,
  LIBRA_SQL: 8,
  HIVE_SERVER: 9,
  SFTP: 10,
  IMPALA_SQL: 11,
  TIDB_SQL: 12,
  ORACLE_SQL: 13,
  GREEN_PLUM_SQL: 14,
  KUBERNETES: 15,
  PRESTO_SQL: 16,
  NFS: 17
};
exports.COMPONENT_TYPE_VALUE = COMPONENT_TYPE_VALUE;
var COMPONENT_CONFIG_NAME = {
  0: 'Flink',
  1: 'Spark',
  2: 'Learning',
  3: 'DtScript',
  4: 'HDFS',
  5: 'YARN',
  6: 'SparkThrift',
  7: 'CarbonData ThriftServer',
  8: 'LibrA SQL',
  9: 'HiveServer',
  10: 'SFTP',
  11: 'Impala SQL',
  12: 'TiDB SQL',
  13: 'Oracle SQL',
  14: 'Greenplum SQL',
  15: 'Kubernetes',
  16: 'Presto SQL',
  17: 'NFS'
};
exports.COMPONENT_CONFIG_NAME = COMPONENT_CONFIG_NAME;
var TABS_TITLE = (_TABS_TITLE = {}, (0, _defineProperty2["default"])(_TABS_TITLE, TABS_TITLE_KEY.COMMON, {
  iconName: 'iconcunchuzujian',
  name: '公共组件'
}), (0, _defineProperty2["default"])(_TABS_TITLE, TABS_TITLE_KEY.SOURCE, {
  iconName: 'icongonggongzujian',
  name: '资源调度组件'
}), (0, _defineProperty2["default"])(_TABS_TITLE, TABS_TITLE_KEY.STORE, {
  iconName: 'iconjisuanzujian',
  name: '存储组件'
}), (0, _defineProperty2["default"])(_TABS_TITLE, TABS_TITLE_KEY.COMPUTE, {
  iconName: 'iconziyuantiaodu',
  name: '计算组件'
}), _TABS_TITLE);
exports.TABS_TITLE = TABS_TITLE;
var CONFIG_BUTTON_TYPE = (_CONFIG_BUTTON_TYPE = {}, (0, _defineProperty2["default"])(_CONFIG_BUTTON_TYPE, TABS_TITLE_KEY.COMMON, [{
  code: COMPONENT_TYPE_VALUE.SFTP,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.SFTP]
}]), (0, _defineProperty2["default"])(_CONFIG_BUTTON_TYPE, TABS_TITLE_KEY.SOURCE, [{
  code: COMPONENT_TYPE_VALUE.YARN,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.YARN]
}, {
  code: COMPONENT_TYPE_VALUE.KUBERNETES,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.KUBERNETES]
}]), (0, _defineProperty2["default"])(_CONFIG_BUTTON_TYPE, TABS_TITLE_KEY.STORE, [{
  code: COMPONENT_TYPE_VALUE.HDFS,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.HDFS]
}, {
  code: COMPONENT_TYPE_VALUE.NFS,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.NFS]
}]), (0, _defineProperty2["default"])(_CONFIG_BUTTON_TYPE, TABS_TITLE_KEY.COMPUTE, [{
  code: COMPONENT_TYPE_VALUE.SPARK,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.SPARK]
}, {
  code: COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER]
}, {
  code: COMPONENT_TYPE_VALUE.FLINK,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.FLINK]
}, {
  code: COMPONENT_TYPE_VALUE.HIVE_SERVER,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.HIVE_SERVER]
}, {
  code: COMPONENT_TYPE_VALUE.IMPALA_SQL,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.IMPALA_SQL]
}, {
  code: COMPONENT_TYPE_VALUE.DTYARNSHELL,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.DTYARNSHELL]
}, {
  code: COMPONENT_TYPE_VALUE.LEARNING,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.LEARNING]
}, {
  code: COMPONENT_TYPE_VALUE.PRESTO_SQL,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.PRESTO_SQL]
}, {
  code: COMPONENT_TYPE_VALUE.TIDB_SQL,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.TIDB_SQL]
}, {
  code: COMPONENT_TYPE_VALUE.LIBRA_SQL,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.LIBRA_SQL]
}, {
  code: COMPONENT_TYPE_VALUE.ORACLE_SQL,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.ORACLE_SQL]
}, {
  code: COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL,
  componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL]
}]), _CONFIG_BUTTON_TYPE);
exports.CONFIG_BUTTON_TYPE = CONFIG_BUTTON_TYPE;
var VERSION_TYPE = {
  0: 'Flink',
  9: 'HiveServer',
  1: 'Spark',
  6: 'SparkThrift'
};
exports.VERSION_TYPE = VERSION_TYPE;
var FILE_TYPE = {
  KERNEROS: 0,
  CONFIGS: 1,
  PARAMES: 2
};
exports.FILE_TYPE = FILE_TYPE;
var CONFIG_ITEM_TYPE = {
  RADIO: 'RADIO',
  INPUT: 'INPUT',
  SELECT: 'SELECT',
  CHECKBOX: 'CHECKBOX',
  PASSWORD: 'PASSWORD',
  GROUP: 'GROUP'
};
exports.CONFIG_ITEM_TYPE = CONFIG_ITEM_TYPE;
var DEFAULT_COMP_VERSION = (_DEFAULT_COMP_VERSION = {}, (0, _defineProperty2["default"])(_DEFAULT_COMP_VERSION, COMPONENT_TYPE_VALUE.FLINK, '180'), (0, _defineProperty2["default"])(_DEFAULT_COMP_VERSION, COMPONENT_TYPE_VALUE.SPARK, '210'), (0, _defineProperty2["default"])(_DEFAULT_COMP_VERSION, COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER, '2.x'), (0, _defineProperty2["default"])(_DEFAULT_COMP_VERSION, COMPONENT_TYPE_VALUE.HIVE_SERVER, '2.x'), _DEFAULT_COMP_VERSION);
exports.DEFAULT_COMP_VERSION = DEFAULT_COMP_VERSION;
var CONFIG_FILE_DESC = (_CONFIG_FILE_DESC = {}, (0, _defineProperty2["default"])(_CONFIG_FILE_DESC, COMPONENT_TYPE_VALUE.YARN, 'zip格式，至少包括yarn-site.xml和core-site.xml'), (0, _defineProperty2["default"])(_CONFIG_FILE_DESC, COMPONENT_TYPE_VALUE.HDFS, 'zip格式，至少包括core-site.xml、hdfs-site.xml、hive-site.xml'), (0, _defineProperty2["default"])(_CONFIG_FILE_DESC, COMPONENT_TYPE_VALUE.KUBERNETES, 'zip格式，至少包括kubernetes.config'), _CONFIG_FILE_DESC);
exports.CONFIG_FILE_DESC = CONFIG_FILE_DESC;

/***/ }),

/***/ "jRo7":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("DQNa");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _table = _interopRequireDefault(__webpack_require__("DtFj"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

var _lodash = __webpack_require__("LvDl");

var _chart = _interopRequireDefault(__webpack_require__("JtgN"));

var _constant = __webpack_require__("Ps9q");

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

var ResourceCard = function ResourceCard(props) {
  function setOptions(value) {
    var option = (0, _lodash.cloneDeep)(_constant.pieOption);
    option.series[1].data = [_objectSpread(_objectSpread({}, option.series[1].data[0]), {}, {
      value: value,
      itemStyle: {
        normal: {
          color: value >= _constant.ALARM_DEFAULT ? value >= _constant.ALARM_HIGHT ? '#FF5F5C' : '#FFB310' : '#16DE9A'
        }
      }
    }), _objectSpread(_objectSpread({}, option.series[1].data[1]), {}, {
      value: 150 - value
    })];
    return option;
  }

  var title = props.title,
      useNum = props.useNum,
      total = props.total,
      _props$value = props.value,
      value = _props$value === void 0 ? 0 : _props$value;
  var option = setOptions(value);
  return /*#__PURE__*/React.createElement("div", {
    className: "c-resourceCard__container"
  }, /*#__PURE__*/React.createElement(_chart["default"], {
    option: option,
    width: 110,
    height: 110
  }), /*#__PURE__*/React.createElement("div", {
    className: "c-resourceCard__container__title"
  }, /*#__PURE__*/React.createElement("p", null, title), /*#__PURE__*/React.createElement("p", null, /*#__PURE__*/React.createElement("span", {
    style: {
      fontSize: 18
    }
  }, useNum || '-'), " / ", total || '-')));
};

var RenderTable = function RenderTable(props) {
  var columns = props.columns,
      data = props.data,
      title = props.title,
      _props$desc = props.desc,
      desc = _props$desc === void 0 ? '' : _props$desc;
  return /*#__PURE__*/React.createElement("div", {
    className: "c-resourceView__table__container"
  }, /*#__PURE__*/React.createElement("p", null, title, desc && "\uFF08".concat(desc, "\uFF09")), /*#__PURE__*/React.createElement(_table["default"], {
    className: "dt-table-border dt-table-last-row-noborder",
    style: {
      marginTop: '10px'
    },
    columns: columns,
    dataSource: data,
    pagination: false
  }));
};

var Resource = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(Resource, _React$Component);

  var _super = _createSuper(Resource);

  function Resource() {
    var _this;

    (0, _classCallCheck2["default"])(this, Resource);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      nodesListSource: [],
      queuesListSource: [],
      target: '',
      resourceMetrics: {},
      type: _constant.SCHEDULE_TYPE.Capacity
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "initQueuesColumns", function () {
      var type = _this.state.type;
      var colums = [{
        title: '资源队列',
        dataIndex: 'queueName',
        render: function render(_, record) {
          return record.queueName;
        }
      }, {
        title: '已使用容量',
        dataIndex: 'usedCapacity',
        render: function render(_, record) {
          return "".concat(record.usedCapacity, "%");
        }
      }, {
        title: '分配容量',
        dataIndex: 'capacity',
        render: function render(_, record) {
          return "".concat(record.capacity, "%");
        }
      }, {
        title: '最大容量',
        dataIndex: 'maxCapacity',
        render: function render(_, record) {
          return "".concat(record.maxCapacity, "%");
        }
      }, {
        title: '查看',
        dataIndex: 'action',
        render: function render(_, record) {
          return /*#__PURE__*/React.createElement("a", {
            onClick: function onClick() {
              _this.handleShowDetailt(record);
            }
          }, "\u8D44\u6E90\u8BE6\u60C5");
        },
        width: 150
      }];

      switch (type) {
        case _constant.SCHEDULE_TYPE.Fair:
          colums = [{
            title: '资源队列',
            dataIndex: 'queueName',
            render: function render(_, record) {
              return record.queueName;
            }
          }, {
            title: '已使资源数',
            dataIndex: 'usedResources',
            render: function render(text) {
              return /*#__PURE__*/React.createElement("span", null, "memory:", /*#__PURE__*/React.createElement("span", {
                style: {
                  margin: 5
                }
              }, text.memory, ",\xA0"), "vCores:", /*#__PURE__*/React.createElement("span", {
                style: {
                  margin: 5
                }
              }, text.vCores));
            }
          }, {
            title: '最大资源数',
            dataIndex: 'maxResources',
            render: function render(text) {
              return /*#__PURE__*/React.createElement("span", null, "memory:", /*#__PURE__*/React.createElement("span", {
                style: {
                  margin: 5
                }
              }, text.memory, ",\xA0"), "vCores:", /*#__PURE__*/React.createElement("span", {
                style: {
                  margin: 5
                }
              }, text.vCores));
            }
          }, {
            title: '最小资源数',
            dataIndex: 'minResources',
            render: function render(text) {
              return /*#__PURE__*/React.createElement("span", null, "memory:", /*#__PURE__*/React.createElement("span", {
                style: {
                  margin: 5
                }
              }, text.memory, ",\xA0"), "vCores:", /*#__PURE__*/React.createElement("span", {
                style: {
                  margin: 5
                }
              }, text.vCores));
            }
          }];
          break;

        case _constant.SCHEDULE_TYPE.FIFO:
          colums = [{
            title: '容量',
            dataIndex: 'capacity',
            render: function render(_, record) {
              return record.capacity;
            }
          }, {
            title: '已使用容量',
            dataIndex: 'usedCapacity',
            render: function render(_, record) {
              return record.usedCapacity;
            }
          }, {
            title: '节点数量',
            dataIndex: 'numNodes',
            render: function render(_, record) {
              return record.numNodes;
            }
          }];
          break;

        default:
          break;
      }

      return colums;
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "initDetailtColumns", function () {
      return [{
        title: 'Username',
        dataIndex: 'username'
      }, {
        title: 'Max Resource',
        dataIndex: 'maxResource',
        render: function render(text) {
          return /*#__PURE__*/React.createElement("span", null, "memory:", /*#__PURE__*/React.createElement("span", {
            style: {
              margin: 5
            }
          }, text.memory, ",\xA0"), "vCores:", /*#__PURE__*/React.createElement("span", {
            style: {
              margin: 5
            }
          }, text.vCores));
        }
      }, {
        title: 'Used Resource',
        dataIndex: 'resourcesUsed',
        render: function render(text) {
          return /*#__PURE__*/React.createElement("span", null, "memory:", /*#__PURE__*/React.createElement("span", {
            style: {
              margin: 5
            }
          }, text.memory, ",\xA0"), "vCores:", /*#__PURE__*/React.createElement("span", {
            style: {
              margin: 5
            }
          }, text.vCores));
        }
      }, {
        title: 'Max AM Resource',
        dataIndex: 'maxAMResource',
        render: function render(text) {
          return /*#__PURE__*/React.createElement("span", null, "memory:", /*#__PURE__*/React.createElement("span", {
            style: {
              margin: 5
            }
          }, text.memory, ",\xA0"), "vCores:", /*#__PURE__*/React.createElement("span", {
            style: {
              margin: 5
            }
          }, text.vCores));
        }
      }, {
        title: 'Used AM Resource',
        dataIndex: 'AMResourceUsed',
        render: function render(text) {
          return /*#__PURE__*/React.createElement("span", null, "memory:", /*#__PURE__*/React.createElement("span", {
            style: {
              margin: 5
            }
          }, text.memory, ",\xA0"), "vCores:", /*#__PURE__*/React.createElement("span", {
            style: {
              margin: 5
            }
          }, text.vCores));
        }
      }];
    });
    return _this;
  }

  (0, _createClass2["default"])(Resource, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      this.getClusterResources();
    } // 获取资源信息

  }, {
    key: "getClusterResources",
    value: function getClusterResources() {
      var _this2 = this;

      var clusterName = this.props.clusterName;

      _console["default"].getClusterResources({
        clusterName: clusterName
      }).then(function (res) {
        var _res$data, _res$data$scheduleInf, _res$data$scheduleInf2, _childQueues$queue, _res$data2;

        if (res.code == 1) {
          var type = _constant.SCHEDULE_TYPE.Capacity;
          var queuesListSource = res.data.queues || [];

          switch (res.data.scheduleInfo.type) {
            case _constant.SCHEDULE_TYPE.Fair:
              var childQueues = (_res$data = res.data) === null || _res$data === void 0 ? void 0 : (_res$data$scheduleInf = _res$data.scheduleInfo) === null || _res$data$scheduleInf === void 0 ? void 0 : (_res$data$scheduleInf2 = _res$data$scheduleInf.rootQueue) === null || _res$data$scheduleInf2 === void 0 ? void 0 : _res$data$scheduleInf2.childQueues;
              queuesListSource = (_childQueues$queue = childQueues === null || childQueues === void 0 ? void 0 : childQueues.queue) !== null && _childQueues$queue !== void 0 ? _childQueues$queue : childQueues;
              type = _constant.SCHEDULE_TYPE.Fair;
              break;

            case _constant.SCHEDULE_TYPE.FIFO:
              queuesListSource = [_objectSpread({}, (_res$data2 = res.data) === null || _res$data2 === void 0 ? void 0 : _res$data2.scheduleInfo)];
              type = _constant.SCHEDULE_TYPE.FIFO;
              break;

            default:
              break;
          }

          _this2.setState({
            queuesListSource: queuesListSource,
            type: type,
            nodesListSource: res.data.nodes || [],
            resourceMetrics: res.data.resourceMetrics || {}
          });
        }
      });
    }
  }, {
    key: "handleShowDetailt",
    value: function handleShowDetailt(record) {
      var target = this.state.target;
      var newRecord = (0, _lodash.cloneDeep)(record);

      if ((target === null || target === void 0 ? void 0 : target.queueName) === record.queueName && (target === null || target === void 0 ? void 0 : target.queueName)) {
        newRecord = '';
      }

      this.setState({
        target: newRecord
      });
    }
  }, {
    key: "initNodesColumns",
    value: function initNodesColumns() {
      return [{
        title: 'nodeName',
        dataIndex: 'nodeName',
        render: function render(_, record) {
          return record.nodeName || '-';
        }
      }, {
        title: 'virtualCores',
        dataIndex: 'virtualCores',
        render: function render(_, record) {
          return record.virtualCores;
        }
      }, {
        title: 'usedVirtualCores',
        dataIndex: 'usedVirtualCores',
        render: function render(_, record) {
          return record.usedVirtualCores;
        }
      }, {
        title: 'memory (M)',
        dataIndex: 'memory',
        render: function render(_, record) {
          return record.memory;
        }
      }, {
        title: 'usedMemory (M)',
        dataIndex: 'usedMemory',
        render: function render(_, record) {
          return record.usedMemory;
        }
      }];
    }
  }, {
    key: "render",
    value: function render() {
      var columnsNodes = this.initNodesColumns();
      var columnsQueues = this.initQueuesColumns();
      var columnsDetail = this.initDetailtColumns();
      var _this$state = this.state,
          nodesListSource = _this$state.nodesListSource,
          target = _this$state.target,
          queuesListSource = _this$state.queuesListSource,
          type = _this$state.type;
      var _this$state$resourceM = this.state.resourceMetrics,
          usedCores = _this$state$resourceM.usedCores,
          totalCores = _this$state$resourceM.totalCores,
          usedMem = _this$state$resourceM.usedMem,
          totalMem = _this$state$resourceM.totalMem,
          memRate = _this$state$resourceM.memRate,
          coresRate = _this$state$resourceM.coresRate;
      console.log(this.state);
      return /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("div", {
        className: "c-resourceView__container"
      }, /*#__PURE__*/React.createElement("div", {
        style: {
          height: 110,
          width: '50%',
          marginRight: 10
        }
      }, /*#__PURE__*/React.createElement(ResourceCard, {
        type: "cpu",
        title: "CPU\uFF08core\uFF09",
        useNum: usedCores,
        total: totalCores,
        value: coresRate
      })), /*#__PURE__*/React.createElement("div", {
        style: {
          height: 110,
          width: '50%',
          marginLeft: 10
        }
      }, /*#__PURE__*/React.createElement(ResourceCard, {
        type: "memory",
        title: "\u5185\u5B58\uFF08GB\uFF09",
        useNum: usedMem,
        total: totalMem,
        value: memRate
      }))), /*#__PURE__*/React.createElement(RenderTable, {
        columns: columnsNodes,
        data: nodesListSource,
        title: "Yarn-NodeManager\u8D44\u6E90\u4F7F\u7528"
      }), /*#__PURE__*/React.createElement(RenderTable, {
        key: type,
        columns: columnsQueues,
        data: queuesListSource,
        title: "\u5404\u8D44\u6E90\u961F\u5217\u8D44\u6E90\u4F7F\u7528\uFF08\u8C03\u5EA6\u65B9\u5F0F\uFF1A".concat((0, _lodash.findKey)(_constant.SCHEDULE_TYPE, function (val) {
          return val === type;
        }), "\uFF09")
      }), target ? /*#__PURE__*/React.createElement(RenderTable, {
        columns: columnsDetail,
        data: target.users,
        title: "\u8D44\u6E90\u8BE6\u60C5",
        desc: target.queueName
      }) : null);
    }
  }]);
  return Resource;
}(React.Component);

var _default = Resource;
exports["default"] = _default;

/***/ }),

/***/ "kYLF":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("sMBO");

__webpack_require__("zKZe");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _table = _interopRequireDefault(__webpack_require__("DtFj"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _popconfirm = _interopRequireDefault(__webpack_require__("h0/l"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _regenerator = _interopRequireDefault(__webpack_require__("o0o1"));

__webpack_require__("ls82");

var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__("yXPU"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _input = _interopRequireDefault(__webpack_require__("iJl9"));

var _select = _interopRequireDefault(__webpack_require__("FAat"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _lodash = __webpack_require__("LvDl");

var _utils = _interopRequireDefault(__webpack_require__("j+Cx"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

var _account = _interopRequireDefault(__webpack_require__("itRn"));

var _consts = __webpack_require__("RzPm");

var _bindModal = _interopRequireDefault(__webpack_require__("ZeNE"));

var _ldapBindModal = _interopRequireDefault(__webpack_require__("K4z+"));

var _clusterFunc = __webpack_require__("IiER");

var _help = __webpack_require__("FZsQ");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var Option = _select["default"].Option;
var Search = _input["default"].Search;
var PAGESIZE = 10;

var BindAccountTable = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(BindAccountTable, _React$Component);

  var _super = _createSuper(BindAccountTable);

  function BindAccountTable() {
    var _this;

    (0, _classCallCheck2["default"])(this, BindAccountTable);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      tableData: [],
      unbindUserList: [],
      tenantList: [],
      queryParams: {
        dtuicTenantId: '',
        username: '',
        currentPage: 1,
        total: 0,
        pageSize: PAGESIZE
      },
      modalData: null,
      loading: false,
      visible: false
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "fetchData", /*#__PURE__*/(0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee() {
      var queryParams, engineType, res;
      return _regenerator["default"].wrap(function _callee$(_context) {
        while (1) {
          switch (_context.prev = _context.next) {
            case 0:
              queryParams = _this.state.queryParams;
              engineType = _this.props.engineType;

              _this.setState({
                loading: true
              });

              queryParams.engineType = engineType;
              _context.next = 6;
              return _account["default"].getBindAccounts(queryParams);

            case 6:
              res = _context.sent;

              if (res.code === 1) {
                _this.setState({
                  tableData: (0, _lodash.get)(res, 'data.data', []),
                  queryParams: Object.assign({}, queryParams, {
                    total: (0, _lodash.get)(res, 'data.totalCount', '')
                  })
                });
              }

              _this.setState({
                loading: false
              });

            case 9:
            case "end":
              return _context.stop();
          }
        }
      }, _callee);
    })));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "fetchUnbindUsers", /*#__PURE__*/(0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee2() {
      var queryParams, engineType, res;
      return _regenerator["default"].wrap(function _callee2$(_context2) {
        while (1) {
          switch (_context2.prev = _context2.next) {
            case 0:
              queryParams = _this.state.queryParams;
              engineType = _this.props.engineType;
              _context2.next = 4;
              return _account["default"].getUnbindAccounts({
                dtuicTenantId: queryParams.dtuicTenantId,
                engineType: engineType
              });

            case 4:
              res = _context2.sent;

              if (res.code === 1) {
                _this.setState({
                  unbindUserList: (0, _lodash.get)(res, 'data', [])
                });
              }

            case 6:
            case "end":
              return _context2.stop();
          }
        }
      }, _callee2);
    })));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "fetchTenants", /*#__PURE__*/(0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee3() {
      var _this$props, clusterId, engineType, response, tenantList;

      return _regenerator["default"].wrap(function _callee3$(_context3) {
        while (1) {
          switch (_context3.prev = _context3.next) {
            case 0:
              _this$props = _this.props, clusterId = _this$props.clusterId, engineType = _this$props.engineType;
              _context3.next = 3;
              return _console["default"].searchTenant({
                clusterId: clusterId,
                engineType: engineType,
                currentPage: 1,
                pageSize: 1000
              });

            case 3:
              response = _context3.sent;

              if (response.code === 1) {
                tenantList = (0, _lodash.get)(response, 'data.data', []);

                _this.setState({
                  tenantList: tenantList
                });

                if (tenantList && tenantList.length > 0) {
                  _this.onTenantChange("".concat(tenantList[0].tenantId));
                }
              }

            case 5:
            case "end":
              return _context3.stop();
          }
        }
      }, _callee3);
    })));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "updateQueryParams", function (params, callback) {
      _this.setState({
        queryParams: Object.assign(_this.state.queryParams, params)
      }, function () {
        if (callback) callback();
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onTenantChange", function (value) {
      _this.updateQueryParams({
        dtuicTenantId: value
      }, function () {
        _this.handleTableChange({
          current: 1
        });

        _this.fetchUnbindUsers();
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleTableChange", function (pagination, filters, sorter) {
      _this.updateQueryParams({
        currentPage: pagination.current
      }, _this.fetchData);
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "showHideBindModal", function (item) {
      _this.setState({
        visible: !_this.state.visible,
        modalData: item
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onUnBindAccount", /*#__PURE__*/function () {
      var _ref4 = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee4(account) {
        var engineType, params, res;
        return _regenerator["default"].wrap(function _callee4$(_context4) {
          while (1) {
            switch (_context4.prev = _context4.next) {
              case 0:
                engineType = _this.props.engineType;
                params = {
                  id: account.bindUserId,
                  name: account.name,
                  password: account.password || ''
                };

                if ((0, _clusterFunc.isHadoopEngine)(engineType)) {
                  params = _objectSpread(_objectSpread({}, params), {}, {
                    id: account.id
                  });
                }

                _context4.next = 5;
                return _account["default"].unbindAccount(params);

              case 5:
                res = _context4.sent;

                if (res.code === 1) {
                  _message2["default"].success('解绑成功！');

                  !(0, _clusterFunc.isHadoopEngine)(engineType) && _this.showHideBindModal(null);

                  _this.handleTableChange({
                    current: 1
                  });

                  _this.fetchUnbindUsers();
                }

              case 7:
              case "end":
                return _context4.stop();
            }
          }
        }, _callee4);
      }));

      return function (_x) {
        return _ref4.apply(this, arguments);
      };
    }());
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onBindAccountUpdate", /*#__PURE__*/function () {
      var _ref5 = (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee5(account) {
        var _this$state, queryParams, modalData, engineType, isEdit, handOk, res, params;

        return _regenerator["default"].wrap(function _callee5$(_context5) {
          while (1) {
            switch (_context5.prev = _context5.next) {
              case 0:
                _this$state = _this.state, queryParams = _this$state.queryParams, modalData = _this$state.modalData;
                engineType = _this.props.engineType;
                isEdit = modalData;

                handOk = function handOk() {
                  _this.showHideBindModal(null);

                  _this.handleTableChange({
                    current: 1
                  });

                  _this.fetchUnbindUsers();
                };

                res = {
                  code: 0
                };

                if (!isEdit) {
                  _context5.next = 13;
                  break;
                }

                account.bindTenantId = queryParams.dtuicTenantId;
                account.engineType = engineType;
                _context5.next = 10;
                return _account["default"].updateBindAccount(account);

              case 10:
                res = _context5.sent;
                _context5.next = 25;
                break;

              case 13:
                if (!(!isEdit && !(0, _clusterFunc.isHadoopEngine)(engineType))) {
                  _context5.next = 21;
                  break;
                }

                account.bindTenantId = queryParams.dtuicTenantId;
                account.engineType = engineType;
                _context5.next = 18;
                return _account["default"].bindAccount(account);

              case 18:
                res = _context5.sent;
                _context5.next = 25;
                break;

              case 21:
                params = account.map(function (a) {
                  return _objectSpread(_objectSpread({}, a), {}, {
                    bindTenantId: queryParams.dtuicTenantId,
                    engineType: engineType
                  });
                });
                _context5.next = 24;
                return _account["default"].ldapBindAccount({
                  accountList: params
                });

              case 24:
                res = _context5.sent;

              case 25:
                if (res.code === 1) {
                  _message2["default"].success('绑定成功！');

                  handOk();
                }

              case 26:
              case "end":
                return _context5.stop();
            }
          }
        }, _callee5);
      }));

      return function (_x2) {
        return _ref5.apply(this, arguments);
      };
    }());
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "initColumns", function () {
      var engineType = _this.props.engineType;
      return [{
        title: '产品账号',
        dataIndex: 'username',
        render: function render(text, record) {
          return text;
        }
      }, {
        title: (0, _clusterFunc.isHadoopEngine)(engineType) ? 'LDAP账号' : '数据库账号',
        dataIndex: 'name',
        render: function render(text, record) {
          return text;
        }
      }, {
        title: '最近修改人',
        dataIndex: 'modifyUserName',
        render: function render(text, record) {
          return text;
        }
      }, {
        title: '最近修改时间',
        dataIndex: 'gmtModified',
        render: function render(text, record) {
          return _utils["default"].formatDateTime(text);
        }
      }, {
        title: '操作',
        dataIndex: 'deal',
        render: function render(text, record) {
          if ((0, _clusterFunc.isHadoopEngine)(engineType)) {
            return /*#__PURE__*/React.createElement("span", null, /*#__PURE__*/React.createElement("a", {
              onClick: function onClick() {
                _this.showHideBindModal(record);
              }
            }, "\u7F16\u8F91"), /*#__PURE__*/React.createElement("span", {
              className: "ant-divider"
            }), /*#__PURE__*/React.createElement(_popconfirm["default"], {
              title: "\u786E\u8BA4\u5220\u9664\u8BE5LDAP\u8D26\u53F7\u7ED1\u5B9A\uFF1F",
              okText: "\u786E\u5B9A",
              cancelText: "\u53D6\u6D88",
              onConfirm: function onConfirm() {
                _this.onUnBindAccount(record);
              }
            }, /*#__PURE__*/React.createElement("a", {
              style: {
                color: '#FF5F5C'
              }
            }, "\u5220\u9664")));
          }

          return /*#__PURE__*/React.createElement("a", {
            onClick: function onClick() {
              _this.showHideBindModal(record);
            }
          }, "\u4FEE\u6539\u7ED1\u5B9A");
        }
      }];
    });
    return _this;
  }

  (0, _createClass2["default"])(BindAccountTable, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      this.fetchTenants();
    }
  }, {
    key: "render",
    value: function render() {
      var _this2 = this;

      var _this$state2 = this.state,
          tableData = _this$state2.tableData,
          queryParams = _this$state2.queryParams,
          loading = _this$state2.loading,
          visible = _this$state2.visible,
          modalData = _this$state2.modalData,
          tenantList = _this$state2.tenantList,
          unbindUserList = _this$state2.unbindUserList;
      var engineType = this.props.engineType;
      var pagination = {
        current: queryParams.currentPage,
        pageSize: PAGESIZE,
        total: queryParams.total
      };
      return /*#__PURE__*/React.createElement("div", {
        style: {
          margin: '15px'
        }
      }, /*#__PURE__*/React.createElement(_select["default"], {
        className: "cluster-select",
        style: {
          width: '180px'
        },
        placeholder: "\u8BF7\u9009\u62E9\u79DF\u6237",
        showSearch: true,
        value: "".concat(queryParams.dtuicTenantId),
        optionFilterProp: "title",
        onChange: this.onTenantChange
      }, tenantList && tenantList.map(function (item) {
        return /*#__PURE__*/React.createElement(Option, {
          key: "".concat(item.tenantId),
          title: item.tenantName,
          value: "".concat(item.tenantId)
        }, item.tenantName);
      })), /*#__PURE__*/React.createElement(Search, {
        style: {
          width: '200px',
          marginBottom: '20px',
          marginLeft: '10px'
        },
        placeholder: "\u6309\u4EA7\u54C1\u8D26\u53F7\u3001".concat((0, _clusterFunc.isHadoopEngine)(engineType) ? 'LDAP' : '数据库', "\u8D26\u53F7\u641C\u7D22"),
        value: queryParams.username,
        onChange: function onChange(e) {
          _this2.updateQueryParams({
            username: e.target.value,
            currentPage: 1
          });
        },
        onSearch: this.fetchData
      }), /*#__PURE__*/React.createElement("span", {
        className: "right"
      }, /*#__PURE__*/React.createElement(_button["default"], {
        type: "primary",
        onClick: function onClick() {
          return _this2.showHideBindModal();
        }
      }, "\u7ED1\u5B9A\u8D26\u53F7")), /*#__PURE__*/React.createElement(_table["default"], {
        className: "dt-table-border",
        loading: loading,
        rowKey: function rowKey(record, index) {
          return "accounts-".concat(index, "-").concat(record.userId);
        },
        columns: this.initColumns(),
        dataSource: tableData,
        pagination: pagination,
        onChange: this.handleTableChange
      }), !(0, _clusterFunc.isHadoopEngine)(engineType) ? /*#__PURE__*/React.createElement(_bindModal["default"], {
        visible: visible,
        data: modalData,
        userList: modalData ? tableData : unbindUserList,
        title: modalData ? '编辑账号' : '绑定账号',
        onOk: this.onBindAccountUpdate,
        onUnbind: this.onUnBindAccount,
        onCancel: function onCancel() {
          return _this2.showHideBindModal(null);
        },
        engineText: _consts.ENGIN_TYPE_TEXT[engineType]
      }) : /*#__PURE__*/React.createElement(_ldapBindModal["default"], {
        key: (0, _help.giveMeAKey)(),
        visible: visible,
        data: modalData,
        userList: unbindUserList,
        title: modalData ? '编辑账号' : '绑定账号',
        onOk: this.onBindAccountUpdate,
        onCancel: function onCancel() {
          return _this2.showHideBindModal(null);
        }
      }));
    }
  }]);
  return BindAccountTable;
}(React.Component);

var _default = BindAccountTable;
exports["default"] = _default;

/***/ }),

/***/ "kaZZ":
/***/ (function(module, exports, __webpack_require__) {

var map = {
	"./af": "ywm0",
	"./af.js": "ywm0",
	"./ar": "xfuQ",
	"./ar-dz": "WqUt",
	"./ar-dz.js": "WqUt",
	"./ar-kw": "g34P",
	"./ar-kw.js": "g34P",
	"./ar-ly": "/zFM",
	"./ar-ly.js": "/zFM",
	"./ar-ma": "wp1y",
	"./ar-ma.js": "wp1y",
	"./ar-sa": "g5BY",
	"./ar-sa.js": "g5BY",
	"./ar-tn": "h4gT",
	"./ar-tn.js": "h4gT",
	"./ar.js": "xfuQ",
	"./az": "tZF6",
	"./az.js": "tZF6",
	"./be": "QAwJ",
	"./be.js": "QAwJ",
	"./bg": "wdh0",
	"./bg.js": "wdh0",
	"./bm": "f8vO",
	"./bm.js": "f8vO",
	"./bn": "PQDu",
	"./bn-bd": "7uPm",
	"./bn-bd.js": "7uPm",
	"./bn.js": "PQDu",
	"./bo": "M+//",
	"./bo.js": "M+//",
	"./br": "PcHX",
	"./br.js": "PcHX",
	"./bs": "ObId",
	"./bs.js": "ObId",
	"./ca": "TffX",
	"./ca.js": "TffX",
	"./cs": "XE1a",
	"./cs.js": "XE1a",
	"./cv": "ZoQ8",
	"./cv.js": "ZoQ8",
	"./cy": "WVdD",
	"./cy.js": "WVdD",
	"./da": "HcEQ",
	"./da.js": "HcEQ",
	"./de": "bqXG",
	"./de-at": "ei0R",
	"./de-at.js": "ei0R",
	"./de-ch": "3nt/",
	"./de-ch.js": "3nt/",
	"./de.js": "bqXG",
	"./dv": "A5im",
	"./dv.js": "A5im",
	"./el": "dJol",
	"./el.js": "dJol",
	"./en-au": "DI6O",
	"./en-au.js": "DI6O",
	"./en-ca": "7wED",
	"./en-ca.js": "7wED",
	"./en-gb": "0boi",
	"./en-gb.js": "0boi",
	"./en-ie": "iay8",
	"./en-ie.js": "iay8",
	"./en-il": "DbSe",
	"./en-il.js": "DbSe",
	"./en-in": "2yOs",
	"./en-in.js": "2yOs",
	"./en-nz": "L7rQ",
	"./en-nz.js": "L7rQ",
	"./en-sg": "dgJH",
	"./en-sg.js": "dgJH",
	"./eo": "rpXD",
	"./eo.js": "rpXD",
	"./es": "ZBsk",
	"./es-do": "AZ+q",
	"./es-do.js": "AZ+q",
	"./es-mx": "YI0v",
	"./es-mx.js": "YI0v",
	"./es-us": "m+Kj",
	"./es-us.js": "m+Kj",
	"./es.js": "ZBsk",
	"./et": "O26U",
	"./et.js": "O26U",
	"./eu": "Dm2w",
	"./eu.js": "Dm2w",
	"./fa": "x/UU",
	"./fa.js": "x/UU",
	"./fi": "UOM9",
	"./fi.js": "UOM9",
	"./fil": "4nsI",
	"./fil.js": "4nsI",
	"./fo": "Jl0t",
	"./fo.js": "Jl0t",
	"./fr": "wwtS",
	"./fr-ca": "bgUh",
	"./fr-ca.js": "bgUh",
	"./fr-ch": "v8r9",
	"./fr-ch.js": "v8r9",
	"./fr.js": "wwtS",
	"./fy": "ispS",
	"./fy.js": "ispS",
	"./ga": "9vQk",
	"./ga.js": "9vQk",
	"./gd": "WzX9",
	"./gd.js": "WzX9",
	"./gl": "C2tv",
	"./gl.js": "C2tv",
	"./gom-deva": "OPal",
	"./gom-deva.js": "OPal",
	"./gom-latn": "ps6a",
	"./gom-latn.js": "ps6a",
	"./gu": "/BxG",
	"./gu.js": "/BxG",
	"./he": "u9BN",
	"./he.js": "u9BN",
	"./hi": "CemP",
	"./hi.js": "CemP",
	"./hr": "HGZp",
	"./hr.js": "HGZp",
	"./hu": "IWRJ",
	"./hu.js": "IWRJ",
	"./hy-am": "OKHI",
	"./hy-am.js": "OKHI",
	"./id": "KYSN",
	"./id.js": "KYSN",
	"./is": "/A5/",
	"./is.js": "/A5/",
	"./it": "LCp6",
	"./it-ch": "IqAm",
	"./it-ch.js": "IqAm",
	"./it.js": "LCp6",
	"./ja": "6MD4",
	"./ja.js": "6MD4",
	"./jv": "izZI",
	"./jv.js": "izZI",
	"./ka": "c7aE",
	"./ka.js": "c7aE",
	"./kk": "yNxy",
	"./kk.js": "yNxy",
	"./km": "KRRr",
	"./km.js": "KRRr",
	"./kn": "yLIN",
	"./kn.js": "yLIN",
	"./ko": "+vUy",
	"./ko.js": "+vUy",
	"./ku": "Ye4i",
	"./ku.js": "Ye4i",
	"./ky": "R2ED",
	"./ky.js": "R2ED",
	"./lb": "749k",
	"./lb.js": "749k",
	"./lo": "sFn+",
	"./lo.js": "sFn+",
	"./lt": "HihB",
	"./lt.js": "HihB",
	"./lv": "UpP0",
	"./lv.js": "UpP0",
	"./me": "sbC4",
	"./me.js": "sbC4",
	"./mi": "NP+U",
	"./mi.js": "NP+U",
	"./mk": "zQ24",
	"./mk.js": "zQ24",
	"./ml": "rNv0",
	"./ml.js": "rNv0",
	"./mn": "fNMV",
	"./mn.js": "fNMV",
	"./mr": "wgvq",
	"./mr.js": "wgvq",
	"./ms": "Y1qj",
	"./ms-my": "u0Om",
	"./ms-my.js": "u0Om",
	"./ms.js": "Y1qj",
	"./mt": "TeeV",
	"./mt.js": "TeeV",
	"./my": "616q",
	"./my.js": "616q",
	"./nb": "BPep",
	"./nb.js": "BPep",
	"./ne": "A3YO",
	"./ne.js": "A3YO",
	"./nl": "Y9Aq",
	"./nl-be": "fnVk",
	"./nl-be.js": "fnVk",
	"./nl.js": "Y9Aq",
	"./nn": "J7Xp",
	"./nn.js": "J7Xp",
	"./oc-lnc": "gfhg",
	"./oc-lnc.js": "gfhg",
	"./pa-in": "QhQT",
	"./pa-in.js": "QhQT",
	"./pl": "6sm9",
	"./pl.js": "6sm9",
	"./pt": "AiLN",
	"./pt-br": "Ph03",
	"./pt-br.js": "Ph03",
	"./pt.js": "AiLN",
	"./ro": "sBAS",
	"./ro.js": "sBAS",
	"./ru": "pDm9",
	"./ru.js": "pDm9",
	"./sd": "lULW",
	"./sd.js": "lULW",
	"./se": "HONv",
	"./se.js": "HONv",
	"./si": "Nfnh",
	"./si.js": "Nfnh",
	"./sk": "+LIA",
	"./sk.js": "+LIA",
	"./sl": "jYGj",
	"./sl.js": "jYGj",
	"./sq": "vQMS",
	"./sq.js": "vQMS",
	"./sr": "LJkV",
	"./sr-cyrl": "85R6",
	"./sr-cyrl.js": "85R6",
	"./sr.js": "LJkV",
	"./ss": "i1mr",
	"./ss.js": "i1mr",
	"./sv": "VWpO",
	"./sv.js": "VWpO",
	"./sw": "bxiW",
	"./sw.js": "bxiW",
	"./ta": "is5F",
	"./ta.js": "is5F",
	"./te": "b8kL",
	"./te.js": "b8kL",
	"./tet": "O/Pc",
	"./tet.js": "O/Pc",
	"./tg": "+LjB",
	"./tg.js": "+LjB",
	"./th": "SR3F",
	"./th.js": "SR3F",
	"./tk": "D9Nd",
	"./tk.js": "D9Nd",
	"./tl-ph": "Ic75",
	"./tl-ph.js": "Ic75",
	"./tlh": "zM/U",
	"./tlh.js": "zM/U",
	"./tr": "XuJn",
	"./tr.js": "XuJn",
	"./tzl": "fsgd",
	"./tzl.js": "fsgd",
	"./tzm": "iyqY",
	"./tzm-latn": "yhnr",
	"./tzm-latn.js": "yhnr",
	"./tzm.js": "iyqY",
	"./ug-cn": "oADZ",
	"./ug-cn.js": "oADZ",
	"./uk": "6gjB",
	"./uk.js": "6gjB",
	"./ur": "2YoS",
	"./ur.js": "2YoS",
	"./uz": "zyqa",
	"./uz-latn": "lS5O",
	"./uz-latn.js": "lS5O",
	"./uz.js": "zyqa",
	"./vi": "NLtu",
	"./vi.js": "NLtu",
	"./x-pseudo": "NQ/h",
	"./x-pseudo.js": "NQ/h",
	"./yo": "uSf3",
	"./yo.js": "uSf3",
	"./zh-cn": "IUwI",
	"./zh-cn.js": "IUwI",
	"./zh-hk": "Kh9b",
	"./zh-hk.js": "Kh9b",
	"./zh-mo": "O+7K",
	"./zh-mo.js": "O+7K",
	"./zh-tw": "5Aom",
	"./zh-tw.js": "5Aom"
};


function webpackContext(req) {
	var id = webpackContextResolve(req);
	return __webpack_require__(id);
}
function webpackContextResolve(req) {
	if(!__webpack_require__.o(map, req)) {
		var e = new Error("Cannot find module '" + req + "'");
		e.code = 'MODULE_NOT_FOUND';
		throw e;
	}
	return map[req];
}
webpackContext.keys = function webpackContextKeys() {
	return Object.keys(map);
};
webpackContext.resolve = webpackContextResolve;
module.exports = webpackContext;
webpackContext.id = "kaZZ";

/***/ }),

/***/ "o4I+":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.userActions = void 0;

var _mirrorCreator = _interopRequireDefault(__webpack_require__("zbcj"));

var userActions = (0, _mirrorCreator["default"])(['SET_TENANT_LIST'], {
  prefix: 'console/user'
});
exports.userActions = userActions;

/***/ }),

/***/ "pWBi":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("TeQF");

__webpack_require__("QWBl");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("5s+n");

__webpack_require__("FZtP");

__webpack_require__("R5XZ");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.authBeforeFormate = authBeforeFormate;
exports.authAfterFormated = authAfterFormated;
exports.isSelectedProject = isSelectedProject;
exports.isLogin = isLogin;

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _notification2 = _interopRequireDefault(__webpack_require__("rR1Q"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

var _reactRouter = __webpack_require__("dtw8");

var _utils = _interopRequireDefault(__webpack_require__("j+Cx"));

var _localDb = _interopRequireDefault(__webpack_require__("hl8m"));

var _user = _interopRequireDefault(__webpack_require__("IdVG"));

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

var maxHeightStyle = {
  maxHeight: '500px',
  overflowY: 'auto'
};

function authBeforeFormate(response) {
  switch (response.status) {
    case 402:
    case 200:
      return response;

    case 302:
      _message2["default"].info('登录超时, 请重新登录！');

      return Promise.reject(response);

    case 500:
      _message2["default"].error('服务器出现了点问题');

      return Promise.reject(response);

    default:
      if (false) {}

      return response;
  }
}

function authAfterFormated(response) {
  switch (response.code) {
    case 1:
      return response;

    case 0:
      // 无权限，需要登录
      _user["default"].logout();

      return response;

    case 3:
      // 功能无权限
      _notification2["default"]['error']({
        message: '权限通知',
        description: response.message
      });

      return Promise.reject(response);

    case 16:
      // 需要重新进入Web首页选择项目，并进入
      _reactRouter.hashHistory.push('/');

      return Promise.reject(response);

    default:
      if (response.message) {
        setTimeout(function () {
          _notification2["default"]['error']({
            message: '异常',
            description: response.message,
            style: _objectSpread(_objectSpread({}, maxHeightStyle), {}, {
              wordBreak: 'break-all'
            })
          });
        }, 0);
      }

      return response;
  }
}

function isSelectedProject() {
  var pid = _utils["default"].getCookie('project_id');

  if (!pid || pid === 'undefined') {
    _utils["default"].deleteCookie('project_id'); // browserHistory.push('/')

  }
}

function isLogin() {
  return _localDb["default"].get('session');
}

/***/ }),

/***/ "pXCp":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("x0AG");

__webpack_require__("QWBl");

__webpack_require__("4mDm");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("T63A");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("inlA");

__webpack_require__("LKBx");

__webpack_require__("FZtP");

__webpack_require__("3bBZ");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _input = _interopRequireDefault(__webpack_require__("iJl9"));

var _row = _interopRequireDefault(__webpack_require__("9xET"));

var _col = _interopRequireDefault(__webpack_require__("ZPTe"));

var _slicedToArray2 = _interopRequireDefault(__webpack_require__("J4zp"));

var _toConsumableArray2 = _interopRequireDefault(__webpack_require__("RIqP"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _consts = __webpack_require__("RzPm");

var _help = __webpack_require__("LNB4");

var _const = __webpack_require__("j1Tt");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var FormItem = _form["default"].Item;

var CustomParams = /*#__PURE__*/function (_React$PureComponent) {
  (0, _inherits2["default"])(CustomParams, _React$PureComponent);

  var _super = _createSuper(CustomParams);

  function CustomParams() {
    var _this;

    (0, _classCallCheck2["default"])(this, CustomParams);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      customParams: []
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "addCustomerParams", function () {
      _this.setState(function (preState) {
        return {
          customParams: [].concat((0, _toConsumableArray2["default"])(preState.customParams), [{
            id: (0, _help.giveMeAKey)()
          }])
        };
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "deleteCustomerParams", function (id) {
      var customParams = _this.state.customParams;
      var newCustomParam = customParams.filter(function (param) {
        return param.id !== id;
      });

      _this.setState({
        customParams: newCustomParam
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleCustomParam", function (e, id) {
      var _getValueByJson, _form$getFieldValue;

      var value = e.target.value;
      var _this$props = _this.props,
          template = _this$props.template,
          form = _this$props.form,
          typeCode = _this$props.typeCode,
          comp = _this$props.comp;
      var customParams = _this.state.customParams;
      var isGroup = template.type == _const.CONFIG_ITEM_TYPE.GROUP;
      var feildName = isGroup ? "".concat(typeCode, ".customParam.").concat(template.key) : "".concat(typeCode, ".customParam");
      var compConfig = (_getValueByJson = (0, _help.getValueByJson)(comp === null || comp === void 0 ? void 0 : comp.componentConfig)) !== null && _getValueByJson !== void 0 ? _getValueByJson : {};
      var config = (_form$getFieldValue = form.getFieldValue("".concat(typeCode, ".specialConfig"))) !== null && _form$getFieldValue !== void 0 ? _form$getFieldValue : compConfig;
      var keyAndValue = Object.entries(config);
      /**
       * 与已渲染表单值、模版固定参数比较自定义参数是否相同
       *  yarn、hdfs组件需要比较componentConfig中的key值是否相同
       */

      var sameAtTemp = -1;
      var sameAtParams = false;

      if (!(0, _help.isNeedTemp)(typeCode)) {
        var _ref;

        sameAtTemp = (_ref = isGroup ? template.values : template) === null || _ref === void 0 ? void 0 : _ref.findIndex(function (param) {
          return param.key == value && !param.id;
        });
      } else {
        sameAtTemp = keyAndValue.findIndex(function (_ref2) {
          var _ref3 = (0, _slicedToArray2["default"])(_ref2, 2),
              key = _ref3[0],
              name = _ref3[1];

          return key == value;
        });
      }

      for (var _i = 0, _Object$entries = Object.entries(form.getFieldValue(feildName)); _i < _Object$entries.length; _i++) {
        var _Object$entries$_i = (0, _slicedToArray2["default"])(_Object$entries[_i], 2),
            key = _Object$entries$_i[0],
            name = _Object$entries$_i[1];

        if (key.startsWith('%') && key.endsWith('-key') && value == name) {
          sameAtParams = true;
          break;
        }
      }

      var newCustomParam = customParams.map(function (param) {
        if (param.id == id) {
          return _objectSpread(_objectSpread({}, param), {}, {
            isSameKey: sameAtParams || sameAtTemp > -1,
            key: value
          });
        }

        return param;
      });

      _this.setState({
        customParams: newCustomParam
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderAddCustomParam", function () {
      var labelCol = _this.props.labelCol;
      return /*#__PURE__*/React.createElement(_row["default"], null, /*#__PURE__*/React.createElement(_col["default"], {
        span: labelCol !== null && labelCol !== void 0 ? labelCol : _consts.formItemLayout.labelCol.sm.span
      }), /*#__PURE__*/React.createElement(_col["default"], {
        className: "m-card",
        style: {
          marginBottom: '20px'
        },
        span: _consts.formItemLayout.wrapperCol.sm.span
      }, /*#__PURE__*/React.createElement("a", {
        onClick: function onClick() {
          return _this.addCustomerParams();
        }
      }, "\u6DFB\u52A0\u81EA\u5B9A\u4E49\u53C2\u6570")));
    });
    return _this;
  }

  (0, _createClass2["default"])(CustomParams, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      var template = this.props.template;
      var isGroup = template.type == _const.CONFIG_ITEM_TYPE.GROUP;
      this.setState({
        customParams: (0, _help.getCustomerParams)(isGroup ? template.values : template)
      });
    } // 新增自定义参数

  }, {
    key: "render",
    value: function render() {
      var _this2 = this;

      var _this$props2 = this.props,
          typeCode = _this$props2.typeCode,
          form = _this$props2.form,
          view = _this$props2.view,
          template = _this$props2.template,
          maxWidth = _this$props2.maxWidth,
          labelCol = _this$props2.labelCol,
          wrapperCol = _this$props2.wrapperCol;
      var customParams = this.state.customParams;
      var groupKey = template.key;

      if (customParams.length == 0) {
        return !view && this.renderAddCustomParam();
      }

      return /*#__PURE__*/React.createElement(React.Fragment, null, customParams && customParams.map(function (param) {
        var fieldName = groupKey ? "".concat(typeCode, ".customParam.").concat(groupKey) : "".concat(typeCode, ".customParam");
        return param.id && /*#__PURE__*/React.createElement(_row["default"], {
          key: param.id
        }, /*#__PURE__*/React.createElement(_col["default"], {
          span: labelCol !== null && labelCol !== void 0 ? labelCol : _consts.formItemLayout.labelCol.sm.span
        }, /*#__PURE__*/React.createElement(FormItem, {
          key: param.id + '-key'
        }, form.getFieldDecorator("".concat(fieldName, ".%").concat(param.id, "-key"), {
          rules: [{
            required: true,
            message: '请输入参数属性名'
          }],
          initialValue: param.key || ''
        })( /*#__PURE__*/React.createElement(_input["default"], {
          disabled: view,
          style: {
            width: 'calc(100% - 12px)'
          },
          onChange: function onChange(e) {
            return _this2.handleCustomParam(e, param.id);
          }
        })), /*#__PURE__*/React.createElement("span", {
          style: {
            marginLeft: 2
          }
        }, ":"))), /*#__PURE__*/React.createElement(_col["default"], {
          span: wrapperCol !== null && wrapperCol !== void 0 ? wrapperCol : _consts.formItemLayout.wrapperCol.sm.span
        }, /*#__PURE__*/React.createElement(FormItem, {
          key: param.id + '-value'
        }, form.getFieldDecorator("".concat(fieldName, ".%").concat(param.id, "-value"), {
          rules: [{
            required: true,
            message: '请输入参数属性值'
          }],
          initialValue: param.value || ''
        })( /*#__PURE__*/React.createElement(_input["default"], {
          disabled: view,
          style: {
            maxWidth: maxWidth ? 680 : 'unset'
          }
        })))), !view && /*#__PURE__*/React.createElement("a", {
          className: "formItem-right-text",
          onClick: function onClick() {
            return _this2.deleteCustomerParams(param.id);
          }
        }, "\u5220\u9664"), !view && param.isSameKey && /*#__PURE__*/React.createElement("span", {
          className: "formItem-right-text"
        }, "\u8BE5\u53C2\u6570\u5DF2\u5B58\u5728"));
      }), !view && this.renderAddCustomParam());
    }
  }]);
  return CustomParams;
}(React.PureComponent);

exports["default"] = CustomParams;

/***/ }),

/***/ "s7lY":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("yXV3");

__webpack_require__("oVuX");

__webpack_require__("2B1R");

__webpack_require__("zKZe");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _tooltip = _interopRequireDefault(__webpack_require__("d1El"));

var _input = _interopRequireDefault(__webpack_require__("iJl9"));

var _extends2 = _interopRequireDefault(__webpack_require__("pVnL"));

var _icon = _interopRequireDefault(__webpack_require__("Pbn2"));

var _toConsumableArray2 = _interopRequireDefault(__webpack_require__("RIqP"));

var _slicedToArray2 = _interopRequireDefault(__webpack_require__("J4zp"));

var _form = _interopRequireDefault(__webpack_require__("qu0K"));

var _select = _interopRequireDefault(__webpack_require__("FAat"));

var _react = _interopRequireWildcard(__webpack_require__("q1tI"));

var _lodash = __webpack_require__("LvDl");

var _api = _interopRequireDefault(__webpack_require__("pYSY"));

var _customHooks = __webpack_require__("A9pX");

var _consts = __webpack_require__("RzPm");

var Option = _select["default"].Option;
var FormItem = _form["default"].Item;

var CustomModal = function CustomModal(props) {
  var form = props.form,
      _props$form = props.form,
      getFieldDecorator = _props$form.getFieldDecorator,
      resetFields = _props$form.resetFields,
      visible = props.visible,
      _onOk = props.onOk,
      onCancel = props.onCancel,
      title = props.title,
      isBindTenant = props.isBindTenant,
      disabled = props.disabled,
      _props$tenantInfo = props.tenantInfo,
      tenantInfo = _props$tenantInfo === void 0 ? {} : _props$tenantInfo,
      Id = props.clusterId,
      clusterList = props.clusterList,
      isBindNamespace = props.isBindNamespace;

  var _useState = (0, _react.useState)([]),
      _useState2 = (0, _slicedToArray2["default"])(_useState, 2),
      tenantList = _useState2[0],
      setTenantList = _useState2[1];

  var prevVisible = (0, _react.useRef)(null);

  var _useState3 = (0, _react.useState)(Id),
      _useState4 = (0, _slicedToArray2["default"])(_useState3, 2),
      clusterId = _useState4[0],
      setClusterId = _useState4[1];

  var _useEnv = (0, _customHooks.useEnv)({
    clusterId: clusterId || Id,
    visible: visible,
    form: form,
    clusterList: clusterList
  }),
      env = _useEnv.env,
      queueList = _useEnv.queueList; // 切换集群


  (0, _react.useEffect)(function () {
    prevVisible.current = visible;

    if (visible === false) {
      resetFields();
      setClusterId(undefined);
    }
  }, [visible, resetFields]);

  var onSearchTenantUser = function onSearchTenantUser(value) {
    _api["default"].getFullTenants(value).then(function (res) {
      if (res.success) {
        setTenantList(res.data || []);
      }
    });
  };

  var handleChangeCluster = function handleChangeCluster(e) {
    setClusterId(e);
  };

  var debounceSearchTenant = (0, _lodash.debounce)(onSearchTenantUser, 1000);

  var getServiceParam = function getServiceParam() {
    var params = {
      canSubmit: false,
      reqParams: {}
    };

    var _props$form2 = props === null || props === void 0 ? void 0 : props.form,
        getFieldsValue = _props$form2.getFieldsValue,
        validateFields = _props$form2.validateFields;

    var reqParams = getFieldsValue();
    var hasKubernetes = env.hasKubernetes;
    validateFields(function (err) {
      if (!err) {
        params.canSubmit = true;
        params.reqParams = reqParams; // 切换队列覆盖默认值name

        if (!isBindTenant) params.reqParams = Object.assign(reqParams, {
          tenantId: tenantInfo.tenantId
        });

        if (isBindNamespace) {
          params.reqParams = Object.assign(reqParams, {
            tenantId: tenantInfo.tenantId,
            queueId: tenantInfo.queueId
          });
        }

        params.hasKubernetes = hasKubernetes;
      }
    });
    return params;
  };

  var getEnginName = function getEnginName() {
    var hasLibra = env.hasLibra,
        hasTiDB = env.hasTiDB,
        hasOracle = env.hasOracle,
        hasGreenPlum = env.hasGreenPlum,
        hasPresto = env.hasPresto;
    var enginName = [];
    enginName = hasLibra ? [].concat((0, _toConsumableArray2["default"])(enginName), ['Libra']) : enginName;
    enginName = hasTiDB ? [].concat((0, _toConsumableArray2["default"])(enginName), ['TiDB']) : enginName;
    enginName = hasOracle ? [].concat((0, _toConsumableArray2["default"])(enginName), ['Oracle']) : enginName;
    enginName = hasGreenPlum ? [].concat((0, _toConsumableArray2["default"])(enginName), ['Greenplum']) : enginName;
    enginName = hasPresto ? [].concat((0, _toConsumableArray2["default"])(enginName), ['Presto']) : enginName;
    return enginName;
  };

  var hasHadoop = env.hasHadoop,
      hasKubernetes = env.hasKubernetes;
  var bindEnginName = getEnginName();
  return /*#__PURE__*/_react["default"].createElement(_modal["default"], {
    title: title,
    visible: visible,
    onOk: function onOk() {
      _onOk(getServiceParam());
    },
    onCancel: onCancel,
    width: "600px",
    className: isBindTenant ? 'no-padding-modal' : ''
  }, /*#__PURE__*/_react["default"].createElement(_react["default"].Fragment, null, isBindTenant && /*#__PURE__*/_react["default"].createElement("div", {
    className: "info-title"
  }, /*#__PURE__*/_react["default"].createElement(_icon["default"], {
    type: "info-circle",
    style: {
      color: '#2491F7'
    }
  }), /*#__PURE__*/_react["default"].createElement("span", {
    className: "info-text"
  }, "\u5C06\u79DF\u6237\u7ED1\u5B9A\u5230\u96C6\u7FA4\uFF0C\u53EF\u4F7F\u7528\u96C6\u7FA4\u5185\u7684\u6BCF\u79CD\u8BA1\u7B97\u5F15\u64CE\uFF0C\u7ED1\u5B9A\u540E\uFF0C\u4E0D\u80FD\u5207\u6362\u5176\u4ED6\u96C6\u7FA4\u3002")), /*#__PURE__*/_react["default"].createElement(_form["default"], null, /*#__PURE__*/_react["default"].createElement(_form["default"].Item, (0, _extends2["default"])({
    label: "\u79DF\u6237"
  }, _consts.formItemLayout), getFieldDecorator('tenantId', {
    rules: [{
      required: true,
      message: '租户不可为空！'
    }],
    initialValue: tenantInfo.tenantName || ''
  })( /*#__PURE__*/_react["default"].createElement(_select["default"], {
    allowClear: true,
    showSearch: true,
    placeholder: "\u8BF7\u641C\u7D22\u8981\u7ED1\u5B9A\u7684\u79DF\u6237",
    optionFilterProp: "title",
    disabled: disabled,
    onSearch: debounceSearchTenant,
    filterOption: function filterOption(input, option) {
      return option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    }
  }, tenantList && tenantList.map(function (tenantItem) {
    return /*#__PURE__*/_react["default"].createElement(Option, {
      key: "".concat(tenantItem.tenantId),
      value: "".concat(tenantItem.tenantId),
      title: tenantItem.tenantName
    }, tenantItem.tenantName);
  })))), /*#__PURE__*/_react["default"].createElement(_form["default"].Item, (0, _extends2["default"])({
    label: "\u96C6\u7FA4"
  }, _consts.formItemLayout), getFieldDecorator('clusterId', {
    rules: [{
      required: true,
      message: '集群不可为空！'
    }],
    initialValue: Id || ''
  })( /*#__PURE__*/_react["default"].createElement(_select["default"], {
    allowClear: true,
    placeholder: "\u8BF7\u9009\u62E9\u96C6\u7FA4",
    disabled: disabled,
    onChange: handleChangeCluster
  }, clusterList.map(function (clusterItem) {
    return /*#__PURE__*/_react["default"].createElement(Option, {
      key: "".concat(clusterItem.clusterId),
      value: "".concat(clusterItem.clusterId)
    }, clusterItem.clusterName);
  })))), hasKubernetes && /*#__PURE__*/_react["default"].createElement("div", {
    className: "border-item"
  }, /*#__PURE__*/_react["default"].createElement("div", {
    className: "engine-title"
  }, "Kubernetes"), /*#__PURE__*/_react["default"].createElement(_form["default"].Item, (0, _extends2["default"])({
    label: "Namespace"
  }, _consts.formItemLayout), getFieldDecorator('namespace', {
    initialValue: (tenantInfo === null || tenantInfo === void 0 ? void 0 : tenantInfo.queue) || ''
  })( /*#__PURE__*/_react["default"].createElement(_input["default"], null)))), hasHadoop && !hasKubernetes ? /*#__PURE__*/_react["default"].createElement("div", {
    className: "border-item"
  }, /*#__PURE__*/_react["default"].createElement("div", {
    className: "engine-title"
  }, "Hadoop"), /*#__PURE__*/_react["default"].createElement(FormItem, (0, _extends2["default"])({
    label: /*#__PURE__*/_react["default"].createElement("span", null, "\u8D44\u6E90\u961F\u5217\xA0", /*#__PURE__*/_react["default"].createElement(_tooltip["default"], {
      title: "\u6307Yarn\u4E0A\u5206\u914D\u7684\u8D44\u6E90\u961F\u5217\uFF0C\u82E5\u4E0B\u62C9\u5217\u8868\u4E2D\u65E0\u5168\u90E8\u961F\u5217\uFF0C\u8BF7\u524D\u5F80\u201C\u591A\u96C6\u7FA4\u7BA1\u7406\u201D\u9875\u9762\u7684\u5177\u4F53\u96C6\u7FA4\u4E2D\u5237\u65B0\u96C6\u7FA4"
    }, /*#__PURE__*/_react["default"].createElement(_icon["default"], {
      type: "question-circle-o"
    })))
  }, _consts.formItemLayout), getFieldDecorator('queueId', {
    rules: [{
      required: true,
      message: '资源队列不可为空！'
    }],
    initialValue: tenantInfo === null || tenantInfo === void 0 ? void 0 : tenantInfo.tenantName
  })( /*#__PURE__*/_react["default"].createElement(_select["default"], {
    allowClear: true,
    placeholder: "\u8BF7\u9009\u62E9\u8D44\u6E90\u961F\u5217"
  }, queueList.map(function (item) {
    return /*#__PURE__*/_react["default"].createElement(Option, {
      key: "".concat(item.queueId),
      value: "".concat(item.queueId)
    }, item.queueName);
  }))))) : null, bindEnginName.length > 0 ? /*#__PURE__*/_react["default"].createElement("div", {
    className: "border-item"
  }, /*#__PURE__*/_react["default"].createElement("div", {
    className: "engine-name"
  }, "\u521B\u5EFA\u9879\u76EE\u65F6\uFF0C\u81EA\u52A8\u5173\u8054\u5230\u79DF\u6237\u7684", bindEnginName.join('、'), "\u5F15\u64CE")) : null)));
};

var areEqual = function areEqual(prevprops, nextprops) {
  if ((prevprops === null || prevprops === void 0 ? void 0 : prevprops.visible) !== (nextprops === null || nextprops === void 0 ? void 0 : nextprops.visible) || (nextprops === null || nextprops === void 0 ? void 0 : nextprops.visible) === true) return false;
  return true;
};

var _default = _form["default"].create()( /*#__PURE__*/_react["default"].memo(CustomModal, areEqual));

exports["default"] = _default;

/***/ }),

/***/ "tgir":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("ma9I");

__webpack_require__("2B1R");

__webpack_require__("DQNa");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _popconfirm = _interopRequireDefault(__webpack_require__("h0/l"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _row = _interopRequireDefault(__webpack_require__("9xET"));

var _col = _interopRequireDefault(__webpack_require__("ZPTe"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _radio = _interopRequireDefault(__webpack_require__("qPIi"));

var _checkbox = _interopRequireDefault(__webpack_require__("g4D/"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _ = _interopRequireWildcard(__webpack_require__("LvDl"));

var _modifyModal = _interopRequireDefault(__webpack_require__("06UE"));

var _help = __webpack_require__("LNB4");

var _const = __webpack_require__("j1Tt");

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var CheckboxGroup = _checkbox["default"].Group;
var RadioGroup = _radio["default"].Group;

var ComponentButton = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(ComponentButton, _React$Component);

  var _super = _createSuper(ComponentButton);

  function ComponentButton() {
    var _this;

    (0, _classCallCheck2["default"])(this, ComponentButton);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      visible: false,
      addComps: [],
      deleteComps: [],
      initialValues: []
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "getInitialValues", function () {
      var _this$props$comps = _this.props.comps,
          comps = _this$props$comps === void 0 ? [] : _this$props$comps;
      return comps.map(function (comp) {
        return comp === null || comp === void 0 ? void 0 : comp.componentTypeCode;
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleSelectValue", function () {
      var comps = _this.props.comps;
      var selectValues = comps.map(function (comp) {
        return comp.componentTypeCode;
      });
      return selectValues;
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleCheckValues", function (value) {
      var activeKey = _this.props.activeKey;

      var initialValues = _this.getInitialValues();

      if ((0, _help.isSourceTab)(activeKey)) {
        return;
      } // 和初始值取两次交集可得删除的组件


      var intersectionArr = _.xor(value, initialValues);

      var deleteComps = _.intersection(intersectionArr, initialValues); // 和初始值取一次合集，一次交集可得增加的组件


      var unionArr = _.union(value, initialValues);

      var addComps = _.xor(unionArr, initialValues);

      _this.setState({
        deleteComps: deleteComps,
        addComps: addComps,
        initialValues: value
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleRadioValues", function (e) {
      var initialValues = _this.getInitialValues(); // 和初始值取不一致时，新增为选中组件，删除已有组件，相同时同步value值


      if (!_.isEqual(initialValues[0], e.target.value)) {
        var deleteComps = initialValues;
        var addComps = [];
        addComps.push(e.target.value);

        _this.setState({
          deleteComps: deleteComps,
          addComps: addComps,
          initialValues: [e.target.value]
        });
      } else {
        _this.setState({
          initialValues: [e.target.value]
        });
      }
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderTitle", function () {
      return /*#__PURE__*/React.createElement("div", {
        className: "c-componentButton__title"
      }, /*#__PURE__*/React.createElement("span", null, "\u7EC4\u4EF6\u914D\u7F6E"));
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "renderContent", function () {
      var activeKey = _this.props.activeKey;
      var initialValues = _this.state.initialValues;

      if ((0, _help.isSourceTab)(activeKey)) {
        return /*#__PURE__*/React.createElement(React.Fragment, null, _this.renderTitle(), /*#__PURE__*/React.createElement(RadioGroup, {
          className: "c-componentButton__content",
          defaultValue: initialValues[0],
          value: initialValues[0],
          onChange: _this.handleRadioValues
        }, /*#__PURE__*/React.createElement(_row["default"], null, _const.CONFIG_BUTTON_TYPE[activeKey].map(function (item) {
          return /*#__PURE__*/React.createElement(_col["default"], {
            key: "".concat(item.code)
          }, /*#__PURE__*/React.createElement(_radio["default"], {
            value: item.code
          }, item.componentName));
        }))));
      }

      return /*#__PURE__*/React.createElement(React.Fragment, null, _this.renderTitle(), /*#__PURE__*/React.createElement(CheckboxGroup, {
        className: "c-componentButton__content",
        value: initialValues,
        defaultValue: initialValues,
        onChange: _this.handleCheckValues
      }, /*#__PURE__*/React.createElement(_row["default"], null, _const.CONFIG_BUTTON_TYPE[activeKey].map(function (item) {
        return /*#__PURE__*/React.createElement(_col["default"], {
          key: "".concat(item.code)
        }, /*#__PURE__*/React.createElement(_checkbox["default"], {
          value: item.code
        }, item.componentName));
      }))));
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleConfirm", function () {
      var _this$state = _this.state,
          addComps = _this$state.addComps,
          deleteComps = _this$state.deleteComps;

      _this.props.handlePopVisible(false);

      if (deleteComps.length > 0) {
        _this.setState({
          visible: true
        });
      } else {
        _this.props.handleConfirm(addComps, deleteComps);
      }
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleCancel", function () {
      _this.setState({
        addComps: [],
        deleteComps: [],
        visible: false
      });

      _this.props.handlePopVisible(false);
    });
    return _this;
  }

  (0, _createClass2["default"])(ComponentButton, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      this.setState({
        initialValues: this.getInitialValues()
      });
    }
  }, {
    key: "componentDidUpdate",
    value: function componentDidUpdate(preProps) {
      var _this$props = this.props,
          comps = _this$props.comps,
          popVisible = _this$props.popVisible;

      if (preProps.comps != comps || preProps.popVisible != popVisible && popVisible) {
        this.setState({
          initialValues: this.getInitialValues()
        });
      }
    }
  }, {
    key: "render",
    value: function render() {
      var _this2 = this;

      var _this$state2 = this.state,
          deleteComps = _this$state2.deleteComps,
          addComps = _this$state2.addComps,
          visible = _this$state2.visible;
      return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_popconfirm["default"], {
        icon: null,
        placement: "topRight",
        title: this.renderContent(),
        onConfirm: this.handleConfirm,
        onCancel: this.handleCancel
      }, /*#__PURE__*/React.createElement(_button["default"], {
        className: "c-editCluster__componentButton",
        onClick: function onClick() {
          return _this2.props.handlePopVisible();
        }
      }, /*#__PURE__*/React.createElement("i", {
        className: "iconfont iconzujianpeizhi",
        style: {
          marginRight: 2
        }
      }), "\u7EC4\u4EF6\u914D\u7F6E")), /*#__PURE__*/React.createElement(_modifyModal["default"], {
        visible: visible,
        addComps: addComps,
        deleteComps: deleteComps,
        onCancel: this.handleCancel,
        onOk: function onOk() {
          _this2.setState({
            visible: false
          });

          _this2.props.handleConfirm(addComps, deleteComps);
        }
      }));
    }
  }]);
  return ComponentButton;
}(React.Component);

exports["default"] = ComponentButton;

/***/ }),

/***/ "vNIf":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("DQNa");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _configProvider = _interopRequireDefault(__webpack_require__("vgIT"));

var _extends2 = _interopRequireDefault(__webpack_require__("pVnL"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _reactRouter = __webpack_require__("dtw8");

var _reactRedux = __webpack_require__("/MKj");

var _zh_CN = _interopRequireDefault(__webpack_require__("+Gva"));

__webpack_require__("m2I1");

__webpack_require__("TVWj");

__webpack_require__("+5i3");

var _routers = _interopRequireDefault(__webpack_require__("Efuq"));

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

var Root = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(Root, _React$Component);

  var _super = _createSuper(Root);

  function Root() {
    (0, _classCallCheck2["default"])(this, Root);
    return _super.apply(this, arguments);
  }

  (0, _createClass2["default"])(Root, [{
    key: "render",
    value: function render() {
      var _this$props = this.props,
          store = _this$props.store,
          history = _this$props.history;
      return /*#__PURE__*/React.createElement(_configProvider["default"], {
        locale: _zh_CN["default"]
      }, /*#__PURE__*/React.createElement(_reactRedux.Provider, {
        store: store
      }, /*#__PURE__*/React.createElement(_reactRouter.Router, (0, _extends2["default"])({
        routes: _routers["default"],
        history: history,
        key: Math.random()
      }, {
        onEnter: function onEnter() {
          console.log('enter');
        }
      }))));
    }
  }]);
  return Root;
}(React.Component);

exports["default"] = Root;

/***/ }),

/***/ "zqmc":
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _interopRequireWildcard = __webpack_require__("284h");

var _interopRequireDefault = __webpack_require__("TqRt");

__webpack_require__("pNMO");

__webpack_require__("ma9I");

__webpack_require__("TeQF");

__webpack_require__("fbCW");

__webpack_require__("QWBl");

__webpack_require__("J30X");

__webpack_require__("2B1R");

__webpack_require__("E9XD");

__webpack_require__("DQNa");

__webpack_require__("wLYn");

__webpack_require__("zKZe");

__webpack_require__("HRxU");

__webpack_require__("eoL8");

__webpack_require__("5DmW");

__webpack_require__("27RR");

__webpack_require__("tkto");

__webpack_require__("07d7");

__webpack_require__("SuFq");

__webpack_require__("JfAA");

__webpack_require__("FZtP");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = exports.RedTxt = void 0;

var _card = _interopRequireDefault(__webpack_require__("N9UN"));

var _table = _interopRequireDefault(__webpack_require__("DtFj"));

var _button = _interopRequireDefault(__webpack_require__("4IMT"));

var _regenerator = _interopRequireDefault(__webpack_require__("o0o1"));

var _message2 = _interopRequireDefault(__webpack_require__("QpBz"));

__webpack_require__("ls82");

var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__("yXPU"));

var _modal = _interopRequireDefault(__webpack_require__("CC+v"));

var _classCallCheck2 = _interopRequireDefault(__webpack_require__("lwsE"));

var _createClass2 = _interopRequireDefault(__webpack_require__("W8MJ"));

var _assertThisInitialized2 = _interopRequireDefault(__webpack_require__("PJYZ"));

var _inherits2 = _interopRequireDefault(__webpack_require__("7W2i"));

var _possibleConstructorReturn2 = _interopRequireDefault(__webpack_require__("a1gu"));

var _getPrototypeOf2 = _interopRequireDefault(__webpack_require__("Nsbk"));

var _defineProperty2 = _interopRequireDefault(__webpack_require__("lSNA"));

var _taggedTemplateLiteral2 = _interopRequireDefault(__webpack_require__("VkAN"));

var _select = _interopRequireDefault(__webpack_require__("FAat"));

var React = _interopRequireWildcard(__webpack_require__("q1tI"));

var _styledComponents = _interopRequireDefault(__webpack_require__("9ObM"));

var _console = _interopRequireDefault(__webpack_require__("RHHV"));

__webpack_require__("+5i3");

var _index = __webpack_require__("RzPm");

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { (0, _defineProperty2["default"])(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _createSuper(Derived) { var hasNativeReflectConstruct = _isNativeReflectConstruct(); return function _createSuperInternal() { var Super = (0, _getPrototypeOf2["default"])(Derived), result; if (hasNativeReflectConstruct) { var NewTarget = (0, _getPrototypeOf2["default"])(this).constructor; result = Reflect.construct(Super, arguments, NewTarget); } else { result = Super.apply(this, arguments); } return (0, _possibleConstructorReturn2["default"])(this, result); }; }

function _isNativeReflectConstruct() { if (typeof Reflect === "undefined" || !Reflect.construct) return false; if (Reflect.construct.sham) return false; if (typeof Proxy === "function") return true; try { Date.prototype.toString.call(Reflect.construct(Date, [], function () {})); return true; } catch (e) { return false; } }

function _templateObject() {
  var data = (0, _taggedTemplateLiteral2["default"])(["\n    color: #FF5F5C;\n"]);

  _templateObject = function _templateObject() {
    return data;
  };

  return data;
}

// import Resource from '../../components/resource';
var Option = _select["default"].Option;

var RedTxt = _styledComponents["default"].span(_templateObject());

exports.RedTxt = RedTxt;

var QueueManage = /*#__PURE__*/function (_React$Component) {
  (0, _inherits2["default"])(QueueManage, _React$Component);

  var _super = _createSuper(QueueManage);

  function QueueManage() {
    var _this;

    (0, _classCallCheck2["default"])(this, QueueManage);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _this = _super.call.apply(_super, [this].concat(args));
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "state", {
      dataSource: [],
      table: {
        loading: false
      },
      clusterList: [],
      clusterId: undefined,
      clusterMap: {},
      nodeList: [],
      // 节点值
      node: undefined,
      // 剩余资源
      isShowResource: false,
      editModalKey: null
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "onTableChange", function (pagination, filters, sorter) {
      var table = Object.assign(_this.state.table, {
        pageIndex: pagination.current
      });

      _this.setState({
        table: table
      }, function () {
        _this.getClusterDetail();
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "handleKillAll", function (e) {
      _this.setState({
        isShowAllKill: true,
        isKillAllTasks: true
      });
    });
    (0, _defineProperty2["default"])((0, _assertThisInitialized2["default"])(_this), "getClusterItem", function (clusterId) {
      var _this$state$clusterLi = _this.state.clusterList,
          clusterList = _this$state$clusterLi === void 0 ? [] : _this$state$clusterLi;
      return clusterList.find(function (cluster) {
        return cluster.id === clusterId;
      });
    });
    return _this;
  }

  (0, _createClass2["default"])(QueueManage, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      this.getClusterSelect();
    } // 渲染集群

  }, {
    key: "getClusterDetail",
    value: function getClusterDetail() {
      var _this2 = this;

      var _this$state = this.state,
          table = _this$state.table,
          node = _this$state.node;
      var clusterId = this.state.clusterId;
      var cluster = this.getClusterItem(clusterId);

      if (cluster) {
        this.setState({
          table: _objectSpread(_objectSpread({}, table), {}, {
            loading: true
          })
        });

        _console["default"].getClusterDetail({
          clusterName: cluster.clusterName,
          nodeAddress: node
        }).then(function (res) {
          if (res.code == 1) {
            var data = res.data;

            _this2.setState({
              dataSource: data || [],
              table: _objectSpread(_objectSpread({}, table), {}, {
                loading: false
              })
            });
          } else {
            _this2.setState({
              table: _objectSpread(_objectSpread({}, table), {}, {
                loading: false
              })
            });
          }
        });
      }
    } // 获取集群下拉数据

  }, {
    key: "getClusterSelect",
    value: function getClusterSelect() {
      var _this3 = this;

      return _console["default"].getAllCluster().then(function (res) {
        if (res.code == 1) {
          var data = res.data;
          var clusterMap = {};
          var clusterList = [];

          if (Array.isArray(data)) {
            clusterMap = data.reduce(function (pre, curr) {
              return _objectSpread(_objectSpread({}, pre), {}, (0, _defineProperty2["default"])({}, curr.id, curr.clusterName));
            }, {});
            clusterList = data.map(function (item) {
              return _objectSpread(_objectSpread({}, item), {}, {
                id: item.id + ''
              });
            });
          }

          _this3.setState({
            clusterList: clusterList,
            clusterMap: clusterMap,
            clusterId: data && data[0] && data[0].id + '' // 首次取第一项集群名称展示

          }, function () {
            _this3.getNodeAddressSelect();
          });
        }
      });
    } // 获取集群下拉视图

  }, {
    key: "getClusterOptionView",
    value: function getClusterOptionView() {
      var clusterList = this.state.clusterList;
      return clusterList.map(function (item, index) {
        return /*#__PURE__*/React.createElement(Option, {
          key: item.id,
          value: "".concat(item.id),
          "data-item": item
        }, item.clusterName);
      });
    } // 集群option改变

  }, {
    key: "clusterOptionChange",
    value: function clusterOptionChange(clusterId) {
      this.setState({
        clusterId: clusterId
      }, this.getClusterDetail);
    } // 获取节点下拉数据

  }, {
    key: "getNodeAddressSelect",
    value: function getNodeAddressSelect() {
      var _this4 = this;

      return _console["default"].getNodeAddressSelect().then(function (res) {
        if (res.code == 1) {
          var data = res.data;

          _this4.setState({
            nodeList: data || []
          }, _this4.getClusterDetail.bind(_this4));
        }
      });
    } // 获取节点下拉视图

  }, {
    key: "getNodeAddressOptionView",
    value: function getNodeAddressOptionView() {
      var nodeList = this.state.nodeList;
      return nodeList.map(function (item, index) {
        return /*#__PURE__*/React.createElement(Option, {
          key: index,
          value: item
        }, item);
      });
    } // 节点option改变

  }, {
    key: "nodeAddressOptionChange",
    value: function nodeAddressOptionChange(value) {
      this.setState({
        node: value
      }, this.getClusterDetail.bind(this));
    } // 表格换页

  }, {
    key: "handleClickResource",
    // 剩余资源
    value: function handleClickResource() {
      this.setState({
        isShowResource: true,
        editModalKey: Math.random()
      });
    }
  }, {
    key: "handleCloseResource",
    value: function handleCloseResource() {
      this.setState({
        isShowResource: false
      });
    } // 查看明细(需要传入参数 集群,引擎,group) detailInfo

  }, {
    key: "viewDetails",
    value: function viewDetails(record, jobStage) {
      var _this$state2 = this.state,
          clusterId = _this$state2.clusterId,
          node = _this$state2.node;
      var cluster = this.getClusterItem(clusterId);
      this.props.router.push({
        pathname: '/console/queueManage/detail',
        query: {
          node: node,
          jobStage: jobStage,
          clusterName: cluster.clusterName,
          engineType: record.engineType,
          jobResource: record.jobResource
        }
      });
    }
  }, {
    key: "onKillAllTask",
    value: function onKillAllTask(record) {
      var node = this.state.node;
      var ctx = this;

      _modal["default"].confirm({
        title: '杀死全部',
        okText: '杀死全部',
        okType: 'danger',
        cancelText: '取消',
        width: '460px',
        iconType: 'close-circle',
        content: /*#__PURE__*/React.createElement("div", {
          style: {
            fontSize: '14px'
          }
        }, /*#__PURE__*/React.createElement("p", null, "\u6740\u6B7B\u6240\u6709", /*#__PURE__*/React.createElement(RedTxt, null, "\u961F\u5217\u4E2D\u3001\u5DF2\u5B58\u50A8\u3001\u7B49\u5F85\u91CD\u8BD5\u3001\u7B49\u5F85\u8D44\u6E90"), "\u7684\u4EFB\u52A1"), /*#__PURE__*/React.createElement("p", null, /*#__PURE__*/React.createElement(RedTxt, null, "\u8FD0\u884C\u4E2D\u7684\u4EFB\u52A1\u4E0D\u4F1A\u88AB\u6740\u6B7B"), "\uFF0C\u53EF\u70B9\u51FB\u8FD0\u884C\u4E2D\u7684\u4EFB\u52A1\uFF0C\u5E76\u6267\u884C\u6279\u91CF\u6740\u6B7B\u64CD\u4F5C")),
        onOk: function onOk() {
          return (0, _asyncToGenerator2["default"])( /*#__PURE__*/_regenerator["default"].mark(function _callee() {
            var res;
            return _regenerator["default"].wrap(function _callee$(_context) {
              while (1) {
                switch (_context.prev = _context.next) {
                  case 0:
                    _context.next = 2;
                    return _console["default"].killAllTask({
                      jobResource: record.jobResource,
                      nodeAddress: node
                    });

                  case 2:
                    res = _context.sent;

                    if (res.code === 1) {
                      _message2["default"].success('杀死全部成功！');

                      ctx.getClusterDetail();
                    }

                  case 4:
                  case "end":
                    return _context.stop();
                }
              }
            }, _callee);
          }))();
        },
        onCancel: function onCancel() {
          console.log('Cancel');
        }
      });
    }
  }, {
    key: "initTableColumns",
    value: function initTableColumns() {
      var _this5 = this;

      var colText = function colText(text, record, jobStage) {
        return /*#__PURE__*/React.createElement("a", {
          onClick: _this5.viewDetails.bind(_this5, record, jobStage)
        }, text || 0);
      };

      return [{
        title: '计算类型',
        dataIndex: 'jobResource',
        render: function render(text, record) {
          return colText(text, record, _index.JobStage.Queueing);
        }
      }, {
        title: '队列中(等待时长)',
        width: 220,
        dataIndex: 'priorityJobSize',
        render: function render(text, record) {
          var txt = text + (record.priorityWaitTime ? " (".concat(record.priorityWaitTime, ")") : '');
          return colText(txt, record, _index.JobStage.Queueing);
        }
      }, {
        title: '已存储',
        dataIndex: 'dbJobSize',
        render: function render(text, record) {
          return colText(text, record, _index.JobStage.Saved);
        }
      }, {
        title: '等待重试',
        dataIndex: 'restartJobSize',
        render: function render(text, record) {
          return colText(text, record, _index.JobStage.WaitTry);
        }
      }, {
        title: '等待资源',
        dataIndex: 'lackingJobSize',
        render: function render(text, record) {
          return colText(text, record, _index.JobStage.WaitResource);
        }
      }, {
        title: '运行中',
        dataIndex: 'submittedJobSize',
        render: function render(text, record) {
          return colText(text, record, _index.JobStage.Running);
        }
      }, {
        title: '操作',
        dataIndex: 'deal',
        render: function render(text, record) {
          return /*#__PURE__*/React.createElement("div", null, /*#__PURE__*/React.createElement("a", {
            onClick: _this5.onKillAllTask.bind(_this5, record)
          }, "\u6740\u6B7B\u5168\u90E8"));
        }
      }];
    }
  }, {
    key: "render",
    value: function render() {
      var columns = this.initTableColumns();
      var _this$state3 = this.state,
          dataSource = _this$state3.dataSource,
          table = _this$state3.table,
          clusterId = _this$state3.clusterId,
          node = _this$state3.node;
      var loading = table.loading;
      return /*#__PURE__*/React.createElement("div", {
        className: " api-mine nobackground m-card height-auto",
        style: {
          marginTop: '20px'
        }
      }, /*#__PURE__*/React.createElement("div", {
        style: {
          margin: '20px'
        }
      }, "\u96C6\u7FA4\uFF1A", /*#__PURE__*/React.createElement(_select["default"], {
        className: "dt-form-shadow-bg",
        style: {
          width: 150,
          marginRight: '10px'
        },
        placeholder: "\u9009\u62E9\u96C6\u7FA4",
        onChange: this.clusterOptionChange.bind(this),
        value: clusterId
      }, this.getClusterOptionView()), "\u8282\u70B9\uFF1A", /*#__PURE__*/React.createElement(_select["default"], {
        className: "dt-form-shadow-bg",
        style: {
          width: 150
        },
        placeholder: "\u9009\u62E9\u8282\u70B9",
        allowClear: true,
        onChange: this.nodeAddressOptionChange.bind(this),
        value: node
      }, this.getNodeAddressOptionView()), /*#__PURE__*/React.createElement("div", {
        style: {
          "float": 'right'
        }
      }, /*#__PURE__*/React.createElement(_button["default"], {
        size: "large",
        style: {
          marginLeft: '8px'
        },
        onClick: this.getClusterDetail.bind(this)
      }, "\u5237\u65B0"))), /*#__PURE__*/React.createElement(_card["default"], {
        style: {
          marginTop: '0px'
        },
        className: "box-1",
        hoverable: true
      }, /*#__PURE__*/React.createElement(_table["default"], {
        rowKey: function rowKey(record) {
          return record.clusterId;
        },
        className: "dt-table-border",
        loading: loading,
        columns: columns,
        dataSource: dataSource,
        onChange: this.onTableChange
      })));
    }
  }]);
  return QueueManage;
}(React.Component);

var _default = QueueManage;
exports["default"] = _default;

/***/ })

},[["XTUZ",4,2,5]]]);
});