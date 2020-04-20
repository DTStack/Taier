package com.dtstack.engine.master.router.vertx;

import com.dtstack.dtcenter.common.hadoop.IDownload;
import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.master.router.callback.ApiCallback;
import com.dtstack.engine.master.router.callback.ApiCallbackMethod;
import com.dtstack.engine.master.router.util.RequestUtil;
import com.google.common.collect.Maps;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.impl.BufferFactoryImpl;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author toutian
 */
public class ResourceVerticle extends BaseVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ResourceVerticle.class);

    private static final String DOWN_FILE_TYPE_XML = ".xml";

    private static final String DOWNLOAD_FILE_NAME_PREFIX = "dtstack_ide_";

    //添加bom头信息
    private static byte[] bomByte = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};


    public ResourceVerticle(ApplicationContext context) {
        this.context = context;
    }

    public void handleDownloadXml(RoutingContext routingContext){
        this.handleDownload(routingContext, DOWN_FILE_TYPE_XML);
    }


    /**
     * 处理分批次导出文件
     *
     * @param routingContext
     */
    public void handleDownload(RoutingContext routingContext, String suf) {

        String downFileName = DOWNLOAD_FILE_NAME_PREFIX + UUID.randomUUID().toString() + suf;
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/octet-stream;charset=UTF-8");
        response.putHeader("Pragma", "no-cache");
        response.putHeader("Cache-Control", "no-cache");
        response.setChunked(true);
        response.write(new BufferFactoryImpl().buffer(bomByte));

        IDownload downloadInvoke = null;
        Map<String, Object> paramMap = Maps.newHashMap();
        MultiMap reqParam = routingContext.request().params();
        reqParam.forEach(entry -> paramMap.put(entry.getKey(), entry.getValue()));
        try {
            Map<String, Object> params = RequestUtil.getRequestParams(paramMap, routingContext);
            downloadInvoke = (IDownload) reflectionMethod(routingContext, params);
            downFileName = downloadInvoke.getFileName();
            response.putHeader("Content-Disposition", "attachment;filename=" + downFileName);
            if (downloadInvoke == null) {
                logger.error("fail to download,downloadInvoke == null");
                response.write("文件正在生成，请稍后重试");
            } else {
                while (!downloadInvoke.reachedEnd()) {
                    Object row = downloadInvoke.readNext();
                    response.write(row.toString());
                }
            }
        } catch (InvocationTargetException e) {
            response.write("下载文件异常:" + e.getTargetException().getMessage());
        } catch (Exception e) {
            logger.error("", e);
            response.write("下载文件异常:" + e.getMessage());
        } finally {
            if (downloadInvoke != null) {
                try {
                    downloadInvoke.close();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }

        routingContext.response().end();
    }

    public void handleUploadResource(RoutingContext routingContext) {

        final ResourceVerticle resourceVerticle = this;
        Set<FileUpload> fileUploadSet = routingContext.fileUploads();

        List<Resource> resources = new ArrayList<>(fileUploadSet.size());
        for (FileUpload fileUpload : fileUploadSet) {
            resources.add(new Resource(fileUpload.fileName(), fileUpload.uploadedFileName(), (int) fileUpload.size(), fileUpload.contentType(), fileUpload.name()));
        }

        ApiCallbackMethod.doCallback(new ApiCallback() {

            @Override
            public Object execute() throws Exception {
                Map<String, Object> paramMap = new HashedMap();
                for (Map.Entry<String, String> tmp : routingContext.request().params()) {
                    paramMap.put(tmp.getKey(), tmp.getValue());
                }

                paramMap.put("resources", resources);

                return resourceVerticle.reflectionMethod(routingContext, RequestUtil.getRequestParams(paramMap, routingContext));
            }

        }, routingContext);
    }

}


