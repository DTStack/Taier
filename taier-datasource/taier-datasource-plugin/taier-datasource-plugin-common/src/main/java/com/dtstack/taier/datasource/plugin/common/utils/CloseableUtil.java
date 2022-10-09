package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.plugin.common.function.SingleParamFunc;
import com.dtstack.taier.datasource.api.exception.SourceException;

import java.util.function.Supplier;

/**
 * 针对 AutoCloseable 接口实现类进行操作的工具类
 *
 * @author ：wangchuan
 * date：Created in 下午5:04 2022/3/16
 * company: www.dtstack.com
 */
public class CloseableUtil {

    /**
     * 执行方法并调用 close 关闭 obj
     *
     * @param autoCloseSupplier 获取 autoClose 接口实现类的方法, 只有在调用
     *                          {@link java.util.function.Supplier#get()} 方法时才会进行获取</p>
     * @param callbackFunc      方法执行回调
     * @param <T>               返回值的范型
     * @return 方法执行结果
     */
    public static <T, M extends AutoCloseable> T executeAndClose(Supplier<M> autoCloseSupplier,
                                                                 SingleParamFunc<T, M> callbackFunc) {
        try (M closeableObj = autoCloseSupplier.get()) {
            return callbackFunc.execute(closeableObj);
        } catch (Exception e) {
            throw new SourceException(String.format("execute method error : %s", e.getMessage()), e);
        }
    }
}
