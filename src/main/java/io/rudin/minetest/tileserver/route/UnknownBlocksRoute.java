package io.rudin.minetest.tileserver.route;

import io.rudin.minetest.tileserver.util.UnknownBlockCollector;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UnknownBlocksRoute implements Route {

    @Inject
    public UnknownBlocksRoute(UnknownBlockCollector unknownBlockCollector){
        this.unknownBlockCollector = unknownBlockCollector;
    }

    private final UnknownBlockCollector unknownBlockCollector;

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.header("Content-Type", "application/json");

        return unknownBlockCollector.getUnknownBlockCount();
    }
}
