package io.rudin.minetest.tileserver.query;

import io.rudin.minetest.tileserver.config.Layer;
import io.rudin.minetest.tileserver.config.LayerConfig;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

@Singleton
public class YQueryBuilder {

    @Inject
    public YQueryBuilder(DSLContext ctx, LayerConfig cfg){
        this.cfg = cfg;
        this.ctx = ctx;

        Condition condition = DSL.condition(true);

        for (Layer layer: cfg.layers){
            condition.and(getCondition(layer));
        }

        this.allLayers = condition;
    }

    private final DSLContext ctx;

    private final LayerConfig cfg;

    private final Condition allLayers;

    public Condition getAllLayersCondition(){
        return allLayers;
    }

    public static int coordinateToMapBlock(int coordinate){
        return (int)Math.round(coordinate / 16.0);
    }

    public Condition getCondition(Layer layer){
        int from = coordinateToMapBlock(layer.from);
        int to = coordinateToMapBlock(layer.to);

        return BLOCKS.POSY.between(from, to);
    }

}
