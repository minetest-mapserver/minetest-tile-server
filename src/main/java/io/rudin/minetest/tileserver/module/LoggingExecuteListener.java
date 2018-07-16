package io.rudin.minetest.tileserver.module;

import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.tools.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExecuteListener extends DefaultExecuteListener {

    private static final Logger logger = LoggerFactory.getLogger(LoggingExecuteListener.class);

    StopWatch watch;

    class SQLPerformanceWarning extends Exception {}

    @Override
    public void executeStart(ExecuteContext ctx) {
        super.executeStart(ctx);
        watch = new StopWatch();
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
        super.executeEnd(ctx);
        if (watch.split() > 5_000_000_000L)
            logger.warn(
                    "Slow SQL",
                    new SQLPerformanceWarning());
    }
}
