# 数栈配置文件说明

#### 公有配置（所有应用都可用的配置）

| 参数名  | 必填  | 类型  |  默认值 | 备注  |
| ------------ | ------------ | ------------ | ------------ | ------------ |
|  UIC_URL |  是 | string  | 无  | UIC 中心地址  |
|  UIC_DOMAIN | 是  | string  |  无 |  UIC 域，注意，是**域**，例如 ```.dtstack.com``` ，**不是 url** |
| prefix  | 否  |  string | 无  |  应用的前缀，例如 `DTinsight.离线计算` 中的`DTinsight` |
| name  |  否 | string  | 无  | 应用名字，在左上角显示，例如 `DTinsight.离线计算` 中的 `离线计算` 。**注意：门户页面没有 name**  |
| titleName  | 否  |  string | 无  | 网页的 title 中显示的名字，例如`DTinsight-离线计算`中的 `离线计算`  |
| loadingTitle  |  否 |  string | 无  | loading 页面显示的名字，和 titleName 类似 |
| theme  |  否 | 'default' / 'aliyun'  | 'default'  | 应用的主题，将会改变门户页面的布局样式以及应用的导航栏。当前只有 `aliyun` 和 `default` 两种可选  |
| hideUserCenter |  否 | boolean  | false  | 是否隐藏右上角下拉框中的用户中心  |
| disableHelp  | 否  |  boolean | false  | 是否隐藏帮助文档  |
| macChrome | 否 | string | 无 | mac 版`Chrome浏览器`下载地址 |
| windowsChrome | 否 | string | 无 | windows 版`Chrome浏览器`下载地址 |

#### 各应用特殊配置

##### main
| 参数名  | 必填  | 类型  |  默认值 | 备注  |
| ------------ | ------------ | ------------ | ------------ | ------------ |
| indexTitle  |  是 | string  | 无  |  门户页面的大标题 |
| indexDesc  | 是  |  string | 无  | 门户页面大标题下面的小说明  |
| showCopyright | 否  |  boolean | false  | 是否显示版权信息  |
| showSummary | 否  | boolean  |  false | 是否显示功能说明  |
| summary | 否  |  { title: string, content: string } |  无 |  功能说明详细内容 |

##### dataQuality
| 参数名  | 必填  | 类型  |  默认值 | 备注  |
| ------------ | ------------ | ------------ | ------------ | ------------ |
| API_SERVER  |  是 | string  | 无  |  API 服务 Base 地址 |
