```
@hd: 1px; // 基本单位

// 支付宝钱包默认主题
// https://github.com/ant-design/ant-design-mobile/wiki/设计变量表及命名规范

// 色彩
// ---
// 文字色
@color-text-base: #000;                  // 基本
@color-text-base-inverse: #fff;          // 基本 - 反色
@color-text-secondary: #a4a9b0;          // 辅助色
@color-text-placeholder: #bbb;           // 文本框提示
@color-text-disabled: #bbb;              // 失效
@color-text-caption: #888;               // 辅助描述
@color-text-paragraph: #333;             // 段落
@color-link: @brand-primary;             // 链接

// 背景色
@fill-base: #fff;                           // 组件默认背景
@fill-body: #f5f5f9;                        // 页面背景
@fill-tap: #ddd;                            // 组件默认背景 - 按下
@fill-disabled: #ddd;                       // 通用失效背景
@fill-mask: rgba(0, 0, 0, 0.4);              // 遮罩背景
@color-icon-base: #ccc;                      // 许多小图标的背景，比如一些小圆点，加减号
@fill-grey: #f7f7f7;

// 透明度
@opacity-disabled: 0.3;   // switch checkbox radio 等组件禁用的透明度

// 全局/品牌色
@brand-primary: #108ee9;
@brand-primary-tap: #0e80d2;
@brand-success: #6abf47;
@brand-warning: #ffc600;
@brand-error: #f4333c;
@brand-important: #ff5b05;  // 用于小红点
@brand-wait: #108ee9;

// 边框色
@border-color-base: #ddd;

// 字体尺寸
// ---
@font-size-icontext: 10 * @hd;
@font-size-caption-sm: 12 * @hd;
@font-size-base: 14 * @hd;
@font-size-subhead: 15 * @hd;
@font-size-caption: 16 * @hd;
@font-size-heading: 17 * @hd;

// 圆角
// ---
@radius-xs: 2 * @hd;
@radius-sm: 3 * @hd;
@radius-md: 5 * @hd;
@radius-lg: 7 * @hd;
@radius-circle: 50%;

// 边框尺寸
// ---
@border-width-sm: 1PX;
@border-width-md: 1PX;
@border-width-lg: 2 * @hd;

// 间距
// ---
// 水平间距
@h-spacing-sm: 5 * @hd;
@h-spacing-md: 8 * @hd;
@h-spacing-lg: 15 * @hd;

// 垂直间距
@v-spacing-xs: 3 * @hd;
@v-spacing-sm: 6 * @hd;
@v-spacing-md: 9 * @hd;
@v-spacing-lg: 15 * @hd;
@v-spacing-xl: 21 * @hd;

// 高度
// ---
@line-height-base: 1;           // 单行行高
@line-height-paragraph: 1.5;    // 多行行高

// 图标尺寸
// ---
@icon-size-xxs: 15 * @hd;
@icon-size-xs: 18 * @hd;
@icon-size-sm: 21 * @hd;
@icon-size-md: 22 * @hd;       // 导航条上的图标、grid的图标大小
@icon-size-lg: 36 * @hd;

// 动画缓动
// ---
@ease-in-out-quint: cubic-bezier(.86, 0, .07, 1);

// 组件变量
// ---

@actionsheet-item-height: 50 * @hd;
@actionsheet-item-font-size: 18 * @hd;

// button
@button-height: 47 * @hd;
@button-font-size: 18 * @hd;

@button-height-sm: 30 * @hd;
@button-font-size-sm: 13 * @hd;

@primary-button-fill: @brand-primary;
@primary-button-fill-tap: @brand-primary-tap;

@ghost-button-color: @brand-primary;    // 同时应用于背景、文字颜色、边框色
@ghost-button-fill-tap: fade(@brand-primary, 60%);

@warning-button-fill: #e94f4f;
@warning-button-fill-tap: #d24747;

@link-button-fill-tap: #ddd;
@link-button-font-size: 16 * @hd;

// modal
@modal-font-size-heading: 18 * @hd;
@modal-button-font-size: 18 * @hd; // 按钮字号
@modal-button-height: 50 * @hd; // 按钮高度

// list
@list-title-height: 30 * @hd;
@list-item-height-sm: 35 * @hd;
@list-item-height: 44 * @hd;

// input
@input-label-width: 17 * @hd;       // InputItem、TextareaItem 文字长度基础值
@input-font-size: 17 * @hd;
@input-color-icon: #ccc; // input clear icon 的背景色
@input-color-icon-tap: @brand-primary;

// tabs
@tabs-color: @brand-primary;
@tabs-height: 43.5 * @hd;
@tabs-font-size-heading: 15 * @hd;
@tabs-ink-bar-height: @border-width-lg;

// segmented-control
@segmented-control-color: @brand-primary;  // 同时应用于背景、文字颜色、边框色
@segmented-control-height: 27 * @hd;
@segmented-control-fill-tap: fade(@brand-primary, 0.1);

// tab-bar
@tab-bar-fill: #ebeeef;
@tab-bar-height: 50 * @hd;

// toast
@toast-fill: rgba(58, 58, 58, 0.9); // toast, activity-indicator 的背景颜色

// search-bar
@search-bar-fill: #efeff4;
@search-bar-height: 44 * @hd;
@search-bar-input-height: 28 * @hd;
@search-bar-font-size: 15 * @hd;
@search-color-icon: #bbb; // input search icon 的背景色

// notice-bar
@notice-bar-fill: #fefcec;
@notice-bar-height: 36 * @hd;
@notice-bar-color: #f76a24;

// switch
@switch-fill: #4dd865;
@switch-fill-android: @brand-primary;

// tag
@tag-height: 25 * @hd;
@tag-height-sm: 15 * @hd;
@tag-color: @brand-primary;

// keyboard
@keyboard-confirm-color: @brand-primary;
@keyboard-confirm-tap-color: @brand-primary-tap;

// picker
@option-height: 42 * @hd;           // picker 标题的高度

// z-index
@progress-zindex: 2000;
@popover-zindex: 1999;
@toast-zindex: 1999;
@action-sheet-zindex: 1000; // actonsheet 会放到 popup / modal 中
@picker-zindex: 1000;
@popup-zindex: 999;
@modal-zindex: 999; // modal.alert 应该最大，其他应该较小
@tabs-pagination-zindex: 999;

```