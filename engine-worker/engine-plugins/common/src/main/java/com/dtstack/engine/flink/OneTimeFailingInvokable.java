package flink;

import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.util.FlinkException;

import java.util.concurrent.atomic.AtomicBoolean;

public class OneTimeFailingInvokable extends AbstractInvokable {

	private static final AtomicBoolean hasFailed = new AtomicBoolean(false);

	/**
	 * Create an Invokable task and set its environment.
	 *
	 * @param environment The environment assigned to this invokable.
	 */
	public OneTimeFailingInvokable(Environment environment) {
		super(environment);
	}

	@Override
	public void invoke() throws Exception {
		if (hasFailed.compareAndSet(false, true)) {
			throw new FlinkException("One time failure.");
		}
	}
}