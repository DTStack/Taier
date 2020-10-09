package com.dtstack.engine.flink.check;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.io.Serializable;
import java.util.*;

/**
 * @author mowen
 * @ProjectName engine-all
 * @ClassName FlinkSessionCheck.java
 * @Description TODO
 * @createTime 2020年09月29日 11:30:00
 */
public class FlinkSessionCheck {

    public static void main(String[] args) throws Exception {
        String checkpoint = args[0];

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(1000);
        env.getConfig().disableSysoutLogging();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, 0L));
        env.setStateBackend(new RocksDBStateBackend(checkpoint, true));

        DataStream<String> stream = env.addSource(new FlinkSessionCheck.StringGeneratingSourceFunction(10_000_000L));
        stream.map(new FlinkSessionCheck.StringPrefixCountRichMapFunction())
                .addSink(new FlinkSessionCheck.ValidatingSink());
        env.execute("session check");
    }


    public static class StringGeneratingSourceFunction extends RichParallelSourceFunction<String>
            implements ListCheckpointed<Integer> {
        private final long numElements;

        private int index;

        private volatile boolean isRunning = true;

        StringGeneratingSourceFunction(long numElements) {
            this.numElements = numElements;
        }

        @Override
        public void run(SourceContext<String> ctx) throws Exception {
            final Object lockingObject = ctx.getCheckpointLock();

            final Random rnd = new Random();
            final StringBuilder stringBuilder = new StringBuilder();

            final int step = getRuntimeContext().getNumberOfParallelSubtasks();

            if (index == 0) {
                index = getRuntimeContext().getIndexOfThisSubtask();
            }

            while (isRunning && index < numElements) {
                char first = (char) ((index % 40) + 40);

                stringBuilder.setLength(0);
                stringBuilder.append(first);

                String result = randomString(stringBuilder, rnd);

                synchronized (lockingObject) {
                    index += step;
                    ctx.collect(result);
                }
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }

        private static String randomString(StringBuilder bld, Random rnd) {
            final int len = rnd.nextInt(10) + 5;

            for (int i = 0; i < len; i++) {
                char next = (char) (rnd.nextInt(20000) + 33);
                bld.append(next);
            }

            return bld.toString();
        }

        @Override
        public List<Integer> snapshotState(long checkpointId, long timestamp) throws Exception {
            return Collections.singletonList(this.index);
        }

        @Override
        public void restoreState(List<Integer> state) throws Exception {
            if (state.isEmpty() || state.size() > 1) {
                throw new RuntimeException("Test failed due to unexpected recovered state size " + state.size());
            }
            this.index = state.get(0);
        }
    }

    public static class ValidatingSink extends RichSinkFunction<PrefixCount>
            implements ListCheckpointed<HashMap<Character, Long>> {

        @SuppressWarnings("unchecked")
        private static Map<Character, Long>[] maps = (Map<Character, Long>[]) new Map<?, ?>[1];

        private HashMap<Character, Long> counts = new HashMap<Character, Long>();

        @Override
        public void invoke(PrefixCount value) {
            Character first = value.prefix.charAt(0);
            Long previous = counts.get(first);
            if (previous == null) {
                counts.put(first, value.count);
            } else {
                counts.put(first, Math.max(previous, value.count));
            }
        }

        @Override
        public void close() throws Exception {
            maps[getRuntimeContext().getIndexOfThisSubtask()] = counts;
        }

        @Override
        public List<HashMap<Character, Long>> snapshotState(long checkpointId, long timestamp) throws Exception {
            return Collections.singletonList(this.counts);
        }

        @Override
        public void restoreState(List<HashMap<Character, Long>> state) throws Exception {
            if (state.isEmpty() || state.size() > 1) {
                throw new RuntimeException("Test failed due to unexpected recovered state size " + state.size());
            }
            this.counts.putAll(state.get(0));
        }
    }

    public static class PrefixCount implements Serializable {

        public String prefix;
        public String value;
        public long count;

        public PrefixCount() {}

        public PrefixCount(String prefix, String value, long count) {
            this.prefix = prefix;
            this.value = value;
            this.count = count;
        }

        @Override
        public String toString() {
            return prefix + " / " + value;
        }
    }

    public static class StringPrefixCountRichMapFunction extends RichMapFunction<String, PrefixCount>
            implements ListCheckpointed<Long> {

        static long[] counts = new long[1];

        private long count;

        @Override
        public PrefixCount map(String value) {
            count++;
            return new PrefixCount(value.substring(0, 1), value, 1L);
        }

        @Override
        public void close() {
            counts[getRuntimeContext().getIndexOfThisSubtask()] = count;
        }

        @Override
        public List<Long> snapshotState(long checkpointId, long timestamp) throws Exception {
            return Collections.singletonList(this.count);
        }

        @Override
        public void restoreState(List<Long> state) throws Exception {
            if (state.isEmpty() || state.size() > 1) {
                throw new RuntimeException("Test failed due to unexpected recovered state size " + state.size());
            }
            this.count = state.get(0);
        }
    }


}
