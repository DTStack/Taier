package flink;

import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;

public class NoOpInvokable extends AbstractInvokable {

	public NoOpInvokable(Environment environment) {
        super(environment);
	}

	@Override
	public void invoke() {}
}