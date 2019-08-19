// 帮助文档
import * as React from 'react'

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
export const registerApiPathInfo = (
    <div>
        <p>API path即API调用路径。若填写，生成的API调用URL中将包含API path，可帮助用户快速识别调用的API。</p>
        <p>支持英文，数字，下划线，连字符(-)，限制2—200个字符，只能 / 开头，如/user。</p>
        <p>若API path中包含请求参数中的入参，放在{'{}'}中，如/user/{'{'}userid{'}'}，且API path中的入参名需与后端服务path中的保持一致。</p>
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
        <p>若后端服务path中包含请求参数中的入参，放在{'{}'}中，如/user/{'{'}userid{'}'}。</p>
    </div>
)

export const tokenSpecification = (
    <div>
        <p>
            API的调用方式自 v3.6.0 版本后，已修改为AK/SK的加密调用方式，
            但是仍可兼容之前版本的API-TOKEN调用方式，不影响老API的使用。
            对于老API, 用户可继续使用原有Token加密方式，也可切换至AK/SK的加密调用方式，
            但是建议用户使用API签名的方法，可更好的保障数据的安全性。
        </p>
    </div>
)
