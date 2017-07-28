package com.dtstack.rdos.engine.execution.flink130.sink.batch.elasticsearch;

import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkBase;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch.util.ElasticsearchUtils;
import org.apache.flink.types.Row;
import org.apache.flink.util.InstantiationUtil;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.Netty3Plugin;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.flink.util.Preconditions.checkArgument;
import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * dataset elastic 写入es插件
 * Date: 2017/7/18
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class Elastic5OutputFormat extends RichOutputFormat<Row> {

    private static final long serialVersionUID = -1007596293618451942L;

    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchSinkBase.class);

    // ------------------------------------------------------------------------
    //  Internal bulk processor configuration
    // ------------------------------------------------------------------------

    public static final String CONFIG_KEY_BULK_FLUSH_MAX_ACTIONS = "bulk.flush.max.actions";
    public static final String CONFIG_KEY_BULK_FLUSH_MAX_SIZE_MB = "bulk.flush.max.size.mb";
    public static final String CONFIG_KEY_BULK_FLUSH_INTERVAL_MS = "bulk.flush.interval.ms";
    public static final String CONFIG_KEY_BULK_FLUSH_BACKOFF_ENABLE = "bulk.flush.backoff.enable";
    public static final String CONFIG_KEY_BULK_FLUSH_BACKOFF_TYPE = "bulk.flush.backoff.type";
    public static final String CONFIG_KEY_BULK_FLUSH_BACKOFF_RETRIES = "bulk.flush.backoff.retries";
    public static final String CONFIG_KEY_BULK_FLUSH_BACKOFF_DELAY = "bulk.flush.backoff.delay";

    public enum FlushBackoffType {
        CONSTANT,
        EXPONENTIAL
    }

    public class BulkFlushBackoffPolicy implements Serializable {

        private static final long serialVersionUID = -6022851996101826049L;

        // the default values follow the Elasticsearch default settings for BulkProcessor
        private ElasticsearchSinkBase.FlushBackoffType backoffType = ElasticsearchSinkBase.FlushBackoffType.EXPONENTIAL;
        private int maxRetryCount = 8;
        private long delayMillis = 50;

        public ElasticsearchSinkBase.FlushBackoffType getBackoffType() {
            return backoffType;
        }

        public int getMaxRetryCount() {
            return maxRetryCount;
        }

        public long getDelayMillis() {
            return delayMillis;
        }

        public void setBackoffType(ElasticsearchSinkBase.FlushBackoffType backoffType) {
            this.backoffType = checkNotNull(backoffType);
        }

        public void setMaxRetryCount(int maxRetryCount) {
            checkArgument(maxRetryCount >= 0);
            this.maxRetryCount = maxRetryCount;
        }

        public void setDelayMillis(long delayMillis) {
            checkArgument(delayMillis >= 0);
            this.delayMillis = delayMillis;
        }
    }

    private final Integer bulkProcessorFlushMaxActions;
    private final Integer bulkProcessorFlushMaxSizeMb;
    private final Integer bulkProcessorFlushIntervalMillis;
    private final BulkFlushBackoffPolicy bulkProcessorFlushBackoffPolicy;
    private final List<InetSocketAddress> transportAddresses;


    // ------------------------------------------------------------------------
    //  User-facing API and configuration
    // ------------------------------------------------------------------------

    /** The user specified config map that we forward to Elasticsearch when we create the {@link Client}. */
    private final Map<String, String> userConfig;

    /** The function that is used to construct mulitple {@link ActionRequest ActionRequests} from each incoming element. */
    private final ElasticsearchOutputFunction<Row> elasticsearchOutputFunction;

    /** User-provided handler for failed {@link ActionRequest ActionRequests}. */
    private final ActionRequestFailureHandler failureHandler;

    /** Provided to the user via the {@link ElasticsearchOutputFunction} to add {@link ActionRequest ActionRequests}. */
    private transient BulkProcessorIndexer requestIndexer;

    /** Elasticsearch client created using the call bridge. */
    private transient Client client;

    /** Bulk processor to buffer and send requests to Elasticsearch, created using the client. */
    private transient BulkProcessor bulkProcessor;

    /**
     * This is set from inside the {@link BulkProcessor.Listener} if a {@link Throwable} was thrown in callbacks and
     * the user considered it should fail the sink via the
     * {@link ActionRequestFailureHandler#onFailure(ActionRequest, Throwable, int, RequestIndexer)} method.
     *
     * Errors will be checked and rethrown before processing each input element, and when the sink is closed.
     */
    private final AtomicReference<Throwable> failureThrowable = new AtomicReference<>();


    public Elastic5OutputFormat(
            Map<String, String> userConfig,
            List<InetSocketAddress> transportAddresses,
            ElasticsearchOutputFunction<Row> elasticsearchOutputFunction,
            ActionRequestFailureHandler failureHandler) {

        this.elasticsearchOutputFunction = checkNotNull(elasticsearchOutputFunction);
        this.failureHandler = checkNotNull(failureHandler);
        this.transportAddresses = checkNotNull(transportAddresses);

        // we eagerly check if the user-provided sink function and failure handler is serializable;
        // otherwise, if they aren't serializable, users will merely get a non-informative error message
        // "ElasticsearchSinkBase is not serializable"

        checkArgument(InstantiationUtil.isSerializable(elasticsearchOutputFunction),
                "The implementation of the provided ElasticsearchOutputFunction is not serializable. " +
                        "The object probably contains or references non-serializable fields.");

        checkArgument(InstantiationUtil.isSerializable(failureHandler),
                "The implementation of the provided ActionRequestFailureHandler is not serializable. " +
                        "The object probably contains or references non-serializable fields.");

        // extract and remove bulk processor related configuration from the user-provided config,
        // so that the resulting user config only contains configuration related to the Elasticsearch client.

        checkNotNull(userConfig);

        ParameterTool params = ParameterTool.fromMap(userConfig);

        if (params.has(CONFIG_KEY_BULK_FLUSH_MAX_ACTIONS)) {
            bulkProcessorFlushMaxActions = params.getInt(CONFIG_KEY_BULK_FLUSH_MAX_ACTIONS);
            userConfig.remove(CONFIG_KEY_BULK_FLUSH_MAX_ACTIONS);
        } else {
            bulkProcessorFlushMaxActions = null;
        }

        if (params.has(CONFIG_KEY_BULK_FLUSH_MAX_SIZE_MB)) {
            bulkProcessorFlushMaxSizeMb = params.getInt(CONFIG_KEY_BULK_FLUSH_MAX_SIZE_MB);
            userConfig.remove(CONFIG_KEY_BULK_FLUSH_MAX_SIZE_MB);
        } else {
            bulkProcessorFlushMaxSizeMb = null;
        }

        if (params.has(CONFIG_KEY_BULK_FLUSH_INTERVAL_MS)) {
            bulkProcessorFlushIntervalMillis = params.getInt(CONFIG_KEY_BULK_FLUSH_INTERVAL_MS);
            userConfig.remove(CONFIG_KEY_BULK_FLUSH_INTERVAL_MS);
        } else {
            bulkProcessorFlushIntervalMillis = null;
        }

        boolean bulkProcessorFlushBackoffEnable = params.getBoolean(CONFIG_KEY_BULK_FLUSH_BACKOFF_ENABLE, true);
        userConfig.remove(CONFIG_KEY_BULK_FLUSH_BACKOFF_ENABLE);

        if (bulkProcessorFlushBackoffEnable) {
            this.bulkProcessorFlushBackoffPolicy = new BulkFlushBackoffPolicy();

            if (params.has(CONFIG_KEY_BULK_FLUSH_BACKOFF_TYPE)) {
                bulkProcessorFlushBackoffPolicy.setBackoffType(ElasticsearchSinkBase.FlushBackoffType.valueOf(params.get(CONFIG_KEY_BULK_FLUSH_BACKOFF_TYPE)));
                userConfig.remove(CONFIG_KEY_BULK_FLUSH_BACKOFF_TYPE);
            }

            if (params.has(CONFIG_KEY_BULK_FLUSH_BACKOFF_RETRIES)) {
                bulkProcessorFlushBackoffPolicy.setMaxRetryCount(params.getInt(CONFIG_KEY_BULK_FLUSH_BACKOFF_RETRIES));
                userConfig.remove(CONFIG_KEY_BULK_FLUSH_BACKOFF_RETRIES);
            }

            if (params.has(CONFIG_KEY_BULK_FLUSH_BACKOFF_DELAY)) {
                bulkProcessorFlushBackoffPolicy.setDelayMillis(params.getLong(CONFIG_KEY_BULK_FLUSH_BACKOFF_DELAY));
                userConfig.remove(CONFIG_KEY_BULK_FLUSH_BACKOFF_DELAY);
            }

        } else {
            bulkProcessorFlushBackoffPolicy = null;
        }

        this.userConfig = userConfig;
    }

    public Client createClient(Map<String, String> clientConfig) {
        Settings settings = Settings.builder().put(clientConfig)
                .put(NetworkModule.HTTP_TYPE_KEY, Netty3Plugin.NETTY_HTTP_TRANSPORT_NAME)
                .put(NetworkModule.TRANSPORT_TYPE_KEY, Netty3Plugin.NETTY_TRANSPORT_NAME)
                .build();

        TransportClient transportClient = new PreBuiltTransportClient(settings);
        for (TransportAddress transport : ElasticsearchUtils.convertInetSocketAddresses(transportAddresses)) {
            transportClient.addTransportAddress(transport);
        }

        // verify that we actually are connected to a cluster
        if (transportClient.connectedNodes().isEmpty()) {
            throw new RuntimeException("Elasticsearch client is not connected to any Elasticsearch nodes!");
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Created Elasticsearch TransportClient with connected nodes {}", transportClient.connectedNodes());
        }

        return transportClient;
    }

    @Override
    public void configure(Configuration parameters) {
        client = createClient(userConfig);
        bulkProcessor = buildBulkProcessor(new BulkProcessorListener());
        requestIndexer = new BulkProcessorIndexer(bulkProcessor);
    }

    @Override
    public void open(int taskNumber, int numTasks) throws IOException {

    }

    @Override
    public void writeRecord(Row record) throws IOException {
        checkErrorAndRethrow();

        elasticsearchOutputFunction.process(record, getRuntimeContext(), requestIndexer);
    }


    @Override
    public void close() throws IOException {
        if (bulkProcessor != null) {
            bulkProcessor.close();
            bulkProcessor = null;
        }

        if (client != null) {
            client.close();
            client = null;
        }

        // make sure any errors from callbacks are rethrown
        checkErrorAndRethrow();
    }



    /**
     * Build the {@link BulkProcessor}.
     *
     * Note: this is exposed for testing purposes.
     */
    protected BulkProcessor buildBulkProcessor(BulkProcessor.Listener listener) {
        checkNotNull(listener);

        BulkProcessor.Builder bulkProcessorBuilder = BulkProcessor.builder(client, listener);

        // This makes flush() blocking
        bulkProcessorBuilder.setConcurrentRequests(0);

        if (bulkProcessorFlushMaxActions != null) {
            bulkProcessorBuilder.setBulkActions(bulkProcessorFlushMaxActions);
        }

        if (bulkProcessorFlushMaxSizeMb != null) {
            bulkProcessorBuilder.setBulkSize(new ByteSizeValue(bulkProcessorFlushMaxSizeMb, ByteSizeUnit.MB));
        }

        if (bulkProcessorFlushIntervalMillis != null) {
            bulkProcessorBuilder.setFlushInterval(TimeValue.timeValueMillis(bulkProcessorFlushIntervalMillis));
        }

        // if backoff retrying is disabled, bulkProcessorFlushBackoffPolicy will be null
        configureBulkProcessorBackoff(bulkProcessorBuilder, bulkProcessorFlushBackoffPolicy);

        return bulkProcessorBuilder.build();
    }

    public void configureBulkProcessorBackoff(
            BulkProcessor.Builder builder,
            @Nullable BulkFlushBackoffPolicy flushBackoffPolicy) {

        BackoffPolicy backoffPolicy;
        if (flushBackoffPolicy != null) {
            switch (flushBackoffPolicy.getBackoffType()) {
                case CONSTANT:
                    backoffPolicy = BackoffPolicy.constantBackoff(
                            new TimeValue(flushBackoffPolicy.getDelayMillis()),
                            flushBackoffPolicy.getMaxRetryCount());
                    break;
                case EXPONENTIAL:
                default:
                    backoffPolicy = BackoffPolicy.exponentialBackoff(
                            new TimeValue(flushBackoffPolicy.getDelayMillis()),
                            flushBackoffPolicy.getMaxRetryCount());
            }
        } else {
            backoffPolicy = BackoffPolicy.noBackoff();
        }

        builder.setBackoffPolicy(backoffPolicy);
    }

    private void checkErrorAndRethrow() {
        Throwable cause = failureThrowable.get();
        if (cause != null) {
            throw new RuntimeException("An error occurred in RdosElasticsearchSink.", cause);
        }
    }

    public Throwable extractFailureCauseFromBulkItemResponse(BulkItemResponse bulkItemResponse) {
        if (!bulkItemResponse.isFailed()) {
            return null;
        } else {
            return bulkItemResponse.getFailure().getCause();
        }
    }

    private class BulkProcessorListener implements BulkProcessor.Listener {
        @Override
        public void beforeBulk(long executionId, BulkRequest request) { }

        @Override
        public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            if (response.hasFailures()) {
                BulkItemResponse itemResponse;
                Throwable failure;
                RestStatus restStatus;

                try {
                    for (int i = 0; i < response.getItems().length; i++) {
                        itemResponse = response.getItems()[i];
                        failure = extractFailureCauseFromBulkItemResponse(itemResponse);
                        if (failure != null) {
                            LOG.error("Failed Elasticsearch item request: {}", itemResponse.getFailureMessage(), failure);

                            restStatus = itemResponse.getFailure().getStatus();
                            if (restStatus == null) {
                                failureHandler.onFailure(request.requests().get(i), failure, -1, requestIndexer);
                            } else {
                                failureHandler.onFailure(request.requests().get(i), failure, restStatus.getStatus(), requestIndexer);
                            }
                        }
                    }
                } catch (Throwable t) {
                    // fail the sink and skip the rest of the items
                    // if the failure handler decides to throw an exception
                    failureThrowable.compareAndSet(null, t);
                }
            }

        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            LOG.error("Failed Elasticsearch bulk request: {}", failure.getMessage(), failure.getCause());

            try {
                for (ActionRequest action : request.requests()) {
                    failureHandler.onFailure(action, failure, -1, requestIndexer);
                }
            } catch (Throwable t) {
                // fail the sink and skip the rest of the items
                // if the failure handler decides to throw an exception
                failureThrowable.compareAndSet(null, t);
            }

        }
    }

}
