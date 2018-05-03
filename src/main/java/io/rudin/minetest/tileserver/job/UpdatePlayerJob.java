package io.rudin.minetest.tileserver.job;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Player;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.sql.Timestamp;
import java.util.List;

import static io.rudin.minetest.tileserver.blockdb.tables.Player.PLAYER;

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

                EventBus.PlayerMovedEvent event = new EventBus.PlayerMovedEvent();
                event.player = player;
                eventBus.post(event);
            }
        } finally {
            running = false;

        }

    }
}
