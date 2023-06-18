package es.bukkitclassmapper._shared.utils;

import java.util.concurrent.Executor;

public final class FakeExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
