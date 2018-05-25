package io.rudin.minetest.tileserver.job;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Player;
import io.rudin.minetest.tileserver.blockdb.tables.pojos.PlayerMetadata;
import io.rudin.minetest.tileserver.entity.PlayerInfo;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.sql.Timestamp;
import java.util.List;

import static io.rudin.minetest.tileserver.blockdb.tables.Player.PLAYER;
import static io.rudin.minetest.tileserver.blockdb.tables.PlayerMetadata.PLAYER_METADATA;

@Singleton
public class UpdatePlayerJob implements Runnable {

    @Inject
    public UpdatePlayerJob(DSLContext ctx, EventBus eventBus){
        this.ctx = ctx;
        this.eventBus = eventBus;
    }

    private final DSLContext ctx;

    private final EventBus eventBus;

    private Timestamp timestamp = new Timestamp(0L);

    private boolean running = false;

    @Override
    public void run() {
        if (running)
            return;

        try {
            running = true;

            List<Player> players = ctx
                    .selectFrom(PLAYER)
                    .where(PLAYER.MODIFICATION_DATE.gt(timestamp))
                    .fetch()
                    .into(Player.class);

            for (Player player : players) {

                Timestamp modificationDate = player.getModificationDate();

                if (modificationDate.after(timestamp)) {
                    //Rember newest modification date
                    this.timestamp = modificationDate;
                }

                List<PlayerMetadata> metadata = ctx
                        .selectFrom(PLAYER_METADATA)
                        .where(PLAYER_METADATA.PLAYER.eq(player.getName()))
                        .fetchInto(PlayerMetadata.class);

                PlayerInfo info = new PlayerInfo(player, metadata);

                EventBus.PlayerMovedEvent event = new EventBus.PlayerMovedEvent();
                event.info = info;
                eventBus.post(event);
            }
        } finally {
            running = false;

        }

    }
}
