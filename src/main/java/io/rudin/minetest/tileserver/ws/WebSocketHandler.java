package io.rudin.minetest.tileserver.ws;

import io.prometheus.client.Gauge;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class WebSocketHandler {

    static final Gauge clientCountGauge = Gauge.build()
            .name("tileserver_websocket_clients")
            .help("Number of websocket clients")
            .register();;


    public static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.add(session);
        clientCountGauge.set(sessions.size());
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
        clientCountGauge.set(sessions.size());
    }

}
