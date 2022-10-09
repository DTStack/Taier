package com.dtstack.taier.datasource.api.client;

import com.dtstack.taier.datasource.api.base.Client;
import com.dtstack.taier.datasource.api.dto.restful.Response;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;

import java.io.File;
import java.util.Map;

/**
 * <p>提供 Restful 相关操作方法</p>
 *
 * @author ：wangchuan
 * date：Created in 上午10:06 2021/8/9
 * company: www.dtstack.com
 */
public interface IRestful extends Client {

    /**
     * get 请求
     *
     * @param source  数据源信息
     * @param params  请求参数
     * @param cookies cookie 信息
     * @param headers header 信息
     * @return 相应
     */
    Response get(ISourceDTO source, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers);

    /**
     * post 请求
     *
     * @param source   数据源信息
     * @param bodyData 请求参数
     * @param cookies  cookie 信息
     * @param headers  header 信息
     * @return 相应
     */
    Response post(ISourceDTO source, String bodyData, Map<String, String> cookies, Map<String, String> headers);

    /**
     * delete 请求
     *
     * @param source   数据源信息
     * @param bodyData 请求参数
     * @param cookies  cookie 信息
     * @param headers  header 信息
     * @return 相应
     */
    Response delete(ISourceDTO source, String bodyData, Map<String, String> cookies, Map<String, String> headers);

    /**
     * put 请求
     *
     * @param source   数据源信息
     * @param bodyData body 信息
     * @param cookies  cookie 信息
     * @param headers  header 信息
     * @return 相应
     */
    Response put(ISourceDTO source, String bodyData, Map<String, String> cookies, Map<String, String> headers);

    /**
     * put Multipart
     *
     * @param source  数据源信息
     * @param params  请求参数
     * @param cookies cookie 信息
     * @param headers header 信息
     * @param files   文件信息
     * @return 相应
     */
    Response postMultipart(ISourceDTO source, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers, Map<String, File> files);
}
