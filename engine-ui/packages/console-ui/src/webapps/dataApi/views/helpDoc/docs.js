// 帮助文档
import React from 'react'

export const docExample = (
    <div>
        docExample.
    </div>
)
export const securityTip = (
    <div>
        <p>每个安全组是一组IP地址，用于限制此API在这些IP范围内调用或禁止调用</p>
        <p>如果您未建立任何安全组，可进入安全组进行新建</p>
    </div>
)
export const apiPathInfo = (
    <div>
        <p>API path 即 API 调用路径。若填写，生成的 API 调用 URL 中将包含 API path，可帮助用户快速识别调用的 API。</p>
    </div>
)
export const registerApiHost = (
    <div>
        <p>填写后台域名，支持HTTP、HTTPS协议</p>
    </div>
)
export const registerApiPath = (
    <div>
        <p>填写后端服务路径，支持英文，数字，下划线，连字符(-)，限制2—200个字符，只能 / 开头。</p>
        <p>若后端服务path中包含请求参数中的入参，放在[]中，如/user/[userid]</p>
    </div>
)
