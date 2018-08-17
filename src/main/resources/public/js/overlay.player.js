
(function(tileserver){

    var PlayerIcon = L.icon({
        iconUrl: 'pics/sam.png',

        iconSize:     [16, 32],
        iconAnchor:   [8, 16],
        popupAnchor:  [0, -16]
    });

    var playerLayer = L.layerGroup();

    var playerMarkers = {}; // name -> L.Marker

    function updatePlayer(info) {
        var player = info.player;
        var marker = playerMarkers[player.name];
        var zOffset = 15;
        var latLng = [(player.posz/10)-zOffset, player.posx/10];

        var age = Date.now() - player.modificationDate;

        var popup = "<h4>" + player.name + "</h4><hr>" +
          "<b>X: </b> " + player.posx/10 + "<br>" +
          "<b>Y: </b> " + player.posy/10 + "<br>" +
          "<b>Z: </b> " + player.posz/10 + "<br>" +
          "<b>XP: </b> " + info.metadata.xp + "<br>" +
          "<b>Deathcount: </b> " + info.metadata.died + "<br>" +
          "<b>Playtime: </b> " + moment.duration((info.metadata.played_time || 1) * 1000).humanize() + "<br>" +
          "<b>Digcount: </b> " + info.metadata.digged_nodes + "<br>" +
          "<b>Craftcount: </b> " + info.metadata.crafted + "<br>" +
          "<b>Placecount: </b> " + info.metadata.placed_nodes + "<br>" +
          "<b>Health: </b> " + player.hp + "<br>" +
          "<b>Breath: </b> " + player.breath + "<br>" +
          "<b>Last login: </b> " + moment.duration(age).humanize();



        if (!marker){
          //Create marker
          marker = L.marker(latLng, {icon: PlayerIcon});
          marker.bindPopup(popup).addTo(playerLayer);
          playerMarkers[player.name] = marker;

        } else {
          //Update marker
          marker.setLatLng(latLng);
          marker.bindPopup(popup);

        }

    }

    function updatePlayers(){
      m.request({ url: "player" })
      .then(function(players){
        players.forEach(updatePlayer);
      });
    }

    //initial player update
    updatePlayers();

    tileserver.overlays["Player"] = playerLayer;
    tileserver.defaultOverlays.push(playerLayer);

    tileserver.websocketCallbacks.push(function(event){
        if (event.type === "player-move"){
            updatePlayer(event.data.info);
        }
    });

})(window.tileserver);