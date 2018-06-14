package io.rudin.minetest.tileserver.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import io.rudin.minetest.tileserver.service.EventBus;
import org.eclipse.jetty.websocket.api.Session;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WebSocketUpdater {

    @Inject
    public WebSocketUpdater(EventBus eventBus){
        this.eventBus = eventBus;
        this.mapper = new ObjectMapper();
    }

    private final EventBus eventBus;

    private final ObjectMapper mapper;

    public void init(){
        eventBus.register(this);
    }

    public static class EventContainer {
        public String type;
        public Object data;
    }


    @Subscribe void onPlayerMove(EventBus.PlayerMovedEvent e){
        try {
            EventContainer container = new EventContainer();
            container.data = e;
            container.type = "player-move";
            String json = mapper.writeValueAsString(container);

            for (Session session : WebSocketHandler.sessions) {
                try {
                    session.getRemote().sendString(json);

                } catch (Exception e3) {
                    //TODO
                }
            }
        } catch (Exception e2){
            e2.printStackTrace();
        }

    }

    @Subscribe void onTileUpdate(EventBus.TileChangedEvent e){
        try {
            EventContainer container = new EventContainer();
            container.data = e;
            container.type = "tile-update";
            String json = mapper.writeValueAsString(container);

            for (Session session : WebSocketHandler.sessions) {
                try {
                    session.getRemote().sendString(json);

                } catch (Exception e3) {
                    //TODO
                }
            }
        } catch (Exception e2){
            e2.printStackTrace();
        }

    }

}
