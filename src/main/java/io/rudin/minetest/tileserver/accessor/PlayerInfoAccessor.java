package io.rudin.minetest.tileserver.accessor;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Player;
import io.rudin.minetest.tileserver.blockdb.tables.pojos.PlayerMetadata;
import io.rudin.minetest.tileserver.entity.PlayerInfo;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static io.rudin.minetest.tileserver.blockdb.tables.Player.PLAYER;
import static io.rudin.minetest.tileserver.blockdb.tables.PlayerMetadata.PLAYER_METADATA;

@Singleton
public class PlayerInfoAccessor {

    @Inject
    public PlayerInfoAccessor(DSLContext ctx){
        this.ctx = ctx;
    }

    private final DSLContext ctx;


    public List<PlayerInfo> getPlayersSince(Timestamp timestamp){
        List<PlayerInfo> list = new ArrayList<>();

        List<Player> players = ctx
                .selectFrom(PLAYER)
                .where(PLAYER.MODIFICATION_DATE.gt(timestamp))
                .fetch()
                .into(Player.class);


        for (Player player : players) {

            List<PlayerMetadata> metadata = ctx
                    .selectFrom(PLAYER_METADATA)
                    .where(PLAYER_METADATA.PLAYER.eq(player.getName()))
                    .and(PLAYER_METADATA.ATTR.in("xp", "died", "played_time", "digged_nodes", "crafted", "placed_nodes", "homedecor:player_skin"))
                    .fetchInto(PlayerMetadata.class);

            PlayerInfo info = new PlayerInfo(player, metadata);

            list.add(info);
        }

        return list;
    }

}
