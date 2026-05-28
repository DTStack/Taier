import com.dtstack.taier.metrics.builder.ThreadPoolBuilder;
import com.dtstack.taier.metrics.executor.EngineExecutor;
import com.dtstack.taier.metrics.monitor.TpMonitor;

import java.util.Collections;

/**
 * @author xingyi
 * @date 2025/9/17
 */
public class TpMetricsTests {

    public static void main(String[] args) throws InterruptedException {
        // 1. build a thread pool
        EngineExecutor eager = ThreadPoolBuilder.newBuilder()
                .corePoolSize(1)
                .maximumPoolSize(10)
                .eager()
                .queueTimeout(400)
                .runTimeout(400)
                .buildDynamic()
                .registry(); // register by build

        // eager.execute(() -> System.out.println("<UNK>"));

        // 执行任务
        for (int i = 0; i < 100; i++) {
            eager.execute(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " 执行任务");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        TpMonitor sm = new TpMonitor();
        sm.interval(2, 0, Collections.singletonList("output"));

        Thread.sleep(2000l);

    }

}
