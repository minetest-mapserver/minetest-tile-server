package io.rudin.minetest.tileserver.entity;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Player;
import io.rudin.minetest.tileserver.blockdb.tables.pojos.PlayerMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerInfo {

    public PlayerInfo(Player player, List<PlayerMetadata> metadata){
        this.player = player;

        for (PlayerMetadata md: metadata){
            this.metadata.put(md.getAttr(), md.getValue());
        }
    }

    private final Player player;

    private final Map<String, String> metadata = new HashMap<>();

    public Player getPlayer() {
        return player;
    }

    public Map<String, String> getMetadata(){
        return metadata;
    }
}
